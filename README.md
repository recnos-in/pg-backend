# PG Backend

## Prerequisites

- Java 21
- Maven
- PostgreSQL

## Setup

### 1. Install Java 21

On macOS (using Homebrew):
```bash
brew install openjdk@21
```

### 2. Set Java Environment Variables

Add the following to your `~/.zshrc` (or `~/.bash_profile` for bash):

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"
export PATH="$JAVA_HOME/bin:$PATH"
```

Then reload your shell configuration:
```bash
source ~/.zshrc
```

Or for the current session only:
```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"
export PATH="$JAVA_HOME/bin:$PATH"
```

### 3. Verify Java Installation

```bash
java -version
```

You should see output like:
```
openjdk version "21.0.9" 2025-10-21
OpenJDK Runtime Environment Homebrew (build 21.0.9)
OpenJDK 64-Bit Server VM Homebrew (build 21.0.9, mixed mode, sharing)
```

## Build the Project

```bash
mvn clean install
```

## Run the Application

```bash
mvn spring-boot:run
```

Or after building, run the JAR file:
```bash
java -jar target/pg-backend-1.0.jar
```

## Configuration

Configure your database connection in `src/main/resources/application.properties` or set environment variables as needed.