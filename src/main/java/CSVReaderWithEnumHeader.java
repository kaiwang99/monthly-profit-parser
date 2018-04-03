import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

//import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

//Kai test enum as header
enum Header {
	 FullName, email, Phone,Countrys
}

public class CSVReaderWithEnumHeader {
    private static final String SAMPLE_CSV_FILE_PATH = "./src/main/resources/users-with-header.csv";
   // private static final String SAMPLE_CSV_FILE_PATH = "./src/main/resources/2017DecMonthlyTransaction.csv";

    public static void main(String[] args) throws IOException {
        try (
            Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
             //       .withHeader("name", "email", "phone", "country")
            			.withHeader(Header.class)
                    .withIgnoreHeaderCase()
                    .withTrim());
        ) {
        		boolean dataStarted = false; 
        		int recordCnt = 0;
            for (CSVRecord csvRecord : csvParser.getRecords()) {
            		// Accessing values by the names assigned to each column
            		
            		// skip commented lines
            		if (csvRecord.size() < 2) continue; 
            		
            		if (!dataStarted && csvRecord.get(Header.FullName).equalsIgnoreCase("name")) {
            			dataStarted = true; 
            			continue;
            		}
            		if(dataStarted) recordCnt++;
            		
                String email = csvRecord.get("Email");
                String phone = csvRecord.get("Phone");
                String country = csvRecord.get("Countrys");
                String name = csvRecord.get(Header.FullName);
                
                
                System.out.println("Record No - " + csvRecord.getRecordNumber());
                System.out.println("---------------");
                System.out.println("Name : " + name);
                System.out.println("Email : " + email);
                System.out.println("Phone : " + phone);
                System.out.println("Country : " + country);
                System.out.println("---------------\n\n");
            }
            System.out.println("\n\n total data record = " + recordCnt);
        }
    }
}