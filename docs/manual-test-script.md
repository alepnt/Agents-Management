# Script di test manuale

Questo script descrive i passaggi consigliati per verificare a mano i flussi principali dell'applicazione desktop JavaFX.

## 1. Login
1. Avvia l'applicazione (`mvn -pl client javafx:run` oppure eseguendo l'eseguibile).
2. Verifica che nella schermata di login siano visibili i campi **Email**, **Nome visualizzato**, **Azure ID**, **Access Token** e i pulsanti **Accedi** e **Registrati**.
3. Inserisci un'email valida, un nome visualizzato, l'Azure ID e un token di accesso Microsoft valido.
4. Premi **Accedi** e controlla che:
   - Venga mostrato un messaggio di benvenuto.
   - Si apra la dashboard "Gestore Agenti".
5. (Negativo) Lascia vuoti i campi obbligatori e verifica che vengano mostrati messaggi di errore e nessuna navigazione avvenga.

## 2. Navigazione dashboard
1. Verifica la presenza delle schede **Fatture**, **Contratti**, **Clienti**, **Articoli**, **Storico** e **Statistiche**.
2. Passa da una scheda all'altra e controlla che i form e le tabelle vengano aggiornati senza errori.
3. Usa il pulsante **Aggiorna** nella toolbar per ricaricare i dati e controlla il messaggio nella barra di stato.

## 3. CRUD entità principali
Eseguire i test con dati di prova coerenti con l'ambiente.

### Fatture
1. Nella scheda **Fatture**, compila i campi principali (numero, cliente, contratto, importi e date) e premi **Crea**.
2. Seleziona la riga appena creata e verifica che i campi vengano popolati.
3. Modifica uno o più campi e premi **Aggiorna**; controlla che la tabella mostri i dati aggiornati.
4. Registra un pagamento con **Registra pagamento** e verifica lo stato aggiornato.
5. Premi **Elimina** e conferma che la riga scompaia dalla tabella.

### Contratti
1. Passa alla scheda **Contratti** e crea un nuovo contratto compilando agente, cliente, descrizione, valore, stato e date.
2. Seleziona il contratto, aggiorna un campo e salva con **Aggiorna**.
3. Premi **Elimina** e assicurati che la riga venga rimossa.

### Clienti
1. Nella scheda **Clienti**, crea un nuovo cliente con nome, email, telefono, P.IVA e indirizzo.
2. Aggiorna un campo (es. telefono) e salva con **Aggiorna**.
3. Elimina il cliente e verifica la scomparsa dalla tabella e dalle combo di fatture/contratti.

### Articoli
1. Nella scheda **Articoli**, crea un articolo inserendo codice, nome, prezzo, IVA e unità di misura.
2. Aggiorna un campo (es. prezzo) e salva con **Aggiorna**.
3. Elimina l'articolo e verifica che non sia più selezionabile nelle righe fattura.

## 4. Storico e statistiche
1. Nella scheda **Storico**, applica filtri per tipo documento e ID e verifica che la tabella venga popolata; prova anche l'esportazione CSV e il report PDF.
2. Nella scheda **Statistiche**, cambia l'anno dal menu a tendina e controlla che grafici e tabelle si aggiornino.

## 5. Logout / fine sessione
1. Chiudi l'applicazione e riaprila per verificare il ripristino automatico della sessione valida (se presente).
2. Se la sessione è scaduta o non valida, assicurati che venga richiesto nuovamente il login.
