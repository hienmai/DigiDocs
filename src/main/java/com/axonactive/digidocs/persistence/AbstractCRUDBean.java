package com.axonactive.digidocs.persistence;

import java.util.List;

public abstract class AbstractCRUDBean<T extends IEntity> {
	
	protected abstract PersistenceService<T> getPersistenceService();
	
	/**
	 * Save the entity to database
	 * @param entity
	 * @return T
	 */
	public T save(T entity) {
		return this.getPersistenceService().save(entity);
	}
	
	/**
	 * Update the entity to database
	 * @param entity
	 * @return T
	 */
	public T update(T entity) {
		return this.getPersistenceService().update(entity);
	}
	
	/**
	 * Remove the entity from database
	 * @param entity
	 */
	public void removeEntity(T entity) {
		this.getPersistenceService().removeEntity(entity);
	}
	
	/**
	 * Remove the entity from database by entity's id
	 * @param id
	 */
	public void remove(Integer id) {
		this.getPersistenceService().remove(id);
	}
	
	/**
	 * Find the entity from database by entity's id
	 * @param id
	 * @return T
	 */
	public T findById(Integer id) {
		return this.getPersistenceService().findById(id);
	}
	
	/**
	 * Get all the entity from database
	 * @return list
	 */
	public List<T> getAll() {	
		return this.getPersistenceService().getAll();
	}
}

