package org.mgoulene.mya.service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.mgoulene.mya.repository.MyaBankAccountRepository;
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

    public MyaBankAccountService(
        BankAccountRepository bankAccountRepository,
        BankAccountMapper bankAccountMapper,
        BankAccountSearchRepository bankAccountSearchRepository,
        MyaBankAccountRepository myaBankAccountRepository
    ) {
        super(bankAccountRepository, bankAccountMapper, bankAccountSearchRepository);
        this.myaBankAccountRepository = myaBankAccountRepository;
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
}
