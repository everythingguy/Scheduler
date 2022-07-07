
/**
 * Filename: DB.java
 * 
 * Project: Homework3
 * 
 * Author: Kevin Gyorick
 * 
 * Date: 10/09/2021
 * 
 * File Purpose:    This file handles the database connection and queries. 
 *                  This file creates all the tables, creates select/update/insert
 *                  queries, and deletes the database.
 * 
 * Program Purpose: The program schedules appointments for a mechanic shop. 
 *                  Appointments are scheduled first come, first served as 
 *                  long as the job can be completed the same day it is started 
 *                  given the current schedule. Mechanics with lower bay numbers 
 *                  assigned have higher priority when multiple mechanics are available
 */

import java.io.File;
import java.sql.*;

public class DB {
    static final boolean DEBUG_OUTPUT = false; // whether or not to display debug output for the creation of the
                                               // database
    static final String DATABASE_NAME = "SQLiteTest1.db"; // the name to give the database file
    static Connection conn = null; // the connection to the database

    /**
     * construct a database and get a new connection
     * 
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      connected to
     */
    public DB() throws SQLException {
        if (conn == null)
            conn = getConnection();
    }

    /**
     * Gets a connection to the SQLite database using the jdbc driver
     * 
     * @return the connection to the database
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      connected to
     */
    private Connection getConnection() throws SQLException {
        // open a connection to the sqlite database using the jdbc driver
        conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
        // return the connection
        return conn;
    }

    /**
     * Gets the row count of the table with the provided name
     * 
     * @param strTableName the name of the table to get the row count of
     * @return the row count of the provided table
     * @throws SQLException throws if the table name is not valid or there is a
     *                      problem with the connection
     */
    private int getTableRowCount(String strTableName) throws SQLException {
        // sql statement
        Statement state;
        // sql query result
        ResultSet res;

        // create a new sql statement using the database connection
        state = conn.createStatement();
        // execute a sql query on the database to get the row count of the provided
        // table
        res = state.executeQuery("SELECT COUNT(*) FROM " + strTableName);

        // get the next and only result
        res.next();
        // return the result as a integer
        return res.getInt(1);
    }

    /**
     * Closes the database connection and deletes the database file
     * 
     * @throws SQLException throws if there is a problem closing the connection
     */
    public void dropDatabase() throws SQLException {
        // close the database connection
        conn.close();
        // delete the db file
        new File(DATABASE_NAME).delete();
    }

    /**
     * Builds the database by creating the mechanic, customer, vehicle, service,
     * bay, and schedule tables. Prepopulates the mechanic, service, and bay table
     * 
     * @param DBExists whether or not the database already exist
     * @throws SQLException throws if there is a query error, or problem with the
     *                      connection
     */
    public void buildDatabase(boolean DBExists) throws SQLException {
        // sql statement
        Statement state;
        // sql query result
        ResultSet res;

        // if the database does not exist
        if (!DBExists) {
            // set the database's existence to true
            DBExists = true;

            // create a new sql statement
            state = conn.createStatement();
            // excute a sql query to check the master table for the existence of the
            // customer table
            res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='Customers_Table'");
            // if no result
            if (!res.next()) {
                // build the tables
                buildMechanicsTable();
                buildCustomerTable();
                buildVehicleTable();
                buildServicesTable();
                buildBaysTable();
                buildScheduleTable();

                // add the mechanics to the database
                addMechanic(new Mechanic("Sue", 10.00));
                addMechanic(new Mechanic("Steve", 9.00));

                // add the shop services to the database
                addService(new Service("Oil Change", 30));
                addService(new Service("Tire Replacement", 60));
                addService(new Service("Brakes", 180));
                addService(new Service("Transmission Filter Replacement", 120));
                addService(new Service("Cooling System Cleaning", 240));

                // add the shop bays to the database
                addBay(new Bay(1));
                addBay(new Bay(2));

                // tell the user the database was created and prepopulated
                System.out.println("Database prepopulated");
            }
        }
    }

    /**
     * Builds the customer table
     * 
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    private void buildCustomerTable() throws SQLException {
        // sql statement
        Statement state;

        if (DEBUG_OUTPUT)
            System.out.println("Build the CUSTOMER table");
        // create a new sql statement using the connection
        state = conn.createStatement();
        // execute a sql query to create the customer table
        state.executeUpdate("CREATE TABLE Customers_Table(TUID INTEGER,Name VARCHAR(60),PRIMARY KEY (TUID));");
    }

    /**
     * Adds a customer to the database
     * 
     * @param newCustomer the new customer to add to the database
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    public void addCustomer(Customer newCustomer) throws SQLException {
        // sql statment
        PreparedStatement prep;

        if (DEBUG_OUTPUT)
            System.out.println("Add " + newCustomer.strName + " to USER table");
        // create query template for inserting a new customer into the customer table
        prep = conn.prepareStatement("INSERT INTO Customers_Table (Name) VALUES (?);");
        // set the customer name in the template
        prep.setString(1, newCustomer.strName);
        // execute the template with the parameters
        prep.execute();
    }

    /**
     * Gets all customers from the database
     * 
     * @return a array of customers from the database
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    public Customer[] getCustomers() throws SQLException {
        // sql statement
        Statement state;
        // sql query result
        ResultSet res;
        // get the table row count of the customer table
        int count = getTableRowCount("Customers_Table");
        // index starting at zero
        int i = 0;

        // create a new sql statement using the connection
        state = conn.createStatement();
        // execute sql query to select all customers from the database
        res = state.executeQuery("SELECT TUID, Name FROM Customers_Table");

        // create a array to store the customers in
        Customer[] arrCustomers = new Customer[count];

        // while there is another result
        while (res.next()) {
            // store the current result in the array
            arrCustomers[i] = new Customer(res.getInt("TUID"), res.getString("Name"));
            // increment index
            i++;
        }

        // return the array of customers
        return arrCustomers;
    }

    /**
     * Builds the mechanic table
     * 
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    private void buildMechanicsTable() throws SQLException {
        // sql statement
        Statement state;

        if (DEBUG_OUTPUT)
            System.out.println("Build MECHANICS table");
        // create a new sql statement using the connection
        state = conn.createStatement();
        // execute sql query to create the mechanics table
        state.executeUpdate(
                "CREATE TABLE Mechanics_Table(TUID INTEGER,Mechanic_Name VARCHAR(60),Hourly_Payrate DECIMAL(5,2),PRIMARY KEY (TUID));");
    }

    /**
     * Adds a new mechanic to the database
     * 
     * @param newMechanic the new mechanic to add to the database
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    public void addMechanic(Mechanic newMechanic) throws SQLException {
        // sql statement template
        PreparedStatement prep;

        if (DEBUG_OUTPUT)
            System.out.println("Add " + newMechanic.strName + " to MECHANIC table");
        // create a template for inserting a mechanic into the mechanics table
        prep = conn.prepareStatement("INSERT INTO Mechanics_Table (Mechanic_Name,Hourly_Payrate) VALUES (?,?);");
        // set the mechanic name in the template
        prep.setString(1, newMechanic.strName);
        // set the payrate of the mechanic in the template
        prep.setDouble(2, newMechanic.intHourly_payrate);
        // excute the sql statement
        prep.execute();
    }

    /**
     * Gets all mechanics from the database
     * 
     * @return a array of all mechanics in the databse
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    public Mechanic[] getMechanics() throws SQLException {
        // sql statement
        Statement state;
        // sql query result
        ResultSet res;
        // get the row count of the mechanics table
        int count = getTableRowCount("Mechanics_Table");
        // index
        int i = 0;

        // create a new sql statement using the connection
        state = conn.createStatement();
        // execute a sql query to select all mechanics in the database
        res = state.executeQuery("SELECT TUID, Mechanic_Name, Hourly_Payrate FROM Mechanics_Table");

        // array for storing the mechanics
        Mechanic[] arrMechanics = new Mechanic[count];

        // while there is another result
        while (res.next()) {
            // add the current mechanic to the array
            arrMechanics[i] = new Mechanic(res.getInt("TUID"), res.getString("Mechanic_Name"),
                    res.getDouble("Hourly_Payrate"));
            // increment the index
            i++;
        }

        // return the array of mechanics
        return arrMechanics;
    }

    /**
     * Builds the vehicle table
     * 
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    private void buildVehicleTable() throws SQLException {
        // sql statement
        Statement state;

        if (DEBUG_OUTPUT)
            System.out.println("Build VEHICLE table");
        // create a new sql statement using the connection
        state = conn.createStatement();
        // execute a sql query to create the vehicle table
        state.executeUpdate(
                "CREATE TABLE Vehicle_Table(TUID INTEGER,Customer_TUID INTEGER,Vehicle_Description VARCHAR(60),PRIMARY KEY (TUID));");
    }

    /**
     * Adds a new vehicle to the database
     * 
     * @param newVehicle the new vehicle to add to the database
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    public void addVehicle(Vehicle newVehicle) throws SQLException {
        // sql statment
        PreparedStatement prep;

        if (DEBUG_OUTPUT)
            System.out.println("Add a record to VEHICLE table");
        // create a template statement for inserting a new vehicle into the database
        prep = conn.prepareStatement("INSERT INTO Vehicle_Table (Customer_TUID,Vehicle_Description) VALUES (?,?);");
        // set the owner of the vehicle in the template
        prep.setInt(1, newVehicle.getCustomerTUID());
        // set the vehicle description in the template
        prep.setString(2, newVehicle.strVehicle_Description);
        // execute the sql statement
        prep.execute();
    }

    /**
     * Gets all vehicles from the database
     * 
     * @return a array of all vehicles in the database
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    public Vehicle[] getVehicles() throws SQLException {
        // sql statement
        Statement state;
        // sql query result
        ResultSet res;
        // get the row count of the vehicle table
        int count = getTableRowCount("Vehicle_Table");
        // index
        int i = 0;

        // create a new sql statement using the connection
        state = conn.createStatement();
        // execute sql query to get all vehicles from the database
        res = state.executeQuery("SELECT TUID, Customer_TUID, Vehicle_Description FROM Vehicle_Table");

        // array for storing the vehicles from the database
        Vehicle[] arrVehicles = new Vehicle[count];

        // while there is another result
        while (res.next()) {
            // add the current vehicle to the array
            arrVehicles[i] = new Vehicle(res.getInt("TUID"), res.getInt("Customer_TUID"),
                    res.getString("Vehicle_Description"));
            // increment index
            i++;
        }

        // return the array of vehicles
        return arrVehicles;
    }

    /**
     * Builds the service table in the database
     * 
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    private void buildServicesTable() throws SQLException {
        // sql statement
        Statement state;

        if (DEBUG_OUTPUT)
            System.out.println("Build SERVICES table");
        // create a new sql statement using the connection
        state = conn.createStatement();
        // execute a sql query to create the service table
        state.executeUpdate(
                "CREATE TABLE Services_Table(TUID INTEGER,Service_Name VARCHAR(60),Service_Time INTEGER,PRIMARY KEY (TUID));");
    }

    /**
     * Adds a new service to the database
     * 
     * @param newService the new service to add to the database
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    public void addService(Service newService) throws SQLException {
        // sql statement
        PreparedStatement prep;

        if (DEBUG_OUTPUT)
            System.out.println("Add " + newService.strService_Name + " to SERVICES table");
        // create a new sql template statement using the connection
        prep = conn.prepareStatement("INSERT INTO Services_Table (Service_Name,Service_Time) VALUES (?,?);");
        // set the service name in the template
        prep.setString(1, newService.strService_Name);
        // set the service length in the template
        prep.setInt(2, newService.intService_Time);
        // execute the sql statement
        prep.execute();
    }

    /**
     * Gets all services from the database
     * 
     * @return a array of all services in the database
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    public Service[] getServices() throws SQLException {
        // sql statement
        Statement state;
        // sql results
        ResultSet res;
        // get the row count of the services table
        int count = getTableRowCount("Services_Table");
        // index
        int i = 0;

        // create a new sql statement using the connection
        state = conn.createStatement();
        // execute query to get all services from the database
        res = state.executeQuery("SELECT TUID, Service_Name, Service_Time FROM Services_Table");

        // create a array for storing the services
        Service[] arrServices = new Service[count];

        // while there is a result left
        while (res.next()) {
            // add the current service to the array
            arrServices[i] = new Service(res.getInt("TUID"), res.getString("Service_Name"), res.getInt("Service_Time"));
            // increment the index
            i++;
        }

        // return the array of services
        return arrServices;
    }

    /**
     * Builds the bay table in the database
     * 
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    private void buildBaysTable() throws SQLException {
        // sql statement
        Statement state;

        if (DEBUG_OUTPUT)
            System.out.println("Build BAYS table");
        // create a new sql statement using the connection
        state = conn.createStatement();
        // execute a sql query to create the bays table
        state.executeUpdate("CREATE TABLE Bays_Table(TUID INTEGER,Mechanic_TUID INTEGER,PRIMARY KEY (TUID));");
    }

    /**
     * Adds a new bay to the database
     * 
     * @param newBay the new bay to add to the database
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    public void addBay(Bay newBay) throws SQLException {
        // sql statement
        PreparedStatement prep;

        if (DEBUG_OUTPUT)
            System.out.println("Add a record to BAYS table");
        // create sql statement template using the database connection
        prep = conn.prepareStatement("INSERT INTO Bays_Table (Mechanic_TUID) VALUES (?);");
        // insert the bay id into the template
        prep.setInt(1, newBay.Mechanic_TUID);
        // execute the sql statement
        prep.execute();
    }

    /**
     * Gets all bays in the database
     * 
     * @return a array of all bays in the database
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    public Bay[] getBays() throws SQLException {
        // sql statement
        Statement state;
        // sql result
        ResultSet res;
        // get the row count of the bays table
        int count = getTableRowCount("Bays_Table");
        // index
        int i = 0;

        // create a new sql statement using the connection
        state = conn.createStatement();
        // execute query to get all bays from the database
        res = state.executeQuery("SELECT TUID, Mechanic_TUID FROM Bays_Table");

        // create a array to store the bays in
        Bay[] arrBays = new Bay[count];

        // while there is a result left
        while (res.next()) {
            // add the current bay to the array
            arrBays[i] = new Bay(res.getInt("TUID"), res.getInt("Mechanic_TUID"));
            // increment the index
            i++;
        }

        // return the array of bays
        return arrBays;
    }

    /**
     * Builds the schedule table
     * 
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    private void buildScheduleTable() throws SQLException {
        // sql statement
        Statement state;

        if (DEBUG_OUTPUT)
            System.out.println("Build SCHEDULE table");
        // create a new sql statement using the connection
        state = conn.createStatement();
        // execute query to create the schedule table
        state.executeUpdate(
                "CREATE TABLE Schedule_Table(TUID INTEGER,Vehicles_TUID INTEGER,Bays_TUID INTEGER,Service_TUID INTEGER,Appointment_Start_Time DATETIME,Appointment_End_Time DATETIME,PRIMARY KEY (TUID));");
    }

    /**
     * Adds a new schedule to the database
     * 
     * @param newSchedule the new schedule to add to the database
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    public void addSchedule(Schedule newSchedule) throws SQLException {
        // sql statement
        PreparedStatement prep;

        if (DEBUG_OUTPUT)
            System.out.println("Add a record to SCHEDULE table");
        // create a new sql template statement using the connection
        prep = conn.prepareStatement(
                "INSERT INTO Schedule_Table (Vehicles_TUID,Bays_TUID,Service_TUID,Appointment_Start_Time,Appointment_End_Time) VALUES (?,?,?,?,?);");
        // insert the vehicle id into the template
        prep.setInt(1, newSchedule.Vehicles_TUID);
        // insert the bay id into the template
        prep.setInt(2, newSchedule.Bays_TUID);
        // insert the service id into the template
        prep.setInt(3, newSchedule.Service_TUID);
        // insert the appointment start time into the template
        prep.setTimestamp(4, newSchedule.Appointment_Start_Time);
        // insert the appointment end time into the template
        prep.setTimestamp(5, newSchedule.Appointment_End_Time);
        // execute the query
        prep.execute();
    }

    /**
     * Gets all schedules from the database
     * 
     * @return a array of all schedules in the database
     * @throws SQLException throws if there is a query error or problem with the
     *                      connection
     */
    public Schedule[] getSchedule() throws SQLException {
        // sql statement
        Statement state;
        // sql result
        ResultSet res;
        // get the row count of the schedule table
        int count = getTableRowCount("Schedule_Table");
        // index
        int i = 0;

        // create a new sql statement using the database connection
        state = conn.createStatement();
        // execute query to get all schedules from the database
        res = state.executeQuery(
                "SELECT TUID, Vehicles_TUID, Bays_TUID, Service_TUID, Appointment_Start_Time, Appointment_End_Time FROM Schedule_Table");

        // array for storing the schedules
        Schedule[] arrSchedule = new Schedule[count];

        // while there is a result left
        while (res.next()) {
            // add the current schedule to the array
            arrSchedule[i] = new Schedule(res.getInt("TUID"), res.getInt("Vehicles_TUID"), res.getInt("Bays_TUID"),
                    res.getInt("Service_TUID"), res.getTimestamp("Appointment_Start_Time"),
                    res.getTimestamp("Appointment_End_Time"));
            // increment the result
            i++;
        }

        // return the array of schedules
        return arrSchedule;
    }
}
