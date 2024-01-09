
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

The two files worth looking at are `AppTest.java` found in the `app/src/test/java/io/readyset/bygone/` directory and the `Employee.java` file in the `app/src/main/java/io/readyset/bygone/` directory. The former class drives the latter; what's more, there are only two queries present. One is a simple point query and the other does a three way join. Both queries are supported by ReadySet. There's a lot more that can be added to this project, but it's good enough for now to demonstrate simple JDBC reads. 

## Directions to spin-up bygone-era

1. First, clone this repository and then change directories into `bygone-era`.

2. Decompress the `employees_data.sql.bz2` file by running:

    ```
    $ bunzip2 employees_data.sql.bz2
    ```

    Note, you may need to install [bzip2](https://en.wikipedia.org/wiki/Bzip2). 


2. Next, run `gradle composeUp`, which will start two containers. One container is named `postgres-primary` and exposes port `5432` and the other `cache`. It's exposed on port `5434`. The latter container depends on the primary instance starting up successfully. When Postgres starts up, it'll execute the `employees_data.sql` file, which will create a large employee-like database. Additionally, ReadySet will start up and snapshot this database (this might take 2-3 minutes). 

3. I'm lazy and am making this step manual for now: wait about a minute for ReadySet to snapshot. You can check the logs for ReadySet and look for the line that says streaming replication has started and then you know things are good go. Once ReadySet is ready, run `gradle test`. 

4. The previous step will run 4 JUnit tests successfully if Postgres and ReadySet are running. Shell into ReadySet via `psql postgresql://postgres:hoodoo@localhost:5433/hoodoo` and issue a `show proxied queries`  command. You should see two cache-able queries. Cache them via the `create cache from <cache_id>` command. 

5. Rerun `gradle test` to verify that JDBC is going through ReadySet now! 

6. You can shut both Docker containers down via the `gradle composeDown` command. 

## References

1. [Employees database for Postgres](https://github.com/h8/employees-database)
1. [Bitnami Docker distribution documentation](https://hub.docker.com/r/bitnami/postgresql)
1. [Gradle Docker Compose plugin](https://github.com/avast/gradle-docker-compose-plugin)