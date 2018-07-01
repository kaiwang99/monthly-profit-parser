package com.nineforce.ecom.csvparser;



import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


	
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)

@Suite.SuiteClasses({
   AmznCsvHeaderEnumTest.class,
   AmznCSVTxnParser.class, 
   COGSTest.class, 
   CSVMonthlyTxnTest.class, 
   NFAccountEnumTest.class, 
   PaypalCSVTxnParserTest.class, 
   UtilTest.class
})

public class AllTest {   
}  	
