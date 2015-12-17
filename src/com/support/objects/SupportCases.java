package com.support.objects;

import java.io.Serializable;



public class SupportCases implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3613343224583678170L;
	private String CaseNumber;
	private String Issue;
	private String Severity;
	private String status;
	private String DateCreated;
	private String Client;
	private String ClientContact;
	private String Assigned;
	private String Replied;
	private Boolean Attachment;
	private String phone;
	private boolean Notes;

	public boolean hasNotes() {
		return Notes;
	}

	public void setNotes(boolean notes) {
		Notes = notes;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}



	public Boolean getAttachment() {
		return Attachment;
	}
	public void setAttachment(Boolean attachment) {
		Attachment = attachment;
	}
	public String getReplied() {
		return Replied;
	}
	public void setReplied(String replied) {
		Replied = replied;
	}
	public String getCaseNumber() {
		return CaseNumber;
	}
	public void setCaseNumber(String caseNumber) {
		CaseNumber = caseNumber;
	}
	public String getIssue() {
		return Issue;
	}
	public void setIssue(String issue) {
		Issue = issue;
	}
	public String getSeverity() {
		return Severity;
	}
	public void setSeverity(String severity) {
		Severity = severity;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDateCreated() {
		return DateCreated;
	}
	public void setDateCreated(String dateCreated) {
		DateCreated = dateCreated;
	}
	public String getClient() {
		return Client;
	}
	public void setClient(String client) {
		Client = client;
	}
	public String getClientContact() {
		return ClientContact;
	}
	public void setClientContact(String clientContact) {
		ClientContact = clientContact;
	}
	public String getAssigned() {
		return Assigned;
	}
	public void setAssigned(String assigned) {
		Assigned = assigned;
	}

	
}

