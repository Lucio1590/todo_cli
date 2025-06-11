## Attributi principali

- **Logger logger**: per registrare errori e informazioni di debug.
- **AuthenticationService authService**: servizio per login, logout, registrazione e gestione profilo utente.
- **Scanner scanner**: per la lettura degli input dell’utente dalla CLI.

---

## Costruttore

### `AuthenticationCommandHandler(AuthenticationService authService, Scanner scanner)`
Inizializza il servizio di autenticazione e lo scanner per l’input utente.

---

## Metodi pubblici

### `boolean handleLogin()`
**Cosa fa:** Gestisce il processo di login utente.

**Logica dettagliata:**
- Pulisce lo schermo e mostra l’header.
- Chiede username e password (non effettua mascheramento del password, nota indicata nei commenti).
- Controlla che i campi non siano vuoti; altrimenti mostra errore e attende invio.
- Usa il servizio per autenticare l’utente.
- Se login riuscito, mostra messaggio di benvenuto; altrimenti, mostra errore.
- Gestisce eccezioni specifiche di autenticazione e generiche.

---

### `boolean handleRegistration()`
**Cosa fa:** Gestisce la registrazione di un nuovo utente.

**Logica dettagliata:**
- Pulisce lo schermo e mostra l’header.
- Chiede username, email, password, conferma password, nome e cognome.
- Controlla che nessun campo obbligatorio sia vuoto.
- Verifica che le due password coincidano.
- Procede con la registrazione tramite il servizio.
- Mostra esito e messaggio di benvenuto.
- Gestisce eccezioni di autenticazione e generiche.

---

### `void handleLogout()`
**Cosa fa:** Gestisce il logout dell’utente corrente.

**Logica dettagliata:**
- Pulisce lo schermo e mostra l’header.
- Verifica se un utente è loggato.
- Chiede conferma prima di eseguire il logout.
- Se confermato, esegue il logout e mostra messaggio di successo; altrimenti mostra che l’operazione è stata annullata o che nessun utente è loggato.
- Gestisce eventuali errori.

---

### `void displayUserProfile()`
**Cosa fa:** Mostra le informazioni del profilo utente corrente.

**Logica dettagliata:**
- Pulisce lo schermo e mostra l’header.
- Recupera l’utente corrente tramite il servizio.
- Se non c’è un utente loggato, mostra errore.
- Mostra informazioni: username, email, nome completo, stato account (attivo/inattivo), data di iscrizione, ultima modifica profilo (se diversa).
- Gestisce eventuali errori.

---

### `void handleProfileUpdate()`
**Cosa fa:** Permette all’utente di aggiornare il proprio profilo (email, nome, cognome).

**Logica dettagliata:**
- Mostra i valori attuali e permette di lasciarli invariati (basta premere invio).
- Controlla se sono stati effettivamente cambiati dei dati.
- Mostra un riepilogo dei cambiamenti e chiede conferma.
- Se confermato, aggiorna il profilo tramite il servizio.
- Mostra esito e gestisce errori.

---

### `void handlePasswordChange()`
**Cosa fa:** Gestisce il cambio della password utente.

**Logica dettagliata:**
- Chiede la password attuale e la nuova password (due volte per conferma).
- Controlla che la nuova password sia diversa dall’attuale e che le due inserite coincidano.
- Procede con il cambio password tramite servizio.
- Mostra esito e gestisce errori.

---

### `void displayAuthenticatedMenu()`
**Cosa fa:** Mostra il menu delle opzioni disponibili per un utente autenticato.

**Logica dettagliata:**
- Pulisce lo schermo e mostra l’header.
- Mostra informazioni di login e un elenco di opzioni (profilo, modifica profilo, cambio password, logout, ritorno al menu principale).

---

### `void handleAuthenticatedMenu()`
**Cosa fa:** Gestisce le scelte del menu utente autenticato in un ciclo.

**Logica dettagliata:**
- Mostra il menu, attende la scelta e chiama il metodo corrispondente.
- Gestisce anche le varianti testuali delle scelte (esempio: “profile” o “view” per la voce 1).
- In caso di logout, esce dal ciclo.
- Gestisce errori e li mostra all’utente.

---

### `void displayLoginMenu()`
**Cosa fa:** Mostra il menu iniziale per utenti non autenticati (login, registrazione, uscita).

**Logica dettagliata:**
- Pulisce lo schermo e mostra l’header.
- Mostra le opzioni disponibili e una nota che ricorda la necessità di autenticarsi.

---

### `boolean handleLoginMenu()`
**Cosa fa:** Gestisce il menu iniziale in un ciclo per utenti non autenticati.

**Logica dettagliata:**
- Mostra il menu, attende la scelta e chiama il metodo corrispondente.
- Gestisce varianti testuali delle scelte (es: “login”, “register”, “exit”, ecc).
- Ritorna true se l’utente effettua login/registrazione, false se decide di uscire.
- Gestisce errori e li mostra all’utente.

---

## Dettagli “tricky” e logiche particolari

- **Gestione input robusta**: tutti i campi obbligatori sono controllati; le password vengono confrontate due volte; i cambiamenti profilo sono riassunti prima della conferma.
- **Ciclo dei menu**: sia per autenticati che non, l’utente rimane nel menu finché non effettua logout o esce.
- **Conferme**: prima di cambiare dati sensibili (logout, update, password) viene sempre richiesta conferma.
- **Gestione stato**: il menu autenticato viene disabilitato se l’utente effettua logout.
- **Logging**: errori inattesi sono sempre loggati via logger, oltre che mostrati all’utente.
- **User Experience**: uso di colori, header, e messaggi di successo/errore per rendere l’uso in CLI chiaro e piacevole.

---

## Esempio di flusso

1. **Login:**  
   `handleLoginMenu()` → utente sceglie “login” → `handleLogin()` → autenticazione.
2. **Profilo:**  
   `displayAuthenticatedMenu()` → utente visualizza/modifica dati → tutti i cambiamenti sono riassunti e devono essere confermati.
3. **Logout:**  
   L’utente seleziona logout dal menu autenticato → conferma → ritorna al menu di login.
