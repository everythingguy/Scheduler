
/**
 * Filename: Customer.java
 * 
 * Project: Homework3
 * 
 * Author: Kevin Gyorick
 * 
 * Date: 10/09/2021
 * 
 * File Purpose:    This file represents a customer object and acts as middleware 
 *                  between the database and the application for customers.
 * 
 * Program Purpose: The program schedules appointments for a mechanic shop. 
 *                  Appointments are scheduled first come, first served as 
 *                  long as the job can be completed the same day it is started 
 *                  given the current schedule. Mechanics with lower bay numbers 
 *                  assigned have higher priority when multiple mechanics are available
 */
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class Customer extends Entity {
    String strName; // the customer's name

    /**
     * Constructs a customer using their name
     * 
     * @param strName the name of the new customer
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public Customer(String strName) throws SQLException {
        // construct a customer w/ a name
        super();
        this.strName = strName;
    }

    /**
     * Constructs a customer using their id and name
     * 
     * @param TUID    the id of the customer
     * @param strName the name of the customer
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public Customer(int TUID, String strName) throws SQLException {
        // construct a customer w/ a id and name
        super(TUID);
        this.strName = strName;
    }

    /**
     * Gets all customers from the database
     * 
     * @return a array of customers from the database
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public static Customer[] getCustomers() throws SQLException {
        // make sure the database connection is established and built
        initializeDatabase();
        // return the customers in the database
        return Database.getCustomers();
    }

    /**
     * Gets a customer by id from the provided array
     * 
     * @param TUID         the id of the customer that needs to be found
     * @param arrCustomers the array of customers
     * @return a customer with the provided id
     * @throws NoSuchElementException throws if no customer in the array has the
     *                                provided id
     */
    public static Customer getCustomer(int TUID, Customer[] arrCustomers) throws NoSuchElementException {
        // foreach customer
        for (Customer currCustomer : arrCustomers) {
            // if the ids match return that customer
            if (currCustomer.getTUID() == TUID)
                return currCustomer;
        }
        // no customer found by the provided id throw error
        throw new NoSuchElementException("No customer found with the TUID " + TUID);
    }

    /**
     * Creates a new customer with the provided name in the database
     * 
     * @param strCustomerName the name of the new customer
     * @param arrCustomers    the array of customers
     * @return the updated array of customers
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public static Customer[] createCustomer(String strCustomerName, Customer[] arrCustomers) throws SQLException {
        // make sure the database connection is established and built
        initializeDatabase();
        // foreach customer in the array
        for (Customer currentCustomer : arrCustomers) {
            // if the name already exists in the databse throw error
            if (currentCustomer.strName.equals(strCustomerName)) {
                throw new SQLException("Cannot insert customer (" + strCustomerName
                        + ") with the same name since vehicles are inserted based on customer name");
            }
        }
        // construct a new customer with the provided name
        Customer newCustomer = new Customer(strCustomerName);
        // add the customer to the database
        Database.addCustomer(newCustomer);
        // return a updated array of customers
        return Customer.getCustomers();
    }

    /**
     * @return a string representation of a customer
     */
    @Override
    public String toString() {
        // a string representation of a customer
        return "Customer: TUID=" + this.getTUID() + " Name=" + strName;
    }
}
