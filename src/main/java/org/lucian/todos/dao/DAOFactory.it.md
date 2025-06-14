## Scopo della classe

`DAOFactory` è una **factory centralizzata** che si occupa di creare e fornire le istanze dei Data Access Object (DAO) dell’applicazione Todo Management System.  
Implementa il **pattern Singleton** per garantire che i DAO e il DatabaseManager siano condivisi e gestiti in modo centralizzato.

---

## Attributi principali

- **DatabaseManager databaseManager:**  
  Gestore delle connessioni al database, condiviso da tutti i DAO.
- **TodoDAO todoDAO:**  
  DAO per la gestione della persistenza dei todo.
- **ProjectDAO projectDAO:**  
  DAO per la gestione della persistenza dei progetti.
- **UserDAO userDAO:**  
  DAO per la gestione della persistenza degli utenti.
- **static DAOFactory instance:**  
  Istanza singleton della factory.

---

## Logica di inizializzazione

### Singleton pattern

- La factory viene istanziata una sola volta tramite il metodo statico `getInstance()`, che è **synchronized** per garantire thread-safety.
- All’interno del costruttore privato, vengono creati i DAO, ognuno dei quali riceve il medesimo `DatabaseManager` (anch’esso singleton).
- In questo modo, tutte le parti dell’applicazione che richiedono accesso ai dati utilizzano lo stesso DatabaseManager e le stesse istanze di DAO.
- È possibile costruire una factory alternativa passando un database manager custom (ad esempio per test o ambienti diversi).

---

## Metodi principali

### `public static synchronized DAOFactory getInstance()`
- Restituisce l’istanza singleton della factory.
- Se non esiste, la crea, usando il singleton di `DatabaseManager`.

### `public TodoDAO getTodoDAO()`
- Restituisce l’istanza di `TodoDAO` creata al momento dell’inizializzazione.

### `public ProjectDAO getProjectDAO()`
- Restituisce l’istanza di `ProjectDAO` creata al momento dell’inizializzazione.

### `public UserDAO getUserDAO()`
- Restituisce l’istanza di `UserDAO` creata al momento dell’inizializzazione.

### `public DatabaseManager getDatabaseManager()`
- Restituisce l’istanza di `DatabaseManager` utilizzata internamente dalla factory.

---

## Logica di sicurezza e best practice

- **Centralizzazione delle dipendenze:**  
  Tutti i DAO condividono la stessa istanza di `DatabaseManager`, evitando problemi di concorrenza e garantendo la coerenza delle connessioni al database.
- **Singleton e thread safety:**  
  Il metodo `getInstance()` è synchronized per evitare condizioni di race in ambienti multi-thread.
- **Iniezione delle dipendenze:**  
  I DAO sono creati passando il DatabaseManager, facilitando testabilità, estensione e sostituzione del backend dati.

---

## Logica di migrazione (integrazione con DatabaseManager)

- La factory non si occupa direttamente della migrazione dello schema del database, ma ne delega la responsabilità a `DatabaseManager`.
- Poiché crea i DAO solo dopo che il DatabaseManager è stato inizializzato (e la logica di migrazione/integrità è stata eseguita), si garantisce che i DAO lavorino sempre su uno schema coerente e aggiornato.

---

## Domande da colloquio (e risposte)

**Q: Perché usare una factory per i DAO e non istanziarli direttamente dove servono?**  
**A:**  
La factory centralizza la creazione, garantisce che tutte le parti dell’applicazione utilizzino la stessa istanza dei DAO (e quindi del DatabaseManager), semplifica la manutenzione e rende più semplice cambiare implementazione o backend.

---

**Q: Che problemi si evitano usando il pattern Singleton per la factory?**  
**A:**  
Si evita la creazione multipla e inconsistente dei DAO, si riducono problemi di concorrenza e si facilita la gestione delle risorse legate al database.

---

**Q: Come si potrebbe estendere la factory per supportare nuovi tipi di DAO?**  
**A:**  
Basta aggiungere nuovi attributi e metodi getter per i nuovi DAO, istanziandoli nel costruttore e passandogli il DatabaseManager.

---

**Q: È possibile utilizzare un DatabaseManager alternativo (ad esempio per i test)?**  
**A:**  
Sì, la factory può essere istanziata anche passando un DatabaseManager custom, ad esempio in-memory per i test, permettendo la massima flessibilità e isolamento nei test.

---

## Esempio d’uso

```java
DAOFactory factory = DAOFactory.getInstance();
TodoDAO todoDAO = factory.getTodoDAO();
ProjectDAO projectDAO = factory.getProjectDAO();
UserDAO userDAO = factory.getUserDAO();
```

---

## Conclusione

`DAOFactory` è un elemento chiave dell’architettura data-access di Todo Management System, garantendo coerenza, sicurezza e facilità di manutenzione nell’accesso a tutte le entità persistenti del sistema.