[![Build Status](https://travis-ci.com/resolvingarchitecture/rta-java.svg?branch=master)](https://travis-ci.com/resolvingarchitecture/rta-java)
[![Maven Version](https://maven-badges.herokuapp.com/maven-central/resolvingarchitecture/rta-java/badge.svg)](http://search.maven.org/#search|gav|1|g:"resolvingarchitecture"%20AND%20a:"rta-java")

# RTA - Real-Time Analytics
General RTA implementation using Storm with Kafka, Cassandra, Neo4J, MongoDB, PostgreSQL, and other components.

Newly upgraded to latest libraries as of March 2020...NOT DEPLOYED NOR TESTED YET. 
Will not run yet as it is being refactored into a form for more general use.

## Version

0.5.2-SNAPSHOT

## Authors / Developers

* Brian Taylor - [GitHub](https://github.com/objectorange) | [LinkedIn](https://www.linkedin.com/in/decentralizationarchitect/) 
| brian@resolvingarchitecture.io PGP: 2FA3 9B12 DA50 BD7C E43C 3031 A15D FABB 2579 77DC

## Opportunities


## Solution


## Design

### Databases

#### Event Database: Cassandra
Events are persisted to Cassandra and used to feed population summarization and other analytics.

#### Reference Database: File

#### Relationships Database: Neo4J

#### Cache Database: In-Memory HashMap<id,Individual>


## [Implementation](https://github.com/resolvingarchitecture/rta)

The application is compiled using JDK 1.11 although some components do support older versions (1.7).

## Integrations


## Applications


## Support


## Installation

### Ubuntu 18.04 LTS

### OpenJDK11
https://www.ubuntu18.com/ubuntu-install-openjdk-11/

### Kafka
https://www.digitalocean.com/community/tutorials/how-to-install-apache-kafka-on-ubuntu-18-04
sudo systemctl start kafka
sudo systemctl status kafka
sudo journalctl -u kafka
sudo systemctl enable kafka
sudo systemctl stop kafka
kafkat partitions

### Storm

### Neo4J

### MongoDB

### Cassandra

### PostgreSQL

