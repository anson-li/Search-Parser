//package c291proj2;

import com.sleepycat.db.*;

/*
Intended table design:

create table REVIEW {
    userId CHAR[15],
    productId CHAR[15],
    profileName CHAR[50],
    helpfulness CHAR[10],
    score DOUBLE,
    time DATETIME,
    summary CHAR[50],
    text CHAR[500],
    PRIMARY KEY (userId, productId),
    FOREIGN KEY(productId) ON DELETE CASCADE
}
*/

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
    	System.out.println("Reviewer User Id: "+ getUserID().replace("&quot;","\"").replace("\\\\\\\\", "\\"));
    	System.out.println("Review Product Id: "+ getProductID().replace("&quot;","\"").replace("\\\\\\\\", "\\"));
    	System.out.println("Reviewer Profile Name: "+ getProfileName().replace("&quot;","\"").replace("\\\\\\\\", "\\"));
    	System.out.println("Review Helpfulness: "+ getHelpfulness().replace("&quot;","\"").replace("\\\\\\\\", "\\"));
    	System.out.println("Review Score: "+ getScore());
    	System.out.println("Review Time: "+ getTime().replace("&quot;","\"").replace("\\\\\\\\", "\\"));
    	System.out.println("Review Summary: "+ getSummary().replace("&quot;","\"").replace("\\\\\\\\", "\\"));
    	System.out.println("Review Text: "+ getText().replace("&quot;","\"").replace("\\\\\\\\", "\\"));
    }

    public String getUserID() { return this.userId; }
    public String getProductID() { return this.productId; }
    public String getProfileName() { return this.profileName; }
    public String getHelpfulness() { return this.helpfulness; }
    public Double getScore() { return this.score; }
    public String getTime() { return this.time; }
    public String getSummary() { return this.summary; }
    public String getText() { return this.text; }

} 