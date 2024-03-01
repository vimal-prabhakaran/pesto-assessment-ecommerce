package com.pesto.productmanagerservice.service.impl;

import com.pesto.ecomm.common.lib.builder.MetaDataDTOBuilder;
import com.pesto.ecomm.common.lib.dto.BuyerProductInfoDTO;
import com.pesto.ecomm.common.lib.dto.BuyerProductListResponseDTO;
import com.pesto.ecomm.common.lib.dto.MetaDataDTO;
import com.pesto.ecomm.common.lib.dto.OfferDTO;
import com.pesto.ecomm.common.lib.dto.ProductDTO;
import com.pesto.ecomm.common.lib.entity.Product;
import com.pesto.ecomm.common.lib.repository.ProductRepository;
import com.pesto.productmanagerservice.service.ProductService;
import com.pesto.productmanagerservice.service.builder.OfferDTOBuilder;
import com.pesto.productmanagerservice.service.builder.ProductDTOBuilder;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDTOBuilder productDTOBuilder;

    @Autowired
    private OfferDTOBuilder offerDTOBuilder;

    @Autowired
    private MetaDataDTOBuilder metaDataDTOBuilder;

    @Override
    public BuyerProductListResponseDTO searchProducts(String query, Integer pageNo, Integer pageSize) {
        BuyerProductListResponseDTO responseDTO = new BuyerProductListResponseDTO();
        pageNo = Objects.isNull(pageNo) ? 0 : pageNo;
        pageSize = Objects.isNull(pageSize) ? 10 : pageSize;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> productPage = StringUtils.isBlank(query) ? productRepository.findAll(pageable) :
                productRepository.findByNameContainingOrDescriptionContaining(query, query, pageable);
        List<Product> productList = productPage.toList();
        List<BuyerProductInfoDTO> buyerProductInfoDTOList = new ArrayList<>();
        for (Product product : productList) {
            ProductDTO productDTO = productDTOBuilder.build(product);
            List<OfferDTO> offerDTOList = offerDTOBuilder.build(product.getOffers());
            BuyerProductInfoDTO productInfoDTO = new BuyerProductInfoDTO(productDTO, offerDTOList);
            buyerProductInfoDTOList.add(productInfoDTO);
        }
        responseDTO.setProducts(buyerProductInfoDTOList);
        MetaDataDTO metaDataDTO = metaDataDTOBuilder.build(productPage);
        responseDTO.setMetaData(metaDataDTO);
        return responseDTO;
    }

}
