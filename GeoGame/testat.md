# Hilfestellung zur Prüfungsvorbereitung 

Die Prüfung des schriftlichen Testats wird mit maximal 60 Punkten bewertet. Die zweite Prüfungsleistung aus der Projektarbeit wird ebenfalls mit 60 Punkten bewertet.  

Die via SVN bereit gestellten Quellen sind v.a. für die Projektarbeit relevant und sollen Ihnen ein Gerüst und Vorlagen liefern, die Sie nach Ihren Vorstellungen und Projektzielen gestalten können.
Ich werde Ihnen pro Projekt-Team einen Branch im SVN einrichten. Diesen Branch checken Sie bitte separat _neben_ dem "trunk" auf Ihrem Rechner aus.

Mit Abgabe der Prüfungsleistung werden die branches für das Schreiben gesperrt und der Zustand eingefroren.

Die Team-Aufteilung ist:
* Team Alpha
    * Nicolas,
    * Jamin
    * Annika
* Team Bravo
    * Jan
    * Philip
    * Marius
* Team Charly
    * Daniel
    * Elisa
    * Jannick
    * Cilia __war bei der Teameinteilung nicht anwesend__


## Themen

Der Lernstoff umfaßt grundsätzlich alle Themen, die in der Vorlesung in Form von Präsentationsfolien gezeigt und über den Campus an Sie ausgegeben wurden.
Die Folien, welche die Einrichtung der Entwicklungsumgebung behandeln sind insofern _nicht_ Prüfungsstoff, als sie sich mit der Bedienung der Werkzeuge beschäftigen. Die Konzepte einer Entwicklungsumgebung mit z.B. Compiler, Build-Prozess sind ausdrücklich Lernstoff.
Es ist angesichts unterschiedlicher Vorbildung und notwendiger Voraussetzungen der Vorlesung (Java, EDV-Kenntnisse, Betriebssystem) unumgänglich für Sie ggf. in Eigenrecherche Wissenslücken, die Sie identifizieren, auszugleichen. 
Sie sollten ein klares Verständnis haben, was eine Virtual Machine ist und wie in Android Studio Programme die Sie auf einem Desktop Rechner mit dem Build-Prozess erstellen auf das Gerät geladen und ausgeführt werden. 

Auch welche Rolle das AppCompat (auch Support-Lib oder support-v4/support-v7 genannt) spielt im Verhältnis zu der Vielzahl in der Fläche genutzen Android OS Versionen (aktuell 4.4, 5.0, 5.1, 6.0, 7.0) sollten Sie frei beantworten können.
Sie könnten das z.B. anhand des NotificationBuilders erklären oder auf die Programmierung von Activities mit ConstraintLayout Bezug nehmen.

Stichworte:

* Compiler, Build-Prozess mit Gradle, CLASS- & DEX-Files, Generierung der R-Klasse
* Bedeutung von Signatur und Keystore für das APK. Was ist ein APK?
* Logging vs. Debugging (Vorteile/Nachteile, Was geht nur mit Logging, Warum nutzt man z.B. SLF4J und besser nicht Android Log.d)
* Grundlagen weniger Patterns (Factory, Model-View-Controller, Adapter, Observer, Flyweight, Singleton)
* Android Manifest und Inversion of Control, Factory Pattern
* Layout-XMLs, Inflate-Konzept, Objekt-Bäume von View-Objekten
* Bemaßungseinheiten, Zusammenhang mit Geräte-Formaten.
* Internationalisierung
* Verschiedene Verfahren auf OnClick Ereignisse zu reagieren.
* alternatives Serialisierungskonzept "Parcelable" mit Maps (Key/Value)
* Intent, PendingIntent, Singleton-Zugriff auf SystemServices (z.B. AlarmManger, NotificationManager)
* Aufruf von Activities (normal & "forResult"), Activity-Stack, Deklaration im Manifest
* Activity Lifecycle (onCreate, onResume, onPause, onStop, onDestroy...)  und Lage-Sensor
* Verarbeitung von Rückgabewerten von Activities (startActivityForResult, setResult/finish, onActivityResult)
* Receiver (Deklaration im Manifest, System-Events wie BOOT_COMPLETED, Zusammenspiel mit SystemServices)
* IntentService (Action, Extras, Deklaration im Manifest, Aufruf aus einer Activity)
* Datenbankzugriff mit ORM Lite
* ListAdapter (Prinzip, ArrayAdapter, CursorAdapter, Recycling von View-Objekten, Inflator, Model-View Binding, Flyweight) 
* Prinzip des Refactorings (Gründe, ggf. 2-3 der auf den Folien gezeigten Refactorings benennen können)
 
Was nicht Lerninhalte sind, obwohl sie vielleicht in der Vorlesung angesprochen wurden:

* SQL
* Bedienungsweise von Werkzeugen (Commandline, Menüstrukturen, Systemvariablen)
* UTF, Zeichenkodierungen
* Marktanteile
* Bedienung Google Services API Console, Google Play Developer Console, Firebase Console,...
* Die SVN Quellen sind nur insofern prüfungsrelevant, als Sie ihnen auf den Folien präsentiert wurden. Sie müssen nicht alle eingecheckten Quellen kennen und in weiten Teilen auch nur den Zustand der präsentiert wurde. Für die Projektarbeit wurde Code refactored. 


## Prüfungsverfahren

Es werden im Multiple-Choice-Verfahren viele Fragen gestellt, die richtig angekreuzt werden müssen.
Wenn Mehrfachnennungen möglich sind, dann wird das an der Frage ausgewiesen. Auch in diesen Fällen kann es aber sein, daß nur eine Antwort richtig ist. 
Jede richtige Antwort gibt die an der Frage genannte Anzahl von Punkten. Falsche Antworten bei Fragen mit Mehrfachnennung führen zu einem Punktabzug in der genannten Höhe. Die Gesamtleitung des Testats kann nicht weniger als 0 Punkte betragen.

Zusätzlich werden wenige Verständnisfragen gestellt, die Sie mit freien Worten beantworten sollen. Hier wird eine höhere Punktzahl als bei den Multiple-Choice Fragen pro Frage ausgewiesen und qualitativ bewertet.

Es können bei den Fragen auch Code-Beispiele gezeigt werden. Sie müssen keinen Code (Stift, Computer) selber schreiben.


## Musterfragen

Mit welchem Verfahren können Sie Fehler, die sich bei Ihren App-Nutzern in der Fläche - aber nicht bei Ihnen in der Entwicklungsumgebung - zeigen analysieren? 1-2 Sätze.

Ist der PendingIntent dazu geeignet synchron Ereignisse auszulösen? ja/nein

...





