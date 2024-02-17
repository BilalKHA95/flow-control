# Flow Control and Message Passing System

This Java project implements a distributed flow control and message passing system designed to manage communication and resource allocation between various networked entities, namely producers, consumers, and a master coordinator. It utilizes a combination of token passing for resource management, request handling for coordination, and message queues for asynchronous communication.

## Overview

The system is built around three main types of entities:

- **Producers (`Producteur`)**: Entities that generate and send application-specific messages.
- **Consumers (`Consommateurs`)**: Entities that receive and process messages from producers.
- **Master Coordinator (`MasterT`)**: An entity that manages tokens and directs messages between producers and consumers to ensure proper flow control and resource allocation.

Messages within the system are encapsulated in several classes, each serving different communication purposes:

- **Application Messages (`ApplicatifMessage`)**: Carries payload for application-level communication.
- **Requests (`Requests`)**: Manages request messages with statuses for acknowledging, rejecting, or making requests.
- **Tokens (`Token`)**: Controls access or permissions within the network, indicating resource availability or permissions.

## Key Features

- **Token Passing Mechanism**: Ensures flow control by passing a token among entities, granting them the permission to send messages.
- **State Management**: Entities manage their state (e.g., sleeping, processing, waiting, success) to handle requests and messages efficiently.
- **Asynchronous Communication**: Utilizes message queues and buffers to manage messages before processing or forwarding, supporting asynchronous communication.
- **Dynamic Resource Allocation**: The master coordinator dynamically allocates resources based on the system's current state and demand from producers and consumers.
- **Network Communication**: Implements networking capabilities, allowing entities to communicate over a network using sockets.

## System Architecture

The project is structured into several Java packages and classes:

- `fr.dauphine.ja.teamdeter.flowControl.message`: Contains message classes (`Message`, `ApplicatifMessage`, `Requests`, `Token`).
- `fr.dauphine.ja.teamdeter.flowControl.stations`: Contains the main entities (`Station`, `Producteur`, `Consommateurs`, `MasterT`) and utility classes (`State`).
- `Main`: Initializes the system, setting up producers, consumers, and the master coordinator, and starts the communication process.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or later.

### Setup and Configuration

1. **Clone the Repository**: Clone or download the project to your local machine.
2. **Compile the Project**: Navigate to the project's root directory and compile the Java files.
   ```
   javac -d bin src/fr/dauphine/ja/teamdeter/flowControl/**/*.java
   ```
3. **Run the Main Class**: Start the system by running the `Main` class.
   ```
   java -cp bin fr.dauphine.ja.teamdeter.flowControl.stations.Main
   ```

### Configuration Options

The system's behavior can be adjusted by modifying the configuration parameters in the `Main` class:

- `nbProd`: Number of producers.
- `nbCons`: Number of consumers.
- `timeProcessConsommateurs`: Processing time for consumers.
- `timeProcessProducteur`: Processing time for producers.
- Other parameters related to buffer sizes, message handling, and timing can also be adjusted as needed.


## Acknowledgments

This project was developed as part of a university assignment at Dauphine University, Paris.
