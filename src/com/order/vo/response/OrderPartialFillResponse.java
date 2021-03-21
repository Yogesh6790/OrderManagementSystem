package com.order.vo.response;

import com.order.util.MessageType;

public class OrderPartialFillResponse extends OrderResponse{

    private long orderId;

    private long quantity;

    public OrderPartialFillResponse(int messageType, long orderId, long quantity) {
        super(messageType);
        this.orderId = orderId;
        this.quantity = quantity;
    }

    @Override
    public String toString(){
        return MessageType.ORDER_PARTIAL_FILL.getMessageTypeVal() + "," + orderId + "," + quantity;
    }


}
