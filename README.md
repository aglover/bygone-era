
# Bygone-era 

<img src="img/logo.png" align="right"
     alt="bygone-era logo" width="150" height="150">

A simple Java application that uses Plain Jane JDBC to connect to a local Postgres instance; 
what's more, it also connects to a local ReadySet instance (via port 5433). Both ReadySet and
Postgres can be run via a `docker compose` command. 

To run everything, you need to have the following installed:

1. Java version 21
2. Gradle 8.5
3. Docker Engine

## Directions to spin-up bygone-era

1. First, clone this repository and then change directories into `bygone-era`.

2. Decompress the `employees_data.sql.bz2` file by running:

    ```
    $ bunzip2 employees_data.sql.bz2
    ```

    Note, you may need to install [bzip2](https://en.wikipedia.org/wiki/Bzip2). 


2. Next, run `gradle composeUp`, which will start two containers. One container is named `postgres-primary` and exposes port `5432` and the other `cache`. It's exposed on port `5434`. The latter container depends on the primary instance starting up successfully. When Postgres starts up, it'll execute the `employees_data.sql` file, which will create a large employee-like database. Additionally, ReadySet will start up and snapshot this database (this might take 2-3 minutes). 

3. I'm lazy and am making this step manual for now: wait about a minute for ReadySet to snapshot. You can check the logs for ReadySet and look for the line that says streaming replication has started and then you know things are good go. Once ReadySet is ready, run `gradle test`. 

4. You can shut both Docker containers down via the `gradle composeDown` command. 

## References

1. [Employees database for Postgres](https://github.com/h8/employees-database)
1. [Bitnami Docker distribution documentation](https://hub.docker.com/r/bitnami/postgresql)