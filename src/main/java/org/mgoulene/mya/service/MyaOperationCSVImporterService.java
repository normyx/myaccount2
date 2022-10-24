package org.mgoulene.mya.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.mgoulene.config.ApplicationProperties;
import org.mgoulene.mya.service.dto.MyaImportOperationActions;
import org.mgoulene.mya.service.dto.MyaOperationKey;
import org.mgoulene.mya.service.dto.OperationCSVDTO;
import org.mgoulene.mya.service.mapper.OperationCSVMapper;
import org.mgoulene.service.SubCategoryService;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.mgoulene.service.dto.BankAccountDTO;
import org.mgoulene.service.dto.OperationDTO;
import org.mgoulene.service.dto.SubCategoryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Importation of the Operations from CSV
 * File.
 */
@Service
@Transactional
public class MyaOperationCSVImporterService {

    private final Logger log = LoggerFactory.getLogger(MyaOperationCSVImporterService.class);

    private final OperationCSVMapper operationCSVMapper;

    private final SubCategoryService subCategoryService;

    private final MyaBankAccountService bankAccountService;

    private final MyaOperationService operationService;

    public MyaOperationCSVImporterService(
        OperationCSVMapper operationCSVMapper,
        SubCategoryService subCategoryService,
        ApplicationProperties applicationProperties,
        MyaOperationService operationService,
        MyaBankAccountService bankAccountService
    ) {
        this.operationCSVMapper = operationCSVMapper;
        this.subCategoryService = subCategoryService;
        this.operationService = operationService;
        this.bankAccountService = bankAccountService;
    }

    public MyaImportOperationActions prepareImportOperationFromCSVFile(ApplicationUserDTO account, InputStream is) {
        //log.info("--Step 01");
        MyaImportOperationActions actions = new MyaImportOperationActions();
        CsvToBean<OperationCSVDTO> csvToBean = new CsvToBeanBuilder<OperationCSVDTO>(
            new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_16))
        )
            .withType(OperationCSVDTO.class)
            .withSeparator('\t')
            .withSkipLines(1)
            .withKeepCarriageReturn(true)
            .withIgnoreLeadingWhiteSpace(true)
            .build();
        List<OperationCSVDTO> csvList = csvToBean.parse();
        //log.info("--Step 02");
        // Get the HashMap subCategories
        Map<String, SubCategoryDTO> subCategoriesMap = new HashMap<String, SubCategoryDTO>();

        List<SubCategoryDTO> subCategories = subCategoryService.findAll();
        for (SubCategoryDTO subCategoryDTO : subCategories) {
            String encoded = new String(subCategoryDTO.getSubCategoryName().getBytes(), StandardCharsets.ISO_8859_1);
            //log.warn("SubCategory '{}' {}", encoded, encoded.hashCode());
            subCategoriesMap.put(encoded, subCategoryDTO);
        }
        //log.info("--Step 03");
        // Get the HashMap of BankAccount

        Map<String, BankAccountDTO> bankAccountNameMap = new HashMap<>();
        List<BankAccountDTO> bankAccountDTOs = bankAccountService.findAllByAccountId(account.getId());
        for (BankAccountDTO bankAccountDTO : bankAccountDTOs) {
            bankAccountNameMap.put(bankAccountDTO.getAccountName(), bankAccountDTO);
        }
        //log.info("--Step 04");
        for (OperationCSVDTO csvDto : csvList) {
            //log.debug("Get Subcatégory and Bank Account for: {}", csvDto);
            // Get the subcategory id
            if (csvDto.getSubCategoryName() != null) {
                //log.warn("Subcategories Map {}", subCategoriesMap);
                String encoded = new String(csvDto.getSubCategoryName().getBytes(), StandardCharsets.ISO_8859_1);
                //log.warn("One SubCategory Name '{}' {}", encoded, encoded.hashCode());
                SubCategoryDTO subCategory = subCategoriesMap.get(encoded);
                //log.warn("gettting SC : {}", subCategory);
                // log.debug("Retrieving the subCategory Id {}, for the Name : {}",
                // subCategoryId,
                // "\"" + csvDto.getSubCategoryName() + "\"");
                csvDto.setSubCategory(subCategory);
            } else {
                log.info("Error retrieving the SubCategory Name : {}", "\"" + csvDto.getSubCategoryName() + "\"");
            }

            String accountName = csvDto.getAccountName();
            BankAccountDTO bankAccountDTO = bankAccountNameMap.get(accountName);
            // log.debug("BankAccount for {} is {}", accountName, bankAccountDTO);
            // Need to create a BankAccount if it does not exists
            if (bankAccountDTO == null) {
                bankAccountDTO = new BankAccountDTO();
                bankAccountDTO.setAccountBank(csvDto.getBankName());
                bankAccountDTO.setAccountName(csvDto.getAccountName());
                bankAccountDTO.setAccount(account);
                bankAccountDTO.setInitialAmount(0f);
                bankAccountDTO.setArchived(false);
                bankAccountDTO = bankAccountService.save(bankAccountDTO);
                log.warn("Creating BankAccount : {}", bankAccountDTO);
                bankAccountNameMap.put(bankAccountDTO.getAccountName(), bankAccountDTO);
            }
            csvDto.setAccount(account);
            csvDto.setBankAccount(bankAccountDTO);
        }
        //log.info("--Step 05");
        List<OperationDTO> operationDTOs = operationCSVMapper.toDto(csvList);
        //log.info("--Step 06");
        List<OperationDTO> existingOperationDTOs = operationService.findAllByAccount(account.getId());
        //log.info("--Step 07");
        HashMap<MyaOperationKey, Stack<OperationDTO>> operations = operationService.getOperationMap(existingOperationDTOs);
        //log.info("--Step 08");
        for (OperationDTO dto : operationDTOs) {
            //log.warn("Trying to save : {}", dto);
            operationService.importOperation(dto, operations, actions);
        }
        operationService.deleteOperations(operations, actions);
        // Sort List
        Collections.sort(
            actions.getOperationsToDelete(),
            (o1, o2) -> {
                return -o1.getDate().compareTo(o2.getDate());
            }
        );
        Collections.sort(
            actions.getOperationsToDeleteWithHardLock(),
            (o1, o2) -> {
                return -o1.getDate().compareTo(o2.getDate());
            }
        );
        Collections.sort(
            actions.getOperationsToCreate(),
            (o1, o2) -> {
                return -o1.getDate().compareTo(o2.getDate());
            }
        );
        Collections.sort(
            actions.getOperationsToUpdate(),
            (o1, o2) -> {
                return -o1.getTarget().getDate().compareTo(o2.getTarget().getDate());
            }
        );
        Collections.sort(
            actions.getOperationsNotClosed(),
            (o1, o2) -> {
                return -o1.getTarget().getDate().compareTo(o2.getTarget().getDate());
            }
        );

        return actions;
    }
}
