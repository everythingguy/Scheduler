
/**
 * Filename: Entity.java
 * 
 * Project: Homework3
 * 
 * Author: Kevin Gyorick
 * 
 * Date: 10/09/2021
 * 
 * File Purpose:    This file contains the functions and variables that are in common between
 *                  all database entity objects such as Bay, Customer, Mechanic, Schedule, Service, and Vehicle
 * 
 * Program Purpose: The program schedules appointments for a mechanic shop. 
 *                  Appointments are scheduled first come, first served as 
 *                  long as the job can be completed the same day it is started 
 *                  given the current schedule. Mechanics with lower bay numbers 
 *                  assigned have higher priority when multiple mechanics are available
 */
import java.sql.SQLException;

public abstract class Entity {
    // instance of the database
    static DB Database = null;
    // wether or not the database is known to exist
    static boolean DBExists = false;
    // this entities database id
    private int TUID;

    /**
     * construct a new entity and initialize the database
     * 
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public Entity() throws SQLException {
        initializeDatabase();
    }

    /**
     * construct a new entity with a id and initialize the database
     * 
     * @param TUID the id of the entity
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public Entity(int TUID) throws SQLException {
        this();
        this.TUID = TUID;
    }

    /**
     * Gets the entity's id
     * 
     * @return the id of the entity
     */
    public int getTUID() {
        // return the id of the entity
        return this.TUID;
    }

    /**
     * Makes sure there is a active connection to the database and the database is
     * built
     * 
     * @throws SQLException throws if there is a connection or query error with the
     *                      database
     */
    public static void initializeDatabase() throws SQLException {
        // if the database is null, initialize
        if (Database == null)
            Database = new DB();
        // if the database does not exist build it
        if (!DBExists) {
            DBExists = true;
            Database.buildDatabase(false);
        }
    }

    /**
     * Drops the whole database
     * 
     * @throws SQLException throws if there a problem terminating the database
     *                      connection
     */
    public static void dropDatabase() throws SQLException {
        // drop the database
        Database.dropDatabase();
    }

    /**
     * Prints all entities in a 2D array, useful for debugging
     * 
     * @param arrEntity the 2D array of entities to print
     */
    public static void printEntity(Entity[][] arrEntity) {
        // print a 2D array of entities
        for (Entity[] arrEntities : arrEntity) {
            printEntity(arrEntities);
        }
    }

    /**
     * Prints all entities in a array, useful for debugging
     * 
     * @param arrEntity the array of entities to print
     */
    public static void printEntity(Entity[] arrEntity) {
        // print a array of entities
        for (Entity entityCurrent : arrEntity) {
            printEntity(entityCurrent);
        }
    }

    /**
     * Prints a entity to the terminal
     * 
     * @param entityCurrent the entity to print
     */
    public static void printEntity(Entity entityCurrent) {
        // print a entity
        System.out.println(entityCurrent);
    }

    // entities must override the toString method
    /**
     * @return a string representation of the entity
     */
    @Override
    public abstract String toString();
}
