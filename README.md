# S25-Flynn-Group15
## Wordle Implementation

This project is a custom implementation of the popular word-guessing game Wordle, developed by a team of five developers. Our goal is to deliver a fully functional application that caters to a diverse range of users—including players, administrators, and testers—by addressing 15 predetermined user stories.

Players can enjoy guessing 5-letter words with real-time feedback, while administrators gain tools for analyzing user guesses and managing word sets. Testers are equipped with features to validate the application's functionality through automated input and output processing.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation and Setup](#installation-and-setup)
    - [Java Setup](#java-setup)
    - [Adding External Libraries](#adding-external-libraries)
    - [Database Configuration](#database-configuration)
- [Usage](#usage)
- [Authors](#authors)
- [Credits and Acknowledgments](#credits-and-acknowledgments)

---

## Overview

This project is a full-featured Wordle game implementation built in Java. Designed to cater to different user roles:

- **Players** enjoy an engaging 5-letter word guessing experience with real-time feedback.
- **Administrators** gain powerful tools to analyze game data and manage word sets.
- **Testers** benefit from built-in features for validating application functionality.
---

## Features

- **Real-time Feedback:** Immediate visual feedback for correct, misplaced, or incorrect guesses.
- **User Roles:** Custom interfaces and tools for players, administrators, and testers.
- **Database Integration:** Uses an SQLite database for persistent data storage.
- **JSON Parsing:** Incorporates JSON for handling configuration and data exchange.

---

## Prerequisites

- **Java 23 (or newer):** Ensure that Java is installed on your machine. Verify with:
  ```bash
  java -version
  ```

---

## Installation and Setup

### Java Setup

1. **Clone the Repository:**
   ```bash
   git clone <repository-url>
   cd wordle-implementation
   ```

2. **Open Project:**
   Open the project in IntelliJ IDEA or Visual Studio Code.

### Adding External Libraries

The project requires two external libraries that must be manually added:

1. **SQLite JDBC Driver:**
    - Visit the [SQLite JDBC Driver GitHub](https://github.com/xerial/sqlite-jdbc) page.
    - Scroll down to the download section. Click on **Releases** and download the jar file labeled `sqlite-jdbc-3.49.1.0.jar` from the most recent release.
    - In your IDE, navigate to **Project Structure** → **Libraries**. Click the "**+**" button, select **Java**, and add the downloaded `sqlite-jdbc-3.49.1.0.jar`.

2. **JSON-Java Library:**
    - Visit the [JSON-Java GitHub](https://github.com/stleary/JSON-java) page.
    - Scroll down until you see **JSON in Java [package org.json]**. Underneath, there is a link to download the latest release jar.
    - Repeat the process from above: add the downloaded JSON jar to your project libraries via **Project Structure** → **Libraries**.

### Database Configuration

After setting up the libraries, configure the database connection:

1. **Obtain the SQLite Database File Path:**
    - In your project (if updated based on the Dev branch), locate the `identifier.sqlite` file.
    - Copy its absolute file path.

2. **Update the DatabaseManager Class:**
    - Open the `DatabaseManager` class.
    - Locate the URL string that starts with `jdbc:sqlite:`.
    - Replace everything after `jdbc:sqlite:` with the absolute path of the `identifier.sqlite` file.

---

### Hard Mode

Hard Mode is a special challenge setting that hides all letters from your previous guesses, 
leaving only color-coded feedback.

🟩 Green = Correct letter, correct position

🟨 Yellow = Correct letter, wrong position

⬛ Gray = Letter is not in the word

How It Works
You can enable Hard Mode by checking the Hard Mode box at the start of the game.

Once you submit your first guess, Hard Mode is locked in for the rest of the game.

On future guesses, you will only see the color blocks from previous guesses — not the letters.

💡 Use your memory and logic skills to deduce the secret word with limited visual cues!

---

## Usage

- **Running the Application:**
  Once everything is set up, you can run the application directly from IntelliJ IDEA or Visual Studio Code using your configured run/debug configuration.

- **Testing:**
  Execute the test suite directly through your IDE's built-in testing tools.

---

## Authors

- **Oliver Grudzinski** - [grudzinskio@msoe.edu](mailto:grudzinskio@msoe.edu)
- **Mathias Galvan** - [galvanm@msoe.edu](mailto:galvanm@msoe.edu)
- **Jude Gill** - [gillj@msoe.edu](mailto:gillj@msoe.edu)
- **Charles Harris** - [harrisch@msoe.edu](mailto:harrisch@msoe.edu)
- **Anupranay Thouta** - [thoutaa@msoe.edu](mailto:thoutaa@msoe.edu)

---

Made with ❤ and 🍪
