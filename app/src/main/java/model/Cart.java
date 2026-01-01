package model;

public class Cart {
    private String productId;
    private String cartId;
    private String userId;
    private String title;
    private String qty;
    private String price;
    private  String category;

    private String image1;

    public Cart() {
    }

    public Cart(String productId, String cartId,String title, String userId, String qty, String price, String category, String image1) {
        this.productId = productId;
        this.cartId = cartId;
        this.qty = qty;
        this.price = price;
        this.category = category;
        this.image1 = image1;
        this.userId = userId;
        this.title=title;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }
}
