
/**
 * Filename: Vehicle.java
 * 
 * Project: Homework3
 * 
 * Author: Kevin Gyorick
 * 
 * Date: 10/09/2021
 * 
 * File Purpose:    This file represents a vehicle object and acts as middleware 
 *                  between the database and the application for vehicles.
 * 
 * Program Purpose: The program schedules appointments for a mechanic shop. 
 *                  Appointments are scheduled first come, first served as 
 *                  long as the job can be completed the same day it is started 
 *                  given the current schedule. Mechanics with lower bay numbers 
 *                  assigned have higher priority when multiple mechanics are available
 */
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class Vehicle extends Entity {
    private int Customer_TUID; // the vehicle owner's id
    String strVehicle_Description; // a description of the vehicle

    /**
     * construct a vehicle with a customer id, and description
     * 
     * @param Customer_TUID          the id of the owner of the vehicle
     * @param strVehicle_Description the description of the vehicle
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public Vehicle(int Customer_TUID, String strVehicle_Description) throws SQLException {
        super();
        this.Customer_TUID = Customer_TUID;
        this.strVehicle_Description = strVehicle_Description;
    }

    /**
     * construct a vehicle with a id, customer id, and description
     * 
     * @param TUID                   the id of the vehicle
     * @param Customer_TUID          the id of the owner of the vehicle
     * @param strVehicle_Description the description of the vehicle
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public Vehicle(int TUID, int Customer_TUID, String strVehicle_Description) throws SQLException {
        super(TUID);
        this.Customer_TUID = Customer_TUID;
        this.strVehicle_Description = strVehicle_Description;
    }

    /**
     * get the vehicle owner's id
     * 
     * @return the id of the owner of the vehicle
     */
    public int getCustomerTUID() {
        return this.Customer_TUID;
    }

    /**
     * Get the owner of the vehicle
     * 
     * @param arrCustomers the array of customers from the database
     * @return a customer object representing the owner of the vehicle
     * @throws NoSuchElementException throws if no customer is found with the
     *                                owner's id
     */
    public Customer getOwner(Customer[] arrCustomers) throws NoSuchElementException {
        // get the owner's information
        // foreach customer
        for (Customer currCustomer : arrCustomers) {
            // if the owner id matches the current customer
            if (currCustomer.getTUID() == this.Customer_TUID)
                // return the owner's info
                return currCustomer;
        }
        // no owner found for the vehicle throw error
        throw new NoSuchElementException("No owner found, no customer has the TUID=" + this.Customer_TUID);
    }

    /**
     * Creates a new vehicle
     * 
     * @param strCustomerName        the name of the vehicle's owner
     * @param strVehicle_Description the description of the vehicle
     * @param arrCustomers           the array of customers from the database
     * @param arrVehicles            the array of vehicles from the database
     * @return the updated array of vehicles from the database
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public static Vehicle[] createVehicle(String strCustomerName, String strVehicle_Description,
            Customer[] arrCustomers, Vehicle[] arrVehicles) throws SQLException {
        // foreach customer
        for (Customer currentCustomer : arrCustomers) {
            // if the current customer's name matches the provided name
            if (currentCustomer.strName.equals(strCustomerName)) {
                // create the vehicle entry with the owner's id
                return createVehicle(currentCustomer.getTUID(), strVehicle_Description, arrVehicles);
            }
        }
        // no customer found with the provided name throw error
        throw new SQLException("Customer " + strCustomerName + " not in the database");
    }

    /**
     * Creates a new vehicle
     * 
     * @param intCustomerTUID        the id of the vehicle's owner
     * @param strVehicle_Description the description of the vehicle
     * @param arrVehicles            the array of vehicles from the database
     * @return the updated array of vehicles from the database
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public static Vehicle[] createVehicle(int intCustomerTUID, String strVehicle_Description, Vehicle[] arrVehicles)
            throws SQLException {
        // initialize the database
        initializeDatabase();
        // foreach vehicle in the array
        for (Vehicle currVehicle : arrVehicles) {
            // if the owner id matches the provided customer id and the vehicle descriptions
            // match
            if (currVehicle.Customer_TUID == intCustomerTUID
                    && currVehicle.strVehicle_Description.equals(strVehicle_Description))
                // throw error, there is no way to tell this new entry appart from an existing
                // entry
                throw new SQLException(
                        "Cannot insert new vehicle with the same owner and description, no way to tell them apart Customer_TUID="
                                + intCustomerTUID + ", Vehicle_Description=" + strVehicle_Description);
        }
        // create the new vehicle
        Vehicle newVehicle = new Vehicle(intCustomerTUID, strVehicle_Description);
        // add the new vehicle to the database
        Database.addVehicle(newVehicle);
        // return the updated array of vehicles
        return Vehicle.getVehicles();
    }

    /**
     * Get a vehicle by id from the provided array
     * 
     * @param TUID        the id of the requested vehicle
     * @param arrVehicles the array of vehicles from the database
     * @return a vehicle object representing the requested vehicle
     * @throws NoSuchElementException throws if no vehicle is found in the array
     *                                with the provided id
     */
    public static Vehicle getVehicle(int TUID, Vehicle[] arrVehicles) throws NoSuchElementException {
        // get vehicle by id
        // foreach vehicle in the array
        for (Vehicle currVehicle : arrVehicles) {
            // if the ids match return the vehicle
            if (currVehicle.getTUID() == TUID)
                return currVehicle;
        }
        // no vehicle found by the provided id throw error
        throw new NoSuchElementException("No vehicle found with the TUID " + TUID);
    }

    /**
     * Get a vehicle by owner name and description
     * 
     * @param strCustomerName        the name of the owner of the vehicle
     * @param strVehicle_Description the description of the vehicle
     * @param arrCustomers           the array of customers from the database
     * @param arrVehicles            the array of vehicles from the database
     * @return a vehicle object representing the requested vehicle
     * @throws NoSuchElementException throws if no customer has the owner's name or
     *                                if no vehicle has the provided description
     */
    public static Vehicle getVehicle(String strCustomerName, String strVehicle_Description, Customer[] arrCustomers,
            Vehicle[] arrVehicles) throws NoSuchElementException {
        // the owner's id
        int intCustomerTUID = -1;
        // foreach customer
        for (Customer currentCustomer : arrCustomers) {
            // if the names match set the owner id
            if (currentCustomer.strName.equals(strCustomerName)) {
                intCustomerTUID = currentCustomer.getTUID();
                break;
            }
        }
        // if owner not found throw error
        if (intCustomerTUID == -1)
            throw new NoSuchElementException("No user by the name of " + strCustomerName + " found");
        // foreach vehicle
        for (Vehicle currentVehicle : arrVehicles) {
            // if the owner id and description match return the vehicle
            if (currentVehicle.getCustomerTUID() == intCustomerTUID
                    && currentVehicle.strVehicle_Description.equals(strVehicle_Description)) {
                return currentVehicle;
            }
        }
        // no vehicle found with the provided owner and description throw error
        throw new NoSuchElementException("No vehicle found where Customer_TUID=" + intCustomerTUID
                + " and Vehicle_Description=" + strVehicle_Description);
    }

    /**
     * Gets all vehicles in the database
     * 
     * @return a array of vehicles in the database
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public static Vehicle[] getVehicles() throws SQLException {
        // initialize the database
        initializeDatabase();
        // return a array of vehicles in the database
        return Database.getVehicles();
    }

    /**
     * Gets a string representation of a vehicle
     * 
     * @return a string representation of a vehicle
     */
    @Override
    public String toString() {
        // a string representation of a vehicle
        return "Vehicle: TUID=" + this.getTUID() + " Customer_TUID=" + Customer_TUID + " Vehicle_Description="
                + strVehicle_Description;
    }
}
