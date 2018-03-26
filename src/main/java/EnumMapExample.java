import java.util.EnumMap;
import java.util.Iterator;

/**
 * Java program to demonstrate How to use EnumMap in Java
 * If Key Object is Enum than it’s best to EnumMap to get better performance.
 * Most of IDE like Netbeans and Eclipse suggest you to use EnumMap instead of HashMap
 * or any other Map implementation when key object is Enum.
 *
 * @author Javarevisited
 */

public class EnumMapExample{
  
    public enum STATE{
        NEW, RUNNING, WAITING, FINISHED;
    }

    public static void main(String args[]) {
      
        // Java EnumMap Example 1: creating EnumMap in java with key as enum type STATE
        EnumMap<STATE, String> stateMap = new EnumMap<STATE, String>(STATE.class);
      
        // Java EnumMap Example 2:
        //putting values inside EnumMap in Java
        //we are inserting Enum keys on different order than their natural order
        stateMap.put(STATE.RUNNING, "Program is running");
        stateMap.put(STATE.WAITING, "Program is waiting");
        stateMap.put(STATE.NEW, "Program has just created");
        stateMap.put(STATE.FINISHED, "Program has finished");
      
        // Java EnumMap Example 3:
        //printing size of EnumMap in java
        System.out.println("Size of EnumMap in java: " + stateMap.size());
      
        // Java EnumMap Example 5:
        //printing Java EnumMap , should print EnumMap in natural order 
        //of enum keys (order on which they are declared)
        System.out.println("EnumMap: " + stateMap);
      
        // Java EnumMap Example 5:
        //retrieving value from EnumMap in java
        System.out.println("EnumMap key : " + STATE.NEW +" value: " + stateMap.get(STATE.NEW));
      
        // Java EnumMap Example 6:
        //Iterating over Java EnumMap
        Iterator<STATE> enumKeySet = stateMap.keySet().iterator();
        while(enumKeySet.hasNext()){
            STATE currentState = enumKeySet.next();
            System.out.println("key : " + currentState + " value : " + stateMap.get(currentState));
        }
      
        //Java EnumMap Example 7: checking if EnumMap contains a particular key
        System.out.println("Does stateMap has :" + STATE.NEW + " : " 
                            +  stateMap.containsKey(STATE.NEW));
      
        //Java EnumMap Example 8: checking if EnumMap contains a particular value
        System.out.println("Does stateMap has :" + STATE.NEW + " : " + stateMap.containsValue(null));

    }
  
}


// Read more: http://javarevisited.blogspot.com/2012/09/what-is-enummap-in-java-example-tutorial.html#ixzz5A8Y5nHW0