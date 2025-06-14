## 1. Descrizione Generale

Il progetto `todo_cli` è un **Task Management System** scritto in Java SE, pensato per la gestione di task (`Todo`), progetti (`Project`) e utenti tramite un’interfaccia a linea di comando (**CLI**).  
Il sistema supporta funzioni avanzate come autenticazione sicura, gestione di attività ricorrenti, statistiche, validazione e logging robusto tramite SLF4J/Logback.

---

## 2. Architettura del Progetto

### **a. Struttura a Livelli (Layered Architecture)**

1. **Model**  
   - Oggetti di dominio: `User`, `Project`, `Todo`, `RecurringTodo`, ecc.
   - Incapsulano la logica e le regole di validazione di base.
2. **DAO (Data Access Object)**  
   - Interfacce e implementazioni: `UserDAO`, `ProjectDAO`, `TodoDAO` ecc.
   - Gestiscono la persistenza dei dati (in memoria o su DB).
3. **Service Layer**  
   - Orchestrano la logica di business: `AuthenticationService`, `ProjectService`, `TodoService`.
   - Gestione validazione avanzata, orchestrazione tra DAO e logica di dominio.
4. **CLI (Command Line Interface)**  
   - Pacchetto `org.lucian.todos.cli` e sottopacchetti.
   - Gestione dell’interazione con l’utente (input/output su terminale), menu, handler di comandi, ecc.
5. **Utility e Supporto**
   - Utility per CLI (`CLIUtils`), logging, gestione degli stream di sistema.
   - Handler specifici per autenticazione, menu, ecc.
6. **Test**  
   - Test di unità e integrazione tramite JUnit 5 e Mockito.
   - Copertura sia della logica di dominio che della CLI.

---

## 3. Funzionalità del Database

- **Persistenza**: progettato per supportare (anche in futuro) una persistenza tramite SQLite (driver già incluso in `pom.xml`), ma può funzionare anche in modalità in-memory/mock.
- **DAO Pattern**:  
  - Ciascun tipo di entità ha il proprio DAO, astratto tramite interfacce.
  - Le implementazioni si occupano di CRUD e query avanzate (ricerca per nome, status, project, ecc.).
- **Gestione Transazioni e Errori**:  
  - Gestione errori robusta tramite exception custom (es. `DatabaseException`).
  - I DAO possono essere facilmente mockati nei test per isolamento della logica business/CLI.
- **Espandibilità**:  
  - La presenza del driver SQLite e la struttura a DAO permette di migrare facilmente verso una persistenza reale senza modifiche alla logica di business.

---

## 4. Funzionalità della CLI

La CLI fornisce una **user experience interattiva** per la gestione di task, progetti e utenti, tramite menu e comandi digitati.

### **Principali caratteristiche:**
- **Login/Logout/Registrazione**:  
  - Autenticazione sicura con hash delle password e validazione.
- **Gestione Todo**:  
  - Crea, modifica, elimina, cerca e aggiorna lo stato dei task.
  - Supporto a task ricorrenti (con avanzamento automatico delle occorrenze).
  - Ricerca per titolo, descrizione, priorità, stato, scadenza.
- **Gestione Progetti**:  
  - Crea, modifica, elimina progetti.
  - Associa/dissocia task ai progetti.
  - Calcolo statistiche di avanzamento/completamento.
- **Statistiche e Dashboard**:  
  - Visualizzazione di statistiche aggregate su task e progetti.
  - Output colorato e formattato (tramite utility).
- **Gestione Utente**:  
  - Aggiornamento profilo, cambio password, validazione e gestione stato attivo/disattivo.
- **Gestione Errori e Logging**:  
  - Output user-friendly in caso di errori o eccezioni.
  - Logging avanzato per troubleshooting e audit.

---

## 5. Funzionalità Avanzate e Parti Complesse

- **Sicurezza**:  
  - Password hashate con SHA-256 + salt casuale per ogni utente.
  - Verifica sicura delle password (confronto byte-level, prevenzione timing attack).
- **Validazione Centralizzata**:  
  - Tutti i dati (user, todo, project) sono validati sia in fase di creazione che modifica, prevenendo dati incoerenti.
- **Recurring Tasks**:  
  - Gestione trasparente di attività ricorrenti, con avanzamento automatico delle occorrenze al completamento.
- **Mocking Static Methods nei Test**:  
  - Uso avanzato di Mockito per testare anche utility statiche e CLI senza side effect.
- **Test Coverage**:  
  - Test su edge case, validazione, iterator custom, override metodi fondamentali (`equals`, `hashCode`, `toString`).
- **CLI Testata in Isolamento**:  
  - Possibilità di testare i metodi privati della CLI tramite reflection e mocking completo delle dipendenze.

---

## 6. 30 Domande Possibili con Risposte

1. **Q:** Come vengono gestite le password nel sistema?  
   **A:** Le password sono hashate con SHA-256 e salt casuale per ogni utente, mai memorizzate in chiaro.

2. **Q:** Qual è il pattern architetturale principale del progetto?  
   **A:** Layered Architecture (model, dao, service, cli).

3. **Q:** Come avviene la validazione dei dati utente?  
   **A:** Nei costruttori e setter degli oggetti model (User), con eccezioni specifiche per valori non validi.

4. **Q:** Come posso eseguire l’applicazione da terminale?  
   **A:** Con `mvn exec:java` grazie al plugin `exec-maven-plugin`.

5. **Q:** Che ruolo ha il package `org.lucian.todos.service`?  
   **A:** Orchestrazione della logica di business e validazione avanzata.

6. **Q:** Quale database è supportato?  
   **A:** SQLite tramite JDBC driver (già incluso tra le dipendenze).

7. **Q:** Come vengono gestite le attività ricorrenti (RecurringTodo)?  
   **A:** Ogni volta che una ricorrenza è completata, si aggiorna automaticamente la prossima scadenza e il contatore delle occorrenze.

8. **Q:** Come sono testate le funzionalità CLI?  
   **A:** Con JUnit 5 e Mockito, mockando input/output di sistema e metodi statici.

9. **Q:** Che tool di logging viene usato?  
   **A:** SLF4J con Logback come implementazione.

10. **Q:** Come viene gestito l’output della CLI nei test?  
    **A:** Redirezionando `System.out` e `System.err` su buffer per verificarne il contenuto.

11. **Q:** È possibile cambiare la password?  
    **A:** Sì, tramite la CLI e solo dopo verifica della password attuale.

12. **Q:** Come viene gestito lo stato “attivo” di un utente?  
    **A:** Tramite un campo booleano nel model e controlli in fase di login.

13. **Q:** Che tipi di statistiche vengono calcolate?  
    **A:** Su task (totali, per stato, overdue) e progetti (completati, attivi, percentuale avanzamento).

14. **Q:** La CLI è interattiva o basata su comandi?  
    **A:** Interattiva, con menu e prompt per l’input utente.

15. **Q:** Come vengono gestiti i progetti?  
    **A:** Si possono creare, modificare, eliminare, cercare e associare/dissociare task.

16. **Q:** È possibile cercare un task per titolo o descrizione?  
    **A:** Sì, tramite funzionalità di ricerca nella CLI.

17. **Q:** Come sono gestite le eccezioni di database?  
    **A:** Tramite `DatabaseException` e messaggi user-friendly per l’utente.

18. **Q:** Come si garantisce la consistenza delle date nei progetti?  
    **A:** Validazione che la data di inizio non sia dopo quella di fine.

19. **Q:** Come vengono gestiti i test dei metodi privati?  
    **A:** Usando reflection per invocare i metodi privati direttamente nei test.

20. **Q:** È possibile associare un task a più progetti?  
    **A:** No, ogni task può essere associato a un solo progetto alla volta.

21. **Q:** Cosa accade se un task urgente viene creato senza dueDate?  
    **A:** Viene loggato un warning, ma la creazione è permessa.

22. **Q:** Come vengono gestiti i tentativi di login falliti?  
    **A:** Viene restituito un messaggio generico per evitare enumeration degli account.

23. **Q:** Come sono gestite le statistiche delle priorità dei task?  
    **A:** L’infrastruttura è pronta ma richiede estensione dei DAO per conteggi reali (attualmente placeholder nei test).

24. **Q:** Come è isolata la logica di business dalla persistenza?  
    **A:** Tramite DAO pattern e service layer.

25. **Q:** È possibile estendere facilmente la logica di business?  
    **A:** Sì, la separazione in livelli e l’uso di interfacce/mocking lo rende semplice.

26. **Q:** Come vengono gestite le iterazioni sui todo di un progetto?  
    **A:** Con un Iterator custom che gestisce anche il caso di liste vuote.

27. **Q:** Le eccezioni custom hanno messaggi user-friendly?  
    **A:** Sì, vengono inclusi dettagli chiave (es. id dell’oggetto non trovato).

28. **Q:** Come viene gestito il logging degli errori?  
    **A:** Tutte le eccezioni vengono loggate con dettagli tramite SLF4J/Logback.

29. **Q:** Come viene gestita la validazione delle attività ricorrenti?  
    **A:** Si controlla che l’intervallo sia valido, che le occorrenze siano >= 1, ecc.

30. **Q:** Il sistema è facilmente testabile e coperto da test?  
    **A:** Sì, grazie a JUnit 5, Mockito e mocking avanzato, con copertura di casi base e avanzati.