package org.mgoulene.mya.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.ehcache.shadow.org.terracotta.offheapstore.util.ReopeningInterruptibleChannel.IoOperation;
import org.mgoulene.domain.Operation;
import org.mgoulene.mya.repository.MyaOperationRepository;
import org.mgoulene.mya.service.dto.MyaImportOperationActions;
import org.mgoulene.mya.service.dto.MyaOperationKey;
import org.mgoulene.repository.OperationRepository;
import org.mgoulene.repository.search.OperationSearchRepository;
import org.mgoulene.service.OperationService;
import org.mgoulene.service.dto.OperationDTO;
import org.mgoulene.service.mapper.OperationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Operation}.
 */
@Service
@Transactional
public class MyaOperationService extends OperationService {

    private final Logger log = LoggerFactory.getLogger(MyaOperationService.class);

    private final MyaOperationRepository myaOperationRepository;

    public MyaOperationService(
        OperationRepository operationRepository,
        OperationMapper operationMapper,
        OperationSearchRepository operationSearchRepository,
        MyaOperationRepository myaOperationRepository
    ) {
        super(operationRepository, operationMapper, operationSearchRepository);
        this.myaOperationRepository = myaOperationRepository;
    }

    /**
     * Get all the operations that fits with the key date, amount and label and
     * accountId that is not uptodate
     *
     * @param date      the date
     * @param amount    the amount
     * @param label     the label
     * @param accountID the account id
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<OperationDTO> findAllByDateLabelAmountAndAccountAndNotUpToDate(LocalDate date, float amount, String label, Long accountId) {
        log.debug("Request to get all Operations by date, label and amount");
        return StreamSupport
            .stream(
                myaOperationRepository.findAllByDateAmountLabelAccountAndNotUpToDate(date, amount, label, accountId).spliterator(),
                false
            )
            .map(operationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the operations that fits with accountId
     *
     * @param accountID the account id
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<OperationDTO> findAllByAccount(Long accountId) {
        log.debug("Request to get all Operations by date, label and amount");
        return StreamSupport
            .stream(myaOperationRepository.findAllByAccount(accountId).spliterator(), false)
            .map(operationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Update all the operation from an accountId to isUpToDate to false.
     *
     * @param accountId the id of the userAccount
     */
    public int updateIsUpToDate(Long accountId) {
        log.debug("Request to update isUpToDate for Operation to false : {}", accountId);
        return myaOperationRepository.updateIsUpToDate(accountId);
    }

    /**
     * Delete the operation from an account id where isUpToDate is false.
     *
     * @param accountId the id of the userAccount
     */
    public int deleteIsNotUpToDate(Long accountId) {
        log.debug("Request to update isUpToDate for Operation to false : {}", accountId);
        return myaOperationRepository.deleteIsNotUpToDate(accountId);
    }

    /**
     * get all the operations where BudgetItem is null.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public int findCountAllCloseToBudgetItemPeriodWithoutAlreadyAssigned(
        Long accountId,
        Long categoryId,
        float amount,
        LocalDate dateFrom,
        LocalDate dateTo
    ) {
        log.debug(
            "Request to count all operations close to a findCountAllCloseToBudgetItemPeriod accountId: {}, categoryId: {}, amount: {}, dateFrom: {}, dateTo: {}",
            accountId,
            categoryId,
            amount,
            dateFrom,
            dateTo
        );
        return findAllCloseToBudgetItemPeriod(accountId, categoryId, amount, dateFrom, dateTo).size();
    }

    /**
     * get all the operations where BudgetItem is null.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<OperationDTO> findAllCloseToBudgetItemPeriod(
        Long accountId,
        Long categoryId,
        float amount,
        LocalDate dateFrom,
        LocalDate dateTo
    ) {
        log.debug(
            "Request to get all operations close to a findCountAllCloseToBudgetItemPeriod accountId: {}, categoryId: {}, amount: {}, dateFrom: {}, dateTo: {}",
            accountId,
            categoryId,
            amount,
            dateFrom,
            dateTo
        );
        List<Operation> operations = myaOperationRepository.findAllCloseToBudgetItemPeriod(accountId, categoryId, amount, dateFrom, dateTo);
        log.debug("Request to get all operations close to a findCountAllCloseToBudgetItemPeriod | count: {}", operations.size());
        return StreamSupport
            .stream(operations.spliterator(), false)
            .map(operationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * get all the operations where BudgetItem is null.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<OperationDTO> findAllCloseToBudgetItemPeriodWithoutAlreadyAssigned(
        Long accountId,
        Long categoryId,
        float value,
        LocalDate dateFrom,
        LocalDate dateTo
    ) {
        log.debug("Request to get all operations close to a budgetItemPeriod");
        return StreamSupport
            .stream(
                myaOperationRepository
                    .findAllCloseToBudgetItemPeriodWithoutAlreadyAssigned(accountId, categoryId, value, dateFrom, dateTo)
                    .spliterator(),
                false
            )
            .map(operationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public Float getSumOfOperationForBankAccount(Long bankAccountId) {
        log.debug("Request to getSomeOfOperationForBankAccount with bankAccountId: {}", bankAccountId);
        return myaOperationRepository.getSumOfOperationForBankAccount(bankAccountId);
    }

    @Transactional(readOnly = true)
    public LocalDate getLastOperationDateForBankAccount(Long bankAccountId) {
        log.debug("Request to getLastOperationDateForBankAccount with bankAccountId: {}", bankAccountId);
        return myaOperationRepository.getLastOperationDateForBankAccount(bankAccountId);
    }

    /*
     * public OperationDTO importOperation(OperationDTO operationDTO) {
     * List<OperationDTO> results =
     * findAllByDateLabelAmountAndAccountAndNotUpToDate(
     * operationDTO.getDate(),
     * operationDTO.getAmount(),
     * operationDTO.getLabel(),
     * operationDTO.getAccount().getId()
     * );
     * OperationDTO operationToSave;
     * if (!results.isEmpty()) {
     * log.debug("Data already exists. Updatating : {} to {} ", results.get(0),
     * operationDTO);
     * // Only take the first one
     * operationToSave = results.get(0);
     * // if (operationDTO.isIdentical(operationToSave))
     * operationToSave.setNote(operationDTO.getNote());
     * operationToSave.setCheckNumber(operationDTO.getCheckNumber());
     * operationToSave.setSubCategory(operationDTO.getSubCategory());
     * } else {
     * log.error("Create data : {}", operationDTO);
     * operationToSave = operationDTO;
     * }
     *
     * operationToSave.setIsUpToDate(true);
     * return save(operationToSave);
     * }
     */

    public void importOperation(
        OperationDTO operationDTO,
        HashMap<MyaOperationKey, Stack<OperationDTO>> operations,
        MyaImportOperationActions actions
    ) {
        if (operationDTO.getAccount() == null) {
            //log.error("Trying to import Operation with no account {}", operationDTO);
        }
        MyaOperationKey key = new MyaOperationKey(operationDTO);
        Stack<OperationDTO> stack = operations.get(key);
        operationDTO.setIsUpToDate(true);

        if (stack != null && !stack.empty()) {
            OperationDTO opEquals = MyaOperationKey.findEquals(operationDTO, stack);
            if (opEquals != null) {
                // log.info("Data already exists. No Update needed {} ", operationDTO);
                stack.remove(opEquals);
            } else {
                OperationDTO opVerySimilar = MyaOperationKey.findVerySimilar(operationDTO, stack);
                if (opVerySimilar != null) {
                    stack.remove(opVerySimilar);
                    // log.info("Data already exists. Updatating - Very Similar: {} to {} ",
                    // opVerySimilar, operationDTO);
                    // Only take the first one
                    // if (operationDTO.isIdentical(operationToSave))
                    /*opVerySimilar.setLabel(operationDTO.getLabel());
                    opVerySimilar.setNote(operationDTO.getNote());
                    opVerySimilar.setCheckNumber(operationDTO.getCheckNumber());
                    opVerySimilar.setSubCategory(operationDTO.getSubCategory());
                    opVerySimilar.setAmount(operationDTO.getAmount());
                    opVerySimilar.setBankAccount(operationDTO.getBankAccount());*/
                    actions.addOperationToUpdate(operationDTO, opVerySimilar);
                } else {
                    OperationDTO opKeyEquals = MyaOperationKey.findKeyEquals(operationDTO, stack);
                    if (opKeyEquals != null) {
                        stack.remove(opKeyEquals);
                        // log.info("Data already exists. Updatating - Not Closed : {} to {} ",
                        // opKeyEquals, operationDTO);
                        // Only take the first one
                        // if (operationDTO.isIdentical(operationToSave))
                        /*opKeyEquals.setLabel(operationDTO.getLabel());
                        opKeyEquals.setNote(operationDTO.getNote());
                        opKeyEquals.setCheckNumber(operationDTO.getCheckNumber());
                        opKeyEquals.setSubCategory(operationDTO.getSubCategory());
                        opKeyEquals.setAmount(operationDTO.getAmount());
                        opKeyEquals.setBankAccount(operationDTO.getBankAccount());*/
                        actions.addOperationNotClosed(operationDTO, opKeyEquals);
                    } else {
                        log.error("Finding operations error {} \n {} \n {} ", operationDTO, stack);
                    }
                }
            }
        } else {
            operations.remove(key);
            actions.addOperationToCreate(operationDTO);
        }
    }

    public void updateOperation(OperationDTO source, OperationDTO target) {
        target.setLabel(source.getLabel());
        target.setNote(source.getNote());
        target.setCheckNumber(source.getCheckNumber());
        target.setSubCategory(source.getSubCategory());
        target.setAmount(source.getAmount());
        target.setBankAccount(source.getBankAccount());
        save(target);
    }

    public void deleteOperations(HashMap<MyaOperationKey, Stack<OperationDTO>> operations, MyaImportOperationActions actions) {
        for (Stack<OperationDTO> stack : operations.values()) {
            while (!stack.empty()) {
                OperationDTO opToDelete = stack.pop();

                if (opToDelete.getDeletingHardLock() == null || !opToDelete.getDeletingHardLock()) {
                    //log.info("Deleting data : {}", opToDelete);
                    actions.addOperationToDelete(opToDelete);
                } else {
                    //log.info("Data not deleted due to Hard Lock : {}", opToDelete);
                    actions.addOperationToDeleteWithHardLock(opToDelete);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public LocalDate findLastOperationDate(Long accountId) {
        log.debug("Request to findLastOperationDate");
        return myaOperationRepository.findLastOperationDate(accountId);
    }

    public HashMap<MyaOperationKey, Stack<OperationDTO>> getOperationMap(List<OperationDTO> operations) {
        HashMap<MyaOperationKey, Stack<OperationDTO>> map = new HashMap<MyaOperationKey, Stack<OperationDTO>>();
        for (OperationDTO operationDTO : operations) {
            MyaOperationKey key = new MyaOperationKey(operationDTO);
            Stack<OperationDTO> stack = map.get(key);
            if (stack == null) {
                stack = new Stack<OperationDTO>();
                map.put(key, stack);
            }
            stack.add(operationDTO);
            // map.put(new MyaOperationKey(operationDTO), operationDTO);
        }
        return map;
    }
}
