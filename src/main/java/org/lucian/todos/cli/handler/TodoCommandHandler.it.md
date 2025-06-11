La classe rappresenta il “controller” dei comandi della CLI specifici per la gestione dei `Todo`.

---

## Attributi principali

- **Logger logger**: per registrare errori e informazioni.
- **TodoService todoService**: servizio di accesso ai dati/business logic dei todo.
- **Scanner scanner**: per leggere input dell’utente.

---

## Costruttore

### `TodoCommandHandler(TodoService todoService, Scanner scanner)`
Salva il servizio e lo scanner da usare per tutte le operazioni utente.

---

## Metodi pubblici core

### `showAllTodos()`
Recupera e mostra tutti i todo tramite il servizio.  
- Se la lista è vuota, stampa messaggio informativo.
- Altrimenti, stampa header e chiama `displayTodos()` per la tabella.

### `listTodos()`
Alias per `showAllTodos()`.

### `createTodo()`
Crea un nuovo todo tramite input interattivo:
- Chiede titolo (obbligatorio), descrizione (facoltativa), data scadenza (facoltativa, controlla formato).
- Chiede la priorità con un menu.
- Crea il todo, lo salva col servizio e stampa il risultato.
- Gestisce input errato e problemi col database.

### `updateTodo()`
Aggiorna un todo esistente:
- Chiede ID, recupera il todo dal servizio.
- Mostra info attuale, chiede nuovi valori per ogni campo (accetta default/valori correnti).
- Gestisce input errato (date, numeri), aggiorna i campi e salva.
- Gestisce errori di ID non trovato e problemi col database.

### `deleteTodo()`
Cancella un todo:
- Chiede ID, conferma l’esistenza, chiede conferma all’utente.
- Chiama il servizio per la cancellazione e stampa esito.

### `viewTodo()`
Mostra i dettagli di un todo dato l’ID.
- Usa `displayTodoDetails()` per stampa dettagliata.

### `markTodoCompleted()`
Segna un todo come completato.
- Gestisce errori (ID non trovato, stato non valido).

### `assignTodoToProject()`
Assegna o rimuove un todo da un progetto:
- Chiede ID todo e ID progetto (vuoto per rimuovere).
- Aggiorna l’associazione tramite servizio.

### `showOverdueTodos()`
Mostra i todo scaduti, con tabella.

### `showTodoStatistics()`
Stampa statistiche aggregate sui todo (totale, per stato, scaduti, etc).

### `searchTodos()`
Cerca i todo tramite una stringa chiave inserita dall’utente.
- Gestisce input vuoto.
- Mostra risultati con `displayTodos()`.

### `viewTodoDetails()`
Alias di `viewTodo()`.

### `displayTodoStatistics()`
Alias di `showTodoStatistics()`.

### `updateTodoStatus()`
Permette di aggiornare solo lo stato di un todo:
- Chiede ID e nuovo stato tramite menu.
- Aggiorna e stampa esito.

### `displayOverdueTodos()`
Alias di `showOverdueTodos()`.

### `displayTodosByPriority()`
Mostra i todo filtrati per priorità scelta dall’utente (oppure tutti).  
- Menu di scelta, recupera e mostra i risultati (ordinati per priorità quando “tutte”).

---

## Metodi privati di utilità

### `displayTodos(List<Todo> todos)`
Stampa una tabella con i todos (ID, titolo, scadenza, priorità, stato).
- Usa `truncate()` per stringhe lunghe.
- Formatta bene le colonne.

### `displayTodoDetails(Todo todo)`
Stampa tutti i dettagli di un singolo todo, inclusi: ID, titolo, descrizione, scadenza, priorità, stato, ID progetto, data creazione e se è scaduto.

### `promptForTodoId(String prompt)`
Chiede all’utente un ID todo, controlla che sia un numero valido.
- Gestisce input vuoto o non numerico.

### `promptForPriority()`
Menu di scelta per la priorità (`LOW`, `MEDIUM` (default), `HIGH`, `URGENT`).
- Restituisce l’enum corrispondente.

### `promptForStatus()`
Menu per scegliere lo stato del todo (`TODO` di default, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`).

### `truncate(String text, int maxLength)`
Taglia le stringhe troppo lunghe, aggiunge “...” se necessario.

### `handleException(String message, Exception e)`
Stampa errore per l’utente e logga l’eccezione in dettaglio.

---

## Note sulle parti “tricky”

- **Gestione input**: molti metodi usano menu numerici e default, e gestiscono con attenzione input non valido per evitare crash.
- **Alias**: diversi metodi sono alias (es. `listTodos`, `viewTodoDetails`, `displayTodoStatistics`, `displayOverdueTodos`), facilitando la compatibilità con più comandi CLI.
- **Visualizzazione**: la stampa tabellare usa formati fissi, e le descrizioni vengono troncate per non rovinare l’allineamento.
- **Aggiornamento dati**: la logica di update dei todo permette di mantenere i dati esistenti se l’utente preme solo invio.
- **Gestione stato/scadenza**: la stampa dei dettagli evidenzia quando un todo è scaduto.
