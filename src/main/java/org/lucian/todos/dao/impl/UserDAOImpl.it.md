## Scopo della classe

`UserDAOImpl` è l’**implementazione SQLite** dell’interfaccia `UserDAO`.  
Gestisce tutte le operazioni di persistenza degli utenti per il sistema Todo Management, tramite JDBC, fornendo tutte le operazioni di CRUD e query avanzate per il dominio utente.

---

## Attributi principali

- **Logger logger**: per tracciare operazioni e errori.
- **DatabaseManager databaseManager**: gestione centralizzata delle connessioni al database.

---

## Costruttore

```java
public UserDAOImpl(DatabaseManager databaseManager)
```
Riceve un’istanza di `DatabaseManager` da utilizzare per tutte le operazioni sul database.

---

## Principali metodi pubblici

### `User create(User user)`
- Inserisce un nuovo utente nella tabella `users`.
- Usa una PreparedStatement per valorizzare tutti i campi (username, email, password hash, dati personali, stato, timestamps).
- Ottiene l’id generato tramite `last_insert_rowid()` (specifico SQLite).
- Logga l’operazione e gestisce errori tramite `DatabaseException`.

---

### `Optional<User> findById(Long id)`
- Recupera un utente tramite il suo id, restituendo un Optional vuoto se non trovato.
- Usa la funzione di mapping (`mapResultSetToUser`).

---

### `Optional<User> findByUsername(String username)`  
### `Optional<User> findByEmail(String email)`
- Recuperano utente tramite username o email.
- Effettuano validazione sul parametro (null o vuoto → Optional vuoto).
- Usano la funzione di mapping.

---

### `List<User> findAll()`
- Restituisce la lista di tutti gli utenti ordinati per username.

---

### `List<User> findAllActive()`
- Restituisce la lista di tutti gli utenti attivi (`active=1`).

---

### `User update(User user)`
- Aggiorna i dati di un utente identificato dal suo id.
- Aggiorna: username, email, password hash, dati personali, stato, `updated_at`.
- Lancia `DatabaseException` se utente non trovato.

---

### `void updateLastLogin(Long userId)`
- Aggiorna il timestamp `last_login_at` e `updated_at` per un dato utente.
- Utile per tracciare accessi e statistiche.

---

### `boolean delete(Long id)`
- Cancella un utente tramite il suo id.
- Restituisce true se l’utente è stato effettivamente cancellato.

---

### `boolean deactivate(Long id)`
- Imposta `active=0` per un utente, segnandolo come disattivo.
- Restituisce true se la disattivazione è avvenuta.

---

### `boolean reactivate(Long id)`
- Imposta `active=1` per un utente, segnandolo come attivo.
- Restituisce true se la riattivazione è avvenuta.

---

### `boolean exists(Long id)`
- Verifica se un utente esiste tramite il suo id.

---

### `boolean usernameExists(String username)`  
### `boolean emailExists(String email)`
- Verificano l’esistenza di uno username o email nel DB (usati per validazione in fase di registrazione o update).

---

### `long count()`
- Restituisce il numero totale di utenti nella tabella.

---

### `long countActive()`
- Restituisce il numero totale di utenti attivi.

---

## Helper Method

### `User mapResultSetToUser(ResultSet resultSet)`
- Mappa una riga del result set in un oggetto `User`.
- Gestisce i campi di timestamp (created, updated, last login).
- Legge tutti i campi necessari all’oggetto User del modello.

---

## Logiche particolari (“tricky” e best practice)

- **Gestione robusta degli errori:**  
  Tutte le operazioni SQL sono protette da try/catch che loggano e rilanciano come `DatabaseException`.
- **Gestione Optional:**  
  L’uso di `Optional<User>` evita i null e rende il codice più sicuro e idiomatico Java 8+.
- **PreparedStatement:**  
  Tutte le query usano PreparedStatement per sicurezza (contro SQL injection) e gestione robusta dei parametri.
- **Controllo su parametri null o vuoti:**  
  I metodi di ricerca e validazione controllano sempre la validità degli input per evitare query inutili o errori runtime.
- **Gestione dello stato attivo/inattivo:**  
  L’attivazione/disattivazione utenti è gestita tramite il campo `active` invece che cancellazione hard, per ragioni di audit e reversibilità.
- **Timestamps coerenti:**  
  Gli aggiornamenti ai dati aggiornano sempre il campo `updated_at` e, dove necessario, anche `last_login_at`.
- **Logging dettagliato:**  
  Tutte le operazioni principali sono loggate (sia successo che fallimento), facilitando debug e tracciamento delle attività utente.

---

## Domande da colloquio e risposte

**Q: Perché usare Optional invece che restituire null da findById/findByUsername?**  
**A:**  
Optional rende esplicito il caso “non trovato”, obbligando chi usa il metodo a gestirlo e riducendo il rischio di NullPointerException.

---

**Q: Come viene garantita la sicurezza dei dati sensibili come password?**  
**A:**  
Nel database viene sempre salvato solo l’hash della password, mai la password in chiaro.  
La validazione e la generazione dell’hash vengono gestite a livello di business/service.

---

**Q: Come gestire la concorrenza nell’aggiornamento utenti (ad esempio per il campo last_login_at)?**  
**A:**  
Le operazioni di update sono atomiche. In caso di concorrenza elevata si potrebbe valutare l’adozione di transazioni esplicite o lock più sofisticati, ma per la maggior parte delle applicazioni CLI il livello di concorrenza è basso.

---

**Q: Perché preferire la disattivazione (active=0) invece della cancellazione hard?**  
**A:**  
Per motivi di audit, compliance e possibilità di recupero. Un utente disattivato non può accedere ma i suoi dati vengono conservati per statistiche, storicizzazione e possibili riattivazioni in futuro.

---

## Conclusione

`UserDAOImpl` è una implementazione robusta e completa del pattern DAO per la gestione utenti, con attenzione alla sicurezza, consistenza e best practice di programmazione Java moderna.
