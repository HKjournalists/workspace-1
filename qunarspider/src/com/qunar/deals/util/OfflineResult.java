package com.qunar.deals.util;

public class OfflineResult {

	private boolean offline;
	
	private String reason;

	private String url;
	
	private int id;
	
	private String wrapperId;
	
	private String wrapperName;
	
	
	
	
	public String getWrapperId() {
		return wrapperId;
	}

	public void setWrapperId(String wrapperId) {
		this.wrapperId = wrapperId;
	}

	public String getWrapperName() {
		return wrapperName;
	}

	public void setWrapperName(String wrapperName) {
		this.wrapperName = wrapperName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "OfflineResult [id=" + id + ", offline=" + offline + ", reason="
				+ reason + ", url=" + url + "]";
	}


	
	
	
	
}
