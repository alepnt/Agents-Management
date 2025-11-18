package com.example.common.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

/**
 * Contratto API per la generazione di report.
 */
public interface ReportApiContract {

    ResponseEntity<byte[]> closedInvoices(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                          Long agentId);
}
