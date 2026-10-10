package framework.utils;

import java.util.Objects;

/**
 * String utility functions for the framework.
 * 
 * Utility class providing common string operations.
 * All methods are static - no instance creation needed.
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class StringUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private StringUtils() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param value the string to check
     * @return true if null or empty
     */
    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * Checks if a string is null, empty, or contains only whitespace.
     *
     * @param value the string to check
     * @return true if null, empty, or blank
     */
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Checks if a string has content (not null, empty, or blank).
     *
     * @param value the string to check
     * @return true if string has content
     */
    public static boolean hasContent(String value) {
        return !isBlank(value);
    }

    /**
     * Capitalizes the first character of a string.
     *
     * @param value the string to capitalize
     * @return the capitalized string, or empty string if input is null/empty
     */
    public static String capitalize(String value) {
        if (isEmpty(value)) {
            return value;
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    /**
     * Converts a string to lowercase.
     *
     * @param value the string to convert
     * @return the lowercase string, or null if input is null
     */
    public static String toLower(String value) {
        return value == null ? null : value.toLowerCase();
    }

    /**
     * Converts a string to uppercase.
     *
     * @param value the string to convert
     * @return the uppercase string, or null if input is null
     */
    public static String toUpper(String value) {
        return value == null ? null : value.toUpperCase();
    }

    /**
     * Trims whitespace from a string.
     *
     * @param value the string to trim
     * @return the trimmed string, or null if input is null
     */
    public static String trim(String value) {
        return value == null ? null : value.trim();
    }

    /**
     * Replaces all occurrences of a substring.
     *
     * @param value the original string
     * @param search the substring to find
     * @param replacement the replacement string
     * @return the modified string, or original if search not found
     */
    public static String replace(String value, String search, String replacement) {
        if (isEmpty(value)) {
            return value;
        }
        return value.replace(search, replacement);
    }

    /**
     * Checks if a string contains a substring (case-insensitive).
     *
     * @param value the string to search in
     * @param substring the substring to find
     * @return true if substring is found (case-insensitive)
     */
    public static boolean containsIgnoreCase(String value, String substring) {
        if (isEmpty(value) || isEmpty(substring)) {
            return false;
        }
        return value.toLowerCase().contains(substring.toLowerCase());
    }

    /**
     * Joins multiple strings with a delimiter.
     *
     * @param delimiter the delimiter to use
     * @param values the strings to join
     * @return the joined string
     */
    public static String join(String delimiter, String... values) {
        return String.join(Objects.requireNonNull(delimiter), values);
    }

    /**
     * Repeats a string N times.
     *
     * @param value the string to repeat
     * @param count the number of times to repeat
     * @return the repeated string
     */
    public static String repeat(String value, int count) {
        if (isEmpty(value) || count <= 0) {
            return "";
        }
        return value.repeat(Math.max(0, count));
    }

    /**
     * Gets a substring between two strings.
     *
     * @param value the source string
     * @param start the start delimiter
     * @param end the end delimiter
     * @return the substring between delimiters, or empty if not found
     */
    public static String substringBetween(String value, String start, String end) {
        if (isEmpty(value) || isEmpty(start) || isEmpty(end)) {
            return "";
        }
        int startIndex = value.indexOf(start);
        if (startIndex == -1) {
            return "";
        }
        int endIndex = value.indexOf(end, startIndex + start.length());
        if (endIndex == -1) {
            return "";
        }
        return value.substring(startIndex + start.length(), endIndex);
    }

    /**
     * Checks if a string starts with a prefix (case-insensitive).
     *
     * @param value the string to check
     * @param prefix the prefix
     * @return true if string starts with prefix (case-insensitive)
     */
    public static boolean startsWithIgnoreCase(String value, String prefix) {
        if (isEmpty(value) || isEmpty(prefix)) {
            return false;
        }
        return value.toLowerCase().startsWith(prefix.toLowerCase());
    }

    /**
     * Checks if a string ends with a suffix (case-insensitive).
     *
     * @param value the string to check
     * @param suffix the suffix
     * @return true if string ends with suffix (case-insensitive)
     */
    public static boolean endsWithIgnoreCase(String value, String suffix) {
        if (isEmpty(value) || isEmpty(suffix)) {
            return false;
        }
        return value.toLowerCase().endsWith(suffix.toLowerCase());
    }
}
