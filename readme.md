# Purpose

This program takes a tab delimited input file of customers, vehicles, and services in order to create a schedule for each mechanic in the shop. This program uses a database to save the mechanics, customers, customer vehicles, types of services, shop bays, and schedule.

# Build Steps

- Download the [SQLite JDBC](https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/sqlite-jdbc-3.36.0.3.jar) and move it to the build folder
- Open a command prompt in the src directory and run the following
- javac -d ../build \*.java
- cd ../build
- java -classpath ".;sqlite-jdbc-3.36.0.3.jar" App

Note: Make sure the sqlite filename matches the name of the one you downloaded.

# Example Input

Types can be mixed in a tab delimited text file.

| Type | Customer Name |
| ---- | ------------- |
| C    | J. Billings   |
| C    | R. Stevens    |
| C    | Z. Samero     |
| C    | N. Smith      |
| C    | H. Davis      |
| C    | S. Rodgers    |
| C    | F. Tomas      |
| C    | A. Stark      |
| C    | H. Potter     |
| C    | K. Williams   |
| C    | N. Fletcher   |
| C    | M. Gomez      |
| C    | U. Giacomo    |
| C    | T. Thomas     |
| C    | R. Zico       |
| C    | M. Novis      |

| Type | Customer Name | Vehicle Name          |
| ---- | ------------- | --------------------- |
| V    | J. Billings   | Chevrolet 1500        |
| V    | R. Stevens    | Kia Sorento           |
| V    | J. Billings   | Chevrolet Monte Carlo |
| V    | Z. Samero     | Dodge Dart            |
| V    | N. Smith      | Ford Focus            |
| V    | H. Davis      | Ford Thunderbird      |
| V    | N. Smith      | Buick Skylark         |
| V    | S. Rodgers    | Pontiac Solstice      |
| V    | F. Tomas      | Dodge Dart            |
| V    | A. Stark      | Chevorlet Equinox     |
| V    | H. Potter     | Dodge Grand Caravan   |
| V    | K. Williams   | Oldsmobile Cutless    |
| V    | N. Fletcher   | GMC Envoy             |
| V    | M. Gomez      | Ferrari Testarossa    |
| V    | U. Giacomo    | Pontiac Firebird      |
| V    | U. Giacomo    | Chevrolet Camero      |
| V    | U. Giacomo    | Ford Mustang          |
| V    | T. Thomas     | Jeep Liberty          |
| V    | R. Zico       | Jeep Wrangler         |
| V    | M. Noviss     | Jeep Cherokee         |
| V    | M. Noviss     | Chevrolet Volt        |

| Type | Customer Name | Vehicle Name          | Service Type                    |
| ---- | ------------- | --------------------- | ------------------------------- |
| S    | J. Billings   | Chevrolet 1500        | Cooling System Cleaning         |
| S    | R. Stevens    | Kia Sorento           | Cooling System Cleaning         |
| S    | J. Billings   | Chevrolet Monte Carlo | Cooling System Cleaning         |
| S    | Z. Samero     | Dodge Dart            | Brakes                          |
| S    | N. Smith      | Ford Focus            | Transmission Filter Replacement |
| S    | H. Davis      | Ford Thunderbird      | Oil Change                      |
| S    | N. Smith      | Buick Skylark         | Tire Replacement                |
| S    | S. Rodgers    | Pontiac Solstice      | Brakes                          |
| S    | F. Tomas      | Dodge Dart            | Oil Change                      |
| S    | A. Stark      | Chevorlet Equinox     | Oil Change                      |
| S    | H. Potter     | Dodge Grand Caravan   | Tire Replacement                |
| S    | U. Giacomo    | Pontiac Firebird      | Brakes                          |
| S    | N. Fletcher   | GMC Envoy             | Cooling System Cleaning         |
| S    | M. Gomez      | Ferrari Testarossa    | Brakes                          |
| S    | K. Williams   | Oldsmobile Cutless    | Tire Replacement                |
| S    | U. Giacomo    | Ford Mustang          | Cooling System Cleaning         |
| S    | K. Williams   | Oldsmobile Cutless    | Transmission Filter Replacement |
| S    | R. Zico       | Jeep Wrangler         | Tire Replacement                |
| S    | U. Giacomo    | Chevrolet Camero      | Brakes                          |
| S    | U. Giacomo    | Pontiac Firebird      | Cooling System Cleaning         |
| S    | U. Giacomo    | Ford Mustang          | Cooling System Cleaning         |
| S    | T. Thomas     | Jeep Liberty          | Transmission Filter Replacement |
| S    | R. Zico       | Jeep Wrangler         | Brakes                          |
| S    | M. Noviss     | Chevrolet Volt        | Transmission Filter Replacement |
| S    | M. Noviss     | Jeep Cherokee         | Transmission Filter Replacement |
| S    | U. Giacomo    | Chevrolet Camero      | Brakes                          |
| S    | N. Smith      | Ford Focus            | Brakes                          |
| S    | R. Stevens    | Kia Sorento           | Brakes                          |
| S    | K. Williams   | Oldsmobile Cutless    | Cooling System Cleaning         |
| S    | M. Noviss     | Chevrolet Volt        | Transmission Filter Replacement |

# Example Output

```
Database prepopulated
Would you like to import a new file? (Yes/No)
Yes
What is the path to the input file?
input.txt
Appointment Schedule
Sue's Schedule
Bay Number  Customer Name      Vehicle Description                            Service             Start Date & Time               End Date & Time
1             J. Billings           Chevrolet 1500            Cooling System Cleaning         2022-07-11 08:00:00.0         2022-07-11 12:00:00.0
1             J. Billings    Chevrolet Monte Carlo            Cooling System Cleaning         2022-07-11 13:00:00.0         2022-07-11 17:00:00.0
1                N. Smith               Ford Focus    Transmission Filter Replacement         2022-07-12 08:00:00.0         2022-07-12 10:00:00.0
1                A. Stark        Chevorlet Equinox                         Oil Change         2022-07-12 10:00:00.0         2022-07-12 10:30:00.0
1               H. Potter      Dodge Grand Caravan                   Tire Replacement         2022-07-12 10:30:00.0         2022-07-12 11:30:00.0
1              U. Giacomo         Pontiac Firebird                             Brakes         2022-07-12 11:30:00.0         2022-07-12 15:30:00.0
1             K. Williams       Oldsmobile Cutless                   Tire Replacement         2022-07-12 15:30:00.0         2022-07-12 16:30:00.0
1                M. Gomez       Ferrari Testarossa                             Brakes         2022-07-13 08:00:00.0         2022-07-13 11:00:00.0
1             K. Williams       Oldsmobile Cutless    Transmission Filter Replacement         2022-07-13 11:00:00.0         2022-07-13 14:00:00.0
1              U. Giacomo         Chevrolet Camero                             Brakes         2022-07-13 14:00:00.0         2022-07-13 17:00:00.0
1              U. Giacomo         Pontiac Firebird            Cooling System Cleaning         2022-07-14 08:00:00.0         2022-07-14 12:00:00.0
1                 R. Zico            Jeep Wrangler                             Brakes         2022-07-14 13:00:00.0         2022-07-14 16:00:00.0
1              U. Giacomo         Chevrolet Camero                             Brakes         2022-07-15 08:00:00.0         2022-07-15 11:00:00.0
1              R. Stevens              Kia Sorento                             Brakes         2022-07-15 11:00:00.0         2022-07-15 15:00:00.0
1               M. Noviss           Chevrolet Volt    Transmission Filter Replacement         2022-07-15 15:00:00.0         2022-07-15 17:00:00.0
Paychecks:
Week 1: $385.00

Steve's Schedule
Bay Number  Customer Name      Vehicle Description                            Service             Start Date & Time               End Date & Time
2              R. Stevens              Kia Sorento            Cooling System Cleaning         2022-07-11 08:00:00.0         2022-07-11 12:00:00.0
2               Z. Samero               Dodge Dart                             Brakes         2022-07-11 13:00:00.0         2022-07-11 16:00:00.0
2                H. Davis         Ford Thunderbird                         Oil Change         2022-07-11 16:00:00.0         2022-07-11 16:30:00.0
2                F. Tomas               Dodge Dart                         Oil Change         2022-07-11 16:30:00.0         2022-07-11 17:00:00.0
2                N. Smith            Buick Skylark                   Tire Replacement         2022-07-12 08:00:00.0         2022-07-12 09:00:00.0
2              S. Rodgers         Pontiac Solstice                             Brakes         2022-07-12 09:00:00.0         2022-07-12 12:00:00.0
2             N. Fletcher                GMC Envoy            Cooling System Cleaning         2022-07-12 13:00:00.0         2022-07-12 17:00:00.0
2              U. Giacomo             Ford Mustang            Cooling System Cleaning         2022-07-13 08:00:00.0         2022-07-13 12:00:00.0
2                 R. Zico            Jeep Wrangler                   Tire Replacement         2022-07-13 13:00:00.0         2022-07-13 14:00:00.0
2               T. Thomas             Jeep Liberty    Transmission Filter Replacement         2022-07-13 14:00:00.0         2022-07-13 16:00:00.0
2              U. Giacomo             Ford Mustang            Cooling System Cleaning         2022-07-14 08:00:00.0         2022-07-14 12:00:00.0
2               M. Noviss           Chevrolet Volt    Transmission Filter Replacement         2022-07-14 13:00:00.0         2022-07-14 15:00:00.0
2               M. Noviss            Jeep Cherokee    Transmission Filter Replacement         2022-07-14 15:00:00.0         2022-07-14 17:00:00.0
2                N. Smith               Ford Focus                             Brakes         2022-07-15 08:00:00.0         2022-07-15 11:00:00.0
2             K. Williams       Oldsmobile Cutless            Cooling System Cleaning         2022-07-15 11:00:00.0         2022-07-15 16:00:00.0
Paychecks:
Week 1: $342.00

Would you like to drop the database? (Yes/No)
No
```
