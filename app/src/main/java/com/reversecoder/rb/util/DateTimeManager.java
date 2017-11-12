package com.reversecoder.rb.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class DateTimeManager {

    public enum DATE_TIME_PATTERN {

        PATTERN1("EEEE, d MMMM yyyy, HH:mm:ss aaa"),
        PATTERN2("yyyy.MM.dd G 'at' HH:mm:ss z"),
        PATTERN3("EEE, MMM d, ''yy"),
        PATTERN4("h:mm a"),
        PATTERN5("hh 'o''clock' a, zzzz"),
        PATTERN6("K:mm a, z"),
        PATTERN7("yyyyy.MMMMM.dd GGG hh:mm aaa"),
        PATTERN8("EEE, d MMM yyyy HH:mm:ss Z"),
        PATTERN9("yyMMddHHmmssZ"),
        PATTERN10("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
        PATTERN11("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
        PATTERN12("YYYY-'W'ww-u");

        private final String pattern;

        private DATE_TIME_PATTERN(String s) {
            pattern = s;
        }

        public boolean equalsName(String otherName) {
            return pattern.equals(otherName);
        }

        public String toString() {
            return this.pattern;
        }
    }

    /**
     * get current date from the system
     *
     * @param pattern the format.
     */
    public static String getSystemCurrentDate(DATE_TIME_PATTERN pattern) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern.toString());
        String formatDate = dateFormat.format(currentDate);
        return formatDate;
    }

    /**
     * Convert date from one format to another.
     *
     * @param inputDate        The date that is to be re-formated.
     * @param inputDateFormat  The input date format.
     * @param outputDateFormat The output date format.
     * @return String reformated date.
     */
    public static String convertDate(String inputDate, String inputDateFormat, String outputDateFormat) {
        String reformattedStr = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputDateFormat);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputDateFormat);
            reformattedStr = outputFormat.format(inputFormat.parse(inputDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

}
