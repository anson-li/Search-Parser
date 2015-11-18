import com.sleepycat.db.*;
import java.io.*;
import java.util.regex.*;
import java.lang.*;

public class DBQuery {

	/*
	QUERIES TO PROCESS:
	p:camera
	** The first query returns all records that have the term camera in the product title. 
	r:great
	** The second query return all records that have the term great in the review summary or text. 
	camera
	** The third query returns all records that have the term camera in one of the fields product title, 
	** review summary or review text.
	cam%
	** The fourth query returns all records that have a term starting with cam in one of the fields 
	** product title, review summary or review text. 
	r:great cam%
	** The fifth query returns all records that have the term great in the review summary or text 
	** and a term starting with cam in one of the fields product title, review summary or review text. 
	rscore > 4
	** The sixth query returns all records with a review score greater than 4.
	camera rscore < 3
	** The 7th query is the same as the third query except it returns only those records with a 
	** review score less than 3.
	pprice < 60 camera
	** The 8th query is the same as the third query except the query only returns those records 
	** where price is present and has a value less than 60. Note that there is no index on the price field; 
	** this field is checked after retrieving the candidate records using conditions on which indexes are available (e.g. terms). 
	camera rdate > 2007/06/20
	** The 9th query returns the records that have the term camera in one of the fields product title, review 
	** summary or review text, and the review date is after 2007/06/20. Since there is no index on the review date, 
	** this condition is checked after checking the conditions on terms. Also the review date stored in file reviews.txt 
	** is in the form of a timestamp, and the date give in the query must be converted to a timestamp before a comparison 
	** (e.g. check out the date object in the datetime package for Python). Finally the last query returns the same set of 
	** results as in the 9th query except the product price must be greater than 20 and less than 60.
	camera rdate > 2007/06/20 pprice > 20 pprice < 60
	*/

	public static void main(String[] args) {
		System.out.println("Enter your query below:");
		BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		try { line = buffer.readLine(); }
		catch (Exception e) {}
		System.out.println("You input " + line);
		
		
		
		try {
		OperationStatus oprStatus;
		Database std_db = new Database("rw.idx", null, null);
		Cursor std_cursor = std_db.openCursor(null, null); // Create new cursor object
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		
		String searchkey = "1";
		key.setData(searchkey.getBytes()); 
		key.setSize(searchkey.length());

		// Returns OperationStatus
		oprStatus = std_cursor.getSearchKey(key, data, LockMode.DEFAULT);
		
		while (oprStatus == OperationStatus.SUCCESS)
		{
			String s = new String(data.getData( ));
			System.out.println(s);
			oprStatus = std_cursor.getNextDup(key, data, LockMode.DEFAULT);
			// get next duplicate
		}}
		catch (Exception e) {}
	}
}