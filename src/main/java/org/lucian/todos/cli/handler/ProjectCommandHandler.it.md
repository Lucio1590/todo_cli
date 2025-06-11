## Attributi principali

- **Logger logger**: registra errori e traccia operazioni.
- **PROJECTS_PER_PAGE**: costante che definisce quanti progetti mostrare per pagina (default: 8).
- **ProjectService projectService**: business logic e accesso dati per i progetti.
- **TodoService todoService**: accesso ai todo, usato per gestire relazioni progetto-todo.
- **Scanner scanner**: per leggere input dell’utente.

---

## Costruttore

### `ProjectCommandHandler(ProjectService projectService, TodoService todoService, Scanner scanner)`
Inizializza i servizi e lo scanner.

---

## Metodi pubblici

### `createProject()`
**Cosa fa:** Consente all’utente di creare un nuovo progetto tramite input interattivo.

**Logica dettagliata:**
- Pulisce la schermata e stampa l’header.
- Chiede nome (obbligatorio), descrizione (facoltativa), data inizio/fine (facoltative).
- Controlla che la data fine non sia prima della data inizio.
- Crea e salva il progetto tramite `projectService`.
- Mostra il progetto creato con formattazione.
- Gestione errori: stampa messaggi chiari e attende invio.

---

### `listProjects()`
**Cosa fa:** Elenca tutti i progetti con paginazione.

**Logica dettagliata:**
- Recupera tutti i progetti dal servizio.
- Se la lista è vuota, stampa messaggio informativo.
- Altrimenti usa `CLIUtils.displayPaginatedList` per visualizzare la lista a pagine.
- Gestisce eccezioni stampando errori e attendendo invio.

---

### `updateProject()`
**Cosa fa:** Permette di aggiornare un progetto esistente.

**Logica dettagliata:**
- Chiede l’ID del progetto da aggiornare.
- Recupera il progetto e mostra i dettagli attuali.
- Chiede nuovi valori per nome e descrizione (default: quelli attuali).
- Fa scegliere se aggiornare le date (mostra le attuali e chiede conferma).
- Verifica che la nuova data fine non sia prima della data inizio.
- Aggiorna i campi e salva il progetto.
- Stampa il progetto aggiornato.
- Gestione errori: progetto non trovato, date errate, altri errori.

---

### `deleteProject()`
**Cosa fa:** Cancella un progetto dopo conferma.

**Logica dettagliata:**
- Chiede l’ID, recupera il progetto e lo mostra.
- Se il progetto ha dei todo assegnati, avvisa che verranno rimossi dall’associazione.
- Chiede conferma all’utente.
- Se confermato, cancella il progetto e stampa l’esito.
- Gestione errori: progetto non trovato, assegnazioni presenti, altri errori.

---

### `viewProjectTodos()`
**Cosa fa:** Mostra tutti i todo assegnati a un progetto.

**Logica dettagliata:**
- Chiede l’ID del progetto, lo recupera.
- Recupera e mostra tutti i todo assegnati (o avvisa se nessuno).
- Stampa ciascun todo usando `CLIUtils.formatTodo`.

---

### `assignTodoToProject()`
**Cosa fa:** Assegna un todo a un progetto.

**Logica dettagliata:**
- Chiede ID todo e ID progetto.
- Verifica esistenza di entrambi.
- Mostra titolo del todo e nome progetto.
- Esegue l’assegnazione tramite `todoService`.
- Stampa messaggio di successo.
- Gestione errori: input errato, entità non trovate, errori vari.

---

### `viewProjectProgress()`
**Cosa fa:** Mostra lo stato di avanzamento di un progetto.

**Logica dettagliata:**
- Chiede l’ID del progetto.
- Recupera progetto e statistiche di completamento.
- Stampa informazioni base (date, nome, ecc).
- Mostra statistiche: numero todo totali, completati, in corso, da fare, cancellati, scaduti, percentuale completamento.
- Mostra una progress bar grafica.
- Gestione errori: progetto non trovato, altri errori.

---

### `viewProjectDetails()`
**Cosa fa:** Mostra i dettagli completi di un progetto.

**Logica dettagliata:**
- Chiede l’ID progetto, recupera progetto e todos associati.
- Stampa dettagli formattati: ID, nome, descrizione, date, totali, stato (completed/active/overdue).
- Se ci sono todo, mostra anche un sommario per stato (completed, in progress, todo).
- Gestione errori: progetto non trovato, altri errori.

---

### `displayProjectStatistics()`
**Cosa fa:** Mostra statistiche aggregate sui progetti.

**Logica dettagliata:**
- Recupera statistiche globali dal servizio (totali, attivi, completati).
- Stampa header e valori, usando colori.

---

### `displayProjectCompletionStats()`
**Cosa fa:** Mostra la percentuale di completamento per tutti i progetti.

**Logica dettagliata:**
- Recupera tutti i progetti.
- Per ciascuno, mostra nome, numero todo, completati, percentuale.
- Visualizza una mini progress bar.
- Gestione errori: errori durante recupero statistiche per ogni progetto.

---

## Metodi privati

### `displayProgressBar(double percentage)`
Mostra una barra di avanzamento grafica lunga 40 caratteri, colorata, in base alla percentuale.

### `displayMiniProgressBar(double percentage)`
Come sopra, ma mini (20 caratteri), usata per la lista rapida di progetti.

---

## Dettagli “tricky” e logiche particolari

- **Gestione date:** In creazione e aggiornamento, se la data fine è prima della data inizio, viene subito bloccata l’operazione.
- **Gestione assegnazioni:** In cancellazione, se ci sono todo assegnati, viene avvisato l’utente e chiarito che le assegnazioni saranno rimosse.
- **Progress bar:** Uso di Unicode e colori ANSI per rendere l’esperienza CLI più intuitiva e moderna.
- **Paginazione:** Per le liste lunghe, viene gestita la navigazione a pagine (tramite funzione di utility).
- **Gestione errori:** Tutti i metodi sono robusti contro input errato e imprevisti, con log su logger e messaggi chiari per l’utente.

---

## Esempio di flusso utente

1. **Creazione progetto**: `createProject()` → inserimento dati → verifica → creazione e stampa esito.
2. **Aggiornamento progetto**: `updateProject()` → seleziona progetto → modifica campi → verifica → aggiorna e stampa esito.
3. **Visualizzazione progressi**: `viewProjectProgress()` → seleziona progetto → mostra percentuale, dettagli, progress bar.
4. **Statistiche globali**: `displayProjectCompletionStats()` → lista di tutti i progetti con percentuali e barre grafiche.
