package com.axonactive.digidocs.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.axonactive.digidocs.common.Branch;

/**
 * Unit test for FileUtils.
 */
@RunWith(MockitoJUnitRunner.class)
public class FileUtilsTest {

	@Mock
	private File file = Mockito.mock(File.class);

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();


	@Test
	public void testGetHCMBranch() {
		File fileFromHCM = new File("HCM_CEO_001.pdf");
		Branch branch = FileUtils.getBranch(fileFromHCM);
		assertEquals(Branch.HCM, branch);
	}

	@Test
	public void testGetCTBranch() {
		File fileFromCT = new File("CT_CEO_001.pdf");
		Branch branch = FileUtils.getBranch(fileFromCT);
		assertEquals(Branch.CT, branch);
	}

	@Test
	public void testGetDNBranch() {
		File fileFromDN = new File("DN_CEO_001.pdf");
		Branch branch = FileUtils.getBranch(fileFromDN);
		assertEquals(Branch.DN, branch);
	}

	@Test
	public void testGetNoneBranch() {
		File fileFromQN = new File("QN_CEO_001.pdf");
		Branch branch = FileUtils.getBranch(fileFromQN);
		assertEquals(Branch.NONE, branch);
	}

	@Test
	public void testGetCorrectRecipient() {
		File fileToSend = new File("HCM_CEO_002.pdf");
		String recipientRole = FileUtils.getRecipientRole(fileToSend).orElse("");
		assertEquals(recipientRole, "CEO");
	}

	@Test
	public void testGetEmptyRecipient() {
		File fileToSend = new File("HCM001.pdf");
		String recipientRole = FileUtils.getRecipientRole(fileToSend).orElse("");
		assertEquals(recipientRole, "");
	}
	
	@Test
	public void testConverFileSize() {
		long fileSize = 1024;
		assertEquals("1 KB", FileUtils.convertFileSize(fileSize));
		fileSize = 1024*1024*2; 
		assertEquals("2 MB", FileUtils.convertFileSize(fileSize));
	}
}