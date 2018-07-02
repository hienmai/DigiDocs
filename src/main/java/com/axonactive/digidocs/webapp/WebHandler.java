package com.axonactive.digidocs.webapp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.axonactive.digidocs.email.FileBOM;
import com.axonactive.digidocs.email.FileCryptionStorageService;
import com.axonactive.digidocs.email.FileEntity;
import com.axonactive.digidocs.email.FileHandler;
import com.axonactive.digidocs.email.FileService;
import com.axonactive.digidocs.encryption.SymmetricEncryptionFactory;
import com.axonactive.digidocs.login.Roles;
import com.axonactive.digidocs.login.User;
import com.axonactive.digidocs.tag.FileTagEntity;
import com.axonactive.digidocs.tag.FileTagService;
import com.axonactive.digidocs.tag.ResultTag;
import com.axonactive.digidocs.tag.TagBOM;
import com.axonactive.digidocs.tag.TagEntity;
import com.axonactive.digidocs.tag.TagService;
import com.axonactive.digidocs.utils.FacesContextService;
import com.axonactive.digidocs.utils.FacesContextUtils;
import com.axonactive.digidocs.utils.FormatUtils;
import com.axonactive.digidocs.utils.SessionUtils;

import lombok.Getter;
import lombok.Setter;

@ManagedBean
@SessionScoped
@Getter

public class WebHandler implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(FileHandler.class);
	private List<String> folderList = new ArrayList<>();
	private static final String CONTENT_TYPE = "application/pdf";
	private FileBOM currentFileBOM = FileBOM.builder().build();
	private String currentRole;
	private StreamedContent content;
	
	@Setter
	private FileEntity editingFileEntity;

	@Inject
	private FileCryptionStorageService fileCryptionStorageService;

	@Inject
	private FileService fileService;

	@Inject
	private FacesContextService facesContextService;
	
	@Inject
	private FileTagService fileTagService;
	
	@Inject
	private TagService tagService;

	private List<String> listCurrentTags;

	@Getter
	private Map<Character, List<ResultTag>> resultTagMap = new HashMap<>();

	@PostConstruct
	public void init() {
		try {
			User user = (User) SessionUtils.getExistSession().getAttribute("user");
			this.currentRole = Roles.getRoles(user).get(0).getName();
			folderList = fileService.getListYears(currentRole);
			showNewestFile();
		} catch (Exception e) {
			LOGGER.error("PostConstruct in WebHandler failed", e);
		}
	}
	
	/**
	 * Show the latest file
	 */
	public void showNewestFile() {
		Optional<FileEntity> fileEntity = fileService.getLatestFile(currentRole); // latest doc handle here
		if (!fileEntity.isPresent()) {
			setDefaultPage();
			return;
		}
		showSpecificPdf(fileEntity.get());
	}
	
	/**
	 * Show the file when click the link in the notification email
	 * @param event
	 */
	public void showFileById(ComponentSystemEvent event) {
		String idFile = FacesContextUtils.getAttribute(event, "idFile").orElseThrow(() -> new IllegalStateException("idFile must not be empty"));
		if (FacesContextUtils.isPostback() || StringUtils.isEmpty(idFile)) {
			return;
		}
		
		int id;
		try {
			id = Integer.parseInt(new String(FormatUtils.decodeFromBase64(idFile)));
		} catch (NumberFormatException e) {
			setDefaultPage();
			return;
		}
		FileEntity fileEntity = fileService.findById(id);
		
		boolean isDifferentRole = Optional.ofNullable(fileEntity)
				.map(FileEntity::getRole)
				.filter(role -> !role.equalsIgnoreCase(currentRole))
				.isPresent();
		
		if (isDifferentRole) {
			setDefaultPage();
			return;
		}
		showSpecificPdf(fileEntity);
	}
	
	/**
	 * Update state document from unread to read
	 */
	public void updateStateDocument() {
		String idDocument = facesContextService.getParameter("id");
		FileEntity fileEntity = fileService.findById(Integer.valueOf(idDocument));
		fileService.update(fileEntity.toBuilder().read(true).build());
	}
	
	/**
	 * Show the specific document
	 * @param fileEntity
	 */
	public void showSpecificPdf(FileEntity fileEntity) {
		Objects.requireNonNull(fileEntity, "FileEntity must not be empty!");
		Optional<FileBOM> tempBOM = this.fileService.toBom(fileEntity);
		
		if (!tempBOM.isPresent()) {
			setDefaultPage();
			return;
		}
		currentFileBOM = tempBOM.get();
		updateCurrentTagNames();
		byte[] dataFile = fileCryptionStorageService.readFile(currentFileBOM.getPath(), SymmetricEncryptionFactory
				.getAES256EncryptionFromBase64(currentFileBOM.getKeyBase64(), currentFileBOM.getIvBase64()));
		if (dataFile.length == 0) {
			setDefaultPage();
			return;
		}
		setContent(new DefaultStreamedContent(new ByteArrayInputStream(dataFile), getContenttype()));
	}

	public List<FileEntity> readFileInYearFolder(Integer year, String role) {
		return fileService.getFilesInYear(year, role);
	}

	public String branchName(String fileName) {
		return currentFileBOM.getBranchSender();
	} 
	
	public void readSpecificPdfFile(Integer id) {
		FileEntity fileEntity = fileService.findById(id);
		if (fileEntity == null) {
			setDefaultPage();
			return;
		}
		showSpecificPdf(fileEntity);
	}
	
	/**
	 * Update file filename when rename it on the web app
	 */
	public void updateFileName() {
		fileService.update(editingFileEntity);
		if (editingFileEntity.getId() == currentFileBOM.getId()) {
			currentFileBOM = currentFileBOM.toBuilder().name(editingFileEntity.getName()).build();
		}
	}

	public void getFileEntityFromItem(FileEntity fileEntity) {
		this.editingFileEntity = fileEntity;
	}
	
	/**
	 * Set the default page when the C-level haven't had any document yet
	 */
	public void setDefaultPage() {
		currentFileBOM = currentFileBOM.toBuilder().id(null).name(null).build();
		Optional.ofNullable(facesContextService)
				.map(fcs -> fcs.getResource("/resources/DefaultPage.pdf"))
				.map(ByteArrayInputStream::new)
				.map(bais -> new DefaultStreamedContent(bais, CONTENT_TYPE))
				.ifPresent(this::setContent);
//				
//		setContent(new DefaultStreamedContent(
//				new ByteArrayInputStream(facesContextService.getResource("/resources/DefaultPage.pdf")), CONTENT_TYPE));

	}

	public void setUserRole(String currentRole) {
		this.currentRole = currentRole;
	}

	public List<String> getFolderList() {
		folderList = fileService.getListYears(currentRole);
		return folderList;
	}

	public void attachTagToFile() {
		String tagName = facesContextService.getParameter("name");
		if (this.currentFileBOM.getId() == null || StringUtils.isEmpty(tagName)) {
			return;
		}
		TagEntity tagEntity = tagService.tryToSave(tagName);
		FileEntity fileEntity = FileEntity.builder().id(this.currentFileBOM.getId()).build();
		FileTagEntity fileTagEntity = new FileTagEntity(null, fileEntity, tagEntity);
		fileTagService.save(fileTagEntity);
		updateCurrentTagNames();
	}
	
	public void detachTagFromFile() {
		String tagName = facesContextService.getParameter("name");
		if (this.currentFileBOM.getId() == null || StringUtils.isEmpty(tagName)) {
			return;
		}
		Optional<FileTagEntity> fileTagEntityOptional = fileTagService.getFileTag(this.currentFileBOM.getId(), tagName);
		if (!fileTagEntityOptional.isPresent()) {
			LOGGER.error("This file doesn't have this tagName");
			return;
		}
		fileTagService.remove(fileTagEntityOptional.get().getId());
		updateCurrentTagNames();
	}
	
	public void updateCurrentTagNames() {
		List<TagEntity> tagList = tagService.listAllTags(this.currentFileBOM.getId());
		listCurrentTags = tagService.toBoms(tagList)
				.stream()
				.map(TagBOM::getName)
				.collect(Collectors.toList());
	}


	private Map<Character, List<ResultTag>> getAllTagsByRole() {
		return tagService.getAllTagsByRole(this.currentRole);
	}
	
	public void setFolderList(List<String> folderList) {
		this.folderList = folderList;
	}

	public String getCurrentRole() {
		return currentRole;
	}

	public StreamedContent getContent() throws IOException {
		if (this.content == null) {
			showNewestFile();
			return this.content;
		}
		if (this.content.getStream().available() == 0) {
			this.content.getStream().reset();
		}
		return content;
	}

	public void setContent(StreamedContent content) {
		this.content = content;
	}

	public static String getContenttype() {
		return CONTENT_TYPE;
	}

	public FileBOM getCurrentFileBOM() {
		return currentFileBOM;
	}

	public void setCurrentFileBOM(FileBOM currentFileBOM) {
		this.currentFileBOM = currentFileBOM;
	}
	
	public List<String> getListCurrentTags() {
		return listCurrentTags;
	}

	public void setListCurrentTags(List<String> listCurrentTags) {
		//don't set
	}
}