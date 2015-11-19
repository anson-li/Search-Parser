import com.sleepycat.db.*;
import java.io.*;
import java.util.ArrayList;
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
	private static class GenericStack<T> extends java.util.ArrayList<T> {
		  
	  public void push(T obj){
	    add(0, obj);
	  }
	  
	  public T pop(){
	    if (isEmpty())
	      return null;
	    
	    T obj = get(0);
	    remove(0);
	    return obj;
	  }
	  
	  public boolean isEmpty(){
	    if (size() == 0)
	      return true;
	    return false;    
	  }
	}

	public static void main(String[] args) {
		System.out.println("Enter your query below:");
		BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		try { line = buffer.readLine(); }
		catch (Exception e) {}
		System.out.println("You input " + line);
		
		/*
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
		*/
		
		String[] input = line.split(" ");
		validate_input(input);
		GenericStack<String[]> lowpriorities = new GenericStack<String[]>();
		GenericStack<String> highpriorities  = new GenericStack<String>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		// parsing string :( please dont remove 
		for( int i = 0; i < input.length; i++ )
		{
			if (input[i].matches("r:.*"))
			{
				String stringarray = input[i];
				highpriorities.push(stringarray);
			} 
			else if (input[i].matches("p:.*"))
			{
				String stringarray = input[i];
				highpriorities.push(stringarray);
			}
			else if (input[i].matches("pprice"))
			{
				String[] pleaserefactor = {input[i], input[i+1], input[i+2]};
				lowpriorities.push(pleaserefactor);
				i = i + 2;
			}
			else if (input[i].matches("rscore"))
			{
				String[] pleaserefactor = {input[i], input[i+1], input[i+2]};
				lowpriorities.push(pleaserefactor);
				i = i + 2;
			}
			else if (input[i].matches("rdate"))
			{
				String[] pleaserefactor = {input[i], input[i+1], input[i+2]};
				lowpriorities.push(pleaserefactor);
				i = i + 2;
			} else {
				String stringarray = input[i];
				highpriorities.push(stringarray);
			}
		}
		// reading high priority queue
		for (int i = 0; !highpriorities.isEmpty(); i++) {
			String kappa = highpriorities.pop();
			if (kappa.matches("r:.*")) {
				try {
					OperationStatus oprStatus;
					Database std_db = new Database("rt.idx", null, null);
					Cursor std_cursor = std_db.openCursor(null, null); // Create new cursor object
					DatabaseEntry key = new DatabaseEntry();
					DatabaseEntry data = new DatabaseEntry();
					
					String searchkey = kappa.replaceAll("r:", "");
					key.setData(searchkey.getBytes()); 
					key.setSize(searchkey.length());

					// Returns OperationStatus
					oprStatus = std_cursor.getSearchKey(key, data, LockMode.DEFAULT);
					ArrayList<Integer> tempKeys = new ArrayList<Integer>();
					while (oprStatus == OperationStatus.SUCCESS)
					{
						String s = new String(data.getData( ));
						tempKeys.add(Integer.parseInt(s));
						oprStatus = std_cursor.getNextDup(key, data, LockMode.DEFAULT);
					}
					if (i == 0) {
						indices = tempKeys;
					}
					if (i != 0) {
						for (Integer j : indices) {
							if (!tempKeys.contains(j)) {
								indices.remove(j);
							}
						}
					}
					
				}
				catch (Exception e) {}
				for (Integer help : indices) {
					
				}
			}
		}
		for (Integer k : indices) {
			try {
				OperationStatus oprStatus;
				Database std_db = new Database("rw.idx", null, null);
				Cursor std_cursor = std_db.openCursor(null, null); // Create new cursor object
				DatabaseEntry key = new DatabaseEntry();
				DatabaseEntry data = new DatabaseEntry();
				
				String searchkey = k.toString();
				key.setData(searchkey.getBytes()); 
				key.setSize(searchkey.length());

				// Returns OperationStatus
				oprStatus = std_cursor.getSearchKey(key, data, LockMode.DEFAULT);
				while (oprStatus == OperationStatus.SUCCESS)
				{
					String s = new String(data.getData( ));
					// parse into full review
					System.out.println(s);
					oprStatus = std_cursor.getNextDup(key, data, LockMode.DEFAULT);
				}			
			}
			catch (Exception e) {}
		}
		// reading low priority queue
		
		
	}

	private static void validate_input(String[] input) {
		// TODO Auto-generated method stub
		
	}
}