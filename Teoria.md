Manuale Teorico di Programmazione Orientata agli Oggetti (OOP)

Questo manuale fornisce le basi teoriche necessarie per superare l'esame finale del corso di OOP, seguendo i requisiti specificati nel documento di progetto.

1. Principi Fondamentali di OOP

1.1 Incapsulamento (Encapsulation)

L'incapsulamento è il meccanismo che lega insieme dati e metodi che agiscono su di essi, proteggendo l'integrità dello stato interno di un oggetto. In Java:

Gli attributi di classe sono generalmente dichiarati come private.

L'accesso e la modifica avvengono tramite metodi pubblici (get e set).

Vantaggi:

Nasconde i dettagli implementativi (information hiding).

Incrementa la manutenibilità, impedendo modifiche incontrollate.

Migliora la sicurezza del codice, isolando il dato.

1.2 Ereditarietà (Inheritance)

L'ereditarietà consente a una classe (sottoclasse) di riutilizzare attributi e metodi di un'altra classe (superclasse).

In Java si utilizza la keyword extends.

Le sottoclassi possono estendere o sovrascrivere (override) i metodi ereditati.

Vantaggi:

Promuove il riuso di codice.

Favorisce l'organizzazione gerarchica dei concetti.

Riduce la duplicazione di logica comune.

1.3 Polimorfismo (Polymorphism)

Il polimorfismo è la capacità di un oggetto di assumere molte forme:

Override: una sottoclasse fornisce una specifica implementazione di un metodo ereditato.

Overloading: più metodi con lo stesso nome ma firme diverse all'interno della stessa classe.

Interfacce: un'interfaccia può essere implementata da più classi.

Vantaggi:

Permette codice disaccoppiato e flessibile.

Facilita l'estensione del sistema senza modifiche ai client.

1.4 Astrazione (Abstraction)

L'astrazione consiste nel rappresentare solo le caratteristiche essenziali di un oggetto, nascondendo i dettagli irrilevanti.

In Java si usa abstract class e interface.

Le classi astratte possono definire metodi con implementazione e metodi astratti senza implementazione.

Le interfacce dichiarano solo metodi astratti (nelle versioni precedenti a Java 8) e permettono di definire contratti.

Vantaggi:

Semplifica la complessità del sistema.

Consente di focalizzarsi sul "cosa" piuttosto che sul "come".

Favorisce la progettazione guidata dai contratti (programming to an interface).

2. Design Pattern Obbligatori

2.1 Factory Pattern

Scopo: Creazionale.

Quando usarlo: Se non si conosce il tipo concreto da istanziare o si vuole delegare la creazione.

Implementazione:

Definire un'interfaccia/abstract factory con un metodo di creazione.

Implementare sottoclassi concrete che restituiscono istanze specifiche.

Benefici:

Disaccoppiamento tra client e processi di creazione.

Facilita l'estensibilità del sistema.

2.2 Composite Pattern

Scopo: Strutturale.

Quando usarlo: Per rappresentare strutture ad albero, trattando foglie e composizioni in modo uniforme.

Implementazione:

Component comune per Leaf e Composite.

Composite mantiene una lista di Component figli e implementa metodi di gestione (add, remove, getChild).

Benefici:

Semplifica il client: non fa distinzione tra oggetti semplici e compositi.

Aggiunge flessibilità nella gestione di strutture gerarchiche.

2.3 Iterator Pattern

Scopo: Comportamentale.

Quando usarlo: Quando si desidera accedere sequenzialmente agli elementi di una raccolta senza esporre la sua rappresentazione interna.

Implementazione:

L'aggregato fornisce un Iterator con hasNext() e next().

Il client utilizza soltanto l'iterator per scorrere gli elementi.

Benefici:

Disaccoppiamento tra algoritmo di scorrimento e struttura dati.

Consistenza nell'accesso a diverse collezioni.

2.4 Exception Shielding

Scopo: Sicurezza e robustezza.

Quando usarlo: Sempre, per evitare perdite di informazioni nei livelli superiori.

Implementazione:

Catturare eccezioni di basso livello (SQLException, IOException).

Lanciare eccezioni più generiche e sicure (custom), registrando il dettaglio solo nei log.

Benefici:

Previene l'esposizione di stack trace all'utente.

Migliora la qualità dell'errore mostrato.

3. Tecnologie Core di Java

3.1 Collections Framework

Interfacce principali: List, Set, Map.

Implementazioni comuni: ArrayList, LinkedList, HashSet, TreeSet, HashMap, TreeMap.

Considerazioni di scelta:

ArrayList: accessi rapidi per indice.

LinkedList: inserimenti/cancellazioni frequenti.

HashSet: verifica di unicità efficiente.

3.2 Generics

Permettono di definire classi e metodi con parametri di tipo (es. List<String>).

Vantaggi: type-safety a compile-time, riduzione di cast espliciti.

3.3 Java I/O

Stream di byte: InputStream, OutputStream.

Reader/Writer: lavorano su caratteri e gestiscono codifiche.

Decorator Pattern: BufferedInputStream su FileInputStream, ecc.

Attenzione:

Differenza tra byte e caratteri.

"Wrapping" per estendere funzionalità.

3.4 Logging

Framework: SLF4J, Log4j, java.util.logging.

Livelli: DEBUG, INFO, WARN, ERROR.

Configurazione dinamica per package.

Vantaggi:

Monitoraggio e auditing.

Disattivazione selettiva dei log.

3.5 JUnit Testing

Test unitari: isolare metodi singoli.

Annotazioni: @Test, @BeforeEach, @AfterEach.

Asserzioni: assertEquals(), assertTrue(), ecc.

Importanza:

Garanzia di non regressione.

Documentazione del comportamento atteso.

4. Programmazione Sicura

4.1 Input Sanitization

Validazione e pulizia di dati esterni.

Prevenzione di SQL Injection, XSS, crash.

4.2 No Hardcoded Secrets

Esternalizzare credenziali in file di configurazione o variabili d'ambiente.

Evitare password e chiavi API direttamente nel codice.

4.3 Controlled Exception Propagation

Gestione consapevole delle eccezioni.

Registrazione (logging) e rilancio di tipi generici.

Impedire la fuga di dettagli implementativi.

5. Argomenti Avanzati (Opzionali)

5.1 Design Pattern Aggiuntivi

Abstract Factory: factory di factory per famiglie correlate.

Builder: separa costruzione e rappresentazione di oggetti complessi.

Strategy: incapsula algoritmi intercambiabili.

Observer: notifica uno-a-molti tra oggetti.

Chain of Responsibility: catena di handler per richieste.

Adapter / Bridge / Proxy / Decorator: strutturali per interfacce e estensioni.

Singleton: unica istanza globale (uso con cautela).

Memento: snapshot dello stato per undo/redo.

Template Method: scheletro di algoritmo con passi variabili.

5.2 Tecnologie Avanzate

Multithreading: gestione di più thread per parallelismo.

Stream API e Lambda: elaborazione dichiarativa di collezioni (Java 8+).

Reflection: ispezione e modifica di classi a runtime.

Inversion of Control (IoC): controllo invertito (Spring, Guice).

Custom Annotations: metadati elaborabili a runtime.

Mockito: mocking per isolare unità nei test.

6. Documentazione e Colloquio

6.1 README.md

Panoramica dell'applicazione.

Tecnologie e pattern utilizzati con giustificazione.

Istruzioni di build ed esecuzione.

Diagrammi UML (classi, sequenza).

Limitazioni e possibili sviluppi futuri.

6.2 Giustificazioni e Preparazione al Colloquio

Spiega le scelte architetturali e di pattern.

Preparati a domande su alternative, performance e sicurezza.

Esempi di domande:

"Perché ArrayList anziché LinkedList in questo caso?"

"Quali alternative allo Strategy hai valutato?"

"Come garantisci la sicurezza dei dati?"