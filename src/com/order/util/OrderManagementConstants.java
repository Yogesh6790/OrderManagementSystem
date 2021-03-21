package com.order.util;

public class OrderManagementConstants {

    public static final String BUY = "BUY";

    public static final String SELL = "SELL";

    public static final boolean TRUE = true;

    public static final boolean FALSE = false;

    public static final String COMMA = ", ";

    //Error Messages

    public static final String ERROR_MESSAGE_HEADER = "Error Message : ";

    public static final String INVALID_FORMAT_MSG = "Error Message : Please make sure the format of ADD ORDER is <msgtype>,<orderid>,<side>,<quantity>,<price> and CANCEL ORDER is <msgtype>,<orderid>";

    public static final String INVALID_VALUE_IN_MSG = "Error Message : Please make sure the values in the message are numbers";

    public static final String ADD_ORDER_INVALID_MSG_TYPE = "Msg type of the Add order should be 0";

    public static final String ADD_ORDER_INVALID_SIDE = "Bull/Sell side of the Add order should be 0 or 1";

    public static final String ADD_ORDER_INVALID_ORDERID = "Order ID of the Add order should be +ve";

    public static final String ADD_ORDER_ORDERID_EXISTS_ALREADY = "Invalid Order ID: This order id already exists. Cannot add this order id!";

    public static final String ADD_ORDER_INVALID_QUANTITY= "Quantity of the Add order should be +ve";

    public static final String ADD_ORDER_INVALID_PRICE = "Price of the Add order should be +ve";

    public static final String CANCEL_ORDER_INVALID_MSG_TYPE = "Msg type of the Cancel order should be 1";

    public static final String CANCEL_ORDER_INVALID_ORDERID = "Order ID of the Cancel order should be +ve";

    public static final String CANCEL_ORDER_ORDERID_NOT_EXISTS_ALREADY = "Invalid Order ID: This order id is not available to cancel";





}
