package com.pesto.ordermanagerservice.builder;

import com.pesto.ecomm.common.lib.dto.OrderDTO;
import com.pesto.ecomm.common.lib.dto.OrderItemDTO;
import com.pesto.ecomm.common.lib.entity.Order;
import com.pesto.ecomm.common.lib.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderDTOBuilder {

    public OrderDTO build(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setOrderStatus(order.getStatus());
        orderDTO.setBuyerId(order.getBuyer().getUserId());
        orderDTO.setBuyerName(order.getBuyer().getUserName());
        orderDTO.setTotalPrice(order.getTotalPrice());
        List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItemDTOList.add(buildItem(orderItem));
        }
        orderDTO.setOrderItems(orderItemDTOList);
        return orderDTO;
    }

    public OrderItemDTO buildItem(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setOrderItemId(orderItem.getOrderItemId());
        orderItemDTO.setOfferId(orderItem.getOffer().getOfferId());
        orderItemDTO.setSellerId(orderItem.getSeller().getUserId());
        orderItemDTO.setOrderId(orderItem.getOrder().getOrderId());
        orderItemDTO.setStatus(orderItem.getStatus());
        orderItemDTO.setSellerName(orderItem.getSeller().getUserName());
        orderItemDTO.setPrice(orderItem.getPrice());
        orderItemDTO.setQuantity(orderItem.getQuantity());
        orderItemDTO.setShippingStatus(orderItem.getShippingStatus());
        return orderItemDTO;
    }

    public List<OrderDTO> build(List<Order> orders) {
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for(Order order : orders) {
            orderDTOList.add(build(order));
        }
        return orderDTOList;
    }

    public List<OrderItemDTO> buildItems(List<OrderItem> orderItems) {
        List<OrderItemDTO> orderDTOList = new ArrayList<>();
        for(OrderItem orderItem : orderItems) {
            orderDTOList.add(buildItem(orderItem));
        }
        return orderDTOList;
    }
}
