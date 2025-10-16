package com.example.progetto.angelo.rosa.test2;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class AppTest2 {

	@Test
	public void test() {
		PP t = new PP();
		assertThat(t.test()).isEqualTo(1);
	}

}
