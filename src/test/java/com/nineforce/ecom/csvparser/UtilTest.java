package com.nineforce.ecom.csvparser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Locale;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nineforce.ecom.csvparser.Util;

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

	// Test locType -> stdType
	@Test
	final void testGetAmznLocTypeStdTypeMap() {
		Locale l = new Locale("en", "US");
		HashMap<String, String> map = Util.getAmznLocTypeStdTypeMap(l);
		//System.out.println(map);		

		//can test every type by loop enum value getTypeName
		String locType = "FBA Inventory Fee";
		assertEquals(locType, map.get(locType) );

		// test ("en", "UK")
		l = new Locale("en", "UK");
		map = Util.getAmznLocTypeStdTypeMap(l);
		locType = "FBA Inventory Fee";
		assertEquals(locType, map.get(locType) );
	}
	
	// Test locType -> stdType DE
	@Test
	final void testGetAmznLocTypeStdTypeMapDE() {	
		Locale l = new Locale("de", "DE");
		HashMap<String, String> map = Util.getAmznLocTypeStdTypeMap(l);
		//System.out.println(map);
		String locType = "Versand durch Amazon Lagergebühr";
		assertEquals("FBA Inventory Fee", (String) map.get(locType) );
		locType = "Übertrag";
		assertEquals("Transfer", (String) map.get(locType) );
	}
	
	
	
	// Test  stdType -> LocType 
	@Test 
	void testGetAmznStdTypeLocTypeMap() {
		Locale l = new Locale("en", "US");
		HashMap<String, String> map = Util.getAmznStdTypeLocTypeMap(l);  //std->loc
System.out.println(map);
		String stdType = "FBA Inventory Fee";
		String locType = "FBA Inventory Fee";
		assertEquals(locType, map.get(stdType) );
		
		
		l = new Locale("de", "DE");
		map = Util.getAmznStdTypeLocTypeMap(l);  //std->loc
System.out.println(map);
		stdType = "Transfer";
		locType = "Übertrag";
		assertEquals(locType, (String) map.get(stdType) );
	}
	
	@Test
	void testGetCurrentRate() {
		Locale l = Util.US_LOCALE;
		assertEquals(Util.USDRMB_CURRENT, Util.getCurrentRate(l));
		
		l = Util.UK_LOCALE;
		assertEquals(Util.GBPRMB_CURRENT, Util.getCurrentRate(l));
		
		l = Util.DE_LOCALE;
		assertEquals(Util.EURMB_CURRENT, Util.getCurrentRate(l));		
	}

	@Test
	void testParseDouble() {
		String s = "111";
		assertEquals(111, Util.parseDouble(s));	
		
		s = "-111111.00";
		assertEquals(-111111, Util.parseDouble(s));	
		
		// failed when ,
		s = "-111,111.00";
		assertEquals(-111111.00, Util.parseDouble(s));	
	}

	
}
