## Scopo della classe

Questa classe di test rappresenta una **suite avanzata di test di unità** per la logica di dominio (“model”) dell’applicazione Todo.  
I test coprono casi limite, validazioni, edge case e alcune parti complesse dei modelli come:  
- Validazione dei dati (user, project, todo)
- Relazioni tra oggetti (project/todo)
- Iterator custom
- Override di `equals`, `hashCode` e `toString`
- Comportamenti di overdue, recurring e validazione forte

---

## Struttura e dettaglio dei test

### 1. **testUserInvalidCreation**

**Scopo:**  
Verifica la robustezza della validazione nella creazione di oggetti `User`.

**Cosa fa:**  
- Prova a creare User con username null, vuoto o email non valida.
- Si aspetta sempre che venga lanciata `IllegalArgumentException` con messaggi specifici.

**Parte complessa:**  
Il test verifica che la validazione nel costruttore sia stringente e che i messaggi di errore siano descrittivi, utile per debugging e UX.

---

### 2. **testProjectInvalidDates**

**Scopo:**  
Verifica che la logica di validazione delle date in `Project` sia rispettata.

**Cosa fa:**  
- Crea un progetto con data di inizio dopo la data di fine.
- Si aspetta una `IllegalStateException` con messaggio preciso.

**Parte complessa:**  
La validazione delle date è cruciale per la coerenza dei dati (es. un progetto non può finire prima di iniziare).  
Il test obbliga la chiamata a `validate()` per forzare il controllo.

---

### 3. **testTodoValidationAndOverdue**

**Scopo:**  
Testa la logica di overdue e validazione dello stato dei todo.

**Cosa fa:**  
- Crea un todo scaduto (dueDate nel passato): `isOverdue()` deve essere true.
- Marca il todo come completato: ora `isOverdue()` deve essere false.
- Cambia la dueDate a oggi: `isDueToday()` deve essere true.

**Parte complessa:**  
Verifica la coerenza dinamica tra stato e date, e la reattività dei metodi di logica di dominio ai cambiamenti di stato.

---

### 4. **testProjectTodoRelationship**

**Scopo:**  
Assicura che la relazione tra progetto e todo sia inizialmente vuota e che le statistiche siano corrette.

**Cosa fa:**  
- Crea un nuovo progetto.
- Verifica che il conteggio dei todo sia 0.
- Percentuale completamento 0.
- Progetto non completato.

---

### 5. **testProjectIterator**

**Scopo:**  
Verifica l’implementazione dell’`Iterator` custom sui todo del progetto.

**Cosa fa:**  
- Crea un progetto senza todo.
- Ottiene l’iterator e verifica che `hasNext()` sia false.

**Parte complessa:**  
Serve a garantire che il pattern Iterator sia implementato correttamente anche su collezioni vuote (no `NoSuchElementException`).

---

### 6. **testEqualsAndHashCode**

**Scopo:**  
Verifica la correttezza degli override di `equals` e `hashCode` in `Todo` e `Project`.

**Cosa fa:**  
- Due todo con stesso id e titolo sono uguali e hanno stesso hash.
- Due project con stesso id e nome sono uguali.

**Parte complessa:**  
Fondamentale per funzionamento corretto in collezioni, set, mappe e per la persistenza.

---

### 7. **testToStringMethods**

**Scopo:**  
Verifica che i metodi `toString` personalizzati includano le informazioni chiave.

**Cosa fa:**  
- Crea un User e verifica che il suo `toString` contenga l’username.
- Crea un Project e verifica che il suo `toString` contenga il nome.
- Crea un Todo e verifica che il suo `toString` contenga il titolo.
- Crea un RecurringTodo e verifica che il suo `toString` contenga la parola "RecurringTodo".

**Parte complessa:**  
Il test garantisce che i metodi di debug/log siano informativi, facilitando troubleshooting e tracce nei log.

---

## Best practice applicate

- **Uso di assertThrows con controllo del messaggio:**  
  Non basta aspettarsi l’eccezione: si controlla anche che il messaggio sia quello atteso.
- **Test di edge case:**  
  Vengono testati casi-limite (date invertite, email sbagliate, oggetti vuoti) che possono causare bug difficili da individuare in produzione.
- **Separazione dei casi di test:**  
  Ogni test si focalizza su una specifica funzionalità o regola di dominio.

---

## Parti complesse e tricky

- **Validazione forte nei costruttori:**  
  Le eccezioni vengono lanciate già in fase di costruzione degli oggetti, non solo nei setter, prevenendo oggetti “zombie” nel sistema.
- **Iterator custom:**  
  Permette di scorrere i todo di un progetto anche se la lista è vuota, senza rischio di errori run-time.
- **Override di metodi fondamentali (`equals`, `hashCode`, `toString`):**  
  I test assicurano che l’identità logica degli oggetti sia coerente con l’uso che ne verrà fatto in collezioni, ORM, ecc.
- **Sinergia tra stato e date nei todo:**  
  Il test su overdue e completamento verifica che i metodi reagiscano dinamicamente ai cambi di stato e data.

---

## Domande da colloquio e risposte

**Q: Perché è importante testare anche i messaggi delle eccezioni?**  
**A:**  
Per garantire che gli errori siano chiari e utili all’utente/sviluppatore, e che la logica di validazione sia granulare.

---

**Q: Qual è il vantaggio di un iterator custom su una lista di oggetti domain?**  
**A:**  
Permette di nascondere la struttura interna e di aggiungere logica custom (es. filtri, lazy loading) senza esporre la lista reale.

---

**Q: Perché testare `equals` e `hashCode`?**  
**A:**  
Per evitare bug in collezioni come Set/Map o problemi di duplicazione e identificazione degli oggetti.

---

**Q: Cosa si intende per validazione “forte” nei model?**  
**A:**  
Significa che è impossibile creare oggetti in stato incoerente, perché le regole sono applicate già nei costruttori/setter e vengono enforce-ate tramite eccezioni.

---

## Conclusioni

`ModelAdvancedTest` è una suite di test robusta che copre le parti più difficili e sensibili della logica di dominio, garantendo coerenza, sicurezza e affidabilità del core dell’applicazione.