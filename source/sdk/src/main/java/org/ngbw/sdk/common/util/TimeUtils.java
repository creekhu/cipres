/*
 * Created on Jul 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ngbw.sdk.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Roland H. Niedner <br />
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TimeUtils {

    static final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000L;
    static final long MILLISECONDS_PER_HOUR = 60 * 60 * 1000L;
    static final long MILLISECONDS_PER_MINUTE = 60 * 1000L;
    static final long MILLISECONDS_PER_SECOND = 1000L;
    static final int HOURS_PER_DAY = 24;
    static final int MINUTES_PER_HOUR = 60;
    static final int SECONDS_PER_MINUTE = 60;

    private long strt = 0;
    private long time = 0;

	public TimeUtils()
	{
		start();
	}

	public boolean checkCutoff(long cutoffSeconds)
	{
		takeTime();
		if (time > (cutoffSeconds *  MILLISECONDS_PER_SECOND))
		{
			return true;
		}
		return false;
	}

	public String getElapsed()
	{
		return getTimeString(time);
	}

    public void reset() {
        strt = 0;
        time = 0;
    }

    public void start() {
        strt = System.currentTimeMillis();
    }

    public long takeTime() {
        time = time + System.currentTimeMillis() - strt;
        return time;
    }

    public long getTotalTimeInMilliseconds() {
        return time;
    }

    public int getMilliseconds() {
        return getTimeIntegerArray(time)[0].intValue();
    }

    public float getSeconds() {
        return getTimeIntegerArray(time)[1].intValue();
    }

    public float getMinutes() {
        return getTimeIntegerArray(time)[2].intValue();
    }

    public float getHours() {
        return getTimeIntegerArray(time)[3].intValue();
    }

    public float getDays() {
        return getTimeIntegerArray(time)[4].intValue();
    }

    public String getTimeString() {
        return getTimeString(time);
    }

    public static long convert2milliseconds(
        int days,
        int hours,
        int minutes,
        int seconds,
        int milliseconds) {
        return days
            * MILLISECONDS_PER_DAY
            + hours
            * MILLISECONDS_PER_HOUR
            + minutes
            * MILLISECONDS_PER_MINUTE
            + seconds
            * MILLISECONDS_PER_SECOND
            + milliseconds;
    }

    //parses the String days-hours-minutes-seconds-milliseconds
    public static long convert2milliseconds(String time) {
        String[] tokens = time.split("-");
        if (!(tokens.length == 5))
            return -1L;
        return convert2milliseconds(Integer.parseInt(tokens[0]), Integer
            .parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer
            .parseInt(tokens[3]), Integer.parseInt(tokens[4]));
    }

    public static Integer[] getTimeIntegerArray(long togo) {
        Integer millis = Integer.valueOf((int) (togo % MILLISECONDS_PER_SECOND));
        /* /= is just shorthand for togo = togo / 1000 */
        togo /= MILLISECONDS_PER_SECOND;

        Integer seconds = Integer.valueOf((int) (togo % SECONDS_PER_MINUTE));
        togo /= SECONDS_PER_MINUTE;

        Integer minutes = Integer.valueOf((int) (togo % MINUTES_PER_HOUR));
        togo /= MINUTES_PER_HOUR;

        Integer hours = Integer.valueOf((int) (togo % HOURS_PER_DAY));
        Integer days = Integer.valueOf((int) (togo / HOURS_PER_DAY));

        return new Integer[] { millis, seconds, minutes, hours, days };
    }

    public static String getTimeString(long togo) {
        int millis = (int) (togo % MILLISECONDS_PER_SECOND);
        /* /= is just shorthand for togo = togo / 1000 */
        togo /= MILLISECONDS_PER_SECOND;

        int seconds = (int) (togo % SECONDS_PER_MINUTE);
        togo /= SECONDS_PER_MINUTE;

        int minutes = (int) (togo % MINUTES_PER_HOUR);
        togo /= MINUTES_PER_HOUR;

        int hours = (int) (togo % HOURS_PER_DAY);
        int days = (int) (togo / HOURS_PER_DAY);
        StringBuffer sb = new StringBuffer();
        if (days > 0) sb.append(days + " days");
        if (hours > 0) sb.append(" " + hours + " hours");
        if (minutes > 0) sb.append(" " + minutes + " minutes");
        if (seconds > 0) sb.append(" " + seconds + " seconds");
        if (millis > 0) sb.append(" " + millis + " milliseconds");
        return sb.toString();
    }

    public static Date getCurrentDateTime() {
        return new Date(System.currentTimeMillis());
    }

    public static Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Calendar getCurrentCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        return cal;
    }

    public static long getSystemTime() {
        return System.currentTimeMillis();
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static Date dateString2date(String dateString) {
        Date myDate = null;
        if (sdf == null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }
        try {
            myDate = sdf.parse(dateString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return myDate;
    }

    public static Calendar dateString2Calendar(String dateString) {
        Calendar myCal = Calendar.getInstance();
        int year =0;
        int month = -1; //month is 0 based
        int day = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        int milliseconds = 0;
        String date = null;
        String time = null;
        String[] fields = dateString.split(" ");
        date = fields[0];
        String[] dateFields = date.split("-");
        String[] timeFields = null;
        if (fields.length == 2) {
            time = fields[1];
            timeFields = time.split(":");
        }
        int nrOfDateFields = dateFields.length;
        if (nrOfDateFields == 1) {
            int stringLength = date.length();
	        if (stringLength > 3) year += Integer.parseInt(dateString.substring(0, 4));
	        if (stringLength > 5) month += Integer.parseInt(dateString.substring(4, 6));
	        if (stringLength > 7) day += Integer.parseInt(dateString.substring(6, 8));
	        if (stringLength > 9) hours += Integer.parseInt(dateString.substring(8, 10));
	        if (stringLength > 11) minutes += Integer.parseInt(dateString.substring(10, 12));
	        if (stringLength > 13) seconds += Integer.parseInt(dateString.substring(12, 14));
	        if (stringLength > 16) milliseconds += Integer.parseInt(dateString.substring(14, 17));
        } else {
            if (nrOfDateFields > 0) year += Integer.parseInt(dateFields[0]);
            if (nrOfDateFields > 1) month += Integer.parseInt(dateFields[1]);
            if (nrOfDateFields > 2) day += Integer.parseInt(dateFields[2]);
            if (time != null) {
                if (nrOfDateFields > 0) hours += Integer.parseInt(timeFields[0]);
                if (nrOfDateFields > 1) minutes += Integer.parseInt(timeFields[1]);
                if (nrOfDateFields > 2) seconds += Integer.parseInt(timeFields[2]);
                if (nrOfDateFields > 3) milliseconds += Integer.parseInt(timeFields[3]);
            }
        }
        myCal.set(year, month, day, hours, minutes, seconds);
        myCal.set(Calendar.MILLISECOND, milliseconds);
        return myCal;
    }

    public static Calendar date2Calendar(Date date) {
        Calendar myCal = Calendar.getInstance();
        myCal.set(Calendar.HOUR_OF_DAY, 0);
        myCal.set(Calendar.MINUTE, 0);
        myCal.set(Calendar.SECOND, 0);
        myCal.set(Calendar.MILLISECOND, 0);
        myCal.setTime(date);
        return myCal;
    }
}
