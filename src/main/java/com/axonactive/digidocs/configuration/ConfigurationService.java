package com.axonactive.digidocs.configuration;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import com.axonactive.digidocs.encryption.SymmetricEncryption;
import com.axonactive.digidocs.encryption.SymmetricEncryptionFactory;
import com.axonactive.digidocs.persistence.AbstractCRUDBean;
import com.axonactive.digidocs.persistence.PersistenceService;
import com.axonactive.digidocs.utils.FormatUtils;
import com.axonactive.digidocs.utils.OptionalUtils;

@Stateless
public class ConfigurationService extends AbstractCRUDBean<ConfigurationEntity> {

	private static final Logger LOGGER = Logger.getLogger(ConfigurationService.class);

	private static final SymmetricEncryption SYMMETRIC_ENCRYPTION;

	static {
		 Optional<SymmetricEncryption> symmetricEncryptionOptional = SymmetricEncryptionFactory
					.getAES256Encryption("Some Random Password", "$2a$06$qVxS016NiF4nfENsbvNzqe");// salt must follow the format
		 if (symmetricEncryptionOptional.isPresent()) {
			 SYMMETRIC_ENCRYPTION = symmetricEncryptionOptional.get();
		 } else {
			 LOGGER.error("Can't init symmetricEncryption");
			 throw new IllegalArgumentException();
		 }
	}
	
	@Inject
	private PersistenceService<ConfigurationEntity> configurationPersistenceService;

	@Override
	protected PersistenceService<ConfigurationEntity> getPersistenceService() {
		return this.configurationPersistenceService;
	}

	public Optional<ConfigurationBOM> toBom(ConfigurationEntity configurationEntity) {
		if (configurationEntity == null) {
			return Optional.empty();
		}
		String value;
		try {
			value = new String(SYMMETRIC_ENCRYPTION.decrypt(FormatUtils.decodeFromBase64(configurationEntity.getValue())),
					Charset.forName("UTF-8"));
		} catch (Exception e) {
			LOGGER.error("Can't decrypt", e);
			return Optional.empty();
		}
		return Optional.of(new ConfigurationBOM(configurationEntity.getId(), configurationEntity.getKey(), value));
	}

	public Optional<ConfigurationEntity> toEntity(ConfigurationBOM configurationBOM) {
		if (configurationBOM == null) {
			return Optional.empty();
		}
		String value;
		try {
			value = FormatUtils.toBase64(
					(SYMMETRIC_ENCRYPTION.encrypt(configurationBOM.getValue().getBytes(Charset.forName("UTF-8")))));
		} catch (Exception e) {
			LOGGER.error("Can't encrypt", e);
			return Optional.empty();
		}
		return Optional.of(new ConfigurationEntity(configurationBOM.getId(), configurationBOM.getKey(), value));
	}

	public Optional<ConfigurationEntity> findByKey(String key) {
		Query query = this.getPersistenceService().createQuery("from ConfigurationEntity where key = :key");
		query.setParameter("key", key);
		@SuppressWarnings("unchecked")
		List<ConfigurationEntity> configurationEntityList = query.setMaxResults(1).getResultList();
		if (configurationEntityList.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(configurationEntityList.get(0));
	}

	public Map<String, ConfigurationBOM> getAllProperties() {
		return OptionalUtils.flatMap(this.getAll().stream().map(this::toBom))
				.collect(Collectors.toMap(ConfigurationBOM::getKey, Function.identity()));
	}

	private Optional<ConfigurationEntity> updateConfigurationEntity(ConfigurationEntity configurationEntity) {
		return Optional.of(update(configurationEntity));
	}

	public boolean updateConfigurations(Map<String, ConfigurationBOM> properties) {
		OptionalUtils.flatMap(properties.keySet().stream()
						.map(properties::get)
						.map(this::toEntity))
						.forEach(this::updateConfigurationEntity);
		return true;
	}

	public static String generateSalt() {
		return BCrypt.gensalt();
	}
}
