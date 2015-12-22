package com.cabanasrd.data.entities;

import java.io.Serializable;

public class State implements  Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	public Long id = null;
	 private String name = null;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	 
	public State() {

	}
	public State(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	
}
