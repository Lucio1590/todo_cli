# Spiegazione dettagliata della classe `TodoManagementCLI`

Analisi completa e analitica della classe  
`org.lucian.todos.cli.TodoManagementCLI`  
([file sorgente](https://github.com/Lucio1590/todo_cli/blob/main/src/main/java/org/lucian/todos/cli/TodoManagementCLI.java))

---

## Scopo e contesto

Classe **principale** che controlla il ciclo di vita dell’applicazione CLI “Todo Management System”.
- Si occupa di inizializzazione, setup servizi, avvio menu principali e gestione flusso utente.
- Implementa il pattern _controller_ per l’intera CLI.

---

## Attributi principali

- **Logger logger:** per la registrazione di informazioni e errori.
- **Scanner scanner:** input utente (System.in).
- **TodoService todoService:** gestisce logica e dati dei todo.
- **ProjectService projectService:** gestisce logica e dati dei progetti.
- **AuthenticationService authService:** gestisce login/logout e utente corrente.
- **AuthenticationCommandHandler authHandler:** gestore CLI per autenticazione.
- **MainMenu mainMenu:** oggetto che gestisce la navigazione e il display dei menu principali.
- **boolean running:** flag per il main loop del programma.

---

## Costruttore

### `TodoManagementCLI()`
- Inizializza lo scanner.
- Recupera la DAOFactory (singleton), crea le istanze dei servizi (AuthenticationService, TodoService, ProjectService).
- Gestisce eccezioni di inizializzazione (fallimento DB).
- Crea i vari handler (autenticazione, todo, progetto) e il menu principale.
- Imposta il flag `running` a true.
- Logga l’avvenuta inizializzazione.

---

## Metodo principale

### `void start()`
**Funzionamento:**
1. Mostra messaggio di benvenuto e statistiche di sistema (`displayWelcome()`).
2. Gestisce il _login/registrazione_: l’utente deve autenticarsi prima di continuare (`handleAuthenticationFlow()`).
   - Se l’utente rifiuta, mostra “Goodbye!” e termina.
3. Entra in un ciclo principale (`while (running)`) che:
   - Mostra il menu principale (`mainMenu.display()`).
   - Gestisce la scelta dell’utente (`handleMainMenuChoice()`).
4. Gestisce eccezioni generali con messaggio di errore e log.
5. Alla fine, chiama `cleanup()` per chiudere risorse.

---

## Metodi di supporto

### `private void handleMainMenuChoice()`
- Legge la scelta dell’utente dopo il menu principale.
- Switch su numeri e keyword (es: “1”, “todos”, “projects”, “exit”, ecc.).
- Per ogni scelta chiama il relativo menu handler:
  - Todo, progetto, statistiche, account, help, exit.
- Se la scelta è errata, mostra errore e attende invio.
- Gestisce errori inattesi con log e messaggio.

---

### `private void displayWelcome()`
- Pulisce lo schermo e mostra header.
- Messaggio di benvenuto, descrizione app.
- Recupera statistiche attuali (todo e progetti) e le mostra all’utente:
  - Numeri totali, pending, completati, attivi, completati.
  - Se ci sono todo scaduti, mostra un warning.
- Se non riesce a recuperare le statistiche (es. DB non disponibile), mostra "System ready for use".
- Mostra “System initialized successfully!”.
- Attende pressione di invio.

---

### `private void confirmExit()`
- Chiede conferma all’utente prima di uscire.
- Se confermato (risposta che inizia per “y”), imposta `running` a false e mostra messaggio di uscita.

---

### `private void cleanup()`
- Chiude lo scanner (se non nullo).
- Logga la chiusura dell’applicazione.
- Gestisce eccezioni nella chiusura.

---

### `private boolean handleAuthenticationFlow()`
- Se non c’è utente loggato, chiama il metodo di login dell’handler di autenticazione.
- Ritorna true se autenticato, false se l’utente vuole uscire.

---

## Logiche particolari/tricky

- **Gestione robusta dell’inizializzazione:**  
  Tutta la catena di servizi/DAO viene creata all’avvio, con gestione errori. Se fallisce, lancia una `RuntimeException`.
- **Gestione input flessibile:**  
  L’utente può scegliere opzioni sia tramite numero che tramite keyword (es: “4”, “profile” o “account”).
- **Gestione del ciclo principale:**  
  Il ciclo continua finché `running` è true, permettendo all’utente di navigare liberamente tra le varie sezioni.
- **Statistiche a colpo d’occhio:**  
  All’avvio, se possibile, mostra un riepilogo rapido dello stato del sistema (todos e progetti).
- **Pulizia risorse:**  
  Lo scanner viene sempre chiuso in modo sicuro alla fine, con logging anche in caso di errori.

---

## Esempio di flusso utente

1. **Avvio:**  
   - L’utente vede il benvenuto e le statistiche.
   - Deve autenticarsi (login/registrazione).
2. **Menu principale:**  
   - L’utente vede le sezioni: Todo, Progetti, Statistiche, Account, Help, Exit.
   - Può navigare e tornare ai menu precedenti.
3. **Uscita:**  
   - Sceglie “exit” → conferma → app chiude risorse e stampa saluto finale.
