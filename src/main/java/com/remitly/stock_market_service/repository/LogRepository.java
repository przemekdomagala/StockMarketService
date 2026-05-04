package com.remitly.stock_market_service.repository;

import com.remitly.stock_market_service.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findAllByOrderByIdAsc();
}
