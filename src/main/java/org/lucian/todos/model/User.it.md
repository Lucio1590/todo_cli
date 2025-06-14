## Scopo della classe

La classe `User` rappresenta un utente del sistema di gestione dei task.  
Contiene le informazioni di autenticazione (username, email, password hash) e profilo (nome, cognome), lo stato (attivo/disattivo), oltre ai timestamp di creazione, aggiornamento e ultimo login.

---

## Attributi principali

- **Long id:** identificatore univoco utente (PK DB).
- **String username:** nome utente unico (obbligatorio, validato).
- **String email:** email utente unica (obbligatoria, validata).
- **String passwordHash:** hash della password (mai la password in chiaro!).
- **String firstName, lastName:** nome e cognome utente.
- **boolean active:** stato utente (true=attivo, false=disattivato).
- **LocalDateTime createdAt:** timestamp di creazione (final).
- **LocalDateTime updatedAt:** timestamp di ultimo aggiornamento.
- **LocalDateTime lastLoginAt:** timestamp dell’ultimo login.

---

## Costruttori

- **User():**  
  Costruttore vuoto, inizializza timestamps e stato attivo.
- **User(LocalDateTime createdAt):**  
  Usato per mappatura dal DB, consente di impostare il timestamp di creazione.
- **User(String username, String email):**  
  Costruttore chiave, validazione su campi obbligatori e formato email.
- **User(String username, String email, String firstName, String lastName):**  
  Costruttore completo, accetta anche nome e cognome.

---

## Validazione

- **Username:**  
  Non può essere null o vuoto, validato in costruttori e setter.
- **Email:**  
  Non può essere null o vuoto, validato che contenga “@” e “.” (sia in costruttori che in setter).
- **Setter:**  
  Tutti i setter aggiornano `updatedAt` a ogni modifica.

---

## Metodi principali

### Getter e Setter

- Per tutti i campi, con validazione e aggiornamento automatico di `updatedAt`.

### Profilo e visualizzazione

- **getFullName():**  
  Restituisce “nome cognome”, oppure solo uno dei due, oppure username se non presenti.
- **getDisplayName():**  
  Restituisce il nome completo (se disponibile) seguito da username tra parentesi, oppure solo username.

### Timestamp

- **getCreatedAt(), getUpdatedAt(), getLastLoginAt():**  
  Restituiscono i timestamp chiave.
- **setUpdatedAt(), setLastLoginAt():**  
  Permettono aggiornamento esplicito dei timestamp.

---

### Override di equals, hashCode, toString

- **equals/hashCode:**  
  Basati su id, username ed email → due utenti con stessi valori sono considerati uguali.
- **toString():**  
  Restituisce rappresentazione comprensiva dei dati chiave (id, username, email, stato, timestamps).

---

## Best practice e “tricky parts”

- **Validazione centralizzata:**  
  I campi chiave sono validati sia in fase di creazione che di modifica.
- **Nessuna password in chiaro:**  
  Solo l’hash della password viene memorizzato nell’oggetto.
- **Aggiornamento timestamp automatico:**  
  Ogni modifica aggiorna `updatedAt` per tracciabilità.
- **Immutabilità di createdAt:**  
  Il campo di creazione è final e impostato solo nel costruttore.
- **Gestione dello stato attivo:**  
  Il campo booleano `active` permette di gestire soft-delete/disattivazione utenti.

---

## Domande da colloquio e risposte

**Q: Come viene garantita la sicurezza delle informazioni sensibili?**  
**A:**  
Non viene mai memorizzata la password in chiaro; viene sempre usato un hash. La validazione e la gestione dell’autenticazione avvengono in livelli superiori.

---

**Q: Come viene gestito lo stato attivo/inattivo di un utente?**  
**A:**  
Tramite il campo booleano `active`, che permette di disattivare un utente senza cancellare i suoi dati (soft-delete).

---

**Q: Come si aggiorna il timestamp di ultimo accesso?**  
**A:**  
Con il metodo `setLastLoginAt(LocalDateTime)`, che aggiorna anche `updatedAt`.

---

**Q: Come si garantisce la validità di username ed email?**  
**A:**  
Tramite controlli nei costruttori e nei setter; il sistema lancia IllegalArgumentException in caso di valori non validi.

---

**Q: Come si può estendere la classe User per supportare ruoli o permessi?**  
**A:**  
Si possono aggiungere nuovi campi (es. Set<Role> o Enum role) e relativi metodi, mantenendo la compatibilità col modello esistente.

---

## Conclusioni

La classe `User` è progettata per essere sicura, robusta e facilmente estendibile, con attenzione alla validazione, tracciabilità, e separazione tra dati sensibili e dati di profilo.  
Costituisce la base per la gestione degli utenti nel sistema.