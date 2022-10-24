package org.mgoulene.mya.web.rest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.mgoulene.mya.service.MyaApplicationUserService;
import org.mgoulene.mya.service.MyaOperationCSVImporterService;
import org.mgoulene.mya.service.MyaOperationService;
import org.mgoulene.mya.service.dto.MyaImportOperationActions;
import org.mgoulene.mya.service.dto.MyaOperationToUpdate;
import org.mgoulene.repository.OperationRepository;
import org.mgoulene.service.BudgetItemPeriodService;
import org.mgoulene.service.BudgetItemService;
import org.mgoulene.service.OperationQueryService;
import org.mgoulene.service.UserService;
import org.mgoulene.service.criteria.OperationCriteria;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.mgoulene.service.dto.BudgetItemPeriodDTO;
import org.mgoulene.service.dto.OperationDTO;
import org.mgoulene.web.rest.OperationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class MyaOperationResource {

    private final Logger log = LoggerFactory.getLogger(OperationResource.class);

    // private static final String ENTITY_NAME = "operation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MyaOperationService operationService;

    // private final OperationRepository operationRepository;

    private final OperationQueryService operationQueryService;

    private final MyaApplicationUserService applicationUserService;

    private final BudgetItemPeriodService budgetItemPeriodService;

    private final BudgetItemService budgetItemService;

    private final MyaOperationCSVImporterService myaOperationCSVImporterService;

    public MyaOperationResource(
        MyaOperationService operationService,
        OperationRepository operationRepository,
        OperationQueryService operationQueryService,
        UserService userService,
        MyaApplicationUserService applicationUserService,
        BudgetItemPeriodService budgetItemPeriodService,
        BudgetItemService budgetItemService,
        MyaOperationCSVImporterService myaOperationCSVImporterService
    ) {
        this.operationService = operationService;
        // this.operationRepository = operationRepository;
        this.operationQueryService = operationQueryService;
        this.applicationUserService = applicationUserService;
        this.budgetItemPeriodService = budgetItemPeriodService;
        this.budgetItemService = budgetItemService;
        this.myaOperationCSVImporterService = myaOperationCSVImporterService;
    }

    /**
     * GET /operations : get all the operations from signed in user.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of operations in
     *         body
     */
    @GetMapping("/mya-operations/with-signedin-user")
    public ResponseEntity<List<OperationDTO>> getAllOperationsWithUser(OperationCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Operations by criteria: {}", criteria);

        Optional<ApplicationUserDTO> applicationUserOptional = applicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            LongFilter userFilter = new LongFilter();
            userFilter.setEquals(applicationUserOptional.get().getId());
            criteria.setAccountId(userFilter);

            Page<OperationDTO> page = operationQueryService.findByCriteria(criteria, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
        return null;
    }

    /**
     * {@code SEARCH  /_search/operations?query=:query} : search for the operation
     * corresponding
     * to the query.
     *
     * @param query    the query of the operation search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/mya-operations/with-signedin-user")
    public ResponseEntity<List<OperationDTO>> searchOperationsWithUser(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Operations for query {}", query);
        Optional<ApplicationUserDTO> applicationUserOptional = applicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            query += " AND account.id:" + applicationUserOptional.get().getId();
            Page<OperationDTO> page = operationService.search(query, pageable);
            for (int i = 0; i < page.getContent().size(); i++) {
                log.error("Operation BudgetItemPeriod: {}", page.getContent().get(i).getBudgetItemPeriod());
            }
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
        return null;
    }

    private ResponseEntity<List<OperationDTO>> getOperationCloseToBudgetItemPeriod(
        Float amount,
        Long categoryId,
        LocalDate date,
        boolean withAlreadyAssigned
    ) {
        log.debug(
            "REST request to get Operation close to amount : {}, category: {}, date, : {}, withAlreadyAssigned : {}",
            amount,
            categoryId,
            date,
            withAlreadyAssigned
        );
        Optional<ApplicationUserDTO> applicationUserOptional = applicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            Long accoundId = applicationUserOptional.get().getId();
            LocalDate fromDate = date.minusDays(20);
            LocalDate toDate = date.plusDays(20);
            List<OperationDTO> operations;
            if (withAlreadyAssigned) {
                operations = operationService.findAllCloseToBudgetItemPeriod(accoundId, categoryId, amount, fromDate, toDate);
            } else {
                operations =
                    operationService.findAllCloseToBudgetItemPeriodWithoutAlreadyAssigned(accoundId, categoryId, amount, fromDate, toDate);
            }
            return new ResponseEntity<>(operations, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * GET /operations-close-to-budget/:budget-item-period-id : get the "id"
     * operation.
     *
     * @param id the id of the budgetItemPeriodId to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the
     *         operationDTO, or with status 404 (Not Found)
     */
    @GetMapping("/mya-operations/close-to-budget/get/{amount}/{categoryId}/{date}")
    public ResponseEntity<List<OperationDTO>> getOperationCloseToBudgetItemPeriod(
        @PathVariable(name = "amount") Float amount,
        @PathVariable(name = "categoryId") Long categoryId,
        @PathVariable(name = "date") LocalDate date
    ) {
        return getOperationCloseToBudgetItemPeriod(amount, categoryId, date, true);
    }

    /**
     * GET /operations-close-to-budget/:budget-item-period-id : get the "id"
     * operation.
     *
     * @param id the id of the budgetItemPeriodId to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the
     *         operationDTO, or with status 404 (Not Found)
     */
    @GetMapping("/mya-operations/close-to-budget/count/{amount}/{categoryId}/{date}")
    public ResponseEntity<Integer> countOperationCloseToBudgetItemPeriod(
        @PathVariable(name = "amount") Float amount,
        @PathVariable(name = "categoryId") Long categoryId,
        @PathVariable(name = "date") LocalDate date
    ) {
        log.debug("REST request to count Operation close to amount: {}, category: {}, date: {}", amount, categoryId, date);

        Optional<ApplicationUserDTO> applicationUserOptional = applicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            int count = operationService.findCountAllCloseToBudgetItemPeriodWithoutAlreadyAssigned(
                applicationUserOptional.get().getId(),
                categoryId,
                amount,
                date.minusDays(20),
                date.plusDays(20)
            );
            return new ResponseEntity<>(count, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * {@code DELETE  /operations/:id} : delete the "id" operation.
     *
     * @param id the id of the operationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/mya-operations/with-budget_item-period/{id}")
    public ResponseEntity<Void> deleteOperation(@PathVariable Long id) {
        log.debug("REST request to delete Operation with operation : {}", id);
        Optional<OperationDTO> operation = this.operationService.findOne(id);
        if (operation.isPresent()) {
            if (operation.get().getBudgetItemPeriod() != null) {
                Optional<BudgetItemPeriodDTO> bipOptional = budgetItemPeriodService.findOne(operation.get().getBudgetItemPeriod().getId());
                if (bipOptional.isPresent()) {
                    bipOptional.get().setOperation(null);
                    budgetItemPeriodService.save(bipOptional.get());
                }
            }
        }
        operationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, OperationResource.ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping(value = "/mya-operations/upload-csv-file", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<MyaImportOperationActions> uploadFile(@RequestParam(name = "file") MultipartFile multipartFile)
        throws IOException {
        log.debug("REST request to upload file");
        if (multipartFile == null || multipartFile.isEmpty()) {
            log.error("REST request cannot upload file");
        }
        log.debug("REST request to upload file. File size : {}", multipartFile.getSize());
        Optional<ApplicationUserDTO> applicationUserOptional = applicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            MyaImportOperationActions actions = myaOperationCSVImporterService.prepareImportOperationFromCSVFile(
                applicationUserOptional.get(),
                multipartFile.getInputStream()
            );
            //log.info("Actions of import :\n{}", actions);
            return ResponseEntity.ok().body(actions);
        } else {
            log.error("REST request error to get User");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/mya-operations/validate-operations-import")
    public ResponseEntity<Void> validateOperationsImport(@Valid @RequestBody MyaImportOperationActions actions) {
        //log.debug("Validate OperationImport Actions : {}", actions);
        for (OperationDTO toCreate : actions.getOperationsToCreate()) {
            operationService.save(toCreate);
        }
        for (MyaOperationToUpdate toUpdate : actions.getOperationsToUpdate()) {
            operationService.updateOperation(toUpdate.getSource(), toUpdate.getTarget());
        }
        for (OperationDTO toHardLock : actions.getOperationsToDeleteWithHardLock()) {
            toHardLock.setDeletingHardLock(true);
            operationService.save(toHardLock);
        }
        for (OperationDTO toDelete : actions.getOperationsToDelete()) {
            operationService.delete(toDelete.getId());
        }
        return ResponseEntity.ok().body(null);
    }
}
