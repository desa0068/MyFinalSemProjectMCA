package com.bsns.beans;

public class GroupMasterBean {
	private String GroupId;
	private String GroupName; 
	private String UserId;
	private String CreationDate;
	private String IsDelete;
	private String DPUrl;
	public String getGroupId() {
		return GroupId;
	}
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}
	public String getGroupName() {
		return GroupName;
	}
	public void setGroupName(String groupName) {
		GroupName = groupName;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getCreationDate() {
		return CreationDate;
	}
	public void setCreationDate(String creationDate) {
		CreationDate = creationDate;
	}
	public String getIsDelete() {
		return IsDelete;
	}
	public void setIsDelete(String isDelete) {
		IsDelete = isDelete;
	}
	public String getDPUrl() {
		return DPUrl;
	}
	public void setDPUrl(String dPUrl) {
		DPUrl = dPUrl;
	}
}
