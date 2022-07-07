
/**
 * Filename: Bay.java
 * 
 * Project: Homework3
 * 
 * Author: Kevin Gyorick
 * 
 * Date: 10/09/2021
 * 
 * File Purpose:    This file represents a bay object and acts as middleware 
 *                  between the database and the application for bays.
 * 
 * Program Purpose: The program schedules appointments for a mechanic shop. 
 *                  Appointments are scheduled first come, first served as 
 *                  long as the job can be completed the same day it is started 
 *                  given the current schedule. Mechanics with lower bay numbers 
 *                  assigned have higher priority when multiple mechanics are available
 */
import java.sql.SQLException;

public class Bay extends Entity {
    // the mechanic's id
    int Mechanic_TUID;

    /**
     * Constructs a new bay using a mechanic id
     * 
     * @param Mechanic_TUID the id of the mechanic assigned to the bay
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    Bay(int Mechanic_TUID) throws SQLException {
        // construct a bay w/ a mechanic id
        super();
        this.Mechanic_TUID = Mechanic_TUID;
    }

    /**
     * Constructs a new bay using a bay id and mechanic id
     * 
     * @param TUID          the id of the bay
     * @param Mechanic_TUID the id of the mechanic assigned to the bay
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    Bay(int TUID, int Mechanic_TUID) throws SQLException {
        // construct a bay w/ a id and mechanic id
        super(TUID);
        this.Mechanic_TUID = Mechanic_TUID;
    }

    /**
     * Gets all bays in the database
     * 
     * @return a array of all bays in the database
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public static Bay[] getBays() throws SQLException {
        // make sure the database connection is established and built
        initializeDatabase();
        // return the bays in the database
        return Database.getBays();
    }

    /**
     * Finds the bay that is assigned to the provided mechanic
     * 
     * @param currMechanic the mechanic that is assigned to the bay
     * @return the bay assigned to the current mechanic
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public static Bay getMechanicsBay(Mechanic currMechanic) throws SQLException {
        // foreach bay in the database
        for (Bay currBay : getBays()) {
            // if the bay belongs to the mechanic return the bay
            if (currBay.Mechanic_TUID == currMechanic.getTUID())
                return currBay;
        }
        // no bay belonging to the mechanic throw error
        throw new SQLException("Mechanic " + currMechanic.strName + " does not have a bay to work in");
    }

    /**
     * @return a string representation of the bay
     */
    @Override
    public String toString() {
        // a string representation of the bay
        return "Bay: TUID=" + this.getTUID() + " Mechanic_TUID=" + Mechanic_TUID;
    }
}
