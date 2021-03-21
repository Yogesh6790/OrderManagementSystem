package com.order.service;

import com.order.vo.request.AddOrderRequest;
import com.order.vo.request.CancelOrderRequest;
import com.order.vo.request.OrderRequest;

import java.util.List;

public interface OrderBookService {

    boolean isThereAnyMatchingOrder(OrderRequest orderRequest);

    List<Object> matchOrders(AddOrderRequest orderRequest);

    void addOrderToRestingOrders(AddOrderRequest orderRequest);

    void cancelOrders(CancelOrderRequest orderRequest);

}
