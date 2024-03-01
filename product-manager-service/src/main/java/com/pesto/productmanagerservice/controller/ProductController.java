package com.pesto.productmanagerservice.controller;

import com.pesto.ecomm.common.lib.dto.BuyerProductListResponseDTO;
import com.pesto.productmanagerservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RequestMapping("/api/v1")
@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/products/search")
    public ResponseEntity<BuyerProductListResponseDTO> searchProducts(@RequestParam(required = false) String searchQuery,
                                                                      @RequestParam(required = false) Integer pageNo,
                                                                      @RequestParam(required = false) Integer pageSize) {
        BuyerProductListResponseDTO responseDTO = productService.searchProducts(searchQuery, pageNo, pageSize);
        if (Objects.isNull(responseDTO))
            return new ResponseEntity<BuyerProductListResponseDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<BuyerProductListResponseDTO>(responseDTO, HttpStatus.OK);
    }
}
