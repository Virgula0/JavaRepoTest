package com.example.progetto.angelo.rosa.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 * to run succesfully classes in maven build and let maven to see this class as test
 * the class name must end with the name Test
 */
public class AppTest {
	private App app;
	
	@Before
	public void setup() {
		app = new App(); 
	}
	
	@Test
	public void testSayHello() {
		assertEquals("Hello", app.sayHello());
	}
	
	@Test
	public void testSayHello2() {
		assertEquals("", app.sayHello2());
	}
}
