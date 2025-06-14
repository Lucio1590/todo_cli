## 1. Informazioni di base

- **groupId**: `org.lucian`
- **artifactId**: `todo_cli`
- **version**: `1.0-SNAPSHOT`
- **name**: `Todos Management System`
- **description**: Java SE Todo Management System for managing tasks and projects

---

## 2. Proprietà di compilazione

```xml
<maven.compiler.source>23</maven.compiler.source>
<maven.compiler.release>23</maven.compiler.release>
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
```

- **Java 23**:  
  Il progetto usa la versione 23 di Java sia per la compilazione che per la release target.  
  _Nota_: Java 23 è molto recente e richiede un JDK aggiornato.  
- **UTF-8**:  
  Impostazione standard per la codifica dei file sorgente.

---

## 3. Gestione delle dipendenze

### **Testing**

- **JUnit 5** (`org.junit.jupiter:junit-jupiter`):  
  Per unit test moderni con annotazioni e funzionalità Jupiter.
- **Mockito** (`mockito-core` e `mockito-junit-jupiter`):  
  Per mocking e test isolation, con integrazione diretta nel framework JUnit 5.

### **Logging**

- **SLF4J API**:  
  API di logging standard per Java.
- **Logback Classic**:  
  Implementazione runtime di SLF4J, molto usata e configurabile (tipicamente con file logback.xml).

### **Database**

- **SQLite JDBC**:  
  Driver JDBC per SQLite, pronto per supportare una persistenza su file locale (anche se potrebbe non essere ancora usato nella versione attuale).

---

## 4. Plugin di build

### **Compilazione**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <release>23</release>
    </configuration>
</plugin>
```
- Compila il codice usando il target `release 23` (Java 23).  
  _Nota_: la presenza di `<maven.compiler.source>` e `<release>` è ridondante ma non problematica.

### **Test**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.2</version>
    <configuration>
        <argLine>-Dnet.bytebuddy.experimental=true</argLine>
    </configuration>
</plugin>
```
- **Surefire**: esegue i test unitari.
- L’argomento `-Dnet.bytebuddy.experimental=true` serve a garantire compatibilità con alcune feature avanzate di Mockito (soprattutto mocking statico e su Java recenti).

### **Esecuzione applicazione**

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.1.1</version>
    <configuration>
        <mainClass>org.lucian.todos.Main</mainClass>
    </configuration>
</plugin>
```
- Permette di eseguire direttamente l’applicazione CLI con  
  `mvn exec:java`  
  lanciando la classe `org.lucian.todos.Main`.

---

## 5. Dipendenze e versionamento

Tutte le versioni sono gestite tramite property in `<properties>` per semplificare upgrade centralizzati e ridurre rischi di incompatibilità.

---

## 6. Parti complesse e best practice

- **Java 23**:  
  Adottare una versione così recente consente di sfruttare tutte le nuove feature, ma potrebbe introdurre problemi di compatibilità con ambienti legacy.
- **Mocking avanzato in test**:  
  L’argLine per Surefire è fondamentale per abilitare il mocking statico (usato nei test CLI), che richiede ByteBuddy in modalità sperimentale.
- **Separazione netta tra dipendenze di runtime e di test**:  
  Tutto ciò che serve solo ai test è marcato `<scope>test</scope>`, riducendo il peso del classpath in produzione.
- **Configurazione pronta per logging strutturato**:  
  L’accoppiata SLF4J + Logback consente logging robusto, configurabile e facilmente integrabile con sistemi di monitoraggio.
- **Supporto a SQLite out-of-the-box**:  
  Il driver JDBC è già incluso, quindi il progetto può facilmente passare a una persistenza su file locale o embedded DB senza cambiare la build.

---

## Domande da colloquio e risposte

**Q: Perché usare proprietà per versioni delle dipendenze?**  
**A:**  
Per rendere facile e sicuro l’aggiornamento di una versione senza dover cercare e modificare più punti nel POM.

---

**Q: Perché usare Surefire con argLine custom?**  
**A:**  
Per abilitare funzionalità di mocking avanzato su JVM moderne, necessarie per testare efficacemente codice che usa metodi statici o final.

---

**Q: Come si esegue l’applicazione CLI da terminale?**  
**A:**  
Con il comando `mvn exec:java`, grazie al plugin `exec-maven-plugin` che lancia la classe principale.

---

**Q: Cosa succede se si tenta di buildare su una JVM < 23?**  
**A:**  
Il build fallirà, perché la compilazione è forzata su Java 23.

---

## Conclusioni

Il `pom.xml` è moderno, robusto e pronto per sviluppo, test e deploy di un’applicazione Java SE CLI avanzata, con attenzione a testabilità, logging, modularità e upgrade futuro.