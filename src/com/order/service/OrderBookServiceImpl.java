package com.order.service;

import com.order.util.MessageType;
import com.order.util.OrderManagementConstants;
import com.order.vo.OrderBook;
import com.order.vo.RestingOrder;
import com.order.vo.request.AddOrderRequest;
import com.order.vo.request.CancelOrderRequest;
import com.order.vo.request.OrderRequest;
import com.order.vo.response.OrderFullyFillResponse;
import com.order.vo.response.OrderPartialFillResponse;
import com.order.vo.response.TradeEventResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/*
    This is the service which handles order book and produces messages
 */
public class OrderBookServiceImpl implements OrderBookService {


    @Override
    public boolean isThereAnyMatchingOrder(OrderRequest orderRequest) {
        boolean isMatching = OrderManagementConstants.FALSE;
        List<RestingOrder> restingOrders = OrderBook.getOrderBook().getRestingOrders();
        if(orderRequest instanceof AddOrderRequest){
            if(((AddOrderRequest) orderRequest).getSide().equals(OrderManagementConstants.SELL)){
                isMatching = restingOrders.stream().filter(order -> order.getSide().equals(OrderManagementConstants.BUY) && order.getPrice() >= ((AddOrderRequest) orderRequest).getPrice()).findAny().isPresent();
            }else{
                isMatching = restingOrders.stream().filter(order -> order.getSide().equals(OrderManagementConstants.SELL) && order.getPrice() <= ((AddOrderRequest) orderRequest).getPrice()).findAny().isPresent();
            }
        }
        return isMatching;
    }

    @Override
    public List<Object> matchOrders(AddOrderRequest orderRequest) {
        List<Object> response = new ArrayList<>();
        List<RestingOrder> fullyFilledRestingOrderIndices = new ArrayList<>();
        String orderRequestSide = orderRequest.getSide();
        long orderRequestQty = orderRequest.getQuantity();
        List<RestingOrder> restingOrdersOppSide = OrderBook.getOrderBook().getRestingOrders().stream().filter(order -> getOppositeSide(order.getSide()).equals(orderRequest.getSide())).collect(Collectors.toList());
        Comparator<RestingOrder> compareByPriceAndAge = Comparator.comparing(RestingOrder::getPrice).thenComparing(RestingOrder::getOrderId);
        List<RestingOrder> sortedRestingOrders = restingOrdersOppSide.stream().sorted(compareByPriceAndAge).collect(Collectors.toList());
        for(int i = 0; i < sortedRestingOrders.size(); i++){
            if(orderRequestQty == 0){
                //This means Aggressive order is completely matched already
                break;
            }
            RestingOrder restingOrder = sortedRestingOrders.get(i);
            if(restingOrder.getSide().equals(OrderManagementConstants.BUY)){
                if(restingOrder.getPrice() >= orderRequest.getPrice()){
                    orderRequestQty = formResponse(restingOrder, orderRequest, orderRequestQty, response, fullyFilledRestingOrderIndices, orderRequest.getPrice());
                }
            }else if(restingOrder.getSide().equals(OrderManagementConstants.SELL)){
                if(restingOrder.getPrice() <= orderRequest.getPrice()){
                    orderRequestQty = formResponse(restingOrder, orderRequest, orderRequestQty, response, fullyFilledRestingOrderIndices, restingOrder.getPrice());
                }
            }
        }
        //Removing the fully filled resting orders
        fullyFilledRestingOrderIndices.forEach(order -> {
            OrderBook.getOrderBook().getRestingOrders().remove(order);
        });
        //This is to check if we need to add this message to the resting orders list
        orderRequest.setQuantity(orderRequestQty);
        return response;
    }

    @Override
    public void addOrderToRestingOrders(AddOrderRequest orderRequest) {
        if(orderRequest.getQuantity() > 0){
            OrderBook.getOrderBook().getRestingOrders().add(new RestingOrder(orderRequest.getOrderId(), orderRequest.getSide(), orderRequest.getQuantity(), orderRequest.getPrice()));
        }
    }

    @Override
    public void cancelOrders(CancelOrderRequest orderRequest) {
        RestingOrder restingOrder = OrderBook.getOrderBook().getRestingOrders().stream().filter(order -> order.getOrderId() == orderRequest.getOrderId()).findFirst().get();
        OrderBook.getOrderBook().getRestingOrders().remove(restingOrder);
    }

    private long formResponse(RestingOrder restingOrder, AddOrderRequest orderRequest, long orderReqQty, List<Object> response, List<RestingOrder> fullyFilledRestingOrderIndices, double price){
        long orderRequestQty = orderReqQty;
        if(restingOrder.getQuantity() <= orderRequestQty){
            //Fully Fill
            response.add(new TradeEventResponse(MessageType.TRADE_EVENT.getMessageTypeVal(), restingOrder.getQuantity(), price));
            orderRequestQty -= restingOrder.getQuantity();
            if(orderRequestQty == 0){
                response.add(new OrderFullyFillResponse(MessageType.ORDER_FULLY_FILL.getMessageTypeVal(), orderRequest.getOrderId()));
            }else{
                response.add(new OrderPartialFillResponse(MessageType.ORDER_PARTIAL_FILL.getMessageTypeVal(), orderRequest.getOrderId(), orderRequestQty));
            }
            response.add(new OrderFullyFillResponse(MessageType.ORDER_FULLY_FILL.getMessageTypeVal(), restingOrder.getOrderId()));
            fullyFilledRestingOrderIndices.add(restingOrder);
        }else{
            //Partial Fill
            response.add(new TradeEventResponse(MessageType.TRADE_EVENT.getMessageTypeVal(), orderRequestQty, price));
            response.add(new OrderFullyFillResponse(MessageType.ORDER_FULLY_FILL.getMessageTypeVal(), orderRequest.getOrderId()));
            restingOrder.setQuantity(restingOrder.getQuantity() - orderRequestQty);
            response.add(new OrderPartialFillResponse(MessageType.ORDER_PARTIAL_FILL.getMessageTypeVal(), restingOrder.getOrderId(), restingOrder.getQuantity()));
            orderRequestQty = 0;
        }
        return orderRequestQty;
    }

    private String getOppositeSide(String side){
        return side.equals(OrderManagementConstants.SELL) ? OrderManagementConstants.BUY : OrderManagementConstants.SELL;
    }

}
