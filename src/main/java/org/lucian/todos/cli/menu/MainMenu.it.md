## Attributi principali

- **Scanner scanner**: per la lettura dell’input utente.
- **TodoCommandHandler todoHandler**: gestore dei comandi relativi ai Todo.
- **ProjectCommandHandler projectHandler**: gestore dei comandi relativi ai Progetti.
- **AuthenticationCommandHandler authHandler**: gestore dei comandi di autenticazione.
- **AuthenticationService authService**: servizio per gestire lo stato di autenticazione e l’utente corrente.

---

## Costruttore

### `MainMenu(Scanner scanner, TodoCommandHandler todoHandler, ProjectCommandHandler projectHandler, AuthenticationCommandHandler authHandler, AuthenticationService authService)`
Inizializza tutti i gestori e il servizio di autenticazione.

---

## Metodi principali

### `void display()`
**Cosa fa:** Mostra il menu principale dell’applicazione.
- Pulisce lo schermo, mostra l’header.
- Se l’utente è loggato, mostra info utente.
- Elenca le opzioni principali (gestione todo, progetti, statistiche, account, help, uscita).
- Suggerisce che si possono usare sia numeri che parole chiave.

---

### `void handleTodoMenu()`
**Cosa fa:** Gestisce il sottomenu dedicato ai todo, in un ciclo.
- Mostra il sottomenu (vedi sotto).
- Attende input e chiama il metodo appropriato di `todoHandler` (create, list, edit, delete, status, search, assign, details, back).
- Gestisce input sia numerico che testuale (“1” o “create”).
- Gestisce errori inattesi con un handler centralizzato e attende conferma.

---

### `void handleProjectMenu()`
**Cosa fa:** Gestisce il sottomenu dedicato ai progetti, in un ciclo.
- Stessi principi del menu todo: mostra, attende, chiama metodi di `projectHandler`, gestisce errori.

---

### `void handleStatisticsMenu()`
**Cosa fa:** Gestisce il menu delle statistiche/report.
- Permette di accedere a statistiche su todo e progetti, report scaduti, per priorità, completamento progetti, sommario sistema.

---

### `void handleAccountMenu()`
**Cosa fa:** Passa la gestione all’handler di autenticazione, per tutte le funzioni account (profilo, logout, password, ecc).

---

### `void displayHelp()`
**Cosa fa:** Mostra un help dettagliato sulle funzionalità dell’applicazione.
- Spiega tutte le aree funzionali.
- Fornisce tips, shortcut, e suggerimenti pratici all’utente.
- Sottolinea l’utilizzo di parole chiave per navigare nei menu.

---

## Metodi privati di supporto

### `void displayTodoMenu()`
Mostra il menu delle operazioni disponibili sui todo.

### `void displayProjectMenu()`
Mostra il menu delle operazioni disponibili sui progetti.

### `void displayStatisticsMenu()`
Mostra il menu delle statistiche e report disponibili.

### `void displaySystemSummary()`
Mostra un riepilogo generale del sistema: statistiche todo e progetti.
- Chiama i metodi di `todoHandler` e `projectHandler`.
- Gestisce eventuali errori.

---

## Logiche particolari e “tricky”

- **Gestione input flessibile:** L’utente può scrivere sia numeri che keyword (es: “4” o “account”). Questo viene gestito tramite switch/case con più opzioni.
- **Gestione errori centralizzata:** Eccezioni vengono catturate nei blocchi try/catch e gestite tramite `ErrorHandler`.
- **Navigazione a menù annidati:** Ogni area ha il suo ciclo che permette all’utente di restare nella sezione finché non sceglie “back”.
- **Chiarezza UX:** Tutti i menu sono ben commentati, con header, descrizioni delle opzioni e info utili per l’utente.
- **Help dettagliato:** L’help non è solo tecnico ma anche pratico, con suggerimenti reali per migliorare la produttività.

---

## Esempio di flusso

1. **Avvio:**  
   `display()` → menu principale → utente sceglie una sezione.
2. **Gestione Todo:**  
   `handleTodoMenu()` → sottomenu → crea/modifica/elimina/search.
3. **Gestione Progetto:**  
   `handleProjectMenu()` → sottomenu → crea/lista/aggiorna/elimina/assegna.
4. **Statistiche:**  
   `handleStatisticsMenu()` → statistiche todo, progetti, report scaduti, ecc.
5. **Account:**  
   `handleAccountMenu()` → gestione profilo, password, logout.