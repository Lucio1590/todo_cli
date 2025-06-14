## Scopo della classe

`TodoFactory` implementa il **Factory Pattern** per centralizzare e astrarre la logica di creazione di diversi tipi di oggetti `Todo`, inclusi i todo semplici e quelli ricorrenti.  
Questo facilita la manutenzione, la testabilità e l'estensione delle modalità di creazione dei todo.

---

## Attributi principali

- **Logger logger:**  
  Usato per tracciare debug e informazioni sulle creazioni di todo.

---

## Metodi pubblici principali

### 1. `createTodo(TodoType type, String title)`
- **Crea un nuovo todo** del tipo specificato (`SIMPLE` o `RECURRING`) solo con il titolo.
- Valida parametri e lancia `IllegalArgumentException` in caso di errore.
- Usa uno `switch` su `TodoType`:
  - `SIMPLE` → ritorna un nuovo `Todo`.
  - `RECURRING` → ritorna un nuovo `RecurringTodo`.

---

### 2. `createSimpleTodo(...)`
- Crea un todo semplice con titolo, descrizione, scadenza e priorità.
- Parametri: `title`, `description`, `dueDate`, `priority`.
- Valida il titolo.

---

### 3. `createRecurringTodo(...)`
- **Overload 1:**  
  - Crea un `RecurringTodo` con titolo, descrizione, scadenza iniziale, priorità, intervallo di ricorrenza (`Period`).
- **Overload 2:**  
  - Come sopra, ma aggiunge anche il numero massimo di occorrenze.
- Entrambi validano il titolo.

---

### 4. Shortcut Factory Methods

- **`createDailyTodo(String title, LocalDate dueDate)`**  
  Crea un todo ricorrente giornaliero (scorciatoia con intervallo a 1 giorno).

- **`createWeeklyTodo(String title, LocalDate dueDate)`**  
  Crea un todo ricorrente settimanale.

- **`createMonthlyTodo(String title, LocalDate dueDate)`**  
  Crea un todo ricorrente mensile.

- **`createUrgentTodo(String title)`**  
  Crea un todo semplice con priorità `URGENT` e scadenza a oggi.

---

### 5. `createFromTemplate(Todo template, String newTitle)`
- Crea un nuovo todo copiando tutte le proprietà da un template esistente, cambiando il titolo.
- Supporta sia template di tipo `Todo` semplice che `RecurringTodo`.
- Lancia eccezione se il template è null.

---

### 6. `getTodoType(Todo todo)`
- Determina il tipo (`SIMPLE` o `RECURRING`) di un oggetto todo fornito.

---

## Validazione

### `validateTodoParameters(String title)`
- Metodo privato riutilizzato per assicurarsi che il titolo:
  - Non sia null o vuoto.
  - Non superi i 255 caratteri.
- Lancia `IllegalArgumentException` in caso di validazione fallita.

---

## Logiche e “tricky parts” di rilievo

- **Factory Pattern:**  
  Permette di disaccoppiare la logica di creazione dei todo dalla business logic e dai controller.
- **Overloading dei metodi:**  
  Gestisce casi d’uso differenti e scorciatoie per la creazione rapida di recurring todo (giornaliero, settimanale, mensile, urgente).
- **Clone via template:**  
  Permette di duplicare un todo esistente (anche ricorrente) cambiando solo il titolo.
- **Validazione centralizzata:**  
  Tutti i metodi pubblici che creano todo passano dalla stessa validazione del titolo, riducendo la duplicazione del codice.
- **Logging:**  
  Ogni azione di creazione è loggata con livello `debug`, utile per tracciare la provenienza di eventuali problemi di inizializzazione.

---

## Esempi d’uso

```java
// Creazione di un todo semplice
Todo t1 = TodoFactory.createSimpleTodo("Scrivi documentazione", "Scrivere i dettagli...", LocalDate.now().plusDays(3), Priority.HIGH);

// Creazione di un recurring todo settimanale
RecurringTodo rt = TodoFactory.createWeeklyTodo("Pulizia database", LocalDate.now().plusDays(1));

// Creazione di un todo urgente
Todo urgent = TodoFactory.createUrgentTodo("Fix security bug");

// Clonazione di un todo esistente
Todo clone = TodoFactory.createFromTemplate(t1, "Scrivi altra documentazione");
```

---

## Domande da colloquio e risposte

**Q: Perché usare una factory statica invece di istanziare direttamente i todo?**  
**A:**  
- Centralizza la validazione e la logica di creazione.
- Permette di cambiare facilmente la strategia di creazione senza modificare tutti i punti di utilizzo.
- Riduce la duplicazione di codice.

**Q: Cosa succede se passo un titolo vuoto?**  
**A:**  
- Viene lanciata una `IllegalArgumentException` grazie alla validazione centralizzata.

**Q: Come si estende per nuovi tipi di todo?**  
**A:**  
- Si aggiunge un nuovo caso allo `switch` di `createTodo`, e si estende eventualmente la gerarchia di classi.

---

## Conclusioni

`TodoFactory` rende più robusta, modulare ed estendibile la creazione di oggetti `Todo` e `RecurringTodo`, favorendo la manutenzione e la coerenza dell’intero sistema.