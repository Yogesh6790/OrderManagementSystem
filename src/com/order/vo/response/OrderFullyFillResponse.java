package com.order.vo.response;

import com.order.util.MessageType;

public class OrderFullyFillResponse extends OrderResponse{

    private long orderId;

    public OrderFullyFillResponse(int messageType, long orderId) {
        super(messageType);
        this.orderId = orderId;
    }

    @Override
    public String toString(){
        return MessageType.ORDER_FULLY_FILL.getMessageTypeVal() + "," + orderId;
    }
}
