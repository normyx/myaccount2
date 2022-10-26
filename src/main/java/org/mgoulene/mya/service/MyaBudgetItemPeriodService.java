package org.mgoulene.mya.service;

import java.time.LocalDate;
import java.util.List;
import org.mgoulene.domain.BudgetItem;
import org.mgoulene.domain.BudgetItemPeriod;
import org.mgoulene.mya.repository.MyaAvailableDateRepository;
import org.mgoulene.mya.repository.MyaBudgetItemPeriodRepository;
import org.mgoulene.mya.util.LocalDateUtil;
import org.mgoulene.repository.BudgetItemPeriodRepository;
import org.mgoulene.repository.search.BudgetItemPeriodSearchRepository;
import org.mgoulene.service.BudgetItemPeriodService;
import org.mgoulene.service.dto.BudgetItemDTO;
import org.mgoulene.service.dto.BudgetItemPeriodDTO;
import org.mgoulene.service.mapper.BudgetItemMapper;
import org.mgoulene.service.mapper.BudgetItemPeriodMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing BudgetItemPeriod.
 */
@Service
@Transactional
public class MyaBudgetItemPeriodService extends BudgetItemPeriodService {

    private final Logger log = LoggerFactory.getLogger(MyaBudgetItemPeriodService.class);

    private final BudgetItemMapper budgetItemMapper;

    private final MyaAvailableDateRepository availableDateRepository;

    private final MyaBudgetItemPeriodRepository myaBudgetItemPeriodRepository;

    public MyaBudgetItemPeriodService(
        BudgetItemPeriodRepository budgetItemPeriodRepository,
        MyaBudgetItemPeriodRepository myaBudgetItemPeriodRepository,
        BudgetItemPeriodMapper budgetItemPeriodMapper,
        BudgetItemPeriodSearchRepository budgetItemPeriodSearchRepository,
        BudgetItemMapper budgetItemMapper,
        MyaAvailableDateRepository availableDateRepository
    ) {
        super(budgetItemPeriodRepository, budgetItemPeriodMapper, budgetItemPeriodSearchRepository);
        this.budgetItemMapper = budgetItemMapper;
        this.availableDateRepository = availableDateRepository;
        this.myaBudgetItemPeriodRepository = myaBudgetItemPeriodRepository;
    }

    public void createWithNext(BudgetItemDTO budgetItemDTO, BudgetItemPeriodDTO budgetItemPeriodDTO, LocalDate from, int dayOfMonth) {
        budgetItemPeriodDTO.setId(0L);
        BudgetItem budgetItem = budgetItemMapper.toEntity(budgetItemDTO);
        List<LocalDate> months = availableDateRepository.findAllMonthFrom(from);
        // Create all the BudgetItemPeriod from the parameter start month
        for (LocalDate month : months) {
            BudgetItemPeriod bip = budgetItemPeriodMapper.toEntity(budgetItemPeriodDTO);
            bip.setMonth(month);
            bip.setBudgetItem(budgetItem);
            if (!budgetItemPeriodDTO.getIsSmoothed()) {
                bip.setDate(LocalDateUtil.getLocalDate(month, dayOfMonth));
            }
            budgetItemPeriodRepository.save(bip);
        }
    }

    public BudgetItemPeriodDTO findLastBudgetItemPeriod(Long budgetItemId) {
        log.debug("Request to findLastBudgetItemPeriod BudgetItemId : {}", budgetItemId);
        BudgetItemPeriod bip = myaBudgetItemPeriodRepository.findLastBudgetItemPeriod(budgetItemId);

        return budgetItemPeriodMapper.toDto(bip);
    }

    public void extendWithNext(BudgetItemDTO budgetItemDTO) {
        log.debug("Request to extend BudgetItem : {}", budgetItemDTO);
        BudgetItemPeriodDTO budgetItemPeriodDTO = findLastBudgetItemPeriod(budgetItemDTO.getId());
        createWithNext(
            budgetItemDTO,
            budgetItemPeriodDTO,
            budgetItemPeriodDTO.getMonth().plusMonths(1),
            budgetItemPeriodDTO.getIsSmoothed() ? 0 : budgetItemPeriodDTO.getDate().getDayOfMonth()
        );
    }

    /**
     * Delete BudgetItemPeriod with the next.
     *
     * @param budgetItemPeriodDTO the entity to save
     * @return the persisted entity
     */
    public void deleteWithNext(BudgetItemPeriodDTO budgetItemPeriodDTO) {
        log.debug("Request to deleteWithNext BudgetItemPeriod : {}", budgetItemPeriodDTO);

        myaBudgetItemPeriodRepository.deleteWithNext(budgetItemPeriodDTO.getBudgetItem().getId(), budgetItemPeriodDTO.getMonth());
    }

    /**
     * Delete the budgetItemPeriod from budgetItemId.
     *
     * @param id the id of the entity
     */
    public void deleteFromBudgetItem(Long budgetItemId) {
        log.debug("Request to delete BudgetItemPeriod from BudgetItemId : {}", budgetItemId);
        myaBudgetItemPeriodRepository.deleteFromBudgetItem(budgetItemId);
    }

    /**
     * Save a budgetItemPeriod.
     *
     * @param budgetItemPeriodDTOs the entities to save
     * @return the persisted entity
     */
    public List<BudgetItemPeriodDTO> save(List<BudgetItemPeriodDTO> budgetItemPeriodDTOs) {
        log.debug("Request to save BudgetItemPeriods : {}", budgetItemPeriodDTOs);
        List<BudgetItemPeriod> budgetItemPeriods = budgetItemPeriodMapper.toEntity(budgetItemPeriodDTOs);
        budgetItemPeriods = budgetItemPeriodRepository.saveAll(budgetItemPeriods);
        return budgetItemPeriodMapper.toDto(budgetItemPeriods);
    }
}
