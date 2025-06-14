## Scopo del package

Il package contiene **eccezioni personalizzate** (custom exceptions) che rappresentano in modo specifico gli errori e le condizioni anomale che possono verificarsi nell’applicazione Todo Management System.  
Queste eccezioni migliorano la chiarezza del codice, consentono una gestione centralizzata e permettono di distinguere facilmente le cause degli errori.

---

## Panoramica delle classi (tipiche) presenti

> ⚠️ Il dettaglio delle classi può variare leggermente a seconda delle versioni, ma generalmente sono presenti:

- **`DatabaseException`**
- **`AuthenticationException`**
- **`ProjectNotFoundException`**
- **`TodoNotFoundException`**
- **`TodoManagementException`**
- ... (altre specifiche per domini particolari)

---

### 1. `DatabaseException`

**Estende:** tipicamente `Exception` (checked)  
**Scopo:** Segnalare errori legati all’accesso, manipolazione o inizializzazione del database.

**Caratteristiche:**
- Permette di propagare la causa originale tramite il costruttore.
- Utilizzata per distinguere errori a livello infrastrutturale da quelli di business.

**Esempio costruttore:**
```java
public DatabaseException(String message, Throwable cause) {
    super(message, cause);
}
```

---

### 2. `AuthenticationException`

**Estende:** spesso `Exception`  
**Scopo:** Segnalare errori nella procedura di autenticazione (login, registrazione, permessi).

**Caratteristiche:**
- Può essere usata per gestire errori noti come credenziali errate, account disabilitati ecc.
- Permette di personalizzare il messaggio per l’utente e per i log.

---

### 3. `ProjectNotFoundException`  
### 4. `TodoNotFoundException`

**Estendono:** tipicamente `Exception`  
**Scopo:**  
- Indicare che una risorsa specifica (project o todo) non è stata trovata nel DB.
- Permettono una gestione fine e user-friendly di errori “not found”.

**Caratteristiche:**
- Spesso includono l’id della risorsa cercata.
- Permettono di distinguere fra “errore di ricerca” e altri errori più gravi.

---

### 5. `TodoManagementException`

**Estende:** tipicamente `Exception`  
**Scopo:**  
- Eccezione “generica” per errori di business logic non coperti dalle altre custom.
- Può essere usata come superclasse per altre eccezioni specifiche.

---

## Best Practice adottate

- **Checked exceptions per errori previsti:**  
  La maggior parte delle custom exception qui estende `Exception` (checked), il che costringe a gestirle esplicitamente o a propagarle.
- **Costruttori con causa:**  
  Tutte le eccezioni accettano la causa (`Throwable cause`) per mantenere la traccia dello stack originale.
- **Messaggi chiari:**  
  I messaggi sono pensati per essere usati sia nei log che per feedback utente.

---

## Integrazione con la gestione centralizzata degli errori

Le custom exceptions sono progettate per essere **catturate e gestite** da una utility centralizzata come `ErrorHandler`, che:
- Logga i dettagli tecnici.
- Mostra messaggi user-friendly.
- Distingue il tipo di errore per fornire feedback specifico a seconda della classe dell’eccezione.

---

## Domande da colloquio: approfondimenti e risposte

### Q1: **Perché non usare solo RuntimeException?**

**Risposta:**  
Non tutte le eccezioni dovrebbero essere unchecked. Le checked exception (quelle che estendono `Exception` ma non `RuntimeException`) obbligano il chiamante a gestire il caso di errore, rendendo il contratto del metodo esplicito.  
Nel contesto di un’applicazione enterprise, è importante **obbligare** chi scrive codice a gestire errori previsti, come problemi di database, autenticazione, o risorse non trovate.  
Le unchecked vanno riservate a errori di programmazione (null pointer, bug logici) che non si possono o non si dovrebbero gestire a runtime.

---

### Q2: **Quando una checked exception è preferibile?**

**Risposta:**  
Quando l’errore è **recuperabile** o fa parte del normale flusso applicativo e si vuole **forzare la gestione** da parte del chiamante.  
Esempi:  
- Fallimento connessione DB (`DatabaseException`)
- Risorsa non trovata (`TodoNotFoundException`)
- Validazione di input fallita

---

### Q3: **Come e dove gestire la causa originale dell’errore?**

**Risposta:**  
La causa originale va sempre propagata e collegata tramite il costruttore:
```java
public DatabaseException(String message, Throwable cause) {
    super(message, cause);
}
```
In questo modo si preserva lo stacktrace completo, utile per debug e logging.  
La causa va passata fin dalla sorgente dell’errore e “incapsulata” nella custom exception, così che arrivi fino alla gestione centralizzata.

---

### Q4: **Come integrare le eccezioni custom con la gestione centralizzata degli errori?**

**Risposta:**  
La gestione centralizzata (es: `ErrorHandler`) può distinguere il tipo di eccezione tramite `instanceof` e reagire in modo diverso:
- Log dettagliato per `DatabaseException`
- Messaggio user-friendly per `ProjectNotFoundException`
- Avviso di sicurezza per `AuthenticationException`
Inoltre, le custom exception permettono di **non confondere** errori di business con bug di programmazione.

---

## Altri aspetti di rilievo

- **Estensibilità:**  
  Aggiungere nuove eccezioni custom è semplice e migliora la manutenibilità del codice.
- **Internazionalizzazione:**  
  I messaggi delle eccezioni possono essere internazionalizzati più facilmente se separati dai messaggi di sistema.
- **Chiarezza dei contratti:**  
  Le signature dei metodi che lanciano checked exception sono auto-documentanti.

---

## Esempio pratico di utilizzo

```java
try {
    todoService.updateTodo(todo);
} catch (TodoNotFoundException e) {
    ErrorHandler.handleBusinessError(e, "updating todo");
} catch (DatabaseException e) {
    ErrorHandler.handleDatabaseError(e, "updating todo");
}
```

---

## Conclusione

Il package `org.lucian.todos.exceptions` fornisce una **base solida e scalabile** per la gestione degli errori, migliorando chiarezza, manutenibilità e affidabilità dell’applicazione.