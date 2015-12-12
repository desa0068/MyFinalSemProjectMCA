package com.bsns.beans;

import android.os.Build;
import android.text.TextUtils;

public class MylocationTransactionBean {
	private int LocId;
	private String UserId;
	private String latitude;
	private String longtitude;
	private String DeviceName;
	private String InsertedAt;
	
	
	public String getDeviceName() {
		return DeviceName;
	}
	public void setDeviceName() {
		DeviceName = getDeviceNameFromSystem();
	}
	public void setDeviceName(String DeviceName) {
		this.DeviceName = DeviceName;
	}
	public int getLocId() {
		return LocId;
	}
	public void setLocId(int locId) {
		LocId = locId;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	public String getInsertedAt() {
		return InsertedAt;
	}
	public void setInsertedAt(String insertedAt) {
		InsertedAt = insertedAt;
	}
	
	public String getDeviceNameFromSystem()
    {
	    final String manufacturer = Build.MANUFACTURER;
	    final String model = Build.MODEL;
	    if (model.startsWith(manufacturer)) {
	        return capitalize(model);
	    }
	    if (manufacturer.equalsIgnoreCase("HTC")) {
	        // make sure "HTC" is fully capitalized.
	        return "HTC " + model;
	    }
	    return capitalize(manufacturer) + " " + model;
	}

	private String capitalize(String str) {
	    if (TextUtils.isEmpty(str)) {
	        return str;
	    }
	    final char[] arr = str.toCharArray();
	    boolean capitalizeNext = true;
	    String phrase = "";
	    for (final char c : arr) {
	        if (capitalizeNext && Character.isLetter(c)) {
	            phrase += Character.toUpperCase(c);
	            capitalizeNext = false;
	            continue;
	        } else if (Character.isWhitespace(c)) {
	            capitalizeNext = true;
	        }
	        phrase += c;
	    }
	    return phrase;
	}
}
