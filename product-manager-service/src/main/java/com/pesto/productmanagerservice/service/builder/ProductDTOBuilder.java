package com.pesto.productmanagerservice.service.builder;

import com.pesto.ecomm.common.lib.dto.ProductDTO;
import com.pesto.ecomm.common.lib.entity.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ProductDTOBuilder {

    public ProductDTO build(Product product) {
        if (Objects.isNull(product))
            return null;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setProductName(product.getName());
        productDTO.setDescription(product.getDescription());
        return productDTO;
    }

    public List<ProductDTO> build(List<Product> productList) {
        List<ProductDTO> productDTOList = new ArrayList<>();
        for (Product product : productList) {
            productDTOList.add(build(product));
        }
        return productDTOList;
    }
}
