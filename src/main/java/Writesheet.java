import java.io.File;
import java.io.FileOutputStream;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;



public class Writesheet {
	static final int ROW_START = 16;
	

	public static void main(String[] args) throws Exception {

      //Create blank workbook
     XSSFWorkbook workbook = new XSSFWorkbook(); 
	   
	//   HSSFWorkbook workbook = new HSSFWorkbook();

  
      //Create a blank sheet
      XSSFSheet spreadsheet = workbook.createSheet(" Employee Info ");

      //Create row object
      XSSFRow row, frontRow[];

      // save front rows for summary after parsing
      frontRow = new XSSFRow[ROW_START];
      for (int i=0; i<frontRow.length; i++) 
    	  		frontRow[i] = spreadsheet.createRow(i);
      
      //This data needs to be written (Object[])
      Map < String, Object[] > empinfo = 
      new TreeMap < String, Object[] >();
      empinfo.put( "1", new Object[] { "EMP ID", "EMP NAME", "DESIGNATION" });
      empinfo.put( "2", new Object[] { "tp01", "Gopal", "Technical Manager" });
      empinfo.put( "3", new Object[] { "tp02", "Manisha", "Proof Reader" });
      empinfo.put( "4", new Object[] { "tp03", "Masthan", "Technical Writer" });
      empinfo.put( "5", new Object[] { "tp04", "Satish", "Technical Writer" });
      empinfo.put( "6", new Object[] { "tp05", "Krishna", "Technical Writer" });
      
      //Iterate over data and write to sheet
      Set < String > keyid = empinfo.keySet();
      int rowid = ROW_START;

      for (String key : keyid) {
          row = spreadsheet.createRow(rowid++);
         Object [] objectArr = empinfo.get(key);
         int cellid = 0;

         for (Object obj : objectArr) {
            Cell cell = row.createCell(cellid++);
            cell.setCellValue((String)obj);
         }
      }
      
      //Write back front row
      for (int i=0; i<frontRow.length; i++) {
    	  	row = frontRow[i];
    	  	for (int j=0; j<=i; j++) {
    	  		Cell cell=row.createCell(j);
    	  		cell.setCellValue(j);
    	  	}
      }
      
      
      //Write the workbook in file system
      FileOutputStream out = new FileOutputStream(new File("Writesheet.xlsx"));
      workbook.write(out);
      out.close();
      System.out.println("Writesheet.xlsx written successfully");
   }
}
