# lagom-films

### Running all services:

    $mvn lagom:runAll

What’s happening behind the scenes when you runAll?

 - an embedded Service Locator is started
 - an embedded Service Gateway is started
 - a Cassandra server is started
 - a Kafka server is started
 - your services start
    - …and register with the Service Locator
    - …and register the publicly accessible paths in the Service Gateway

### Running one service:

    $mvn -pl <your-project-name> lagom:run

### Kafka

 - Logging is configured such that it goes only to files. You can find the logs of Kafka in the folder
 _**<your-project-root>/target/lagom-dynamic-projects/lagom-internal-meta-project-kafka/target/log4j_output**_.

 - Commit Log - Kafka is essentially a durable commit log. You can find all data persisted by Kafka in the folder
_**<your-project-root>/target/lagom-dynamic-projects/lagom-internal-meta-project-kafka/target/logs**_.