package com.cabanasrd.data.entities;

import java.io.Serializable;

public class MotelService  implements  Serializable{
	 
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String service = null;
	private double price = 0; 
	private int type = 0;
	private int currencyType = 0;
	private String descriptionDetail = null;
	
	
	
	public MotelService(String service, double price, int type,
			int currencyType, String descriptionDetail) {
		super();
		this.service = service;
		this.price = price;
		this.type = type;
		this.currencyType = currencyType;
		this.descriptionDetail = descriptionDetail;
	}



	public int getType() {
		return type;
	}



	public void setType(int type) {
		this.type = type;
	}



	public int getCurrencyType() {
		return currencyType;
	}




	public void setCurrencyType(int currencyType) {
		this.currencyType = currencyType;
	}



	public String getDescriptionDetail() {
		return descriptionDetail;
	}



	public void setDescriptionDetail(String descriptionDetail) {
		this.descriptionDetail = descriptionDetail;
	}



	
			
	 

	    public MotelService(String service,double price) {
		super();
		this.service = service;
		this.price = price;
	}



	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}



	public String getService() {
		return service;
	}



	public void setService(String service) {
		this.service = service;
	}




	

}
