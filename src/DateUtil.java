
/**
 * Filename: DateUtil.java
 * 
 * Project: Homework3
 * 
 * Author: Kevin Gyorick
 * 
 * Date: 10/09/2021
 * 
 * File Purpose:    This file holds some utility functions for java dates. 
 * 
 * Program Purpose: The program schedules appointments for a mechanic shop. 
 *                  Appointments are scheduled first come, first served as 
 *                  long as the job can be completed the same day it is started 
 *                  given the current schedule. Mechanics with lower bay numbers 
 *                  assigned have higher priority when multiple mechanics are available
 */
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {
    static final int MILLISECONDS_SECOND = 1000; // number of milliseconds in a second
    static final int MILLISECONDS_MINUTE = MILLISECONDS_SECOND * 60; // number of milliseconds in a minute
    static final int MILLISECONDS_HOUR = MILLISECONDS_MINUTE * 60; // number of milliseconds in a hour
    static final int MILLISECONDS_DAY = MILLISECONDS_HOUR * 24; // number of milliseconds in a day
    static final int MILLISECONDS_WEEK = MILLISECONDS_DAY * 7; // number of milliseconds in a week
    private static final int LUNCH_HOUR = 12; // the hour of the day lunch starts
    private static final int LUNCH_MINUTE = 0; // the minute on the hour that lunch starts
    private static final int LUNCH_LENGTH = 1; // lunch length in hours
    private static final int OPENING_HOUR = 8; // the hour of the day the business opens
    private static final int OPENING_MINUTE = 0; // the minute on the hour that the business opens

    /**
     * Gets the timestamp of next monday at the opening time of the shop
     * 
     * @return the timestamp of next monday at the opening time of the shop
     */
    public static Timestamp getNextMondaysDate() {
        // the current localdate
        LocalDate ld = LocalDate.now();
        // next monday's localdate
        ld = ld.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        // return the localdate as a timestamp with the opening time
        return Timestamp.valueOf(ld.atTime(LocalTime.of(OPENING_HOUR, OPENING_MINUTE, 0)));
    }

    /**
     * Converts hours to miliseconds
     * 
     * @param lgHours the number of hours to convert to miliseconds
     * @return the number of miliseconds in the amount of hours provided
     */
    public static long hoursToMiliseconds(long lgHours) {
        // convert hours to milliseconds
        return MILLISECONDS_HOUR * lgHours;
    }

    /**
     * Converts minutes to miliseconds
     * 
     * @param lgMinutes the number of miniutes to convert to miliseconds
     * @return the number of miliseconds in the amount of minutes probided
     */
    public static long minutesToMiliseconds(long lgMinutes) {
        // convert minutes to milliseconds
        return MILLISECONDS_MINUTE * lgMinutes;
    }

    /**
     * Gets the number of miliseconds between next monday and the provided
     * appointment timeslot
     * 
     * @param intWeek the week number of the appointment, 0 being the first week of
     *                the schedule starting next monday
     * @param intDay  the day number of the week Monday to Sunday indexing from 0 to
     *                6
     * @param intSlot the timeslot number
     * @param intGCF  the number of minutes a single timeslot represents
     * @return the number of miliseconds between next monday and the provided
     *         appointment timeslot
     */
    public static long WeekDayTimeslotToMillisecond(int intWeek, int intDay, int intSlot, int intGCF) {
        // milliseconds between next monday and the appointment slot
        long milliseconds = 0;
        milliseconds += intWeek * MILLISECONDS_WEEK;
        milliseconds += intDay * MILLISECONDS_DAY;
        milliseconds += intSlot * Service.millisecondsTimeslot(intGCF);
        return milliseconds;
    }

    /**
     * Determines if the provided timeslot should be offset by the duration of lunch
     * 
     * @param ts           the timestamp to check
     * @param blnStartTime whether or not the provided timestamp is a start time or
     *                     end time of an appointment
     * @return true if the timeslot should be offset by the duration of lunch
     */
    private static boolean isDuringOrAfterLunch(Timestamp ts, boolean blnStartTime) {
        // if timestamp is a start time
        // check if the timestamp falls on or after lunch
        if (blnStartTime)
            return ts.toLocalDateTime().toLocalTime()
                    .isAfter(LocalTime.parse(
                            String.format("%02d", LUNCH_HOUR) + ":" + String.format("%02d", LUNCH_MINUTE) + ":00")
                            .minusSeconds(1));
        // else check if the timestamps falls during or after lunch
        return ts.toLocalDateTime().toLocalTime().isAfter(
                LocalTime.parse(String.format("%02d", LUNCH_HOUR) + ":" + String.format("%02d", LUNCH_MINUTE) + ":00"));
    }

    /**
     * Offsets the provided timestamp by the duration of lunch if the appointment
     * happens during or after lunch
     * 
     * @param ts           the timestamp to offset by the duration of lunch
     * @param blnStartTime whether or not the provided timestamp is a start time or
     *                     end time of an appointment
     * @return the offseted timestamp
     */
    public static Timestamp tsOffsetLunch(Timestamp ts, boolean blnStartTime) {
        // if the timestamp is before lunch return the original timestamp
        if (!isDuringOrAfterLunch(ts, blnStartTime))
            return ts;
        // else return the timestamp offseted by the duration of lunch
        return new Timestamp(ts.getTime() + hoursToMiliseconds(LUNCH_LENGTH));
    }
}
