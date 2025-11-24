package com.example.server.service; // Definisce il package del servizio di chat

import com.example.common.dto.ChatConversationDTO; // Importa il DTO che rappresenta una conversazione di chat
import com.example.common.dto.ChatMessageDTO; // Importa il DTO per un messaggio di chat
import com.example.common.dto.ChatMessageRequest; // Importa il DTO per le richieste di invio messaggi
import com.example.server.domain.Message; // Importa l'entità di dominio Message
import com.example.server.domain.User; // Importa l'entità di dominio User
import com.example.server.repository.MessageRepository; // Importa il repository per i messaggi
import com.example.server.repository.UserRepository; // Importa il repository per gli utenti
import org.springframework.stereotype.Service; // Importa l'annotazione di servizio Spring
import org.springframework.web.context.request.async.DeferredResult; // Importa il supporto per le risposte asincrone

import java.time.Clock; // Importa Clock per ottenere l'ora corrente
import java.time.Instant; // Importa Instant per gestire timestamp
import java.util.ArrayList; // Importa la lista mutabile per le sottoscrizioni
import java.util.Comparator; // Importa Comparator per ordinare elementi
import java.util.List; // Importa List per collezionare risultati
import java.util.Map; // Importa Map per raggruppare messaggi
import java.util.Objects; // Importa utilità per controlli di nullità
import java.util.Optional; // Importa Optional per valori facoltativi
import java.util.concurrent.atomic.AtomicBoolean; // Importa AtomicBoolean per gestire stati concorrenti
import java.util.stream.Collectors; // Importa Collectors per operazioni sugli stream
import java.util.stream.StreamSupport; // Importa StreamSupport per creare stream da Iterable

@Service // Contrassegna la classe come servizio Spring
public class ChatService { // Incapsula la logica dell'applicazione relativa alla chat

    private static final String TEAM_PREFIX = "team:"; // Prefisso per identificare conversazioni di team

    private final MessageRepository messageRepository; // Repository per la persistenza dei messaggi
    private final UserRepository userRepository; // Repository per la persistenza degli utenti
    private final ChatPublisher chatPublisher; // Componente che gestisce la pubblicazione dei messaggi
    private final Clock clock; // Orologio iniettato per calcolare i timestamp

    public ChatService(MessageRepository messageRepository, // Costruttore che riceve il repository dei messaggi
                       UserRepository userRepository, // Repository degli utenti iniettato
                       ChatPublisher chatPublisher, // Publisher iniettato per notificare i listener
                       Clock clock) { // Clock iniettato per controllare il tempo
        this.messageRepository = messageRepository; // Assegna il repository dei messaggi
        this.userRepository = userRepository; // Assegna il repository degli utenti
        this.chatPublisher = chatPublisher; // Assegna il publisher
        this.clock = clock; // Assegna l'orologio
    }

    public List<ChatConversationDTO> listConversations(Long userId) { // Restituisce le conversazioni accessibili dall'utente
        User user = requireUser(userId); // Recupera l'utente e verifica che esista
        Map<String, List<Message>> grouped = StreamSupport.stream(messageRepository.findAll().spliterator(), false) // Crea uno stream di tutti i messaggi
                .filter(message -> canAccessConversation(user, message.getConversationId())) // Filtra i messaggi accessibili all'utente
                .collect(Collectors.groupingBy(Message::getConversationId)); // Raggruppa i messaggi per conversazione

        return grouped.entrySet().stream() // Scorre i gruppi di conversazione
                .map(entry -> { // Mappa ogni gruppo in un DTO di conversazione
                    List<Message> messages = entry.getValue(); // Estrae i messaggi del gruppo
                    messages.sort(Comparator.comparing(Message::getCreatedAt).reversed()); // Ordina i messaggi per data decrescente
                    Message latest = messages.getFirst(); // Prende l'ultimo messaggio
                    String title = buildConversationTitle(entry.getKey(), user); // Costruisce il titolo da mostrare
                    return new ChatConversationDTO(entry.getKey(), // Crea il DTO della conversazione
                            title, // Imposta il titolo calcolato
                            latest.getCreatedAt(), // Imposta l'ultima attività
                            truncate(latest.getBody())); // Imposta l'anteprima del messaggio
                })
                .sorted(Comparator.comparing(ChatConversationDTO::lastActivity).reversed()) // Ordina le conversazioni per attività recente
                .toList(); // Colleziona i risultati in lista
    }

    public List<ChatMessageDTO> getMessages(Long userId, String conversationId, Instant since) { // Restituisce i messaggi di una conversazione
        User user = requireUser(userId); // Recupera e valida l'utente richiedente
        String requiredConversationId = Objects.requireNonNull(conversationId, "conversationId must not be null"); // Verifica che l'id conversazione non sia nullo
        assertCanAccess(user, requiredConversationId); // Controlla i permessi sulla conversazione

        List<Message> messages = Optional.ofNullable(since) // Costruisce la query in base al parametro since
                .map(instant -> messageRepository // Se since è presente, recupera i messaggi successivi
                        .findByConversationIdAndCreatedAtAfterOrderByCreatedAtAsc(requiredConversationId, instant)) // Query filtrata per data
                .orElseGet(() -> messageRepository.findByConversationIdOrderByCreatedAtAsc(requiredConversationId)); // Altrimenti recupera tutti i messaggi ordinati

        return messages.stream().map(this::toResponse).toList(); // Converte i messaggi in DTO e li restituisce
    }

    public ChatMessageDTO sendMessage(ChatMessageRequest request) { // Invia un nuovo messaggio
        ChatMessageRequest requiredRequest = Objects.requireNonNull(request, "request must not be null"); // Verifica che la richiesta non sia nulla
        User sender = requireUser(requiredRequest.senderId()); // Recupera il mittente assicurandosi che esista
        String conversationId = Objects.requireNonNull(requiredRequest.conversationId(), // Verifica che la conversazione sia indicata
                "conversationId must not be null"); // Messaggio per conversazione nulla
        assertCanAccess(sender, conversationId); // Controlla che il mittente possa accedere alla conversazione
        Message message = Objects.requireNonNull(Message.create(conversationId, sender.getId(), sender.getTeamId(), // Crea il messaggio di dominio
                requiredRequest.body(), Instant.now(clock)), "message must not be null"); // Utilizza l'orologio per impostare la data
        Message saved = messageRepository.save(message); // Salva il messaggio nel database
        ChatMessageDTO response = toResponse(saved); // Converte l'entità salvata in DTO
        chatPublisher.publish(response); // Notifica i listener della conversazione
        return response; // Restituisce il DTO del messaggio inviato
    }

    public void registerConversationListener(Long userId, // Registra un listener per gli aggiornamenti di conversazione
                                             String conversationId, // Identificativo della conversazione da seguire
                                             DeferredResult<List<ChatMessageDTO>> deferredResult) { // Risultato asincrono da completare
        User user = requireUser(userId); // Recupera l'utente che si iscrive
        String requiredConversationId = Objects.requireNonNull(conversationId, "conversationId must not be null"); // Verifica la presenza dell'id conversazione
        DeferredResult<List<ChatMessageDTO>> requiredDeferredResult = Objects.requireNonNull(deferredResult, // Garantisce che il risultato differito non sia nullo
                "deferredResult must not be null"); // Messaggio di errore in caso di null
        assertCanAccess(user, requiredConversationId); // Controlla che l'utente possa ascoltare la conversazione

        AtomicBoolean pending = new AtomicBoolean(true); // Flag per evitare risposte multiple
        List<ChatPublisher.Subscription> subscriptions = new ArrayList<>(); // Lista delle sottoscrizioni attive

        subscriptions.add(chatPublisher.subscribe(requiredConversationId, message -> { // Aggiunge una sottoscrizione al publisher
            if (pending.getAndSet(false)) { // Se è la prima notifica ancora pendente
                ChatMessageDTO nonNullMessage = Objects.requireNonNull(message, "message must not be null"); // Verifica che il messaggio non sia nullo
                List<ChatMessageDTO> result = Objects.requireNonNull(List.of(nonNullMessage), // Crea la lista di risposta con il messaggio
                        "message list must not be null"); // Messaggio di errore in caso di null
                requiredDeferredResult.setResult(result); // Completa il DeferredResult con i dati
            }
        }));

        Runnable cancel = () -> subscriptions.forEach(ChatPublisher.Subscription::cancel); // Operazione di cleanup per annullare tutte le sottoscrizioni
        requiredDeferredResult.onCompletion(cancel); // Registra il cleanup al completamento del DeferredResult
        requiredDeferredResult.onTimeout(() -> { // Gestisce il caso di timeout
            List<ChatMessageDTO> emptyResult = Objects.requireNonNull(List.of(), "result list must not be null"); // Prepara una lista vuota come risposta
            requiredDeferredResult.setResult(emptyResult); // Completa il DeferredResult con risultato vuoto
            cancel.run(); // Esegue il cleanup delle sottoscrizioni
        });
    }

    private User requireUser(Long id) { // Recupera l'utente o solleva eccezione se non presente
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Verifica che l'id non sia nullo
        return userRepository.findById(requiredId) // Cerca l'utente nel repository
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + requiredId)); // Eccezione se l'utente manca
    }

    private void assertCanAccess(User user, String conversationId) { // Verifica l'autorizzazione alla conversazione
        if (!canAccessConversation(user, conversationId)) { // Se l'utente non ha accesso
            throw new IllegalArgumentException("Accesso alla conversazione negato"); // Solleva un'eccezione di accesso negato
        }
    }

    private boolean canAccessConversation(User user, String conversationId) { // Determina se l'utente può accedere a una conversazione
        if (conversationId.startsWith(TEAM_PREFIX)) { // Verifica se la conversazione è di tipo team
            long teamId = Long.parseLong(conversationId, TEAM_PREFIX.length(), conversationId.length(), 10); // Estrae l'id del team dal prefisso
            Long userTeamId = user.getTeamId(); // Recupera il team dell'utente
            return userTeamId != null && userTeamId == teamId; // Consente accesso solo se appartiene allo stesso team
        }
        return true; // Accesso consentito per le altre conversazioni
    }

    private String buildConversationTitle(String conversationId, User user) { // Costruisce il titolo da mostrare per la conversazione
        if (conversationId.startsWith(TEAM_PREFIX)) { // Se è una conversazione di team
            return "Team " + Optional.ofNullable(user.getTeamId()).map(Object::toString).orElse("sconosciuto"); // Usa il team dell'utente o indica sconosciuto
        }
        return conversationId; // Per conversazioni individuali usa l'id
    }

    private String truncate(String body) { // Accorcia il testo del messaggio per anteprima
        if (body == null) { // Se il corpo è nullo
            return ""; // Restituisce stringa vuota
        }
        return body.length() > 60 ? body.substring(0, 57) + "..." : body; // Trunca se più lungo di 60 caratteri
    }

    private ChatMessageDTO toResponse(Message message) { // Converte un'entità Message in DTO
        return new ChatMessageDTO(message.getId(), // Id del messaggio
                message.getConversationId(), // Id della conversazione
                message.getSenderId(), // Id del mittente
                message.getTeamId(), // Id del team del mittente
                message.getBody(), // Testo del messaggio
                message.getCreatedAt()); // Timestamp di creazione
    }
}
