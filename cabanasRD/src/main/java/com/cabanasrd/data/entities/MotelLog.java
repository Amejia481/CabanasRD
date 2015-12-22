package com.cabanasrd.data.entities;

public class MotelLog {
	private int GuestHouse;
	private String  LogDate;
	private String device;
	public int getGuestHouse() {
		return GuestHouse;
	}
	public void setGuestHouse(int guestHouse) {
		GuestHouse = guestHouse;
	}
	public String getLogDate() {
		return LogDate;
	}
	public void setLogDate(String logDate) {
		LogDate = logDate;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public MotelLog(int guestHouse, String logDate, String device) {
		super();
		GuestHouse = guestHouse;
		LogDate = logDate;
		this.device = device;
	}
	
	
	public MotelLog() {
	}
}
