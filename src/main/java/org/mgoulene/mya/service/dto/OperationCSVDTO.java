package org.mgoulene.mya.service.dto;

import com.opencsv.bean.CsvBindByPosition;
import java.io.Serializable;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.mgoulene.service.dto.BankAccountDTO;
import org.mgoulene.service.dto.SubCategoryDTO;

/**
 * A DTO for the Operation entity.
 */
public class OperationCSVDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @CsvBindByPosition(position = 1)
    private String label;

    @CsvBindByPosition(position = 0)
    private String date;

    @CsvBindByPosition(position = 3)
    private String amount;

    @CsvBindByPosition(position = 4)
    private String note;

    @CsvBindByPosition(position = 5)
    private String checkNumber;

    @CsvBindByPosition(position = 2)
    private String subCategoryName;

    @CsvBindByPosition(position = 7)
    private String accountName;

    @CsvBindByPosition(position = 8)
    private String bankName;

    private SubCategoryDTO subCategory;

    private ApplicationUserDTO account;

    private BankAccountDTO bankAccount;

    public String getLabel() {
        return label;
    }

    /**
     * @return the bankAccountId
     */
    public BankAccountDTO getBankAccount() {
        return bankAccount;
    }

    /**
     * @param bankAccountId the bankAccountId to set
     */
    public void setBankAccount(BankAccountDTO bankAccountId) {
        this.bankAccount = bankAccountId;
    }

    /**
     * @return the accountId
     */
    public ApplicationUserDTO getAccount() {
        return account;
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccount(ApplicationUserDTO account) {
        this.account = account;
    }

    /**
     * @return the subCateboryId
     */
    public SubCategoryDTO getSubCategory() {
        return subCategory;
    }

    /**
     * @param subCateboryId the subCateboryId to set
     */
    public void setSubCategory(SubCategoryDTO subCategory) {
        this.subCategory = subCategory;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        // workaround for , décimal delimiter versus .
        this.amount = amount.replace(',', '.');
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String toString() {
        return (
            "OperationCSVDTO{" +
            ", label='" +
            getLabel() +
            "'" +
            ", date='" +
            getDate() +
            "'" +
            ", amount=" +
            getAmount() +
            ", note='" +
            getNote() +
            "'" +
            ", checkNumber='" +
            getCheckNumber() +
            "'" +
            ", subCategory=''" +
            getSubCategoryName() +
            "'" +
            ", bankAccount=''" +
            getBankAccount() +
            "''" +
            ", accountName=''" +
            getAccountName() +
            "''" +
            "}"
        );
    }
}
