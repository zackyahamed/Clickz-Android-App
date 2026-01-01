package com.example.clickz;

public class CartItem {
    private String productId;
    private String title;
    private int qty;
    private double price;
    private String image;
    private int availableqty;
    private double totalPrice;


    public CartItem(String productId,String title, int qty, double price, String image,int availableqty) {
        this.productId=productId;
        this.title = title;
        this.qty = qty;
        this.price = price;
        this.image = image;
        this.availableqty = availableqty;
    }

    public CartItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getAvailableqty() {
        return availableqty;
    }

    public void setAvailableqty(int availableqty) {
        this.availableqty = availableqty;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
