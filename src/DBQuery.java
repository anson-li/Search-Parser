import com.sleepycat.db.*;

import datastructs.Product;
import datastructs.Review;
import datastructs.StringEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.*;
import java.lang.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
    
    private Query userQuery;

    DBQuery() {
        userQuery = new Query("");
    }
    
    private class Query {
        private String query;

        Query(String query) {
            this.query = query;
        }

        public void setQuery(String query) {
            this.query = query;
            compress();
        }

        public String getQuery() {
            return query;
        }

        @Override
        public String toString() {
            return query;
        }

        public boolean isValid() {
            return false;
        }
        
        private void compress() {
            query = query.replaceAll("[  ]*<[  ]*", "<")
                         .replaceAll("[  ]*=[  ]*", "=")
                         .replaceAll("[  ]*>[  ]*", ">");
        }
    }
    
    public void requestUserQuery() throws IOException {
        System.out.print(">> ");
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        userQuery.setQuery(buffer.readLine());
    }

    public void loadSubqueryPriorityQ(Query query) {
        if (!query.isValid())
            return; // TODO: throw an exception.

        for (String subquery : query.getQuery().split(" "))
        {

        }
    }

    
	private static class GenericStack<T> extends java.util.ArrayList<T> {
		  
	  public GenericStack() {}
		
	  public GenericStack(GenericStack<T> parentObj) {
		  for (T el : parentObj) {
			  push(el);
		  }
		}

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
		
        // start of separate method 1

		System.out.println("Enter your query below:");
		BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		try { line = buffer.readLine(); }
		catch (Exception e) {}
		System.out.println("You input " + line);

        // end of separate method 1
		
        // start of separate method 2

		String[] input = line.split(" ");
		validate_input(input);
		GenericStack<String[]> lowpriorities = new GenericStack<String[]>();
		GenericStack<String> highpriorities  = new GenericStack<String>();
		GenericStack<String[]> rscorepriorities = new GenericStack<String[]>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Product> productlist = new ArrayList<Product>();
		ArrayList<Review> reviewlist = new ArrayList<Review>();
		Product product = new Product();
		Review review = new Review();
		boolean isHPreached = false;
		
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
				/**
				* FIXME: rscore should be in 'high priority' but contains 3 values. Process in lowpriority anyway?
				*/
				String[] pleaserefactor = {input[i], input[i+1], input[i+2]};
				rscorepriorities.push(pleaserefactor);
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
			isHPreached = true;
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
						System.out.println(s);
						if (!(tempKeys.contains(Integer.parseInt(s)))) {
							tempKeys.add(Integer.parseInt(s));
						}
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
			}
			else if (kappa.matches("p:.*")) {
				try {
					OperationStatus oprStatus2;
					Database std_db2 = new Database("pt.idx", null, null);
					Cursor std_cursor2 = std_db2.openCursor(null, null); // Create new cursor object
					DatabaseEntry key2 = new DatabaseEntry();
					DatabaseEntry data2 = new DatabaseEntry();
					
					String searchkey2 = kappa.replaceAll("p:", "");
					key2.setData(searchkey2.getBytes()); 
					key2.setSize(searchkey2.length());

					// Returns OperationStatus
					oprStatus2 = std_cursor2.getSearchKey(key2, data2, LockMode.DEFAULT);
					ArrayList<Integer> tempKeys = new ArrayList<Integer>();
					while (oprStatus2 == OperationStatus.SUCCESS)
					{
						String s = new String(data2.getData( ));
						if (!(tempKeys.contains(Integer.parseInt(s)))) {
							System.out.println(new String(data2.getData()));
							tempKeys.add(Integer.parseInt(s));
						}
						oprStatus2 = std_cursor2.getNextDup(key2, data2, LockMode.DEFAULT);
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
			} else if (kappa.matches("%.*") && !kappa.matches(".*%")) {
				// Acquire a cursor for the table.
		        DatabaseEntry entry = new DatabaseEntry();
		        Database std_db1 = new Database("pt.idx", null, null);
		        MultipleKeyDataEntry bulk_data = new MultipleKeyDataEntry();
		        Cursor cursor = std_db1.openCursor(null, null);
		        bulk_data.setData(new byte[1024 * 30000]); // how to setData? 
		        bulk_data.setUserBuffer(1024 * 30000, true);

		        // Walk through the table, printing the key/data pairs.
		        while (cursor.getNext(entry, bulk_data, null) == OperationStatus.SUCCESS) {
		            StringEntry key = new StringEntry();
		            StringEntry data = new StringEntry();

		            while (bulk_data.next(key, data))
		                System.out.println(key.getString() + " : " + data.getString());
		        }
		        cursor.close();
		        std_db1.close();
			} else if (kappa.matches(".*%") && !kappa.matches("%.*")) {
				// only apply to end of value
			} else if (kappa.matches("%.*%")) {
				// apply regex to both ends
			}
			else {
				try {
					OperationStatus oprStatus1;
					Database std_db1 = new Database("pt.idx", null, null);
					Cursor std_cursor1 = std_db1.openCursor(null, null); // Create new cursor object
					DatabaseEntry key1 = new DatabaseEntry();
					DatabaseEntry data1 = new DatabaseEntry();
					ArrayList<Integer> tempKeys = new ArrayList<Integer>();
					
					String searchkey1 = kappa;
					key1.setData(searchkey1.getBytes()); 
					key1.setSize(searchkey1.length());

					// Returns OperationStatus
					oprStatus1 = std_cursor1.getSearchKey(key1, data1, LockMode.DEFAULT);
					while (oprStatus1 == OperationStatus.SUCCESS)
					{
						String s = new String(data1.getData( ));
						System.out.println(new String(data1.getData()));
						tempKeys.add(Integer.parseInt(s));
						oprStatus1 = std_cursor1.getNextDup(key1, data1, LockMode.DEFAULT);
					}
					OperationStatus oprStatus2;
					Database std_db2 = new Database("rt.idx", null, null);
					Cursor std_cursor2 = std_db2.openCursor(null, null); // Create new cursor object
					DatabaseEntry key2 = new DatabaseEntry();
					DatabaseEntry data2 = new DatabaseEntry();
					
					String searchkey2 = kappa;
					key2.setData(searchkey2.getBytes()); 
					key2.setSize(searchkey2.length());

					// Returns OperationStatus
					oprStatus2 = std_cursor2.getSearchKey(key2, data2, LockMode.DEFAULT);
					while (oprStatus2 == OperationStatus.SUCCESS)
					{
						String s = new String(data2.getData( ));
						System.out.println(new String(data2.getData()));
						if (!(tempKeys.contains(Integer.parseInt(s)))) {
							tempKeys.add(Integer.parseInt(s));
						}
						oprStatus2 = std_cursor2.getNextDup(key2, data2, LockMode.DEFAULT);
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
			}
		}
		for (int m = 0; !rscorepriorities.isEmpty(); m++) {
			ArrayList<Integer> tempKeys = new ArrayList<Integer>();
			String[] kappa = rscorepriorities.pop();
			// kappa[0] = rscore kappa[1] = < kappa[2] = 4
			if (kappa[1].equals("<")) {
				for (int n = 0; n < Integer.parseInt(kappa[2]); n++) {
					try {
						OperationStatus oprStatus2;
						Database std_db2 = new Database("sc.idx", null, null);
						Cursor std_cursor2 = std_db2.openCursor(null, null); // Create new cursor object
						DatabaseEntry key2 = new DatabaseEntry();
						DatabaseEntry data2 = new DatabaseEntry();

						String searchkey2 = n + ".0"; // may have to change this depending on iterator
						key2.setData(searchkey2.getBytes()); 
						key2.setSize(searchkey2.length());

						// Returns OperationStatus
						oprStatus2 = std_cursor2.getSearchKey(key2, data2, LockMode.DEFAULT);
						while (oprStatus2 == OperationStatus.SUCCESS)
						{
							String s = new String(data2.getData( ));
							System.out.println(new String(data2.getData()));
							if (!(tempKeys.contains(Integer.parseInt(s)))) {
								tempKeys.add(Integer.parseInt(s));
							}
							oprStatus2 = std_cursor2.getNextDup(key2, data2, LockMode.DEFAULT);
						}
					} catch (Exception e) {}
				}
				if (isHPreached == false && m == 0) {
					indices = tempKeys;
				} else {
					for (Integer o : indices) {
						if (!tempKeys.contains(o)) {
							indices.remove(o);
						}
					}
				}
			} else if (kappa[1].equals(">")) {
				for (int n = 5; n > Integer.parseInt(kappa[2]); n--) {
					try {
						OperationStatus oprStatus2;
						Database std_db2 = new Database("sc.idx", null, null);
						Cursor std_cursor2 = std_db2.openCursor(null, null); // Create new cursor object
						DatabaseEntry key2 = new DatabaseEntry();
						DatabaseEntry data2 = new DatabaseEntry();
						
						String searchkey2 = n + ".0"; // may have to change this depending on iterator
						key2.setData(searchkey2.getBytes()); 
						key2.setSize(searchkey2.length());

						// Returns OperationStatus
						oprStatus2 = std_cursor2.getSearchKey(key2, data2, LockMode.DEFAULT);
						while (oprStatus2 == OperationStatus.SUCCESS)
						{
							String s = new String(data2.getData( ));
							System.out.println(new String(data2.getData()));
							if (!(tempKeys.contains(Integer.parseInt(s)))) {
								tempKeys.add(Integer.parseInt(s));
							}
							oprStatus2 = std_cursor2.getNextDup(key2, data2, LockMode.DEFAULT);
						}
					} catch (Exception e) {}
				}
				if (isHPreached == false && m == 0) {
					indices = tempKeys;
				} else {
					for (Integer o : indices) {
						if (!tempKeys.contains(o)) {
							indices.remove(o);
						}
					}
				}
			} else if (kappa[1].equals("=")) {
				try {
					OperationStatus oprStatus2;
					Database std_db2 = new Database("sc.idx", null, null);
					Cursor std_cursor2 = std_db2.openCursor(null, null); // Create new cursor object
					DatabaseEntry key2 = new DatabaseEntry();
					DatabaseEntry data2 = new DatabaseEntry();
					
					String searchkey2 = kappa[2] + ".0"; // may have to change this depending on iterator
					key2.setData(searchkey2.getBytes()); 
					key2.setSize(searchkey2.length());

					// Returns OperationStatus
					oprStatus2 = std_cursor2.getSearchKey(key2, data2, LockMode.DEFAULT);
					while (oprStatus2 == OperationStatus.SUCCESS)
					{
						String s = new String(data2.getData( ));
						System.out.println(new String(data2.getData()));
						if (!(tempKeys.contains(Integer.parseInt(s)))) {
								tempKeys.add(Integer.parseInt(s));
						}
						oprStatus2 = std_cursor2.getNextDup(key2, data2, LockMode.DEFAULT);
					}
				} catch (Exception e) {}
				if (isHPreached == false && m == 0) {
					indices = tempKeys;
				} else {
					for (Integer o : indices) {
						if (!tempKeys.contains(o)) {
							indices.remove(o);
						}
					}
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
									
					load_data(product, review, s);
					
					/**
					 * FIXME:XXX:TODO: reading low priority queue
					 */
					GenericStack<String[]> tmplow = new GenericStack<String[]>(lowpriorities);
					Bill: {
						for (int l = 0; !tmplow.isEmpty(); l++) {
							String[] mappa = tmplow.pop();
							if (mappa[0].equals("pprice") ) {
								String comparator = mappa[1];
								Double value = Double.parseDouble(mappa[2]);
								switch (comparator) {
									case "<":
										if (product.getPrice().equals("unknown") || !(Double.parseDouble(product.getPrice()) < value)) {
											break Bill;
										} else {
											break;
										}
									case ">":
										if (product.getPrice().equals("unknown") || !(Double.parseDouble(product.getPrice()) > value)) {
											break Bill;
										} else {
											break;
										}
									case "=":
										if (product.getPrice().equals("unknown") || !(Double.parseDouble(product.getPrice()) == value)) {
											break Bill;
										} else {
											break;
										}
									default:
										break Bill;
								}
							}
							if (mappa[0].equals("rdate") ) {
								String comparator = mappa[1];
							    DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
							    Date valuedate = df.parse(mappa[2] + " 00:00:00");
							    System.out.println("Valuedate: " + valuedate.getTime() + " || Review: " + review.getTime());
							    long valuedatedoesntmatataer = (valuedate.getTime() / 1000) - 25200; // delay set by 7hours - timezone difference.
								switch (comparator) {
									case "<":
										if (!(Long.parseLong(review.getTime()) < valuedatedoesntmatataer)) {
											break Bill;
										} else {
											break;
										}
									case ">":
										if (!(Long.parseLong(review.getTime()) > valuedatedoesntmatataer)) {
											break Bill;
										} else {
											break;
										}
									case "=":
										if (!(Long.parseLong(review.getTime()) == valuedatedoesntmatataer)) {
											break Bill;
										} else {
											break;
										}
									default:
										break Bill;
								}
							}
						}
						product.print();
						review.print();	
					}
					oprStatus = std_cursor.getNextDup(key, data, LockMode.DEFAULT);
				}			
			}
			catch (Exception e) {e.printStackTrace();}
		}
		
	}

	private static void load_data(Product product, Review review, String s) {
		
		Scanner scan = new Scanner(s);
		
		review.setProductID(scan.findInLine("[\\w]+,\"").replace(",\"", ""));
		product.setID(review.getProductID());
		product.setTitle(scan.findInLine("[^\"]+\",").replace("\",", ""));
		product.setPrice(scan.findInLine("[^,]+,").replace(",", ""));
		review.setUserID(scan.findInLine("[\\w]+,\"").replace(",\"", ""));
		review.setProfileName(scan.findInLine("[^\"]+\",").replace("\",", ""));
		review.setHelpfulness(scan.findInLine("[^,]+,").replace(",", ""));
		review.setScore(Double.parseDouble(scan.findInLine("[^,]+,").replace(",", "")));
		review.setTime(scan.findInLine("[^,]+,\"").replace(",\"", ""));
		review.setSummary(scan.findInLine("[^\"]+\",\"").replace("\",\"", ""));
		review.setText(scan.findInLine("[^\"]+\"").replace("\"", ""));
	}

	private static void validate_input(String[] input) {
		// TODO Auto-generated method stub
		
	}

}
