package com.axonactive.digidocs.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.axonactive.digidocs.email.FileEntity;
import com.axonactive.digidocs.persistence.PersistenceService;

@RunWith(MockitoJUnitRunner.class)
public class FileTagServiceTest {

	@InjectMocks
	private FileTagService fileTagService;

	@Mock
	private PersistenceService<FileTagEntity> fileTagPersistenceService;

	@Test
	public void getFileTag_shouldReturnOptional() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(fileTagPersistenceService.createQuery(Mockito.any())).thenReturn(query);
		Mockito.when(query.getResultList()).thenReturn(new ArrayList());
		assertEquals(Optional.empty(),fileTagService.getFileTag(1, "abc"));
	}

	@Test
	public void getFileTag_shouldReturnFileTagEntity() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(fileTagPersistenceService.createQuery(Mockito.any())).thenReturn(query);
		List<FileTagEntity> fileTagEntities = new ArrayList();
		fileTagEntities.add(new FileTagEntity());
		Mockito.when(query.getResultList()).thenReturn(fileTagEntities);
		assertNotNull(fileTagService.getFileTag(1, "aaa").get());
	} 

	@Test
	public void save_whenFileTagAvailable() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(fileTagPersistenceService.createQuery(Mockito.any())).thenReturn(query);
		List<FileTagEntity> fileTagEntities = new ArrayList();
		fileTagEntities.add(new FileTagEntity());
		Mockito.when(query.getResultList()).thenReturn(fileTagEntities);
		FileTagEntity fileTagEntity = new FileTagEntity(1, FileEntity.builder().id(1).build() , new TagEntity(1, "a", null));
		Mockito.when(fileTagPersistenceService.save(Mockito.any())).thenReturn(fileTagEntity);
		assertNotNull(fileTagService.save(fileTagEntity));
	}
	
	@Test
	public void save_whenFileTagNotAvailable() {
		Query query = Mockito.mock(Query.class);
		Mockito.when(fileTagPersistenceService.createQuery(Mockito.any())).thenReturn(query);
		Mockito.when(query.getResultList()).thenReturn(new ArrayList());
		FileTagEntity entity=new FileTagEntity();
		Mockito.when(fileTagPersistenceService.save(Mockito.any())).thenReturn(entity);
		FileTagEntity fileTagEntity = new FileTagEntity(1,FileEntity.builder().id(1).build(), new TagEntity(1, "a", null));
		assertEquals(entity, fileTagService.save(fileTagEntity));
	}
	
	

}
