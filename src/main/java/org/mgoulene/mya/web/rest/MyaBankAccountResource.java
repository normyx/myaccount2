package org.mgoulene.mya.web.rest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.mgoulene.mya.service.MyaApplicationUserService;
import org.mgoulene.service.BankAccountQueryService;
import org.mgoulene.service.criteria.BankAccountCriteria;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.mgoulene.service.dto.BankAccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.service.filter.LongFilter;

/**
 * REST controller for managing {@link org.mgoulene.domain.BudgetItem}.
 */
@RestController
@RequestMapping("/api")
public class MyaBankAccountResource {

    private final Logger log = LoggerFactory.getLogger(MyaBankAccountResource.class);

    private static final String ENTITY_NAME = "bankAccount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BankAccountQueryService bankAccountQueryService;
    private final MyaApplicationUserService myaApplicationUserService;

    public MyaBankAccountResource(BankAccountQueryService bankAccountQueryService, MyaApplicationUserService myaApplicationUserService) {
        this.bankAccountQueryService = bankAccountQueryService;
        this.myaApplicationUserService = myaApplicationUserService;
    }

    /**
     * {@code GET  /budget-items} : get all the budgetItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of budgetItems in body.
     */
    @GetMapping("/mya/bank-accounts/with-signedin-user")
    public ResponseEntity<List<BankAccountDTO>> getAllBankAccountsWithUser(BankAccountCriteria criteria) {
        log.debug("REST request to get BankAccounts with Application Userby criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        Optional<ApplicationUserDTO> applicationUserOptional = myaApplicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            LongFilter userFilter = new LongFilter();
            userFilter.setEquals(applicationUserOptional.get().getId());
            criteria.setAccountId(userFilter);

            List<BankAccountDTO> entityList = bankAccountQueryService.findByCriteria(criteria);
            Collections.sort(
                entityList,
                (BankAccountDTO ba1, BankAccountDTO ba2) -> {
                    if (ba1.getArchived() != ba2.getArchived()) {
                        if (!ba1.getArchived()) return -1; else return 1;
                    }
                    if (ba1.getAccountBank() != ba2.getAccountBank()) {
                        return ba1.getAccountBank().compareTo(ba2.getAccountBank());
                    }
                    return ba1.getAccountName().compareTo(ba2.getAccountName());
                }
            );
            return ResponseEntity.ok().body(entityList);
        }
        return null;
    }
}
