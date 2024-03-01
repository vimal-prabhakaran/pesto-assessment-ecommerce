package com.pesto.productmanagerservice.service.builder;

import com.pesto.ecomm.common.lib.dto.OfferDTO;
import com.pesto.ecomm.common.lib.entity.Offer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OfferDTOBuilder {

    public OfferDTO build(Offer offer) {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setOfferId(offer.getOfferId());
        offerDTO.setAvailableInventory(offer.getQuantityAvailable());
        offerDTO.setPrice(offer.getPrice());
        return offerDTO;
    }

    public List<OfferDTO> build(List<Offer> offerList) {
        List<OfferDTO> offerDTOList = new ArrayList<>();
        for (Offer offer : offerList) {
            offerDTOList.add(build(offer));
        }
        return offerDTOList;
    }

}
