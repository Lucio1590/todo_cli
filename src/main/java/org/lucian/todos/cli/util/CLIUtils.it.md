# Spiegazione dettagliata dei metodi della classe `CLIUtils`

Questa guida spiega **ogni metodo** della classe `org.lucian.todos.cli.util.CLIUtils` (dal file [`CLIUtils.java`](https://github.com/Lucio1590/todo_cli/blob/main/src/main/java/org/lucian/todos/cli/util/CLIUtils.java)), incluse le logiche più particolari.  
La classe offre utility per la gestione di input/output e formattazione nella CLI di un'applicazione todo-list.

---

## Costanti ANSI (Colori Terminale)

- `RESET`, `BOLD`, `RED`, `GREEN`, `YELLOW`, `BLUE`, `MAGENTA`, `CYAN`  
  **Cosa fanno:** Stringhe di escape ANSI per colorare o formattare il testo del terminale.  
  **Utilità:** Permettono di evidenziare messaggi (successo, errore, info...).

---

## Formatter per le date

- `DATE_FORMATTER`, `DISPLAY_DATE_FORMATTER`, `DISPLAY_DATETIME_FORMATTER`  
  **Cosa fanno:** Oggetti `DateTimeFormatter` per convertire date in stringa e viceversa.  
  **Tricky:** Il formato scelto (`yyyy-MM-dd` per input, `MMM dd, yyyy` o `MMM dd, yyyy HH:mm` per output) permette sia parsing facile che una visualizzazione user-friendly.

---

## Metodo: `clearScreen()`

**Descrizione:**  
Pulisce la console simulando il comando "clear" (Linux/macOS) o "cls" (Windows).  
**Dettaglio tricky:**  
- Su Windows usa un `ProcessBuilder` per lanciare `cmd /c cls`.
- Su Unix invia escape ANSI `\033[2J\033[H` (cancella + porta il cursore in alto).
- Se fallisce (es. ambiente non interattivo), stampa 50 righe vuote per “spingere via” il contenuto precedente.

---

## Metodo: `printHeader(String title)`

**Descrizione:**  
Stampa un’intestazione grande e centrata, su tre righe:  
1. Riga di `=` colorata (lunghezza almeno 50 caratteri o titolo + 10)
2. Titolo centrato e in grassetto
3. Riga di `=`  
**Tricky:**  
- Usa la funzione `centerText` per centrare il testo.
- Usa i codici colore per rendere l'header ben visibile.

---

## Metodo: `printSectionHeader(String title)`

**Descrizione:**  
Stampa il titolo di una sezione in blu/grassetto seguito da una linea di `-`.

---

## Metodi: `printSuccess`, `printError`, `printWarning`, `printInfo`

**Descrizione:**  
Stampano messaggi con icone e colori diversi per:
- Successo (verde, ✓)
- Errore (rosso, ✗)
- Avviso (giallo, ⚠)
- Informazione (blu, ℹ)

---

## Metodo: `getInput(Scanner scanner, String prompt)`

**Descrizione:**  
Mostra il prompt, attende e restituisce ciò che l’utente digita (anche stringa vuota).

---

## Metodo: `getInputWithDefault(Scanner scanner, String prompt, String defaultValue)`

**Descrizione:**  
Chiede un input e mostra il valore di default tra parentesi quadre.  
Se l’utente inserisce solo spazi/vuoto, restituisce il default.

---

## Metodo: `getIntInput(Scanner scanner, String prompt, int min, int max)`

**Descrizione:**  
Chiede un numero intero tra min e max, ripete la richiesta finché l’utente non inserisce un valore valido.  
**Tricky:**  
- Usa un ciclo infinito con try/catch per evitare crash su input non numerico.
- Se input fuori range, stampa errore custom.

---

## Metodo: `getDateInput(Scanner scanner, String prompt, boolean allowEmpty)`

**Descrizione:**  
Chiede una data in formato `yyyy-MM-dd`.  
- Se `allowEmpty` è true, accetta input vuoto (restituisce `null`).
- Se l’input non rispetta il formato, stampa errore e ripete.

---

## Metodo: `getPriorityInput(Scanner scanner, String prompt)`

**Descrizione:**  
Mostra tutte le priorità disponibili (enum `Priority`) numerate, chiede all’utente di selezionarne una tramite numero, e la restituisce.  
**Tricky:**  
- Usa `getIntInput` per evitare errori di input.

---

## Metodo: `getTodoStatusInput(Scanner scanner, String prompt)`

**Descrizione:**  
Simile a `getPriorityInput`, ma per tutti gli stati di un todo (`TodoStatus`).  
Mostra anche il display name dello stato.

---

## Metodo: `waitForKeyPress(Scanner scanner)`

**Descrizione:**  
Stampa "Press Enter to continue..." e attende che l’utente prema invio.

---

## Metodo privato: `centerText(String text, int width)`

**Descrizione:**  
Restituisce il testo centrato in una stringa di lunghezza `width`.  
**Tricky:**  
- Calcola spazi a sinistra e destra, se il testo è più lungo della larghezza, lo restituisce così com’è.

---

## Metodo: `formatTodo(Todo todo)`

**Descrizione:**  
Restituisce una stringa multi-linea che rappresenta il todo in modo colorato e compatto.  
**Dettaglio tricky:**  
- Colora lo stato (`getStatusColor`) e la priorità (`getPriorityColor`).
- Se la data di scadenza è passata e il todo non è completo, evidenzia “OVERDUE!” in rosso.
- Taglia la descrizione se supera 50 caratteri, aggiungendo "...".
- Mostra eventuale ID progetto associato.

---

## Metodo: `formatProject(Project project)`

**Descrizione:**  
Restituisce una rappresentazione compatta del progetto:
- ID, nome, date di inizio/fine, numero di todos, una breve descrizione.
- Taglia la descrizione dopo 60 caratteri.

---

## Metodo privato: `getStatusColor(TodoStatus status)`

**Descrizione:**  
Restituisce il codice colore ANSI per ogni stato:
- TODO: giallo
- IN_PROGRESS: blu
- COMPLETED: verde
- CANCELLED: rosso

---

## Metodo privato: `getPriorityColor(Priority priority)`

**Descrizione:**  
Restituisce il codice colore ANSI per ogni priorità:
- URGENT: rosso/grassetto
- HIGH: rosso
- MEDIUM: giallo
- LOW: verde

---

## Metodo: `displayPaginatedList(List<T> items, int itemsPerPage, Scanner scanner, Function<T, String> formatter)`

**Descrizione:**  
Permette di visualizzare una lista di oggetti suddivisa in pagine.  
**Funzionamento tricky:**  
- Calcola quante pagine servono (divisione arrotondata in alto).
- Ciclo principale:
  1. Pulisce lo schermo.
  2. Stampa header con pagina attuale.
  3. Mostra solo gli oggetti della pagina corrente, numerati.
  4. Mostra comandi: `[n]ext`, `[p]revious`, `[q]uit`.
  5. Attende il comando dell’utente. Naviga tra le pagine, oppure esce.
  6. Se c’è solo una pagina, termina dopo la pressione di Enter.
- Usa una funzione formatter per stampare ogni elemento (può essere `formatTodo`, `formatProject`, ecc).

---

## Metodo: `formatDate(LocalDate date)`

**Descrizione:**  
Restituisce la data in formato leggibile (`MMM dd, yyyy`) o "Not set" se nulla.

---

## Metodo: `formatDateTime(LocalDateTime dateTime)`

**Descrizione:**  
Restituisce la data e ora in formato leggibile (`MMM dd, yyyy HH:mm`) o "Not set" se nulla.

---

## Utilità generale della classe

- Tutti i metodi sono statici: non serve istanziare la classe.
- Gestisce input robusto (mai crash su input utente scorretto).
- Fornisce output sempre leggibile e colorato.
- Si integra bene con la logica di una CLI moderna.

---

**Hai bisogno di esempi d’uso pratici per uno o più metodi?**