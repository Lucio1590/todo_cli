## Scopo della classe

La classe `Project` rappresenta un progetto che raggruppa più attività (`Todo`).  
Realizza il **Composite Pattern** per gestire in modo strutturato e polimorfico collezioni di oggetti `Todo`.  
Fornisce anche metodi per statistiche, validazione e gestione avanzata dello stato del progetto.

---

## Attributi principali

- **Long id**: identificatore univoco del progetto (DB PK).
- **String name**: nome del progetto (obbligatorio).
- **String description**: descrizione opzionale.
- **LocalDate startDate, endDate**: date di inizio e fine progetto.
- **Long userId**: riferimento all’utente proprietario.
- **List<Todo> todos**: lista delle attività (composite pattern).
- **LocalDateTime createdAt, updatedAt**: timestamps di creazione e aggiornamento.

---

## Costruttori

- **Project()**: vuoto, inizializza la lista di todo e i timestamps.
- **Project(String name)**: obbliga a specificare il nome, valido e non vuoto.
- **Project(String name, String description, LocalDate start, LocalDate end)**: costruttore completo.

---

## Metodi principali

### Getter/Setter

- Per tutti i campi principali, con validazioni su nome e aggiornamento automatico di `updatedAt`.

---

### Gestione dei Todo (Composite Pattern)

- **addTodo(Todo todo)**: Aggiunge un todo al progetto, aggiorna il projectId del todo e il timestamp.
- **removeTodo(Todo todo)**: Rimuove un todo dalla lista, azzera il suo projectId, aggiorna timestamp.
- **removeTodoById(Long todoId)**: Rimuove un todo specifico tramite ID.
- **getTodos()**: Restituisce una vista immutabile della lista todos.
- **getTodoCount()**: Restituisce la quantità di attività.
- **findTodoById(Long todoId)**: Trova un todo per ID, oppure null se non trovato.
- **getTodosByStatus(TodoStatus status)**: Filtra i todo per stato.
- **getTodosByPriority(Priority priority)**: Filtra i todo per priorità.
- **getOverdueTodos()**: Restituisce i todo scaduti.
- **getCompletedTodos()**: Restituisce i todo completati.
- **iterator()**: Restituisce un iteratore custom per i todo del progetto.

---

### Stato, statistiche e validazione

- **getCompletionPercentage()**: Percentuale di completamento (0-100%) in base ai todo completati.
- **isCompleted()**: True se tutti i todo sono completati.
- **isOverdue()**: True se il progetto è scaduto (endDate passata e non completato).
- **validate()**: Valida lo stato del progetto (nome presente, date coerenti), lancia `IllegalStateException` se non valido.

---

### Pattern implementati

- **Composite pattern**: Ogni progetto contiene una lista di `Todo` e li gestisce come sotto-componenti.
- **Iterator pattern**: Fornisce un iteratore custom per scorrere i todo del progetto.

---

### Override di equals, hashCode, toString

- **equals/hashCode**: Basati su id e name (due progetti con stesso id e nome sono considerati uguali).
- **toString**: Restituisce una rappresentazione compatta con id, nome, numero todo e percentuale completamento.

---

## Best practice e “tricky parts”

- **Immutabilità delle liste**: `getTodos()` restituisce una vista non modificabile.
- **Aggiornamento automatico di updatedAt**: Ogni set di campo significativo aggiorna il timestamp.
- **Validazione dati**: Invocabile con `validate()`, ma anche i setter più importanti fanno check.
- **Gestione null**: Tutte le operazioni accettano e gestiscono null in modo sicuro (ad es. non lanciando NPE).
- **Separazione tra stato e comportamento**: La classe non esegue operazioni di persistenza, ma solo logica di dominio.

---

## Domande da colloquio e risposte

**Q: Che pattern implementa questa classe e perché?**  
**A:**  
Il Composite Pattern, per permettere di gestire in modo uniforme sia singoli todo che insiemi di todo in un progetto. Implementa inoltre un Iterator custom per scorrere i todo.

---

**Q: Come viene gestita la consistenza tra progetto e todo?**  
**A:**  
Quando si aggiunge/rimuove un todo, il suo projectId viene aggiornato di conseguenza, mantenendo coerenza tra la struttura del dominio e la persistenza.

---

**Q: Come viene calcolato il completamento di un progetto?**  
**A:**  
Tramite `getCompletionPercentage()`, calcolando il rapporto tra todo completati e totali.

---

**Q: Come viene gestita la validazione?**  
**A:**  
Tramite il metodo `validate()`, che controlla nome non vuoto e coerenza tra startDate e endDate.

---

## Conclusioni

La classe `Project` è un esempio solido di modellazione di dominio, con attenzione a pattern, validazione e sicurezza dei dati, e può essere facilmente estesa o integrata in architetture più complesse.
