package com.example.progetto.angelo.rosa.model;

import java.util.Objects;

public class Student {
	private String id;
	private String name;
	
	public Student(String id, String name) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Student))
			return false;
		Student other = (Student) obj;
		return Objects.equals(id, other.id) && Objects.equals(name, other.name);
	}
}
