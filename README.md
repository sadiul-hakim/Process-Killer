# 🛠️ Process Killer CLI

[![Java Version](https://img.shields.io/badge/Java-21+-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

A **cross-platform command-line utility** to manage processes by PID or Port. Works on **Windows, Linux, and macOS**, allowing you to **list, find, and kill processes** directly from your terminal.

---

## 🌟 Features

* List all running processes with ports
* Find a process by port
* Kill a process by PID
* Cross-platform: Windows, Linux, macOS
* Interactive CLI with real-time feedback

---

## 💻 Requirements

* Java 21+
* Windows / Linux / macOS
* Terminal / Command Prompt access

---

## 📝 Usage

Once the program starts, the CLI displays the list of running processes. Use the following commands:

| Command          | Description                                        |
| ---------------- | -------------------------------------------------- |
| `refresh`        | Refresh and display all running processes.         |
| `find by <port>` | Find the PID of the process using a specific port. |
| `kill <pid>`     | Kill a process by its PID.                         |
| `q`              | Quit the application.                              |

### Example Session

```text
: refresh
TCP    0.0.0.0:8080    LISTENING    1234
TCP    0.0.0.0:3306    LISTENING    5678
...

: find by 8080
Found process on port 8080 with PID: 1234

: kill 1234
Killed process 1234

: q
```

---

## 🛠 How It Works

* **Windows**

  * Lists processes using `netstat -ano`
  * Kills processes using `taskkill /PID <pid> /F`
* **Linux / macOS**

  * Lists processes using `lsof -i -P -n`
  * Kills processes using `kill -9 <pid>`

The tool automatically detects your OS and runs the appropriate commands.

---

## 🎨 Screenshots

**Listing Processes**

```
TCP    0.0.0.0:8080    LISTENING    1234
TCP    0.0.0.0:3306    LISTENING    5678
```

**Finding Process by Port**

```
: find by 8080
Found process on port 8080 with PID: 1234
```

**Killing a Process**

```
: kill 1234
Killed process 1234
```

---

## ⚠️ Notes

* Some processes require **admin/root privileges** to be killed.
* Invalid PIDs or ports are gracefully handled.
* Intended for **development and local use only**.

---

© [sadiul-hakim](https://github.com/sadiul-hakim)