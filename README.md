# item-lending-library
Prototype for a platform for loaning out items. Made as an individual project for a traineeship.

# Installation

To install Rotom, execute the following line from the root directory of the project:

```
sudo ./setup.sh -d
```

It will ask some inputs. Use 'root' as the password for MySQL. Use default for all other options.

# Running Rotom

First, run kafka:
```
./run-kafka.sh
```

To see events, run the following lines in a new terminal:
```
cd kafka_*
bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server localhost:9092
```

Lastly, run Rotom using a Vite server and follow the localhost port link (likely port 3000) using the following lines in a new terminal:

```
./run-dev.sh
```

Login credentials to try:  
```
username: jan
password: password
```
```
username: piet  
password: password  
```

# Stack
### Backend
- Scala
  - Slick (for accessing databases)
  - http4s (server framework)
  - Circe (JSON parsing)
  - MUnit (unit testing)
- Kafka
- MySQL

### Frontend
- Vite
- React
- Typescript