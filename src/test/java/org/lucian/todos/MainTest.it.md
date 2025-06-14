## Scopo della classe

`MainTest` è una classe di test unitario basata su **JUnit 5** (Jupiter) che verifica alcuni aspetti fondamentali dell’ambiente di test e funzionalità basilari dell’applicazione:

- Verifica che il setup di JUnit 5 funzioni correttamente.
- Verifica che il metodo statico `Main.getApplicationName()` restituisca il valore atteso.
- (Prepara la base per eventuali test futuri che richiedano il controllo dell’output console.)

---

## Struttura della classe

### 1. Configurazione degli stream di output

Per molti test di applicazioni CLI, è utile intercettare e verificare ciò che viene stampato su `System.out` e `System.err`.  
Questa classe lo fa ridefinendo temporaneamente questi stream:

- **Attributi**:
  - `ByteArrayOutputStream outContent, errContent`: buffer per raccogliere l’output.
  - `PrintStream originalOut, originalErr`: riferimenti agli stream originali, per ripristinarli dopo il test.

- **setUp** (`@BeforeEach`):  
  - Vengono creati nuovi buffer.
  - `System.setOut` e `System.setErr` vengono reindirizzati verso questi buffer.

- **restoreStreams** (`@AfterEach`):  
  - Gli stream originali vengono ripristinati, per evitare effetti collaterali tra test diversi.

**Nota complessa:**  
Questa tecnica permette di testare metodi che stampano direttamente su console, intercettando l’output senza modificarne l’implementazione. È fondamentale per testare CLI, logging e messaggi di errore.

---

### 2. Uso di JUnit 5 e Mockito

- **JUnit 5**:  
  - Annotations come `@BeforeEach`, `@AfterEach`, `@Test`, `@DisplayName` forniscono chiarezza e modularità ai test.
- **Mockito Extension**:  
  - L’annotazione `@ExtendWith(MockitoExtension.class)` abilita le funzionalità di Mockito per eventuali mock futuri.  
  - In questa classe, non vengono usati mock esplicitamente, ma la presenza dell’extension permette di aggiungere facilmente dipendenze mockate in test successivi.  
  - È un esempio di “test scaffolding”, preparazione per l’estensione futura.

---

### 3. Test presenti

#### a. `testBasicAssertion`

- **Scopo**:  
  - Verificare che il setup di JUnit 5 funzioni (test banale).
- **Cosa verifica**:
  - `assertTrue(true)`: sempre vero.
  - `assertEquals(2, 1 + 1)`: verifica operazioni matematiche di base.
- **Utilità**:  
  - Serve esclusivamente come “sanity check” della configurazione del framework di test.

#### b. `testGetApplicationName`

- **Scopo**:  
  - Verificare che il metodo statico `Main.getApplicationName()` restituisca la stringa attesa ("Task Management System").
- **Cosa verifica**:
  - `assertNotNull`: il nome non deve essere null.
  - `assertEquals("Task Management System", appName)`: il valore deve essere esattamente quello atteso.
- **Nota**:  
  - Questo test è utile come esempio di test black-box di un metodo statico, senza dipendenze.

---

## Parti complesse e best practice

- **Redirect degli stream di output**:  
  - È una tecnica utile per testare CLI e metodi che fanno uso di `System.out`/`System.err`, ma va usata con cautela per evitare interferenze tra test concorrenti o dipendenti dallo stesso JVM.
- **Uso di Mockito Extension**:  
  - Anche se qui non viene usato direttamente, prepara la classe a test future che richiedano mocking di dipendenze complesse, facilitando la scrittura di test isolati e controllati.
- **Ripristino dopo i test**:  
  - Il ripristino degli stream è fondamentale per evitare che un test “sporchi” altri test, buona pratica di isolamento dei test.

---

## Domande da colloquio e risposte

**Q: Perché è utile ridefinire System.out/System.err nei test?**  
**A:**  
Per intercettare e verificare l’output prodotto dalla CLI o dai metodi che stampano su console, senza modificare il codice di produzione.

---

**Q: Quali rischi comporta il redirect degli stream di sistema?**  
**A:**  
Se non vengono ripristinati correttamente, possono causare comportamenti inattesi negli altri test o nella JVM, soprattutto in esecuzioni parallele.

---

**Q: Perché usare MockitoExtension se non ci sono mock?**  
**A:**  
Per preparare la classe a futuri test che richiedano mocking, facilitando l’iniezione di dipendenze simulate e la scrittura di test più complessi.

---

## Conclusione

`MainTest` è una classe di test semplice ma ben strutturata, che applica best practice di unit testing per Java CLI app, preparando il terreno per test più avanzati e garantendo l’isolamento e la manutenibilità dei test.
