package com.ghx.api.operations.util;

import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.truncate;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;

import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * 
 * @author Rajasekar Jayakumar
 *         Utility class contains date conversion methods.
 */

public class DateUtils {

    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(DateUtils.class);

    private static Locale locale = LocaleContextHolder.getLocale();

    /**
     * Returns today's date as java.util.Date object
     *
     * @return today's date as java.util.Date object
     */
    public static Date today() {
        return new Date();
    }

    /**
     * Returns today's date as yyyy-MM-dd format
     *
     * @return today's date as yyyy-MM-dd format
     */
    public static String todayStr() {
        SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.YYYY_MM_DD, locale);
        return sdf.format(today());
    }

    /**
     * Returns the formatted String date for the passed java.util.Date object
     *
     * @param date
     * @return
     */
    public static String formattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.YYYY_MM_DD, locale);
        return date != null ? sdf.format(date) : todayStr();
    }

    /**
     * Returns yesterday's date
     *
     * @param date
     * @return
     */
    public static Date getPreviousDay(Date date) {

        Instant previous = date.toInstant().minus(1, ChronoUnit.DAYS);
        return Date.from(previous);
    }

    /**
     * Returns next date
     *
     * @param date
     * @return
     */
    public static Date getNextDay(Date date) {
        Instant previous = date.toInstant().plus(1, ChronoUnit.DAYS);
        return Date.from(previous);
    }

    /**
     * Returns as Date in yyyy-MM-dd format
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date todaysDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.YYYY_MM_DD, locale);
        Date finalDate = new Date();
        try {
            finalDate = sdf.parse(date);
        } catch (ParseException e) {
            LOGGER.debug("Invalid date format", e.getMessage());
        }
        return finalDate;
    }

    /**
     * Returns Date in ISO Date format of particular Zone
     *
     * @param date
     * @param timeZone
     * @return
     * @throws Exception
     */

    public static Date getISODate(Date date, String timeZone) {
        Date finalDate = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantUtils.YYYY_MM_DD);
            String text = formattedDate(date);
            LocalDate parsedDate = LocalDate.parse(text, formatter);
            finalDate = Date.from(parsedDate.atStartOfDay(ZoneId.of(timeZone)).toInstant());
        } catch (DateTimeParseException e) {
            LOGGER.error("Invalid date format :: ", e.getMessage());
        }
        return finalDate;
    }

    public static String convertDateToStringFormat(Date dateToConvert, String outputFormat) {
        DateFormat dateFormat = new SimpleDateFormat(outputFormat, locale);
        return dateFormat.format(dateToConvert);
    }

    /**
     * Gets the minus dates.
     * @param days
     * @return the minus dates
     */
    public static Date getMinusDates(int days) {
        return truncate(addDays(new Date(), -days), Calendar.DATE);
    }
    
    
    /**
     * Returns as Date in yyyy-MM-dd format
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getDateFromString(String date, String inputFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(inputFormat, locale);
        Date finalDate = null;
        try {
            finalDate = sdf.parse(date);
        } catch (ParseException e) {
            LOGGER.debug("getDateFromString:: Invalid date format", e.getMessage());
        }
        return finalDate;
    }

    /**
     * given date string format will changed to timestamp in yyyy-MM-dd format
     * 
     * @param date
     * @return
     */
    public static Timestamp convertStringToTimeStamp(String date) {
        Timestamp formattedDate = null;
        try {
            formattedDate = new Timestamp(new SimpleDateFormat(ConstantUtils.YYYY_MM_DD, locale).parse(date).getTime());
        } catch (ParseException e) {
            LOGGER.debug("getDateFromString:: Invalid date format", e.getMessage());
        }
        return formattedDate;
    }

}
