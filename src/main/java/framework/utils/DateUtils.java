package framework.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Date and time utility functions for the framework.
 *
 * Provides utilities for: - Date/time formatting - Date calculations -
 * Timestamp generation - Date comparisons
 *
 * @author Automation Team
 * @version 1.0.0
 */
public class DateUtils {

    public static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DEFAULT_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DEFAULT_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Private constructor to prevent instantiation.
     */
    private DateUtils() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Gets current date.
     *
     * @return the current date
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * Gets current date and time.
     *
     * @return the current date and time
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    /**
     * Gets current date as a formatted string (yyyy-MM-dd).
     *
     * @return the current date as string
     */
    public static String getCurrentDateAsString() {
        return getCurrentDate().format(DEFAULT_DATE_FORMAT);
    }

    /**
     * Gets current date and time as a formatted string (yyyy-MM-dd HH:mm:ss).
     *
     * @return the current date and time as string
     */
    public static String getCurrentDateTimeAsString() {
        return getCurrentDateTime().format(DEFAULT_DATETIME_FORMAT);
    }

    /**
     * Gets current time as a formatted string (HH:mm:ss).
     *
     * @return the current time as string
     */
    public static String getCurrentTimeAsString() {
        return LocalDateTime.now().format(DEFAULT_TIME_FORMAT);
    }

    /**
     * Formats a LocalDate with default format.
     *
     * @param date the date to format
     * @return the formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date.format(DEFAULT_DATE_FORMAT);
    }

    /**
     * Formats a LocalDateTime with default format.
     *
     * @param dateTime the date time to format
     * @return the formatted date time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DEFAULT_DATETIME_FORMAT);
    }

    /**
     * Formats a date with a custom pattern.
     *
     * @param date the date to format
     * @param pattern the format pattern
     * @return the formatted date string
     */
    public static String formatDateWithPattern(LocalDate date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    /**
     * Formats a date time with a custom pattern.
     *
     * @param dateTime the date time to format
     * @param pattern the format pattern
     * @return the formatted date time string
     */
    public static String formatDateTimeWithPattern(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * Parses a date string to LocalDate.
     *
     * @param dateString the date string (format: yyyy-MM-dd)
     * @return the parsed LocalDate
     */
    public static LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, DEFAULT_DATE_FORMAT);
    }

    /**
     * Parses a date time string to LocalDateTime.
     *
     * @param dateTimeString the date time string (format: yyyy-MM-dd HH:mm:ss)
     * @return the parsed LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * Parses a date string with custom pattern.
     *
     * @param dateString the date string
     * @param pattern the format pattern
     * @return the parsed LocalDate
     */
    public static LocalDate parseDateWithPattern(String dateString, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateString, formatter);
    }

    /**
     * Adds days to a date.
     *
     * @param date the base date
     * @param daysToAdd the number of days to add
     * @return the new date
     */
    public static LocalDate addDays(LocalDate date, long daysToAdd) {
        return date.plusDays(daysToAdd);
    }

    /**
     * Subtracts days from a date.
     *
     * @param date the base date
     * @param daysToSubtract the number of days to subtract
     * @return the new date
     */
    public static LocalDate subtractDays(LocalDate date, long daysToSubtract) {
        return date.minusDays(daysToSubtract);
    }

    /**
     * Adds months to a date.
     *
     * @param date the base date
     * @param monthsToAdd the number of months to add
     * @return the new date
     */
    public static LocalDate addMonths(LocalDate date, long monthsToAdd) {
        return date.plusMonths(monthsToAdd);
    }

    /**
     * Adds years to a date.
     *
     * @param date the base date
     * @param yearsToAdd the number of years to add
     * @return the new date
     */
    public static LocalDate addYears(LocalDate date, long yearsToAdd) {
        return date.plusYears(yearsToAdd);
    }

    /**
     * Calculates the number of days between two dates.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return the number of days between dates
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Calculates the number of days between two date times.
     *
     * @param startDateTime the start date time
     * @param endDateTime the end date time
     * @return the number of days between date times
     */
    public static long daysBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.DAYS.between(startDateTime, endDateTime);
    }

    /**
     * Checks if a date is before another date.
     *
     * @param date1 the first date
     * @param date2 the second date
     * @return true if date1 is before date2
     */
    public static boolean isBefore(LocalDate date1, LocalDate date2) {
        return date1.isBefore(date2);
    }

    /**
     * Checks if a date is after another date.
     *
     * @param date1 the first date
     * @param date2 the second date
     * @return true if date1 is after date2
     */
    public static boolean isAfter(LocalDate date1, LocalDate date2) {
        return date1.isAfter(date2);
    }

    /**
     * Checks if two dates are equal.
     *
     * @param date1 the first date
     * @param date2 the second date
     * @return true if dates are equal
     */
    public static boolean isEqual(LocalDate date1, LocalDate date2) {
        return date1.isEqual(date2);
    }

    /**
     * Gets a timestamp string for file naming. Format: yyyyMMdd_HHmmss
     *
     * @return the timestamp string
     */
    public static String getTimestampForFileName() {
        return getCurrentDateTime().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    /**
     * Gets the day name of a date.
     *
     * @param date the date
     * @return the day name (MONDAY, TUESDAY, etc.)
     */
    public static String getDayName(LocalDate date) {
        return date.getDayOfWeek().toString();
    }

    /**
     * Gets the month name of a date.
     *
     * @param date the date
     * @return the month name (JANUARY, FEBRUARY, etc.)
     */
    public static String getMonthName(LocalDate date) {
        return date.getMonth().toString();
    }
}
