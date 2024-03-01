package com.pesto.ordermanagerservice.controller;

import com.pesto.ecomm.common.lib.dto.OrderItemDTO;
import com.pesto.ecomm.common.lib.dto.OrderItemListResponse;
import com.pesto.ecomm.common.lib.dto.request.OrderStatusUpdateRequestDTO;
import com.pesto.ordermanagerservice.service.SellerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RequestMapping("/api/v1/seller")
@RestController
public class SellerOrderController {

    @Autowired
    SellerOrderService sellerOrderService;

    @GetMapping("/orders")
    public ResponseEntity<OrderItemListResponse> getAllSellerOrders(@RequestHeader(name="X-UserId") String userId,
                                                                      @RequestParam(required = false) Integer pageNo,
                                                                      @RequestParam(required = false) Integer pageSize) {
        OrderItemListResponse responseDTO = sellerOrderService.getAllSellerOrders(userId, pageNo, pageSize);
        if (Objects.isNull(responseDTO))
            return new ResponseEntity<OrderItemListResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<OrderItemListResponse>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/order/item/{orderItemId}/status")
    public ResponseEntity<OrderItemDTO> updateOrderStatus(@PathVariable String orderItemId,
                                                          @RequestBody OrderStatusUpdateRequestDTO requestDTO) {
        requestDTO.setOrderItemId(orderItemId);
        OrderItemDTO response = sellerOrderService.updateOrderStatus(requestDTO);
        if (Objects.isNull(response))
            return new ResponseEntity<OrderItemDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<OrderItemDTO>(response, HttpStatus.OK);
    }

}
