package org.mgoulene.mya.web.rest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.mgoulene.mya.service.MyaApplicationUserService;
import org.mgoulene.mya.service.MyaBankAccountService;
import org.mgoulene.mya.service.MyaStockPortfolioItemService;
import org.mgoulene.mya.service.dto.MyaDateDataSinglePoints;
import org.mgoulene.mya.service.dto.MyaDateDataStockPoints;
import org.mgoulene.service.BankAccountQueryService;
import org.mgoulene.service.criteria.BankAccountCriteria;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.mgoulene.service.dto.BankAccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final MyaBankAccountService myaBankAccountService;
    private final MyaStockPortfolioItemService myaStockPortfolioItemService;

    public MyaBankAccountResource(
        BankAccountQueryService bankAccountQueryService,
        MyaApplicationUserService myaApplicationUserService,
        MyaBankAccountService myaBankAccountService,
        MyaStockPortfolioItemService myaStockPortfolioItemService
    ) {
        this.bankAccountQueryService = bankAccountQueryService;
        this.myaApplicationUserService = myaApplicationUserService;
        this.myaBankAccountService = myaBankAccountService;
        this.myaStockPortfolioItemService = myaStockPortfolioItemService;
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

    @GetMapping("/mya-bank-accounts/bank-account-evolution-data-points")
    public ResponseEntity<MyaDateDataSinglePoints> getUserAllBankAccountsEvolutionDataPoints() {
        log.debug("REST request to getUserAllBankAccountsEvolutionDataPoints");
        Optional<ApplicationUserDTO> applicationUserOptional = myaApplicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            MyaDateDataSinglePoints dataPoints = myaBankAccountService.findUserBankAccountDateDataPoints(
                applicationUserOptional.get().getId()
            );

            return ResponseEntity.ok().body(dataPoints);
        }
        return null;
    }

    @GetMapping("/mya-bank-accounts/current-bank-account-evolution-data-points")
    public ResponseEntity<MyaDateDataSinglePoints> getUserCurrentBankAccountsEvolutionDataPoints() {
        log.debug("REST request to getUserCurrentBankAccountsEvolutionDataPoints");
        Optional<ApplicationUserDTO> applicationUserOptional = myaApplicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            MyaDateDataSinglePoints dataPoints = myaBankAccountService.findUserCurrentBankAccountDateDataPoints(
                applicationUserOptional.get().getId()
            );

            return ResponseEntity.ok().body(dataPoints);
        }
        return null;
    }

    @GetMapping("/mya-bank-accounts/savings-bank-account-evolution-data-points")
    public ResponseEntity<MyaDateDataSinglePoints> getUserSavingsBankAccountsEvolutionDataPoints() {
        log.debug("REST request to getUserSavingsBankAccountsEvolutionDataPoints");
        Optional<ApplicationUserDTO> applicationUserOptional = myaApplicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            MyaDateDataSinglePoints dataPoints = myaBankAccountService.findUserSavingsBankAccountDateDataPoints(
                applicationUserOptional.get().getId()
            );

            return ResponseEntity.ok().body(dataPoints);
        }
        return null;
    }

    @GetMapping("/mya-bank-accounts/stock-evolution-data-points")
    public ResponseEntity<MyaDateDataStockPoints> getUserStocksEvolutionDataPoints() {
        log.debug("REST request to getUserStocksEvolutionDataPoints");
        Optional<ApplicationUserDTO> applicationUserOptional = myaApplicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            MyaDateDataStockPoints dataPoints = myaStockPortfolioItemService.findDateDataPoints(applicationUserOptional.get().getId());

            return ResponseEntity.ok().body(dataPoints);
        }
        return null;
    }

    @GetMapping("/mya-bank-accounts/all-evolution-data-points")
    public ResponseEntity<MyaDateDataSinglePoints> getUserAllEvolutionDataPoints() {
        log.debug("REST request to getUserAllEvolutionDataPoints");
        Optional<ApplicationUserDTO> applicationUserOptional = myaApplicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            MyaDateDataSinglePoints bankDataPoints = myaBankAccountService.findUserBankAccountDateDataPoints(
                applicationUserOptional.get().getId()
            );
            MyaDateDataStockPoints stockDataPoints = myaStockPortfolioItemService.findDateDataPoints(applicationUserOptional.get().getId());

            bankDataPoints.merge(stockDataPoints.toSimplePoints());
            return ResponseEntity.ok().body(bankDataPoints);
        }
        return null;
    }

    @GetMapping("/mya-bank-accounts/bank-account-evolution-data-points/{bankAccountId}")
    public ResponseEntity<MyaDateDataSinglePoints> getBankAccountEvolutionDataPoints(@PathVariable Long bankAccountId) {
        log.debug("REST request to getBankAccountEvolutionDataPoints: {}", bankAccountId);

        MyaDateDataSinglePoints bankDataPoints = myaBankAccountService.findBankAccountDateDataPoints(bankAccountId);

        return ResponseEntity.ok().body(bankDataPoints);
    }

    @GetMapping("/mya-bank-accounts/stock-symbol-evolution-data-points/{symbol}")
    public ResponseEntity<MyaDateDataStockPoints> getUserStockSymbolEvolutionDataPoints(@PathVariable String symbol) {
        log.debug("REST request to getUserStockSymbolEvolutionDataPoints: {}", symbol);
        Optional<ApplicationUserDTO> applicationUserOptional = myaApplicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            MyaDateDataStockPoints dataPoints = myaStockPortfolioItemService.findSymbolDateDataPoints(
                applicationUserOptional.get().getId(),
                symbol
            );

            return ResponseEntity.ok().body(dataPoints);
        }
        return null;
    }
}
