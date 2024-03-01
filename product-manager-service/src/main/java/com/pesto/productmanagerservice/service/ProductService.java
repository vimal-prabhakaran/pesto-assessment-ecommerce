package com.pesto.productmanagerservice.service;

import com.pesto.ecomm.common.lib.dto.BuyerProductListResponseDTO;

public interface ProductService {

    BuyerProductListResponseDTO searchProducts(String query, Integer pageNo, Integer pageSize);

}
