package com.cabanasrd.data.entities;

import java.io.Serializable;

public class Rating implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int Id;
	private int GuestHouse;
	private String Device;
	private double Value;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getGuestHouse() {
		return GuestHouse;
	}
	public void setGuestHouse(int guestHouse) {
		GuestHouse = guestHouse;
	}
	public String getDevice() {
		return Device;
	}
	public void setDevice(String device) {
		Device = device;
	}
	public double getValue() {
		return Value;
	}
	public void setValue(double value) {
		Value = value;
	}
	

}
