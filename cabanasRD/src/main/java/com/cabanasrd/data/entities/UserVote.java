package com.cabanasrd.data.entities;

public class UserVote {
	private String Vote = null;
	private String MotelId = null;
	private String Device = null;
	public String getVote() {
		return Vote;
	}
	public void setVote(String vote) {
		Vote = vote;
	}
	public String getMotelId() {
		return MotelId;
	}
	public void setMotelId(String motelId) {
		MotelId = motelId;
	}
	public String getDevice() {
		return Device;
	}
	public void setDevice(String device) {
		Device = device;
	}
	public UserVote(String vote, String motelId, String device) {
		super();
		Vote = vote;
		MotelId = motelId;
		Device = device;
	}
	
	
}
