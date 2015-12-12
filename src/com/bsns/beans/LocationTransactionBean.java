package com.bsns.beans;

import android.os.Build;
import android.text.TextUtils;

public class LocationTransactionBean {
	private String UserId;
	private String Latitude;
	private String Longitude;
	private String DeviceName;
	private String UpdatedAt;
	
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

	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		Longitude = longitude;
	}

	public String getDeviceName() {
		return DeviceName;
	}

	public void setDeviceName() {
		this.DeviceName = getDeviceNameFromSystem();
	}
	
	public void setDeviceName(String DevName) {
		this.DeviceName = DevName;
	}

	public String getUpdatedAt() {
		return UpdatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		UpdatedAt = updatedAt;
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
