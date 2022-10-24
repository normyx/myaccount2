package org.mgoulene.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BudgetItem.
 */
@Entity
@Table(name = "budget_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "budgetitem")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BudgetItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 5, max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Column(name = "jhi_order", nullable = false)
    private Integer order;

    @OneToMany(mappedBy = "budgetItem")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "operation", "budgetItem" }, allowSetters = true)
    private Set<BudgetItemPeriod> budgetItemPeriods = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "subCategories" }, allowSetters = true)
    private Category category;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private ApplicationUser account;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BudgetItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public BudgetItem name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return this.order;
    }

    public BudgetItem order(Integer order) {
        this.setOrder(order);
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Set<BudgetItemPeriod> getBudgetItemPeriods() {
        return this.budgetItemPeriods;
    }

    public void setBudgetItemPeriods(Set<BudgetItemPeriod> budgetItemPeriods) {
        if (this.budgetItemPeriods != null) {
            this.budgetItemPeriods.forEach(i -> i.setBudgetItem(null));
        }
        if (budgetItemPeriods != null) {
            budgetItemPeriods.forEach(i -> i.setBudgetItem(this));
        }
        this.budgetItemPeriods = budgetItemPeriods;
    }

    public BudgetItem budgetItemPeriods(Set<BudgetItemPeriod> budgetItemPeriods) {
        this.setBudgetItemPeriods(budgetItemPeriods);
        return this;
    }

    public BudgetItem addBudgetItemPeriods(BudgetItemPeriod budgetItemPeriod) {
        this.budgetItemPeriods.add(budgetItemPeriod);
        budgetItemPeriod.setBudgetItem(this);
        return this;
    }

    public BudgetItem removeBudgetItemPeriods(BudgetItemPeriod budgetItemPeriod) {
        this.budgetItemPeriods.remove(budgetItemPeriod);
        budgetItemPeriod.setBudgetItem(null);
        return this;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BudgetItem category(Category category) {
        this.setCategory(category);
        return this;
    }

    public ApplicationUser getAccount() {
        return this.account;
    }

    public void setAccount(ApplicationUser applicationUser) {
        this.account = applicationUser;
    }

    public BudgetItem account(ApplicationUser applicationUser) {
        this.setAccount(applicationUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BudgetItem)) {
            return false;
        }
        return id != null && id.equals(((BudgetItem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BudgetItem{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", order=" + getOrder() +
            "}";
    }
}
