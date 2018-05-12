package com.nineforce.ecom.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Locale;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UtilTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	final void testGetAmznTypeMapByLocale() {
		Locale l = new Locale("en", "US");
		HashMap<String, String> map = Util.getAmznTypeMapByLocale(l);
		//System.out.println(map);		

		//can test every type by loop enum value getTypeName
		String locType = "FBA Inventory Fee";
		assertEquals(locType, map.get(locType) );

		// test ("en", "UK")
		l = new Locale("en", "UK");
		map = Util.getAmznTypeMapByLocale(l);
		locType = "FBA Inventory Fee";
		assertEquals(locType, map.get(locType) );
	}
	
	@Test
	final void testGetAmznTypeMapByLocaleDE() {	
		Locale l = new Locale("de", "DE");
		HashMap<String, String> map = Util.getAmznTypeMapByLocale(l);
		System.out.println(map);
		String locType = "Versand durch Amazon Lagergebühr";
		assertEquals("FBA Inventory Fee", (String) map.get(locType) );
		
		locType = "Übertrag";
		assertEquals("Transfer", (String) map.get(locType) );
	}

}
