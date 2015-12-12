package com.bsns.beans;

public class UserListTransactionBean {
	private String UserListId;
	private String UserId1;
	private String UserId2;
	private String SenderRelId;
	private String ReceiverRelId;
	private String IsSharingLocation;
	private String IsAccept;
	private String IsDelete;
	private String CreationDate;
	
	
	
	public String getSenderRelId() {
		return SenderRelId;
	}
	public void setSenderRelId(String senderRelId) {
		SenderRelId = senderRelId;
	}
	public String getReceiverRelId() {
		return ReceiverRelId;
	}
	public void setReceiverRelId(String receiverRelId) {
		ReceiverRelId = receiverRelId;
	}
	public String getUserListId() {
		return UserListId;
	}
	public void setUserListId(String userListId) {
		UserListId = userListId;
	}
	public String getUserId1() {
		return UserId1;
	}
	public void setUserId1(String userId1) {
		UserId1 = userId1;
	}
	public String getUserId2() {
		return UserId2;
	}
	public void setUserId2(String userId2) {
		UserId2 = userId2;
	}
	public String getIsSharingLocation() {
		return IsSharingLocation;
	}
	public void setIsSharingLocation(String isSharingLocation) {
		IsSharingLocation = isSharingLocation;
	}
	public String getIsAccept() {
		return IsAccept;
	}
	public void setIsAccept(String isAccept) {
		IsAccept = isAccept;
	}
	public String getIsDelete() {
		return IsDelete;
	}
	public void setIsDelete(String isDelete) {
		IsDelete = isDelete;
	}
	public String getCreationDate() {
		return CreationDate;
	}
	public void setCreationDate(String creationDate) {
		CreationDate = creationDate;
	}
	
}
