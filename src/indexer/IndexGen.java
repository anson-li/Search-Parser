package indexer;
import java.io.*;

public class IndexGen{

	/* Test .idx files:
	   db_dump rw.idx -da
	*/
	public static void main(String[] args){
		// Sorts text file and puts it on stdin / perl sorts / db_load parses into hash
		IndexGen shell = new IndexGen();
		/**
		* Simple breakdown of shell command:
		* cat grabs all the text files be used and pipes their values
		* sort, if used, sorts the values by (for 2nd and 3rd) the first 
		* column first, and by the 2nd column second. n denotes number.
		* perl break.pl converts entries to use in db.
		* db_load processes all entered values and inputs into .idx files.
		*/
		shell.executeCommand("cat reviews.txt | perl break.pl | db_load -c duplicates=1 rw.idx -T -t hash");
		shell.executeCommand("cat pterms.txt | sort -t, -k 1,1 -k 2,2n -u | perl break.pl | db_load -c duplicates=1 pt.idx -T -t btree");
		shell.executeCommand("cat rterms.txt | sort -t, -k 1,1 -k 2,2n -u | perl break.pl | db_load -c duplicates=1 rt.idx -T -t btree");
		shell.executeCommand("cat scores.txt | sort -t, -k 1,1n -k 2,2n -u | perl break.pl | db_load -c duplicates=1 sc.idx -T -t btree");	
	}

	/**
	* Executes shell commands in Java
	* Referenced http://www.mkyong.com/java/how-to-execute-shell-command-from-java/
	* @param command holds the bash command to process
	* @return output if there is any. Used in DBQuery to process regex queries faster.
	*/
	public String executeCommand(String command) {
		String[] cmd = {"/bin/sh", "-c", command}; // uses STDIN to execute commands
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			p = Runtime.getRuntime().exec(cmd);	// implements runtime command 
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
