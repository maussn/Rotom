# item-lending-library
Prototype for a platform for loaning out items. Made as an individual project for a traineeship.

# Installation

To install Rotom, execute the following line from the root directory of the project:

```
sudo ./setup.sh
```

It will ask some inputs. Use 'root' as the password for MySQL. Use default for all other options.

# Running Rotom

To run Rotom using a Vite server as reverse proxy, execute the following line from the root directory of the project:

```
./run-dev.sh
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