import com.sleepycat.db.*;

import datastructs.GenericStack;
import datastructs.Product;
import datastructs.Review;
import indexer.IndexGen;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DBQuery {
	
	static class StringEntry extends DatabaseEntry {
        StringEntry() {
        }

        StringEntry(String value) {
            setString(value);
        }

        void setString(String value) {
            byte[] data = value.getBytes();
            setData(data);
            setSize(data.length);
        }

        String getString() {
            return new String(getData(), getOffset(), getSize());
        }
    }

    DBQuery() {

    }

	public static void main(String[] args) {

        // start of separate method 1

		System.out.println("Enter your query below:");
		Scanner scan = new Scanner (System.in);
		String line = "";
		try { line = scan.nextLine(); }
		catch (Exception e) {}
		scan.close();
		System.out.println("You input " + line);

        // end of separate method 1

        // start of separate method 2

		String[] input = line.split(" ");
		GenericStack<String[]> lowpriorities = new GenericStack<String[]>();
		GenericStack<String> highpriorities  = new GenericStack<String>();
		GenericStack<String[]> rscorepriorities = new GenericStack<String[]>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		Product product = new Product();
		Review review = new Review();
		boolean isHPreached = false;

		// parsing string :( please dont remove
		for( int i = 0; i < input.length; i++ )
		{
			if (input[i].matches("(?i:r:.*)"))
			{
				String stringarray = input[i];
				highpriorities.push(stringarray);
			}
			else if (input[i].matches("(?i:p:.*)"))
			{
				String stringarray = input[i];
				highpriorities.push(stringarray);
			}
			else if (input[i].matches("(?i:pprice)"))
			{
				String[] pleaserefactor = {input[i], input[i+1], input[i+2]};
				lowpriorities.push(pleaserefactor);
				i = i + 2;
			}
			else if (input[i].matches("(?i:rscore)"))
			{
				/**
				* FIXME: rscore should be in 'high priority' but contains 3 values. Process in lowpriority anyway?
				* FIXME: have to parse 0 spaces , 1 space and multiple spaces in between!
				*/
				String[] pleaserefactor = {input[i], input[i+1], input[i+2]};
				rscorepriorities.push(pleaserefactor);
				i = i + 2;
			}
			else if (input[i].matches("(?i:rdate)"))
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

					String searchkey = kappa.replaceAll("r:", "").toLowerCase();
					key.setData(searchkey.getBytes());
					key.setSize(searchkey.length());

					// Returns OperationStatus
					oprStatus = std_cursor.getSearchKey(key, data, LockMode.DEFAULT);
					ArrayList<Integer> tempKeys = new ArrayList<Integer>();
					while (oprStatus == OperationStatus.SUCCESS)
					{
						String s = new String(data.getData( ));
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

					String searchkey2 = kappa.replaceAll("p:", "").toLowerCase();
					key2.setData(searchkey2.getBytes());
					key2.setSize(searchkey2.length());

					// Returns OperationStatus
					oprStatus2 = std_cursor2.getSearchKey(key2, data2, LockMode.DEFAULT);
					ArrayList<Integer> tempKeys = new ArrayList<Integer>();
					while (oprStatus2 == OperationStatus.SUCCESS)
					{
						String s = new String(data2.getData( ));
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
			} else if (kappa.matches(".*%")) {
				// Acquire a cursor for the table.
				try {

					IndexGen shell = new IndexGen();
					ArrayList<Integer> tempKeys = new ArrayList<Integer>();

					for ( String match : shell.executeCommand("grep -oh \""+ kappa.toLowerCase().replace("%", "") +"[[:alpha:]]*\" 'rterms.txt' | sort | uniq").split("\n")) {
						try {
							OperationStatus oprStatus1;
							Database std_db1 = new Database("pt.idx", null, null);
							Cursor std_cursor1 = std_db1.openCursor(null, null); // Create new cursor object
							DatabaseEntry key1 = new DatabaseEntry();
							DatabaseEntry data1 = new DatabaseEntry();

							String searchkey1 = match.toLowerCase();
							key1.setData(searchkey1.getBytes());
							key1.setSize(searchkey1.length());

							// Returns OperationStatus
							oprStatus1 = std_cursor1.getSearchKey(key1, data1, LockMode.DEFAULT);
							while (oprStatus1 == OperationStatus.SUCCESS)
							{
								String s = new String(data1.getData( ));
								tempKeys.add(Integer.parseInt(s));
								oprStatus1 = std_cursor1.getNextDup(key1, data1, LockMode.DEFAULT);
							}
							OperationStatus oprStatus2;
							Database std_db2 = new Database("rt.idx", null, null);
							Cursor std_cursor2 = std_db2.openCursor(null, null); // Create new cursor object
							DatabaseEntry key2 = new DatabaseEntry();
							DatabaseEntry data2 = new DatabaseEntry();

							String searchkey2 = match.toLowerCase();
							key2.setData(searchkey2.getBytes());
							key2.setSize(searchkey2.length());

							// Returns OperationStatus
							oprStatus2 = std_cursor2.getSearchKey(key2, data2, LockMode.DEFAULT);
							while (oprStatus2 == OperationStatus.SUCCESS)
							{
								String s = new String(data2.getData( ));
								if (!(tempKeys.contains(Integer.parseInt(s)))) {
									tempKeys.add(Integer.parseInt(s));
								}
								oprStatus2 = std_cursor2.getNextDup(key2, data2, LockMode.DEFAULT);
							}
							std_cursor1.close();
							std_db1.close();

						}
						catch (Exception e) {}
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

					/*

					OperationStatus oprStatus2;
					Database std_db2 = new Database("rt.idx", null, null);
					Cursor std_cursor2 = std_db2.openCursor(null, null); // Create new cursor object
					DatabaseEntry key2 = new DatabaseEntry();
					DatabaseEntry data2 = new DatabaseEntry();

					oprStatus2 = std_cursor2.getFirst(key2, data2, LockMode.DEFAULT);
					ArrayList<Integer> tempKeys = new ArrayList<Integer>();
					while (oprStatus2 == OperationStatus.SUCCESS)
					{
						while (oprStatus2 == OperationStatus.SUCCESS) {
							String s = new String(data2.getData( ));
							//String k = new String(key2.getData( ));
							//System.out.println("String : " + s + " Key : " + k);

							Pattern p = Pattern.compile("(?i:" + kappa.replace("%", ".*") + ")");
							if (s.matches(kappa.replace("%", "")) p.matcher(s).matches() {
								if (!(tempKeys.contains(Integer.parseInt(key2.getData().toString())))) {
									tempKeys.add(Integer.parseInt(key2.getData().toString()));
								}
							}
							oprStatus2 = std_cursor2.getNextDup(key2, data2, LockMode.DEFAULT);
						}
						oprStatus2 = std_cursor2.getNextNoDup(key2, data2, LockMode.DEFAULT);
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
			        std_cursor2.close();
			        std_db2.close();

					ArrayList<String> list = new ArrayList<String>();
			        DatabaseEntry entry = new DatabaseEntry();
			        Database std_db1 = new Database("pt.idx", null, null);
			        MultipleKeyDataEntry bulk_data = new MultipleKeyDataEntry();
			        Cursor cursor = std_db1.openCursor(null, null);
			        bulk_data.setData(new byte[1024 * 100000]); // how to setData?
			        bulk_data.setUserBuffer(1024 * 100000, true);

			        // Walk through the table, printing the key/data pairs.
			        while (cursor.getNext(entry, bulk_data, null) == OperationStatus.SUCCESS) {
			            StringEntry key = new StringEntry();
			            StringEntry data = new StringEntry();

			            while (bulk_data.next(key, data)) {
			            	if (!list.contains(key.getString())) {
			            		list.add(key.getString());
			            	}
			            }
			        }

			        DatabaseEntry entry2 = new DatabaseEntry();
			        Database std_db2 = new Database("rt.idx", null, null);
			        MultipleKeyDataEntry bulk_data2 = new MultipleKeyDataEntry();
			        Cursor cursor2 = std_db2.openCursor(null, null);
			        bulk_data2.setData(new byte[1024 * 100000]); // how to setData?
			        bulk_data2.setUserBuffer(1024 * 100000, true);
			        System.out.println("1");
			        // Walk through the table, printing the key/data pairs.
			        while (cursor2.getNext(entry2, bulk_data2, null) == OperationStatus.SUCCESS) {
			            StringEntry key = new StringEntry();
			            StringEntry data = new StringEntry();

			            while (bulk_data2.next(key, data)) {
			            	if (!list.contains(key.getString())) {
			            		list.add(key.getString());
			            	}
			            }
			        }
			        System.out.println("2");
			        ArrayList<String> matches = new ArrayList<String>();
					Pattern p = Pattern.compile("(?i:" + kappa.replace("%", ".*") + ")");
					for (String s:list) {
						if (p.matcher(s).matches()) {
					    	matches.add(s);
					    }
					}
			        System.out.println("3");
					ArrayList<Integer> tempKeys = new ArrayList<Integer>();
					for (String val : matches) {
						System.out.println(val);
			        	OperationStatus oprStatus3;
						Database std_db3 = new Database("pt.idx", null, null);
						Cursor std_cursor3 = std_db3.openCursor(null, null); // Create new cursor object
						DatabaseEntry key3 = new DatabaseEntry();
						DatabaseEntry data3 = new DatabaseEntry();

						String searchkey3 = val.toLowerCase();
						key3.setData(searchkey3.getBytes());
						key3.setSize(searchkey3.length());

						// Returns OperationStatus
						oprStatus3 = std_cursor3.getSearchKey(key3, data3, LockMode.DEFAULT);
						while (oprStatus3 == OperationStatus.SUCCESS)
						{
							String s = new String(data3.getData( ));
							tempKeys.add(Integer.parseInt(s));
							oprStatus3 = std_cursor3.getNextDup(key3, data3, LockMode.DEFAULT);
						}

						OperationStatus oprStatus4;
						Database std_db4 = new Database("rt.idx", null, null);
						Cursor std_cursor4 = std_db4.openCursor(null, null); // Create new cursor object
						DatabaseEntry key4 = new DatabaseEntry();
						DatabaseEntry data4 = new DatabaseEntry();

						String searchkey4 = val.toLowerCase();
						key4.setData(searchkey4.getBytes());
						key4.setSize(searchkey4.length());

						// Returns OperationStatus
						oprStatus4 = std_cursor4.getSearchKey(key4, data4, LockMode.DEFAULT);
						while (oprStatus4 == OperationStatus.SUCCESS)
						{
							String s = new String(data4.getData( ));
							if (!(tempKeys.contains(Integer.parseInt(s)))) {
								tempKeys.add(Integer.parseInt(s));
							}
							oprStatus4 = std_cursor4.getNextDup(key4, data4, LockMode.DEFAULT);
						}
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
			        cursor.close();
			        std_db1.close();
			        cursor2.close();
			        std_db2.close();*/
			    } catch (Exception e) {}
			}
			else {
				try {
					OperationStatus oprStatus1;
					Database std_db1 = new Database("pt.idx", null, null);
					Cursor std_cursor1 = std_db1.openCursor(null, null); // Create new cursor object
					DatabaseEntry key1 = new DatabaseEntry();
					DatabaseEntry data1 = new DatabaseEntry();
					ArrayList<Integer> tempKeys = new ArrayList<Integer>();

					String searchkey1 = kappa.toLowerCase();
					key1.setData(searchkey1.getBytes());
					key1.setSize(searchkey1.length());

					// Returns OperationStatus
					oprStatus1 = std_cursor1.getSearchKey(key1, data1, LockMode.DEFAULT);
					while (oprStatus1 == OperationStatus.SUCCESS)
					{
						String s = new String(data1.getData( ));
						tempKeys.add(Integer.parseInt(s));
						oprStatus1 = std_cursor1.getNextDup(key1, data1, LockMode.DEFAULT);
					}
					OperationStatus oprStatus2;
					Database std_db2 = new Database("rt.idx", null, null);
					Cursor std_cursor2 = std_db2.openCursor(null, null); // Create new cursor object
					DatabaseEntry key2 = new DatabaseEntry();
					DatabaseEntry data2 = new DatabaseEntry();

					String searchkey2 = kappa.toLowerCase();
					key2.setData(searchkey2.getBytes());
					key2.setSize(searchkey2.length());

					// Returns OperationStatus
					oprStatus2 = std_cursor2.getSearchKey(key2, data2, LockMode.DEFAULT);
					while (oprStatus2 == OperationStatus.SUCCESS)
					{
						String s = new String(data2.getData( ));
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
			System.out.println("Size of indices is: " + indices.size());
			ArrayList<Integer> tempKeys = new ArrayList<Integer>();
			String[] kappa = rscorepriorities.pop();
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
							if (!(tempKeys.contains(Integer.parseInt(s)))) {
								tempKeys.add(Integer.parseInt(s));
							}
							oprStatus2 = std_cursor2.getNextDup(key2, data2, LockMode.DEFAULT);
						}

						std_cursor2.close();
						std_db2.close();
					} catch (Exception e) {e.printStackTrace();}
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
							if (!(tempKeys.contains(Integer.parseInt(s)))) {
								tempKeys.add(Integer.parseInt(s));
							}
							oprStatus2 = std_cursor2.getNextDup(key2, data2, LockMode.DEFAULT);
						}

						std_db2.close();
						std_cursor2.close();
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
						if (!(tempKeys.contains(Integer.parseInt(s)))) {
								tempKeys.add(Integer.parseInt(s));
						}
						oprStatus2 = std_cursor2.getNextDup(key2, data2, LockMode.DEFAULT);
					}

					std_db2.close();
					std_cursor2.close();
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
		System.out.println("Size of indices is: " + indices.size());
		for (Integer k : indices) {
			try {
				OperationStatus oprStatus;
				Database std_db = new Database("rw.idx", null, null);
				Cursor std_cursor = std_db.openCursor(null, null); // Create new cursor object
				DatabaseEntry key = new DatabaseEntry();
				DatabaseEntry data = new DatabaseEntry();

				String searchkey = k.toString().toLowerCase();
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
						while(!tmplow.isEmpty()) {
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
						System.out.print(k + ", ");
						//product.print();
						//review.print();
					}
					oprStatus = std_cursor.getNextDup(key, data, LockMode.DEFAULT);
				}
				std_cursor.close();
				std_db.close();
			}
			catch (Exception e) {e.printStackTrace(); System.out.println("....." + k + "....");}
		}
		System.out.println();

	}

	private static void load_data(Product product, Review review, String s) {

		Scanner scan = new Scanner(s);

		review.setProductID(scan.findInLine("[\\w]+,\"").replace(",\"", ""));
		product.setID(review.getProductID());
		product.setTitle(scan.findInLine("[^\"]*\",").replace("\",", ""));
		product.setPrice(scan.findInLine("[^,]+,").replace(",", ""));
		review.setUserID(scan.findInLine("[\\w]+,\"").replace(",\"", ""));
		review.setProfileName(scan.findInLine("[^\"]+\",").replace("\",", ""));
		review.setHelpfulness(scan.findInLine("[^,]+,").replace(",", ""));
		review.setScore(Double.parseDouble(scan.findInLine("[^,]+,").replace(",", "")));
		review.setTime(scan.findInLine("[^,]+,\"").replace(",\"", ""));
		review.setSummary(scan.findInLine("[^\"]+\",\"").replace("\",\"", ""));
		review.setText(scan.findInLine("[^\"]+\"").replace("\"", ""));

		scan.close();
	}
}
