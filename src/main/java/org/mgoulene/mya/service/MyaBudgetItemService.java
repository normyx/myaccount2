package org.mgoulene.mya.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.mgoulene.domain.BudgetItem;
import org.mgoulene.mya.repository.MyaBudgetItemPeriodRepository;
import org.mgoulene.mya.repository.MyaBudgetItemRepository;
import org.mgoulene.repository.BudgetItemRepository;
import org.mgoulene.repository.search.BudgetItemSearchRepository;
import org.mgoulene.service.BudgetItemService;
import org.mgoulene.service.dto.BudgetItemDTO;
import org.mgoulene.service.dto.BudgetItemPeriodDTO;
import org.mgoulene.service.mapper.BudgetItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BudgetItem}.
 */
@Service
@Transactional
public class MyaBudgetItemService extends BudgetItemService {

    private final Logger log = LoggerFactory.getLogger(MyaBudgetItemService.class);

    private final MyaBudgetItemRepository myaBudgetItemRepository;

    private final MyaBudgetItemPeriodService myaBudgetItemPeriodService;

    public MyaBudgetItemService(
        BudgetItemRepository budgetItemRepository,
        BudgetItemMapper budgetItemMapper,
        BudgetItemSearchRepository budgetItemSearchRepository,
        MyaBudgetItemRepository myaBudgetItemRepository,
        MyaBudgetItemPeriodService myaBudgetItemPeriodService
    ) {
        super(budgetItemRepository, budgetItemMapper, budgetItemSearchRepository);
        this.myaBudgetItemRepository = myaBudgetItemRepository;
        this.myaBudgetItemPeriodService = myaBudgetItemPeriodService;
    }

    /**
     * Save a budgetItem.
     *
     * @param budgetItemDTO the entity to save.
     * @return the persisted entity.
     */
    public List<BudgetItemDTO> findAllAvailableBetweenDate(Long accountId, LocalDate from, LocalDate to, Long categoryId) {
        log.debug("Request to get BudgetItem from Account : {}, from: {}, to: {} with categoryId : {}", accountId, from, to, categoryId);

        return StreamSupport
            .stream(
                (
                    (categoryId == null)
                        ? myaBudgetItemRepository.findAllAvailableBetweenDate(accountId, from, to)
                        : myaBudgetItemRepository.findAllAvailableBetweenDateWithCategory(accountId, from, to, categoryId)
                ).spliterator(),
                false
            )
            .map(budgetItemMapper::toDto)
            .collect(Collectors.toList());
    }

    public void reorderUp(Long fromId, Long toId) {
        log.debug("Request to reorder up : {} to {}", fromId, toId);
        Optional<BudgetItem> fromBIO = myaBudgetItemRepository.findById(fromId);
        Optional<BudgetItem> toBIO = myaBudgetItemRepository.findById(toId);
        if (fromBIO.isPresent() && toBIO.isPresent()) {
            BudgetItem fromBI = fromBIO.get();
            BudgetItem toBI = toBIO.get();
            int newOrder = toBI.getOrder();
            long accountId = toBI.getAccount().getId();
            this.myaBudgetItemRepository.increaseOrder(newOrder, accountId);
            fromBI.setOrder(newOrder);
            this.myaBudgetItemRepository.save(fromBI);
        }
    }

    public void reorderDown(Long fromId, Long toId) {
        log.debug("Request to reorder down : {} to {}", fromId, toId);
        Optional<BudgetItem> fromBIO = myaBudgetItemRepository.findById(fromId);
        Optional<BudgetItem> toBIO = myaBudgetItemRepository.findById(toId);
        if (fromBIO.isPresent() && toBIO.isPresent()) {
            BudgetItem fromBI = fromBIO.get();
            BudgetItem toBI = toBIO.get();
            int newOrder = toBI.getOrder();
            long accountId = toBI.getAccount().getId();
            this.myaBudgetItemRepository.decreaseOrder(newOrder, accountId);
            fromBI.setOrder(newOrder);
            this.myaBudgetItemRepository.save(fromBI);
        }
    }

    public Integer findNewOrder(Long accountId) {
        log.debug("Request to findNextOrderBudgetItem of : {}", accountId);
        Integer order = myaBudgetItemRepository.findNewOrder(accountId);
        return order == null ? 1 : order;
    }

    public BudgetItemDTO saveWithBudgetItemPeriod(BudgetItemDTO budgetItemDTO, BudgetItemPeriodDTO budgetItemPeriodDTO) {
        log.debug("Request to save BudgetItem : {}", budgetItemDTO);
        BudgetItem budgetItem = budgetItemMapper.toEntity(budgetItemDTO);
        budgetItem = budgetItemRepository.save(budgetItem);
        BudgetItemDTO result = budgetItemMapper.toDto(budgetItem);
        int dayOfMonth = budgetItemPeriodDTO.getIsSmoothed() ? 0 : budgetItemPeriodDTO.getDate().getDayOfMonth();
        myaBudgetItemPeriodService.createWithNext(result, budgetItemPeriodDTO, budgetItemPeriodDTO.getMonth(), dayOfMonth);
        return result;
    }
}
