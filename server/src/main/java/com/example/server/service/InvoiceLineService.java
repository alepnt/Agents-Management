package com.example.server.service; // Definisce il package per i servizi di gestione delle righe fattura.

import com.example.common.dto.InvoiceLineDTO; // Importa il DTO delle righe fattura.
import com.example.server.domain.InvoiceLine; // Importa l'entità InvoiceLine.
import com.example.server.repository.InvoiceLineRepository; // Importa il repository per l'accesso ai dati delle righe fattura.
import com.example.server.service.mapper.InvoiceLineMapper; // Importa il mapper tra entità e DTO delle righe fattura.
import org.springframework.stereotype.Service; // Importa l'annotazione Service di Spring.
import org.springframework.transaction.annotation.Transactional; // Importa il supporto transazionale.
import org.springframework.util.StringUtils; // Importa utility per la gestione delle stringhe.

import java.math.BigDecimal; // Importa BigDecimal per i calcoli monetari.
import java.util.List; // Importa l'interfaccia List.
import java.util.Objects; // Importa metodi per controlli null-safe.
import java.util.Optional; // Importa Optional per gestire risultati opzionali.
import java.util.stream.StreamSupport; // Importa StreamSupport per trasformare Iterable in stream.

@Service // Indica che la classe è un servizio Spring.
public class InvoiceLineService { // Gestisce le operazioni sulle righe di fattura.

    private final InvoiceLineRepository invoiceLineRepository; // Repository per l'accesso ai dati delle righe fattura.

    public InvoiceLineService(InvoiceLineRepository invoiceLineRepository) { // Costruttore che riceve il repository come dipendenza.
        this.invoiceLineRepository = invoiceLineRepository; // Inizializza il repository.
    }

    public List<InvoiceLineDTO> findAll() { // Restituisce tutte le righe di fattura.
        return StreamSupport.stream(invoiceLineRepository.findAll().spliterator(), false) // Converte tutti i record in uno stream sequenziale.
                .map(InvoiceLineMapper::toDto) // Converte ogni entità in DTO.
                .toList(); // Colleziona i DTO in una lista.
    }

    public List<InvoiceLineDTO> findByInvoiceId(Long invoiceId) { // Restituisce le righe di una specifica fattura.
        return invoiceLineRepository.findByInvoiceIdOrderById(Objects.requireNonNull(invoiceId, "invoiceId must not be null")).stream() // Recupera le righe per id fattura e le converte in stream.
                .map(InvoiceLineMapper::toDto) // Converte ogni entità in DTO.
                .toList(); // Colleziona i DTO in una lista.
    }

    public Optional<InvoiceLineDTO> findById(Long id) { // Recupera una riga di fattura per id.
        return invoiceLineRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Valida l'id e interroga il repository.
                .map(InvoiceLineMapper::toDto); // Converte l'entità trovata in DTO.
    }

    @Transactional // Esegue l'operazione in transazione.
    public InvoiceLineDTO create(InvoiceLineDTO dto) { // Crea una nuova riga di fattura.
        InvoiceLine toSave = validateAndNormalize(null, Objects.requireNonNull(dto, "invoice line must not be null")); // Valida, normalizza e crea l'entità da salvare.
        InvoiceLine saved = invoiceLineRepository.save(toSave); // Salva l'entità nel database.
        return InvoiceLineMapper.toDto(saved); // Restituisce il DTO della riga salvata.
    }

    @Transactional // Esegue l'operazione in transazione.
    public Optional<InvoiceLineDTO> update(Long id, InvoiceLineDTO dto) { // Aggiorna una riga di fattura esistente.
        InvoiceLineDTO validated = Objects.requireNonNull(dto, "invoice line must not be null"); // Verifica che il DTO non sia null.
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Valida l'id della riga.
        return invoiceLineRepository.findById(requiredId) // Cerca la riga da aggiornare.
                .map(existing -> validateAndNormalize(existing.getId(), validated)) // Valida e normalizza i nuovi dati mantenendo l'id esistente.
                .map(invoiceLineRepository::save) // Salva l'entità aggiornata.
                .map(InvoiceLineMapper::toDto); // Converte l'entità aggiornata in DTO.
    }

    @Transactional // Esegue l'operazione in transazione.
    public boolean delete(Long id) { // Elimina una riga di fattura se presente.
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Valida l'id fornito.
        if (!invoiceLineRepository.existsById(requiredId)) { // Verifica l'esistenza della riga.
            return false; // Restituisce false se non trovata.
        }
        invoiceLineRepository.deleteById(requiredId); // Cancella la riga dal database.
        return true; // Indica che la cancellazione è avvenuta.
    }

    private InvoiceLine validateAndNormalize(Long id, InvoiceLineDTO dto) { // Valida i dati e crea un'entità InvoiceLine normalizzata.
        Long invoiceId = Objects.requireNonNull(dto.getInvoiceId(), "invoiceId must not be null"); // Valida l'id della fattura associata.
        BigDecimal quantity = normalizeQuantity(dto.getQuantity()); // Normalizza e valida la quantità.
        BigDecimal unitPrice = normalizePrice(dto.getUnitPrice()); // Normalizza e valida il prezzo unitario.
        BigDecimal vatRate = normalizeVatRate(dto.getVatRate()); // Normalizza e valida l'aliquota IVA.
        String description = normalizeText(dto.getDescription()); // Normalizza la descrizione testuale.
        String articleCode = normalizeText(dto.getArticleCode()); // Normalizza il codice articolo.
        BigDecimal total = calculateTotal(quantity, unitPrice, vatRate); // Calcola il totale della riga.
        return new InvoiceLine( // Costruisce la nuova entità InvoiceLine.
                id, // Id della riga (può essere null in creazione).
                invoiceId, // Id della fattura a cui appartiene la riga.
                dto.getArticleId(), // Id dell'articolo associato.
                articleCode, // Codice articolo normalizzato.
                description, // Descrizione normalizzata.
                quantity, // Quantità della riga.
                unitPrice, // Prezzo unitario normalizzato.
                vatRate, // Aliquota IVA normalizzata.
                total // Totale calcolato della riga.
        );
    }

    private BigDecimal normalizeQuantity(BigDecimal quantity) { // Normalizza la quantità impostando un valore minimo.
        BigDecimal normalized = quantity != null ? quantity : BigDecimal.ONE; // Usa la quantità fornita o default a 1.
        if (normalized.signum() <= 0) { // Verifica che la quantità sia positiva.
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero"); // Lancia eccezione se non valida.
        }
        return normalized; // Restituisce la quantità normalizzata.
    }

    private BigDecimal normalizePrice(BigDecimal unitPrice) { // Normalizza il prezzo unitario.
        BigDecimal normalized = unitPrice != null ? unitPrice : BigDecimal.ZERO; // Usa il prezzo fornito o default a zero.
        if (normalized.signum() < 0) { // Controlla che il prezzo non sia negativo.
            throw new IllegalArgumentException("Il prezzo unitario non può essere negativo"); // Lancia eccezione se non valido.
        }
        return normalized; // Restituisce il prezzo normalizzato.
    }

    private BigDecimal normalizeVatRate(BigDecimal vatRate) { // Normalizza l'aliquota IVA.
        if (vatRate == null) { // Se non specificata.
            return BigDecimal.ZERO; // Usa aliquota zero.
        }
        if (vatRate.signum() < 0) { // Controlla che l'aliquota non sia negativa.
            throw new IllegalArgumentException("L'aliquota IVA non può essere negativa"); // Lancia eccezione se non valida.
        }
        return vatRate; // Restituisce l'aliquota validata.
    }

    private String normalizeText(String value) { // Normalizza un campo di testo eliminando spazi e gestendo null.
        return StringUtils.hasText(value) ? value.trim() : null; // Restituisce la stringa ripulita o null se vuota.
    }

    private BigDecimal calculateTotal(BigDecimal quantity, BigDecimal unitPrice, BigDecimal vatRate) { // Calcola il totale della riga.
        BigDecimal subtotal = unitPrice.multiply(quantity); // Calcola il subtotale quantità x prezzo.
        if (vatRate == null) { // Se l'aliquota IVA non è presente.
            return subtotal; // Restituisce il subtotale senza IVA.
        }
        return subtotal.add(subtotal.multiply(vatRate)); // Aggiunge l'IVA al subtotale e restituisce il totale.
    }
}
