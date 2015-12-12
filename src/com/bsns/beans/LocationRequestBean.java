package com.bsns.beans;

public class LocationRequestBean {
	private int LrId;
	private String UserId;
	private String Latitude;
	private String longtitude;
	private String radius;
	private String AmPiId;
	private String PlPiId;
	private String RepeatTime;
	
	
	
	public String getRepeatTime() {
		return RepeatTime;
	}
	public void setRepeatTime(String repeatTime) {
		RepeatTime = repeatTime;
	}
	public String getRadius() {
		return radius;
	}
	public void setRadius(String radius) {
		this.radius = radius;
	}
	public int getLrId() {
		return LrId;
	}
	public void setLrId(int lrId) {
		LrId = lrId;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getLatitude() {
		return Latitude;
	}
	public void setLatitude(String latitude) {
		Latitude = latitude;
	}
	public String getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	public String getAmPiId() {
		return AmPiId;
	}
	public void setAmPiId(String amPiId) {
		AmPiId = amPiId;
	}
	public String getPlPiId() {
		return PlPiId;
	}
	public void setPlPiId(String plPiId) {
		PlPiId = plPiId;
	}
}
