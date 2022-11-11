package org.mgoulene.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A StockMarketData.
 */
@Entity
@Table(name = "stock_market_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "stockmarketdata")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockMarketData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 10)
    @Column(name = "symbol", length = 10, nullable = false)
    private String symbol;

    @NotNull
    @Column(name = "data_date", nullable = false)
    private LocalDate dataDate;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "close_value", nullable = false)
    private Float closeValue;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockMarketData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public StockMarketData symbol(String symbol) {
        this.setSymbol(symbol);
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDate getDataDate() {
        return this.dataDate;
    }

    public StockMarketData dataDate(LocalDate dataDate) {
        this.setDataDate(dataDate);
        return this;
    }

    public void setDataDate(LocalDate dataDate) {
        this.dataDate = dataDate;
    }

    public Float getCloseValue() {
        return this.closeValue;
    }

    public StockMarketData closeValue(Float closeValue) {
        this.setCloseValue(closeValue);
        return this;
    }

    public void setCloseValue(Float closeValue) {
        this.closeValue = closeValue;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockMarketData)) {
            return false;
        }
        return id != null && id.equals(((StockMarketData) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockMarketData{" +
            "id=" + getId() +
            ", symbol='" + getSymbol() + "'" +
            ", dataDate='" + getDataDate() + "'" +
            ", closeValue=" + getCloseValue() +
            "}";
    }
}
