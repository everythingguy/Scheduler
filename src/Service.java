
/**
 * Filename: Service.java
 * 
 * Project: Homework3
 * 
 * Author: Kevin Gyorick
 * 
 * Date: 10/09/2021
 * 
 * File Purpose:    This file represents a service object and acts as middleware 
 *                  between the database and the application for services.
 * 
 * Program Purpose: The program schedules appointments for a mechanic shop. 
 *                  Appointments are scheduled first come, first served as 
 *                  long as the job can be completed the same day it is started 
 *                  given the current schedule. Mechanics with lower bay numbers 
 *                  assigned have higher priority when multiple mechanics are available
 */
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class Service extends Entity {
    String strService_Name; // the name of the service
    int intService_Time; // the length of the service in minutes

    /**
     * construct a new service using a service name, and length
     * 
     * @param strService_Name the name of the new service
     * @param intService_Time the time length in minutes of the new service
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public Service(String strService_Name, int intService_Time) throws SQLException {
        super();
        this.strService_Name = strService_Name;
        this.intService_Time = intService_Time;
    }

    /**
     * construct a new service using a id, service name, and length
     * 
     * @param TUID            the id of the service
     * @param strService_Name the name of the service
     * @param intService_Time the length in minutes of the service
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public Service(int TUID, String strService_Name, int intService_Time) throws SQLException {
        super(TUID);
        this.strService_Name = strService_Name;
        this.intService_Time = intService_Time;
    }

    /**
     * get a service from a array by id
     * 
     * @param TUID        the id of the service
     * @param arrServices the array of services from the database
     * @return the service with the provided id
     * @throws NoSuchElementException throws if no service has the provided id
     */
    public static Service getService(int TUID, Service[] arrServices) throws NoSuchElementException {
        // foreach service
        for (Service currentService : arrServices) {
            // if the id matches return the service
            if (currentService.getTUID() == TUID)
                return currentService;
        }
        // no service found for the provided id throw a error
        throw new NoSuchElementException("No service found where TUID=" + TUID);
    }

    /**
     * Get service by name
     * 
     * @param strService_Name the name of the service
     * @param arrServices     the array of services from the database
     * @return the service with the provided name
     * @throws NoSuchElementException throws if no service has the provided name
     */
    public static Service getService(String strService_Name, Service[] arrServices) throws NoSuchElementException {
        // foreach service
        for (Service currentService : arrServices) {
            // if the service names match return the service
            if (currentService.strService_Name.equals(strService_Name))
                return currentService;
        }
        // no service by that name throw a error
        throw new NoSuchElementException("No service found where Service_Name=" + strService_Name);
    }

    /**
     * Get all services from the database
     * 
     * @return a array of all services in the database
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public static Service[] getServices() throws SQLException {
        // initialize the database
        initializeDatabase();
        // return a array of services that are in the database
        return Database.getServices();
    }

    /**
     * Gets how many miliseconds are in a single timeslot
     * 
     * @param GCF the GCF between service lengths
     * @return the number of miliseconds in a single timeslot
     */
    public static int millisecondsTimeslot(int GCF) {
        // get the length of a timeslot in milliseconds
        return DateUtil.MILLISECONDS_MINUTE * GCF;
    }

    /**
     * Gets the GCF between two number
     * 
     * @param num1 the first number
     * @param num2 the second number
     * @return the GCF between the two provided numbers
     */
    private static int GCF(int num1, int num2) {
        // get the greatest common factor between two numbers
        // if the first number is zero then the GCF is the other number
        if (num1 == 0)
            return num2;
        // recursion with number 2 mod number 1, number 1
        return GCF(num2 % num1, num1);
    }

    /**
     * Gets the GCF between service lengths
     * 
     * @param arrServices a array of services
     * @return the GCF between the lengths of the provided services
     */
    public static int GCF(Service[] arrServices) {
        // the GCF
        int intGCF = 0;
        // foreach service
        for (Service currentService : arrServices) {
            // get the greatest common factor between the current GCF and the current
            // service
            intGCF = GCF(intGCF, currentService.intService_Time);
            // if lowest possible GCF return it
            if (intGCF == 1)
                break;
        }

        // return the GCF
        return intGCF;
    }

    /**
     * Gets a string representation of a service
     * 
     * @return a string representation of a service
     */
    @Override
    public String toString() {
        // a string representation of a service
        return "Service: TUID=" + this.getTUID() + " Service_Name=" + strService_Name + " Service_Time="
                + intService_Time;
    }
}
