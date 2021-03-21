package com.order.application;

import com.order.service.OrderBookService;
import com.order.service.OrderBookServiceImpl;
import com.order.util.MessageType;
import com.order.util.OrderManagementConstants;
import com.order.vo.OrderBook;
import com.order.vo.request.AddOrderRequest;
import com.order.vo.request.CancelOrderRequest;
import com.order.vo.request.OrderRequest;
import com.order.vo.response.OrderFullyFillResponse;
import com.order.vo.response.OrderPartialFillResponse;
import com.order.vo.response.TradeEventResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
 * There are several test input files in com.order.resources (package)
 * This application produces different response for each file
 * Pls paste the content in each file as input to check the response for each file
 *
 * NOTE:
 * 1. The Price in trade event message in the output is displayed in double format(to be accurate)
 * 2. Everytime the OrderManagementApplication is run, the order book would be reset
 *
 * ASSUMPTIONS:
 * I've made the following assumptions while designing this application
 * 1. Order Id, Quantity will not be more than 9,223,372,036,854,775,807(Max Value of Long data type)
 * 2. Price will not be more than 1797693134862315.7E293 (Max value of double)
 * 3. Order Id represent the timestamp indirectly(lower order id means a old order, for ex: order id = 104 is older when compared to order id = 107)
 *
 */
public class OrderManagementApplication {


    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        String message = null;
        OrderBookService orderBookService = new OrderBookServiceImpl();
        while((message = in.nextLine()) != null && !message.trim().equals("")){
            //Removing whitespace in the message
            message = message.replace(" ","");
            String[] messageArr = message.split(",");
            //Validation
            try{
                validateInputMessage(messageArr);
            }catch(Exception e){
                System.err.println(e.getMessage());
                continue;
            }


            OrderRequest orderRequest = null;
            if(Integer.parseInt(messageArr[0]) == MessageType.ADD_ORDER_REQUEST.getMessageTypeVal()){
                orderRequest = new AddOrderRequest(Long.valueOf(messageArr[1]), messageArr[2].equals("0") ? OrderManagementConstants.BUY : OrderManagementConstants.SELL,
                        Long.valueOf(messageArr[3]), Double.valueOf(messageArr[4]));
                if(orderBookService.isThereAnyMatchingOrder(orderRequest)){
                    //Match orders and update orders in the order Book
                    List<Object> responses = orderBookService.matchOrders((AddOrderRequest) orderRequest);
                    //Add Output Message for Trade Event and Order Partial Fill and Fully Fill message
                    logTradeAndOrderMessages(responses);
                }
                //Add order to the resting orders in the order book (if applicable)
                orderBookService.addOrderToRestingOrders((AddOrderRequest) orderRequest);
            }else{
                orderRequest = new CancelOrderRequest(Long.valueOf(messageArr[1]));
                //Remove order in the order book
                orderBookService.cancelOrders((CancelOrderRequest) orderRequest);
            }

        }
    }

    private static void logTradeAndOrderMessages(List<Object> response){
        response.forEach(object -> {
            if(object instanceof TradeEventResponse){
                System.out.println((TradeEventResponse)object);
            }else if(object instanceof OrderFullyFillResponse){
                System.out.println((OrderFullyFillResponse)object);
            }else{
                System.out.println((OrderPartialFillResponse) object);
            }
        });
    }

    private static void validateInputMessage(String[] arr) throws Exception {
        if(arr.length != 2 && arr.length != 5){
            throw new Exception(OrderManagementConstants.INVALID_FORMAT_MSG);
        }

        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        for(String elem : arr){
            if(arr == null || !pattern.matcher(elem).matches()){
                throw new Exception(OrderManagementConstants.INVALID_VALUE_IN_MSG);
            }
        }

        List<String> errorMessages = new ArrayList<>();
        if(arr.length == 5){
            if(!arr[0].equals("0")){
                errorMessages.add(OrderManagementConstants.ADD_ORDER_INVALID_MSG_TYPE);
            }
            if(Long.parseLong(arr[1]) <= 0){
                errorMessages.add(OrderManagementConstants.ADD_ORDER_INVALID_ORDERID);
            }
            if(OrderBook.getOrderBook().getRestingOrders().stream().filter(order -> order.getOrderId() == Long.parseLong(arr[1])).findAny().isPresent()){
                errorMessages.add(OrderManagementConstants.ADD_ORDER_ORDERID_EXISTS_ALREADY);
            }
            if(!arr[2].equals("0") && !arr[2].equals("1")){
                errorMessages.add(OrderManagementConstants.ADD_ORDER_INVALID_SIDE);
            }
            if(Long.parseLong(arr[3]) <= 0){
                errorMessages.add(OrderManagementConstants.ADD_ORDER_INVALID_QUANTITY);
            }
            if(Double.parseDouble(arr[4]) <= 0.0){
                errorMessages.add(OrderManagementConstants.ADD_ORDER_INVALID_PRICE);
            }
            if(errorMessages.size() > 0){
                throw new Exception(OrderManagementConstants.ERROR_MESSAGE_HEADER + errorMessages.stream().collect(Collectors.joining(", ")));
            }
        }

        if(arr.length == 2){
            if(!arr[0].equals("1")){
                errorMessages.add(OrderManagementConstants.CANCEL_ORDER_INVALID_MSG_TYPE);
            }
            if(Long.parseLong(arr[1]) <= 0){
                errorMessages.add(OrderManagementConstants.CANCEL_ORDER_INVALID_ORDERID);
            }
            if(!OrderBook.getOrderBook().getRestingOrders().stream().filter(order -> order.getOrderId() == Long.parseLong(arr[1])).findAny().isPresent()){
                errorMessages.add(OrderManagementConstants.CANCEL_ORDER_ORDERID_NOT_EXISTS_ALREADY);
            }
            if(errorMessages.size() > 0){
                throw new Exception(OrderManagementConstants.ERROR_MESSAGE_HEADER + errorMessages.stream().collect(Collectors.joining(", ")));
            }
        }
    }

}
