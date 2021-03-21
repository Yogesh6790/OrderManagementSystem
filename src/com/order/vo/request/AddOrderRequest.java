package com.order.vo.request;

public class AddOrderRequest extends OrderRequest {

    private String side;
    private long quantity;
    private double price;

    public AddOrderRequest(long orderId, String side, long quantity, double price){
        super(orderId);
        this.side = side;
        this.quantity = quantity;
        this.price = price;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
