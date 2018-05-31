import static org.junit.Assert.assertEquals;  
import org.junit.After;  
import org.junit.AfterClass;  
import org.junit.Before;  
import org.junit.BeforeClass;  
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  


public class TestCase2 {  
	public static Logger logger;
	
    @BeforeClass  
    public static void setUpBeforeClass() throws Exception {  
        logger = (Logger) LoggerFactory.getLogger(TestCase2.class);        
        System.out.println("before class");  
    }  
    @Before  
    public void setUp() throws Exception {  
        System.out.println("before");  
    }  
  
    @Test  
    public void testFindMax(){  
        System.out.println("test case find max");  
        assertEquals(4,Calculation.findMax(new int[]{1,3,4,2}));  
        assertEquals(-2,Calculation.findMax(new int[]{-12,-3,-4,-2}));  
        logger.debug("test case find max"); 
    }  

    @Test
    public void testCube(){  
    	System.out.println("test case cube");  
        assertEquals(27,Calculation.cube(3));  
        logger.debug("test case cube"); 
    }  
    @Test  
    public void testReverseWord(){  
        System.out.println("test case reverse word");  
        assertEquals("ym eman si nahk",Calculation.reverseWord("my name is khan"));  
        logger.debug("test case reverse word");  
    }  
    @After  
    public void tearDown() throws Exception {  
        System.out.println("after");  
    }  
  
    @AfterClass  
    public static void tearDownAfterClass() throws Exception {  
        System.out.println("after class");  
    }  
  
}  