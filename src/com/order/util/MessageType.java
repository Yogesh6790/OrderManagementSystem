package com.order.util;

public enum MessageType {
    ADD_ORDER_REQUEST(0),
    CANCEL_ORDER(1),
    TRADE_EVENT(2),
    ORDER_FULLY_FILL(3),
    ORDER_PARTIAL_FILL(4);

    private int messageTypeVal;

    MessageType(int messageTypeVal) {
        this.messageTypeVal = messageTypeVal;
    }

    public int getMessageTypeVal() {
        return messageTypeVal;
    }
}
