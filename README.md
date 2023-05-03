# Kassensystem Version 1.0

Dieses Programm stellt ein in JavaFX programmiertes Kassensystem dar.
Aufgrund der Produktauswahl habe ich es für den Gebrauch in einer Bäckerei angepasst.

## Allgemeine Nutzungshinweise

1. Klonen Sie das Repository an einen beliebigen Ort.
2. Wechseln Sie in das Verzeichnis.
3. Um das Programm auszuführen, führen Sie `./gradlew run` mit einer Shell aus.

Gradle lädt dann die benötigten Komponenten herunter und speichert sie 
in Ihrem Home-Verzeichnis unter einem versteckten Ordner names `.gradle` ab.

Die Produkte werden dynamisch aus einer Json-Datei geladen.
Dort kann man die Produkte und die Produktgruppen selbst erstellen.
Bei den Produkten kann man den **Namen**, **Kurznamen**, **Preis** und **Position** nach seinen Wünschen anpassen.
Zudem kann man Row- und Column-Span und die Dimensionen der Produktgruppe einstellen.

Die Datei befindet sich hier: `src/main/resources/com/openjfx/backwaren_linux.json`

----

## Infos

Für die Implementierung habe ich **Java 19** verwendet.
Der Code sollte aber auch mit **Java 17 LTS** ausführbar sein.

Für den Build-Prozess habe ich das Plugin "**org.openjfx.javafxplugin**" benutzt.
Doch weil das Plugin sehr veraltet ist, arbeitet meine Anwendung mit JavaFX version 16.
