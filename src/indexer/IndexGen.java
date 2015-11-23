package indexer;
import java.io.*;

public class IndexGen{

	/* Test .idx files:
	   db_dump rw.idx -da
	*/
	public static void main(String[] args){
		// Sorts text file and puts it on stdin / perl sorts / db_load parses into hash
		IndexGen shell = new IndexGen();
		shell.executeCommand("cat reviews.txt | perl break.pl | db_load -c duplicates=1 rw.idx -T -t hash");
		shell.executeCommand("cat pterms.txt | sort -t, -k 1,1 -k 2,2n -u | perl break.pl | db_load -c duplicates=1 pt.idx -T -t btree");
		shell.executeCommand("cat rterms.txt | sort -t, -k 1,1 -k 2,2n -u | perl break.pl | db_load -c duplicates=1 rt.idx -T -t btree");
		shell.executeCommand("cat scores.txt | sort -t, -k 1,1n -k 2,2n -u | perl break.pl | db_load -c duplicates=1 sc.idx -T -t btree");	
	}

	//http://www.mkyong.com/java/how-to-execute-shell-command-from-java/
	public String executeCommand(String command) {
		String[] cmd = {"/bin/sh", "-c", command};
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			p = Runtime.getRuntime().exec(cmd);
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
