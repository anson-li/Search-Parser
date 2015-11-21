package querier;
//import com.sleepycat.db.*;

import datastructs.Query;
import exceptions.DBMSException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class DBMS {

    private String reviewsIndex = "rw.idx";
    private String rtermsIndex  = "rt.idx";
    private String ptermsIndex  = "pt.idx";
    private String rscoreIndex  = "sc.idx";
    
    BufferedReader buffer;
    
    DBMS() throws DBMSException {
        buffer = new BufferedReader(new InputStreamReader(System.in));
        verifyDB();
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

    public void loadSubqueryPriorityQ(Query query) {
        if (!query.isValid())
            return; // TODO: throw an exception.

        for (String subquery : query.getQuery().split(" "))
        {

        }
    }
}
