package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import datastructs.Product;
import datastructs.Review;


public class DataFileGenerator {
    
    private BufferedReader stdinReader;
    
    private PrintWriter reviewsWriter;
    private PrintWriter ptermsWriter;
    private PrintWriter rtermsWriter;
    private PrintWriter scoresWriter;
    
    private Review      review;
    private Product     product;
    private Data        data;
    
    private String      nextLine;
    
    DataFileGenerator() {
        review = new Review();
        product = new Product();
        stdinReader = new BufferedReader(new InputStreamReader(System.in));
        nextLine = "";
        data = new Data();
    }
    
    private static class Data {
        public enum   FIELD { PRODUCT_ID,
                              PRODUCT_TITLE,
                              PRODUCT_PRICE,
                              REVIEW_USERID,
                              REVIEW_PROFILENAME,
                              REVIEW_HELPFUL,
                              REVIEW_SCORE,
                              REVIEW_TIME,
                              REVIEW_SUMMARY,
                              REVIEW_TEXT,
                              NEW_LINE }
        
        public String productId;
        public String productTitle;
        public String productPrice;
        public String reviewUserId;
        public String reviewProfileName;
        public String reviewHelpful;
        public String reviewScore;
        public String reviewTime;
        public String reviewSummary;
        public String reviewText;
        
        Data() {
            productId         = "product/productId: ";
            productTitle      = "product/title: ";
            productPrice      = "product/price: ";
            reviewUserId      = "review/userId: ";
            reviewProfileName = "review/profileName: ";
            reviewHelpful    = "review/helpfulness: ";
            reviewScore       = "review/score: ";
            reviewTime        = "review/time: ";
            reviewSummary     = "review/summary: ";
            reviewText        = "review/text: ";
        }
        
        public FIELD getField(String line) {
            if (line.matches(productId + ".*"))
                return FIELD.PRODUCT_ID;
                
            if (line.matches(productTitle + ".*"))
                return FIELD.PRODUCT_TITLE;
            
            if (line.matches(productPrice + ".*"))
                return FIELD.PRODUCT_PRICE;
            
            if (line.matches(reviewUserId + ".*"))
                return FIELD.REVIEW_USERID;
            
            if (line.matches(reviewProfileName + ".*"))
                return FIELD.REVIEW_PROFILENAME;
            
            if (line.matches(reviewHelpful + ".*"))
                return FIELD.REVIEW_HELPFUL;
            
            if (line.matches(reviewScore + ".*"))
                return FIELD.REVIEW_SCORE;
              
            if (line.matches(reviewTime + ".*"))
                return FIELD.REVIEW_TIME;
              
            if (line.matches(reviewSummary + ".*"))
                return FIELD.REVIEW_SUMMARY;
            
            if (line.matches(reviewText + ".*"))
                return FIELD.REVIEW_TEXT;
            
            if (line.matches(""))
                return FIELD.NEW_LINE;
            
            return null;
        }
    }
    
    public String[] getFileNames() {
        String [] fnames = { "./reviews.txt",
                             "./pterms.txt",
                             "./rterms.txt",
                             "./scores.txt" };
        return fnames;
    }

    public void createFiles(String[] fileNames) 
           throws FileNotFoundException, UnsupportedEncodingException
    {
        reviewsWriter = new PrintWriter(new File(fileNames[0]), "UTF-8");
        ptermsWriter  = new PrintWriter(new File(fileNames[1]), "UTF-8");
        rtermsWriter  = new PrintWriter(new File(fileNames[2]), "UTF-8");
        scoresWriter  = new PrintWriter(new File(fileNames[3]), "UTF-8");
    }
    
    public void processReviews() {
        for(int reviewNum = 1; grabNextLine();) {
            try {
                if(assignToField())
                    continue;
                else {
                    writeReview(reviewNum);
                    writeRTerms(reviewNum);
                    writePTerms(reviewNum);
                    writeRScore(reviewNum);
                    reviewNum += 1;
                }
            } catch (IllegalArgumentException iae) {
                System.err.println("Error at review " + reviewNum);
                System.err.println(iae.getMessage());
                return;
            }
        }
    }
    
    private void writeReview(int reviewNum) {
        reviewsWriter.println(reviewNum                + "," 
                             + product.getID()          + ",\""
                             + product.getTitle()       + "\","
                             + product.getPrice()       + ","
                             + review.getUserID()       + ",\""
                             + review.getProfileName()  + "\","
                             + review.getHelpfulness()  + ","
                             + review.getScore()        + ","
                             + review.getTime()         + ",\""
                             + review.getSummary()      + "\",\""
                             + review.getText()         + "\"");
    }
    
    private void writeRTerms(int reviewNum) {
        for (String word : getWords(review.getSummary()))
            if (word.length() >= 3)
                rtermsWriter.println(word.toLowerCase() + "," + reviewNum);
        
        for (String word : getWords(review.getText()))
            if (word.length() >= 3)
                rtermsWriter.println(word.toLowerCase() + "," + reviewNum);
    }
    
    private String[] getWords(String s) {
        return s.split("[^0-9a-zA-Z_]");
    }
    
    private void writePTerms(int reviewNum) {
        for (String word : getWords(product.getTitle()))
            if (word.length() >= 3)
                ptermsWriter.println(word.toLowerCase() + "," + reviewNum);
    }
    
    private void writeRScore(int reviewNum) {
        scoresWriter.println(review.getScore() + "," + reviewNum);
    }
    
    private boolean assignToField() throws IllegalArgumentException {
        switch (data.getField(nextLine)) {
        case PRODUCT_ID:
            product.setID(nextLine.replace(data.productId, ""));
            review.setProductID(nextLine.replace(data.productId, ""));
            return true;
            
        case PRODUCT_TITLE:
            product.setTitle(nextLine.replace(data.productTitle, ""));
            return true;
            
        case PRODUCT_PRICE:
            product.setPrice(nextLine.replace(data.productPrice, ""));
            return true;
            
        case REVIEW_USERID:
            review.setUserID(nextLine.replace(data.reviewUserId, ""));
            return true;
             
        case REVIEW_PROFILENAME:
            review.setProfileName(nextLine.replace(data.reviewProfileName, ""));
            return true;
             
        case REVIEW_HELPFUL:
            review.setHelpfulness(nextLine.replace(data.reviewHelpful, ""));
            return true;
             
        case REVIEW_SCORE:
            review.setScore(Double.valueOf(nextLine.replace(data.reviewScore, "")));
            return true;
             
        case REVIEW_TIME:
            review.setTime(nextLine.replace(data.reviewTime, ""));
            return true;
             
        case REVIEW_SUMMARY:
            review.setSummary(nextLine.replace(data.reviewSummary, ""));
            return true;
             
        case REVIEW_TEXT:
            review.setText(nextLine.replace(data.reviewText, ""));
            return true;
            
        case NEW_LINE:
            return false;
            
        default:
            throw new IllegalArgumentException("Erroneous Input: " + nextLine);
        }
    }
    
    private boolean grabNextLine() {
        try {
            if ((nextLine = stdinReader.readLine()) == null) {
                nextLine = "";
                return false;
            } else {
                nextLine = nextLine.replaceAll("\"", "&quot;")
                                   .replaceAll("\\\\","\\\\\\\\");
                return true;
            }
        } catch (IOException e) {
            System.err.println("I/O Error in reading from input.");
            nextLine = "";
            return false;
        }
    }
    
    public void close() {
        reviewsWriter.close();
        rtermsWriter.close();
        ptermsWriter.close();
        scoresWriter.close();
    }

    public static void main(String[] args) {
        DataFileGenerator dfg = new DataFileGenerator();
        
        try {
            dfg.createFiles(dfg.getFileNames());
        } catch (FileNotFoundException fnfe) {
            System.err.println("Unable to open the data files for writing.");
            return;
        } catch (UnsupportedEncodingException usee) {
            System.err.println("Character encoding UTF-8 unsupported.");
            return;
        }
        
        dfg.processReviews();
        dfg.close();
    }
}
