package com.bsns.beans;

public class CountryBean {
	private String CountryId;
	private String CountryName;
	private String CountryCode;
	private String CountryDescription;
	private String IsDelete;
	public String getCountryId() {
		return CountryId;
	}
	public void setCountryId(String countryId) {
		CountryId = countryId;
	}
	public String getCountryName() {
		return CountryName;
	}
	public void setCountryName(String countryName) {
		CountryName = countryName;
	}
	public String getCountryCode() {
		return CountryCode;
	}
	public void setCountryCode(String countryCode) {
		CountryCode = countryCode;
	}
	public String getCountryDescription() {
		return CountryDescription;
	}
	public void setCountryDescription(String countryDescription) {
		CountryDescription = countryDescription;
	}
	public String getIsDelete() {
		return IsDelete;
	}
	public void setIsDelete(String isDelete) {
		IsDelete = isDelete;
	}
}
