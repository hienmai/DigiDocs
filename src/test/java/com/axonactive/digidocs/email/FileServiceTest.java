package com.axonactive.digidocs.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.axonactive.digidocs.configuration.ConfigurationBOM;
import com.axonactive.digidocs.configuration.ConfigurationEntity;
import com.axonactive.digidocs.configuration.ConfigurationService;
import com.axonactive.digidocs.encryption.AES256Encryption;
import com.axonactive.digidocs.encryption.EncryptionException;
import com.axonactive.digidocs.encryption.SymmetricEncryption;
import com.axonactive.digidocs.encryption.SymmetricEncryptionFactory;
import com.axonactive.digidocs.persistence.PersistenceService;
import com.axonactive.digidocs.utils.FormatUtils;

@RunWith(PowerMockRunner.class)
public class FileServiceTest {

	@Mock
	PersistenceService<FileEntity> persistenceService;

	@Mock
	ConfigurationService configurationService;

	@InjectMocks
	FileService fileService;

	// Test function "init"
	@Test
	public void testInit() throws Exception {
		Optional<ConfigurationEntity> saltConfigurationEntityOptional = Optional
				.of(PowerMockito.mock(ConfigurationEntity.class));
		PowerMockito.when(configurationService.findByKey("database.salt")).thenReturn(saltConfigurationEntityOptional);
		Optional<ConfigurationBOM> configurationBOM = Optional.of(new ConfigurationBOM());
		configurationBOM.get().setValue("AAA");
		PowerMockito.when(configurationService.toBom(Mockito.any())).thenReturn(configurationBOM);
		Whitebox.invokeMethod(fileService, "init");
		String salt = Whitebox.getInternalState(fileService, "salt");
		assertEquals("AAA", salt);
	}

	@Test
	public void testInitNullSalt() throws Exception {
		Optional<ConfigurationEntity> saltConfigurationEntityOptional = Optional.empty();
		PowerMockito.when(configurationService.findByKey("database.salt")).thenReturn(saltConfigurationEntityOptional);
		Optional<ConfigurationBOM> configurationBOM = Optional.of(new ConfigurationBOM());
		configurationBOM.get().setValue("AAA");
		PowerMockito.when(configurationService.toBom(Mockito.any())).thenReturn(configurationBOM);
		Whitebox.invokeMethod(fileService, "init");
		String salt = Whitebox.getInternalState(fileService, "salt");
		assertNull(salt);
	}

	// Test function "toEntity" with null input
	@Test
	public void testToEntityWithNullCase() {
		Optional<FileEntity> fileEntity = fileService.toEntity(null);
		assertEquals(Optional.empty(), fileEntity);
	}

	// Test function "toEntity" with null id
	@PrepareForTest({ SymmetricEncryptionFactory.class, FormatUtils.class })
	@Test
	public void testToEntityWithNullId() {
		FileBOM fileBom = FileBOM.builder()
		.role("CEO")
		.path("/#2018-05-15-120000.pdf")
		.name("#2018-05-15-120000.pdf")
		.year(2018)
		.keyBase64("key")
		.ivBase64("iv").build();
		Optional<SymmetricEncryption> aes256Cryption = Optional.of(PowerMockito.mock(AES256Encryption.class));
		PowerMockito.mockStatic(SymmetricEncryptionFactory.class);
		PowerMockito.when(SymmetricEncryptionFactory.getAES256Encryption(Mockito.any(), Mockito.any()))
				.thenReturn(aes256Cryption);
		PowerMockito.mockStatic(FormatUtils.class);
		Optional<FileEntity> fileEntity = fileService.toEntity(fileBom);
		assertNull(fileEntity.get().getId());
	}

	// Test function "toEntity" with invalid IV
	@PrepareForTest({ SymmetricEncryptionFactory.class, FormatUtils.class })
	@Test
	public void testToEntityWithInvalidIV() throws Exception {
		FileBOM fileBom = FileBOM.builder().build();
		Optional<SymmetricEncryption> aes256Cryption = Optional.of(PowerMockito.mock(AES256Encryption.class));
		PowerMockito.mockStatic(SymmetricEncryptionFactory.class);
		PowerMockito.when(SymmetricEncryptionFactory.getAES256Encryption(Mockito.any(), Mockito.any()))
				.thenReturn(aes256Cryption);
		PowerMockito.when(aes256Cryption.get().encrypt(Mockito.any())).thenThrow(new EncryptionException(new InvalidAlgorithmParameterException()));
		PowerMockito.mockStatic(FormatUtils.class);
		Optional<FileEntity> fileEntity = fileService.toEntity(fileBom);
		assertFalse(fileEntity.isPresent());
	}

	// Test function "toEntity" with successful case
	@PrepareForTest({ SymmetricEncryptionFactory.class, FormatUtils.class })
	@Test
	public void testToEntity() {
		FileBOM fileBom = FileBOM.builder()
				.id(0)
				.role("CEO")
				.branchSender("HCM")
				.path("/#2018-05-15-120000.pdf")
				.name("#2018-05-15-120000.pdf")
				.fileSize(100)
				.date(new Date())
				.year(2018)
				.read(false)
				.keyBase64("Key")
				.ivBase64("IV")
				.build();
		Optional<SymmetricEncryption> aes256Cryption = Optional.of(PowerMockito.mock(AES256Encryption.class));
		PowerMockito.mockStatic(SymmetricEncryptionFactory.class);
		PowerMockito.when(SymmetricEncryptionFactory.getAES256Encryption(Mockito.any(), Mockito.any()))
				.thenReturn(aes256Cryption);
		PowerMockito.mockStatic(FormatUtils.class);
		Optional<FileEntity> fileEntity = fileService.toEntity(fileBom);
		Object[] expectedResults = { fileBom.getId(), fileBom.getRole(), fileBom.getPath(), fileBom.getName(),
				fileBom.getYear(), fileBom.isRead() };
		Object[] actualResults = { fileEntity.get().getId(), fileEntity.get().getRole(), fileEntity.get().getPath(), fileEntity.get().getName(),
				fileEntity.get().getYear(), fileEntity.get().isRead() };
		Assert.assertArrayEquals(expectedResults, actualResults);
	}

	// Test function "toBOM" with null input
	@Test
	public void testToBomWithNullCase() {
		Optional<FileBOM> fileBom = fileService.toBom(null);
		assertEquals(Optional.empty(), fileBom);
	}

	// Test function "toBOM" with null IV (or null Key)
	@PrepareForTest({ SymmetricEncryptionFactory.class, FormatUtils.class })
	@Test
	public void testToBomWithNullIV() {
		FileEntity fileEntity = FileEntity
				.builder()
				.id(0)
				.role("HCMCBD")
				.branchSender("HCMC")
				.path("/#2018-05-10-120000.pdf")
				.name("#2018-05-10-120000.pdf")
				.fileSize(100)
				.date(new Date())
				.year(2018)
				.read(false)
				.key(null)
				.iv(null)
				.filesTagsEntities(new ArrayList<>())
				.build();
				
		Optional<SymmetricEncryption> aes256Cryption = Optional.of(PowerMockito.mock(AES256Encryption.class));
		PowerMockito.mockStatic(SymmetricEncryptionFactory.class);
		PowerMockito.when(SymmetricEncryptionFactory.getAES256Encryption(Mockito.any(), Mockito.any()))
				.thenReturn(aes256Cryption);
		PowerMockito.mockStatic(FormatUtils.class);
		Optional<FileBOM> fileBom = fileService.toBom(fileEntity);
		Object[] expectedResults = { fileEntity.getId(), fileEntity.getRole(), fileEntity.getPath(),
				fileEntity.getName(), fileEntity.getYear(), fileEntity.isRead(), fileEntity.getIv(),
				fileEntity.getKey() };
		Object[] actualResults = { fileBom.get().getId(), fileBom.get().getRole(), fileBom.get().getPath(), fileBom.get().getName(),
				fileBom.get().getYear(), fileBom.get().isRead(), fileBom.get().getIvBase64(), fileBom.get().getKeyBase64() };
		Assert.assertArrayEquals(expectedResults, actualResults);
	}

	// Test function "toBOM" with invalid IV
	@PrepareForTest({ SymmetricEncryptionFactory.class, FormatUtils.class })
	@Test
	public void testToBomWithInvalidIV() throws Exception {
		FileEntity fileEntity = FileEntity
				.builder()
				.id(0)
				.role("HCMCBD")
				.branchSender("HCMC")
				.path("/#2018-05-10-120000.pdf")
				.name("#2018-05-10-120000.pdf")
				.fileSize(100)
				.date(new Date())
				.year(2018)
				.read(false)
				.key("key")
				.iv("")
				.filesTagsEntities(new ArrayList<>())
				.build();
				
		Optional<SymmetricEncryption> aes256Cryption = Optional.of(PowerMockito.mock(AES256Encryption.class));
		PowerMockito.mockStatic(SymmetricEncryptionFactory.class);
		PowerMockito.when(SymmetricEncryptionFactory.getAES256Encryption(Mockito.any(), Mockito.any()))
				.thenReturn(aes256Cryption);
		PowerMockito.when(aes256Cryption.get().decrypt(Mockito.any())).thenThrow(new EncryptionException(new InvalidAlgorithmParameterException()));
		PowerMockito.mockStatic(FormatUtils.class);
		Optional<FileBOM> fileBom = fileService.toBom(fileEntity);
		assertEquals(Optional.empty(), fileBom);
	}

	// Test function "toBOM" with successful case
	@PrepareForTest({ SymmetricEncryptionFactory.class, FormatUtils.class })
	@Test
	public void testToBom() {
		FileEntity fileEntity = FileEntity
				.builder()
				.id(0)
				.role("HCMCBD")
				.branchSender("HCMC")
				.path("/#2018-05-10-120000.pdf")
				.name("#2018-05-10-120000.pdf")
				.fileSize(100)
				.date(new Date())
				.year(2018)
				.read(false)
				.key("key")
				.iv("iv")
				.filesTagsEntities(new ArrayList<>())
				.build();
		Optional<SymmetricEncryption> aes256Cryption = Optional.of(PowerMockito.mock(AES256Encryption.class));
		PowerMockito.mockStatic(SymmetricEncryptionFactory.class);
		PowerMockito.when(SymmetricEncryptionFactory.getAES256Encryption(Mockito.any(), Mockito.any()))
				.thenReturn(aes256Cryption);
		PowerMockito.mockStatic(FormatUtils.class);
		Optional<FileBOM> fileBom = fileService.toBom(fileEntity);
		Object[] expectedResults = { fileEntity.getId(), fileEntity.getRole(), fileEntity.getBranchSender(),
				fileEntity.getPath(), fileEntity.getName(), fileEntity.getFileSize(), fileEntity.getDate(),
				fileEntity.getYear(), fileEntity.isRead() };
		Object[] actualResults = { fileBom.get().getId(), fileBom.get().getRole(), fileBom.get().getBranchSender(), fileBom.get().getPath(),
				fileBom.get().getName(), fileBom.get().getFileSize(), fileBom.get().getDate(), fileBom.get().getYear(), fileBom.get().isRead() };
		Assert.assertArrayEquals(expectedResults, actualResults);
	}

	// Test function "findById" with successful case
	@Test
	public void findSuccessfullyWithEntity() {
		FileEntity fileEntity = FileEntity.builder()
				.id(0)
				.build();
		PowerMockito.when(fileService.findById(0)).thenReturn(fileEntity);
		FileEntity fileEntityAfter = fileService.findById(fileEntity.getId());
		assertNotNull(fileEntityAfter);
	}

	// Test function "toBoms" with successful case
	@PrepareForTest({ SymmetricEncryptionFactory.class, FormatUtils.class })
	@Test
	public void testToBOMs() {
		FileEntity fileEntity1 = new FileEntity();
		FileEntity fileEntity2 = new FileEntity();
		List<FileEntity> fileEntityList = new ArrayList<FileEntity>();
		fileEntityList.add(fileEntity1);
		fileEntityList.add(fileEntity2);
		Optional<SymmetricEncryption> aes256Cryption = Optional.of(PowerMockito.mock(AES256Encryption.class));
		PowerMockito.mockStatic(SymmetricEncryptionFactory.class);
		PowerMockito.when(SymmetricEncryptionFactory.getAES256Encryption(Mockito.any(), Mockito.any()))
				.thenReturn(aes256Cryption);
		PowerMockito.mockStatic(FormatUtils.class);
		List<FileBOM> fileBOMList = fileService.toBoms(fileEntityList);
		Assert.assertEquals(fileBOMList.size(), fileEntityList.size());
	}

	// Test function "toBoms" with null input
	@Test
	public void testToBomsWithNullCase() {
		List<FileBOM> fileBOMList = fileService.toBoms(null);
		Assert.assertEquals(fileBOMList.size(), 0);
	}

	// Test function "toEntities" with successful case
	@PrepareForTest({ SymmetricEncryptionFactory.class, FormatUtils.class })
	@Test
	public void testToEntities() {
		FileBOM fileBOM1 = FileBOM.builder()
				.id(0)
				.role("CEO")
				.branchSender("HCM")
				.path("/#2018-05-15-120000.pdf")
				.name("#2018-05-15-120000.pdf")
				.fileSize(100)
				.date(new Date())
				.year(2018)
				.read(false)
				.keyBase64("Key")
				.ivBase64("IV")
				.build();
		FileBOM fileBOM2 = FileBOM.builder()
				.id(0)
				.role("CEO")
				.branchSender("HCM")
				.path("/#2018-05-15-120000.pdf")
				.name("#2018-05-15-120000.pdf")
				.fileSize(100)
				.date(new Date())
				.year(2018)
				.read(false)
				.keyBase64("Key")
				.ivBase64("IV")
				.build();
		List<FileBOM> fileBOMList = new ArrayList<FileBOM>();
		fileBOMList.add(fileBOM1);
		fileBOMList.add(fileBOM2);
		Optional<SymmetricEncryption> aes256Cryption = Optional.of(PowerMockito.mock(AES256Encryption.class));
		PowerMockito.mockStatic(SymmetricEncryptionFactory.class);
		PowerMockito.when(SymmetricEncryptionFactory.getAES256Encryption(Mockito.any(), Mockito.any()))
				.thenReturn(aes256Cryption);
		PowerMockito.mockStatic(FormatUtils.class);
		List<FileEntity> fileEntityList = fileService.toFileEntities(fileBOMList);
		Assert.assertEquals(fileBOMList.size(), fileEntityList.size());
	}

	// Test function "toEntities" with null input
	@Test
	public void testToEntitiesWithNullCase() {
		List<FileEntity> fileEntityList = fileService.toFileEntities(null);
		Assert.assertEquals(fileEntityList.size(), 0);
	}

	// Test function "getLastestFile" with successful case
	@Test
	public void testGetLastestFile() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(persistenceService.createQuery(Mockito.anyString())).thenReturn(query);
		Mockito.when(query.setMaxResults(Mockito.any(Integer.class))).thenReturn(query);
		List<FileEntity> list = new ArrayList<>();
		FileEntity file1 = new FileEntity();
		list.add(file1);
		Mockito.when(query.setMaxResults(1).getResultList()).thenReturn(list);
		Assert.assertEquals(Optional.of(list.get(0)), fileService.getLatestFile("ceo"));
	}

	// Test function "getLastestFile" with empty data
	@Test
	public void testGetLastestFileWithEmptyData() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(persistenceService.createQuery(Mockito.anyString())).thenReturn(query);
		Mockito.when(query.setMaxResults(Mockito.any(Integer.class))).thenReturn(query);
		List<FileEntity> list = new ArrayList<>();
		Mockito.when(query.setMaxResults(1).getResultList()).thenReturn(list);
		Assert.assertEquals(Optional.empty(), fileService.getLatestFile("ceo"));
	}

	@Test
	public void testGetListYears() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(persistenceService
				.createQuery("select distinct cast(year as string) FROM FileEntity where role = :role"))
				.thenReturn(query);
		Assert.assertEquals(query.getResultList(), fileService.getListYears("CEO"));
	}

	@Test
	public void TestgetFilesInYear() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(persistenceService.createQuery(Mockito.anyString())).thenReturn(query);
		Assert.assertEquals(query.getResultList(), fileService.getFilesInYear(2018, "CEO"));
	}

}