package querier;

import com.sleepycat.db.*;

import datastructs.GenericStack;
import datastructs.Product;
import datastructs.Query;
import datastructs.Review;
import exceptions.DBMSException;
import exceptions.DBMSExitException;
import indexer.IndexGen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

public class DBMS {
    private String reviewsIndex ;
    private String rtermsIndex  ;
    private String ptermsIndex  ;
    private String rscoreIndex  ;
    private BufferedReader buffer;
    private GenericStack<String> lowpriorities;
    private GenericStack<String> highpriorities;
    private GenericStack<String> rscorepriorities;
    private ArrayList<Integer> indices;
    
    private enum COMPARE { GREATER, LESS, EQUAL };
    DBMS() throws DBMSException {
        buffer = new BufferedReader(new InputStreamReader(System.in));
        

        reviewsIndex = "rw.idx";
        rtermsIndex  = "rt.idx";
        ptermsIndex  = "pt.idx";
        rscoreIndex  = "sc.idx";
        verifyDB();
        
        lowpriorities    = new GenericStack<String>();
        highpriorities   = new GenericStack<String>();
        rscorepriorities = new GenericStack<String>();
        indices			 = new ArrayList<Integer>();
    }


    private void verifyDB() throws DBMSException {
        for(File file : getIndexFiles()) {
            if (!file.isFile())
                throw new DBMSException("Index file not defined: " + file.getName());
        }
    }

    private File[] getIndexFiles() {
        File[] files = new File[4];
        files[0]     = new File(reviewsIndex);
        files[1]     = new File(rtermsIndex);
        files[2]     = new File(ptermsIndex);
        files[3]     = new File(rscoreIndex);
        return files;
    }

    public void requestUserQuery(Query query) throws IOException, DBMSExitException {
        System.out.print(">> ");
        query.setQuery(buffer.readLine());
        if (query.toString().equals("exit()"))
        	throw new DBMSExitException("Caught exit()");
    }
    
    public void processQuery(Query query) {
        loadSubqueryPriorityQ(query);
        
        boolean has_hp_query = false;
        
        try {
			has_hp_query = processHighPriorities();
		} catch (FileNotFoundException | DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        	
		try {
			processRScorePriority(has_hp_query);
		} catch (FileNotFoundException | DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			printResults();
		} catch (FileNotFoundException | DatabaseException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }

    private void printResults() throws DatabaseException, FileNotFoundException, ParseException {
    	
        System.out.println("Num of indices before pprice rdate constraints: " + indices.size());
        int counter = 0;
    	if (indices.isEmpty())
    		System.out.println("No results matching given query.");

		for (Integer index : indices) {
    		
    		OperationStatus oprStatus;
			Database std_db = new Database("rw.idx", null, null);
			Cursor std_cursor = std_db.openCursor(null, null); // Create new cursor object
			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry data = new DatabaseEntry();
			Product product  = new Product();
			Review review = new Review();

			String searchkey = index.toString().toLowerCase();
			key.setData(searchkey.getBytes());
			key.setSize(searchkey.length());

			// Returns OperationStatus
			oprStatus = std_cursor.getSearchKey(key, data, LockMode.DEFAULT);
			Bill: {
				if (oprStatus == OperationStatus.SUCCESS)
				{
					String s = new String(data.getData( ));
	
					load_data(product, review, s);
	
					/**
					 * FIXME:XXX:TODO: reading low priority queue
					 */
					GenericStack<String> tmplow = new GenericStack<String>(lowpriorities);
					while(!tmplow.isEmpty()) {
						String subquery = tmplow.pop();
						
						if (subquery.matches("pprice.*")) {
							Double value = Double.parseDouble(subquery.replace("pprice", "").replace(">", "").replace("=", "").replace("<", ""));
							
							if (product.getPrice().equals("unknown"))
								break Bill;
							if (subquery.matches("pprice<.*") && !(Double.parseDouble(product.getPrice()) > value))
								continue;
							else if (subquery.matches("pprice=.*") && !(Double.parseDouble(product.getPrice()) == value))
								continue;
							else if (subquery.matches("pprice>.*") && !(Double.parseDouble(product.getPrice()) < value))
								continue;
							else
								break Bill;
							
						} else if (subquery.matches("rdate.*")) {
							DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
						    Date valuedate = df.parse(subquery.replace("rdate", "").replace(">", "").replace("=", "").replace("<", "") + " 00:00:00");
						    long value = (valuedate.getTime() / 1000) - 25200; // delay set by 7hours - timezone difference.
							
							if (product.getPrice().equals("unknown"))
								break Bill;
							if (subquery.matches("rdate<.*") && (Long.parseLong(review.getTime()) > value))
								continue;
							else if (subquery.matches("rdate=.*") && (Long.parseLong(review.getTime()) == value))
								continue;
							else if (subquery.matches("rdate>.*") && (Long.parseLong(review.getTime()) < value))
								continue;
							else
								break Bill;
						}
					}
			    	
					System.out.print(" "+ index +" ");
					counter += 1;
		    		
					///product.print();
					//review.print();
					
					//oprStatus = std_cursor.getNextNoDup(key, data, LockMode.DEFAULT);
				}
				std_cursor.close();
				std_db.close();
		    	
			}
		}
    	System.out.println("Done. Counter: " + counter);
	}


	private void load_data(Product product, Review review, String s) {

		Scanner scan = new Scanner(s);

		review.setProductID(scan.findInLine("[\\w]+,\"").replace(",\"", ""));
		product.setID(review.getProductID());
		product.setTitle(scan.findInLine("[^\"]*\",").replace("\",", ""));
		product.setPrice(scan.findInLine("[^,]+,").replace(",", ""));
		review.setUserID(scan.findInLine("[\\w]+,\"").replace(",\"", ""));
		review.setProfileName(scan.findInLine("[^\"]*\",").replace("\",", ""));
		review.setHelpfulness(scan.findInLine("[^,]*,").replace(",", ""));
		review.setScore(Double.parseDouble(scan.findInLine("[^,]+,").replace(",", "")));
		review.setTime(scan.findInLine("[^,]+,\"").replace(",\"", ""));
		review.setSummary(scan.findInLine("[^\"]*\",\"").replace("\",\"", ""));
		review.setText(scan.findInLine("[^\"]*\"").replace("\"", ""));

		scan.close();
		
	}


	private void loadSubqueryPriorityQ(Query query) {
        if (!query.isValid())
            return; // TODO: throw an exception.

        for (String subquery : query.getQuery().split(" "))
        {
        	if (   subquery.matches("(?i:r:.*)")
                || subquery.matches("(?i:p:.*)"))
            {
                highpriorities.push(subquery);
            }
            else if (   subquery.matches("(?i:pprice.*)")
                     || subquery.matches("(?i:rdate.*)"))
            {
                lowpriorities.push(subquery);
            }
            else if (subquery.matches("(?i:rscore.*)"))
            {
                rscorepriorities.push(subquery);
            }
            else
                highpriorities.push(subquery);
        }
    }
    
    private void queryRTerms(String query, ArrayList<Integer> resultIndices) 
    		throws DatabaseException, FileNotFoundException
    {
    	query = query.replace("r:", "").toLowerCase();
    	queryDB(query, rtermsIndex, resultIndices);
    }
    
    private void queryPTerms(String query, ArrayList<Integer> resultIndices) 
    		throws DatabaseException, FileNotFoundException
    {
        query = query.replace("p:", "").toLowerCase();
    	queryDB(query, ptermsIndex, resultIndices);
    }
    
    private void queryRScore(String query, ArrayList<Integer> resultIndices, COMPARE cmp) 
    		throws FileNotFoundException, DatabaseException
    {
    	if (cmp == COMPARE.EQUAL) {
    		query = query.replace("rscore=", "").toLowerCase();
        	queryDB(query, rscoreIndex, resultIndices);
        	return;
    	} else if (cmp == COMPARE.LESS) {
    		query = query.replace("rscore<", "");
    	} else {
    		query = query.replace("rscore>", "");
    	}
    	
    	OperationStatus oprStatus;
		Database std_db = new Database(rscoreIndex, null, null);
		Cursor std_cursor = std_db.openCursor(null, null); // Create new cursor object
		
		if (cmp == COMPARE.LESS) {
			for (int n = 0; n < Integer.parseInt(query); n++) {
			    String searchkey = new String();
                searchkey = n + ".0";
                DatabaseEntry key = new DatabaseEntry();
				DatabaseEntry data = new DatabaseEntry();
				oprStatus = std_cursor.getFirst(key, data, LockMode.DEFAULT);
                key.setData(searchkey.getBytes());
				key.setSize(searchkey.length());
				data = new DatabaseEntry();
                oprStatus = std_cursor.getSearchKey(key, data, LockMode.DEFAULT);
				while (oprStatus == OperationStatus.SUCCESS)
				{
					String s = new String(data.getData( ));
					if (!(resultIndices.contains(Integer.parseInt(s)))) {
						resultIndices.add(Integer.parseInt(s));
					}
					oprStatus = std_cursor.getNextDup(key, data, LockMode.DEFAULT);
				}
			}
				
		} else {
			for (int n = 5; n > Integer.parseInt(query); n--) {
				String searchkey = new String();
				searchkey = n + ".0";
				DatabaseEntry key = new DatabaseEntry();
				DatabaseEntry data = new DatabaseEntry();
				oprStatus = std_cursor.getFirst(key, data, LockMode.DEFAULT);
				key.setData(searchkey.getBytes());
				key.setSize(searchkey.length());
				data = new DatabaseEntry();
				oprStatus = std_cursor.getSearchKey(key, data, LockMode.DEFAULT);
				while (oprStatus == OperationStatus.SUCCESS)
				{
					String s = new String(data.getData( ));
					if (!(resultIndices.contains(Integer.parseInt(s)))) {
						resultIndices.add(Integer.parseInt(s));
					}
					oprStatus = std_cursor.getNextDup(key, data, LockMode.DEFAULT);
				}
			}
		}
	}	
    
    private void queryDB(String query, String db_name, ArrayList<Integer> indices) 
    		throws DatabaseException, FileNotFoundException
    {
    	OperationStatus oprStatus;
        Database std_db = new Database(db_name, null, null);
        Cursor std_cursor = std_db.openCursor(null, null); // Create new cursor object
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        
        key.setData(query.getBytes());
        key.setSize(query.length());

        // Returns OperationStatus
        oprStatus = std_cursor.getSearchKey(key, data, LockMode.DEFAULT);
        while (oprStatus == OperationStatus.SUCCESS)
        {
            String s = new String(data.getData( ));
            if (!(indices.contains(Integer.parseInt(s)))) {
                indices.add(Integer.parseInt(s));
            }
            oprStatus = std_cursor.getNextDup(key, data, LockMode.DEFAULT);
        }

        std_cursor.close();
        std_db.close();
    }
    
    private boolean processHighPriorities() 
    		throws DatabaseException, FileNotFoundException
    {
        if (highpriorities.isEmpty())
            return false;
         
        for (int i = 0; !highpriorities.isEmpty(); i++) {
            String subquery = highpriorities.pop();
            if (subquery.matches(".*%")) {
                subquery = subquery.replace("%", "");
                IndexGen shell = new IndexGen();
                ArrayList<Integer> next_result_indices = new ArrayList<Integer>();
                
                String check_file = "";
                
                if (subquery.matches("r:[^%]*")) {
                	check_file += "'rterms.txt'";
                	subquery = subquery.replace("r:", "");
                } else if (subquery.matches("p:[^%]*")) {
                	check_file += "'pterms.txt'";
                	subquery = subquery.replace("p:", "");
                } else {
                	check_file += "'rterms.txt' 'pterms.txt'";
                }
            	for ( String match : shell.executeCommand("grep -oh \""+ subquery.toLowerCase().replace("%", "") +"[[:alpha:]]*\" "+ check_file+" | sort | uniq").split("\n")) {
					queryPTerms(match, next_result_indices);
					queryRTerms(match, next_result_indices);
				}
				
				if (i == 0)
                    indices = next_result_indices;
            	else {
            		Iterator<Integer> iter = indices.iterator();
            		while(iter.hasNext())
                        if (!next_result_indices.contains(iter.next()))
                            iter.remove();
            	}
                
            } else if (subquery.matches("r:[^%]*")) {
                ArrayList<Integer> next_result_indices = new ArrayList<Integer>();
            	queryRTerms(subquery, next_result_indices);
            	if (i == 0)
                    indices = next_result_indices;
            	else {
            		Iterator<Integer> iter = indices.iterator();
            		while(iter.hasNext())
                        if (!next_result_indices.contains(iter.next()))
                            iter.remove();
            	}
                
            } else if (subquery.matches("p:[^%]*")) {
            	ArrayList<Integer> next_result_indices = new ArrayList<Integer>();
            	queryPTerms(subquery, next_result_indices);
            	if (i == 0)
                    indices = next_result_indices;
            	else {
            		Iterator<Integer> iter = indices.iterator();
            		while(iter.hasNext())
                        if (!next_result_indices.contains(iter.next()))
                            iter.remove();
            	}
            } else {
            	ArrayList<Integer> next_result_indices = new ArrayList<Integer>();
            	queryPTerms(subquery, next_result_indices);
            	queryRTerms(subquery, next_result_indices);
            	if (i == 0)
                    indices = next_result_indices;
            	else {
            		Iterator<Integer> iter = indices.iterator();
            		while(iter.hasNext())
                        if (!next_result_indices.contains(iter.next()))
                            iter.remove();
            	}
            }
        }
        return true;
    }

    private void processRScorePriority(boolean has_high_priority) 
    		throws FileNotFoundException, DatabaseException
    {
    	boolean first = true;
    	while(!rscorepriorities.isEmpty()) {
			ArrayList<Integer> tempKeys = new ArrayList<Integer>();
			String subquery = rscorepriorities.pop();
			COMPARE cmp = COMPARE.EQUAL;
			if (subquery.matches("rscore<.*"))
				cmp = COMPARE.LESS;
			else if (subquery.matches("rscore=.*"))
				cmp = COMPARE.EQUAL;
			else if (subquery.matches("rscore>.*"))
				cmp = COMPARE.GREATER;
			
			queryRScore(subquery, tempKeys, cmp);
			
			if (!has_high_priority && first) {
				indices = tempKeys;
				first = false;
			} else {

				Iterator<Integer> iter = indices.iterator();
				while(iter.hasNext())
					if (!tempKeys.contains(iter.next()))
						iter.remove();
			}
		}
	}
    
    public void reset() {
        lowpriorities    = new GenericStack<String>();
        highpriorities   = new GenericStack<String>();
        rscorepriorities = new GenericStack<String>();
        indices			 = new ArrayList<Integer>();
    }
    
}
