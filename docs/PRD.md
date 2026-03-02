# PRD: Bibliotheksverwaltung

## 1. Ziel

Eine einfache Spring Boot Anwendung zur Verwaltung einer Bibliothek. Das Projekt dient als Grundlage zum Testen von Code-Reviews und soll typische Schichten einer Spring-Anwendung abbilden (Domain, Service, Controller).

## 2. Rahmenbedingungen

| Aspekt              | Entscheidung                  |
|---------------------|-------------------------------|
| Framework           | Spring Boot                   |
| Persistenz          | Spring Data JPA               |
| Datenbank           | H2 In-Memory                  |
| API                 | REST (JSON)                   |
| GUI                 | Keine                         |
| Authentifizierung   | Keine                         |

## 3. Domain-Modell

### 3.1 Book (Buch)

| Feld            | Typ      | Beschreibung                          |
|-----------------|----------|---------------------------------------|
| id              | Long     | Technischer Primaerschluessel         |
| title           | String   | Titel des Buches (Pflichtfeld)        |
| author          | String   | Autor des Buches (Pflichtfeld)        |
| isbn            | String   | ISBN-Nummer (eindeutig, Pflichtfeld)  |
| publicationYear | int      | Erscheinungsjahr                      |
| available       | boolean  | Verfuegbarkeitsstatus (default: true) |

### 3.2 Member (Mitglied)

| Feld         | Typ    | Beschreibung                             |
|--------------|--------|------------------------------------------|
| id           | Long   | Technischer Primaerschluessel            |
| name         | String | Name des Mitglieds (Pflichtfeld)         |
| email        | String | E-Mail-Adresse (eindeutig, Pflichtfeld)  |
| memberNumber | String | Mitgliedsnummer (eindeutig, generiert)   |

### 3.3 Loan (Ausleihe)

| Feld       | Typ       | Beschreibung                              |
|------------|-----------|-------------------------------------------|
| id         | Long      | Technischer Primaerschluessel             |
| book       | Book      | Referenz auf das ausgeliehene Buch        |
| member     | Member    | Referenz auf das ausleihende Mitglied     |
| loanDate   | LocalDate | Ausleihdatum (wird automatisch gesetzt)   |
| dueDate    | LocalDate | Faelligkeitsdatum (loanDate + 14 Tage)    |
| returnDate | LocalDate | Rueckgabedatum (null solange ausgeliehen) |

## 4. Features

### 4.1 Buecher verwalten

- **Buch anlegen** — `POST /api/books`
- **Alle Buecher auflisten** — `GET /api/books`
- **Buch nach Titel suchen** — `GET /api/books?title={title}`
- **Buch nach Autor suchen** — `GET /api/books?author={author}`

### 4.2 Mitglieder verwalten

- **Mitglied anlegen** — `POST /api/members`
- **Alle Mitglieder auflisten** — `GET /api/members`
- **Mitglied nach Name suchen** — `GET /api/members?name={name}`

### 4.3 Ausleihe

- **Buch ausleihen** — `POST /api/loans`
  - Erwartet: `bookId` und `memberId`
  - Setzt `loanDate` auf heute, `dueDate` auf heute + 14 Tage
  - Setzt `book.available` auf `false`
- **Buch zurueckgeben** — `PUT /api/loans/{loanId}/return`
  - Setzt `returnDate` auf heute
  - Setzt `book.available` auf `true`

## 5. Geschaeftsregeln

| Nr. | Regel                                                         |
|-----|---------------------------------------------------------------|
| G1  | Ein Mitglied darf maximal 3 Buecher gleichzeitig ausleihen.  |
| G2  | Ein bereits ausgeliehenes Buch kann nicht erneut verliehen werden. |
| G3  | Die ISBN muss eindeutig sein.                                 |
| G4  | Die E-Mail-Adresse eines Mitglieds muss eindeutig sein.      |

## 6. Fehlerbehandlung

Bei Regelverstossen soll die API sinnvolle HTTP-Statuscodes und Fehlermeldungen zurueckgeben:

| Fall                          | HTTP-Status | Beispiel-Meldung                              |
|-------------------------------|-------------|------------------------------------------------|
| Buch nicht gefunden           | 404         | "Book not found with id: {id}"                 |
| Mitglied nicht gefunden       | 404         | "Member not found with id: {id}"               |
| Buch nicht verfuegbar         | 409         | "Book is already on loan"                      |
| Ausleihlimit erreicht         | 409         | "Member has reached the maximum of 3 loans"    |
| Doppelte ISBN                 | 409         | "A book with this ISBN already exists"          |
| Doppelte E-Mail               | 409         | "A member with this email already exists"       |
| Validierungsfehler            | 400         | Feldspezifische Fehlermeldungen                 |

## 7. Projektstruktur (Ziel)

```
src/main/java/com/.../
    domain/          # Entities: Book, Member, Loan
    repository/      # Spring Data JPA Repositories
    service/         # Geschaeftslogik
    controller/      # REST-Controller
    exception/       # Eigene Exceptions + ExceptionHandler
```
