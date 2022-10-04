# Beispielprogramme des Programmierprojekts

Hier ist der Quellcode für die in der Einarbeitungsphase genutzten Projekte zu
finden. Es sind dies einerseits die Beispielprogramme des _jMonkeyEngine
Beginner Tutorials_
(https://wiki.jmonkeyengine.org/docs/3.4/tutorials/beginner/beginner.html) und
andererseits das Spiel *Droids*.

Die Quellen bestehen aus den folgenden Gradle-Unterprojekten:

* _:jme-helloworld_
* _:droids:app_
* _:droids:model_
* _:droids:npc_
* _:droids:converter_
* _:droids:assignments_
* _:common_
* _:graphics_

Das Unterprojekt _:jme-helloworld_ enthält den Quellcode des Tutorials vom
jMonkeyEngine-Git-Repository
(https://github.com/jMonkeyEngine/jmonkeyengine.git) auf GitHub, Commit af375391
mit geringfügigen Änderungen, damit der Quellcode auch auf Apple-Computern mit
M1-Prozessoren läuft.

Die anderen Unterprojekte gehören zu _Droids_. _:droids:converter_ wird für das
Spiel selbst nicht benötigt, sondern enthält lediglich den Code, um einige im
Spiel verwendete Modelle etc. in _J3O_-Dateien umzuwandeln, die von jME
einfacher geladen werden können.

Das Unterprojekt _:droids:model_ enthält den Modellanteil des Spiels,
_:droids:app_ View und Controller sowie die eigentliche jME-Funktionalität und
die Hauptklasse _pp.droids.DroidsApp_. _:droids:npc_ dient der Realisierung des
Bosses, und _:droids:assignments_ enthält Testfälle für das Boss-Verhalten. Die
Unterprojekte _:common_ und _:graphics_ enthalten Hilfsklassen.

Im Verzeichnis `droids/doc` finden sich einige Diagramme (im _PlantUML_- sowie
im _PNG_-Format) zu _Droids_.

## 1 Vorbereitung

Das Projekt nutzt Java 18. Unter Linux muss [_Eclipse
Temurin_](https://adoptium.net/temurin/releases/?version=18) als JDK verwendet
werden, andere JDKs verursachen unter Linux Probleme. Auf anderen
Betriebssystemen empfehlen wir aber ebenfalls Temurin. Im Folgenden ist
beschrieben, wie Sie Temurin installieren und die Umgebungsvariable
**JAVA_HOME** richtig setzen, damit Sie Gradle (siehe unten) verwenden können.

### 1.1 Installation von Temurin

Laden Sie [_Eclipse Temurin_](https://adoptium.net/temurin/releases/?version=18)
entsprechend Ihrem Betriebssystem und Ihrer Prozessorarchitektur herunter und
entpacken Sie das Archiv in einem Verzeichnis Ihrer Wahl auf Ihrem Rechner.

### 1.2 Setzen von JAVA_HOME

Zur Verwendung mit Gradle muss die Umgebungsvariable **JAVA_HOME** richtig
gesetzt werden. Folgen Sie dazu den nachfolgenden Anweisungen entsprechend Ihrem
Betriebssystem:

* **Windows:**

  Öffnen Sie ihre Powershell (Core) bzw. ihr Windows Terminal mit Powershell
  (Core). Überprüfen Sie, ob die Umgebungsvariable korrekt gesetzt ist:  
  `Get-ChildItem -Path Env:JAVA_HOME`  
  Falls kein oder ein falscher Pfad gesetzt ist, setzen Sie diesen mit dem
  folgenden Kommando (in einer Zeile):  
  `[System.Environment]::SetEnvironmentVariable('JAVA_HOME','<Pfad zum SDK>',[System.EnvironmentVariableTarget]::User)`

  Alternativ können Sie die GUI verwenden. Unter Windows 10 klicken Sie die
  Windows-Taste und dann das Zahnrad um die Einstellungen zu öffnen. Dort wählen
  Sie "System", dann "Info" (links unten) und nun
  "Erweiterte Systemeinstellungen" (rechts) um den Dialog "Systemeigenschaften"
  zu starten. Im Reiter "Erweitert" klicken Sie
  "Umgebungsvariablen..." und klicken dann unter "Benutzervariablen" den Knopf
  "Neu.." um JAVA_HOME anzulegen oder "Bearbeiten" um ihn zu ändern. Geben Sie
  als Name `JAVA_HOME` und als Wert den Pfad ein. Schließen Sie mit "OK".

  > **(!) Beachten Sie, dass Sie die jeweilige Applikation neustarten müssen**,
  > um von der gesetzten Umgebungsvariablen Notiz zu nehmen.
  > Dies betrifft auch die Shell, die Sie gerade verwenden.

* **UNIX (Linux/MacOS):**

  Öffnen oder erstellen Sie die Datei `~/.profile` und ergänzen Sie am Ende der
  Datei die Zeile:

  `export JAVA_HOME="<Pfad zum entpackten Archiv>"`

  Ersetzen Sie dabei `<Pfad zum entpackten Archiv>` mit dem entsprechenden Pfad.
  Zum Beispiel:

  `export JAVA_HOME="/home/user/jdk-18.0.2.1"`

  Fügen Sie dann die folgende Zeile hinzu:

  `export PATH="$JAVA_HOME/bin:$PATH"`

## 2 Programmstart

Grundsätzlich kann man das gesamte Projekt einfach in IntelliJ öffnen. Details
dazu sind im Aufgabenblatt zur Einarbeitungsaufgabe zu finden. Im Folgenden ist
beschrieben, wie die einzelnen Programme (_Droids_ und die Programme des
Tutorials) unmittelbar von der Kommandozeile gestartet werden können.

Am einfachsten erzeugt man ausführbare Programme und Start-Skripte mit
dem Befehl

`./gradlew`

Anschließend lässt sich Droids starten, indem man das generierte Start-Skript

`droids/app/build/install/droids/bin/droids`

in der Kommandozeile aufruft. Unter Windows kann es je nach Shell
(Eingabeaufforderung cmd) erforderlich sein, `/` jeweils durch `\ ` zu ersetzen.

Die einzelnen Programme des Tutorials kann man starten, indem man entsprechend eines
der Start-Skripte aufruft, die im Verzeichnis

`jme-helloworld/build/install/HelloJME3/bin`

zu finden sind, also z.B.

`jme-helloworld/build/install/HelloJME3/bin/HelloJME3`

Die Namen dieser Skripte entsprechen den Klassennamen im Tutorial.

Droids kann man alternativ auch unmittelbar mit Gradle starten, indem man den
Befehl

`./gradlew :droids:app:run`

in der Kommandozeile aufruft.

## 3 Hinweise zum Spiel _Droids_

1) Droids hat ein Menü, in dem man startet und in das man immer mit der
   Esc-Taste kommt. Aus dem Menü heraus lässt sich das Programm auch
   schließen. Beachte, dass sich beim Laden und Speichern eines Spiels
   kein Dateidialog öffnet. Vielmehr muss man den Dateipfad in das
   Dialogfeld eingeben. Da JSON-Dateien geschrieben werden, empfiehlt
   sich das Datei-Suffix _.json_.

2) Wenn das Spiel gestartet wird, wird im **aktuellen
   Arbeitsverzeichnis** nach einer Datei `config.json` gesucht und diese
   geladen. Mit ihr lassen sich Voreinstellungen des Spiels ändern. Eine
   solche Datei ist im Verzeichnis `droids/app` zu finden.

## 4 Allgemeine Gradle-Tasks:

- `./gradlew clean`

  Entfernt alle `build`-Verzeichnisse und alle erzeugten Dateien.

- `./gradlew classes`

  Übersetzt den Quellcode und legt unter build den Bytecode sowie
  Ressourcen ab.

- `./gradlew javadoc`

  Erzeugt die Dokumentation aus den JavaDoc-Kommentaren im Verzeichnis
  `build/docs/javadoc` des jeweiligen Unterprojekts.

- `./gradlew test`

  Führt die JUnit-Tests durch. Ergebnisse sind im Verzeichnis
  `build/reports/tests` des jeweiligen Unterprojekts zu finden.

- `./gradlew build`

  Führt die JUnit-Tests durch und erstellt in `build/distributions`
  gepackte Distributionsdateien

- `./gradlew installDist`

  Erstellt unter `droids/app/build/install` ein Verzeichnis, das eine
  ausführbare Distribution samt Start-Skripten enthält (siehe oben).
  
---
Oktober 2022
