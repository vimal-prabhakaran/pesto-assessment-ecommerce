package com.pesto.ordermanagerservice.service;

import com.pesto.ecomm.common.lib.dto.OrderDTO;
import com.pesto.ecomm.common.lib.dto.OrderItemDTO;
import com.pesto.ecomm.common.lib.dto.OrderItemListResponse;
import com.pesto.ecomm.common.lib.dto.OrderListResponseDTO;
import com.pesto.ecomm.common.lib.dto.request.OrderStatusUpdateRequestDTO;

public interface  SellerOrderService {

    OrderItemListResponse getAllSellerOrders(String sellerUserName, Integer pageNo, Integer pageSize);

    OrderItemDTO updateOrderStatus(OrderStatusUpdateRequestDTO requestDto);

}
