package datastructs;

public class Review {

    private String userId;
    private String productId;
    private String profileName;
    private String helpfulness;
    private Double score;
    private String time;
    private String summary;
    private String text;

    public void setUserID(String data) {
        this.userId = data;
    }

    public void setProductID(String data) {
        this.productId = data;
    }

    public void setProfileName(String data) {
        this.profileName = data;
    }

    public void setHelpfulness(String data) {
        this.helpfulness = data;
    }

    public void setScore(Double data) {
        this.score = data;
    }

    public void setTime(String data) {
        this.time = data;
    }

    public void setSummary(String data) {
        this.summary = data;
    }

    public void setText(String data) {
        this.text = data;
    }
    
    public void print() {
        System.out.println("+=-=-=-=-=-=+ REVIEW +=-=-=-=-=-=-=+");
    	System.out.println("Reviewer User Id: "      + readable(getUserID()));
    	System.out.println("Review Product Id: "     + readable(getProductID()));
    	System.out.println("Reviewer Profile Name: " + readable(getProfileName()));
    	System.out.println("Review Helpfulness: "    + readable(getHelpfulness()));
    	System.out.println("Review Score: "          + getScore());
    	System.out.println("Review Time: "           + readable(getTime()));
    	System.out.println("Review Summary: "        + readable(getSummary()));
    	System.out.println("Review Text: "           + readable(getText()));
        System.out.println("+=-=-=-=-=-=-=-=-==-=-=-=-=-=-=-=-=+");

    }

    private String readable(String nonfriendly) {
        return nonfriendly.replace("&quot;", "\"")
                          .replace("\\\\\\\\", "\\");
    }

    public String getUserID()       { return this.userId; }
    public String getProductID()    { return this.productId; }
    public String getProfileName()  { return this.profileName; }
    public String getHelpfulness()  { return this.helpfulness; }
    public Double getScore()        { return this.score; }
    public String getTime()         { return this.time; }
    public String getSummary()      { return this.summary; }
    public String getText()         { return this.text; }
} 
