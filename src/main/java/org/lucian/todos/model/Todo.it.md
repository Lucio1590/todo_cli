## Scopo della classe

La classe `Todo` modella un’attività (“to-do”) di base nel sistema, rappresentando un singolo task con proprietà come titolo, descrizione, scadenza, priorità, stato e riferimenti a progetto e utente.  
Fornisce anche metodi per la gestione dello stato, la validazione e alcune utility per la business logic.

---

## Attributi principali

- **Long id**: identificatore univoco (PK nel DB).
- **String title**: titolo dell’attività (obbligatorio, validato).
- **String description**: descrizione opzionale.
- **LocalDate dueDate**: data di scadenza.
- **Priority priority**: priorità dell’attività (default: MEDIUM).
- **TodoStatus status**: stato dell’attività (`TODO`, `IN_PROGRESS`, `COMPLETED`, ecc.; default: TODO).
- **Long projectId**: riferimento al progetto padre (può essere null).
- **Long userId**: riferimento all’utente proprietario (può essere null).
- **LocalDateTime createdAt**: timestamp di creazione (final).
- **LocalDateTime updatedAt**: timestamp di ultimo aggiornamento.

---

## Costruttori

- **Todo()**: costruttore vuoto, inizializza timestamp, priorità e stato di default.
- **Todo(String title)**: costruttore con titolo obbligatorio, usa il setter per validazione.
- **Todo(String title, String description, LocalDate dueDate, Priority priority)**: costruttore completo.

---

## Metodi principali

### Getter/Setter

- Tutti i campi hanno getter e setter.
- I setter aggiornano sempre il campo `updatedAt` (tracciamento automatico delle modifiche).
- I setter di campi obbligatori (es. `setTitle`) fanno validazione (non null/vuoto).

---

### Gestione dello stato

- **markCompleted()**: imposta lo stato su COMPLETED.
- **markInProgress()**: imposta lo stato su IN_PROGRESS.
- **markCancelled()**: imposta lo stato su CANCELLED.

---

### Utility di logica

- **isOverdue()**: true se la dueDate è passata e il todo non è “finished”.
- **isDueToday()**: true se la dueDate è oggi.
- **isModifiable()**: true se lo stato attuale permette modifiche (delegato al metodo nello status).

---

### Validazione

- **validate()**: controlla che titolo, priorità e stato non siano null/vuoti; lancia IllegalStateException se non valido.

---

### Override di equals, hashCode, toString

- **equals/hashCode**: basati su id e title (due todo con stesso id e titolo sono considerati uguali).
- **toString()**: rappresentazione compatta con id, titolo, stato, priorità e dueDate.

---

## Best practice e “tricky parts”

- **Validazione nei setter e nei costruttori**: Il titolo viene sempre validato. È impossibile avere un todo senza titolo valido.
- **Modifica automatica di updatedAt**: Ogni cambiamento significativo aggiorna il timestamp di modifica.
- **Default robusti**: Priorità e stato hanno valori di default che evitano inconsistenze.
- **Check di stato in isOverdue/isModifiable**: La logica è delegata a metodi dello stato (presumibilmente un Enum con logica custom).

---

## Domande da colloquio e risposte

**Q: Come viene garantita la consistenza degli oggetti Todo?**  
**A:**  
Attraverso la validazione nei setter e nel metodo validate(), e valori di default robusti per priorità e stato.

---

**Q: Come viene gestito l’aggiornamento automatico dei timestamp?**  
**A:**  
Ogni setter che modifica un campo chiave aggiorna anche updatedAt a LocalDateTime.now().

---

**Q: Come si verifica se un todo è modificabile?**  
**A:**  
Tramite isModifiable(), che delega la logica all’enum TodoStatus.

---

**Q: Come si estende la logica per ricorrenza?**  
**A:**  
La classe è progettata per essere estesa (vedi RecurringTodo), sfruttando l’ereditarietà e i metodi protetti.

---

## Conclusioni

La classe `Todo` fornisce una base solida, sicura e facilmente estendibile per la gestione delle attività nel sistema, con attenzione a validazione, tracciamento e logica di dominio.