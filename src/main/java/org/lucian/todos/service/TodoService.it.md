## Scopo della classe

`TodoService` è la classe di **servizio** responsabile di tutta la logica di business relativa ai task (todo).  
Si occupa di:
- Validazione avanzata delle attività
- Orchestrazione tra DAO e autenticazione utente
- Applicazione delle regole di dominio e di business
- Gestione avanzata dei todo ricorrenti
- Calcolo di statistiche e ricerca

---

## Attributi principali

- **TodoDAO todoDAO**  
  Interfaccia per l’accesso ai dati persistenti dei todo.
- **AuthenticationService authService**  
  Gestisce l’utente attualmente autenticato e le regole di accesso.

---

## Metodi principali e logica

### 1. Creazione di un todo (`createTodo`)

- **Validazione**: chiama `validateTodo` che applica regole di consistenza e business (vedi dettagli sotto).
- **Impostazione default**: priorità e stato vengono defaultati se non specificati (MEDIUM/TODO).
- **Associazione utente**: imposta il campo `userId` con l’id dell’utente autenticato (solleva errore se non autenticato).
- **Persistenza**: chiama `todoDAO.create`.
- **Gestione errori**: differenzia tra eccezioni di validazione, database e errori generici, loggando sempre il dettaglio.

---

### 2. Ricerca/lettura todo

- **findTodoById**:  
  Recupera un todo per id, solleva `TodoNotFoundException` se non trovato.
- **getAllTodos**:  
  Restituisce tutti i todo.
- **getTodosByProject/status/priority**:  
  Filtra i todo per projectId, stato o priorità (validando i parametri).
- **getOverdueTodos**:  
  Recupera tutti i todo scaduti (non completati/cancellati).
- **getTodosDueToday**:  
  Restituisce solo i todo con scadenza oggi (filtrando in memoria).

---

### 3. Modifica todo (`updateTodo`)

- **Validazione**: chiama `validateTodo`.
- **Controllo esistenza**: verifica che il todo esista prima di aggiornare.
- **Persistenza**: chiama `todoDAO.update`.

---

### 4. Gestione stato dei todo

- **markTodoCompleted**:  
  Marca un todo come COMPLETED.  
  - Se è un `RecurringTodo` con altre occorrenze residue, esegue la progressione automatica (vedi parte “complessa” sotto).
- **markTodoInProgress/markTodoCancelled**:  
  Marca il todo come IN_PROGRESS o CANCELLED, ma solo se lo stato attuale lo permette (`isModifiable`).
- **updateTodoStatus**:  
  Permette di cambiare lo stato a uno specifico, con controllo della modificabilità.

---

### 5. Gestione relazione con progetto

- **assignTodoToProject**:  
  Collega un todo a un progetto dato il projectId.
- **removeTodoFromProject**:  
  Scollega un todo dal progetto (projectId a null).

---

### 6. Eliminazione (`deleteTodo`)

- Elimina un todo per id, controllando che non sia nullo.

---

### 7. Ricerca avanzata (`searchTodos`)

- Filtra tutti i todo per titolo o descrizione che contenga una stringa (case-insensitive).
- **Nota**: la ricerca avviene in memoria, non a livello di query SQL (scelta per semplicità, ma potenzialmente inefficiente con DB grandi).

---

### 8. Calcolo statistiche (`getTodoStatistics`)

- Crea e restituisce un oggetto `TodoStatistics` con conteggi per:
  - Totale todo
  - Numero per stato (TODO, IN_PROGRESS, COMPLETED, CANCELLED)
  - Numero di todo scaduti (overdue)
- **Nota**: i conteggi per priorità sono stub e sempre a 0, andrebbero implementati con nuovi metodi DAO.

---

### 9. Validazione avanzata (`validateTodo`)

- Controlla che il todo non sia nullo, abbia titolo valido e lunghezze non eccessive.
- **Se RecurringTodo:**
  - Controlla che abbia un intervallo di ricorrenza valido
  - Che maxOccurrences sia almeno 1
  - Che currentOccurrence sia almeno 1
- **Business rules:**
  - Se priorità HIGH o URGENT deve avere una dueDate (solo warning, non errore bloccante)
  - Se la dueDate è nel passato su un nuovo todo, warning (non errore)

---

## Parti complesse e “tricky”

### **Gestione dei todo ricorrenti**

- Quando un todo (`RecurringTodo`) viene marcato come completato:
  - Se ci sono ancora occorrenze residue (`hasMoreOccurrences()`), viene chiamato `moveToNextOccurrence()`, che:
    - Aggiorna la dueDate alla prossima occorrenza
    - Incrementa il contatore delle occorrenze
    - Reset dello stato
    - Calcola la nuova nextDueDate
  - Tutto questo avviene in modo trasparente per l’utente, permettendo la gestione automatica dei recurring task.
- L’update finale viene sempre fatto tramite DAO, garantendo la persistenza dello stato aggiornato.

---

### **Validazione centralizzata e multi-livello**

- Tutte le regole di business (lunghezze massime, coerenza dati, warning su date e priorità) sono raccolte in un unico metodo privato.
- La validazione per i recurring todo è separata e più restrittiva (intervallo e occorrenze).

---

### **Gestione robusta degli errori**

- Tutte le chiamate ai DAO sono racchiuse in try/catch, con log dettagliato e rilancio di eccezioni custom o generiche a seconda del contesto.
- Messaggi per l’utente sono pensati per essere “user-friendly” anche quando si verificano errori tecnici.

---

### **Ricerca in memoria**

- Il metodo `searchTodos` effettua il filtro in memoria su tutti i todo caricati dal DAO.
- Questa scelta è semplice e portabile, ma può essere inefficiente con un numero elevato di task (scalabilità).

---

### **Statistiche “stub”**

- Il conteggio per priorità (es. quanti HIGH, URGENT, ecc.) è impostato a 0 come placeholder, segnalando la necessità di estendere il DAO per supportare queste statistiche reali.

---

## Inner class

- **TodoStatistics**  
  DTO che contiene tutti i conteggi aggregati sulle attività, con metodi getter/setter e una `toString` compatta.

---

## Domande da colloquio e risposte

**Q: Come viene gestita la progressione dei recurring todo?**  
**A:**  
Quando un recurring todo viene completato, se ci sono occorrenze residue, viene aggiornata la dueDate, incrementato il contatore e calcolata la nuova scadenza tramite `moveToNextOccurrence`.

---

**Q: Dove si trova la logica di validazione delle attività?**  
**A:**  
In un unico metodo privato, che applica regole sia generali che specifiche per i todo ricorrenti, e business rules (es. warning su dueDate passata o priorità alta senza scadenza).

---

**Q: Perché la ricerca avviene in memoria e quali sono i limiti di questa scelta?**  
**A:**  
Per semplicità e portabilità. Tuttavia, con grandi dataset può portare a problemi di performance; sarebbe meglio implementare la ricerca direttamente tramite query SQL nel DAO.

---

**Q: Come si gestisce il legame tra utente e todo?**  
**A:**  
Alla creazione, il campo userId viene impostato usando l’utente attualmente autenticato tramite `AuthenticationService`.

---

## Conclusioni

`TodoService` è un esempio di service layer ben strutturato che centralizza la logica di business e la validazione per i task, con particolare attenzione alla gestione dei recurring todo, alla robustezza degli errori e alle regole di dominio.