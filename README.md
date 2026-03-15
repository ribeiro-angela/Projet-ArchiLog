# Projet Médiathèque - R4.01 Architecture Logicielle

Projet BUT2 - Gestion des réservations, emprunts et retours d'une médiathèque via une architecture client/serveur Java.

## Structure du projet

```
src/
├── exceptions/
│   ├── ReservationException.java
│   ├── EmpruntException.java
│   └── RetourException.java
├── metier/
│   ├── Document.java        ← interface contractuelle
│   ├── Abonne.java
│   ├── Livre.java
│   ├── DVD.java
│   └── Mediatheque.java
├── serveur/
│   ├── AppServeur.java      ← point d'entrée serveur
│   ├── ServeurEcoute.java
│   ├── ServiceReservation.java
│   ├── ServiceEmprunt.java
│   └── ServiceRetour.java
└── client/
    ├── ClientBttp.java      ← client générique
    ├── ClientReservation.java
    ├── ClientEmprunt.java
    └── ClientRetour.java
```

## Lancer le projet

### 1. Compiler

```bash
mkdir out
javac -d out src/exceptions/*.java src/metier/*.java src/serveur/*.java src/client/*.java
```

### 2. Lancer le serveur

```bash
java -cp out serveur.AppServeur
```

Le serveur écoute sur 3 ports :
- `2000` → réservation
- `2001` → emprunt
- `2002` → retour

### 3. Lancer un client

```bash
# Client générique (bttp2.0)
java -cp out client.ClientBttp 2000   # réservation
java -cp out client.ClientBttp 2001   # emprunt
java -cp out client.ClientBttp 2002   # retour

# Clients dédiés
java -cp out client.ClientReservation
java -cp out client.ClientEmprunt
java -cp out client.ClientRetour
```

### ❌ Sitting Bull
Non implémenté (nécessite une config SMTP javax.mail).
