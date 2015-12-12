package com.bsns.beans;

public class GroupTransactionBean {
	private String GtId;
	private String GroupId;
	private String UserId;
	private String IsDelete;
	private String UpdationDate;

	
	public String getUpdationDate() {
		return UpdationDate;
	}
	public void setUpdationDate(String updationDate) {
		UpdationDate = updationDate;
	}
	public String getGtId() {
		return GtId;
	}
	public void setGtId(String gtId) {
		GtId = gtId;
	}
	public String getGroupId() {
		return GroupId;
	}
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getIsDelete() {
		return IsDelete;
	}
	public void setIsDelete(String isDelete) {
		IsDelete = isDelete;
	}
}
