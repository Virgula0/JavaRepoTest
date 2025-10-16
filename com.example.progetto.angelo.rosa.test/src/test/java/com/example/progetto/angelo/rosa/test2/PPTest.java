package com.example.progetto.angelo.rosa.test2;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PPTest {

	@Test
	public void test() {
		PP t = new PP();
		assertThat(t.test()).isEqualTo(1);
	}

}
