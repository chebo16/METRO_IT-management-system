# METRO IT Management System

Sistem web pentru gestionarea echipamentelor IT, incidentelor tehnice și activităților de mentenanță din cadrul unui magazin METRO.

Aplicația a fost realizată ca proiect practic și urmărește un flux simplu de lucru între administratorul sistemului și tehnicienii IT. Prin intermediul aplicației pot fi înregistrate echipamentele, pot fi create și urmărite incidentele, pot fi repartizate sarcini tehnicienilor și poate fi păstrat istoricul lucrărilor de mentenanță.

---

## 1. Funcționalitățile principale

Aplicația este împărțită în funcție de rolul utilizatorului autentificat.

### Administrator

Administratorul poate:

- vizualiza informațiile generale din Dashboard;
- crea utilizatori noi;
- modifica utilizatorii existenți;
- activa sau dezactiva utilizatori;
- gestiona rolurile utilizatorilor;
- adăuga echipamente IT;
- modifica informațiile despre echipamente;
- schimba starea echipamentelor;
- vizualiza și căuta echipamente;
- crea incidente;
- vizualiza toate incidentele;
- atribui un incident unui tehnician;
- schimba tehnicianul responsabil cât timp incidentul permite acest lucru;
- urmări starea incidentelor;
- închide incidentele rezolvate;
- consulta istoricul lucrărilor de mentenanță;
- utiliza filtrele și căutarea disponibile în aplicație.

### Tehnician

Tehnicianul poate:

- vizualiza propriul Dashboard;
- consulta lista echipamentelor;
- căuta echipamente;
- consulta lista generală de incidente;
- consulta separat incidentele care îi sunt atribuite;
- începe lucrul asupra unui incident;
- adăuga înregistrări de mentenanță pentru incidentele aflate în lucru;
- specifica lucrările efectuate;
- indica componentele înlocuite;
- selecta rezultatul lucrării de mentenanță;
- introduce descrierea soluției;
- marca incidentul ca rezolvat;
- consulta istoricul activităților de mentenanță.

---

# 2. Accesarea aplicației

După pornirea serverului Apache Tomcat și publicarea aplicației, aceasta poate fi accesată în browser la adresa:

```text
http://localhost:8080/metro-it/
```

Accesarea adresei principale redirecționează automat utilizatorul către pagina de autentificare dacă nu există deja o sesiune activă.

Pagina de autentificare poate fi accesată și direct:

```text
http://localhost:8080/metro-it/login
```

Numele `metro-it` provine din numele fișierului WAR configurat în `pom.xml`.

---

## 3. Conturi demonstrative

Fișierul:

```text
database/sample_data.sql
```

creează trei conturi demonstrative.

### Administrator

```text
Username: admin
Password: MetroAdmin2026!
Role: ADMIN
```

### Tehnician 1

```text
Username: technician1
Password: MetroTech2026!
Role: TECHNICIAN
```

### Tehnician 2

```text
Username: technician2
Password: MetroTech2026!
Role: TECHNICIAN
```

Aceste conturi sunt destinate demonstrării și testării aplicației.

Parolele nu sunt păstrate în baza de date în format text. În tabelul `users` sunt stocate doar hash-uri BCrypt.

Într-un mediu real de producție, parolele demonstrative trebuie înlocuite cu parole individuale și sigure.

---

# 4. Fluxul de lucru al unui incident

Fluxul principal implementat în aplicație este:

```text
┌─────────────┐
│     NEW     │
└──────┬──────┘
       │
       │ Tehnicianul începe lucrul
       ▼
┌─────────────┐
│ IN_PROGRESS │
└──────┬──────┘
       │
       │ Se efectuează lucrările de mentenanță
       │ și se introduce soluția
       ▼
┌─────────────┐
│  RESOLVED   │
└──────┬──────┘
       │
       │ Administratorul verifică și închide incidentul
       ▼
┌─────────────┐
│   CLOSED    │
└─────────────┘
```

Un incident nou poate fi atribuit unui tehnician de către administrator.

Tehnicianul atribuit poate trece incidentul din starea `NEW` în `IN_PROGRESS`.

Înregistrările de mentenanță pot fi adăugate numai cât timp incidentul se află în starea `IN_PROGRESS`.

După efectuarea lucrărilor și completarea soluției, tehnicianul poate marca incidentul ca `RESOLVED`.

Închiderea definitivă a incidentului este realizată de administrator prin trecerea acestuia în starea `CLOSED`.

După rezolvarea sau închiderea incidentului, atribuirea tehnicianului nu mai poate fi modificată.

---

# 5. Instrumente utilizate

Pentru dezvoltarea, configurarea, testarea și rularea proiectului au fost utilizate următoarele instrumente.

## IntelliJ IDEA

```text
IntelliJ IDEA 2026.2
```

IntelliJ IDEA a fost mediul principal de dezvoltare.

A fost utilizat pentru:

- scrierea codului Java;
- dezvoltarea paginilor JSP;
- editarea fișierelor CSS;
- organizarea structurii Maven;
- compilarea proiectului;
- crearea pachetului WAR;
- configurarea serverului Tomcat;
- executarea și verificarea aplicației.

---

## Java

```text
Java 21
Eclipse Temurin JDK 21.0.11
```

Codul aplicației este scris în Java 21.

Java este utilizat pentru:

- modelele aplicației;
- accesul la baza de date;
- logica de business;
- autentificare;
- autorizare;
- validarea datelor;
- gestionarea sesiunilor;
- Servlet-uri;
- filtre web.

---

## Apache Maven

Proiectul utilizează Maven pentru gestionarea dependențelor și procesul de build.

Fișierul principal de configurare este:

```text
application/pom.xml
```

Maven este responsabil pentru:

- descărcarea bibliotecilor necesare;
- compilarea codului Java;
- compilarea claselor de test;
- construirea aplicației;
- generarea fișierului WAR.

Fișierul rezultat este:

```text
application/target/metro-it.war
```

---

## Apache Tomcat

```text
Apache Tomcat 10.1.57
```

Tomcat este serverul web utilizat pentru rularea aplicației.

Aplicația utilizează Jakarta Servlet API și este publicată pe Tomcat sub forma unui fișier WAR.

---

## MySQL Server

Baza de date utilizată este MySQL.

Schema utilizează:

```text
utf8mb4
utf8mb4_0900_ai_ci
InnoDB
```

Din acest motiv este recomandată utilizarea MySQL 8 sau a unei versiuni compatibile.

Numele bazei de date este:

```text
metro_it
```

---

## MySQL Workbench

MySQL Workbench a fost utilizat pentru:

- conectarea la serverul MySQL;
- crearea bazei de date;
- executarea scripturilor SQL;
- vizualizarea tabelelor;
- verificarea datelor;
- efectuarea interogărilor SQL;
- verificarea relațiilor dintre înregistrări.

---

## JDBC

Comunicarea dintre aplicația Java și baza de date MySQL este realizată prin JDBC.

Driverul utilizat este:

```text
com.mysql.cj.jdbc.Driver
```

Biblioteca Maven:

```text
MySQL Connector/J 9.7.0
```

---

## Jakarta Servlet

Aplicația folosește:

```text
Jakarta Servlet API 6.0.0
```

Servlet-urile sunt responsabile pentru procesarea cererilor HTTP, apelarea serviciilor și trimiterea informațiilor către paginile JSP.

---

## JSP

JavaServer Pages sunt utilizate pentru interfața web.

JSP este folosit pentru:

- afișarea informațiilor primite de la Servlet-uri;
- formulare;
- tabele;
- afișarea mesajelor;
- afișarea diferită a funcțiilor în funcție de rol;
- navigarea în aplicație.

---

## HTML, CSS și JavaScript

HTML și JSP formează structura paginilor.

Fișierul principal de stil este:

```text
application/src/main/webapp/css/style.css
```

CSS este utilizat pentru aspectul general al aplicației, tabele, formulare, butoane, carduri, stări și elementele de navigare.

JavaScript este folosit numai pentru funcții simple ale interfeței, cum ar fi revenirea la pagina precedentă.

---

## BCrypt

Parolele utilizatorilor sunt protejate cu BCrypt.

Biblioteca utilizată:

```text
at.favre.lib:bcrypt:0.10.2
```

Costul BCrypt configurat în aplicație este:

```text
12
```

Aplicația nu compară parolele în format text și nu păstrează parole necriptate în baza de date.

---

## GitHub

GitHub este utilizat pentru păstrarea versiunii finale a proiectului și organizarea codului sursă.

În repository sunt păstrate:

- codul sursă;
- fișierele JSP;
- CSS;
- fișierele Maven;
- schema bazei de date;
- datele demonstrative;
- documentația proiectului.

Datele locale sensibile și fișierele generate automat nu sunt încărcate.

---

# 6. Arhitectura aplicației

Aplicația este organizată pe mai multe niveluri.

Fluxul general este:

```text
Browser
   │
   │ HTTP Request
   ▼
Servlet / Filter
   │
   ▼
Service
   │
   ▼
DAO
   │
   ▼
JDBC
   │
   ▼
MySQL
```

Pentru afișarea rezultatului:

```text
MySQL
   │
   ▼
DAO
   │
   ▼
Service
   │
   ▼
Servlet
   │
   ▼
JSP
   │
   ▼
Browser
```

Această separare permite ca interfața, logica aplicației și accesul la baza de date să nu fie amestecate în aceleași clase.

---

# 7. Nivelurile aplicației

## Model

Clasele din pachetul `model` reprezintă obiectele principale ale sistemului:

```text
User
Equipment
Incident
MaintenanceRecord
```

Enum-urile definesc valorile controlate utilizate în aplicație:

```text
UserRole
EquipmentStatus
IncidentPriority
IncidentStatus
MaintenanceResult
```

---

## DAO

DAO înseamnă `Data Access Object`.

Acest nivel este responsabil pentru comunicarea directă cu baza de date.

Principalele clase sunt:

```text
UserDAO
EquipmentDAO
IncidentDAO
MaintenanceRecordDAO
```

DAO-urile:

- execută instrucțiuni SQL;
- citesc rezultatele din MySQL;
- creează obiecte Java;
- inserează date;
- modifică date;
- caută înregistrări;
- gestionează interogările necesare aplicației.

---

## Service

Nivelul `service` conține logica aplicației.

Principalele clase sunt:

```text
AuthService
UserService
EquipmentService
IncidentService
MaintenanceRecordService
```

Aici sunt realizate:

- validarea datelor;
- verificarea regulilor aplicației;
- controlul stărilor incidentelor;
- verificarea tehnicianului atribuit;
- verificarea relațiilor dintre incident și echipament;
- verificarea permisiunilor necesare;
- tratarea erorilor provenite de la DAO.

---

## Servlet

Servlet-urile primesc cererile HTTP de la browser.

Exemple de operații gestionate de Servlet-uri:

```text
/login
/logout
/dashboard
/equipment
/incidents
/maintenance
/admin/...
```

Servlet-ul:

1. primește cererea;
2. citește parametrii;
3. apelează Service-ul necesar;
4. pregătește datele;
5. redirecționează sau transmite cererea către JSP.

---

## Filter

Aplicația folosește filtre pentru controlul accesului.

### AuthenticationFilter

Verifică dacă utilizatorul este autentificat.

Dacă nu există o sesiune validă, utilizatorul este trimis către pagina de login.

### RoleAuthorizationFilter

Verifică dacă utilizatorul are rolul necesar pentru accesarea resurselor administrative.

Zona:

```text
/admin/*
```

este rezervată administratorului.

---

## Session

Informațiile utilizatorului autentificat sunt păstrate în sesiunea HTTP.

Clasele utilizate sunt:

```text
SessionUser
SessionConstants
```

În sesiune nu este păstrată parola utilizatorului.

---

## JSP

Fișierele JSP reprezintă partea vizibilă a aplicației.

Ele primesc datele pregătite de Servlet-uri și construiesc paginile HTML afișate în browser.

---

# 8. Structura repository-ului

Structura generală este:

```text
METRO_IT-management-system/
│
├── application/
│   │
│   ├── pom.xml
│   │
│   └── src/
│       │
│       ├── main/
│       │   │
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── chebo16/
│       │   │           └── metroit/
│       │   │               │
│       │   │               ├── Main.java
│       │   │               │
│       │   │               ├── dao/
│       │   │               │   ├── EquipmentDAO.java
│       │   │               │   ├── IncidentDAO.java
│       │   │               │   ├── MaintenanceRecordDAO.java
│       │   │               │   └── UserDAO.java
│       │   │               │
│       │   │               ├── exception/
│       │   │               │   ├── NotFoundException.java
│       │   │               │   ├── ServiceException.java
│       │   │               │   └── ValidationException.java
│       │   │               │
│       │   │               ├── model/
│       │   │               │   ├── Equipment.java
│       │   │               │   ├── Incident.java
│       │   │               │   ├── MaintenanceRecord.java
│       │   │               │   ├── User.java
│       │   │               │   │
│       │   │               │   └── enums/
│       │   │               │       ├── EquipmentStatus.java
│       │   │               │       ├── IncidentPriority.java
│       │   │               │       ├── IncidentStatus.java
│       │   │               │       ├── MaintenanceResult.java
│       │   │               │       └── UserRole.java
│       │   │               │
│       │   │               ├── service/
│       │   │               │   ├── AuthService.java
│       │   │               │   ├── EquipmentService.java
│       │   │               │   ├── IncidentService.java
│       │   │               │   ├── MaintenanceRecordService.java
│       │   │               │   └── UserService.java
│       │   │               │
│       │   │               ├── util/
│       │   │               │   ├── DatabaseConnection.java
│       │   │               │   └── PasswordUtil.java
│       │   │               │
│       │   │               └── web/
│       │   │                   │
│       │   │                   ├── filter/
│       │   │                   │   ├── AuthenticationFilter.java
│       │   │                   │   └── RoleAuthorizationFilter.java
│       │   │                   │
│       │   │                   ├── servlet/
│       │   │                   │   ├── admin/
│       │   │                   │   ├── dashboard/
│       │   │                   │   ├── equipment/
│       │   │                   │   ├── incident/
│       │   │                   │   └── maintenance/
│       │   │                   │
│       │   │                   └── session/
│       │   │                       ├── SessionConstants.java
│       │   │                       └── SessionUser.java
│       │   │
│       │   ├── resources/
│       │   │   └── db.properties.example
│       │   │
│       │   └── webapp/
│       │       │
│       │       ├── index.jsp
│       │       ├── css/
│       │       │   └── style.css
│       │       │
│       │       └── WEB-INF/
│       │           └── views/
│       │               ├── admin/
│       │               ├── auth/
│       │               ├── common/
│       │               ├── dashboard/
│       │               ├── equipment/
│       │               ├── incidents/
│       │               └── maintenance/
│       │
│       └── test/
│           └── java/
│               └── com/
│                   └── chebo16/
│                       └── metroit/
│
├── database/
│   ├── schema.sql
│   └── sample_data.sql
│
├── .gitignore
└── README.md
```

---

# 9. Structura bazei de date

Baza de date conține patru tabele principale:

```text
users
equipment
incidents
maintenance_records
```

Relațiile principale pot fi reprezentate astfel:

```text
                    ┌──────────────────┐
                    │      users       │
                    │──────────────────│
                    │ id               │
                    │ username         │
                    │ password_hash    │
                    │ full_name        │
                    │ email            │
                    │ role             │
                    │ active           │
                    │ created_at       │
                    └───────┬──────────┘
                            │
             ┌──────────────┼──────────────────┐
             │              │                  │
             │ created_by   │ assigned         │ technician_id
             │              │ technician       │
             ▼              ▼                  ▼
┌──────────────────┐   ┌──────────────────┐   ┌─────────────────────┐
│    equipment     │   │    incidents     │   │ maintenance_records │
│──────────────────│   │──────────────────│   │─────────────────────│
│ id               │◄──│ equipment_id     │◄──│ equipment_id        │
│ inventory_number │   │ created_by       │   │ incident_id         │
│ name             │   │ assigned_tech... │   │ technician_id       │
│ type             │   │ title            │   │ work_description    │
│ manufacturer     │   │ description      │   │ replaced_components │
│ model            │   │ priority         │   │ result              │
│ serial_number    │   │ status           │   │ performed_at        │
│ ip_address       │   │ timestamps       │   └─────────────────────┘
│ status           │   │ solution         │
│ notes            │   └──────────────────┘
└──────────────────┘
```

Mai simplu:

```text
users
  │
  ├──────────────► incidents
  │                 │
  │                 ├──────────────► equipment
  │                 │
  │                 └──────────────► maintenance_records
  │
  └────────────────────────────────► maintenance_records
```

---

# 10. Tabelul `users`

Tabelul păstrează informațiile despre utilizatorii aplicației.

Câmpuri principale:

```text
id
username
password_hash
full_name
email
role
active
created_at
```

Rolurile disponibile sunt:

```text
ADMIN
TECHNICIAN
```

---

# 11. Tabelul `equipment`

Tabelul reprezintă inventarul echipamentelor IT.

Câmpuri importante:

```text
inventory_number
name
type
manufacturer
model
serial_number
ip_address
status
notes
```

Stările disponibile sunt:

```text
ACTIVE
IN_REPAIR
INACTIVE
DECOMMISSIONED
```

---

# 12. Tabelul `incidents`

Tabelul păstrează incidentele raportate.

Un incident este legat de:

- un echipament;
- utilizatorul care l-a creat;
- tehnicianul responsabil, dacă unul a fost atribuit.

Prioritățile disponibile sunt:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

Stările disponibile sunt:

```text
NEW
IN_PROGRESS
RESOLVED
CLOSED
```

Pentru urmărirea ciclului de viață sunt păstrate:

```text
created_at
started_at
resolved_at
closed_at
```

Soluția finală este păstrată în:

```text
solution_description
```

---

# 13. Tabelul `maintenance_records`

Acest tabel reprezintă istoricul intervențiilor efectuate de tehnicieni.

Fiecare înregistrare este legată de:

```text
incident
equipment
technician
```

Pentru fiecare intervenție se păstrează:

```text
work_description
replaced_components
result
performed_at
```

Rezultatele disponibile sunt:

```text
SUCCESS
PARTIALLY_COMPLETED
FAILED
```

---

# 14. Pregătirea bazei de date

Scripturile SQL se găsesc în:

```text
database/
```

Ele trebuie executate în următoarea ordine:

```text
1. database/schema.sql
2. database/sample_data.sql
```

---

## Pasul 1 – schema bazei de date

Se deschide MySQL Workbench și se execută:

```text
database/schema.sql
```

Acest script:

- creează baza `metro_it`;
- creează tabelul `users`;
- creează tabelul `equipment`;
- creează tabelul `incidents`;
- creează tabelul `maintenance_records`;
- configurează cheile externe;
- configurează indexurile;
- configurează tipurile ENUM.

---

## Pasul 2 – datele demonstrative

După crearea schemei se execută:

```text
database/sample_data.sql
```

Scriptul adaugă:

```text
3 utilizatori
13 echipamente
13 incidente
11 înregistrări de mentenanță
```

`sample_data.sql` este destinat unei baze pregătite pentru datele demonstrative. Executarea repetată peste aceleași date poate genera erori din cauza valorilor unice, cum ar fi username, email, inventory number sau serial number.

---

# 15. Configurarea conexiunii MySQL

Fișierul real cu datele de conectare la baza de date nu este păstrat pe GitHub.

Repository-ul conține numai:

```text
application/src/main/resources/db.properties.example
```

Se creează local o copie cu numele:

```text
application/src/main/resources/db.properties
```

Conținutul trebuie completat cu datele serverului MySQL local:

```properties
db.url=jdbc:mysql://localhost:3306/metro_it?useSSL=false&allowPublicKeyRetrieval=true&connectionTimeZone=LOCAL&preserveInstants=false
db.username=YOUR_DATABASE_USERNAME
db.password=YOUR_DATABASE_PASSWORD
db.driver=com.mysql.cj.jdbc.Driver
```

Exemplu:

```properties
db.url=jdbc:mysql://localhost:3306/metro_it?useSSL=false&allowPublicKeyRetrieval=true&connectionTimeZone=LOCAL&preserveInstants=false
db.username=root
db.password=parola_mysql_locala
db.driver=com.mysql.cj.jdbc.Driver
```

Parola reală MySQL nu trebuie încărcată pe GitHub.

---

# 16. Deschiderea proiectului în IntelliJ IDEA

După descărcarea repository-ului:

1. Se deschide IntelliJ IDEA.
2. Se selectează `Open`.
3. Se deschide directorul:

```text
application
```

4. IntelliJ va detecta fișierul:

```text
pom.xml
```

5. Se așteaptă importarea proiectului Maven și descărcarea dependențelor.
6. Se verifică dacă proiectul utilizează Java 21.
7. Se verifică dacă este selectat Eclipse Temurin JDK 21 sau un alt JDK 21 compatibil.

---

# 17. Construirea aplicației cu Maven

Din directorul:

```text
application
```

se poate executa:

```bash
mvn clean package
```

La final trebuie creat:

```text
target/metro-it.war
```

Pentru o verificare separată se pot utiliza:

```bash
mvn clean
mvn test
mvn package
```

În IntelliJ aceleași operații pot fi executate din fereastra Maven fără utilizarea terminalului.

---

# 18. Configurarea Apache Tomcat în IntelliJ IDEA

Pentru rularea proiectului prin IntelliJ:

1. Se deschide `Run` → `Edit Configurations`.
2. Se adaugă o configurație `Tomcat Server – Local`.
3. Se indică instalarea Apache Tomcat 10.1.57.
4. În secțiunea `Deployment` se adaugă aplicația WAR.
5. Context path trebuie să fie:

```text
/metro-it
```

6. Se pornește serverul.

După pornire se deschide:

```text
http://localhost:8080/metro-it/
```

---

# 19. Publicarea manuală pe Tomcat

Aplicația poate fi publicată și fără configurația IntelliJ.

După:

```bash
mvn clean package
```

fișierul:

```text
application/target/metro-it.war
```

se copiază în:

```text
<TOMCAT_HOME>/webapps/
```

Tomcat va publica automat aplicația.

După pornirea serverului:

```text
http://localhost:8080/metro-it/
```

---

# 20. Dependențele Maven principale

Proiectul utilizează următoarele dependențe principale:

```text
Jakarta Servlet API 6.0.0
MySQL Connector/J 9.7.0
BCrypt 0.10.2
```

Pentru construirea WAR este utilizat:

```text
Maven WAR Plugin 3.5.1
```

Configurarea completă se află în:

```text
application/pom.xml
```

---

# 21. Autentificarea

Procesul de autentificare funcționează astfel:

```text
Utilizator
    │
    ▼
LoginServlet
    │
    ▼
AuthService
    │
    ▼
UserDAO
    │
    ▼
MySQL
```

După identificarea utilizatorului, parola introdusă este verificată prin BCrypt.

Dacă datele sunt corecte:

```text
User
  │
  ▼
SessionUser
  │
  ▼
HTTP Session
  │
  ▼
Dashboard
```

Dacă autentificarea eșuează, utilizatorul rămâne pe pagina de login și primește un mesaj corespunzător.

---

# 22. Controlul accesului

Accesul este controlat atât prin sesiune, cât și prin rol.

```text
Cerere HTTP
    │
    ▼
AuthenticationFilter
    │
    ├── utilizator neautentificat ──► Login
    │
    ▼
RoleAuthorizationFilter
    │
    ├── acces nepermis ──► Access denied
    │
    ▼
Servlet
```

Un utilizator cu rol `TECHNICIAN` nu poate utiliza funcțiile administrative protejate prin:

```text
/admin/*
```

---

# 23. Legătura dintre incident și mentenanță

O înregistrare de mentenanță nu este independentă.

Ea trebuie să respecte relația:

```text
Incident
   │
   ├── Echipament
   │
   └── Tehnician atribuit
          │
          ▼
    MaintenanceRecord
```

Tehnicianul care înregistrează mentenanța trebuie să fie tehnicianul atribuit incidentului.

Echipamentul din înregistrarea de mentenanță trebuie să fie același cu echipamentul incidentului.

Mentenanța se poate înregistra numai când incidentul este în starea:

```text
IN_PROGRESS
```

---

# 24. Validarea datelor

Validarea este realizată în principal în nivelul `service`.

Sunt verificate, printre altele:

- identificatorii;
- câmpurile obligatorii;
- username-urile;
- emailurile;
- parolele;
- rolurile;
- existența echipamentelor;
- existența incidentelor;
- existența utilizatorilor;
- tehnicianul atribuit;
- starea incidentului;
- tranzițiile dintre stări;
- relația dintre incident și echipament;
- relația dintre incident și mentenanță.

Erorile aplicației sunt separate prin:

```text
ValidationException
NotFoundException
ServiceException
```

---

# 25. Securitatea parolelor

Parolele sunt tratate separat de celelalte informații ale utilizatorului.

Aplicația:

- nu stochează parole în format text;
- utilizează BCrypt;
- utilizează cost BCrypt 12;
- verifică parola pe baza hash-ului;
- nu păstrează parola în obiectul de sesiune;
- nu afișează hash-ul parolei în interfață;
- nu include parola bazei de date în repository.

Fișierul:

```text
db.properties
```

este exclus din Git prin `.gitignore`.

---

# 26. Fișiere care nu trebuie încărcate pe GitHub

Repository-ul nu trebuie să conțină fișiere locale sau generate automat.

Printre acestea se află:

```text
.idea/
target/
war-check/
application/src/main/resources/db.properties
DevelopmentPasswordSetup.java
```

Fișierul:

```text
db.properties.example
```

este păstrat intenționat deoarece nu conține date reale de autentificare și reprezintă modelul de configurare pentru instalarea proiectului pe un alt calculator.

---

# 27. Testarea proiectului

Proiectul conține clase de test pentru componentele principale:

```text
AuthService
Database connection
EquipmentDAO
EquipmentService
IncidentDAO
IncidentService
MaintenanceRecordDAO
MaintenanceRecordService
PasswordUtil
UserDAO
UserService
```

O parte dintre testele proiectului sunt teste de integrare și verificări manuale cu metode `main()`.

Din acest motiv:

```bash
mvn test
```

verifică și compilarea codului din `src/test/java`, dar clasele bazate pe `main()` nu reprezintă o suită JUnit executată automat.

Funcționalitatea aplicației a fost verificată și prin rularea fluxurilor complete în browser.

---

# 28. Exemplu de flux complet verificat

Un scenariu complet de utilizare este:

```text
Administrator
    │
    ├── creează incidentul
    │
    └── atribuie tehnicianul
            │
            ▼
       Tehnician
            │
            ├── Start work
            │
            ▼
       IN_PROGRESS
            │
            ├── adaugă mentenanță
            ├── descrie lucrările
            ├── indică componentele înlocuite
            ├── stabilește rezultatul
            ├── introduce soluția
            │
            ▼
        RESOLVED
            │
            ▼
      Administrator
            │
            ├── verifică incidentul
            │
            └── Close incident
                    │
                    ▼
                  CLOSED
```

În timpul acestui flux sunt actualizate automat informațiile temporale:

```text
Created at
Started at
Resolved at
Closed at
```

---

# 29. Datele demonstrative

Datele din `sample_data.sql` sunt create pentru a putea demonstra imediat funcționarea aplicației fără introducerea manuală a unui număr mare de înregistrări.

Exemple de echipamente incluse:

```text
Main Network Switch
Network Router
Self-Checkout Stations
Office Printers
Barcode Scanners
Network Terminal
Monitors
Phone
```

Incidentele demonstrative conțin mai multe priorități și stări pentru a permite verificarea:

```text
LOW
MEDIUM
HIGH
CRITICAL

NEW
IN_PROGRESS
RESOLVED
CLOSED
```

Istoricul de mentenanță conține atât lucrări finalizate cu succes, cât și lucrări parțial finalizate.

---

# 30. Ordinea recomandată pentru instalarea proiectului

Pentru instalarea proiectului pe un calculator nou se recomandă următoarea ordine:

```text
1. Instalarea JDK 21
          │
          ▼
2. Instalarea MySQL Server și MySQL Workbench
          │
          ▼
3. Executarea database/schema.sql
          │
          ▼
4. Executarea database/sample_data.sql
          │
          ▼
5. Crearea application/src/main/resources/db.properties
          │
          ▼
6. Deschiderea application/ în IntelliJ IDEA
          │
          ▼
7. Încărcarea proiectului Maven
          │
          ▼
8. Configurarea Apache Tomcat 10.1.x
          │
          ▼
9. mvn clean package
          │
          ▼
10. Publicarea metro-it.war
          │
          ▼
11. Deschiderea http://localhost:8080/metro-it/
          │
          ▼
12. Autentificarea cu unul dintre conturile demo
```

---

# 31. Rezumat tehnic

```text
Limbaj:               Java 21
JDK:                   Eclipse Temurin 21
Build system:          Apache Maven
Server web:            Apache Tomcat 10.1
Servlet API:           Jakarta Servlet 6
Interfață:             JSP + HTML + CSS
Acces bază de date:    JDBC
Bază de date:          MySQL
Administrare DB:       MySQL Workbench
Driver MySQL:          MySQL Connector/J
Hash parole:           BCrypt
Arhitectură:           Model + DAO + Service + Servlet + JSP
Pachet de distribuție: WAR
Versionare:            Git / GitHub
```

---

# 32. Pornire rapidă

Pentru cine are deja instalate Java, Maven, MySQL și Tomcat, pașii principali sunt:

```text
1. Rulează database/schema.sql
2. Rulează database/sample_data.sql
3. Creează application/src/main/resources/db.properties
4. Configurează datele MySQL
5. Deschide directorul application
6. Rulează mvn clean package
7. Publică target/metro-it.war pe Tomcat
8. Accesează http://localhost:8080/metro-it/
```

Pentru autentificare rapidă:

```text
Administrator
admin
MetroAdmin2026!

Tehnician
technician1
MetroTech2026!
```
