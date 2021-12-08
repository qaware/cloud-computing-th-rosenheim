# Übung: PaaS mit Heroku

## Aufgabe 1

Erstellen Sie sich einen [kostenlosen Heroku-Account](https://signup.heroku.com/).

## Aufgabe 2

Installieren Sie die [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli) und loggen Sie sich ein (`heroku login`).

## Aufgabe 3

Deployen Sie den [Neinhorn](https://github.com/zalintyre/neinhorn)-Service von GitHub so auf Heroku, dass Sie danach dessen UI im Browser ansehen können.

## Aufgabe 4

Schreiben Sie einen Hello World Service in der Sprache ihrer Wahl und deployen Sie diesen auf Heroku.

[Hier](https://devcenter.heroku.com/start) gibt es diverse Heroku "Getting Started" Guides.

### Lokale Entwicklung

Bringen Sie die lokale Entwicklung zum Laufen (`heroku local`), so dass sie bequem an der Anwendung entwickeln können.

### Konfiguration

Bauen Sie in Ihre Anwendung eine Konfigurationsmöglichkeit ein (z.B. um die Begrüßung zu konfigurieren).
Verwenden Sie dann die [Heroku-Mechanismen zur Konfiguration](https://devcenter.heroku.com/articles/config-vars) um
Ihre Anwendung zu konfigurieren.

### (Optional) Datenbank

Verwenden Sie [Heroku Postgres](https://www.heroku.com/postgres), um Ihrer Anwendung eine Datenbank zu spendieren.
Programmieren Sie einen einfachen Anwendungsfall, der diese Datenbank verwendet.
