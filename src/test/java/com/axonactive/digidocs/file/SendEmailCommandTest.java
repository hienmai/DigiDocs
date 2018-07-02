package com.axonactive.digidocs.file;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.axonactive.digidocs.email.EmailHandler;
import com.axonactive.digidocs.email.EmailInformation;
import com.axonactive.digidocs.email.EmailInformationFactory;
import com.axonactive.digidocs.email.ServerConfiguration;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.net.ssl.*" })
public class SendEmailCommandTest {

	private Command previousCommand = Mockito.mock(Command.class); 
	private Command commandResult = Mockito.mock(Command.class);
	private ServerConfiguration serverConfiguration = Mockito.mock(ServerConfiguration.class);
	
	@Test
	public void testSendEmailWithFailedPreviousCommand() {
		Mockito.when(previousCommand.execute()).thenReturn(commandResult);
		CommandResult commandResultResult = new CommandResult();
		commandResultResult.setResult(Result.FAIL_DATABASE_SAVING);
		Mockito.when(commandResult.getCommandResult()).thenReturn(commandResultResult);
		
		Command command = new SendEmailCommand(previousCommand, serverConfiguration);
		assertEquals(Result.FAIL_DATABASE_SAVING, command.execute().getCommandResult().getResult());
	}
	
	@Test
	@PrepareForTest({EmailInformationFactory.class, EmailHandler.class, ServerConfiguration.class})
	public void testSendEmailSuccess() throws Exception {
		Mockito.when(previousCommand.execute()).thenReturn(commandResult);
		CommandResult commandResultResult = new CommandResult();
		commandResultResult.setResult(Result.STEP_SUCESS);
		Mockito.when(commandResult.getCommandResult()).thenReturn(commandResultResult);
		EmailInformation emailInformation = Mockito.mock(EmailInformation.class);
		PowerMockito.mockStatic(EmailInformationFactory.class);
		PowerMockito.when(EmailInformationFactory.getEmailInformation(Mockito.any()))
					.thenReturn(Optional.of(emailInformation));
		PowerMockito.mockStatic(EmailHandler.class);
		PowerMockito.when(EmailHandler.sendMail(Mockito.any(), Mockito.any())).thenReturn(true);
		Command command = new SendEmailCommand(previousCommand, serverConfiguration);
		assertEquals(Result.SUCCESS, command.execute().getCommandResult().getResult());
	}
	
	@Test
	@PrepareForTest(EmailInformationFactory.class)
	public void testSendEmailFail() {
		Mockito.when(previousCommand.execute()).thenReturn(commandResult);
		CommandResult commandResultResult = new CommandResult();
		commandResultResult.setResult(Result.STEP_SUCESS);
		Mockito.when(commandResult.getCommandResult()).thenReturn(commandResultResult);
		PowerMockito.mockStatic(EmailInformationFactory.class);
		PowerMockito.when(EmailInformationFactory.getEmailInformation(Mockito.any()))
					.thenReturn(Optional.empty());
		
		Command command = new SendEmailCommand(previousCommand, serverConfiguration);
		assertEquals(Result.SUCCESS, command.execute().getCommandResult().getResult());
	}
}
