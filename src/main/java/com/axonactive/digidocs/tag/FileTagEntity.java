package com.axonactive.digidocs.tag;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.axonactive.digidocs.email.FileEntity;
import com.axonactive.digidocs.persistence.IEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor 
@Entity
@Table(name="filetag")
@Builder(toBuilder = true)
public class FileTagEntity implements IEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="fileId")
	private FileEntity fileEntity;
	
	@ManyToOne
	@JoinColumn(name="tagId")
	private TagEntity tagEntity;
	
	
}
