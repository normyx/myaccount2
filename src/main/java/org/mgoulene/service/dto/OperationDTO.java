package org.mgoulene.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.mgoulene.domain.Operation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OperationDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 400)
    private String label;

    @NotNull
    private LocalDate date;

    @NotNull
    private Float amount;

    @Size(max = 400)
    private String note;

    @Size(max = 20)
    private String checkNumber;

    @NotNull
    private Boolean isUpToDate;

    private Boolean deletingHardLock;

    private SubCategoryDTO subCategory;

    private ApplicationUserDTO account;

    private BankAccountDTO bankAccount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
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

    public Boolean getIsUpToDate() {
        return isUpToDate;
    }

    public void setIsUpToDate(Boolean isUpToDate) {
        this.isUpToDate = isUpToDate;
    }

    public Boolean getDeletingHardLock() {
        return deletingHardLock;
    }

    public void setDeletingHardLock(Boolean deletingHardLock) {
        this.deletingHardLock = deletingHardLock;
    }

    public SubCategoryDTO getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategoryDTO subCategory) {
        this.subCategory = subCategory;
    }

    public ApplicationUserDTO getAccount() {
        return account;
    }

    public void setAccount(ApplicationUserDTO account) {
        this.account = account;
    }

    public BankAccountDTO getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccountDTO bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OperationDTO)) {
            return false;
        }

        OperationDTO operationDTO = (OperationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, operationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OperationDTO{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", date='" + getDate() + "'" +
            ", amount=" + getAmount() +
            ", note='" + getNote() + "'" +
            ", checkNumber='" + getCheckNumber() + "'" +
            ", isUpToDate='" + getIsUpToDate() + "'" +
            ", deletingHardLock='" + getDeletingHardLock() + "'" +
            ", subCategory=" + getSubCategory() +
            ", account=" + getAccount() +
            ", bankAccount=" + getBankAccount() +
            "}";
    }
}
