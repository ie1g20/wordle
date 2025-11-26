# Wordle Application - Setup Guide

This guide will help you get the Wordle application running locally using Docker and Firebase (either via emulator or a real Firebase project).

---

## Prerequisites

Before starting, make sure you have the following installed:

- [Docker](https://www.docker.com/get-started)  
- [Docker Compose](https://docs.docker.com/compose/install/)  
- [Firebase CLI](https://firebase.google.com/docs/cli)  

---

## 1. Set up Firebase

The application supports two Firebase setups: **Emulator** or **Real Firebase Project**.

### Option 1: Using Firebase Emulator

1. Open a terminal and navigate to the `firebase` folder:  
```bash
cd firebase
```

2. Start the emulator:  
```bash
firebase emulators:start --only firestore,ui
```

3. The Firebase UI will be available at:  
[http://127.0.0.1:4001/firestore](http://127.0.0.1:4001/firestore)

> ⚠️ If using the emulator, no backend configuration changes are required.

### Option 2: Using Your Own Firebase Database

1. Update the `FIRESTORE_EMULATOR_HOST` variable in the backend `Dockerfile` to point to your Firebase project.  
2. Package/build the backend as usual.  

---

## 2. Start the Application

From the root `wordle` directory, run:

```bash
docker-compose up -d
```

This will start the following services:

| Service   | URL                         |
|-----------|----------------------------|
| Keycloak  | [http://localhost:8080](http://localhost:8080) (Admin console) |
| Frontend  | [http://localhost:3000](http://localhost:3000) |
| Backend   | Running as part of Docker Compose |

---

## 3. Stop the Application

To stop all running containers:

```bash
docker-compose down
```

---

## 4. Default Credentials (Keycloak)

> ⚠️ Only needed if you are accessing the Keycloak admin console.

- **Username:** `admin`  
- **Password:** `admin`  

*(Change these credentials in production.)*

---

## 5. Notes & Tips

- Ensure the ports `3000`, `8080`, and `4001` are free before starting.  
- For first-time backend setup, make sure the Firebase project has Firestore enabled.  
- Consider using the emulator for local development to avoid accidental writes to production.

---

## 6. Architecture Diagram

```
        +----------------+          +----------------+
        |   Frontend     |  <--->   |   Backend      |
        +----------------+          +----------------+
                 |                           |
                 |                           |
                 v                           v
        +----------------+          +----------------+
        | Firebase       |          |  Keycloak      |
        | (Firestore)    |          | (Auth Server)  |
        +----------------+          +----------------+
```

- **Frontend** communicates with **Backend** for app logic.
- **Backend** reads/writes to **Firebase**.
- **Backend** uses **Keycloak** for authentication.

## 7. Notes
- In the Dockerfile you need to replace password and usernames with enviromental variables for security reason.
