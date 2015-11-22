package datastructs;

public class Product {

    private String productId;
    private String title;
    private String price;

    public void setID(String data) {
        productId = data;
    }

    public void setTitle(String data) {
        title = data;
    }

    public void setPrice(String data) {
        price = data;
    }
    
    public void print() {
        System.out.println("\n+=-=-=-=-=-=+ PRODUCT +-=-=-=-=-=-=+");
    	System.out.println("Product ID: "    + readable(getID()));
    	System.out.println("Product Title: " + readable(getTitle()));
    	System.out.println("Product Price: " + readable(getPrice()));
    }

    private String readable(String unfriendly) {
        return unfriendly.replace("&quot;", "\"")
                         .replace("\\\\\\\\", "\\");
    }

    public String getID()    { return productId; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
} 
