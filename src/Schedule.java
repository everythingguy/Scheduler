
/**
 * Filename: Schedule.java
 * 
 * Project: Homework3
 * 
 * Author: Kevin Gyorick
 * 
 * Date: 10/09/2021
 * 
 * File Purpose:    This file represents a schedule object and acts as middleware 
 *                  between the database and the application for schedules.
 * 
 * Program Purpose: The program schedules appointments for a mechanic shop. 
 *                  Appointments are scheduled first come, first served as 
 *                  long as the job can be completed the same day it is started 
 *                  given the current schedule. Mechanics with lower bay numbers 
 *                  assigned have higher priority when multiple mechanics are available
 */
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Schedule extends Entity {
    private static MechanicSchedule[] allMechanicSchedules = null;

    int Vehicles_TUID; // the id of the vehicle being worked on during this appointment
    int Bays_TUID; // the bay to be used during this appointment
    int Service_TUID; // the service to be provided during this appointment
    Timestamp Appointment_Start_Time; // the start time of this appointment
    Timestamp Appointment_End_Time; // the end time of this appointment

    /**
     * construct a schedule with a vehicle id, bay id, service id, start time, and
     * end time
     * 
     * @param Vehicles_TUID          the id of the vehicle that will be worked on
     *                               during the appointment
     * @param Bays_TUID              the id of the bay where the work will be
     *                               conducted
     * @param Service_TUID           the id of the service to provide during this
     *                               appointment
     * @param Appointment_Start_Time a timestamp of when the appointment will start
     * @param Appointment_End_Time   a timestamp of when the appointment will end
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public Schedule(int Vehicles_TUID, int Bays_TUID, int Service_TUID, Timestamp Appointment_Start_Time,
            Timestamp Appointment_End_Time) throws SQLException {
        super();
        this.Vehicles_TUID = Vehicles_TUID;
        this.Bays_TUID = Bays_TUID;
        this.Service_TUID = Service_TUID;
        this.Appointment_Start_Time = Appointment_Start_Time;
        this.Appointment_End_Time = Appointment_End_Time;
    }

    /**
     * construct a schedule with a id, vehicle id, bay id, service id, start time,
     * and end time
     * 
     * @param TUID                   the id of this schedule
     * @param Vehicles_TUID          the id of the vehicle that will be worked on
     *                               during the appointment
     * @param Bays_TUID              the id of the bay where the work will be
     *                               conducted
     * @param Service_TUID           the id of the service to provide during this
     *                               appointment
     * @param Appointment_Start_Time a timestamp of when the appointment will start
     * @param Appointment_End_Time   a timestamp of when the appointment will end
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public Schedule(int TUID, int Vehicles_TUID, int Bays_TUID, int Service_TUID, Timestamp Appointment_Start_Time,
            Timestamp Appointment_End_Time) throws SQLException {
        super(TUID);
        this.Vehicles_TUID = Vehicles_TUID;
        this.Bays_TUID = Bays_TUID;
        this.Service_TUID = Service_TUID;
        this.Appointment_Start_Time = Appointment_Start_Time;
        this.Appointment_End_Time = Appointment_End_Time;
    }

    /**
     * Gets all schedules from the database and sorts them by start time
     * 
     * @param sortedByTime whether to sort the array by time or by id
     * @return a sorted array of all schedules in the database, sorted by start time
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public static Schedule[] getSchedules(boolean sortedByTime) throws SQLException {
        // initialize the database
        initializeDatabase();
        // return all schedules from the database in a array sorted by start time
        if (sortedByTime)
            return Schedule.sortScheduleByTime(Database.getSchedule());
        else
            return Schedule.sortScheduleById(Database.getSchedule());
    }

    /**
     * Saves a new schedule to the database
     * 
     * @param newSchedule the schedule to add to the database
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public static void saveSchedule(Schedule newSchedule) throws SQLException {
        // initialize the database
        initializeDatabase();
        // add the new schedule to the database
        Database.addSchedule(newSchedule);
    }

    /**
     * Creates a new appointment
     * 
     * @param appointmentTUID        if the appointment already exist provide its
     *                               id, otherwise provide -1 This is to allow the
     *                               data structure to be prepopulated on startup
     *                               and is needed to prevent overlap protection
     *                               from going off on the same exact appointment
     * @param strCustomerName        the name of the customer the appointment is for
     * @param strVehicle_Description the description of the customer's vehicle that
     *                               needs the service
     * @param strService_Name        the service requested by the customer
     * @param arrCustomers           a array of customers from the database
     * @param arrVehicles            a array of vehicles from the database
     * @param arrServices            a array of services from the database
     * @param arrBays                a array of bays from the database
     * @param arrMechanics           a array of mechanics from the database
     * @return the updated array of schedules
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    public static Schedule[] createAppointment(int appointmentTUID, String strCustomerName,
            String strVehicle_Description, String strService_Name, Customer[] arrCustomers, Vehicle[] arrVehicles,
            Service[] arrServices, Bay[] arrBays, Mechanic[] arrMechanics) throws SQLException {
        // initialize the database
        initializeDatabase();
        // get the vehicle that is being worked on during this appointment
        Vehicle customerVehicle = Vehicle.getVehicle(strCustomerName, strVehicle_Description, arrCustomers,
                arrVehicles);
        // get the service requested for this appointment
        Service currentService = Service.getService(strService_Name, arrServices);

        // get the GCF between service lengths
        final int GCFServiceTime = Service.GCF(arrServices);
        // if the schedule data strucure has not been created, create it
        if (allMechanicSchedules == null) {
            allMechanicSchedules = new MechanicSchedule[arrMechanics.length];
            for (int i = 0; i < allMechanicSchedules.length; i++) {
                allMechanicSchedules[i] = new MechanicSchedule(GCFServiceTime);
            }
        }

        // the number of timeslots this service requires
        int intSlotsNeeded = currentService.intService_Time / GCFServiceTime;
        // get the earliest possible appointment for the service
        int[] earliest = getEarliestAppointments(allMechanicSchedules, arrMechanics, currentService, GCFServiceTime);
        // the index of the soonest available mechanic for the appointment
        int intMechanicNum = earliest[0];
        // the soonest week for the appointment
        int soonestWeek = earliest[1];
        // the soonest day of the week for the appointment
        int soonestDay = earliest[2];
        // the soonest timeslot in the day for the appointment
        int soonestGCFSlot = earliest[3];

        // calculate timestamps of appointment start and end
        // appointments are scheduled starting next Monday
        // if a timestamp occurs during or after lunch the lunch offset is applied
        Timestamp[] appointmentTime = calcStartEndTimestamps(earliest, currentService, GCFServiceTime);
        Timestamp tsStartDate = appointmentTime[0];
        Timestamp tsEndDate = appointmentTime[1];

        // do not schedule the same car to be worked on at the same time with two
        // mechanics
        // if so, temporarly reserve the starting GCF timeslot with the currently
        // selected mechanic and get the soonest appointments again until no conflict

        // if the appointment has a overlap with another appointment for the same
        // vehicle
        if (hasOverlap(appointmentTUID, customerVehicle, tsStartDate, tsEndDate)) {
            // clone the schedule data structure for temporary appointments
            MechanicSchedule[] cloneSchedule = cloneScheduleStructure(arrMechanics);
            // while there is still a overlap
            do {
                // reserve one GCF slot where the appointment would start if it were a different
                // vehicle
                cloneSchedule[intMechanicNum].reserveSlot(soonestWeek, soonestDay, soonestGCFSlot);
                // get the new earliest possible appointment for the service
                earliest = getEarliestAppointments(cloneSchedule, arrMechanics, currentService, GCFServiceTime);
                // the index of the soonest available mechanic for the appointment
                intMechanicNum = earliest[0];
                // the soonest week for the appointment
                soonestWeek = earliest[1];
                // the soonest day of the week for the appointment
                soonestDay = earliest[2];
                // the soonest timeslot in the day for the appointment
                soonestGCFSlot = earliest[3];

                // calculate timestamps of appointment start and end
                // appointments are scheduled starting next Monday
                // if a timestamp occurs during or after lunch the lunch offset is applied
                appointmentTime = calcStartEndTimestamps(earliest, currentService, GCFServiceTime);
                tsStartDate = appointmentTime[0];
                tsEndDate = appointmentTime[1];
            } while (hasOverlap(appointmentTUID, customerVehicle, tsStartDate, tsEndDate));
        }

        // add the appointment to the schedule data structure, reserving the slots
        // needed
        for (int i = 0; i < intSlotsNeeded; i++) {
            allMechanicSchedules[intMechanicNum].reserveSlot(soonestWeek, soonestDay, soonestGCFSlot + i);
        }

        // get the mechanic's bay number
        Bay currBay = Bay.getMechanicsBay(arrMechanics[intMechanicNum]);

        // save the appointment to the database if the appointment is new, represented
        // by a invalid id
        if (appointmentTUID <= 0)
            saveSchedule(new Schedule(customerVehicle.getTUID(), currBay.getTUID(), currentService.getTUID(),
                    tsStartDate, tsEndDate));

        // return the updated schedule array
        return Schedule.getSchedules(true);
    }

    /**
     * Prepopulates the schedule data structure used for scheduling new appointents
     * with any existing appointments from the database that have yet to happen
     * 
     * @param arrCustomers the array of customers from the database
     * @param arrVehicles  the array of vehicles from the database
     * @param arrServices  the array of services from the database
     * @param arrBays      the array of bays from the database
     * @param arrMechanics the array of mechanics from the database
     * @throws SQLException           throws a SQL exception if the database cannot
     *                                be initialized
     * @throws NoSuchElementException throws if the vehicle, customer, or service is
     *                                not found in the arrays that belong to an
     *                                appointment
     */
    public static Schedule[] prepopulateSchedules(Customer[] arrCustomers, Vehicle[] arrVehicles, Service[] arrServices,
            Bay[] arrBays, Mechanic[] arrMechanics) throws SQLException, NoSuchElementException {

        // get all appointments from the database sorted by id
        Schedule[] allAppointments = Schedule.getSchedules(false);
        // call createAppointment for each future appointment without saving the
        // appointment to the database
        // timestamp for next monday
        Timestamp tsNextMonday = DateUtil.getNextMondaysDate();
        // foreach appointment already in the database
        for (Schedule currAppointment : allAppointments) {
            // if the appointment is in the future starting next monday
            if (currAppointment.Appointment_Start_Time.getTime() >= tsNextMonday.getTime()) {
                // get the appointment's vehicle
                Vehicle currVehicle = Vehicle.getVehicle(currAppointment.Vehicles_TUID, arrVehicles);
                // get the appointment's customer
                Customer currCustomer = Customer.getCustomer(currVehicle.getCustomerTUID(), arrCustomers);
                // get the appointment's service
                Service currService = Service.getService(currAppointment.Service_TUID, arrServices);

                // recreate the appointment in the data structure
                createAppointment(currAppointment.getTUID(), currCustomer.strName, currVehicle.strVehicle_Description,
                        currService.strService_Name, arrCustomers, arrVehicles, arrServices, arrBays, arrMechanics);
            }
        }

        // return the array of appoinments that are in the database
        return allAppointments;
    }

    /**
     * Calculates the start and end timestamps of a reserved timeslot in the
     * schedule data structure data structure
     * 
     * @param earliest       an array containing the earliest week, day, and
     *                       timeslot
     * @param currentService the requested service for this appointment
     * @param GCFServiceTime GCF between service lengths
     * @return an array with the start and end time for the appointment
     */
    private static Timestamp[] calcStartEndTimestamps(int[] earliest, Service currentService, int GCFServiceTime) {
        // the soonest week for the appointment
        int soonestWeek = earliest[1];
        // the soonest day of the week for the appointment
        int soonestDay = earliest[2];
        // the soonest timeslot in the day for the appointment
        int soonestGCFSlot = earliest[3];

        Timestamp tsStartDate = new Timestamp(DateUtil.getNextMondaysDate().getTime()
                + DateUtil.WeekDayTimeslotToMillisecond(soonestWeek, soonestDay, soonestGCFSlot, GCFServiceTime));
        Timestamp tsEndDate = DateUtil.tsOffsetLunch(
                new Timestamp(tsStartDate.getTime() + DateUtil.minutesToMiliseconds(currentService.intService_Time)),
                false);
        tsStartDate = DateUtil.tsOffsetLunch(tsStartDate, true);

        return new Timestamp[] { tsStartDate, tsEndDate };
    }

    /**
     * Gets the earliest appointment with each mechanic for the requested service
     * and returns the earliest one
     * 
     * @param scheduleStructure a data structure to schedule appointments in empty
     *                          timeslots
     * @param arrMechanics      the array of mechanics from the database
     * @param currentService    the service requested
     * @param GCFServiceTime    the GCF between service lengths
     * @return an array with the earliest appointment indexes in the
     *         scheduleStructure
     */
    private static int[] getEarliestAppointments(MechanicSchedule[] scheduleStructure, Mechanic[] arrMechanics,
            Service currentService, int GCFServiceTime) {
        // array for storing the earlist possible appointment times with each mechanic
        int[][] earliestAppointments = new int[arrMechanics.length][2];
        // the number of timeslots this service requires
        int intSlotsNeeded = currentService.intService_Time / GCFServiceTime;

        // foreach mechanic
        for (int i = 0; i < scheduleStructure.length; i++) {
            // get the earlist possible appointment time with each mechanic
            earliestAppointments[i] = earliestAppointment(scheduleStructure[i], currentService, GCFServiceTime,
                    intSlotsNeeded);
        }

        // the index of the soonest available mechanic for the appointment
        int intMechanicNum = 0;
        // the soonest week for the appointment
        int soonestWeek = earliestAppointments[intMechanicNum][0];
        // the soonest day of the week for the appointment
        int soonestDay = earliestAppointments[intMechanicNum][1];
        // the soonest timeslot in the day for the appointment
        int soonestGCFSlot = earliestAppointments[intMechanicNum][2];

        // foreach earlist possible appointment time with each mechanic
        for (int i = 1; i < earliestAppointments.length; i++) {
            // the week of the current appointment candidate
            final int currentWeek = earliestAppointments[i][0];
            // the day of the current appointment candidate
            final int currentDay = earliestAppointments[i][1];
            // the start timeslot of the current appointment candidate
            final int currentGCFSlot = earliestAppointments[i][2];

            // if the current week is earlier than the previous earlist week
            if (currentWeek < soonestWeek) {
                // update the new min
                intMechanicNum = i;
                soonestWeek = earliestAppointments[i][0];
                soonestDay = earliestAppointments[i][1];
                soonestGCFSlot = earliestAppointments[i][2];
            } else if (currentWeek == soonestWeek) {
                if (currentDay < soonestDay) {
                    // update the new min
                    intMechanicNum = i;
                    soonestDay = earliestAppointments[i][1];
                    soonestGCFSlot = earliestAppointments[i][2];
                } else if (currentDay == soonestDay) {
                    if (currentGCFSlot < soonestGCFSlot) {
                        // update the new min
                        intMechanicNum = i;
                        soonestGCFSlot = earliestAppointments[i][2];
                    }
                }
            }
        }

        return new int[] { intMechanicNum, soonestWeek, soonestDay, soonestGCFSlot };
    }

    /**
     * Clones the schedule data structure in order to schedule temporary
     * appointments to avoid the same car being schedule for two appointments at the
     * same time if two services are requested.
     * 
     * @param arrMechanics the array of mechanics from the database
     * @return a deep clone of all the mechanic schedules
     */
    private static MechanicSchedule[] cloneScheduleStructure(Mechanic[] arrMechanics) {
        // new schedule that will be a copy of all the mechanic schedules
        MechanicSchedule[] cloneSchedule = new MechanicSchedule[arrMechanics.length];
        // foreach mechanic
        for (int i = 0; i < cloneSchedule.length; i++) {
            try {
                // clone the mechanic's schedule into the clone schedule
                cloneSchedule[i] = (MechanicSchedule) allMechanicSchedules[i].clone();
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
            }
        }

        // return the deep clone
        return cloneSchedule;
    }

    /**
     * Checks if a vehicle has another appointment at this time
     * 
     * @param appointmentTUID if the appointment already exist the id is provided to
     *                        prevent overlap protection from going off on the same
     *                        exact appointment. Otherwise provide -1.
     * @param currVehicle     the vehicle to check for current appointments
     * @param tsStartDate     the start timestamp of the new appointment that wants
     *                        to be added to the schedule
     * @param tsEndDate       the end timestamp of the new appointment that wants to
     *                        be added to the schedule
     * @return whether or not this appointment overlaps an existing appointment with
     *         the vehicle
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    private static boolean hasOverlap(int appointmentTUID, Vehicle currVehicle, Timestamp tsStartDate,
            Timestamp tsEndDate) throws SQLException {
        // get other appointments for this vehicle
        Schedule[] otherAppointmentsForVehicle = getVehiclesAppointments(currVehicle);
        // check if any other appointment overlaps this new one
        for (Schedule currOtherAppointment : otherAppointmentsForVehicle) {
            if (currOtherAppointment.Appointment_Start_Time.getTime() < tsEndDate.getTime()
                    && currOtherAppointment.Appointment_End_Time.getTime() > tsStartDate.getTime()
                    && currOtherAppointment.getTUID() != appointmentTUID) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the appointments with the given vehicle
     * 
     * @param currVehicle the vehicle to check for existing appointments with
     * @return the schedule for the provided vehicle
     * @throws SQLException throws a SQL exception if the database cannot be
     *                      initialized
     */
    private static Schedule[] getVehiclesAppointments(Vehicle currVehicle) throws SQLException {
        Schedule[] appointments = Schedule.getSchedules(true);
        LinkedList<Schedule> vehiclesAppointments = new LinkedList<Schedule>();
        for (Schedule currAppointment : appointments) {
            if (currAppointment.Vehicles_TUID == currVehicle.getTUID())
                vehiclesAppointments.add(currAppointment);
        }
        return vehiclesAppointments.toArray(new Schedule[0]);
    }

    /**
     * Get the earliest appointment for a service with the provided mechanic
     * 
     * @param mechanicSchedule the schedule of the mechanic the may take on the
     *                         appointment
     * @param currentService   the service being requested
     * @param GCFServiceTime   the GCF between service lengths
     * @param intSlotsNeeded   the number of appointment slots needed to finish the
     *                         service
     * @return an array with the earliest appointment indexes in the mechanic's
     *         schedule
     */
    private static int[] earliestAppointment(MechanicSchedule mechanicSchedule, Service currentService,
            int GCFServiceTime, int intSlotsNeeded) {
        // the earlist week, day, timeslot combination
        int[] arrEarliest = { -1, -1, -1 };
        // the current week index
        int weekNum = 0;
        while (true) {
            // foreach work day
            for (int i = 0; i < MechanicSchedule.NUM_WORK_DAYS; i++) {
                // foreach appointment slot
                for (int j = 0; j < MechanicSchedule.NUM_WORK_MINUTES / GCFServiceTime; j++) {
                    // if not enough slots left in the day
                    if (j + intSlotsNeeded - 1 >= MechanicSchedule.NUM_WORK_MINUTES / GCFServiceTime) {
                        // go on to next day
                        break;
                    }
                    // check if there is enough GCF slots in a row for the current service requested
                    boolean blnOpenSlot = true;
                    // foreach slot needed
                    for (int k = 0; k < intSlotsNeeded; k++) {
                        // if the mechanic is not available
                        if (mechanicSchedule.getSlot(weekNum, i, j + k)) {
                            // slot not open
                            blnOpenSlot = false;
                            break;
                        }
                    }
                    // if slot available return it
                    if (blnOpenSlot) {
                        arrEarliest[0] = weekNum;
                        arrEarliest[1] = i;
                        arrEarliest[2] = j;
                        return arrEarliest;
                    }
                }
            }
            // increment weekNum
            weekNum += 1;
            // add new week to array if it doesn't exist
            if (mechanicSchedule.weekLength() == weekNum)
                mechanicSchedule.addWeek();
        }
    }

    /**
     * Sorts a schedule array by start time
     * 
     * @param unsortedSchedule the schedule array that needs to be sorted by start
     *                         time
     * @return the provided schedule array sorted by start time
     */
    public static Schedule[] sortScheduleByTime(Schedule[] unsortedSchedule) {
        // sort the schedule array by start time
        Arrays.sort(unsortedSchedule, new Comparator<Schedule>() {
            @Override
            public int compare(Schedule s1, Schedule s2) {
                if (s1.Appointment_Start_Time.getTime() < s2.Appointment_Start_Time.getTime()) {
                    return -1;
                } else if (s1.Appointment_Start_Time.getTime() > s2.Appointment_Start_Time.getTime()) {
                    return 1;
                }
                return 0;
            }
        });
        // return the newly sorted array, not required since Arrays.sort works on the
        // original array
        return unsortedSchedule;
    }

    /**
     * Sorts a schedule array by id
     * 
     * @param unsortedSchedule the schedule array that needs to be sorted by id
     * @return the provided schedule array sorted by id
     */
    public static Schedule[] sortScheduleById(Schedule[] unsortedSchedule) {
        // sort the schedule array by id
        Arrays.sort(unsortedSchedule, new Comparator<Schedule>() {
            @Override
            public int compare(Schedule s1, Schedule s2) {
                if (s1.getTUID() < s2.getTUID()) {
                    return -1;
                } else if (s1.getTUID() > s2.getTUID()) {
                    return 1;
                }
                return 0;
            }
        });

        // return the sorted by id schedule array
        return unsortedSchedule;
    }

    /**
     * Gets the paychecks of each mechanic for each week on the schedule
     * 
     * @param arrMechanics the array of mechanics from the database
     * @param arrServices  the array of services from the database
     * @return a 2D array of paycheck amounts where the index is (mechanic id - 1,
     *         week number: where 0 is next week)
     */
    public static double[][] getPaychecks(Mechanic[] arrMechanics, Service[] arrServices) {
        // the greatest common factor between service lengths
        final int GCFServiceTime = Service.GCF(arrServices);
        // the max number of weeks being worked on the schedule
        int maxWeekLength = 0;
        // foreach mechanic schedule
        for (MechanicSchedule currMechanicSchedule : allMechanicSchedules) {
            // if new longest week length set it to max
            if (currMechanicSchedule.weekLength() > maxWeekLength)
                maxWeekLength = currMechanicSchedule.weekLength();
        }
        // 2D double array for storing the amount a mechanic is getting paid for the
        // week
        // index (mechanic id - 1, week index)
        double[][] paychecks = new double[arrMechanics.length][maxWeekLength];

        // foreach mechanic
        for (int i = 0; i < arrMechanics.length; i++) {
            // foreach week
            for (int j = 0; j < maxWeekLength; j++) {
                // compute the paycheck for that mechanic on that week
                paychecks[i][j] = allMechanicSchedules[i].computePaycheckForWeek(j, arrMechanics[i].intHourly_payrate,
                        GCFServiceTime);
            }
        }

        // return the paychecks' amounts
        return paychecks;
    }

    public static void printScheduleDataStructure() {
        for (MechanicSchedule currMechanicSchedule : allMechanicSchedules) {
            System.out.println(currMechanicSchedule);
            System.out.println();
        }
    }

    /**
     * Gets the string representation of a schedule
     * 
     * @return a string representation of a schedule
     */
    @Override
    public String toString() {
        // a string representation of a schedule
        return "Schedule: TUID=" + this.getTUID() + " Vehicles_TUID=" + Vehicles_TUID + " Bays_TUID=" + Bays_TUID
                + " Appointment_Start_Time=" + Appointment_Start_Time + " Appointment_End_Time=" + Appointment_End_Time;
    }
}
