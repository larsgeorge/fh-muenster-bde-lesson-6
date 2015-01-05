=================================
FH Münster - Big Data Engineering
=================================

## Code für Übung Nummer 6

Dieses Repository enthaelt das Material für Vorlesung und Übung 6.

### Project Build

Das Projekt kann wie immer einfach gecloned und dann mit Maven uebersetzt werden:
```
$ git clone https://github.com/larsgeorge/fh-muenster-bde-lesson-6.git
$ cd fh-muenster-bde-lesson-6
$ mvn clean package
```
 
Das Projekt enthaelt Beispiele fuer Micro und Macro Pipelines, beide werden im folgenden beschrieben.

## Micro Pipelines: Morphlines

Micro Pipelines lassen sich mit Hilfe von Morphlines als Abfolge von eingebauten (aber auch durch eigenen
Code erweiterbaren) Kommandos abbilden. Dies ist den Kommandozeilen Tools in der Linux Shell sehr 
aehnlich, wobei dort das Pipesymbol "|" benutzt wird um die Ausgabe des vorherigen Tools in die Eingabe
des nachfolgenden Tools zu leiten.


Ein Beispiel ist das Parsen der Ausgabe eines Syslog Systemprozesses: 
```
[cloudera@quickstart fh-muenster-bde-lesson-6]$ hadoop jar target/fh-muenster-bde-lesson-6-1.0-SNAPSHOT-bin.jar testmorphline src/test/resources/examples/parse-syslog-morphline.conf src/test/resources/examples/test-morphline-data-parse-syslog.txt 
15/01/05 06:48:31 INFO api.MorphlineContext: Importing commands
15/01/05 06:48:32 INFO api.MorphlineContext: Done importing commands
15/01/05 06:48:32 INFO stdlib.LogInfoBuilder$LogInfo: output record: [{hostname=[syslog], message=[<164>Feb  4 10:46:14 syslog sshd[607]: listening on 0.0.0.0 port 22.], msg=[listening on 0.0.0.0 port 22.], pid=[607], priority=[164], program=[sshd], timestamp=[1970-02-04T18:46:14.000Z]}]
``` 

## Macro Pipelines: Oozie

Hier benutzen wir Oozie, ein prozessgesteuertes System fuer die Abarbeitung von Aufgaben, um unser 
TF-IDF Beispiel aus den frueheren Uebungen zu automatisieren. Ziel ist es nicht nur die drei MapReduce 
Jobs laufen zu lassen, sondern auch auf neue Eingabedaten zu reagieren. Aus diesem Grund wird ein 
Verzeichnis ueberwacht und sobald neue Daten in Form von Dateien eintreffen wird ein neuer Suchindex
erstellt. 

TBD.  

Viel Glück!

Lars George
