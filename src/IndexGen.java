import com.sleepycat.db.*;
import java.io.*;
import java.util.regex.*;
import java.lang.*;

public class IndexGen{

public static void main(String[] args){
	// have to execute everything through the following process:
	ExecuteShellCommand shell = new ExecuteShellCommand();
	shell.executeCommand("sort -u -o rterms.txt rterms.txt");
	shell.executeCommand("sort -u -o pterms.txt pterms.txt");
	shell.executeCommand("sort -u -o scores.txt scores.txt");

}
}
