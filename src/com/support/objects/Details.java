package com.support.objects;

public class Details {
	
	private int userID;
	private int supportUserID;
	private int responseID;
	private String dateCreated;
	private String name;
	private String response;
	private String subject;
	public static String phone;
	private String clientPhone;

	public String getClientPhone() {
		return clientPhone;
	}

	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}

	public static String getPhone() {

		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public int getSupportUserID() {
		return supportUserID;
	}
	public void setSupportUserID(int supportUserID) {
		this.supportUserID = supportUserID;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public int getResponseID() {
		return responseID;
	}
	public void setResponseID(int responseID) {
		this.responseID = responseID;
	}
}
