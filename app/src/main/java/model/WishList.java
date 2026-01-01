package model;

public class WishList {
    private String productId;
    private int wishlistId;

    private String title;
    private int qty;
    private double price;

    private String image1;

    public WishList(String productId, int wishlistId, String title, int qty, double price, String image1) {
        this.productId = productId;
        this.wishlistId = wishlistId;
        this.title = title;
        this.qty = qty;
        this.price = price;
        this.image1 = image1;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }
}

