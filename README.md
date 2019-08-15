# Automatische Kategorisierung von Zeitungsartikeln

Dieser Prototyp ist im Rahmen meiner Bachelorarbeit entstanden. Mit ihm lassen sich Zeitungsartikel mit einem naiven Bayes-Klassifikator in die vier Kategorien *business*, *politics*, *science* oder *sport* einordnen. Außerdem können Zeitungsartikel inhaltlich miteinander verglichen werden.

## Verwendung
Mit diesem Prototyp können folgende Teilaufgaben durchgeführt werden:

* Konvertieren der Guardian-API-Antworten in Zeitungsartikel, die lediglich aus einer Überschrift und einem Textkörper bestehen,
* Verarbeiten von Zeitungsartikeln in für den Bayes-Klassifikatoren nutzbare Vorhersagemodelle,
* Kombinieren verschiedener Vokabulare, um für die Klassifikation herangezogen zu werden,
* Klassifikation von Zeitungsartikeln über einen naiven Bayes und
* Berechnung der Inhaltsähnlichkeit verschiedener Zeitungsartikel.

## Voraussetzungen
Um mit diesem Prototypen zu arbeiten, werden folgende Programme benötigt:

* [Gson](https://github.com/google/gson) - Google Gson wird genutzt, um Zeitungsartikel einzulesen. Außerdem wird es benötigt, um die Vorhersagemodelle zu speichern und zu laden.
* [Stanford CoreNLP](https://stanfordnlp.github.io/CoreNLP/index.html) - Stanford CoreNLP ist für die Verarbeitung der Texter zuständig. Dafür wird speziell der [Part-of-speech-Tagger](https://nlp.stanford.edu/software/tagger.html) eingesetzt.
* [Protocol Buffers](https://developers.google.com/protocol-buffers/) - Sollte bereits im Download von Stanford CoreNLP enthalten sein.

Außerdem müssen die Dateipfade in den Klassen ```GuardianJsonCombiner.java, DictionaryCreator.java, VocabularyCombiner.java, NaiveBayesClassifier.java, ArticleConverter.java``` und ```Application.java``` angepasst werden.

Aufgrund der Nutzungsbedingungen der Guardian Open Platform ist es mir nicht erlaubt, die von mir für meine Bachelorarbeit genutzten Zeitungsartikel zu veröffentlichen. Stattdessen habe ich [hier](https://github.com/Xianahru/guardian-api-reader) das kleine Programm veröffentlicht, mit dem ich insgesamt 42000 Zeitungsartikel über die API heruntergeladen habe.

## License
```
Copyright (C) <2019>  <Marius Rosenbaum>

This program is free software: you can redistribute it and/or
modify it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty 
of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public 
License along with this program.  If not, see
<https://www.gnu.org/licenses/>.

```
