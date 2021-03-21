package com.order.vo;

import com.order.vo.request.AddOrderRequest;

public class RestingOrder extends AddOrderRequest {
    public RestingOrder(long orderId, String side, long quantity, double price) {
        super(orderId, side, quantity, price);
    }
}
