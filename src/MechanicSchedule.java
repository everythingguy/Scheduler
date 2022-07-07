
/**
 * Filename: MechanicSchedule.java
 * 
 * Project: Homework3
 * 
 * Author: Kevin Gyorick
 * 
 * Date: 10/09/2021
 * 
 * File Purpose:    This file is a data structure used for scheduling new appointments with a mechanic.
 *                  The data structure has the indexes (week: where 0 is next week, day: where 0 is monday, timeslot).
 *                  The GCF between service lengths is used to create a number of timeslot for each day.
 *                  Where total timeslots in a day equals total working time in minutes divided by the GCF.
 * 
 * Program Purpose: The program schedules appointments for a mechanic shop. 
 *                  Appointments are scheduled first come, first served as 
 *                  long as the job can be completed the same day it is started 
 *                  given the current schedule. Mechanics with lower bay numbers 
 *                  assigned have higher priority when multiple mechanics are available
 */
import java.util.LinkedList;

public class MechanicSchedule implements Cloneable {
    public final static int NUM_WORK_DAYS = 5; // the number of work days in the week
    public final static int NUM_WORK_HOURS = 8; // the number of hours worked per day
    public final static int NUM_WORK_MINUTES = NUM_WORK_HOURS * 60; // the number of minutes worked per day
    private int GCFServiceTime;

    // the mechanic's schedule for each week
    private LinkedList<boolean[][]> lstWeek;

    /**
     * construct a mechanic schedule using the greatest common factor between
     * service times
     * 
     * @param GCFServiceTime the number of minutes a single timeslot represents
     */
    public MechanicSchedule(int GCFServiceTime) {
        this.GCFServiceTime = GCFServiceTime;
        // initialize the linked list
        lstWeek = new LinkedList<boolean[][]>();
        // add the first week with GCF
        addWeek();
    }

    /**
     * Adds a new week to the mechanic's schedule
     */
    public void addWeek() {
        // add a new week to the schedule
        // a week consist of NUM_WORK_DAYS boolean arrays with NUM_WORK_MINUTES /
        // GCFServiceTime indices
        lstWeek.add(new boolean[NUM_WORK_DAYS][NUM_WORK_MINUTES / this.GCFServiceTime]);
    }

    /**
     * Gets the number of weeks on the mechanic's schedule
     * 
     * @return the number of weeks on the mechanic's schedule
     */
    public int weekLength() {
        // return the number of weeks on the schedule
        return lstWeek.size();
    }

    /**
     * Reserves a new slot on the mechanic's schedule for an appointment
     * 
     * @param weekIndex the week index of the appointment
     * @param dayIndex  the day of the week for the appointment Monday - Sunday
     *                  indexed 0 - 6 assuming a 7 day work week
     * @param timeIndex the timeslot index to reserve
     */
    public void reserveSlot(int weekIndex, int dayIndex, int timeIndex) {
        // reserve a timeslot on the schedule at the index (weekIndex, dayIndex,
        // timeIndex)
        while (weekIndex > weekLength() - 1)
            addWeek();
        lstWeek.get(weekIndex)[dayIndex][timeIndex] = true;
    }

    /**
     * Gets whether or not the timeslot at index (week,day,time) is reserved
     * 
     * @param weekIndex the week index of the slot
     * @param dayIndex  the day index of the slot Monday - Sunday index 0 - 6
     *                  assuming a 7 day work week
     * @param timeIndex the timeslot index
     * @return true if the slot is already reserved and false if open
     */
    public boolean getSlot(int weekIndex, int dayIndex, int timeIndex) {
        // get the boolean value of the slot at the index (weekIndex, dayIndex,
        // timeIndex)
        if (weekIndex > weekLength() - 1)
            return false;
        return lstWeek.get(weekIndex)[dayIndex][timeIndex];
    }

    /**
     * Computes the paycheck for the provided week index at the provided payrate
     * Employees are not paid if there are no appointments to work, they get to go
     * home
     * 
     * @param weekNum       the week index to compute
     * @param hourlyPayrate the payrate of the mechanic this schedule belongs to
     * @param GCF           the number of minutes a timeslot represents
     * @return the amount of the paycheck for the provided week and the provided
     *         payrate according to appointments booked
     */
    public double computePaycheckForWeek(int weekNum, double hourlyPayrate, int GCF) {
        // compute the paycheck for the given week
        // get the total reserved time slots for the provided week
        int totalReservedSlots = totalReservedSlotsForWeek(weekNum);
        // return the value of the paycheck
        return hourlyPayrate / 60 * GCF * totalReservedSlots;
    }

    /**
     * The number of reserved timeslots for the provided week index
     * 
     * @param weekNum the index of the week to check
     * @return the number of reserved timeslots in that week
     */
    public int totalReservedSlotsForWeek(int weekNum) {
        // if week does not exist in the schedule then no spots reserved
        if (weekNum > weekLength() - 1)
            return 0;
        // running total
        int total = 0;
        // get the provided week's schedule
        boolean[][] currWeek = lstWeek.get(weekNum);
        // foreach day on the schedule
        for (int i = 0; i < currWeek.length; i++) {
            // foreach GCF timeslot in the day
            for (int j = 0; j < currWeek[i].length; j++) {
                // if reserved
                if (currWeek[i][j])
                    // increment the total
                    total += 1;
            }
        }

        // return the total
        return total;
    }

    /**
     * Deep clone of the mechanic's schedule, used for temporary bookings in order
     * to avoid booking the same car for two appointments that are at the same time
     * 
     * @return A deep clone of this mechanic schedule
     * @throws CloneNotSupportedException throws if the clone function is not
     *                                    implmented
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        MechanicSchedule cloneSchedule = new MechanicSchedule(this.GCFServiceTime);
        for (int i = 0; i < this.lstWeek.size(); i++) {
            cloneSchedule.addWeek();
            for (int j = 0; j < this.lstWeek.get(i).length; j++) {
                for (int k = 0; k < this.lstWeek.get(i)[j].length; k++) {
                    if (this.getSlot(i, j, k))
                        cloneSchedule.reserveSlot(i, j, k);
                }
            }
        }
        return cloneSchedule;
    }

    /**
     * @return A string representation of the mechanic's schedule
     */
    @Override
    public String toString() {
        // create a new string builder
        StringBuilder builder = new StringBuilder();
        // foreach week
        for (int i = 0; i < lstWeek.size(); i++) {
            // foreach day
            for (int j = 0; j < lstWeek.get(i).length; j++) {
                // foreach timeslot
                for (int k = 0; k < lstWeek.get(i)[j].length; k++) {
                    // if reserved append a 1
                    if (lstWeek.get(i)[j][k])
                        builder.append("1 ");
                    // if not append a 0
                    else
                        builder.append("0 ");
                }
                // new line foreach day
                builder.append("\n");
            }
            // new line for each week
            builder.append("\n");
        }
        // return the builder's string
        return builder.toString();
    }
}
