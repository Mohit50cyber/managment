package com.moglix.wms.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author pankaj on 29/4/19
 */
public class DateUtil {
	
	private DateUtil() {
	}
	
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    
    private static Logger logger = LogManager.getLogger(DateUtil.class);

    public static int getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getYear(Calendar c) {
        return c.get(Calendar.YEAR);
    }

    public static int getCurrentYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    public static int getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    public static boolean isFirstDayofMonth(){
        return (getCurrentDay() == 1);
    }

    public static int getTotalDayInCurrentMonth() {
        Calendar c = Calendar.getInstance();
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int getTotalDayInLastMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Calendar getCurrentTimeCalendar() {
        return Calendar.getInstance();
    }

    public static Date getCurrentDate() {
        Calendar c = getCurrentTimeCalendar();
        return c.getTime();
    }

    public static Date getDateByDay(int day) {
        Calendar c  = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }

    public static Date getYesterday() {
        Calendar c  = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }

    public static Date getCustomizedDate(int type, int diff) {
        Calendar c  = Calendar.getInstance();
        c.add(type, diff);
        return c.getTime();
    }

    public static String getCurrentTimeStampString(String format) {
        if(StringUtils.isBlank(format))
            format = DEFAULT_DATE_FORMAT;
        Date date = getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Long getCurrentTimeInMillis() {
        Calendar c = getCurrentTimeCalendar();
        return c.getTimeInMillis();
    }

    public static Date trimDate(Date date) {
        if(date == null)
            return null;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date endDate(Date date) {
        if(date == null)
            return null;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    public static Date getCurrentDateOnly() {
        return trimDate(Calendar.getInstance().getTime());
    }

    public static Date getCurrentDateTime() {
        return Calendar.getInstance().getTime();
    }

    public static Date convertStringToDate(String str, String format, boolean defaultNull) {
        Date dafaultDate = null;
        if(!defaultNull) {
            dafaultDate = Calendar.getInstance().getTime();
        }
        if(StringUtils.isBlank(str))
            return dafaultDate;
        if(StringUtils.isBlank(format))
            format = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        Date d = dafaultDate;
        try {
            d = sdf.parse(str);
        } catch(Exception e) {
        	logger.error("Error encountered in coverting String to Date. Date: " + str + "Format: " + format, e);
        }
        return d;
    }

    public static String convertDateToString(Date date, String format) {
        String dateStr = null;
        if(StringUtils.isBlank(format))
            format = DEFAULT_DATE_FORMAT;
        if(date == null)
            return dateStr;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            dateStr = sdf.format(date);
        } catch(Exception e) {
            logger.error("Error encountered in coverting date to string. Date: " + date + "Format: " + format, e);
        }
        return dateStr;
    }

    public static Date convertLongToDate(Long d) {
        Date date = null;
        if(d != null) {
            date = new Date(d);
        }
        return date;
    }

    public static String getFyFromDateStr(String fp, String format) {
        Date date = convertStringToDate(fp, format, true);
        if(date == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);

        String fy = null;
        if(month < 4) {
            fy = String.valueOf(year-1) + "-" + year;
        } else {
            fy = year + "-" + (year+1);
        }
        return fy;
    }
}
