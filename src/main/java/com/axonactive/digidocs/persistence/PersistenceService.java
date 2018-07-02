package com.axonactive.digidocs.persistence;

import java.util.List;

import javax.persistence.Query;

public interface PersistenceService<T> {
	
	/**
	 * Save the entity to database
	 * @param entity
	 * @return T
	 */
	T save(T entity);
	
	/**
	 * Update the entity to database
	 * @param entity
	 * @return T
	 */
	T update(T update);
	
	/**
	 * Remove the entity from database
	 * @param entity
	 */
	void removeEntity(T entity);
	
	/**
	 * Remove the entity from database by entity's id
	 * @param id
	 */
	void remove(Integer id);
	
	/**
	 * Find the entity from database by entity's id
	 * @param id
	 * @return T
	 */
	T findById(Integer id);
	
	/**
	 * Create HQL query
	 * @param query
	 * @return query
	 */
	Query createQuery(String query);
	
	/**
	 * Get all the entity from database
	 * @return list
	 */
	List<T> getAll();
}
