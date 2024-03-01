package com.pesto.ordermanagerservice.service.impl;

import com.pesto.ecomm.common.lib.builder.MetaDataDTOBuilder;
import com.pesto.ecomm.common.lib.dto.OrderDTO;
import com.pesto.ecomm.common.lib.dto.OrderItemDTO;
import com.pesto.ecomm.common.lib.dto.OrderListResponseDTO;
import com.pesto.ecomm.common.lib.dto.request.OrderCancellationRequestDTO;
import com.pesto.ecomm.common.lib.entity.Offer;
import com.pesto.ecomm.common.lib.entity.Order;
import com.pesto.ecomm.common.lib.entity.OrderItem;
import com.pesto.ecomm.common.lib.entity.User;
import com.pesto.ecomm.common.lib.enums.OrderStatus;
import com.pesto.ecomm.common.lib.enums.ShippingStatus;
import com.pesto.ecomm.common.lib.exception.UserNotFoundException;
import com.pesto.ecomm.common.lib.repository.OfferRepository;
import com.pesto.ecomm.common.lib.repository.OrderRepository;
import com.pesto.ecomm.common.lib.repository.UserRepository;
import com.pesto.ordermanagerservice.builder.OrderDTOBuilder;
import com.pesto.ordermanagerservice.exception.OfferNotFoundException;
import com.pesto.ordermanagerservice.exception.OrderNotFoundException;
import com.pesto.ordermanagerservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OfferRepository offerRepository;

    @Autowired
    OrderDTOBuilder orderDTOBuilder;

    @Autowired
    MetaDataDTOBuilder metaDataDTOBuilder;

    @Transactional
    @Override
    public OrderDTO placeOrder(OrderDTO orderDTO) throws Exception {
        User buyer = userRepository.findByUserName(orderDTO.getBuyerId());
        if (Objects.isNull(buyer))
            throw new UserNotFoundException("User Id:" + orderDTO.getBuyerId() + " not found!");
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        List<OrderItem> orderItems = new ArrayList<>();
        List<String> offerIds = orderDTO.getOrderItems().stream().map(OrderItemDTO::getOfferId).toList();
        List<Offer> offers = offerRepository.findAllById(offerIds);
        Map<String, Offer> offerIdVsOfferMap = offers.stream().collect(Collectors.toMap(Offer::getOfferId, x -> x));
        for (OrderItemDTO orderItemDTO : orderDTO.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            Offer offer = offerIdVsOfferMap.getOrDefault(orderItemDTO.getOfferId(), null);
            if (Objects.isNull(offer) || offer.getQuantityAvailable() < orderItemDTO.getQuantity()) {
                throw new OfferNotFoundException("Item(s) out of stock!");
            }
            orderItem.setOffer(offer);
            orderItem.setOrder(order);
            orderItem.setQuantity(orderItemDTO.getQuantity());
            orderItem.setPrice(offer.getPrice() * orderItemDTO.getQuantity());
            orderItem.setSeller(offer.getSeller());
            orderItem.setStatus(OrderStatus.PENDING);
            orderItem.setShippingStatus(ShippingStatus.PENDING);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        order.setTotalPrice(calculateTotalPrice(orderItems));
        order.setBuyer(buyer);
        order = orderRepository.save(order);
        updateInventory(orderItems, false);
        OrderDTO responseDto = orderDTOBuilder.build(order);
        List<OrderItemDTO> itemDTOS = orderDTOBuilder.buildItems(order.getOrderItems());
        responseDto.setOrderItems(itemDTOS);
        return responseDto;
    }

    @Override
    public OrderDTO getOrder(String orderId, String buyerUserId) {
        User buyer = userRepository.findByUserName(buyerUserId);
        Order order = orderRepository.findByOrderIdAndBuyer_UserId(orderId, buyer.getUserId());
        if (Objects.isNull(order))
            throw new OrderNotFoundException("Invalid order id!");
        OrderDTO responseDto = orderDTOBuilder.build(order);
        List<OrderItemDTO> itemDTOS = orderDTOBuilder.buildItems(order.getOrderItems());
        responseDto.setOrderItems(itemDTOS);
        return responseDto;
    }

    @Override
    public OrderListResponseDTO getAllOrders(String buyerUserName, Integer pageNo, Integer pageSize) {
        pageNo = Objects.isNull(pageNo) ? 0 : pageNo;
        pageSize = Objects.isNull(pageSize) ? 10 : pageSize;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        User buyer = userRepository.findByUserName(buyerUserName);
        Page<Order> orderPage = orderRepository.findByBuyer_UserId(buyer.getUserId(), pageable);
        OrderListResponseDTO responseDTO = new OrderListResponseDTO();
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (Order order : orderPage.getContent()) {
            OrderDTO responseDto = orderDTOBuilder.build(order);
            List<OrderItemDTO> itemDTOS = orderDTOBuilder.buildItems(order.getOrderItems());
            responseDto.setOrderItems(itemDTOS);
            orderDTOList.add(responseDto);
        }
        responseDTO.setOrders(orderDTOList);
        responseDTO.setMetaData(metaDataDTOBuilder.build(orderPage));
        return responseDTO;
    }

    @Transactional
    @Override
    public OrderDTO cancelOrder(String buyerUserName, OrderCancellationRequestDTO cancellationRequestDTO) {
        User buyer = userRepository.findByUserName(buyerUserName);
        Order order = orderRepository.findByOrderIdAndBuyer_UserId(cancellationRequestDTO.getOrderId(), buyer.getUserId());
        if (Objects.isNull(order))
            throw new OrderNotFoundException("Invalid order id!");
        List<OrderItem> orderItems = order.getOrderItems();
        List<OrderItem> cancelledItems = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            if ((Objects.nonNull(cancellationRequestDTO.getCancelAll()) && cancellationRequestDTO.getCancelAll())
                    || cancellationRequestDTO.getOrderItemIds().contains(orderItem.getOrderItemId())) {
                orderItem.setStatus(OrderStatus.CANCELLED);
                cancelledItems.add(orderItem);
            }
        }

        if (orderItems.size() == cancelledItems.size())
            order.setStatus(OrderStatus.CANCELLED);
        else if (cancelledItems.size() > 0)
            order.setStatus(OrderStatus.PARTIALLY_CANCELLED);
        order.setTotalPrice(order.getTotalPrice() - calculateTotalPrice(cancelledItems));
        order = orderRepository.save(order);
        updateInventory(cancelledItems, true);
        //TODO: Dispatch a pub-sub event to trigger refund
        return orderDTOBuilder.build(order);

    }

    private double calculateTotalPrice(List<OrderItem> orderItems) {
        double totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getPrice();
        }
        return totalPrice;
    }


    @Transactional
    public void updateInventory(List<OrderItem> orderItems, boolean isCancel) {
        try {
            List<Offer> offers = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                Offer offer = orderItem.getOffer();
                offer.setQuantityAvailable(isCancel ? offer.getQuantityAvailable() + orderItem.getQuantity() :
                        offer.getQuantityAvailable() - orderItem.getQuantity());
                offers.add(offer);
            }
            offerRepository.saveAll(offers);
        } catch (Exception e) {
            //TODO: Add pub-sub retry logic
        }
    }

}
