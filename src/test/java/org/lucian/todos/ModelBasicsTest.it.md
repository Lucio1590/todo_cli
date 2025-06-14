## Scopo della classe

`ModelBasicsTest` è una suite di **unit test** per la validazione delle funzionalità base dei modelli principali del sistema di gestione task (User, Project, Todo, RecurringTodo, eccezioni custom).  
Questi test coprono la corretta creazione, uso e validazione delle entità core e verificano che le eccezioni personalizzate forniscano messaggi utili.

---

## Dettaglio dei test presenti

### 1. `testUserCreationAndValidation`

**Scopo:**  
Verificare la corretta istanziazione e i getter di `User`, oltre alla logica di validazione implicita.

**Cosa verifica:**  
- Correttezza di username, email, stato attivo.
- Timestamp di creazione non nullo.
- Metodo `getFullName()` ritorna la stringa attesa ("Lucian Diaconu").

### 2. `testProjectCreationAndValidation`

**Scopo:**  
Testare la creazione di un `Project` e la validazione delle sue proprietà.

**Cosa verifica:**  
- Nome e descrizione corretti.
- Un nuovo progetto non deve risultare completato.
- Il metodo `validate()` non deve sollevare eccezioni con dati validi.

### 3. `testTodoCreationAndStatus`

**Scopo:**  
Testare la creazione di un `Todo` e la gestione dello stato.

**Cosa verifica:**  
- Correttezza di titolo, priorità, stato iniziale.
- Transizione di stato da TODO → IN_PROGRESS → COMPLETED tramite i metodi dedicati.

### 4. `testRecurringTodoFunctionality`

**Scopo:**  
Testare la logica di base di un `RecurringTodo` (attività ricorrente).

**Cosa verifica:**  
- Occorrenza iniziale a 1.
- Presenza di occorrenze residue.
- Dopo aver chiamato `markCompleted()`, l’occorrenza incrementa a 2 e lo stato viene resettato a TODO (segno che la ricorrenza è avanzata correttamente).

### 5. `testExceptionMessages`

**Scopo:**  
Verificare che le eccezioni custom abbiano messaggi user-friendly coerenti, contenenti le informazioni rilevanti.

**Cosa verifica:**  
- `ProjectNotFoundException` contiene l’id del progetto.
- `TodoNotFoundException` contiene l’id del todo.
- `DatabaseException` contiene la stringa "database error" (case-insensitive).

---

## Best practice e aspetti complessi

- **Copertura delle validazioni**:  
  I test assicurano che i costruttori e i metodi di validazione dei model impediscano lo stato incoerente degli oggetti.
- **Test delle transizioni di stato**:  
  Si verifica che la logica di business sugli stati (`markInProgress`, `markCompleted`, ecc.) sia implementata correttamente.
- **Gestione delle ricorrenze**:  
  Il test su `RecurringTodo` assicura che la progressione delle occorrenze e il reset dello stato siano automatici e affidabili.
- **Messaggi di errore significativi**:  
  Le eccezioni personalizzate forniscono feedback chiari, facilitando debugging e UX.
- **Uso di assertDoesNotThrow**:  
  Garantisce che le validazioni non sollevino eccezioni con dati validi, offrendo robustezza ai test.

---

## Domande da colloquio e risposte

**Q: Perché è importante testare anche i messaggi delle eccezioni?**  
**A:**  
Per assicurarsi che siano chiari e informativi per l’utente (o per il log/debug), facilitando la diagnosi di errori.

---

**Q: Qual è la differenza tra i test su `Todo` e `RecurringTodo`?**  
**A:**  
Mentre `Todo` testa le transizioni di stato base, `RecurringTodo` verifica la gestione automatica delle occorrenze e la logica di reset dello stato per attività ricorrenti.

---

**Q: Perché si usa `assertDoesNotThrow` su `validate()`?**  
**A:**  
Per assicurare che la validazione accetti dati validi e sia robusta, senza sollevare errori inaspettati.

---

**Q: In che modo questi test aiutano la manutenibilità del sistema?**  
**A:**  
Permettono di identificare rapidamente regressioni o cambiamenti involontari nella logica dei model, garantendo coerenza e affidabilità.

---

## Conclusioni

`ModelBasicsTest` rappresenta una solida base di test per la logica core dei modelli, assicurando che le funzionalità fondamentali e le regole di dominio siano sempre rispettate e che le eccezioni restituite siano significative e user-friendly.
