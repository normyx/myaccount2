package org.mgoulene.mya.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.mgoulene.domain.StockMarketData;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MyaStockMarketDataRepository extends JpaRepository<StockMarketData, Long>, JpaSpecificationExecutor<StockMarketData> {
    @Query(
        "SELECT stockMarketData FROM StockMarketData stockMarketData WHERE stockMarketData.dataDate = (select max(s.dataDate) from StockMarketData s where s.symbol = :symbol) AND stockMarketData.symbol = :symbol"
    )
    Optional<StockMarketData> findLastStockMarketData(@Param("symbol") String symbol);

    @Query(
        "SELECT stockMarketData FROM StockMarketData stockMarketData WHERE stockMarketData.dataDate = :dataDate AND stockMarketData.symbol = :symbol"
    )
    Optional<StockMarketData> findStockMarketDataForDate(@Param("symbol") String symbol, @Param("dataDate") LocalDate dataDate);

    @Query(
        "SELECT stockMarketData FROM StockMarketData stockMarketData WHERE stockMarketData.symbol = :symbol ORDER BY stockMarketData.dataDate ASC"
    )
    List<StockMarketData> findStockMarketData(@Param("symbol") String symbol);
}
