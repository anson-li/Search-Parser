
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class datafiles {
  
  private class Review{
    
  }
  
  private class Pterm{
    
  }
  
  private class Rterm{
    
  }
  
  private class Scores{
    
  }
  
  private static class Fields {
    public String productId = "product/productId: .*";
    public String productTitle = "product/title: .*";
    public String productPrice = "product/price: .*";
    public String reviewUserId = "review/userId: .*";
    public String reviewProfileName = "review/profileName: .*";
    public String reviewHelpfull = "reviewHelpfulness: .*";
    public String reviewScore = "review/score: .*";
    public String reviewTime = "review/time: .*";
    public String reviewSummary = "review/summary: .*";
    public String reviewText = "review/text: .*";
  }
  
  private boolean processLine(String line) {
    
  }
  
  public static void main(String[] args)
  {
    Scanner scan = new Scanner(System.in);
    
    while (scan.hasNextLine())
    {
      String a = scan.nextLine().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\");
      
      // REGEXES
      String productID = "product/productId: .*";
      
      Pattern p = Pattern.compile(productID);
      Matcher m = p.matcher(a);
      
      System.out.println(m.matches());
    }
    
    
    scan.close();
  }

}
