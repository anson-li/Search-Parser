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
        userId = data;
    }

    public void setProductID(String data) {
        productId = data;
    }

    public void setProfileName(String data) {
        profileName = data;
    }

    public void setHelpfulness(String data) {
        helpfulness = data;
    }

    public void setScore(Double data) {
        score = data;
    }

    public void setTime(String data) {
        time = data;
    }

    public void setSummary(String data) {
        summary = data;
    }

    public void setText(String data) {
        text = data;
    }

    public String getUserID() { return userId; }
    public String getProductID() { return productId; }
    public String getProfileName() { return profileName; }
    public String getHelpfulness() { return helpfulness; }
    public Double getScore() { return score; }
    public String getTime() { return time; }
    public String getSummary() { return summary; }
    public String getText() { return text; }

} 