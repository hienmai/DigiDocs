package com.axonactive.digidocs.webapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.primefaces.model.StreamedContent;

import com.axonactive.digidocs.configuration.ConfigurationEntity;
import com.axonactive.digidocs.email.FileBOM;
import com.axonactive.digidocs.email.FileCryptionStorageService;
import com.axonactive.digidocs.email.FileEntity;
import com.axonactive.digidocs.email.FileService;
import com.axonactive.digidocs.encryption.SymmetricEncryption;
import com.axonactive.digidocs.encryption.SymmetricEncryptionFactory;
import com.axonactive.digidocs.login.CLevelRole;
import com.axonactive.digidocs.login.Role;
import com.axonactive.digidocs.login.Roles;
import com.axonactive.digidocs.persistence.PersistenceService;
import com.axonactive.digidocs.tag.FileTagEntity;
import com.axonactive.digidocs.tag.FileTagService;
import com.axonactive.digidocs.tag.TagBOM;
import com.axonactive.digidocs.tag.TagEntity;
import com.axonactive.digidocs.tag.TagService;
import com.axonactive.digidocs.utils.CDIUtils;
import com.axonactive.digidocs.utils.FacesContextService;
import com.axonactive.digidocs.utils.FacesContextUtils;
import com.axonactive.digidocs.utils.SessionUtils;

@PrepareForTest({ SessionUtils.class, SymmetricEncryptionFactory.class, Roles.class, CDIUtils.class, FacesContext.class,
		FacesContextUtils.class })
@RunWith(PowerMockRunner.class)
public class WebHandlerTest {

	@InjectMocks
	private WebHandler webHandler;

	@Mock
	private static StreamedContent streamContent;

	@Mock
	FileService fileService;

	@Mock
	FacesContextService facesContextService;

	@Mock
	PersistenceService<ConfigurationEntity> configurationPersistenceService;

	@Mock
	FileCryptionStorageService fileCryptionStorageService;

	private FileEntity fileEntity = FileEntity.builder().id(1).role("CD").build();;

	@Mock
	private FileTagService fileTagService;

	@Mock
	TagService tagService;

	
	private FileBOM fileBOM = FileBOM.builder().id(1).branchSender("HCM").role("HCMCBD").build();
	
	@Mock
	List<TagEntity> tags;
	
	@Before
	public void setUp() {
		PowerMockito.when(fileService.toBom(fileEntity)).thenReturn(Optional.of(fileBOM));
		PowerMockito.mockStatic(SymmetricEncryptionFactory.class);
		PowerMockito.mockStatic(FacesContextUtils.class);
		SymmetricEncryption symmetricEncryption = Mockito.mock(SymmetricEncryption.class);

		PowerMockito.when(SymmetricEncryptionFactory.getAES256EncryptionFromBase64(Mockito.any(String.class),
				Mockito.any(String.class))).thenReturn(symmetricEncryption);
		PowerMockito.when(fileService.findById(Mockito.anyInt())).thenReturn(fileEntity);
		PowerMockito.when(fileCryptionStorageService.readFile(Mockito.anyString(), Mockito.eq(symmetricEncryption)))
				.thenReturn(new byte[1]);
	}

	@Test
	public void test_show_file_by_id_case_1() throws IOException {
		ComponentSystemEvent event = PowerMockito.mock(ComponentSystemEvent.class);

		PowerMockito.when(FacesContextUtils.getAttribute(event, "idFile")).thenReturn(Optional.of("MQ=="));
		PowerMockito.when(FacesContextUtils.isPostback()).thenReturn(false);

		PowerMockito.when(fileService.findById(1)).thenReturn(fileEntity);
		webHandler.showFileById(event);
	}

	@Test
	public void test_show_file_by_id_case_2() throws IOException {
		ComponentSystemEvent event = PowerMockito.mock(ComponentSystemEvent.class);

		PowerMockito.when(FacesContextUtils.getAttribute(event, "idFile")).thenReturn(Optional.of("MQ=="));
		PowerMockito.when(FacesContextUtils.isPostback()).thenReturn(false);

		FileEntity fileEntity = FileEntity.builder().id(1).build();

		PowerMockito.when(fileService.findById(1)).thenReturn(fileEntity);

		PowerMockito.when(fileService.toBom(Mockito.any())).thenReturn(Optional.empty());
		webHandler.showFileById(event);
	}

	public void testShowFileById() throws IOException {
		ComponentSystemEvent componentSystemEvent = Mockito.mock(ComponentSystemEvent.class);
		UIComponent uiComponent = Mockito.mock(UIComponent.class);
		PowerMockito.when(componentSystemEvent.getComponent()).thenReturn(uiComponent);
		Map<String, Object> map = Mockito.mock(Map.class);
		PowerMockito.when(uiComponent.getAttributes()).thenReturn(map);
		PowerMockito.when(map.get("idFile")).thenReturn("MQ==");
		PowerMockito.when(fileService.findById(Mockito.anyInt())).thenReturn(fileEntity);
		PowerMockito.mockStatic(FacesContext.class);
		FacesContext facesContext = Mockito.mock(FacesContext.class);
		PowerMockito.when(facesContext.getCurrentInstance()).thenReturn(facesContext);
		PowerMockito.when(facesContext.isPostback()).thenReturn(false);
		PowerMockito.when(fileEntity.getRole()).thenReturn("CD");
		Mockito.when(fileService.findById(Mockito.anyInt())).thenReturn(fileEntity);
		Whitebox.setInternalState(webHandler, "currentRole", "CD");
		webHandler.showFileById(componentSystemEvent);
		assertNotNull(webHandler.getContent());

	}

	@Test
	public void testUpdateStateDocument() {
		String idDocument = "1";
		Mockito.when(facesContextService.getParameter("id")).thenReturn(idDocument);
		FileEntity fileEntity = FileEntity.builder().read(true).build();
		Mockito.when(fileService.findById(Mockito.anyInt())).thenReturn(fileEntity);
		webHandler.updateStateDocument();
		assertEquals(fileEntity.isRead(), true);
		Mockito.verify(fileService).update(Mockito.any());
	}

	@Test
	public void testUpdateFileName() {
		FileEntity fileEntity = FileEntity.builder().id(1).build();

		webHandler.setEditingFileEntity(fileEntity);
		webHandler.updateFileName();

		FileEntity editingFileEntity = webHandler.getEditingFileEntity();
		FileBOM currentFileBOM = webHandler.getCurrentFileBOM();

		assertEquals(currentFileBOM.getName(), editingFileEntity.getName());
	}

	@Test
	public void testReadFileInYearFolder() {
		List<FileEntity> fileList = webHandler.readFileInYearFolder(2018, "HCMCBD");
		assertNotNull(fileList);
	}

	@Test
	public void testBranchName() {
		FileBOM fileBOM = FileBOM.builder().branchSender("HCM").build();
		webHandler.setCurrentFileBOM(fileBOM);
		assertEquals("HCM", webHandler.branchName(""));
	}

	@Test
	public void testReadSpecificPdfFile() throws IOException {
		Mockito.when(fileService.findById(Mockito.anyInt())).thenReturn(null);
		PowerMockito.when(facesContextService.getResource(Mockito.anyString())).thenReturn(new byte[1]);
		webHandler.readSpecificPdfFile(1);
	}

	@Test
	public void testReadSpecificPdfFileIfNotEntityFound() {
		Mockito.when(fileService.findById(1)).thenReturn(null);
		webHandler.readSpecificPdfFile(1);
	}

	@Test
	public void testSetDefaultPage() throws IOException {
		PowerMockito.when(facesContextService.getResource(Mockito.anyString())).thenReturn(new byte[1]);
		webHandler.setDefaultPage();
		assertNotNull(webHandler.getContent());
	}

	@Test
	public void testShowNewestFile() throws IOException {
		PowerMockito.when(fileService.getLatestFile(Mockito.anyString())).thenReturn(Optional.of(fileEntity));
		webHandler.showNewestFile();
		assertNotNull(webHandler.getContent());
	}

	@Test
	public void testInit() {
		PowerMockito.mockStatic(CDIUtils.class);
		PowerMockito.mockStatic(Roles.class);
		PowerMockito.mockStatic(SessionUtils.class);
		HttpSession session = Mockito.mock(HttpSession.class);
		PowerMockito.when(SessionUtils.getExistSession()).thenReturn(session);

		ArrayList<Role> roles = new ArrayList<>();
		roles.add(CLevelRole.CEO);
		PowerMockito.when(Roles.getRoles(Mockito.any())).thenReturn(roles);
		PowerMockito.when(fileService.getLatestFile(Mockito.anyString())).thenReturn(Optional.empty());

		PowerMockito.when(facesContextService.getResource(Mockito.anyString())).thenReturn(new byte[0]);
		webHandler.init();
		Mockito.verify(facesContextService).getResource("/resources/DefaultPage.pdf");
	}

	@Test
	public void test_attachTagToFile() {
		String tagName = "abc";
		Mockito.when(facesContextService.getParameter("name")).thenReturn(tagName);
		FileTagEntity fileTagEntity= FileTagEntity.builder()
												.build();
		
		TagEntity tagEntity = new TagEntity();
		Mockito.when(tagService.tryToSave(tagName)).thenReturn(tagEntity);
		Mockito.when(fileTagService.save(Mockito.any())).thenReturn(fileTagEntity);
		
		FileBOM currentFileBOM = FileBOM.builder()
											.id(1)
											.build();
		
		List<TagEntity> tags = Arrays
				.asList(new TagEntity(1, "abc", Collections.emptyList()),
						new TagEntity(2, "def", Collections.emptyList()),
						new TagEntity(3, "ghi", Collections.emptyList()));
						
		List<TagBOM> boms = Arrays.asList(new TagBOM(1, "abc"),
				new TagBOM(2,"def"), 
				new TagBOM(3, "ghi"));
		
		Mockito.when(tagService.listAllTags(1)).thenReturn(tags);
		Mockito.when(tagService.toBoms(tags)).thenReturn(boms);
		
		webHandler.setCurrentFileBOM(currentFileBOM);
		
		
		
		webHandler.attachTagToFile();
		
		List<String> tagNames = Arrays.asList("abc", "def", "ghi");
		assertEquals(tagNames, webHandler.getListCurrentTags());
		
		Mockito.verify(fileTagService).save(Mockito.any());
	}

	@Test
	public void test_detachTagFromFile() {
		String tagName = "abc";
		Mockito.when(facesContextService.getParameter("name")).thenReturn(tagName);
		
		Optional<FileTagEntity> fileTagEntityOptional = Optional.of(FileTagEntity.builder().build());
		FileBOM currentFileBOM = FileBOM.builder()
										.id(1)
										.build();
		webHandler.setCurrentFileBOM(currentFileBOM);
		Mockito.when(fileTagService.getFileTag(webHandler.getCurrentFileBOM().getId(), tagName))
				.thenReturn(fileTagEntityOptional);
		
		List<TagEntity> tags = Arrays
				.asList(new TagEntity(1, "abc", Collections.emptyList()),
						new TagEntity(2, "def", Collections.emptyList()),
						new TagEntity(3, "ghi", Collections.emptyList()));
		List<TagBOM> boms = Arrays.asList(new TagBOM(1, "abc"),
				new TagBOM(2,"def"), 
				new TagBOM(3, "ghi"));
		List<String> tagNames = Arrays.asList("abc", "def", "ghi");
		Mockito.when(tagService.listAllTags(webHandler.getCurrentFileBOM().getId()))
			.thenReturn(tags);
		Mockito.when(tagService.toBoms(tags)).thenReturn(boms);
		
		
		webHandler.detachTagFromFile();
		
		Mockito.verify(fileTagService).remove(fileTagEntityOptional.get().getId());
		assertEquals(tagNames, webHandler.getListCurrentTags());
	}

}
