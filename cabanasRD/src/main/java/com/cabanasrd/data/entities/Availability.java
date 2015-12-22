package com.cabanasrd.data.entities;

import java.io.Serializable;

public class Availability implements Comparable<Availability>,  Serializable{
	
	private static final long serialVersionUID = 1L;
	private String  LastVoteTime = null;
	private int Count= 0;
	private String Vote = null;

	
	public int getVoteType() {
		int voteType = 0;
		if(Vote.equals("Empty")){
			voteType = 0;
		}
		if(Vote.equals("Medium")){
			voteType = 1;
		}
		if(Vote.equals("Full")){
			voteType = 2;
		}
		return voteType;
	}



	public String getLastDateUserVote() {
		return LastVoteTime;
	}



	public int getCountVote() {
		return Count;
	}



	@Override
	public int compareTo(Availability another) {
		return Double.compare(another.getCountVote(), Count)  ;
	}

	

	
	
	
}


