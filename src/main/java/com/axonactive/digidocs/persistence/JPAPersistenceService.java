package com.axonactive.digidocs.persistence;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Dependent
public class JPAPersistenceService<T extends IEntity> implements PersistenceService<T> {

	@PersistenceContext(name = "DigiDocs")
	protected EntityManager em;

	private Class<T> persistentClass;
	

	
	@SuppressWarnings("unchecked")
	private Class<T> getPersistentClass() {
		if (this.persistentClass == null) {
			this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
		}
		return this.persistentClass;
	}

	@SuppressWarnings("unchecked")
	private Class<T> resolveEntity(InjectionPoint ip) {
		ParameterizedType type = (ParameterizedType) ip.getType();
		Type[] typeArgs = type.getActualTypeArguments();
		return (Class<T>) typeArgs[0];
	}
	
	@Inject
	public void inject(InjectionPoint ip) {
		try {
			this.persistentClass = resolveEntity(ip);
		} catch (Exception e) {
			throw new IllegalArgumentException("Entity class at injection point is invalid", e);
		}
	}
	
	 
	@Override
	public T save(T entity) {
		this.em.persist(entity);
		return entity;
	}

	@Override
	public T update(T entity) {
		return this.em.merge(entity);
	}

	@Override
	public void removeEntity(T entity) {
		this.em.remove(entity);
	}

	@Override
	public void remove(Integer id) {
		T entity = findById(id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	@Override
	public T findById(Integer id) {
		 return em.find(getPersistentClass(), id);
	}

	@Override
	public Query createQuery(String query) {
		return this.em.createQuery(query);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll() {
		Query query = this.em.createQuery("SELECT e FROM " + getPersistentClass().getSimpleName() + " e");
		return query.getResultList();
	}

}