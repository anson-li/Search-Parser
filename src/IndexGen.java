import com.sleepycat.db.*;
import java.io.*;
import java.util.regex.*;
import java.lang.*;

public class IndexGen{

public static void main(String[] args){

	BufferedReader br = null;

	try {
		//database configuration
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setType(DatabaseType.HASH);
		dbConfig.setAllowCreate(true);
		dbConfig.setSortedDuplicates(true); // setting flag for allowing duplicates
		
		//Create a database 
		Database std_db = new Database("rw.idx", null, dbConfig);
		OperationStatus oprStatus;

		//Inserting Data into a database
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();

		br = new BufferedReader(new FileReader("./data.txt"));
		String sCurrentLine;
		while ((sCurrentLine = br.readLine()) != null) {

			String id = sCurrentLine.split(",", 2)[0];
			String value = sCurrentLine.split(",", 2)[1];

			data.setData(value.getBytes());
			data.setSize(value.length()); 
			key.setData(id.getBytes()); 
			key.setSize(id.length());
			oprStatus = std_db.put(null, key, data);

		}

		// Closing the connection
    	std_db.close();

  	} // end of try 

  	catch (Exception ex) 
   		{ ex.getMessage();} 

	}
}
