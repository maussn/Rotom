# item-lending-library
Prototype for a platform for loaning out items. Made as an individual project for a traineeship. In its current state, it is unfinished. Loans requests can be made and the events posted, but nothing is done with the events.

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

Next, run the event projector:
```
./run-projector.sh
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