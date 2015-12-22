package com.cabanasrd.data.entities;

import java.io.Serializable;

public class Image implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url = null;
	private String description = null;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
