# LipariBank Day 07 — Testing: JUnit 5, Mockito, @DataJpaTest

Benvenuto nel progetto **liparibank-day07-broken**.

Applicazione Spring Boot 3.3.4 (Java 21) con una suite di test completa:
`AccountControllerTest` (web layer), `TransferServiceTest` (unit test con Mockito),
`AccountRepositoryTest` (@DataJpaTest con Liquibase).

Il progetto contiene **3 bug intenzionali** da trovare e correggere.

---

## Prerequisiti

- Java 21
- Maven (o usa il wrapper incluso)

---

## Come avviare i test

```bash
./mvnw test
```

Su Windows:

```cmd
mvnw.cmd test
```

---

## Struttura dei test

```
src/test/java/com/lipari/bank/
├── account/
│   ├── AccountControllerTest.java   ← @WebMvcTest
│   └── AccountRepositoryTest.java   ← @DataJpaTest
└── transfer/
    └── TransferServiceTest.java     ← @ExtendWith(MockitoExtension.class)
```

---

## Le 3 Missioni

### Missione 1 — Il contesto Spring non si carica in AccountControllerTest

L'esecuzione di `AccountControllerTest` fallisce in fase di avvio del contesto.
L'errore riporta che non è stato trovato un bean richiesto per l'inizializzazione
di un'altra configurazione importata nel test.

**Sintomo:** `ApplicationContext failure` prima ancora che un singolo test venga
eseguito. Tutti i metodi di test risultano in errore con la stessa root cause.

---

### Missione 2 — TransferServiceTest fallisce con UnnecessaryStubbingException

`TransferServiceTest` fallisce su uno specifico test con:

```
org.mockito.exceptions.misusing.UnnecessaryStubbingException:
Unnecessary stubbings detected.
```

Il test configura uno stub che non viene mai invocato durante l'esecuzione del
metodo sotto test.

**Sintomo:** un singolo test method lancia `UnnecessaryStubbingException`.
Gli altri test della stessa classe passano correttamente.

---

### Missione 3 — AccountRepositoryTest fallisce all'avvio del contesto

`AccountRepositoryTest` (annotata con `@DataJpaTest`) fallisce durante
l'inizializzazione del contesto. Il log mostra un errore SQL durante l'esecuzione
delle migrazioni Liquibase sull'H2 in-memory usato dai test.

**Sintomo:** il contesto non si carica, con errore `LiquibaseException` o
`JdbcSQLSyntaxErrorException`. Nessun test della classe viene eseguito.

---

## Come testare (dopo aver risolto le missioni)

```bash
# Esegui tutti i test
./mvnw test

# Esegui solo i test di una classe specifica
./mvnw test -Dtest=AccountControllerTest
./mvnw test -Dtest=TransferServiceTest
./mvnw test -Dtest=AccountRepositoryTest
```

---

## Struttura del progetto

```
src/main/java/com/lipari/bank/
├── LipariBankApplication.java
├── account/
│   ├── model/Account.java
│   ├── dto/AccountRequest.java
│   ├── AccountRepository.java
│   ├── AccountService.java
│   └── AccountController.java
├── customer/
│   └── model/Customer.java
├── transfer/
│   ├── dto/TransferRequest.java
│   └── TransferService.java
├── auth/
│   ├── model/BankUser.java
│   ├── model/Role.java
│   ├── BankUserRepository.java
│   ├── RoleRepository.java
│   ├── LipariBankUserDetailsService.java
│   ├── AuthService.java
│   ├── AuthController.java
│   └── dto/
│       ├── AuthRequest.java
│       ├── RegisterRequest.java
│       ├── RefreshRequest.java
│       └── AuthResponse.java
└── shared/
    ├── config/
    │   ├── SecurityConfig.java
    │   ├── OpenApiConfig.java
    │   └── DataInitializer.java
    ├── exception/
    │   ├── ResourceNotFoundException.java
    │   ├── BusinessException.java
    │   └── GlobalExceptionHandler.java
    └── security/
        ├── JwtService.java
        ├── JwtAuthenticationFilter.java
        ├── LipariBankAccessDeniedHandler.java
        └── LipariBankAuthenticationEntryPoint.java
src/main/resources/
├── application.yml
└── db/changelog/
    └── db.changelog-master.yaml
```

---

## Bonus Mission — Feature da Implementare (opzionale, ~1 ora)

Una volta risolti i 3 bug, implementa la seguente feature per consolidare i concetti del giorno.

### Classe di test AccountServiceTest

Il progetto ha già test per il controller (web layer) e per il repository (JPA layer), ma manca una classe di test dedicata al layer di servizio in isolamento.

**Cosa implementare:**

Crea una nuova classe di test `AccountServiceTest` nella cartella `src/test/java/com/lipari/bank/account/`. La classe deve testare `AccountService` in completo isolamento, senza Spring context, senza database e senza HTTP — solo il servizio e le sue dipendenze simulate.

Scrivi i seguenti casi di test:

1. **findById — account trovato**: verifica che il metodo restituisca l'oggetto corretto quando il repository trova l'account per l'ID dato.
2. **findById — account non trovato**: verifica che il metodo lanci una `ResourceNotFoundException` quando il repository non trova nessun account per l'ID dato.
3. **save — inserimento corretto**: verifica che, dato un `AccountRequest` valido, il metodo chiami il repository esattamente una volta con i dati corretti e restituisca la risposta attesa.

**Criteri di accettazione:**

- La classe usa il runner di Mockito corretto per i test senza contesto Spring.
- Le dipendenze di `AccountService` sono simulate con mock.
- I mock sono configurati solo per quello che ogni singolo test usa: nessuno stub ridondante.
- I 3 test passano tutti con `./mvnw test -Dtest=AccountServiceTest`.
- I test esistenti non vengono modificati e continuano a passare.

---

*LipariBank Prompt Bootcamp — Testing: JUnit 5, Mockito, @DataJpaTest — Day 07*
