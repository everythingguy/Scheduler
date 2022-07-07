
/**
 * Filename: Mechanic.java
 * 
 * Project: Homework3
 * 
 * Author: Kevin Gyorick
 * 
 * Date: 10/09/2021
 * 
 * File Purpose:    This file represents a mechanic object and acts as middleware 
 *                  between the database and the application for mechanics.
 * 
 * Program Purpose: The program schedules appointments for a mechanic shop. 
 *                  Appointments are scheduled first come, first served as 
 *                  long as the job can be completed the same day it is started 
 *                  given the current schedule. Mechanics with lower bay numbers 
 *                  assigned have higher priority when multiple mechanics are available
 */
import java.sql.SQLException;

public class Mechanic extends Entity {
    String strName; // the mechanic's name
    double intHourly_payrate; // the mechanic's payrate

    /**
     * construct a new mechanic w/ a name and payrate
     * 
     * @param strName           the name of the new mechanic
     * @param intHourly_payrate the payrate of the mechanic
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public Mechanic(String strName, double intHourly_payrate) throws SQLException {
        super();
        this.strName = strName;
        this.intHourly_payrate = intHourly_payrate;
    }

    /**
     * construct a new mechanic w/ a id, name, and payrate
     * 
     * @param TUID              the id of the mechanic
     * @param strName           the name of the mechanic
     * @param intHourly_payrate the payrate of the mechanic
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public Mechanic(int TUID, String strName, double intHourly_payrate) throws SQLException {
        super(TUID);
        this.strName = strName;
        this.intHourly_payrate = intHourly_payrate;
    }

    /**
     * Gets all mechanics from the database
     * 
     * @return a array of all mechanics in the database
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public static Mechanic[] getMechanics() throws SQLException {
        // initialize the database
        initializeDatabase();
        // return all the mechanics from the database in a array
        return Database.getMechanics();
    }

    /**
     * a string representation of a mechanic
     * 
     * @return a string representation of a mechanic
     */
    @Override
    public String toString() {
        // a string representation of a mechanic
        return "Mechanic: TUID=" + this.getTUID() + " Name=" + strName + " Hourly_Payrate=" + intHourly_payrate;
    }
}