package com.example.common.api;

import com.example.common.dto.DocumentHistoryPageDTO;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Contratto API per la consultazione dello storico documentale.
 */
public interface DocumentHistoryApiContract {

    DocumentHistoryPageDTO search(DocumentType documentType,
                                  Long documentId,
                                  List<DocumentAction> actions,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
                                  String search,
                                  int page,
                                  int size);

    ResponseEntity<byte[]> export(DocumentType documentType,
                                  Long documentId,
                                  List<DocumentAction> actions,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
                                  String search);
}
