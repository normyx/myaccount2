package org.mgoulene.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.mgoulene.domain.BudgetItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BudgetItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 5, max = 100)
    private String name;

    @NotNull
    private Integer order;

    private CategoryDTO category;

    private ApplicationUserDTO account;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public ApplicationUserDTO getAccount() {
        return account;
    }

    public void setAccount(ApplicationUserDTO account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BudgetItemDTO)) {
            return false;
        }

        BudgetItemDTO budgetItemDTO = (BudgetItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, budgetItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BudgetItemDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", order=" + getOrder() +
            ", category=" + getCategory() +
            ", account=" + getAccount() +
            "}";
    }
}
