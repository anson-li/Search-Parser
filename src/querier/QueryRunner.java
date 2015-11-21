package querier;

import java.io.IOException;

import datastructs.Query;
import exceptions.DBMSException;

public class QueryRunner {

	public static void main(String[] args) {
	    
        DBMS dbHandler = null;    
        Query userQuery = new Query("");
        
        try {
            dbHandler = new DBMS();
        } catch (DBMSException dbmse) {
            System.out.println(dbmse.getMessage());
        }
        
        if(dbHandler == null)
            return;

        while(true) {
            
            try {
                dbHandler.requestUserQuery(userQuery);
            } catch (IOException e) {
                System.err.println("Error reading query.");
                continue;
            }
            
            
            
        }

	}
}
