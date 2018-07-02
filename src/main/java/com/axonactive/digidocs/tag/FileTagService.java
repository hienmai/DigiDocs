package com.axonactive.digidocs.tag;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import com.axonactive.digidocs.persistence.AbstractCRUDBean;
import com.axonactive.digidocs.persistence.PersistenceService;

@Stateless
public class FileTagService extends AbstractCRUDBean<FileTagEntity> {

	@Inject
	private PersistenceService<FileTagEntity> fileTagPersistenceService;

	@Override
	protected PersistenceService<FileTagEntity> getPersistenceService() {
		return this.fileTagPersistenceService;

	}
	
	/**
	 * Get the tags of the Document
	 * @param fileId
	 * @param tagName
	 * @return fileTagEntities
	 */
	public Optional<FileTagEntity> getFileTag(int fileId, String tagName) {
		Query query = this.getPersistenceService().createQuery("FROM FileTagEntity where fileEntity.id=:fileId and tagEntity.name=:tagName");
		query.setParameter("fileId", fileId);
		query.setParameter("tagName", tagName.trim().toLowerCase());
		List<FileTagEntity> fileTagEntities=query.getResultList();
		return fileTagEntities.isEmpty()? Optional.empty() : Optional.of(fileTagEntities.get(0));
	}

	/**
	 * Save new tag to database
	 */
	@Override
	public FileTagEntity save(FileTagEntity fileTagEntity) {
		Optional<FileTagEntity> availableFileTagEntityOptional = getFileTag(fileTagEntity.getFileEntity().getId(), fileTagEntity.getTagEntity().getName());
		if(!availableFileTagEntityOptional.isPresent()) {
			return this.getPersistenceService().save(fileTagEntity);
		}
		return availableFileTagEntityOptional.get();
	}
}
