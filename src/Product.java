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
    private Double price;

    public void setID(String data) {
            productId = data;
    }

    public void setTitle(String data) {
            title = data;
    }

    public void setPrice(Double data) {
            price = data;
    }

    public String getID() { return productId; }
    public String getTitle() { return title; }
    public int getPrice() { return price; }

} 