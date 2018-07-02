package com.axonactive.digidocs.tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import com.axonactive.digidocs.persistence.AbstractCRUDBean;
import com.axonactive.digidocs.persistence.PersistenceService;
import com.axonactive.digidocs.utils.CollectionUtils;
import com.axonactive.digidocs.utils.OptionalUtils;

@Stateless
public class TagService extends AbstractCRUDBean<TagEntity> {

	@Inject
	private PersistenceService<TagEntity> tagPersistenceService;

	@Override
	protected PersistenceService<TagEntity> getPersistenceService() {
		return this.tagPersistenceService;
	}

	public Optional<TagBOM> toBom(TagEntity entity) {
		return Optional.ofNullable(entity)
						.map(en -> new TagBOM(en.getId(), en.getName()));
	}

	public List<TagBOM> toBoms(List<TagEntity> entities) {
		return OptionalUtils.flatMap(CollectionUtils.nullSafeStream(entities).map(this::toBom))
				.collect(Collectors.toList());
	}

	public Optional<TagEntity> toEntity(TagBOM bom) {
		return Optional.ofNullable(bom)
						.map(b-> new TagEntity(b.getId(), b.getName(), Collections.emptyList()));
	}

	public Optional<TagEntity> getTagByName(String tagName) {
		Query query = this.getPersistenceService().createQuery("FROM TagEntity WHERE name=:name");
		query.setParameter("name", tagName.trim().toLowerCase());
		List<TagEntity> tagEntities = query.getResultList();
		return tagEntities.isEmpty() ? Optional.empty() : Optional.of(tagEntities.get(0));
	}

	public TagEntity tryToSave(String name) {
		Optional<TagEntity> tagEntityOptional = getTagByName(name);
		if (tagEntityOptional.isPresent()) {
			return tagEntityOptional.get();
		}
		return getPersistenceService().save(new TagEntity(null, name.trim().toLowerCase(), Collections.emptyList()));
	}

	@Override
	public TagEntity save(TagEntity entity) {
		return tryToSave(entity.getName());
	}

	public List<TagEntity> listAllTags(int fileId) {
		Query query = this.getPersistenceService().createQuery(
				"select distinct t, ft.id  from TagEntity t, FileTagEntity ft where ft.fileEntity.id = :fileId and ft.tagEntity.id = t.id order by ft.id");
		query.setParameter("fileId", fileId);
		
		return (List<TagEntity>) query.getResultList().stream().map(x -> ((Object[])x)[0]).collect(Collectors.toList());
	}

	public Map<Character, List<ResultTag>> getAllTagsByRole(String role) {
		Query query = this.getPersistenceService()
				.createQuery("SELECT t.id, t.name, count(t.id) " + "FROM TagEntity t, FileEntity f, FileTagEntity ft "
						+ "WHERE f.role = :role " + "AND ft.fileEntity.id = f.id " + "AND t.id = ft.tagEntity.id "
						+ "GROUP BY t.id " + "ORDER BY t.name");
		query.setParameter("role", role);
		List<Object[]> resultList = query.getResultList();
		Map<Character, List<ResultTag>> resultTagMap = convertObjectsToMap(resultList);
		return resultTagMap;
	}

	private Map<Character, List<ResultTag>> convertObjectsToMap(List<Object[]> objectList) {
		Map<Character, List<ResultTag>> resultTagMap = new HashMap<>();
		for (Object[] obj : objectList) {
			ResultTag resultTag = ResultTag.builder()
									.id(Integer.parseInt(obj[0].toString()))
									.name(obj[1].toString())
									.count(Integer.parseInt(obj[2].toString()))
									.build();

			Character firstAlphabet = resultTag.getName().toUpperCase().charAt(0);

			List<ResultTag> tempResultTagList = new ArrayList<>();
			if (resultTagMap.containsKey(firstAlphabet)) {
				tempResultTagList = resultTagMap.get(firstAlphabet);
			}
			tempResultTagList.add(resultTag);
			resultTagMap.put(firstAlphabet, tempResultTagList);
		}
		return resultTagMap;
	}
}
