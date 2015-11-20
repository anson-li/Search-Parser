import com.sleepycat.db.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.*;
import java.lang.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DBMS {

    private Query userQuery;

    DBMS() {
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
}
