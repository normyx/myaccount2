package org.mgoulene.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.mgoulene.domain.StockMarketData} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockMarketDataDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 10)
    private String symbol;

    @NotNull
    private LocalDate dataDate;

    @NotNull
    @DecimalMin(value = "0")
    private Float closeValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDate getDataDate() {
        return dataDate;
    }

    public void setDataDate(LocalDate dataDate) {
        this.dataDate = dataDate;
    }

    public Float getCloseValue() {
        return closeValue;
    }

    public void setCloseValue(Float closeValue) {
        this.closeValue = closeValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockMarketDataDTO)) {
            return false;
        }

        StockMarketDataDTO stockMarketDataDTO = (StockMarketDataDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockMarketDataDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockMarketDataDTO{" +
            "id=" + getId() +
            ", symbol='" + getSymbol() + "'" +
            ", dataDate='" + getDataDate() + "'" +
            ", closeValue=" + getCloseValue() +
            "}";
    }
}
