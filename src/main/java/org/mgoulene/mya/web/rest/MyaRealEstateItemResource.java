package org.mgoulene.mya.web.rest;

import java.util.Optional;
import org.mgoulene.mya.service.MyaRealEstateItemService;
import org.mgoulene.service.dto.RealEstateItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.mgoulene.domain.RealEstateItem}.
 */
@RestController
@RequestMapping("/api")
public class MyaRealEstateItemResource {

    private final Logger log = LoggerFactory.getLogger(MyaRealEstateItemResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MyaRealEstateItemService myaRealEstateItemService;

    public MyaRealEstateItemResource(MyaRealEstateItemService myaRealEstateItemService) {
        this.myaRealEstateItemService = myaRealEstateItemService;
    }

    /**
     * {@code GET  /real-estate-items/:id} : get the "id" realEstateItem.
     *
     * @param id the id of the realEstateItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the realEstateItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/mya-real-estate-items/last-from-bank-account/{bankAccountId}")
    public ResponseEntity<RealEstateItemDTO> getLastRealEstateItemFromBankAccount(@PathVariable Long bankAccountId) {
        log.debug("REST request to get getLastRealEstateItemFromBankAccount : {}", bankAccountId);
        Optional<RealEstateItemDTO> realEstateItemDTO = myaRealEstateItemService.findLastOne(bankAccountId);
        return ResponseUtil.wrapOrNotFound(realEstateItemDTO);
    }
}
