## Scopo della classe

`ProjectService` è la classe di **servizio** dedicata alla gestione delle operazioni di business sui progetti.  
Racchiude la logica di validazione, orchestrazione tra DAO e regole di dominio legate ai progetti e ai loro todo.

---

## Attributi principali

- **ProjectDAO projectDAO**: DAO per la persistenza dei progetti.
- **TodoDAO todoDAO**: DAO per la persistenza dei todo.
- **AuthenticationService authService**: servizio per gestire l’utente autenticato e le regole di accesso.

---

## Metodi principali e logica

### 1. Creazione di un progetto (`createProject`)

- **Validazione**: chiama `validateProject` per assicurarsi che i dati di input siano coerenti (nome, descrizione, date).
- **Associazione utente**: imposta `userId` del progetto con l’utente attualmente autenticato. Se non è autenticato, solleva exception.
- **Persistenza**: usa `projectDAO.create`.
- **Log e gestione errori**: log dettagliato, gestione di errori di autenticazione o database.

---

### 2. Ricerca progetto per ID e caricamento todo (`findProjectById`)

- **Controllo ID**: valida che l’id non sia nullo.
- **Recupero progetto**: tramite `projectDAO.findById`.
- **Gestione not found**: solleva `ProjectNotFoundException` se non trovato.
- **Caricamento todo**: carica i todo associati al progetto tramite `todoDAO.findByProjectId` e li aggiunge tramite `addTodo` (gestione lista interna del progetto).
  - **Nota tricky**: la lista restituita da `getTodos()` è "unmodifiable", quindi i todo vengono aggiunti uno ad uno con `addTodo`.

---

### 3. Ricerca progetti per nome (`findProjectsByName`)

- **Ricerca parziale**: consente ricerca con match parziale (LIKE).
- **Validazione**: nome non nullo/vuoto.

---

### 4. Statistiche di progetto (`getProjectCompletionStats`)

- **Recupero todo**: recupera tutti i todo associati al progetto.
- **Calcolo statistiche**: calcola (tramite stream):
  - Numero totale todo, completed, in progress, cancelled, overdue
  - Percentuale di completamento
- **Ritorna oggetto**: `ProjectCompletionStats` con tutti i dati aggregati.

---

### 5. Aggiunta/rimozione todo da progetto

- **Aggiunta (`addTodoToProject`)**:
  - Verifica esistenza progetto.
  - Imposta il `projectId` sul todo.
  - Se il todo è nuovo (`id == null`), crea; altrimenti aggiorna.
- **Rimozione (`removeTodoFromProject`)**:
  - Verifica esistenza progetto.
  - Cerca il todo per id, verifica che sia effettivamente assegnato al progetto.
  - Imposta il `projectId` a null e aggiorna.

---

### 6. Update di progetto (`updateProject`)

- **Validazione**: come nella creazione.
- **Verifica esistenza**: tramite `projectDAO.exists`.
- **Update**: chiama `projectDAO.update`.

---

### 7. Eliminazione progetto (`deleteProject`)

- **Controllo null**: id non nullo.
- **Delete**: chiama `projectDAO.delete`, che si occupa anche di eliminare tutti i todo associati (atomico lato DAO).

---

### 8. Statistiche generali (`getProjectStatistics`)

- Usa metodi di DAO per calcolare:
  - Totale progetti
  - Progetti attivi
  - Progetti completati

---

### 9. Validazione progetti (`validateProject`)

- Controlla:
  - Nome non nullo/vuoto e lunghezza massima.
  - Descrizione lunghezza massima.
  - End date >= start date
  - Warning se si crea un progetto con end date già nel passato (log warning).

---

## Parti complesse e “tricky”

### **Gestione della lista di todo in Project**

- Il metodo `findProjectById` deve popolare la lista di todo del progetto.  
  Tuttavia, la lista restituita da `getTodos()` è immutabile per design (Composite Pattern).
- Per evitare modifiche dirette, i todo vengono aggiunti uno ad uno tramite il metodo `addTodo`, che aggiorna anche i riferimenti e i timestamp in modo corretto.

---

### **Separazione tra logica di dominio e persistenza**

- La classe non si occupa mai direttamente di SQL o DB, ma orchestra i DAO e si occupa di comporre i risultati e applicare le regole di business.
- Tutte le regole di validazione e controllo sono centralizzate qui, rendendo la logica facilmente testabile e modificabile senza toccare la persistenza.

---

### **Gestione delle statistiche**

- Le statistiche aggregate (sia per singolo progetto che globali) vengono calcolate in maniera funzionale tramite stream e metodi di supporto.
- La logica di calcolo è centralizzata negli inner classes `ProjectCompletionStats` e `ProjectStatistics`.

---

### **Gestione degli errori e delle eccezioni**

- Tutte le operazioni che possono fallire per motivi di accesso (es. utente non autenticato), not found, o errori di business, sollevano eccezioni specifiche (`AuthenticationException`, `ProjectNotFoundException`, `DatabaseException`, `IllegalArgumentException`).
- Gli errori vengono sempre loggati.

---

## Inner classes

- **ProjectCompletionStats**: oggetto con tutti i dati aggregati di completamento per un singolo progetto.
- **ProjectStatistics**: oggetto con statistiche globali sui progetti (totale, attivi, completati).

---

## Domande da colloquio e risposte

**Q: Perché la lista di todo in Project è immutabile?**  
**A:**  
Per evitare modifiche accidentali e forzare l’uso dei metodi di business (`addTodo`, `removeTodo`), garantendo consistenza dei dati e aggiornamento timestamp.

---

**Q: Dove si trova la logica di validazione dei dati di progetto?**  
**A:**  
Nel metodo privato `validateProject`, centralizzato in questo service, con tutte le regole di dominio, vincoli e warning.

---

**Q: Chi si occupa di eliminare i todo associati a un progetto quando questo viene cancellato?**  
**A:**  
La logica atomica è a livello di DAO (`ProjectDAOImpl`), ma la chiamata viene orchestrata da `ProjectService`.

---

**Q: Come sono gestite le statistiche e perché sono inner class?**  
**A:**  
Le statistiche sono classi interne per evitare dispersione e perché sono DTO strettamente legati al contesto di progetto; incapsulano i dati aggregati in modo pulito.

---

## Conclusioni

`ProjectService` è un esempio robusto di service layer in un’architettura a strati, che centralizza la logica di business, la validazione e l’orchestrazione tra DAO, garantendo una separazione netta tra dominio, persistenza e presentazione.