package com.example.common.command;                               // Package che contiene le implementazioni del pattern Command.

import java.util.Collection;                         // DTO che rappresenta una fattura registrata nel contesto.

import org.springframework.lang.NonNull;                                      // Tipo utilizzato per restituire collezioni di fatture.

import com.example.common.dto.InvoiceDTO;                          // Annotazione che garantisce la non-nullità degli argomenti e del ritorno.

/**
 * Restituisce tutte le fatture presenti nel contesto.
 * Comando di sola lettura che non modifica lo stato interno del CommandContext.
 */
public class ListInvoicesCommand implements Command<Collection<InvoiceDTO>> { 
    // Comando che produce la collezione di tutte le fatture disponibili.

    @Override
    public @NonNull Collection<InvoiceDTO> execute(@NonNull CommandContext context) { 
        // Esegue il comando utilizzando un contesto non nullo e valido.

        return context.invoiceValues();                           // Restituisce la collection delle fatture attualmente presenti.
    }
}                                                                  // Fine della classe ListInvoicesCommand.
