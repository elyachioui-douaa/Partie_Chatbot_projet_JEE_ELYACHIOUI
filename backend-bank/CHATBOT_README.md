# Projet : Système de Gestion de Comptes Bancaires avec Chatbot Bancaire Intelligent

## Introduction

#### Ce projet est une extension d’une application bancaire déjà développée en Java/Spring Boot. L’application initiale permettait la gestion des comptes, des clients et des transactions. La présente partie ajoute une fonctionnalité d’intelligence artificielle sous la forme d’un Chatbot bancaire intelligent, capable de répondre aux questions des clients, de fournir des informations sur leurs comptes et leurs opérations récentes, et d’interagir via l’API REST ou un bot Telegram. Cette intégration vise à enrichir l’expérience utilisateur et à automatiser l’assistance client tout en restant sécurisée et contextuelle.

## Description du projet

Le Chatbot permet aux clients de :  
- Consulter le solde de leurs comptes.  
- Vérifier l’historique de leurs transactions.  
- Recevoir des réponses personnalisées basées sur leurs données.  

Toutes les interactions sont enregistrées dans la base de données pour assurer le suivi et l’historique.

## Fonctionnalités

- **Chat contextuel** : le Chatbot utilise les informations du client (profil, comptes, opérations) pour générer des réponses précises.  
- **Historique des conversations** : stockage automatique des messages dans la table `ChatMessage`.  
- **Intégration Telegram** : possibilité de recevoir et répondre aux messages via un bot Telegram.  
- **Sécurité** : JWT pour sécuriser les endpoints, le webhook Telegram peut être protégé avec un secret.

## Endpoints API

 `/api/chatbot/message` | POST | Envoyer une question au Chatbot (ex : solde ou opérations). |
`/api/chatbot/history?customerId={id}` | GET | Obtenir l’historique des conversations d’un client. |
| `/api/telegram/webhook` | POST | Webhook pour Telegram (header secret optionnel : `X-Telegram-Bot-Api-Secret-Token`). |

## Configuration

Ajouter ces informations dans le fichier **`application.properties`** :

```properties
openai.api.key=VOTRE_CLE_OPENAI
openai.model=gpt-3.5-turbo
telegram.bot.token=VOTRE_TELEGRAM_BOT_TOKEN
telegram.webhook.secret=OPTIONAL_SECRET
```
## Sécurité

#### Les endpoints du Chatbot sont protégés par JWT, comme le reste de l’API.

#### Le webhook Telegram /api/telegram/webhook est accessible sans JWT, mais il peut être sécurisé avec le header telegram.webhook.secret.

## Conclusion

#### Ce projet illustre l’intégration d’un Chatbot IA dans un backend bancaire, offrant des réponses contextuelles et personnalisées aux clients. Grâce à l’approche RAG, le Chatbot peut exploiter les données clients de manière sécurisée et fournir un service interactif et fiable. Cette architecture modulaire permet d’étendre facilement les fonctionnalités, d’améliorer la sécurité et de préparer le système pour un usage en production. Le projet sert de base solide pour développer des solutions intelligentes et innovantes dans le domaine bancaire.
