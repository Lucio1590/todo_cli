## Scopo della classe

`AuthenticationService` gestisce tutte le operazioni di **autenticazione** e **gestione delle sessioni utente** per il sistema Todo.  
Si occupa di:
- Registrazione utente sicura
- Login/logout
- Cambio password
- Aggiornamento profilo
- Gestione della sessione (utente corrente e timestamp)
- Hashing e verifica sicura delle password

---

## Attributi principali

- **UserDAO userDAO**  
  Data Access Object per utenti, usato per tutte le operazioni di persistenza.
- **User currentUser**  
  Utente attualmente loggato (null se nessuno).
- **LocalDateTime sessionStartTime**  
  Timestamp di inizio della sessione corrente.
- **Costanti sicurezza**  
  - HASH_ALGORITHM: `"SHA-256"`  
  - SALT_LENGTH: `32` byte (sale crittografico)

---

## Flusso di autenticazione e sicurezza

### 1. **Registrazione (register)**

- Valida input (username, email, password, lunghezza password)
- Controlla unicità di username ed email
- Esegue hash sicuro della password (`hashPassword`)
- Crea e salva l’oggetto `User`
- Restituisce l’utente creato

### 2. **Login (login)**

- Valida input (username, password)
- Recupera utente da DB tramite username
- Verifica che l’account sia attivo
- Verifica la password tramite metodo `verifyPassword`:
  - Decodifica la stringa hashata (Base64)
  - Estrae il sale dai primi 32 byte
  - Ricalcola l’hash della password fornita usando lo stesso sale
  - Confronta byte a byte la parte hashata
- Aggiorna lastLoginAt sia su oggetto che su DB
- Imposta utente corrente e timestamp di sessione

### 3. **Logout (logout)**

- Semplicemente azzera utente corrente e timestamp sessione

### 4. **Cambio password (changePassword)**

- Verifica che ci sia un utente loggato
- Controlla validità dei parametri e che nuova password sia diversa dalla vecchia
- Verifica la password attuale
- Calcola nuovo hash e aggiorna l’utente su DB

### 5. **Aggiornamento profilo (updateProfile)**

- Verifica che ci sia un utente loggato
- Se l’email cambia, controlla unicità
- Aggiorna i dati su oggetto e DB

### 6. **Gestione sessione**

- **isLoggedIn():**  
  True se c’è un utente corrente
- **getCurrentUser():**  
  Restituisce l’utente loggato
- **getSessionStartTime():**  
  Restituisce l’istante di login

---

## Sicurezza: **Hashing e verifica password**

### Hashing (`hashPassword`)

- Genera un sale casuale di 32 byte tramite `SecureRandom`
- Usa SHA-256 per calcolare l’hash della password concatenata al sale
- Salva in una singola stringa Base64: `[sale][hash(password+salt)]`
- Inalterabile: il sale è unico per ogni password/utente, quindi anche password uguali avranno hash diversi

### Verifica (`verifyPassword`)

- Controlla che l’hash sia valido e non vuoto
- Decodifica la stringa Base64
- Estrae il sale (primi 32 byte)
- Ricalcola l’hash della password fornita con lo stesso sale
- Confronta il risultato byte a byte con quello memorizzato
- Usa `MessageDigest.isEqual` per evitare timing attacks

#### **Nota importante**
Questa implementazione è buona per sicurezza, ma in produzioni reali sarebbe preferibile usare algoritmi come bcrypt, PBKDF2, scrypt, argon2, ecc.

---

## Parti complesse e “tricky”

### **Gestione sicura della password**
- L’algoritmo usa un sale casuale generato per ogni password.
- L’hash e il sale sono concatenati e codificati in Base64.
- In fase di verifica, la password fornita viene hashata con lo stesso sale estratto dall’hash memorizzato.
- Il confronto avviene a basso livello (byte array) per evitare attacchi timing.

### **Non rivelare informazioni sull’esistenza dell’utente**
- In caso di login fallito, il messaggio di errore non distingue tra username inesistente e password errata, per evitare enumeration degli account.

### **Gestione dello stato e della sessione**
- L’utente loggato viene mantenuto in memoria (variabile currentUser) e il timestamp di login salvato.
- Tutte le azioni che richiedono autenticazione verificano lo stato di login e sollevano eccezione se non rispettato.

### **Gestione delle eccezioni**
- Tutte le operazioni di persistenza sono protette da catch su `DatabaseException` e rilanciate come `AuthenticationException` con messaggi user-friendly.
- Log dettagliato sia su errori che su operazioni sensibili.

### **Controllo validità hash**
- Prima di decodificare l’hash, viene verificato che sia una stringa Base64 valida (metodo `isValidBase64`).
- Viene controllata anche la lunghezza minima del dato decodificato.

---

## Domande da colloquio e risposte

**Q: Perché usare un sale casuale per ogni password?**  
**A:**  
Per evitare che due utenti con la stessa password abbiano lo stesso hash, e per proteggersi dagli attacchi con rainbow tables.

---

**Q: Perché non si usa direttamente SHA-256 senza sale?**  
**A:**  
Un hash statico è vulnerabile a rainbow tables e preimage attacks; l’uso del sale rende ogni hash unico anche a parità di password.

---

**Q: Come si gestisce il cambio email?**  
**A:**  
Se l’utente cambia email viene controllata l’unicità; l’aggiornamento viene eseguito solo se la nuova email non è già associata a un altro account.

---

**Q: Come si previene la rivelazione di informazioni sugli utenti in fase di login?**  
**A:**  
Indipendentemente dal fatto che sia username o password errati, il messaggio di errore generico “Invalid username or password” viene usato per evitare enumeration.

---

**Q: Quali sono i limiti di questa implementazione in produzione reale?**  
**A:**  
SHA-256 + sale è buono per progetti didattici/CLI, ma in sistemi di produzione occorrerebbe usare algoritmi specifici per password (bcrypt, PBKDF2, argon2...) e gestire anche lockout per tentativi multipli.

---

## Esempio d’uso

```java
AuthenticationService authService = new AuthenticationService(userDAO);

// Registrazione
authService.register("lucio", "lucio@email.com", "mypassword", "Lucio", "Diaconu");

// Login
User user = authService.login("lucio", "mypassword");

// Cambio password
authService.changePassword("mypassword", "newsecurepassword");

// Logout
authService.logout();
```

---

## Conclusioni

La classe `AuthenticationService` è progettata per gestire in modo sicuro, robusto e flessibile l’autenticazione utente in un sistema CLI, con attenzione sia alla sicurezza informatica che all’esperienza utente, e può essere facilmente estesa o integrata in architetture più ampie.