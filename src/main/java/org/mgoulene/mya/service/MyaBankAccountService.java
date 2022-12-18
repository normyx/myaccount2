package org.mgoulene.mya.service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.mgoulene.mya.domain.MyaReportAmountsByDates;
import org.mgoulene.mya.repository.MyaBankAccountRepository;
import org.mgoulene.mya.service.dto.MyaDateDataPoint;
import org.mgoulene.mya.service.dto.MyaDateDataSinglePointData;
import org.mgoulene.mya.service.dto.MyaDateDataSinglePoints;
import org.mgoulene.mya.service.dto.MyaDateDataStockPoints;
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

    public MyaBankAccountService(
        BankAccountRepository bankAccountRepository,
        BankAccountMapper bankAccountMapper,
        BankAccountSearchRepository bankAccountSearchRepository,
        MyaBankAccountRepository myaBankAccountRepository,
        MyaReportDataService myaReportDataService
    ) {
        super(bankAccountRepository, bankAccountMapper, bankAccountSearchRepository);
        this.myaBankAccountRepository = myaBankAccountRepository;
        this.myaReportDataService = myaReportDataService;
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
}
