package org.mgoulene.mya.service;

import java.util.List;
import java.util.Optional;
import org.mgoulene.domain.RealEstateItem;
import org.mgoulene.mya.repository.MyaRealEstateItemRepository;
import org.mgoulene.repository.RealEstateItemRepository;
import org.mgoulene.repository.search.RealEstateItemSearchRepository;
import org.mgoulene.service.RealEstateItemService;
import org.mgoulene.service.dto.RealEstateItemDTO;
import org.mgoulene.service.mapper.RealEstateItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RealEstateItem}.
 */
@Service
@Transactional
public class MyaRealEstateItemService extends RealEstateItemService {

    private final Logger log = LoggerFactory.getLogger(MyaRealEstateItemService.class);

    private final MyaRealEstateItemRepository myaRealEstateItemRepository;

    public MyaRealEstateItemService(
        RealEstateItemRepository realEstateItemRepository,
        RealEstateItemMapper realEstateItemMapper,
        RealEstateItemSearchRepository realEstateItemSearchRepository,
        MyaRealEstateItemRepository myaRealEstateItemRepository
    ) {
        super(realEstateItemRepository, realEstateItemMapper, realEstateItemSearchRepository);
        this.myaRealEstateItemRepository = myaRealEstateItemRepository;
    }

    /**
     * Get one realEstateItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RealEstateItemDTO> findLastOne(Long bankAccountId) {
        log.debug("Request to get findLastOne : {}", bankAccountId);
        List<RealEstateItem> items = myaRealEstateItemRepository.findAllFromBankAccount(bankAccountId);

        return items.size() > 0 ? Optional.of(items.get(0)).map(realEstateItemMapper::toDto) : Optional.empty();
    }
}
