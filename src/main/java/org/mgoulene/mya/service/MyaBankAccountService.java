package org.mgoulene.mya.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.mgoulene.domain.enumeration.BankAccountType;
import org.mgoulene.mya.domain.MyaReportAmountsByDates;
import org.mgoulene.mya.repository.MyaBankAccountRepository;
import org.mgoulene.mya.service.dto.MyaAllBankDataPoints;
import org.mgoulene.mya.service.dto.MyaDateDataPoint;
import org.mgoulene.mya.service.dto.MyaDateDataPoints;
import org.mgoulene.mya.service.dto.MyaDateDataSinglePointData;
import org.mgoulene.mya.service.dto.MyaDateDataSinglePoints;
import org.mgoulene.mya.service.dto.MyaDateDataStockPoints;
import org.mgoulene.mya.service.dto.MyaLocalDateComparator;
import org.mgoulene.repository.BankAccountRepository;
import org.mgoulene.repository.search.BankAccountSearchRepository;
import org.mgoulene.service.BankAccountService;
import org.mgoulene.service.dto.BankAccountDTO;
import org.mgoulene.service.mapper.BankAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MyaBankAccountService extends BankAccountService {

    private final Logger log = LoggerFactory.getLogger(MyaBankAccountService.class);

    private final MyaBankAccountRepository myaBankAccountRepository;
    private final MyaReportDataService myaReportDataService;
    private final MyaStockPortfolioItemService myaStockPortfolioItemService;

    public MyaBankAccountService(
        BankAccountRepository bankAccountRepository,
        BankAccountMapper bankAccountMapper,
        BankAccountSearchRepository bankAccountSearchRepository,
        MyaBankAccountRepository myaBankAccountRepository,
        MyaReportDataService myaReportDataService,
        MyaStockPortfolioItemService myaStockPortfolioItemService
    ) {
        super(bankAccountRepository, bankAccountMapper, bankAccountSearchRepository);
        this.myaBankAccountRepository = myaBankAccountRepository;
        this.myaReportDataService = myaReportDataService;
        this.myaStockPortfolioItemService = myaStockPortfolioItemService;
    }

    /**
     * Get all the bankAccounts for a given user.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<BankAccountDTO> findAllByAccountId(Long accountId) {
        log.debug("Request to get all BankAccounts");
        return myaBankAccountRepository
            .findByAccountId(accountId)
            .stream()
            .map(bankAccountMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public List<BankAccountDTO> findByAccountIdAndType(Long accountId, BankAccountType type) {
        log.debug("Request to get all findByAccountIdAndType");
        return myaBankAccountRepository
            .findByAccountIdAndType(accountId, type)
            .stream()
            .map(bankAccountMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public MyaDateDataSinglePoints findUserBankAccountDateDataPoints(Long applicationUser) {
        log.debug("Request to findAllUserDateDataPoint");
        List<MyaReportAmountsByDates> rawData = myaReportDataService.findUserBankAccountDateDataPoints(applicationUser);
        MyaDateDataSinglePoints points = new MyaDateDataSinglePoints(null);
        for (MyaReportAmountsByDates data : rawData) {
            points.addPoint(
                new MyaDateDataPoint<MyaDateDataSinglePointData>(data.getDate(), new MyaDateDataSinglePointData(data.getAmount()))
            );
        }
        return points;
    }

    @Transactional(readOnly = true)
    public MyaDateDataSinglePoints findUserCurrentBankAccountDateDataPoints(Long applicationUser) {
        log.debug("Request to findAllUserDateDataPoint");
        List<MyaReportAmountsByDates> rawData = myaReportDataService.findUserCurrentBankAccountDateDataPoints(applicationUser);
        MyaDateDataSinglePoints points = new MyaDateDataSinglePoints(null);
        for (MyaReportAmountsByDates data : rawData) {
            points.addPoint(
                new MyaDateDataPoint<MyaDateDataSinglePointData>(data.getDate(), new MyaDateDataSinglePointData(data.getAmount()))
            );
        }
        return points;
    }

    @Transactional(readOnly = true)
    public MyaDateDataSinglePoints findUserSavingsBankAccountDateDataPoints(Long applicationUser) {
        log.debug("Request to findAllUserDateDataPoint");
        List<MyaReportAmountsByDates> rawData = myaReportDataService.findUserSavingsBankAccountDateDataPoints(applicationUser);
        MyaDateDataSinglePoints points = new MyaDateDataSinglePoints(null);
        for (MyaReportAmountsByDates data : rawData) {
            points.addPoint(
                new MyaDateDataPoint<MyaDateDataSinglePointData>(data.getDate(), new MyaDateDataSinglePointData(data.getAmount()))
            );
        }
        return points;
    }

    @Transactional(readOnly = true)
    public MyaDateDataSinglePoints findBankAccountDateDataPoints(Long bankAccountId) {
        log.debug("Request to findBankAccountDateDataPoints");
        List<MyaReportAmountsByDates> rawData = myaReportDataService.findBankAccountDateDataPoints(bankAccountId);
        MyaDateDataSinglePoints points = new MyaDateDataSinglePoints(null);
        for (MyaReportAmountsByDates data : rawData) {
            points.addPoint(
                new MyaDateDataPoint<MyaDateDataSinglePointData>(data.getDate(), new MyaDateDataSinglePointData(data.getAmount()))
            );
        }
        return points;
    }

    @Transactional(readOnly = true)
    public MyaDateDataSinglePoints findRealEstateBankAccountDateDataPoints(Long bankAccountId) {
        log.debug("Request to findRealEstateBankAccountDateDataPoints");
        List<MyaReportAmountsByDates> rawData = myaReportDataService.findRealEstateBankAccountDateDataPoints(bankAccountId);
        MyaDateDataSinglePoints points = new MyaDateDataSinglePoints(null);
        for (MyaReportAmountsByDates data : rawData) {
            points.addPoint(
                new MyaDateDataPoint<MyaDateDataSinglePointData>(data.getDate(), new MyaDateDataSinglePointData(data.getAmount()))
            );
        }
        return points;
    }

    @Transactional(readOnly = true)
    public MyaDateDataSinglePoints findUserRealEstateBankAccountDateDataPoints(Long applicationUserId) {
        log.debug("Request to findUserRealEstateBankAccountDateDataPoints");
        List<BankAccountDTO> realEstateBankAccounts = findByAccountIdAndType(applicationUserId, BankAccountType.REAL_ESTATE);
        MyaDateDataSinglePoints bankDataPoints = null;
        for (BankAccountDTO bankAccountDTO : realEstateBankAccounts) {
            if (bankDataPoints == null) {
                bankDataPoints = findRealEstateBankAccountDateDataPoints(bankAccountDTO.getId());
            } else {
                bankDataPoints.merge(findRealEstateBankAccountDateDataPoints(bankAccountDTO.getId()));
            }
        }

        return bankDataPoints;
    }

    @Transactional(readOnly = true)
    public MyaAllBankDataPoints findAllBankAccountDateDataPoints(Long applicationUserId) {
        log.debug("Request to findRealEstateBankAccountDateDataPoints");
        long timestamp = System.currentTimeMillis();
        long duration = 0;
        MyaDateDataSinglePoints realEstatePoints = findUserRealEstateBankAccountDateDataPoints(applicationUserId);
        MyaDateDataSinglePoints savingsPoints = findUserSavingsBankAccountDateDataPoints(applicationUserId);
        MyaDateDataSinglePoints currentPoints = findUserCurrentBankAccountDateDataPoints(applicationUserId);
        MyaDateDataSinglePoints stockData = myaStockPortfolioItemService.findDateDataPoints(applicationUserId).toSimplePoints();
        duration = System.currentTimeMillis() - timestamp;
        log.debug("Getting points in {} ms", duration);
        timestamp = System.currentTimeMillis();
        HashMap<LocalDate, Object> dateKeys = new HashMap<>();

        for (LocalDate d : realEstatePoints.getDates()) {
            dateKeys.put(d, null);
        }
        for (LocalDate d : savingsPoints.getDates()) {
            dateKeys.put(d, null);
        }
        for (LocalDate d : currentPoints.getDates()) {
            dateKeys.put(d, null);
        }
        for (LocalDate d : stockData.getDates()) {
            dateKeys.put(d, null);
        }
        ArrayList<LocalDate> dates = new ArrayList<>(dateKeys.keySet());
        Collections.sort(dates, new MyaLocalDateComparator());
        duration = System.currentTimeMillis() - timestamp;
        log.debug("getting ordered dates in {} ms", duration);
        timestamp = System.currentTimeMillis();
        realEstatePoints.normalize(dates);
        savingsPoints.normalize(dates);
        currentPoints.normalize(dates);
        stockData.normalize(dates);
        duration = System.currentTimeMillis() - timestamp;
        log.debug("Normalizing in in {} ms", duration);
        timestamp = System.currentTimeMillis();
        return new MyaAllBankDataPoints(realEstatePoints, savingsPoints, currentPoints, stockData);
    }

    private List<LocalDate> mergeDate(List<LocalDate> dates1, List<LocalDate> dates2) {
        HashMap<LocalDate, Object> dateKeys = new HashMap<>();
        for (LocalDate d : dates1) {
            dateKeys.put(d, null);
        }
        for (LocalDate d : dates2) {
            dateKeys.put(d, null);
        }
        ArrayList<LocalDate> dates = new ArrayList<>(dateKeys.keySet());
        Collections.sort(dates, new MyaLocalDateComparator());
        return dates;
    }
}
