package querier;
//import com.sleepycat.db.*;

import Cursor;
import Database;
import DatabaseEntry;
import MultipleKeyDataEntry;
import OperationStatus;
import datastructs.GenericStack;
import datastructs.Query;
import exceptions.DBMSException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import DBQuery.StringEntry;

public class DBMS {

    private String reviewsIndex ;
    private String rtermsIndex  ;
    private String ptermsIndex  ;
    private String rscoreIndex  ;
    private BufferedReader buffer;
    private GenericStack<String> lowpriorities;
    private GenericStack<String> highpriorities;
    private GenericStack<String> rscorepriorities;
    
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
        
        processHighPriorities();
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
    
    private void 
    
    private boolean processHighPriorities() {
        if (highpriorities.isEmpty())
            return false;
                
        for (int i = 0; !highpriorities.isEmpty(); i++) {
            String kappa = highpriorities.pop();
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
            } else if (kappa.matches("%.*") || kappa.matches(".*%")) {
                // Acquire a cursor for the table.
                try {
                    ArrayList<String> list = new ArrayList<String>();
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
                    bulk_data2.setData(new byte[1024 * 30000]); // how to setData?
                    bulk_data2.setUserBuffer(1024 * 30000, true);

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

                    ArrayList<String> matches = new ArrayList<String>();
                    Pattern p = Pattern.compile("(?i:" + kappa.replace("%", ".*") + ")");
                    for (String s:list) {
                        if (p.matcher(s).matches()) {
                            matches.add(s);
                        }
                    }

                    ArrayList<Integer> tempKeys = new ArrayList<Integer>();
                    for (String val : matches) {
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
                    std_db2.close();
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
    }
}