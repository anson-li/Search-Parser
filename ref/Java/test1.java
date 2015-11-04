import com.sleepycat.db.*;

public class test1{

public static void main(String[] args){
	try { 

		//database configuration
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setType(DatabaseType.BTREE);
		dbConfig.setAllowCreate(true);   

		//Create a database 
		Database std_db = new Database("students.db", null, dbConfig);
		OperationStatus oprStatus;

		//Inserting Data into a database
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		
		//Other variables
		String id = "1";
		String name="Josh";
		data.setData(name.getBytes());
		data.setSize(name.length()); 
		key.setData(id.getBytes()); 
		key.setSize(id.length());

		oprStatus = std_db.put(null, key, data);

		// Retrieving data from database
		oprStatus = std_db.get(null, key, data, LockMode.DEFAULT);
		String b = new String (data.getData());  // Converts from byte to string
		System.out.println("Name = " + b + "\n");
	
		// delete a row
		//oprStatus = std_db.delete(null, key);
		
		// Closing the connection
		std_db.close();
	} 
	catch (Exception ex) 
   	{ 
		ex.getMessage();
	} 

	}
}
