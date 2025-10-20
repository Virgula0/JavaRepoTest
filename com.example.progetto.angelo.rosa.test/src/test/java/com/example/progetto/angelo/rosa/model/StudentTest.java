package com.example.progetto.angelo.rosa.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class StudentTest {

	@Test
	public void testEqualRef() {
		Student s = new Student("1", "1");
		assertThat(s).isEqualTo(s);
		assertThat(s).isNotEqualTo(new Student("2","2"));
	}

}
