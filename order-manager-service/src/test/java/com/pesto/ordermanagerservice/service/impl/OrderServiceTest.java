package com.pesto.ordermanagerservice.service.impl;

import com.pesto.ecomm.common.lib.builder.MetaDataDTOBuilder;
import com.pesto.ecomm.common.lib.dto.OrderDTO;
import com.pesto.ecomm.common.lib.dto.OrderItemDTO;
import com.pesto.ecomm.common.lib.entity.Offer;
import com.pesto.ecomm.common.lib.entity.Order;
import com.pesto.ecomm.common.lib.entity.OrderItem;
import com.pesto.ecomm.common.lib.entity.Product;
import com.pesto.ecomm.common.lib.entity.User;
import com.pesto.ecomm.common.lib.enums.OrderStatus;
import com.pesto.ecomm.common.lib.enums.Role;
import com.pesto.ecomm.common.lib.enums.ShippingStatus;
import com.pesto.ecomm.common.lib.repository.OfferRepository;
import com.pesto.ecomm.common.lib.repository.OrderRepository;
import com.pesto.ecomm.common.lib.repository.UserRepository;
import com.pesto.ecomm.common.lib.testcase.BaseTestCase;
import com.pesto.ordermanagerservice.builder.OrderDTOBuilder;
import com.pesto.ordermanagerservice.exception.OfferNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest extends BaseTestCase {

    @InjectMocks
    OrderServiceImpl orderService;

    @Mock
    UserRepository userRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OfferRepository offerRepository;

    @Mock
    OrderDTOBuilder orderDTOBuilder;

    @Mock
    MetaDataDTOBuilder metaDataDTOBuilder;


    @Test
    public void testPlaceOrder_ValidInput() throws Exception {
        OrderDTO orderDTO = createValidOrderDTO();
        User user = createValidUser();
        Offer offer = createValidOffer();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(offerRepository.findAllById(any())).thenReturn(Collections.singletonList(offer));
        assertThrows(OfferNotFoundException.class, () -> orderService.placeOrder(orderDTO));

    }



    public static Order createValidOrder() {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(100.0);
        order.setBuyer(createValidUser());
        order.setOrderItems(createOrderItems());
        return order;
    }


    private static List<OrderItem> createOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem1 = createValidOrderItem();
        OrderItem orderItem2 = createValidOrderItem();
        orderItems.add(orderItem1);
        orderItems.add(orderItem2);
        return orderItems;
    }


    private static OrderItem createValidOrderItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(UUID.randomUUID().toString());
        orderItem.setOffer(createValidOffer());
        orderItem.setPrice(50.0);
        orderItem.setQuantity(2);
        orderItem.setSeller(createValidUser());
        orderItem.setStatus(OrderStatus.PENDING);
        orderItem.setShippingStatus(ShippingStatus.PENDING);
        return orderItem;
    }


    private static User createValidUser() {
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setUserName("JohnDoe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setRole(Role.BUYER);
        return user;
    }


    private static Offer createValidOffer() {
        Offer offer = new Offer();
        offer.setOfferId(UUID.randomUUID().toString());
        Product product = createValidProduct();
        User seller = createValidUser();
        offer.setProduct(product);
        offer.setSeller(seller);
        offer.setPrice(50.0);
        offer.setQuantityAvailable(100);
        return offer;
    }


    private static Product createValidProduct() {
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setName("Product Name");
        product.setDescription("Product Description");
        return product;
    }

    public static OrderDTO createValidOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(UUID.randomUUID().toString());
        orderDTO.setProductId(UUID.randomUUID().toString());
        orderDTO.setBuyerName("John Doe");
        orderDTO.setOrderStatus(OrderStatus.PENDING);
        orderDTO.setBuyerId(UUID.randomUUID().toString());
        orderDTO.setTotalPrice(100.0);
        orderDTO.setOrderItems(createOrderItemDTOs());
        return orderDTO;
    }

    private static List<OrderItemDTO> createOrderItemDTOs() {
        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
        OrderItemDTO orderItemDTO1 = createValidOrderItemDTO();
        OrderItemDTO orderItemDTO2 = createValidOrderItemDTO();
        orderItemDTOs.add(orderItemDTO1);
        orderItemDTOs.add(orderItemDTO2);
        return orderItemDTOs;
    }

    private static OrderItemDTO createValidOrderItemDTO() {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setOrderItemId(UUID.randomUUID().toString());
        orderItemDTO.setOfferId(UUID.randomUUID().toString());
        orderItemDTO.setOrderId(UUID.randomUUID().toString());
        orderItemDTO.setSellerName("SellerName");
        orderItemDTO.setStatus(OrderStatus.PENDING);
        orderItemDTO.setSellerId(UUID.randomUUID().toString());
        orderItemDTO.setProductName("ProductName");
        orderItemDTO.setQuantity(1);
        orderItemDTO.setPrice(50.0);
        orderItemDTO.setShippingStatus(ShippingStatus.PENDING);
        return orderItemDTO;
    }


}
