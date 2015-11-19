//package c291proj2;

import com.sleepycat.db.*;

/*
Intended table design:

create table PRODUCT {
    productId CHAR[15],
    title CHAR[150],
    price INT,
    PRIMARY KEY (productId)
}
*/

public class Product {

    private String productId;
    private String title;
    private String price;

    public void setID(String data) {
            this.productId = data;
    }

    public void setTitle(String data) {
            this.title = data;
    }

    public void setPrice(String data) {
            this.price = data;
    }
    
    public void print() {
    	System.out.println("Product ID: " + getID().replace("&quot;","\"").replace("\\\\\\\\", "\\"));
    	System.out.println("Product Title: "+ getTitle().replace("&quot;","\"").replace("\\\\\\\\", "\\"));
    	System.out.println("Product Price: "+ getPrice().replace("&quot;","\"").replace("\\\\\\\\", "\\"));
    }

    public String getID() { return this.productId; }
    public String getTitle() { return this.title; }
    public String getPrice() { return this.price; }

} 