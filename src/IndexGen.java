import com.sleepycat.db.*;
import java.io.*;
import java.util.regex.*;
import java.lang.*;

public class IndexGen{

	public static void main(String[] args){
		// have to execute everything through the following process:
		executeCommand("sort -u -o rterms.txt rterms.txt");
		executeCommand("sort -u -o pterms.txt pterms.txt");
		executeCommand("sort -u -o scores.txt scores.txt");

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
