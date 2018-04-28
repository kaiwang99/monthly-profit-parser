import java.util.*;

/**
 * Kai:  Enumeration is old... it can't changet the underlying data.  
 * 		interator is new. 
 * @author kaiwang
 *
 */

public class HashTableDemo {
   public static void main(String args[]) {
      
      // create hash table 
      Hashtable htable1 = new Hashtable();      

      // put values in table
      htable1.put(1, "A");
      htable1.put(2, "B");
      htable1.put(3, "C");
      htable1.put(4, "D");

      // create enumeration for keys
      Enumeration en = htable1.keys();

      System.out.println("Display result:"); 

      // display search result
      while (en.hasMoreElements()) {
         System.out.println(en.nextElement());
      }
   }    
}
