## Scopo della cartella

La cartella `dao` (Data Access Object) contiene tutte le **interfacce** e le **classi di supporto** per l’accesso ai dati persistenti del sistema.  
Si tratta dello strato che isola la logica di business dall’implementazione concreta della persistenza (ad es. database SQLite), seguendo il pattern DAO.

---

## Componenti principali

### 1. **Interfacce DAO**

Le interfacce definiscono l’API che le classi concrete devono implementare per ogni entità persistente.  
Questo consente di:
- Separare la logica di accesso ai dati dall’implementazione specifica.
- Facilitare il testing e la sostituzione backend (es. passaggio da SQLite a un altro DB).
- Migliorare la manutenibilità e l’estensibilità.

**Tipiche interfacce presenti:**
- `TodoDAO`  
  (operazioni CRUD e query avanzate sui todo e recurring todo)
- `ProjectDAO`  
  (operazioni CRUD e query su progetti)
- `UserDAO`  
  (operazioni CRUD, autenticazione e gestione utenti)

---

### 2. **Implementazioni DAO (`impl/`)**

Le implementazioni concrete si trovano nella sottocartella `impl` e realizzano le interfacce definite sopra, tipicamente per SQLite:

- `TodoDAOImpl`
- `ProjectDAOImpl`
- `UserDAOImpl`

**Caratteristiche chiave:**
- Usano sempre un’istanza di `DatabaseManager` per ottenere connessioni.
- Gestiscono la mappatura tra le righe del DB e gli oggetti Java.
- Implementano tutte le operazioni richieste dalle interfacce.
- Gestiscono eccezioni tramite custom exception come `DatabaseException`.
- Usano PreparedStatement per sicurezza e performance.

---

### 3. **DAOFactory**

Classe factory centrale che:
- Si occupa di creare e fornire le istanze dei DAO.
- Implementa il Singleton Pattern per evitare istanze multiple e incoerenti.
- Permette di ottenere facilmente i DAO necessari in tutta l’applicazione.
- Favorisce la centralizzazione della gestione delle dipendenze.

---

## Perché usare le interfacce?

- **Astrazione:**  
  Permettono di definire *che cosa* deve essere fatto, lasciando all’implementazione concreta il *come*.
- **Testabilità:**  
  È facile sostituire l’implementazione con mock o fake nei test.
- **Estendibilità:**  
  Si possono aggiungere nuove implementazioni (es. per un altro DB, o per storage in memoria) senza cambiare la business logic.
- **Disaccoppiamento:**  
  Le classi di servizio/business dipendono solo dalle interfacce, non dalle classi concrete.

---

## Scopi e vantaggi

- **Separazione dei ruoli:**  
  La logica di accesso ai dati resta isolata dalla logica applicativa.
- **Riutilizzabilità:**  
  Le interfacce possono essere riutilizzate in contesti diversi.
- **Manutenibilità:**  
  Cambiare la persistenza non impatta la logica di business.
- **Sicurezza e robustezza:**  
  Gestione centralizzata delle eccezioni e delle connessioni.

---

## Flusso tipico d’uso

1. **Un servizio o handler** chiede un DAO a `DAOFactory`.
2. **Il DAO (interfaccia)** viene usato tramite la sua API pubblica.
3. **L’implementazione concreta** esegue le operazioni sul database, usando il `DatabaseManager`.
4. **Gli errori di persistenza** vengono racchiusi in custom exception e propagati verso l’alto per la gestione centralizzata.

---

## Estendibilità

Per aggiungere il supporto a una nuova entità (es. TagDAO):
- Si crea una nuova interfaccia `TagDAO`.
- Si aggiunge una implementazione (es. `TagDAOImpl`).
- Si aggiorna `DAOFactory` per fornire il nuovo DAO.
- Tutto il resto dell’applicazione resta invariato.

---

## Domande da colloquio e risposte

**Q: Perché non usare direttamente le classi di implementazione invece delle interfacce?**  
**A:**  
Perché usando le interfacce si ottiene disaccoppiamento, testabilità e flessibilità. Gli oggetti di business dipendono da un’API, non da un’implementazione specifica.

**Q: Qual è il vantaggio di una factory per i DAO?**  
**A:**  
Centralizza la creazione, garantisce che i DAO condividano le stesse dipendenze (DatabaseManager), semplifica la sostituzione di implementazioni e facilita il testing.

**Q: Come si gestiscono le eccezioni nel livello DAO?**  
**A:**  
Le implementazioni DAO convertono tutte le SQLException in custom exception (`DatabaseException`), che vengono poi gestite centralmente.

---

## Conclusione

La cartella `dao` è fondamentale per garantire un’architettura pulita, scalabile e manutenibile, facilitando il test, la sostituzione e la crescita futura del sistema.