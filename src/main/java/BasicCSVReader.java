import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.swing.text.html.HTMLDocument.Iterator;

//kai test git
public class BasicCSVReader {
    private static final String SAMPLE_CSV_FILE_PATH = "FebTxn/2018AprMonthlyTransaction-amazon-tqs-de.csv";

    public static void main(String[] args) throws IOException {
    	
        Logger logger = (Logger) LoggerFactory.getLogger(BasicCSVReader.class);
        
        try {
        		
        		//File resourcesDirectory = new File("src/test/resources");
        		
            //Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
        		
        		/*
        		Reader reader = new InputStreamReader(new FileInputStream(SAMPLE_CSV_FILE_PATH), "UTF-8");
        		
            CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT);
            */
        		
        		File inputFile = new File(SAMPLE_CSV_FILE_PATH);
        		CSVParser csvParser = CSVParser.parse(inputFile, Charset.forName("utf-8"), 
        				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
      
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
            		for (int j=0; j<csvRecord.size(); j++) {
            			System.out.print(csvRecord.get(j) + ", ");
            			logger.debug(csvRecord.get(j) + ", ");
            		}
            		System.out.println();
            		
            		/*
                String name = csvRecord.get(0);
                String email = csvRecord.get(1);
                String phone = csvRecord.get(2);
                String country = csvRecord.get(3);

                System.out.println("Record No - " + csvRecord.getRecordNumber());
                System.out.println("---------------");
                System.out.println("Name : " + name);
                System.out.println("Email : " + email);
                System.out.println("Phone : " + phone);
                System.out.println("Country : " + country);
                System.out.println("---------------\n\n");
                */
            
            }
        } catch (IOException e) {
        	
        }
    }
}