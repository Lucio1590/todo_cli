## Scopo della classe

`DatabaseManager` è il **singleton** responsabile della gestione della connessione al database SQLite dell’applicazione Todo Management System.

- Incaricato di **inizializzare** e **mantenere** lo schema del database.
- Si occupa di **migrazione schema**, **creazione tabelle**, **creazione utente admin di default**, **connessioni**, **health check** e shutdown.

---

## Attributi principali

- **Logger logger:** Per logging di errori, informazioni e warning.
- **DEFAULT_DATABASE_URL:** URL JDBC per il database persistente locale (`todos.db`).
- **TEST_DATABASE_URL:** URL JDBC per un database in memoria (usato per test).
- **databaseUrl:** URL del database corrente (può essere di produzione o test).
- **instance:** Riferimento statico per implementare il pattern Singleton.

---

## Costruttore

### `private DatabaseManager(String databaseUrl)`
- **Privato** per garantire l’uso del Singleton.
- Salva l’URL e chiama `initializeDatabase()`.
- In caso di errore, logga e rilancia come RuntimeException.

---

## Singleton Methods

### `getInstance()`
- Restituisce l’istanza **singleton** legata al database di produzione.
- Se non esiste, la crea.

### `getTestInstance()`
- Restituisce una nuova istanza (non singleton) per il database **in-memory** (solo per test).

---

## Metodi principali

### `Connection getConnection()`
- Restituisce una nuova connessione JDBC al database.
- Imposta `autoCommit` a true.
- Se fallisce, logga e rilancia come `DatabaseException`.

---

### `boolean isHealthy()`
- Esegue una query di test (`SELECT 1`) per verificare l’accessibilità del database.
- Ritorna true se va a buon fine, false in caso di errore (logga warning).

---

### `void shutdown()`
- Metodo placeholder per la chiusura (non necessario per SQLite, ma buono per compatibilità futura).
- Logga il tentativo di shutdown.

---

### `String getDatabaseUrl()`
- Restituisce l’URL del database attualmente in uso.

---

## Inizializzazione e Migrazione Schema

### `initializeDatabase()`
- **Punto centrale:** Inizializza lo schema del database.
- Abilita chiavi esterne (`PRAGMA foreign_keys = ON`).
- Verifica se serve una **migrazione** (`checkIfMigrationNeeded`):  
  - Se vecchio schema: chiama `performDatabaseMigration()`
  - Altrimenti: chiama `createFreshSchema()`
- Logga successo o errore.

---

### `checkIfMigrationNeeded(Connection connection)`
- Controlla se la tabella `todos` esiste e se manca la colonna `user_id`.
- Se serve, ritorna true (migrazione necessaria), altrimenti false.
- In caso di errore nella verifica, assume fresh install.

---

### `performDatabaseMigration(Connection connection)`
- Aggiorna schema di un database preesistente per aggiungere il supporto multiutente.
- **Cosa fa:**
  - Crea la tabella `users` (se non esiste).
  - Crea utente admin di default.
  - Se la tabella `projects` esiste, aggiunge colonna `user_id` (con gestione duplicati).
  - Se la tabella `todos` esiste, aggiunge colonna `user_id`.
  - Crea tabella `recurring_todos` se non esiste.
  - Crea indici per ottimizzazione ricerche.

---

### `createFreshSchema(Connection connection)`
- Crea tutte le tabelle necessarie (users, projects, todos, recurring_todos) da zero.
- Crea indici.
- Crea utente admin di default.

---

### `createDefaultAdminUser(Statement statement)`
- Inserisce un utente admin predefinito (username: `admin`, password: `admin`) con ID 1.  
- Usa hash sicuro della password (SHA-256 + salt, base64).
- Logga un warning di sicurezza invitando a cambiare la password.

---

### `generateAdminPasswordHash(String password)`
- Replica la logica di hashing usata in autenticazione:
  - Genera salt casuale di 32 byte.
  - SHA-256(salt + password).
  - Concatena salt e hash, codifica Base64.
- In caso di errore, logga e rilancia RuntimeException.

---

### `createIndexes(Statement statement)`
- Crea una serie di indici SQL per ottimizzare le query frequenti (su username, email, stato, user_id, project_id, ecc).

---

### `tableExists(Connection connection, String tableName)`
- Verifica se una tabella esiste interrogando `sqlite_master`.
- Ritorna true/false.

## Logica di Inizializzazione

La logica di inizializzazione è studiata per essere **robusta e idempotente**:

- All’avvio, il costruttore chiama `initializeDatabase()`.
  - Abilita le foreign key con `PRAGMA foreign_keys = ON`.
  - Determina se il database è nuovo o già esistente (tramite `checkIfMigrationNeeded`).
    - Se è nuovo, **crea tutte le tabelle** e l’utente admin di default (`createFreshSchema()`).
    - Se esiste ma è di vecchia versione, **esegue la migrazione** (`performDatabaseMigration()`).
- Tutte le creazioni di tabelle usano `CREATE TABLE IF NOT EXISTS` per evitare errori in caso di avvio multiplo.
- Vengono creati anche **indici** per ottimizzare le query più frequenti.

---

## Logica di Migrazione

La migrazione viene effettuata quando si rileva che lo schema esistente non supporta le funzionalità più recenti (ad esempio, la gestione multiutente):

- **Rilevamento:**  
  Il metodo `checkIfMigrationNeeded()` controlla se nella tabella `todos` manca la colonna `user_id`.
- **Migrazione:**  
  - Crea la tabella `users` e un utente admin di default.
  - Aggiorna le tabelle esistenti `projects` e `todos` aggiungendo la colonna `user_id`, se non presente.
  - Crea la tabella `recurring_todos` se manca.
  - Crea tutti gli indici necessari.
- **Tolleranza agli errori:**  
  Se una colonna esiste già (errore “duplicate column”), viene ignorato e si continua la migrazione.
- **Vincoli FK:**  
  SQLite ha limitazioni nell’aggiunta di constraint FK su tabelle esistenti. Per questo, dove necessario, vengono creati indici come workaround.

---

## Cosa succede all’avvio?

1. **Avvio app:** `DatabaseManager.getInstance()` viene chiamato.
2. **Costruttore:** Chiama `initializeDatabase()`.
   - Se è un nuovo DB: Crea tutte le tabelle e l’utente admin.
   - Se è un DB vecchio: Migra schema, aggiunge tabelle/colonne mancanti.
3. **Connessioni:** Le connessioni sono sempre nuove ad ogni chiamata.
4. **Health check:** Può essere chiamato per verificare lo stato del DB.
5. **Shutdown:** Placeholder, non necessario per SQLite.

---

## Sicurezza

- **Admin di default:**  
  Il sistema crea sempre un admin con password nota.  
  **WARNING:** L’utente deve cambiarla subito dopo il primo accesso (warning loggato).

- **Password hashing:**  
  Salt + SHA-256 + Base64, identico a quello usato in autenticazione.

---

## Logica di Sicurezza

- **Creazione utente admin di default:**  
  Al primo avvio o durante le migrazioni, viene creato un utente admin predefinito con username `admin` e password `admin`.
  - La password viene **hashata** usando SHA-256 con salt casuale di 32 byte e codifica base64.
  - La funzione di hashing replica esattamente quella usata in `AuthenticationService` per coerenza.
  - Viene loggato un **warning di sicurezza** che invita l’utente a cambiare la password admin appena possibile.

- **Vincoli di integrità:**  
  Tutte le relazioni tra tabelle sono protette da **foreign key** con azioni di `ON DELETE CASCADE` o `ON DELETE SET NULL` per evitare inconsistenze.

- **Accesso alle credenziali:**  
  Gli hash delle password non sono mai memorizzati in chiaro.  
  L’inserimento avviene solo tramite hash sicuro e salt generato in runtime.

---

## Metodi di supporto

### `checkIfMigrationNeeded(Connection connection)`
- Controlla se la tabella `todos` esiste e se manca la colonna `user_id`.
- Se serve, ritorna true (migrazione necessaria), altrimenti false.
- In caso di errore nella verifica, assume fresh install.

### `performDatabaseMigration(Connection connection)`
- Aggiorna schema di un database preesistente per aggiungere il supporto multiutente.
- Gestisce errori di doppia aggiunta colonna (`duplicate column`).

### `createFreshSchema(Connection connection)`
- Crea tutte le tabelle necessarie (users, projects, todos, recurring_todos) da zero.
- Crea indici.
- Crea utente admin di default.

### `createDefaultAdminUser(Statement statement)`
- Inserisce un utente admin predefinito (username: `admin`, password: `admin`) con ID 1.  
- Usa hash sicuro della password (SHA-256 + salt, base64).
- Logga un warning di sicurezza invitando a cambiare la password.

### `generateAdminPasswordHash(String password)`
- Replica la logica di hashing usata in autenticazione.
- In caso di errore, logga e rilancia RuntimeException.

### `createIndexes(Statement statement)`
- Crea una serie di indici SQL per ottimizzare le query frequenti.

### `tableExists(Connection connection, String tableName)`
- Verifica se una tabella esiste interrogando `sqlite_master`.
- Ritorna true/false.


## Considerazioni “tricky” e particolari

- **Migrazione schema:**  
  Gestisce in modo sicuro l’upgrade da vecchie versioni senza perdere dati.
  - Gestisce errori di doppia aggiunta colonna (`duplicate column`).
- **SQLite limitations:**  
  Non supporta tutte le operazioni DDL (ad esempio, aggiunta diretta di foreign key constraint a tabelle esistenti).
- **Indici:**  
  Essenziali per performance su ricerche utente, stato todo, priorità, ecc.

---

## Cosa succede all’avvio?

1. **Avvio app:** `DatabaseManager.getInstance()` viene chiamato.
2. **Costruttore:** Chiama `initializeDatabase()`.
   - Se è un nuovo DB: Crea tutte le tabelle e l’utente admin.
   - Se è un DB vecchio: Migra schema, aggiunge tabelle/colonne mancanti.
3. **Connessioni:** Le connessioni sono sempre nuove ad ogni chiamata.
4. **Health check:** Può essere chiamato per verificare lo stato del DB.
5. **Shutdown:** Placeholder, non necessario per SQLite.

---

## Esempio di flusso d’uso

1. **Avvio applicazione**
   - `DatabaseManager.getInstance()` → connessione/inizializzazione
2. **Richiesta connessione**
   - `getConnection()` usato da DAO o servizi
3. **Verifica salute**
   - `isHealthy()` usato per health check periodico o all’avvio
4. **Shutdown**
   - `shutdown()` chiamato in chiusura app (qui solo loggato)
