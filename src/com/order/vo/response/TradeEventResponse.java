package com.order.vo.response;

import com.order.util.MessageType;

public class TradeEventResponse extends OrderResponse{

    private long quantity;

    private double price;

    public TradeEventResponse(int messageType, long quantity, double price) {
        super(messageType);
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String toString(){
        return MessageType.TRADE_EVENT.getMessageTypeVal() + "," + quantity + "," + price;
    }
}
