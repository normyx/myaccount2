package org.mgoulene.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.mgoulene.domain.BudgetItemPeriod} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BudgetItemPeriodDTO implements Serializable {

    private Long id;

    private LocalDate date;

    @NotNull
    private LocalDate month;

    @NotNull
    private Float amount;

    private Boolean isSmoothed;

    private Boolean isRecurrent;

    private OperationDTO operation;

    private BudgetItemDTO budgetItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Boolean getIsSmoothed() {
        return isSmoothed;
    }

    public void setIsSmoothed(Boolean isSmoothed) {
        this.isSmoothed = isSmoothed;
    }

    public Boolean getIsRecurrent() {
        return isRecurrent;
    }

    public void setIsRecurrent(Boolean isRecurrent) {
        this.isRecurrent = isRecurrent;
    }

    public OperationDTO getOperation() {
        return operation;
    }

    public void setOperation(OperationDTO operation) {
        this.operation = operation;
    }

    public BudgetItemDTO getBudgetItem() {
        return budgetItem;
    }

    public void setBudgetItem(BudgetItemDTO budgetItem) {
        this.budgetItem = budgetItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BudgetItemPeriodDTO)) {
            return false;
        }

        BudgetItemPeriodDTO budgetItemPeriodDTO = (BudgetItemPeriodDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, budgetItemPeriodDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BudgetItemPeriodDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", month='" + getMonth() + "'" +
            ", amount=" + getAmount() +
            ", isSmoothed='" + getIsSmoothed() + "'" +
            ", isRecurrent='" + getIsRecurrent() + "'" +
            ", operation=" + getOperation() +
            ", budgetItem=" + getBudgetItem() +
            "}";
    }
}
