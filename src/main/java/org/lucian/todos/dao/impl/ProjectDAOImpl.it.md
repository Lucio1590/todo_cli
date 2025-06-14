## Scopo della classe

`ProjectDAOImpl` è l’**implementazione SQLite** dell’interfaccia `ProjectDAO`.  
Gestisce tutte le operazioni di persistenza dei progetti nell’applicazione Todo Management System accedendo direttamente al database tramite JDBC.

---

## Attributi principali

- **Logger logger**: per tracciare operazioni e errori.
- **DatabaseManager databaseManager**: gestione centralizzata delle connessioni al database.

---

## Costruttore

```java
public ProjectDAOImpl(DatabaseManager databaseManager)
```
Riceve un’istanza di `DatabaseManager` da utilizzare per tutte le operazioni sul database.

---

## Metodi principali

### `Project create(Project project)`
- Inserisce un nuovo progetto nel database.
- Usa una PreparedStatement per l’inserimento e per ottenere l’id generato (tramite `last_insert_rowid()`).
- Gestisce i campi: nome, descrizione, date, user_id, timestamps.
- Logga l’operazione e gestisce errori tramite `DatabaseException`.

---

### `Optional<Project> findById(Long id)`
- Recupera un progetto tramite il suo id.
- Restituisce un Optional vuoto se non trovato.
- Usa la funzione di mapping (`mapResultSetToProject`).

---

### `List<Project> findAll()`
- Restituisce la lista di tutti i progetti ordinati per data di creazione decrescente.

---

### `List<Project> findByName(String name)`
- Ricerca progetti per nome (case-insensitive, LIKE).
- Se il nome è vuoto/null, restituisce lista vuota.

---

### `List<Project> findCompleted()`
- Restituisce i progetti che hanno **almeno un todo** e **tutti i todo associati sono COMPLETED o CANCELLED**.
- Usa una query con `NOT EXISTS` e `EXISTS` per selezionare solo i progetti che soddisfano il criterio.

---

### `List<Project> findActive()`
- Restituisce progetti che hanno almeno un todo associato **non completato né cancellato**.
- Usa una JOIN e un DISTINCT per evitare duplicati.

---

### `Project update(Project project)`
- Aggiorna i dati del progetto identificato dal suo id.
- Aggiorna i campi: nome, descrizione, date, `updated_at`.
- Se il progetto non esiste, lancia `DatabaseException`.

---

### `boolean delete(Long id)`
- Cancella un progetto e tutti i todo associati.
- Transazione esplicita: prima elimina i todo, poi il progetto; se qualcosa fallisce fa rollback.
- Restituisce true se il progetto è stato effettivamente cancellato.

---

### `long count()`
- Restituisce il numero totale di progetti nel database.

---

### `boolean exists(Long id)`
- Verifica se un progetto esiste tramite il suo id.

---

## Helper Methods

### `List<Project> executeQueryForProjectList(String sql, Object... parameters)`
- Esegue query parametrizzate e mappa i risultati in una lista di oggetti `Project`.

### `Project mapResultSetToProject(ResultSet resultSet)`
- Mappa una riga del result set in un oggetto `Project`.

---

## Logiche particolari (“tricky” e best practice)

- **Gestione robusta degli errori:**  
  Tutte le operazioni SQL sono protette da try/catch che loggano e rilanciano come `DatabaseException`.
- **Transazioni:**  
  La cancellazione di un progetto è atomica: se fallisce la cancellazione dei todo o del progetto, si fa rollback.
- **Gestione degli ID:**  
  Per compatibilità con SQLite, l’id generato viene ottenuto con `SELECT last_insert_rowid()` invece di `getGeneratedKeys()`.
- **Optional come ritorno:**  
  L’uso di `Optional<Project>` per i metodi di ricerca evita i null e rende il codice più sicuro.
- **PreparedStatement:**  
  Tutte le query usano PreparedStatement per prevenire SQL injection e gestire i parametri in modo sicuro.

---

## Domande da colloquio e risposte

**Q: Perché usare una transazione nella cancellazione?**  
**A:**  
Per garantire **consistenza**: se fallisce la cancellazione dei todo o del progetto, nessun dato viene lasciato in uno stato intermedio.

**Q: Perché non si usa `getGeneratedKeys()`?**  
**A:**  
SQLite gestisce gli id generati con la funzione `last_insert_rowid()`, che è più affidabile rispetto a `getGeneratedKeys()` su questa piattaforma.

**Q: Come gestire errori di SQL?**  
**A:**  
Tutti gli errori SQL sono loggati e convertiti in una custom exception (`DatabaseException`), che può essere gestita centralmente nell’applicazione.

**Q: Come estendere la classe per supportare nuove query?**  
**A:**  
Aggiungendo nuovi metodi che riutilizzano il pattern di `executeQueryForProjectList`, mantenendo centralizzata la logica di mapping.

---

## Conclusione

`ProjectDAOImpl` è una implementazione robusta e sicura del pattern DAO per la gestione dei progetti, con attenzione alla consistenza, alla gestione degli errori e alla sicurezza delle query.
