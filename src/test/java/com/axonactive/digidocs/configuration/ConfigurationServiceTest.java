package com.axonactive.digidocs.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Query;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.axonactive.digidocs.encryption.SymmetricEncryption;
import com.axonactive.digidocs.encryption.SymmetricEncryptionFactory;
import com.axonactive.digidocs.persistence.PersistenceService;
import com.axonactive.digidocs.utils.FormatUtils;

@PrepareForTest({ SymmetricEncryptionFactory.class, BCrypt.class })
@RunWith(PowerMockRunner.class)
public class ConfigurationServiceTest {

	private static final String EXPECTED_CRYPTION = "result";

	@Mock
	PersistenceService<ConfigurationEntity> configurationPersistenceService;

	@InjectMocks
	ConfigurationService configurationService;

	@BeforeClass
	public static void init() throws Exception {
		Optional<SymmetricEncryption> symmetricEncryption = Optional.of(Mockito.mock(SymmetricEncryption.class));
		PowerMockito.mockStatic(SymmetricEncryptionFactory.class);
		PowerMockito.when(SymmetricEncryptionFactory.getAES256Encryption(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(symmetricEncryption);
		PowerMockito.when(symmetricEncryption.get().decrypt(Mockito.any())).thenReturn(EXPECTED_CRYPTION.getBytes());
		PowerMockito.when(symmetricEncryption.get().encrypt(Mockito.any())).thenReturn(EXPECTED_CRYPTION.getBytes());
	}

	@Test
	public void testToBom() {
		ConfigurationEntity configurationEntity = new ConfigurationEntity();
		configurationEntity.setValue("JFH");
		Optional<ConfigurationBOM> configurationBOM = configurationService.toBom(configurationEntity);
		assertEquals(EXPECTED_CRYPTION, configurationBOM.get().getValue());
	}

	@Test
	public void shouldReturnNullWhenConfigurationEntityIsNull() {
		ConfigurationEntity configurationEntity = new ConfigurationEntity();
		configurationEntity = null;
		assertEquals(Optional.empty(), configurationService.toBom(configurationEntity));
	}

	@Test
	public void shouldReturnNullWhenHaveException() {
		ConfigurationEntity configurationEntity = new ConfigurationEntity();
		assertEquals(Optional.empty(), configurationService.toBom(configurationEntity));
	}

	@Test
	public void shouldReturnNullWhenConfigurationBomIsNull() {
		ConfigurationBOM configurationBOM = new ConfigurationBOM();
		configurationBOM = null;
		assertEquals(Optional.empty(), configurationService.toEntity(configurationBOM));
	}

	@Test
	public void testToEntity() {
		ConfigurationBOM configurationBOM = new ConfigurationBOM();
		configurationBOM.setValue("xyz");
		configurationBOM.setId(1);
		Optional<ConfigurationEntity> configurationEntity = configurationService.toEntity(configurationBOM);
		assertEquals(configurationBOM.getId(), configurationEntity.get().getId());
		assertEquals(FormatUtils.toBase64(EXPECTED_CRYPTION.getBytes()), configurationEntity.get().getValue());
	}

	@Test
	public void shouldReturnNullWhenConfigurationEntityHasException() {
		ConfigurationBOM configurationBOM = new ConfigurationBOM();
		assertEquals(Optional.empty(), configurationService.toEntity(configurationBOM));
	}

	@Test
	public void testFindByKey() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(configurationPersistenceService.createQuery(Mockito.anyString())).thenReturn(query);
		Mockito.when(query.setMaxResults(Mockito.any(Integer.class))).thenReturn(query);
		List<ConfigurationEntity> list = new ArrayList<>();
		ConfigurationEntity configurationEntity = new ConfigurationEntity();
		list.add(configurationEntity);
		Mockito.when(query.setMaxResults(1).getResultList()).thenReturn(list);
		assertEquals(Optional.of(list.get(0)), configurationService.findByKey("source.directory"));
	}

	@Test
	public void shouldReturnEmptyWhenWrongKey() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(configurationPersistenceService.createQuery(Mockito.anyString())).thenReturn(query);
		Mockito.when(query.setMaxResults(Mockito.any(Integer.class))).thenReturn(query);
		List<ConfigurationEntity> list = new ArrayList<>();
		Mockito.when(query.setMaxResults(1).getResultList()).thenReturn(list);
		assertFalse(configurationService.findByKey("source").isPresent());
	}

	@Test
	public void testGetAllProperties() {
		List<ConfigurationEntity> configurationEntityList = new ArrayList<>();
		Mockito.when(configurationPersistenceService.getAll()).thenReturn(configurationEntityList);
		configurationEntityList.add(new ConfigurationEntity(1, "database.salt", "abc"));

		Map<String, ConfigurationBOM> properties = configurationService.getAllProperties();

		assertNotNull(properties);
	}

	@Test
	public void testUpdateConfigurations() {
		Map<String, ConfigurationBOM> properties = new HashMap<>();
		properties.put("database.salt", new ConfigurationBOM(1, "database.salt", "abc"));
		Mockito.when(configurationPersistenceService.update(Mockito.any())).thenReturn(new ConfigurationEntity());

		assertTrue(configurationService.updateConfigurations(properties));
	}

	@Test
	public void testGenerateSalt() {
		PowerMockito.mockStatic(BCrypt.class);
		PowerMockito.when(BCrypt.gensalt()).thenReturn("abc");

		String salt = ConfigurationService.generateSalt();

		assertNotNull(salt);
	}
}
