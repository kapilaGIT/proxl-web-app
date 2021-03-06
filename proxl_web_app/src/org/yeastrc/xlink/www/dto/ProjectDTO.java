package org.yeastrc.xlink.www.dto;

import java.util.Date;

/**
 * table project
 *
 */
public class ProjectDTO {

	private int id;
	private int authShareableObjectId;
	
	private String title;
	private String abstractText;
	
	private boolean enabled;
	private boolean markedForDeletion;
	private Date markedForDeletionTimstamp;
	private Integer markedForDeletionAuthUserId;
	
	private boolean projectLocked;
	
	private Integer publicAccessLevel;
	private boolean publicAccessLocked;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectDTO other = (ProjectDTO) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAuthShareableObjectId() {
		return authShareableObjectId;
	}
	public void setAuthShareableObjectId(int authShareableObjectId) {
		this.authShareableObjectId = authShareableObjectId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAbstractText() {
		return abstractText;
	}
	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}
	
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isMarkedForDeletion() {
		return markedForDeletion;
	}
	public void setMarkedForDeletion(boolean markedForDeletion) {
		this.markedForDeletion = markedForDeletion;
	}
	

	
	public boolean isProjectLocked() {
		return projectLocked;
	}
	public void setProjectLocked(boolean projectLocked) {
		this.projectLocked = projectLocked;
	}
	public Integer getPublicAccessLevel() {
		return publicAccessLevel;
	}
	public void setPublicAccessLevel(Integer publicAccessLevel) {
		this.publicAccessLevel = publicAccessLevel;
	}
	public boolean isPublicAccessLocked() {
		return publicAccessLocked;
	}
	public void setPublicAccessLocked(boolean publicAccessLocked) {
		this.publicAccessLocked = publicAccessLocked;
	}
	public Date getMarkedForDeletionTimstamp() {
		return markedForDeletionTimstamp;
	}
	public void setMarkedForDeletionTimstamp(Date markedForDeletionTimstamp) {
		this.markedForDeletionTimstamp = markedForDeletionTimstamp;
	}
	public Integer getMarkedForDeletionAuthUserId() {
		return markedForDeletionAuthUserId;
	}
	public void setMarkedForDeletionAuthUserId(Integer markedForDeletionAuthUserId) {
		this.markedForDeletionAuthUserId = markedForDeletionAuthUserId;
	}
	
}
