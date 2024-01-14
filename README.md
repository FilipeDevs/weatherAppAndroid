# Projet MOBG5

Ce dépôt contient les sources du projet final : **Weather App**.

## Description

Le projet est une application météo minimaliste permettant d'obtenir des informations météorologiques. L'application offre les fonctionnalités suivantes :

* Consultation de la météo : Prévisions météorologiques actuelles pour n'importe quelle ville ou lieu spécifique.

* Prévisions à court terme : Prévisions pour les 5 prochains jours.

* Géolocalisation : Permet à l'application d'obtenir automatiquement les données météorologiques pour la position actuelle de l'utilisateur, lui montrant ainsi les informations météo pour sa localisation.

* Gestion de la liste de villes : L'utilisateur peut ajouter des villes à sa liste personnelle, lui permettant ainsi de suivre les conditions météorologiques de plusieurs endroits.

* Widget : Widget sur l'écran d'accueil de l'appareil, permettant de jeter un coup d'œil rapide sur les conditions météorologiques sans ouvrir l'application complète.

* Cas d'exceptions : L'application gère également quelques cas d'exception, notamment l'absence d'internet, de signal GPS, ou des permissions de localisation. Malgré ces "problèmes", l'application reste fonctionnelle et informe l'utilisateur de ces situations.

## Persistance des données

L'application utilise la base de données locale (ROOM) pour stocker et gérer les villes ajoutées par l'utilisateur. Les informations météo y sont également stockées pour permettre à l'application de fonctionner dans des cas d'exception.

## Service Rest

L'application fait usage de l'API [OpenWeather](https://openweathermap.org/api) pour fournir des données météorologiques.


## Auteur

**Filipe Pereira Martins** - G58093 - E11 - 2023/24
