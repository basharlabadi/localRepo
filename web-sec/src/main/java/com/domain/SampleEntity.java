package com.domain;

import org.springframework.stereotype.Component;

@Component
public class SampleEntity {
	
	public SampleEntity(){
		
	}
	public SampleEntity(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
