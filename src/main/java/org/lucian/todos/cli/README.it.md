## Scopo della cartella

La cartella `org.lucian.todos.cli` rappresenta il **cuore dell’interfaccia utente a riga di comando** (CLI) del sistema.  
Gestisce l’interazione utente, la navigazione tra menu, l’invocazione di comandi e la presentazione dei dati.

---

## Panoramica delle principali classi e responsabilità

### 1. **`TodoManagementCLI`**
- **Ruolo:** Entry point e controller principale dell’applicazione CLI.
- **Responsabilità:**  
  - Inizializzazione servizi, handler e risorse.
  - Gestione del ciclo di vita dell’applicazione.
  - Mostra il messaggio di benvenuto e le statistiche iniziali.
  - Gestisce il flusso di autenticazione e il ciclo principale del menu.
- **Collegamenti:**  
  - Crea e utilizza: `MainMenu`, `AuthenticationCommandHandler`, `TodoCommandHandler`, `ProjectCommandHandler`, servizi e DAO.

---

### 2. **`MainMenu`**
- **Ruolo:** Menu principale e router delle scelte utente.
- **Responsabilità:**  
  - Presenta il menu principale e i sottomenu (todo, progetti, statistiche, account, help).
  - Delegazione delle scelte ai rispettivi handler.
  - Gestione della navigazione e delle opzioni disponibili.
- **Collegamenti:**  
  - Utilizza: `TodoCommandHandler`, `ProjectCommandHandler`, `AuthenticationCommandHandler`, `CLIUtils`, `ErrorHandler`.

---

### 3. **Handler dei Comandi**
#### a. **`TodoCommandHandler`**
- **Ruolo:** Gestisce tutte le operazioni CLI relative ai todo.
- **Responsabilità:**  
  - CRUD sui todo, assegnazioni, ricerca, visualizzazione dettagliata, statistiche.
- **Collegamenti:**  
  - Usa `TodoService` per la logica, `CLIUtils` per input/output, `ErrorHandler` per errori.

#### b. **`ProjectCommandHandler`**
- **Ruolo:** Gestisce tutte le operazioni CLI relative ai progetti.
- **Responsabilità:**  
  - CRUD sui progetti, gestione assignment todo-progetto, visualizzazione progresso, statistiche.
- **Collegamenti:**  
  - Usa `ProjectService` e `TodoService`, `CLIUtils`, `ErrorHandler`.

#### c. **`AuthenticationCommandHandler`**
- **Ruolo:** Gestisce autenticazione e profilo utente.
- **Responsabilità:**  
  - Login, registrazione, logout, visualizzazione e modifica profilo, cambio password.
- **Collegamenti:**  
  - Usa `AuthenticationService`, `CLIUtils`, `ErrorHandler`.

---

### 4. **Utility e Supporto**
#### a. **`CLIUtils`**
- **Ruolo:** Utility statica per input/output, formattazione, colori, paginazione.
- **Responsabilità:**  
  - Formattazione di todo e progetti, gestione input robusto, stampa messaggi formattati, gestione tabelle e header CLI.

#### b. **`ErrorHandler`**
- **Ruolo:** Gestione centralizzata degli errori.
- **Responsabilità:**  
  - Mostra messaggi user-friendly e logga errori tecnici.
  - Diversifica messaggi a seconda del tipo di errore (business, database, autenticazione, startup, imprevisti).

---

## Flusso tipico dell’applicazione

1. **Avvio:**  
   - `TodoManagementCLI` inizializza i servizi e mostra il welcome.
   - L’utente deve autenticarsi (`AuthenticationCommandHandler`).

2. **Navigazione menu:**  
   - L’utente accede al `MainMenu`.
   - Sceglie se gestire todo, progetti, statistiche, account o consultare l’help.

3. **Gestione comandi:**  
   - Le scelte vengono delegate ai rispettivi handler (todo/project/account).
   - Gli handler interagiscono con i servizi e i DAO per eseguire le operazioni richieste.

4. **Gestione output:**  
   - `CLIUtils` si occupa di tutti gli aspetti di visualizzazione e input.
   - Funzioni di paginazione e formattazione migliorano la user experience in CLI.

5. **Gestione errori:**  
   - Qualsiasi eccezione viene centralizzata tramite `ErrorHandler`, che distingue fra errori previsti e gravi.

---

## Relazioni e interazione tra le classi

- **Nota:** I servizi (es. `TodoService`, `ProjectService`, `AuthenticationService`) non sono nella cartella CLI, ma sono usati dagli handler tramite iniezione nel costruttore.

---

## Punti di forza dell’architettura

- **Separation of Concerns**: ogni classe ha responsabilità ben definite.
- **Gestione errori robusta e centralizzata.**
- **Facilità di estensione:** nuovi comandi o handler possono essere aggiunti senza impattare la struttura.
- **User Experience avanzata per una CLI:** colori, paginazione, messaggi chiari.

---

## Come si integrano le funzionalità

- **Ogni comando dell’utente** viene gestito da un handler specializzato, che comunica con i servizi e restituisce output formattato.
- **I menu** sono strutturati a livelli, con possibilità di tornare indietro o cambiare sezione facilmente.
- **L’input utente** viene sempre controllato e filtrato da utility comuni, riducendo la duplicazione del codice.
- **In caso di errori**, l’utente riceve sempre feedback chiaro e l’app resta stabile.

---

## Conclusione

La cartella `org.lucian.todos.cli` rappresenta una struttura **modulare, estendibile e user-friendly** per gestire una CLI avanzata, mantenendo una netta separazione fra logica di business, interfaccia utente e gestione degli errori.

Per approfondimenti su singole classi, consultare la documentazione specifica per:
- [`TodoManagementCLI`](#)
- [`MainMenu`](#)
- [`TodoCommandHandler`](#)
- [`ProjectCommandHandler`](#)
- [`AuthenticationCommandHandler`](#)
- [`CLIUtils`](#)
- [`ErrorHandler`](#)