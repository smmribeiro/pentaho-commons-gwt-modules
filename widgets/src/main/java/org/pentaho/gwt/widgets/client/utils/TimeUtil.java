/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2020 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.utils;

import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessages;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessagesSingleton;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;

public class TimeUtil {

  private static final WidgetsLocalizedMessages MSGS = WidgetsLocalizedMessagesSingleton.getInstance().getMessages();

  private static final String UTC = "UTC";

  private static final String Z = "Z";

  public static final int HOURS_IN_DAY = 24;

  public static final int MINUTES_IN_HOUR = 60;

  public static final int MINUTES_IN_DAY = HOURS_IN_DAY * MINUTES_IN_HOUR;

  public static final int SECONDS_IN_MINUTE = 60;

  public static final int MILLISECS_IN_SECONDS = 1000;

  public static final int MIN_HOUR = 0;
  public static final int MAX_HOUR = HOURS_IN_DAY / 2;

  public static final int MAX_MINUTE = 60;

  public static final int MAX_SECOND_BY_MILLISEC = Integer.MAX_VALUE / MILLISECS_IN_SECONDS;
  public static final int MAX_MINUTE_BY_MILLISEC = MAX_SECOND_BY_MILLISEC / SECONDS_IN_MINUTE;
  public static final int MAX_HOUR_BY_MILLISEC = MAX_MINUTE_BY_MILLISEC / MINUTES_IN_HOUR;

  public enum TimeOfDay {
    AM( 0, MSGS.am() ), PM( 1, MSGS.pm() );

    TimeOfDay( int value, String name ) {
      this.value = value;
      this.name = name;
    }

    private final int value;

    private final String name;

    private static TimeOfDay[] timeOfDay = { AM, PM };

    public int value() {
      return value;
    }

    public String toString() {
      return name;
    }

    public static TimeOfDay get( int idx ) {
      return timeOfDay[idx];
    }

    public static int length() {
      return timeOfDay.length;
    }

    public static TimeOfDay stringToTimeOfDay( String timeOfDay ) throws EnumException {
      for ( TimeOfDay v : EnumSet.range( TimeOfDay.AM, TimeOfDay.PM ) ) {
        if ( v.toString().equalsIgnoreCase( timeOfDay ) ) {
          return v;
        }
      }
      throw new EnumException( MSGS.invalidStringForTimeOfDay( timeOfDay ) );
    }
  } // end enum TimeOfDay

  /**
   * Names of enum are used as key in resource bundle.
   */
  public enum DayOfWeek {
    SUN( 0, "sunday" ), MON( 1, "monday" ), TUE( 2, "tuesday" ), WED( 3, "wednesday" ), THU( 4,
        "thursday" ), FRI( 5, "friday" ), SAT( 6, "saturday" );

    DayOfWeek( int value, String name ) {
      this.value = value;
      this.name = name;
    }

    private final int value;

    private final String name;

    private static DayOfWeek[] week = { SUN, MON, TUE, WED, THU, FRI, SAT };

    public int value() {
      return value;
    }

    public String toString() {
      return name;
    }

    public static DayOfWeek get( int idx ) {
      return week[idx];
    }

    public static int length() {
      return week.length;
    }

    public int getNext() {
      return ( ordinal() + 1 ) % values().length;
    }

    public int getPrevious() {
      return ( this == SUN ) ? SAT.ordinal() : ordinal() - 1;
    }
  } /* end enum */

  public enum MonthOfYear {
    JAN( 0, "january" ),
    FEB( 1, "february" ),
    MAR( 2, "march" ),
    APR( 3, "april" ),
    MAY( 4, "may" ),
    JUN( 5, "june" ),
    JUL( 6, "july" ),
    AUG( 7, "august" ),
    SEPT( 8, "september" ),
    OCT( 9, "october" ),
    NOV( 10, "november" ),
    DEC( 11, "december" );

    MonthOfYear( int value, String name ) {
      this.value = value;
      this.name = name;
    }

    private final int value;

    private final String name;

    private static MonthOfYear[] year = { JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEPT, OCT, NOV, DEC };

    public int value() {
      return value;
    }

    public String toString() {
      return name;
    }

    public static MonthOfYear get( int idx ) {
      return year[idx];
    }

    public static int length() {
      return year.length;
    }
  } /* end enum */

  public enum WeekOfMonth {
    FIRST( 0, "first_num" ), SECOND( 1, "second_num" ), THIRD( 2, "third_num" ), FOURTH( 3, "fourth_num" ), LAST( 4,
        "last_num" );

    WeekOfMonth( int value, String name ) {
      this.value = value;
      this.name = name;
    }

    private final int value;

    private final String name;

    private static WeekOfMonth[] week = { FIRST, SECOND, THIRD, FOURTH, LAST };

    public int value() {
      return value;
    }

    public String toString() {
      return name;
    }

    public static WeekOfMonth get( int idx ) {
      return week[idx];
    }

    public static int length() {
      return week.length;
    }

    public static WeekOfMonth stringToWeekOfMonth( String weekOfMonth ) throws EnumException {
      for ( WeekOfMonth v : EnumSet.range( WeekOfMonth.FIRST, WeekOfMonth.LAST ) ) {
        if ( v.toString().equals( weekOfMonth ) ) {
          return v;
        }
      }
      throw new EnumException( MSGS.invalidStringForWeekOfMonth( weekOfMonth ) );
    }
  } // end enum WeekOfMonth

  private static Map<MonthOfYear, Integer> validNumDaysOfMonth = new HashMap<MonthOfYear, Integer>();
  static {
    validNumDaysOfMonth.put( MonthOfYear.JAN, 31 );
    validNumDaysOfMonth.put( MonthOfYear.FEB, 29 );
    validNumDaysOfMonth.put( MonthOfYear.MAR, 31 );
    validNumDaysOfMonth.put( MonthOfYear.APR, 30 );
    validNumDaysOfMonth.put( MonthOfYear.MAY, 31 );
    validNumDaysOfMonth.put( MonthOfYear.JUN, 30 );
    validNumDaysOfMonth.put( MonthOfYear.JUL, 31 );
    validNumDaysOfMonth.put( MonthOfYear.AUG, 31 );
    validNumDaysOfMonth.put( MonthOfYear.SEPT, 30 );
    validNumDaysOfMonth.put( MonthOfYear.OCT, 31 );
    validNumDaysOfMonth.put( MonthOfYear.NOV, 30 );
    validNumDaysOfMonth.put( MonthOfYear.DEC, 31 );
  }

  private TimeUtil() {
  } // cannot create instance, static class

  public static long daysToSecs( long days ) {
    return hoursToSecs( days * HOURS_IN_DAY );
  }

  public static long hoursToSecs( long hours ) {
    return minutesToSecs( hours * MINUTES_IN_HOUR );
  }

  public static long minutesToSecs( long minutes ) {
    return minutes * SECONDS_IN_MINUTE;
  }

  public static long secsToMillisecs( long secs ) {
    return secs * MILLISECS_IN_SECONDS;
  }

  public static long secsToDays( long secs ) {
    return secs / HOURS_IN_DAY / MINUTES_IN_HOUR / SECONDS_IN_MINUTE;
  }

  public static long secsToHours( long secs ) {
    return secs / MINUTES_IN_HOUR / SECONDS_IN_MINUTE;
  }

  public static long secsToMinutes( long secs ) {
    return secs / SECONDS_IN_MINUTE;
  }

  public static boolean isSecondsWholeDay( long secs ) {
    return ( daysToSecs( secsToDays( secs ) ) ) == secs;
  }

  public static boolean isSecondsWholeHour( long secs ) {
    return ( hoursToSecs( secsToHours( secs ) ) ) == secs;
  }

  public static boolean isSecondsWholeMinute( long secs ) {
    return ( minutesToSecs( secsToMinutes( secs ) ) ) == secs;
  }

  /**
   * Time of day is element of {AM, PM}
   * 
   * @param hour
   * @return
   */
  public static TimeOfDay getTimeOfDayBy0To23Hour( String hour ) {
    return getTimeOfDayBy0To23Hour( Integer.parseInt( hour ) );
  }

  public static TimeOfDay getTimeOfDayBy0To23Hour( int hour ) {
    return hour < MAX_HOUR ? TimeOfDay.AM : TimeOfDay.PM;
  }

  /**
   * Convert a 24 hour time, where hours are 0-23, to a twelve hour time, where 0-23 maps to 0...11 AM and 0...11 PM
   * 
   * @param int0To23hour
   *          int integer in the range 0..23
   * @return int integer in the range 0..11
   */
  public static int to12HourClock( int int0To23hour ) {
    assert int0To23hour >= 0 && int0To23hour <= 23 : "int0To23hour is out of range"; //$NON-NLS-1$

    int int0To11 = int0To23hour < MAX_HOUR ? int0To23hour : int0To23hour - MAX_HOUR;
    return int0To11;
  }

  /**
   * @param time
   *          String it will look like: '17:17:00 PM' for 5:17 PM
   */
  public static String to12HourClock( String time ) {
    String[] parts = time.split( ":" ); //$NON-NLS-1$
    int hour = Integer.parseInt( parts[0] );
    if ( hour > MAX_HOUR ) {
      hour -= MAX_HOUR;
    }
    if ( hour == 0 ) {
      hour = MAX_HOUR;
    }
    return Integer.toString( hour ) + ":" + parts[1] + ":" + parts[2]; //$NON-NLS-1$//$NON-NLS-2$
  }

  /**
   * map 0..11 to 12,1..11
   */
  public static int map0Through11To12Through11( int int0To11 ) {
    return int0To11 == 0 ? 12 : int0To11;
  }

  // NOTE: this method will produce rounding errors, since it doesn't round, it truncates
  public static long millsecondsToSecs( long milliseconds ) {
    return milliseconds / MILLISECS_IN_SECONDS;
  }

  /**
   * TODO sbarkdull, gwt 1.5 has a DateTimeFormatter, change this method to use it.
   * 
   * sample output: May 21, 2008 8:29:21 PM This is consistent with the formatter constructed like this: DateFormat
   * formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, Locale.getDefault());
   */
  public static String getDateTimeString( String month, String dayInMonth, String year, String hour, String minute,
      String second, TimeOfDay timeOfDay ) {
    return new StringBuilder().append( getDateString( month, dayInMonth, year ) ).append( " " )
        .append( hour ).append( ":" )
        .append( minute ).append( ":" )
        .append( second ).append( " " )
        .append( timeOfDay.toString() ).toString();
  }

  // this code runs in a single thread, so it is ok to share these two DateTimeFormats
  private static DateTimeFormat dateFormatter = DateTimeFormat.getFormat( MSGS.dateFormat() );
  private static DateTimeFormat dateTimeFormatter = DateTimeFormat.getFormat( MSGS.dateFormatLongMedium() );

  public static Date getDateTime( String time, Date date ) {
    String strDate = dateFormatter.format( date );
    return dateTimeFormatter.parse( strDate + " " + time ); //$NON-NLS-1$
  }

  /**
   * 
   * @param strDate
   *          String representing the date, in this format: MMM dd, yyyy HH:mm:ss a
   * @return
   */
  public static Date getDate( String strDate ) {
    return dateTimeFormatter.parse( strDate );
  }

  /**
   * Get the time part of a date string.
   * 
   * @param dateTime
   *          String in this format: MMM dd, yyyy HH:mm:ss a
   * @return String HH:mm:ss a
   */
  public static String getTimePart( String dateTime ) {
    String[] parts = dateTime.split( "\\s" ); //$NON-NLS-1$

    // TODO sbarkdull, use StringBuilder
    return parts[3] + " " + parts[4]; //$NON-NLS-1$
  }

  /**
   * Get the time part of a date string.
   * 
   * @param dateTime
   *          Date
   * @return String HH:mm:ss a
   */
  public static String getTimePart( Date dateTime ) {
    String strDateTime = dateTimeFormatter.format( dateTime );
    return to12HourClock( getTimePart( strDateTime ) );
  }

  /**
   * Get the time part of a date string.
   * 
   * @param dateTime
   *          String in this format: MMM dd, yyyy HH:mm:ss a
   * @return String HH:mm:ss a
   */
  public static String getDatePart( String dateTime ) {
    String[] parts = dateTime.split( "\\s" ); //$NON-NLS-1$
    // TODO sbarkdull, use StringBuilder
    return parts[0] + " " + parts[1] + " " + parts[2]; //$NON-NLS-1$ //$NON-NLS-2$
  }

  public static String get0thTime() {
    // TODO sbarkdull, use StringBuilder
    return "12:00:00 " + TimeOfDay.AM.toString(); //$NON-NLS-1$
  }

  public static String zeroTimePart( String dateTime ) {
    // TODO sbarkdull, use StringBuilder
    return getDatePart( dateTime ) + " " + get0thTime(); //$NON-NLS-1$
  }

  public static Date zeroTimePart( Date dateTime ) {
    // TODO sbarkdull, use StringBuilder
    Date newDate = (Date) dateTime.clone();
    newDate.setHours( 0 );
    newDate.setSeconds( 0 );
    newDate.setMinutes( 0 );
    return newDate;
  }

  /**
   * TODO sbarkdull, gwt 1.5 has a DateTimeFormatter, change this method to use it.
   * 
   * sample output: May 21, 2008 This is consistent with the formatter constructed like this: DateFormat formatter =
   * DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
   */
  public static String getDateString( String month, String dayInMonth, String year ) {
    return new StringBuilder().append( month ).append( " " ) //$NON-NLS-1$
        .append( dayInMonth ).append( ", " ) //$NON-NLS-1$
        .append( year ).toString();
  }

  public static boolean isValidNumOfDaysForMonth( int numDays, MonthOfYear month ) {
    if ( numDays < 1 ) {
      return false;
    } else {
      return validNumDaysOfMonth.get( month ) <= numDays;
    }
  }

  /**
   * Is <param>num</param> between <param>low</param> and <param>high</param>, inclusive.
   * 
   * @param low
   * @param num
   * @param high
   * @return boolean true if <param>num</param> between <param>low</param> and <param>high</param>, inclusive, else
   *         false.
   */
  private static boolean isNumBetween( long low, long num, long high ) {
    return num >= low && num <= high;
  }

  public static boolean isDayOfMonth( int num ) {
    return isNumBetween( 1, num, 31 );
  }

  public static boolean isDayOfWeek( int num ) {
    return isNumBetween( 1, num, 7 );
  }

  public static boolean isWeekOfMonth( int num ) {
    return isNumBetween( 1, num, 4 );
  }

  public static boolean isMonthOfYear( int num ) {
    return isNumBetween( 1, num, 12 );
  }

  public static boolean isSecond( int num ) {
    return isNumBetween( 0, num, 59 );
  }

  public static boolean isMinute( int num ) {
    return isNumBetween( 0, num, 59 );
  }

  public static boolean isHour( int num ) {
    return isNumBetween( 0, num, 23 );
  }

  /**
   * <p>Calculates day variance between two timezones for a specified time (hour and minute).</p>
   * <p/>
   * <p>There are two different types of target timezone information which are supported:</p>
   * <lo>
   * <li>timezone id - e.g. "Eastern Daylight Time (UTC-0500)"</li>
   * <li>dateTime format - e.g. "2018-02-27T07:30:00-05:00"</li>
   * </lo>
   *
   * @param originTimezoneInfo The origin timezone information in one of the formats described above
   * @param targetTimezoneInfo The target timezone information in one of the formats described above
   * @param originHour The hour value in the origin
   * @param originMinute The minute value in the origin
   * @return The calculated day variance for the given time
   */
  public static int getDayVariance( String originTimezoneInfo, String targetTimezoneInfo, int originHour,
                                    int originMinute ) {

    int originOffset =
      ( null != originTimezoneInfo ) ? getTimezoneOffset( originTimezoneInfo ) : -getClientOffsetTimeZone();
    int targetOffset = getTimezoneOffset( targetTimezoneInfo );

    int allMinutesOrigin = MINUTES_IN_HOUR * originHour + originMinute;

    if ( originOffset > targetOffset ) {
      // origin is ahead of target
      int timezoneDiff = targetOffset - originOffset;
      int allMinutesTarget = allMinutesOrigin + timezoneDiff;
      if ( allMinutesTarget < 0 ) {
        // It's possible to have a difference greater than 1 day between timezones
        return ( allMinutesTarget >= -MINUTES_IN_DAY ) ? -1 : -2;
      }
    } else {
      // target is ahead of (or the same as) origin
      int timezoneDiff = originOffset - targetOffset;
      int allMinutesTarget = allMinutesOrigin - timezoneDiff;
      if ( allMinutesTarget >= MINUTES_IN_DAY ) {
        // It's possible to have a difference greater than 1 day between timezones
        return ( allMinutesTarget < 2 * MINUTES_IN_DAY ) ? 1 : 2;
      }
    }

    return 0;
  }

  /**
   * <p>Calculates day variance based on target timezone information.</p>
   * <p/>
   * <p><b>Please do not use this method:</b></p>
   * <lo>
   * <li>it only handles 'whole hour' timezones</li>
   * <li>it only accepts the target timezone (the way the origin timezone is calculated is not reliable)</li>
   * </lo>
   * <p/>
   * <p>To support all timezone variants, use {@link #getDayVariance(String, String, int, int)}.</p>
   *
   * @param selectedTime The time selected by the user which is compared against the timezone diff between client and target
   * @param targetTimezoneInfo The target timezone information in the formats described above
   * @return The calculated day variance
   * @deprecated Please use {@link #getDayVariance(String, String, int, int)}
   */
  @Deprecated
  public static int getDayVariance( int selectedTime, String targetTimezoneInfo ) {
    return getDayVariance( null, targetTimezoneInfo, selectedTime, 0 );
  }

  /**
   * <p>Retrieves the timezone offset (in minutes) based on the timezone information.</p>
   * <p/>
   * <p>Note: The following two types of timezone information are supported:</p>
   * <lo>
   * <li>timezone id - e.g. "Eastern Daylight Time (UTC-0500)"</li>
   * <li>dateTime format - e.g. "2018-02-27T07:30:00-05:00"</li>
   * </lo>
   *
   * @param timezoneInfo the timezone information
   * @return timezone offset in minutes
   */
  public static int getTimezoneOffset( String timezoneInfo ) {
    String targetTzOffset;

    if ( timezoneInfo.endsWith( Z ) ) {
      return 0;
    } else if ( timezoneInfo.contains( UTC ) ) {
      targetTzOffset =
        timezoneInfo.substring( timezoneInfo.indexOf( "(UTC" ) + 4, timezoneInfo.length() - 1 );
    } else {
      int startingOffsetChar =
        Character.isDigit( timezoneInfo.charAt( timezoneInfo.length() - 6 ) ) ? 5 : 6;
      targetTzOffset = timezoneInfo.substring( timezoneInfo.length() - startingOffsetChar );
      targetTzOffset = targetTzOffset.replace( ":", "" );
    }

    int hour = Integer.parseInt( targetTzOffset.substring( 1, 3 ) );
    int minute = Integer.parseInt( targetTzOffset.substring( 3 ) );
    int targetOffset = MINUTES_IN_HOUR * hour + minute;

    // Determine the signal separately, otherwise a NumberFormatException will occur if we try to convert directly
    // the signal to integer.
    if ( targetTzOffset.charAt( 0 ) == '-' ) {
      targetOffset = -targetOffset;
    }
    return targetOffset;
  }

  /**
   * Given the current day of week, calculate the new day of week based on the variance.
   *
   * @param currentDay the current day
   * @param dayVariance the day variance which will be applied on the current day
   * @return the calculated day of week
   */
  public static int getDayOfWeek( DayOfWeek currentDay, int dayVariance ) {

    for ( int i = Math.abs( dayVariance ); i > 0; --i ) {
      currentDay = DayOfWeek.get( ( dayVariance > 0 ) ? currentDay.getNext() : currentDay.getPrevious() );
    }

    return currentDay.ordinal();
  }

  public static native int getClientOffsetTimeZone() /*-{
      return new Date().getTimezoneOffset();
  }-*/;
}
