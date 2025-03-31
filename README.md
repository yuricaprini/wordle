# Wordle-overengineered

An over-engineered implementation of the popular Wordle game.
This project uses Wordle as an excuse to experiment with advanced software architecture and distributed systems concepts. It is built following the Clean Architecture principles, ensuring modularity and maintainability. The system is structured into three main modules (protocol, server, and client) and leverages multiple communication paradigms, including RMI (Remote Method Invocation), HTTP requests/responses, and UDP packets.

For authentication, it implements Bearer Token Authentication with JWT. The server is designed with a multi-threaded approach, featuring a reactor pattern for network communication, a background service for periodic tasks, and a persistent storage system using JSON serialization. The client interacts via CLI, supports real-time leaderboard updates via RMI callbacks, and allows multicast-based result sharing.

This project prioritizes robustness, scalability, and maintainability over practicality, making it a great study case to see distributed systems concepts and clean software design. Further details can be found in the report 'Relazione.pdf' (in 🇮🇹), located within the project.
