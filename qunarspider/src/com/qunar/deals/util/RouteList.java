package com.qunar.deals.util;

public class RouteList {
	

	private String url;
	private String title;
	private String type;
	private String function;
	private String subject;
	private String otherInfo;
	
	public String getOtherInfo() {
		return otherInfo;
	}
	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("<route>");
		sb.append("<type>" + type + "</type>");
		sb.append("<function>" + function + "</function>");
		sb.append("<url>" + url + "</url>");
		sb.append("<title>" + title + "</title>");
		sb.append("<subject>" + subject + "</subject>");
		sb.append("<other>" + otherInfo + "</other>");
		sb.append("</route>");
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteList other = (RouteList) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
