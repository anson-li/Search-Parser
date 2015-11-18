
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class datafiles {
  
  private static Product product;
  private static Review review;
  
  datafiles() {
    product = new Product();
    review = new Review();
  }
  
  private enum FIELDS {PRODUCT_ID,
                       PRODUCT_TITLE,
                       PRODUCT_PRICE,
                       REVIEW_USERID,
                       REVIEW_PROFILENAME,
                       REVIEW_HELPFULL,
                       REVIEW_SCORE,
                       REVIEW_TIME,
                       REVIEW_SUMMARY,
                       REVIEW_TEXT}
  
  private static class data_fields {
    public static String productId = "product/productId: ";
    public static String productTitle = "product/title: ";
    public static String productPrice = "product/price: ";
    public static String reviewUserId = "review/userId: ";
    public static String reviewProfileName = "review/profileName: ";
    public static String reviewHelpfull = "review/helpfulness: ";
    public static String reviewScore = "review/score: ";
    public static String reviewTime = "review/time: ";
    public static String reviewSummary = "review/summary: ";
    public static String reviewText = "review/text: ";
  }
  
  private boolean processLine(String line) {
    return store_data(line);
  }
  
  private static FIELDS getField(String line)
  {
    if (line.matches(data_fields.productId + ".*"))
    {
      return FIELDS.PRODUCT_ID;
    }
        
    if (line.matches(data_fields.productTitle + ".*"))
    {
      return FIELDS.PRODUCT_TITLE;
    }
    
    if (line.matches(data_fields.productPrice + ".*"))
    {
      return FIELDS.PRODUCT_PRICE;
    }
    
    if (line.matches(data_fields.reviewUserId + ".*"))
    {
      return FIELDS.REVIEW_USERID;
    }
    
    if (line.matches(data_fields.reviewProfileName + ".*"))
    {
      return FIELDS.REVIEW_PROFILENAME;
    }
    
    if (line.matches(data_fields.reviewHelpfull + ".*"))
    {
      return FIELDS.REVIEW_HELPFULL;
    }
    
    if (line.matches(data_fields.reviewScore + ".*"))
    {
      return FIELDS.REVIEW_SCORE;
    }
      
    if (line.matches(data_fields.reviewTime + ".*"))
    {
      return FIELDS.REVIEW_TIME;
    }
      
    if (line.matches(data_fields.reviewSummary + ".*"))
    {
      return FIELDS.REVIEW_SUMMARY;
    }
    
    if (line.matches(data_fields.reviewText + ".*"))
    {
      return FIELDS.REVIEW_TEXT;
    }
    
    return null;
  }
  
  private static boolean store_data(String line)
  {
    switch (getField(line)) {
    case PRODUCT_ID:
      line = line.replace(data_fields.productId, "");
      product.setID(line);
      review.setProductID(line);
      return true;
      
    case PRODUCT_TITLE:
      line = line.replace(data_fields.productTitle, "");
      product.setTitle(line);
      return true;
      
    case PRODUCT_PRICE:
      line = line.replace(data_fields.productPrice, "");
      product.setPrice(line);
      return true;
      
    case REVIEW_USERID:
      line = line.replace(data_fields.reviewUserId, "");
      review.setUserID(line);
      return true;
       
    case REVIEW_PROFILENAME:
      line = line.replace(data_fields.reviewProfileName, "");
      review.setProfileName(line);
      return true;
       
    case REVIEW_HELPFULL:
      line = line.replace(data_fields.reviewHelpfull, "");
      review.setHelpfulness(line);
      return true;
       
    case REVIEW_SCORE:
      line = line.replace(data_fields.reviewScore, "");
      review.setScore(Double.valueOf(line));
      return true;
       
    case REVIEW_TIME:
      line = line.replace(data_fields.reviewTime, "");
      review.setTime(line);
      return true;
       
    case REVIEW_SUMMARY:
      line = line.replace(data_fields.reviewSummary, "");
      review.setSummary(line);
      return true;
       
    case REVIEW_TEXT:
      line = line.replace(data_fields.reviewText, "");
      review.setText(line);
      return true;
       
    default:
      return false;
       
    }
  }
  
  public static void main(String[] args)
  {
    new datafiles();
    Scanner scan = new Scanner(System.in);
    
    while (scan.hasNextLine())
    {
      String a = scan.nextLine().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\");
      
      if (a.equals(""))
        continue;
      
      try {
        store_data(a);
      } catch (Exception e)
      {
        scan.close();
        return;
      }
    }
        
    scan.close();
  }

}
