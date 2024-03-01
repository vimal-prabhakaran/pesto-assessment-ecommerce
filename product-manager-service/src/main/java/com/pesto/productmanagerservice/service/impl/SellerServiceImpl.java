package com.pesto.productmanagerservice.service.impl;

import com.pesto.ecomm.common.lib.builder.MetaDataDTOBuilder;
import com.pesto.ecomm.common.lib.dto.OfferDTO;
import com.pesto.ecomm.common.lib.dto.ProductDTO;
import com.pesto.ecomm.common.lib.dto.SellerProductInfoDTO;
import com.pesto.ecomm.common.lib.dto.SellerProductListResponseDTO;
import com.pesto.ecomm.common.lib.dto.request.ProductOnboardRequestDTO;
import com.pesto.ecomm.common.lib.entity.Offer;
import com.pesto.ecomm.common.lib.entity.Product;
import com.pesto.ecomm.common.lib.entity.User;
import com.pesto.ecomm.common.lib.repository.OfferRepository;
import com.pesto.ecomm.common.lib.repository.ProductRepository;
import com.pesto.ecomm.common.lib.repository.UserRepository;
import com.pesto.productmanagerservice.service.SellerService;
import com.pesto.productmanagerservice.service.builder.OfferDTOBuilder;
import com.pesto.productmanagerservice.service.builder.ProductDTOBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    OfferRepository offerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OfferDTOBuilder offerDTOBuilder;

    @Autowired
    ProductDTOBuilder productDTOBuilder;

    @Autowired
    MetaDataDTOBuilder metaDataDTOBuilder;

    @Autowired
    UserRepository userRepository;

    @Override
    public SellerProductListResponseDTO getSellerCatalog(String sellerUserName, Integer pageNo, Integer pageSize) {
        SellerProductListResponseDTO responseDTO = new SellerProductListResponseDTO();
        pageNo = Objects.isNull(pageNo) ? 0 : pageNo;
        pageSize = Objects.isNull(pageSize) ? 10 : pageSize;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        User seller = userRepository.findByUserName(sellerUserName);
        Page<Offer> offerPage = offerRepository.findBySeller_UserIdEqualsOrderByProduct(seller.getUserId(), pageable);
        List<SellerProductInfoDTO> sellerProductInfoDTOList = new ArrayList<>();
        for (Offer offer : offerPage.getContent()) {
            SellerProductInfoDTO sellerProductInfoDTO = new SellerProductInfoDTO();
            Product product = offer.getProduct();
            sellerProductInfoDTO.setProductId(product.getProductId());
            sellerProductInfoDTO.setProductName(product.getName());
            sellerProductInfoDTO.setDescription(product.getDescription());
            sellerProductInfoDTO.setSellerOffer(offerDTOBuilder.build(offer));
            sellerProductInfoDTOList.add(sellerProductInfoDTO);
        }
        responseDTO.setProducts(sellerProductInfoDTOList);
        responseDTO.setMetaData(metaDataDTOBuilder.build(offerPage));
        return responseDTO;
    }

    @Transactional
    @Override
    public SellerProductInfoDTO onboardProduct(ProductOnboardRequestDTO requestDTO) {
        Product product = Objects.nonNull(requestDTO.getProductId()) ? productRepository.findById(requestDTO.getProductId()).orElse(null) :
                productRepository.findByNameEquals(requestDTO.getProductName());

        if (Objects.isNull(product)) {
            // Create a new product if it doesn't exist
            product = new Product();
            product.setName(requestDTO.getProductName());
            product.setDescription(requestDTO.getProductDescription());
            product = productRepository.save(product);
        }

        User seller = userRepository.findById(requestDTO.getSellerId()).orElse(null);
        if (Objects.isNull(seller)) {
            return null;
        }
        // Create a new offer
        List<Offer> offers = !CollectionUtils.isEmpty(product.getOffers()) ? product.getOffers().stream()
                .filter(x -> x.getSeller().getUserId().equals(requestDTO.getSellerId())).toList() : null;
        Offer offer = CollectionUtils.isEmpty(offers) ? new Offer() : offers.get(0);
        offer.setProduct(product);
        offer.setSeller(seller);
        offer.setPrice(requestDTO.getPrice());
        offer.setQuantityAvailable(requestDTO.getAvailableInventory());
        offer = offerRepository.save(offer);
        ProductDTO productDTO = productDTOBuilder.build(product);
        OfferDTO offerDTO = offerDTOBuilder.build(offer);
        return new SellerProductInfoDTO(productDTO, offerDTO);
    }

    @Transactional
    @Override
    public SellerProductInfoDTO editProduct(ProductOnboardRequestDTO requestDTO) {
        Offer offer = offerRepository.findById(requestDTO.getOfferId())
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));
        if (Objects.nonNull(requestDTO.getProductName()) || Objects.nonNull(requestDTO.getProductDescription())) {
            List<Offer> offers = offerRepository.findByProduct_ProductId(offer.getProduct().getProductId());
            if (offers.size() > 1 || !offers.get(0).getSeller().getUserId().equals(requestDTO.getSellerId())) {
                Product product = new Product();
                if (Objects.nonNull(requestDTO.getProductName()))
                    product.setName(requestDTO.getProductName());
                if (Objects.nonNull(requestDTO.getProductDescription()))
                    product.setDescription(requestDTO.getProductDescription());
                product = productRepository.save(product);
                offer.setProduct(product);
            }
        }
        // Use optimistic locking by updating the offer
        offer.setPrice(requestDTO.getPrice());
        offer.setQuantityAvailable(requestDTO.getAvailableInventory());
        offer = offerRepository.save(offer);
        OfferDTO offerDTO = offerDTOBuilder.build(offer);
        ProductDTO productDTO = productDTOBuilder.build(offer.getProduct());
        return new SellerProductInfoDTO(productDTO, offerDTO);
    }

    @Override
    public SellerProductInfoDTO getProduct(String productId, String sellerUserName) {
        User seller = userRepository.findByUserName(sellerUserName);
        Offer offer = offerRepository.findBySeller_UserIdAndProduct_ProductId(seller.getUserId(), productId);
        if (Objects.isNull(offer))
            return null;
        OfferDTO offerDTO = offerDTOBuilder.build(offer);
        ProductDTO productDTO = productDTOBuilder.build(offer.getProduct());
        return new SellerProductInfoDTO(productDTO, offerDTO);
    }

    @Transactional
    @Override
    public Boolean deleteProduct(String productId, String sellerId) {
        try {
            Offer offer = offerRepository.findBySeller_UserIdAndProduct_ProductId(productId, sellerId);
            if (Objects.isNull(offer))
                throw new NoSuchElementException("Offer not found");
            List<Offer> offers = offerRepository.findByProduct_ProductId(offer.getProduct().getProductId());
            if (offers.size() > 1 || !offers.get(0).getSeller().getUserId().equals(sellerId)) {
                offerRepository.delete(offer);
            } else {
                offerRepository.delete(offer);
                productRepository.delete(offer.getProduct());
            }
        } catch (Exception exception) {
            return false;
        }
        return true;
    }
}
