# ScotlandSaar

ScorlandSaar ist eine Augmented Reality Adaption des Brettspiel-Klassikers [Scotland Yard](https://de.wikipedia.org/wiki/Scotland_Yard_(Spiel)). Die Spielidee wurde für Smartphones adapiert. Anstatt Figuren über ein Spielbrett zu steuern, laufen die Spieler mit ihren Smartphones durch die Stadt. Über die Smartphone-Anwendung können die Spieler die Positionen der anderen Spieler sowie den Spielablauf verfolgen. Ziel des Spieles ist es, den verdeckt agierenden Mister X zu stellen. 

Das Spiel basiert auf einem Cloud-Backend, welches mit Hilfe von Google Firebase realisiert wurde. Das Backend verwaltet den Spielstand, fungiert als Spielelobby und realisiert die Spiellogik. Die Smartphone-Anwendung stellt das Spielinterface für die Spieler da und wurde als Android-App entwickelt. 


Bei diesem Projekt handelt es sich um eine prototypische Implementierung. Darüber hinaus wurden API-Keys und Firebase-Entpunkte aus Datenschutzgründen entfernt. Daher kann das Projekt nicht ohne entsprechende Anpassungen gebaut und ausgeführt werden. 

Ein wesentlicher Bestandteil der Android-Anwendung ist [AHBottomnavigation] (https://github.com/aurelhubert/ahbottomnavigation).

Dem Spiel liegt eine Karte der saarländischen Hauptstadt Saarbrücken zugrunde, woraus sich auch der Name des Projekts herleitet. Es ist aber möglich Karten weiter Städte für das Spiel aufzubereiten und in das Spiel zu integrieren. 
---

ScorlandSaar is an augmented reality adaptation of the board game classic [Scotland Yard](https://en.wikipedia.org/wiki/Scotland_Yard_(board_game)). The game idea was adapted for smartphones. Instead of controlling characters on a game board, players use their smartphones to roam the city. The smartphone application allows players to track the positions of other players as well as the course of the game. The goal is to catch the covert Mister X.

The game is based on a cloud backend, which was realized with using Google Firebase. The backend manages the score, acts as a game lobby and implements the game logic. The smartphone application provides the game interface for the players and was developed as an Android app.

Please note that this project is a prototype implementation. Moreover, API Keys and Firebase Endpoints have been removed for privacy reasons. Hence the project cannot be built and run out of the box. 

An integral part of the Android application is [AHBottomnavigation](https://github.com/aurelhubert/ahbottomnavigation).

The game is based on a map of the Saarland capital Saarbrücken, from which the name of the project is derived. However, it is also possible to integrate maps of other cities into the game.
