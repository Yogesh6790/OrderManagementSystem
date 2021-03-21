package com.order.vo;

import java.util.ArrayList;
import java.util.List;

/*
    Keeping OrderBook as Singleton class
    We will have only one instance of this in the application.
    This is essential since we should not have multiple copies of orderbook
 */
public class OrderBook {

    private static OrderBook orderBook = null;

    private List<RestingOrder> restingOrders;

    private OrderBook(){
        restingOrders = new ArrayList<>();
    }

    public static OrderBook getOrderBook(){
        if(orderBook == null){
            orderBook = new OrderBook();
        }
        return orderBook;
    }

    public List<RestingOrder> getRestingOrders() {
        return this.restingOrders;
    }

    public void setRestingOrders(List<RestingOrder> restingOrders) {
        this.restingOrders = restingOrders;
    }



}
