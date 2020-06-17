package net.restfulapi.app.rest.domain.common;

public enum Status {
	ACTIVE("active"), INACTIVE("inactive"), UNKNOWN("Unknown");
	
	private String value;
	private Status(String value) {
	      this.value = value;
	   }

	public String getValue() {
		return value;
	}
}
