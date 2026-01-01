package model;

import java.io.Serializable;

public class Product implements Serializable {

    private String productId;
    private String title;
    private  String description;
    private double price;
    private  int qty;
    private  String category;
    private  String condition;
    private  String model;
    private  String brand;
    private String image1;
    private String image2;
    private String image3;
    private  String datetime;



    public Product(String productId,String title,String description, double price, int qty,String image1,String image2,String image3,String condition,String category,String brand, String model,String datetime) {
        this.productId = productId;
        this.title = title;
        this.description=description;
        this.condition = condition;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.qty = qty;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.datetime = datetime;
    }

    public Product() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
