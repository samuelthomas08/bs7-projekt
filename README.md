# BS7 Projektwoche 11cFI2

Konsolenbasierte Java-Anwendung zum Einlesen, Bereinigen, Analysieren und Exportieren von Bestelldaten aus einer CSV-Datei.

Projekt im Rahmen der BS7-Projektwoche, 11. Klasse.

## Funktionen

- **Einlesen** einer pipe-getrennten CSV-Datei mit Kunden-, Adress- und Bestelldaten
- **Datacleansing**: automatische Normalisierung korrigierbarer Formatfehler (Datumsformate, Dezimaltrennzeichen), Erkennung und Protokollierung nicht korrigierbarer Fehler (z. B. ungültige Kalenderdaten, fehlerhafte E-Mail-Adressen)
- **Datenanalyse**: Filterung und Auswertung von Bestellungen nach Kunde, Postleitzahl, Datum, Woche, Wochentag, Monat, Jahr sowie nach Zeitspannen
- **Export** der Kunden-, Adress- und Bestelldaten als CSV-Dateien (für den Import in eine Datenbank)
- **TUI** (textbasierte, interaktive Konsolensteuerung) für die Bedienung

## Verwendung

1. Projekt kompilieren und `Main.java` ausführen
2. Absoluten Pfad zur CSV-Datei eingeben, wenn danach gefragt wird
3. Im Hauptmenü einen Menüpunkt per Nummer auswählen (Bestellungen filtern, Datenexport, etc.)

## Datenformat

Erwartet wird eine pipe-getrennte (`|`) CSV-Datei mit folgenden Spalten je Zeile:

```
Vorname|Nachname|Geburtsdatum|Kunde seit|eMail|Straße|Hausnummer|PLZ|Ort|Bestelldatum|Bestellbetrag
```

Datumsformat: `yyyy-MM-dd`. Abweichende Formate (`.` statt `-`, ausgeschriebene Monatsnamen) werden beim Import automatisch normalisiert.

## Bekannte Einschränkungen

- `Tui.clearConsole()` ist Windows-spezifisch (nutzt `cmd /c cls`)
- Keine automatisierten Tests (Unit-Tests wurden aus Zeitgründen bewusst weggelassen, siehe Dokumentation Abschnitt 3.2)