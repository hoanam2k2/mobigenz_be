package com.api.mobigenz_be.services;

import com.api.mobigenz_be.DTOs.OrderDetailDto;
import com.api.mobigenz_be.DTOs.OrderDto;
import com.api.mobigenz_be.DTOs.UserOrderDto;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    OrderDto adminSaveOrder(OrderDto orderDto);

    OrderDto saveOrder(OrderDto orderDto);

    void cancelOrder(Integer order_id, String note);

    void updateOrderStatus(Integer order_id, Integer newStatus, String note);

    OrderDto getOrderById(Integer order_id);

    List<OrderDto> getOrdersByCustomerId(String term, Integer order_status, Integer customer_id);

    List<OrderDto> searchOrders(String term, Integer order_status);

    List<OrderDto> getOrdersByOrderStatus(Integer orderStatus);

    List<Object[]> statisticsByBestSellingProducts();

    //Double dthu2(LocalDate time1, LocalDate time2);

}
