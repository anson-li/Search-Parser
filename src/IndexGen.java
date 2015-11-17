import com.sleepycat.db.*;
import java.io.*;
import java.util.regex.*;
import java.lang.*;

public class IndexGen{

	/* Test .idx files:
	   db_dump rw.idx -da
	*/
	public static void main(String[] args){
		// Sorts text file and puts it on stdin / perl sorts / db_load parses into hash
		IndexGen shell = new IndexGen();
		shell.executeCommand("sort -u reviews.txt | perl break.pl | db_load -c duplicates=1 rw.idx -T -t hash");
		printf("RW.IDX : Process complete!");

		shell.executeCommand("sort -u pterms.txt | perl break.pl | db_load -c duplicates=1 pt.idx -T -t btree");
		printf("PT.IDX : Process complete!");

		shell.executeCommand("sort -u rterms.txt | perl break.pl | db_load -c duplicates=1 rt.idx -T -t btree");
		printf("RT.IDX : Process complete!");

		shell.executeCommand("sort -u scores.txt | perl break.pl | db_load -c duplicates=1 sc.idx -T -t btree");	
		printf("SC.IDX : Process complete!");
	}

	//http://www.mkyong.com/java/how-to-execute-shell-command-from-java/
	private String executeCommand(String command) {
		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}
}
