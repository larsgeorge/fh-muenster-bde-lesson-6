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

#### Workflows

Zuerst erstellen wir einen reinen Ablauf, aka Workflow. Dieser befindet sich im `src/main/resources/oozie-workflow` Verzeichnis, zusammen mit einer Properties Datei, welche die Parameter für den Workflow definiert. Beide Dateien, d.h. `workflow.xml` und `job.properties`, werden in ein neues Verzeichnis kopiert und angepasst:

```
[cloudera@quickstart fh-muenster-bde-lesson-6]$ mkdir oozie-tfidf
[cloudera@quickstart fh-muenster-bde-lesson-6]$ cp src/main/resources/oozie-workflow/* oozie-tfidf/
[cloudera@quickstart fh-muenster-bde-lesson-6]$ vim oozie-tfidf/job.properties 
[cloudera@quickstart fh-muenster-bde-lesson-6]$ cat oozie-tfidf/job.properties 
nameNode=hdfs://quickstart.cloudera:8020
jobTracker=quickstart.cloudera:8032
basePath=${nameNode}/user/${user.name}

oozie.wf.application.path=${basePath}/oozie-tfidf

inputJob1=${basePath}/books
outputJob1=${basePath}/1-word-freq
outputJob2=${basePath}/2-word-freq
outputJob3=${basePath}/3-word-freq
```  

Der wichtige Teil ist das Setzen des Applikationspfades, also des Parameters `oozie.wf.application.path`. Dieser muss dem Namen des Verzeichnisses in HDFS entsprechen. Wir benutzen dort den Gleichen wie lokal, `oozie-tfidf`. Dann muss noch die JAR Datei des Projektes in ein Verzeichnis names `lib` kopiert werden:

```
[cloudera@quickstart fh-muenster-bde-lesson-6]$ mkdir oozie-tfidf/lib
[cloudera@quickstart fh-muenster-bde-lesson-6]$ cp target/fh-muenster-bde-lesson-6-1.0-SNAPSHOT-bin.jar oozie-tfidf/lib/
```

Das Ganze sieht dann so aus:

```
[cloudera@quickstart fh-muenster-bde-lesson-6]$ ll -R oozie-tfidf/
oozie-tfidf/:
total 16
-rw-rw-r-- 1 cloudera cloudera  302 Jan  8 23:46 job.properties
drwxrwxr-x 2 cloudera cloudera 4096 Jan  8 04:52 lib
-rw-rw-r-- 1 cloudera cloudera 5236 Jan  8 23:48 workflow.xml

oozie-tfidf/lib:
total 33360
-rw-rw-r-- 1 cloudera cloudera 34158536 Jan  8 04:52 fh-muenster-bde-lesson-6-1.0-SNAPSHOT-bin.jar
```

Was nun noch nötig ist, ist das Kopieren des Verzeichnisses (und der Buchdaten) in HDFS:

```
[cloudera@quickstart fh-muenster-bde-lesson-6]$ hdfs dfs -put oozie-tfidf
[cloudera@quickstart fh-muenster-bde-lesson-6]$ hdfs dfs -put src/main/resources/books
```

Dann kann man den Workflow manuell über die Oozie CLI starten und auch den Status nachfolgend abfragen:

```
[cloudera@quickstart fh-muenster-bde-lesson-6]$ oozie job -run -oozie http://quickstart.cloudera:11000/oozie/ -config oozie-tfidf/job.properties 
job: 0000007-141218042433753-oozie-oozi-W
[cloudera@quickstart fh-muenster-bde-lesson-6]$ oozie job -oozie http://quickstart.cloudera:11000/oozie/ -info 0000007-141218042433753-oozie-oozi-W
Job ID : 0000007-141218042433753-oozie-oozi-W
------------------------------------------------------------------------------------------------------------------------------------
Workflow Name : TF-IDF-1
App Path      : hdfs://quickstart.cloudera:8020/user/cloudera/oozie-tfidf
Status        : RUNNING
Run           : 0
User          : cloudera
Group         : -
Created       : 2015-01-13 20:41 GMT
Started       : 2015-01-13 20:41 GMT
Last Modified : 2015-01-13 20:42 GMT
Ended         : -
CoordAction ID: -

Actions
------------------------------------------------------------------------------------------------------------------------------------
ID                                                                            Status    Ext ID                 Ext Status Err Code  
------------------------------------------------------------------------------------------------------------------------------------
0000007-141218042433753-oozie-oozi-W@:start:                                  OK        -                      OK         -         
------------------------------------------------------------------------------------------------------------------------------------
0000007-141218042433753-oozie-oozi-W@WordFrequencyInDocument                  RUNNING   job_1418905385247_0013 RUNNING    -         
------------------------------------------------------------------------------------------------------------------------------------
```

Oozie hat auch eine eigene webbasierte Oberfläche, welche Workflows und Coordinators etc. anzeigen kann. Dort kann man aber nur lesend auf die Informationen zugreifen. Auch Hue hat ein Oozie Dashboard, welches aber Workflows etc. anhalten, weiterlaufen und beenden kann.

#### Coordinators

TBD.

Viel Glück!

Lars George
