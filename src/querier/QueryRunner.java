package querier;

public class QueryRunner {

	public static void main(String[] args) {
	    
        DBMS dbHandler;    
        try {
            dbHandler = new DBMS();
        } catch (DBMSException dbmse) {
            System.out.println(dbmse.getMessage());
        }
        
        if(dbHandler == null)
            return;

        while(true) {
            dbHandler.requestUserQuery();
            
        }

	}
}
