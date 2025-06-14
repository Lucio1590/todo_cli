## Scopo della classe

`TodoDAOImpl` è l’**implementazione SQLite** dell’interfaccia `TodoDAO`.  
Gestisce tutte le operazioni di persistenza dei todo (inclusi i recurring todo) tramite JDBC, fornendo l’astrazione CRUD e query avanzate per il dominio todo.

---

## Attributi principali

- **Logger logger**: per tracciare operazioni e errori.
- **DatabaseManager databaseManager**: gestione centralizzata delle connessioni al database.

---

## Costruttore

```java
public TodoDAOImpl(DatabaseManager databaseManager)
```
Riceve un’istanza di `DatabaseManager` da utilizzare per tutte le operazioni sul database.

---

## Principali metodi pubblici

### `Todo create(Todo todo)`
- Inserisce un nuovo todo nel database.
- Usa una PreparedStatement per valorizzare tutti i campi (inclusi `project_id` e `user_id`).
- Ottiene l’id generato tramite `last_insert_rowid()` (specifico di SQLite).
- Se il todo è un `RecurringTodo`, crea anche la riga corrispondente in `recurring_todos`.
- Logga l’operazione e gestisce errori tramite `DatabaseException`.

---

### `Optional<Todo> findById(Long id)`
- Recupera un todo tramite il suo id.
- Esegue una LEFT JOIN su `recurring_todos` per includere eventuali dati di ricorrenza.
- Restituisce un Optional vuoto se non trovato.
- Usa la funzione di mapping (`mapResultSetToTodo`).

---

### `List<Todo> findAll()`
- Restituisce la lista di tutti i todo, ordinati per creazione decrescente.
- Include anche i dati eventuali di ricorrenza (LEFT JOIN).

---

### `List<Todo> findByProjectId(Long projectId)`
- Restituisce tutti i todo associati a un progetto specifico.

---

### `List<Todo> findByStatus(TodoStatus status)`
- Restituisce tutti i todo con uno specifico stato.

---

### `List<Todo> findByPriority(Priority priority)`
- Restituisce tutti i todo con una data priorità.

---

### `List<Todo> findDueBefore(LocalDate date)`
- Restituisce tutti i todo con scadenza entro una certa data.

---

### `List<Todo> findOverdue()`
- Restituisce tutti i todo scaduti (non completati né cancellati).

---

### `Todo update(Todo todo)`
- Aggiorna i dati di un todo identificato dal suo id.
- Aggiorna anche i dati specifici dei recurring tutto se necessario.
- Se il todo non esiste, lancia `DatabaseException`.

---

### `boolean delete(Long id)`
- Cancella un todo tramite il suo id.
- Restituisce true se il todo è stato effettivamente cancellato.

---

### `long count()`
- Restituisce il numero totale di todo nel database.

---

### `long countByStatus(TodoStatus status)`
- Restituisce il numero di todo con uno specifico stato.

---

## Helper Methods

### `List<Todo> executeQueryForTodoList(String sql, Object... parameters)`
- Esegue query parametrizzate e mappa i risultati in una lista di oggetti `Todo` (o `RecurringTodo`).

### `Todo mapResultSetToTodo(ResultSet resultSet)`
- Mappa una riga del result set in un oggetto `Todo` o `RecurringTodo`.
- Usa la presenza di `recurring_interval_days` per distinguere tra i due tipi.
- Per i recurring todo, imposta campi "privati" come `currentOccurrence` e `nextDueDate` tramite reflection (workaround per mancanza di setter pubblici).
- Gestisce anche le conversioni di date.

### `void createRecurringTodoData(Connection connection, RecurringTodo recurringTodo)`
- Inserisce la riga corrispondente in `recurring_todos`.

### `void updateRecurringTodoData(Connection connection, RecurringTodo recurringTodo)`
- Aggiorna i dati nella tabella `recurring_todos`.
- Se non esiste una riga, la inserisce (upsert).

---

## Logiche particolari (“tricky” e best practice)

- **Gestione robusta degli errori:**  
  Tutte le operazioni SQL sono protette da try/catch che loggano e rilanciano come `DatabaseException`.
- **Supporto ai recurring todo:**  
  La presenza della tabella `recurring_todos` richiede una gestione parallela a quella della tabella principale `todos`.
  La creazione/aggiornamento di recurring todo è sempre atomica con la transazione principale.
- **Reflection per campi privati:**  
  Alcuni campi dei recurring todo (es: `currentOccurrence`, `nextDueDate`) sono privati/protetti.  
  La classe usa reflection per valorizzarli, gestendo eventuali errori in log.
- **PreparedStatement:**  
  Tutte le query usano PreparedStatement per sicurezza e gestione parametri.
- **Optional come ritorno:**  
  L’uso di `Optional<Todo>` evita i null e rende il codice più sicuro.
- **Controllo sui campi null:**  
  Gestione sicura di campi come `project_id` che possono essere null.

---

## Domande da colloquio e risposte

**Q: Come vengono gestiti i recurring todo a livello di persistenza?**  
**A:**  
Oltre alla tabella principale `todos`, esiste una tabella `recurring_todos`.  
Alla creazione/aggiornamento di un recurring todo, la classe inserisce/aggiorna i dati specifici nella tabella aggiuntiva.  
Quando si legge un todo, una LEFT JOIN su `recurring_todos` permette di ricostruire correttamente l’oggetto Java.

---

**Q: Perché usare reflection per aggiornare campi dei recurring todo?**  
**A:**  
Alcuni campi come `currentOccurrence` e `nextDueDate` non hanno setter pubblici per motivi di incapsulamento.  
Reflection permette di valorizzarli a runtime mantenendo la compatibilità con la logica del modello, ma ogni errore viene tracciato in log.

---

**Q: Perché non si usa `getGeneratedKeys()`?**  
**A:**  
SQLite gestisce gli id generati con `last_insert_rowid()`, che è più affidabile rispetto a `getGeneratedKeys()` su questa piattaforma.

---

**Q: Come viene gestita la coerenza tra todo e recurring todo?**  
**A:**  
Le operazioni di inserimento/aggiornamento sono atomiche: la presenza di errori in una delle due tabelle porta all’eccezione e, se implementato, al rollback della transazione.

---

## Conclusione

`TodoDAOImpl` è una implementazione robusta e completa del pattern DAO per la gestione dei todo (inclusi quelli ricorrenti), con attenzione alla sicurezza, alla consistenza e alla scalabilità.
