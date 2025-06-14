## Scopo della classe

Questa classe fornisce una suite avanzata di **unit test** per la CLI principale dell’applicazione (“TodoManagementCLI”).  
Utilizza **JUnit 5** e **Mockito** per testare scenari complessi di interazione tra CLI e servizi sottostanti, simulando input/output console e gestendo la dipendenza da servizi tramite mock.

---

## Struttura della classe

### 1. **Mocking e Setup globale**

- **Campi mockati**:  
  Tutte le dipendenze principali della CLI vengono mockate tramite `@Mock` di Mockito:
  - `Scanner` (per input utente)
  - `TodoService`, `ProjectService`, `AuthenticationService` (servizi core)
  - `AuthenticationCommandHandler`, `MainMenu` (handler CLI)
  - `DAOFactory` (per factory di DAO)
- **Output/Input stream**:  
  - Redireziona `System.out` e `System.err` su buffer per poter catturare e verificare l’output prodotto dalla CLI.
  - Salva i riferimenti originali per ripristino dopo ogni test.
  - Anche `System.in` viene gestito per simulare input utente.
- **@BeforeEach/@AfterEach**:  
  - Ogni test isola e ripristina gli stream di sistema per evitare side-effect tra test.

---

### 2. **Uso avanzato di Mockito**

- **MockedStatic**:  
  Vengono mockati anche metodi statici (es. `DAOFactory.getInstance()`, `CLIUtils.clearScreen()`, ecc.) tramite `mockStatic`, funzione avanzata di Mockito (>=3.4).
  - Permette di controllare anche utility di sistema o singleton che normalmente sarebbero “hardwired” nella logica della CLI.
- **when/verify**:  
  - Si simulano comportamenti di metodi dei service/handler (es. ritorni di statistiche, lanci di eccezioni, comportamenti di login).
  - Si verifica che certi metodi siano stati chiamati (o meno) tramite `verify`.

---

### 3. **Test principali e logica coperta**

#### a. **testDisplayWelcomeWithSuccessfulStatistics**

- **Scopo**:  
  Testare che la CLI visualizzi correttamente il messaggio di benvenuto e le statistiche quando tutto va a buon fine.
- **Mock**:  
  - `DAOFactory.getInstance()` restituisce la DAO factory mockata.
  - `CLIUtils.clearScreen()`, `printHeader()`, `printSuccess()`, `waitForKeyPress()` sono mockati per non produrre effetti collaterali.
  - I servizi statistiche (`getTodoStatistics()`, `getProjectStatistics()`) restituiscono oggetti mock con valori predefiniti.
- **Chiamata privata via reflection**:  
  - Il metodo `displayWelcome()` è privato: viene invocato tramite reflection.
- **Verifica**:  
  - Si controlla che l’output contenga le stringhe chiave ("Welcome to the Todo Management System", "Current System Status").

---

#### b. **testDisplayWelcomeWithDatabaseException**

- **Scopo**:  
  Verificare che la CLI gestisca correttamente una `DatabaseException` mostrando comunque una schermata informativa.
- **Mock**:  
  - Il servizio `getTodoStatistics()` lancia una `DatabaseException`.
- **Verifica**:  
  - L’output deve contenere almeno il messaggio "System ready for use", a segnalare che il sistema resta utilizzabile (robustezza all’errore).

---

#### c. **testHandleAuthenticationFlowWithSuccessfulLogin**

- **Scopo**:  
  Testare il flusso di autenticazione quando l’utente NON è loggato e il login va a buon fine.
- **Mock**:  
  - `mockAuthService.isLoggedIn()` restituisce `false`.
  - `mockAuthHandler.handleLogin()` restituisce `true`.
- **Chiamata privata via reflection**:  
  - Il metodo privato `handleAuthenticationFlow()` viene invocato tramite reflection.
- **Verifica**:  
  - Il risultato deve essere `true`.
  - Il metodo `handleLogin()` deve essere stato chiamato una volta.

---

#### d. **testHandleAuthenticationFlowWhenAlreadyLoggedIn**

- **Scopo**:  
  Verificare che la CLI non chiami il flusso di login se l’utente è già autenticato.
- **Mock**:  
  - `mockAuthService.isLoggedIn()` restituisce `true`.
- **Verifica**:  
  - Il risultato deve essere `true`.
  - Il metodo `handleLogin()` NON deve essere chiamato (`times(0)`).

---

#### e. **testHandleMainMenuChoiceWithTodosOption**

- **Scopo**:  
  Simulare la selezione dell’opzione "1" (gestione TODO) dal menu principale.
- **Mock**:  
  - `CLIUtils.getInput()` su scanner mockato restituisce "1".
- **Simulazione input**:  
  - `System.in` viene impostato con un input di "1\n" (come se l’utente avesse premuto "1").
- **Chiamata privata via reflection**:  
  - Il metodo privato `handleMainMenuChoice()` viene invocato tramite reflection.
- **Verifica**:  
  - Il metodo `mainMenu.handleTodoMenu()` deve essere chiamato una volta.

---

### 4. **Helper per creazione CLI mockata**

- **createCLI()**:  
  - Usa reflection per istanziare una `TodoManagementCLI` e iniettare direttamente tutti i mock nei campi privati, aggirando costruttori reali e dipendenze effettive.
  - Questo permette di testare la CLI in isolamento completo.

---

## Parti complesse e best practice

### **Mocking Static Methods**

- Mockito permette (da versione >= 3.4) di mockare metodi statici tramite blocchi `try (MockedStatic<...> ...)`.
- È fondamentale per testare codice legacy o utility che altrimenti sarebbero impossibili da isolare nei test.

### **Testing di metodi privati (Reflection)**

- I metodi chiave della CLI sono privati. Si usano reflection API (`getDeclaredMethod`, `setAccessible(true)`, `invoke`) per chiamarli direttamente.
- Questo è utile quando non si vuole (o non si può) cambiare la visibilità dei metodi per il solo scopo dei test.
- **Nota**: È una pratica avanzata, da usare con attenzione (preferire test pubblici quando possibile).

### **Gestione output/input stream di sistema**

- Per testare CLI, è necessario redirezionare `System.out`, `System.err` e spesso anche `System.in`.
- Il test ripristina sempre gli stream originali dopo ogni test, per evitare side effect (best practice fondamentale).

### **Verifica dettagliata delle chiamate mock**

- Si controlla non solo che i metodi siano chiamati, ma anche quante volte (`times(n)`), o che non siano proprio chiamati (`times(0)`).
- Questo garantisce che la logica di branching del codice sia effettivamente coperta e funzionante.

### **Isolamento totale tramite reflection**

- La CLI viene istanziata e configurata via reflection, aggirando costruttori che altrimenti richiederebbero oggetti reali o setup complesso.
- Questo permette di testare anche codice legacy/non testabile senza refactoring invasivi.

---

## Domande da colloquio e risposte

**Q: Perché testare i metodi privati via reflection?**  
**A:**  
Quando la logica pubblica non è facilmente isolabile o per evitare di modificare la visibilità del codice di produzione solo per i test, si può usare reflection per accedere a metodi privati e testarli direttamente.

---

**Q: Quali rischi ci sono nel mockare i metodi statici?**  
**A:**  
Se non si ripristina correttamente il mock statico, si rischiano side-effect su altri test o comportamenti inattesi nell’ambiente di test. Per questo si usano blocchi try-with-resources.

---

**Q: Perché serve redirezionare System.out/System.err nei test CLI?**  
**A:**  
Per poter catturare e verificare l’output prodotto dal programma, senza dover cambiare il codice di produzione.

---

**Q: Come si garantisce l’isolamento e la ripetibilità dei test?**  
**A:**  
Ripristinando sempre gli stream originali dopo ogni test e usando solo dipendenze mockate.

---

**Q: Quando conviene mockare i DAO o le factory?**  
**A:**  
Quando si vuole testare solo la logica di business o la presentazione, senza dipendere da database reali o configurazioni esterne.

---

## Conclusioni

`TodoManagementCLITest` rappresenta un esempio avanzato di test unitari per CLI Java, sfruttando a fondo le potenzialità di Mockito e JUnit 5 per isolare e verificare ogni aspetto della logica di interazione utente-servizio, anche nei casi più complessi e difficili da testare.