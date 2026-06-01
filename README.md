# Sticker Tracker

Desktop app to track your FIFA World Cup 2026 Panini sticker collection. Mark what you have, see what you're missing, and copy your trade list straight to WhatsApp.

Built with Java + Swing + MySQL as a university project.

---

## Features

- **Progress tracker** — see how close you are to completing the album, broken down by section
- **Album view** — full grid of all 1034 stickers; collected ones highlighted, missing ones dimmed
- **Repeated stickers** — track your duplicates and generate a WhatsApp-ready trade list in one click
- **Recently added** — quick view of the last stickers you collected

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| UI | Swing (hand-coded, no NetBeans drag-and-drop) |
| UI Theme | FlatLaf (dark theme) |
| Fonts | Geist & Geist Mono |
| Database | MySQL 8 via JDBC |
| Migrations | Timestamp-based SQL files |

---

## Project Structure

```
sticker-tracker/
├── src/main/java/sticker_tracker/
│   ├── domain/          # Pure domain classes (Sticker, Section, UserSticker...)
│   ├── data/
│   │   ├── repository/  # JDBC repositories (findAll, createX, deleteX...)
│   │   └── migrations/  # SQL files named by timestamp
│   ├── infra/db/        # DatabaseConnection (Singleton), MigrationRunner
│   ├── ui/
│   │   ├── components/  # Reusable Swing components (RoundedPanel, StickerCard...)
│   │   └── screens/     # SplashScreen, HomeScreen, AlbumScreen
│   └── Main.java
├── src/main/resources/
│   └── assets/
│       ├── fonts/       # Geist & Geist Mono .ttf
│       ├── flags/       # Country flags by prefix (BRA.png, MEX.png...)
│       └── data/        # panini-wc-2026-catalog.json
└── lib/                 # flatlaf.jar, mysql-connector-j.jar
```

---

## Getting Started

### Prerequisites

- Java 17+
- Docker (for the database)
- No Maven or Gradle needed — dependencies are in `lib/`

### Setup

1. Start the database
```bash
docker compose up -d
```

2. Compile
```bash
javac -cp "lib/*" -d out $(find src -name "*.java")
```

3. Run
```bash
java -cp "out:lib/*" sticker_tracker.Main
```

On first run the app will automatically:
- Run all pending migrations
- Seed the full Panini WC 2026 catalog (1034 stickers)

---

## Architecture

Layered architecture with clear separation of concerns:

```
UI (Swing screens + components)
        ↓
  Repository layer (JDBC)
        ↓
   Domain classes
        ↓
  Infra (MySQL via JDBC)
```

Key architectural decisions are documented in `docs/` and summarized in `specs/spec-09-docs.md`.