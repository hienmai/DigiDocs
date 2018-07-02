package com.axonactive.digidocs.email;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.axonactive.digidocs.persistence.IEntity;
import com.axonactive.digidocs.tag.FileTagEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "file")
public class FileEntity implements IEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "role", nullable = false)
	private String role;

	@Column(name = "branch_sender", nullable = false)
	private String branchSender;

	@Column(name = "path", nullable = false)
	private String path;

	@Column(name = "name", nullable = false)
	@Setter
	private String name;

	@Column(name = "file_size", nullable = false)
	private long fileSize;

	@Column(name = "date", nullable = false)
	private Date date;

	@Column(name = "year", nullable = false)
	private Integer year;

	@Column(name = "have_read", nullable = false)
	private boolean read = false;

	@Column(name = "encrypt_key", nullable = false)
	private String key;

	@Column(name = "encrypt_initialization_vector", nullable = false)
	private String iv;

	@OneToMany(mappedBy = "fileEntity")
	private List<FileTagEntity> filesTagsEntities;

}
