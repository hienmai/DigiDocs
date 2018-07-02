package com.axonactive.digidocs.smb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@RunWith(PowerMockRunner.class)
public class SmbClientTest {
	
	private static final Logger LOGGER = Logger.getLogger(SmbClient.class);
	
	
	@Mock
	static SmbAuthentication smbAuthentication = Mockito.mock(SmbAuthentication.class);

	
	@Mock
	static NtlmPasswordAuthentication ntlmPasswordAuthentication;
	
	@InjectMocks
	SmbClient smbClient;
	
	@BeforeClass
	public static void createTestFile() {
//		String path = "smb://hcmc-fsr/Teams/DigiDocs_Storage/HCMCBD/2018/ReadFileTest.pdf";
//		byte[] data = "Hello UnitTest".getBytes();
//		ntlmPasswordAuthentication = new NtlmPasswordAuthentication("AAVN","dmstool","admin123!@#");
		Mockito.when(smbAuthentication.getAuth()).thenReturn(new NtlmPasswordAuthentication("AAVN","dmstool","admin123!@#"));
	}
	
	@Test
	public void writeFileSuccessfulTest(){
		String path = "smb://hcmc-fsr/Teams/DigiDocs_Storage/HCMCBD/2018/WriteFileTest.pdf";
		byte[] data = "Hello UnitTest".getBytes();
		smbClient.writeFile(path, data);
		assertNotEquals(0, smbClient.readFile(path));
	}
	
	@Test
	public void writeFileFailTest() {
		String path = "smb://hcmc-fsr\\Teams/DigiDocs_Storage/HCMCBD/2018/WriteFileTest.pdf";
		byte[] data = "Hello UnitTest".getBytes();
		assertFalse(smbClient.writeFile(path, data));
	}
	
	
	@Test
	public void readFileSuccessfulTest() {
		String path = "smb://hcmc-fsr/Teams/DigiDocs_Storage/HCMCBD/2018/ReadFileTest.pdf";
		byte[] data = "Hello UnitTest".getBytes();
		smbClient.writeFile(path, data);
		assertNotEquals(0, smbClient.readFile(path).length);
	}
	
	@Test
	public void readFileFailTest() {
		String path = "smb://hcmc-fsr/Teams/DigiDocs_Storage/HCMCBD/2018/ReadAnotherFile.pdf";
		assertEquals(0, smbClient.readFile(path).length);
	}
	
	@Test
	public void createNewFolderSuccessfulTest() {
		String path = "smb://hcmc-fsr/Teams/DigiDocs_Storage/HCMCBD/2020";
		assertTrue(smbClient.createNewFolder(path));
	}
	
	@AfterClass
	public static void deleteTestFile() {
		deleteFile("smb://hcmc-fsr/Teams/DigiDocs_Storage/HCMCBD/2018/WriteFileTest.pdf");
		deleteFile("smb://hcmc-fsr/Teams/DigiDocs_Storage/HCMCBD/2018/ReadFileTest.pdf");
		deleteFolder("smb://hcmc-fsr/Teams/DigiDocs_Storage/HCMCBD/2020/");
	}
	
	
	
	public static void deleteFile(String path) {
		SmbFile sFile;
		try {
			sFile = new SmbFile(path, smbAuthentication.getAuth());
			sFile.delete();
		} catch (MalformedURLException | SmbException e) {
			LOGGER.error("Can't deletefile",e );
		}
	}
	
	public static void deleteFolder(String path) {
		SmbFile sFolder;
		try {
			sFolder = new SmbFile(path, smbAuthentication.getAuth());
			sFolder.delete();
		} catch (MalformedURLException | SmbException e) {
			LOGGER.error("Can't delete folder",e );
		}
  
	}
}
