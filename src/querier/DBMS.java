package querier;

import com.sleepycat.db.*;

import datastructs.GenericStack;
import datastructs.Query;
import exceptions.DBMSException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
                throw new DBMSException("Index file not defined: " 
                                        + file.getName());
        }
    }

    private File[] getIndexFiles() {
        File[] files = new File[4];
        files[0] = new File(reviewsIndex);
        files[1] = new File(rtermsIndex);
        files[2] = new File(ptermsIndex);
        files[3] = new File(rscoreIndex);
        return files;
    }

    public void requestUserQuery(Query query) throws IOException {
        System.out.print(">> ");
        query.setQuery(buffer.readLine());
    }
    
    public void processQuery(Query query) {
        loadSubqueryPriorityQ(query);
        
        boolean has_hp_query = false;
        
        try {
        	has_hp_query = processHighPriorities();
        } catch (DatabaseException de) {
        	// TODO:
        } catch (FileNotFoundException fnfe) {
        	// TODO:
        }
        
        processRScorePriority();
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
            else if (   subquery.matches("(?i:pprice)")
                     || subquery.matches("(?i:rdate)"))
            {
                lowpriorities.push(subquery);
            }
            else if (subquery.matches("(?i:rscore)"))
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
    	queryDB(query, rtermsIndex, resultIndices);
    }
    
    private void queryRScore(String query, ArrayList<Integer> resultIndices)
    {
    	
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
        
        std_db.close();
        std_cursor.close();
    }
    
    private boolean processHighPriorities() 
    		throws DatabaseException, FileNotFoundException
    {
        if (highpriorities.isEmpty())
            return false;
                
        for (int i = 0; !highpriorities.isEmpty(); i++) {
            String subquery = highpriorities.pop();
            if (subquery.matches("r:[^%]*")) {
                ArrayList<Integer> next_result_indices = new ArrayList<Integer>();
            	queryRTerms(subquery, next_result_indices);
            	if (i == 0)
                    indices = next_result_indices;
            	else
                    for (Integer j : indices)
                        if (!next_result_indices.contains(j))
                            indices.remove(j);
                
            } else if (subquery.matches("p:[^%]*")) {
            	ArrayList<Integer> next_result_indices = new ArrayList<Integer>();
            	queryRTerms(subquery, next_result_indices);
            	if (i == 0)
                    indices = next_result_indices;
            	else
                    for (Integer j : indices)
                        if (!next_result_indices.contains(j))
                            indices.remove(j);
            } else if (subquery.matches(".*%")) {
                subquery = subquery.split("%")[0];
                
                // TODO:
                // TODO:
                // TODO:
                
            } else {
            	ArrayList<Integer> next_result_indices = new ArrayList<Integer>();
            	queryPTerms(subquery, next_result_indices);
            	queryRTerms(subquery, next_result_indices);
            	if (i == 0)
                    indices = next_result_indices;
            	else
            		for (Integer j : indices)
                        if (!next_result_indices.contains(j))
                            indices.remove(j);
            }
        }
        return true;
    }

    private void processRScorePriority(boolean has_high_priority) {
    	while(!rscorepriorities.isEmpty()) {
			ArrayList<Integer> tempKeys = new ArrayList<Integer>();
			String subquery = rscorepriorities.pop();
			if (subquery.matches("rscore<.*")) {
				String value = subquery.replace("rscore<", "");
				for (int n = 0; n < Integer.parseInt(value); n++) {
					
					
					
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
				}
				if (!has_high_priority && m == 0) {
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
    }
}