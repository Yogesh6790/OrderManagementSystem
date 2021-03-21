package com.order.vo.request;

public class OrderRequest {

    private long orderId;

    public OrderRequest(long orderId){
        this.orderId = orderId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

}
