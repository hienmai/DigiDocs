package com.axonactive.digidocs.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.axonactive.digidocs.persistence.PersistenceService;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceTest {
	@InjectMocks
	private TagService tagService;

	@Mock
	private PersistenceService<TagEntity> tagPersistenceService;

	@Test
	public void toBom_shouldReturnEmptyOptionalWhenEntityNull() {
		TagEntity entity = null;
		assertEquals(Optional.empty(), tagService.toBom(entity));
	}

	@Test
	public void toBom_shouldReturnBOM() {
		TagEntity entity = new TagEntity(1, "newtag", new ArrayList());
		TagBOM bom = tagService.toBom(entity).get();
		assertEquals(Optional.of(entity.getId()), Optional.of(bom.getId()));
		assertEquals(Optional.of(entity.getName()), Optional.of(entity.getName()));
	}

	@Test
	public void toEntity_shouldReturnEmptyOptionalWhenBomNull() {
		TagBOM bom = null;
		assertEquals(Optional.empty(), tagService.toEntity(bom));

	}

	@Test
	public void toEntity_shouldReturnEntity() {
		TagBOM bom = new TagBOM(1, "newtag");
		TagEntity entity = tagService.toEntity(bom).get();
		assertEquals(Optional.of(bom.getId()), Optional.of(entity.getId()));
		assertEquals(Optional.of(bom.getName()), Optional.of(entity.getName()));
	}

	@Test
	public void getTagByName_shouldReturnOptional() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(tagPersistenceService.createQuery(Mockito.anyString())).thenReturn(query);
		Mockito.when(query.getResultList()).thenReturn(new ArrayList());
		assertEquals(Optional.empty(), tagService.getTagByName("abc"));
	}

	@Test
	public void getTagByName_shouldReturnTagEntity() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(tagPersistenceService.createQuery(Mockito.anyString())).thenReturn(query);
		List<TagEntity> tagEntities = new ArrayList(Arrays.asList(new TagEntity()));
		Mockito.when(query.getResultList()).thenReturn(tagEntities);
		assertEquals(Optional.of(tagEntities.get(0)), tagService.getTagByName("abc"));
	}

	@Test
	public void tryToSave_whenTagNotAvailable() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(tagPersistenceService.createQuery(Mockito.anyString())).thenReturn(query);
		List<TagEntity> tagEntities = new ArrayList(Arrays.asList(new TagEntity()));
		Mockito.when(query.getResultList()).thenReturn(tagEntities);
		assertEquals(tagEntities.get(0), tagService.tryToSave("abc"));
	}

	@Test
	public void tryToSave_whenTagAvailable() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(tagPersistenceService.createQuery(Mockito.anyString())).thenReturn(query);
		Mockito.when(query.getResultList()).thenReturn(new ArrayList());
		TagEntity entity = new TagEntity();
		Mockito.when(tagPersistenceService.save(Mockito.any())).thenReturn(entity);
		assertEquals(entity, tagService.tryToSave("abc"));
	}

	@Test
	public void save_shouldReturnTagEntity() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(tagPersistenceService.createQuery(Mockito.anyString())).thenReturn(query);
		List<TagEntity> tagEntities = new ArrayList(Arrays.asList(new TagEntity()));
		Mockito.when(query.getResultList()).thenReturn(tagEntities);
		TagEntity entity = new TagEntity(1, "meeting", new ArrayList<>());
		assertNotNull(tagService.save(entity));

	}

	@Test
	public void test_listAllTags() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(tagPersistenceService.createQuery(Mockito.any())).thenReturn(query);
		assertNotNull(tagService.listAllTags(1));
	}

	@Test
	public void test_getAllTagsByRole_successfullCase() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(tagPersistenceService.createQuery(Mockito.anyString())).thenReturn(query);
		List<Object[]> resultList = new ArrayList<>();
		Object[] objs1 = new Object[3];
		objs1[0] = 1;
		objs1[1] = "International";
		objs1[2] = 1;
		resultList.add(objs1);

		Object[] objs2 = new Object[3];
		objs2[0] = 3;
		objs2[1] = "Important";
		objs2[2] = 5;
		resultList.add(objs2);

		Mockito.when(query.getResultList()).thenReturn(resultList);

		Map<Character, List<ResultTag>> resultTagMap = tagService.getAllTagsByRole("HCMCBD");

		assertEquals(1, resultTagMap.get('I').get(0).getTagCount());
		assertEquals(5, resultTagMap.get('I').get(1).getTagCount());
	}

	@Test
	public void test_getAllTagsByRole_emptyMap() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(tagPersistenceService.createQuery(Mockito.anyString())).thenReturn(query);
		List<Object[]> resultList = new ArrayList<>();
		Mockito.when(query.getResultList()).thenReturn(resultList);

		Map<Character, List<ResultTag>> resultTagMap = tagService.getAllTagsByRole("HCMCBD");

		assertNull(resultTagMap.get('A'));
	}

	@Test
	public void toBoms_test() {
		
		List<TagEntity> tagEntity = new ArrayList<>(Arrays.asList(new TagEntity()));
			
		
		assertNotNull(tagService.toBoms(new ArrayList<>(Arrays.asList(new TagEntity(), new TagEntity()))));
	}
	
	

}