package com.axonactive.digidocs.email;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.axonactive.digidocs.configuration.ConfigurationBOM;
import com.axonactive.digidocs.configuration.ConfigurationService;
import com.axonactive.digidocs.encryption.EncryptionException;
import com.axonactive.digidocs.encryption.SymmetricEncryption;
import com.axonactive.digidocs.encryption.SymmetricEncryptionFactory;
import com.axonactive.digidocs.persistence.AbstractCRUDBean;
import com.axonactive.digidocs.persistence.PersistenceService;
import com.axonactive.digidocs.utils.CollectionUtils;
import com.axonactive.digidocs.utils.FormatUtils;
import com.axonactive.digidocs.utils.OptionalUtils;

@Stateless
public class FileService extends AbstractCRUDBean<FileEntity> {
	private String salt;
	private final static Logger LOGGER = Logger.getLogger(FileService.class);
	@Inject
	private PersistenceService<FileEntity> persistenceService;
	@Inject
	private ConfigurationService configurationService;


	@PostConstruct
	private void init() {
		Optional<ConfigurationBOM> salConfigurationOptional = configurationService.findByKey("database.salt").flatMap(configurationService::toBom);
		if(!salConfigurationOptional.isPresent()) {
			LOGGER.error("Can't get database.salt");
			return;
		}
		salt = salConfigurationOptional.get().getValue();
	}

	@Override
	protected PersistenceService<FileEntity> getPersistenceService() {
		return this.persistenceService;
	}
	
	private Optional<SymmetricEncryption> generateEncryption(String role) {
		return SymmetricEncryptionFactory.getAES256Encryption(role, salt);
	}

	public Optional<FileEntity> toEntity(FileBOM bom) {
		byte[] encryptedKey;
		byte[] encryptedIV;

		if (bom != null) {
			Optional<SymmetricEncryption> symmetricEncryptionOptional = generateEncryption(bom.getRole());
			if(!symmetricEncryptionOptional.isPresent()) {
				LOGGER.error("Can't init the key to encrypt");
				return Optional.empty();
			}
			SymmetricEncryption symmetricEncryption = symmetricEncryptionOptional.get();
			try {
				encryptedKey = symmetricEncryption.encrypt(FormatUtils.decodeFromBase64(bom.getKeyBase64()));
				encryptedIV = symmetricEncryption.encrypt(FormatUtils.decodeFromBase64(bom.getIvBase64()));
			} catch (EncryptionException e) {
				LOGGER.error("Encrypt the key failed", e);
				return Optional.empty();
			}

			return Optional.of(FileEntity.builder()
					.id(bom.getId())
					.role(bom.getRole())
					.branchSender(bom.getBranchSender())
					.path(bom.getPath())
					.name(bom.getName())
					.fileSize(bom.getFileSize())
					.date(bom.getDate())
					.year(bom.getYear())
					.read(bom.isRead())
					.key(FormatUtils.toBase64(encryptedKey))
					.iv(FormatUtils.toBase64(encryptedIV))
					.filesTagsEntities(Collections.emptyList())
					.build());
		}
		return Optional.empty();
	}

	public Optional<FileBOM> toBom(FileEntity entity) {
		byte[] encryptKey;
		byte[] encryptIV;
		if (entity != null) {
			Optional<SymmetricEncryption> symmetricEncryptionOptional = generateEncryption(entity.getRole());
			if(!symmetricEncryptionOptional.isPresent()) {
				LOGGER.error("Can't init the key to decrypt");
				return Optional.empty();
			}
			SymmetricEncryption symmetricEncryption = symmetricEncryptionOptional.get();
			try {
				encryptKey = symmetricEncryption.decrypt(FormatUtils.decodeFromBase64(entity.getKey()));
				encryptIV = symmetricEncryption.decrypt(FormatUtils.decodeFromBase64(entity.getIv()));
			} catch (EncryptionException e) {
				LOGGER.error("Decrypt the key failed", e);
				return Optional.empty();
			}
			return Optional.of(FileBOM.builder()
					.id(entity.getId())
					.role(entity.getRole())
					.branchSender(entity.getBranchSender())
					.path(entity.getPath())
					.name(entity.getName())
					.fileSize(entity.getFileSize())
					.date(entity.getDate())
					.year(entity.getYear())
					.read(entity.isRead())
					.keyBase64(FormatUtils.toBase64(encryptKey))
					.ivBase64(FormatUtils.toBase64(encryptIV))
					.build());
		}
		return Optional.empty(); 
	}
	
	/**
	 * Get the latest Document in C-level's folder
	 * @param role
	 * @return
	 */
	public Optional<FileEntity> getLatestFile(String role) {
		Query query = this.persistenceService.createQuery("FROM FileEntity where role = :role order by id DESC");
		query.setParameter("role", role);
		@SuppressWarnings("unchecked")
		List<FileEntity> listFiles = query.setMaxResults(1).getResultList();
		if (listFiles.size() > 0) {
			return Optional.of(listFiles.get(0));
		}
		return Optional.empty();
	}
	
	
	/**
	 * Get the list years in C-level's folder
	 * @param role
	 * @return years
	 */
	@SuppressWarnings("unchecked")
	public List<String> getListYears(String role) {
		Query query = this.persistenceService
				.createQuery("select distinct cast(year as string) FROM FileEntity where role = :role");
		query.setParameter("role", role);
		List<String> years = query.getResultList();
		years.sort((y1, y2) -> y2.compareTo(y1));
		return years;
	}
	
	/**
	 * Get the list files in C-level's folder
	 * @param role
	 * @return list
	 */
	@SuppressWarnings("unchecked")
	public List<FileEntity> getFilesInYear(Integer year, String role) {
		Query query = this.persistenceService
				.createQuery("FROM FileEntity where role = :role " + "and year = :year order by id DESC");
		query.setParameter("role", role);
		query.setParameter("year", year);
		return query.getResultList();
	}

	public List<FileEntity> toFileEntities(List<FileBOM> boms) {
		return OptionalUtils.flatMap(CollectionUtils.nullSafeStream(boms)
				.map(this::toEntity))
				.collect(Collectors.toList());
	}

	public List<FileBOM> toBoms(List<FileEntity> entities) {
		return OptionalUtils.flatMap(CollectionUtils.nullSafeStream(entities)
				.map(this::toBom))
				.collect(Collectors.toList());
	}
}
