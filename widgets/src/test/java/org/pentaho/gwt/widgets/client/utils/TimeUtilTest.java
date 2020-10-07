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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.pentaho.gwt.widgets.client.utils.TimeUtil.getTimeOfDayBy0To23Hour;
import static org.pentaho.gwt.widgets.client.utils.TimeUtil.hoursToSecs;
import static org.pentaho.gwt.widgets.client.utils.TimeUtil.isSecondsWholeDay;
import static org.pentaho.gwt.widgets.client.utils.TimeUtil.isSecondsWholeHour;
import static org.pentaho.gwt.widgets.client.utils.TimeUtil.isSecondsWholeMinute;
import static org.pentaho.gwt.widgets.client.utils.TimeUtil.minutesToSecs;
import static org.pentaho.gwt.widgets.client.utils.TimeUtil.secsToDays;
import static org.pentaho.gwt.widgets.client.utils.TimeUtil.secsToHours;
import static org.pentaho.gwt.widgets.client.utils.TimeUtil.secsToMinutes;
import static org.pentaho.gwt.widgets.client.utils.TimeUtil.to12HourClock;

@RunWith( GwtMockitoTestRunner.class )
public class TimeUtilTest {
  @Test
  public void testGetDayVariance_timezoneIdFormat() {
    String timezoneIdFormat = "Eastern Daylight Time (UTC-0500)";
    int dayVariance = TimeUtil.getDayVariance( 3, timezoneIdFormat );
    assertEquals( dayVariance, -1 );

    timezoneIdFormat = "Japan Daylight Time (UTC+0900)";
    dayVariance = TimeUtil.getDayVariance( 20, timezoneIdFormat );
    assertEquals( dayVariance, 1 );
  }

  @Test
  public void testGetDayVariance_timezoneIdFormat_Null_EDT() {
    String timezoneIdFormat = "Eastern Daylight Time (UTC-0500)";

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting minus one ('-1') for times between 0:00 and 4:59 and zero ('0') for times between 5:00 and 23:59
        int expectedDayVariance = ( h < 5 ) ? -1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance, TimeUtil.getDayVariance( null, timezoneIdFormat, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_timezoneIdFormat_Null_JDT() {
    String timezoneIdFormat = "Japan Daylight Time (UTC+0900)";

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting zero ('0') for times between 0:00 and 14:59 and one ('1') for times between 15:00 and 23:59
        int expectedDayVariance = ( h < 15 ) ? 0 : 1;

        assertEquals( h + ":" + m, expectedDayVariance, TimeUtil.getDayVariance( null, timezoneIdFormat, h, m ) );
      }
    }
  }

  // Some Timezones have half an hour differences; example: IST - Indian Standard Time (UTC+0530)
  @Test
  public void testGetDayVariance_timezoneIdFormat_Null_IST() {
    String timezoneIdFormat = "Indian Standard Time (UTC+0530)";

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting zero ('0') for times between 0:00 and 18:29 and one ('1') for times between 18:30 and 23:59
        int expectedDayVariance = ( h > 18 || ( h == 18 && m >= 30 ) ) ? 1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance, TimeUtil.getDayVariance( null, timezoneIdFormat, h, m ) );
      }
    }
  }

  // Some Timezones have three quarters of an hour differences; example: NPT - Nepal (UTC+0545)
  @Test
  public void testGetDayVariance_timezoneIdFormat_Null_NPT() {
    String timezoneIdFormat = "Nepal (UTC+0545)";

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting zero ('0') for times between 0:00 and 18:14 and one ('1') for times between 18:15 and 23:59
        int expectedDayVariance = ( h > 18 || ( h == 18 && m >= 15 ) ) ? 1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance, TimeUtil.getDayVariance( null, timezoneIdFormat, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_timezoneIdFormat_BST_NPT() {
    String timezone1IdFormat = "BST (UTC+0100)";
    String timezone2IdFormat = "Nepal (UTC+0545)";

    // (UTC+0100) vs (UTC+0545) is equivalent to (UTC+0445)

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting zero ('0') for times between 0:00 and 19:14 and one ('1') for times between 19:15 and 23:59
        int expectedDayVariance = ( h > 19 || ( h == 19 && m >= 15 ) ) ? 1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance,
          TimeUtil.getDayVariance( timezone1IdFormat, timezone2IdFormat, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_timezoneIdFormat_NPT_BST() {
    String timezone1IdFormat = "Nepal (UTC+0545)";
    String timezone2IdFormat = "BST (UTC+0100)";

    // (UTC+0545) vs (UTC+0100) is equivalent to (UTC-0445)

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting minus one ('-1') for times between 0:00 and 4:44 and zero ('0') for times between 4:45 and 23:59
        int expectedDayVariance = ( h < 4 || ( h == 4 && m < 45 ) ) ? -1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance,
          TimeUtil.getDayVariance( timezone1IdFormat, timezone2IdFormat, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_timezoneIdFormat_IST_NPT() {
    String timezone1IdFormat = "Indian Standard Time (UTC+0530)";
    String timezone2IdFormat = "Nepal (UTC+0545)";

    // (UTC+0530) vs (UTC+0545) is equivalent to (UTC+0015)

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting one ('1') for times between 0:00 and 23:45 and zero ('0') for times between 23:46 and 23:59
        int expectedDayVariance = ( h == 23 && m >= 45 ) ? 1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance,
          TimeUtil.getDayVariance( timezone1IdFormat, timezone2IdFormat, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_timezoneIdFormat_NPT_IST() {
    String timezone1IdFormat = "Nepal (UTC+0545)";
    String timezone2IdFormat = "Indian Standard Time (UTC+0530)";

    // (UTC+0545) vs (UTC+0530) is equivalent to (UTC-0015)

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting minus one ('-1') for times between 0:00 and 0:14 and zero ('0') for times between 0:15 and 23:59
        int expectedDayVariance = ( h == 0 && m < 15 ) ? -1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance,
          TimeUtil.getDayVariance( timezone1IdFormat, timezone2IdFormat, h, m ) );
      }
    }
  }

  // 'UTC+1400' and 'UTC-1200' (LINT and AoE, respectively) are the extreme Timezones that currently exist
  @Test
  public void testGetDayVariance_timezoneIdFormat_LINT_AOE() {
    String timezone1IdFormat = "LINT (UTC+1400)";
    String timezone2IdFormat = "AoE (UTC-1200)";

    // (UTC+1400) vs (UTC-1200) is equivalent to (UTC-2600)

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting minus two ('-2') for times between 0:00 and 1:59 and one ('1') for times between 2:00 and 23:59
        int expectedDayVariance = ( h < 2 ) ? -2 : -1;

        assertEquals( h + ":" + m, expectedDayVariance,
          TimeUtil.getDayVariance( timezone1IdFormat, timezone2IdFormat, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_timezoneIdFormat_AOE_LINT() {
    String timezone1IdFormat = "AoE (UTC-1200)";
    String timezone2IdFormat = "LINT (UTC+1400)";

    // (UTC-1200) vs (UTC+1400) is equivalent to (UTC+2600)

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting one ('1') for times between 0:00 and 21:59 and two ('2') for times between 22:00 and 23:59
        int expectedDayVariance = ( h < 22 ) ? 1 : 2;

        assertEquals( h + ":" + m, expectedDayVariance,
          TimeUtil.getDayVariance( timezone1IdFormat, timezone2IdFormat, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_dateTimeFormat() {
    String dateTimeFormat = "2018-01-01T07:30:00-05:00";
    int dayVariance = TimeUtil.getDayVariance( 3, dateTimeFormat );
    assertEquals( -1, dayVariance );

    dateTimeFormat = "2018-01-01T07:30:00+05:00";
    dayVariance = TimeUtil.getDayVariance( 20, dateTimeFormat );
    assertEquals( 1, dayVariance );
  }

  @Test
  public void testGetDayVariance_dateTimeFormat_Null_EDT() {
    String dateTimeFormat = "2018-01-01T07:30:00-05:00";

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting minus one ('-1') for times between 0:00 and 4:59 and zero ('0') for times between 5:00 and 23:59
        int expectedDayVariance = ( h < 5 ) ? -1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance, TimeUtil.getDayVariance( null, dateTimeFormat, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_dateTimeFormat_Null_JDT() {
    String dateTimeFormat = "2018-01-01T07:30:00+09:00";

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting zero ('0') for times between 0:00 and 14:59 and one ('1') for times between 15:00 and 23:59
        int expectedDayVariance = ( h < 15 ) ? 0 : 1;

        assertEquals( h + ":" + m, expectedDayVariance, TimeUtil.getDayVariance( null, dateTimeFormat, h, m ) );
      }
    }
  }

  // Some Timezones have half an hour differences; example: IST - Indian Standard Time (UTC+0530)
  @Test
  public void testGetDayVariance_dateTimeFormat_Null_IST() {
    String dateTimeFormat = "2018-01-01T07:30:00+05:30";

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting zero ('0') for times between 0:00 and 18:29 and one ('1') for times between 18:30 and 23:59
        int expectedDayVariance = ( h > 18 || ( h == 18 && m >= 30 ) ) ? 1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance, TimeUtil.getDayVariance( null, dateTimeFormat, h, m ) );
      }
    }
  }

  // Some Timezones have three quarters of an hour differences; example: NPT - Nepal (UTC+0545)
  @Test
  public void testGetDayVariance_dateTimeFormat_Null_NPT() {
    String dateTimeFormat = "2018-01-01T07:30:00+05:45";

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting zero ('0') for times between 0:00 and 18:14 and one ('1') for times between 18:15 and 23:59
        int expectedDayVariance = ( h > 18 || ( h == 18 && m >= 15 ) ) ? 1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance, TimeUtil.getDayVariance( null, dateTimeFormat, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_dateTimeFormat_BST_NPT() {
    String dateTime1Format = "2018-01-01T07:30:00+01:00";
    String dateTime2Format = "2018-01-01T07:30:00+05:45";

    // (+01:00) vs (+05:45) is equivalent to (+04:45)

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting zero ('0') for times between 0:00 and 19:14 and one ('1') for times between 19:15 and 23:59
        int expectedDayVariance = ( h > 19 || ( h == 19 && m >= 15 ) ) ? 1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance,
          TimeUtil.getDayVariance( dateTime1Format, dateTime2Format, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_dateTimeFormat_NPT_BST() {
    String dateTime1Format = "2018-01-01T07:30:00+05:45";
    String dateTime2Format = "2018-01-01T07:30:00+01:00";

    // (+05:45) vs (+01:00) is equivalent to (UTC-0445)

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting minus one ('-1') for times between 0:00 and 4:44 and zero ('0') for times between 4:45 and 23:59
        int expectedDayVariance = ( h < 4 || ( h == 4 && m < 45 ) ) ? -1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance,
          TimeUtil.getDayVariance( dateTime1Format, dateTime2Format, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_dateTimeFormat_IST_NPT() {
    String dateTime1Format = "2018-01-01T07:30:00+05:30";
    String dateTime2Format = "2018-01-01T07:30:00+05:45";

    // (+05:30) vs (+05:45) is equivalent to (+00:15)

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting one ('1') for times between 0:00 and 23:45 and zero ('0') for times between 23:46 and 23:59
        int expectedDayVariance = ( h == 23 && m >= 45 ) ? 1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance,
          TimeUtil.getDayVariance( dateTime1Format, dateTime2Format, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_dateTimeFormat_NPT_IST() {
    String dateTime1Format = "2018-01-01T07:30:00+05:45";
    String dateTime2Format = "2018-01-01T07:30:00+05:30";

    // (+05:45) vs (+05:30) is equivalent to (-00:15)

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting minus one ('-1') for times between 0:00 and 0:14 and zero ('0') for times between 0:15 and 23:59
        int expectedDayVariance = ( h == 0 && m < 15 ) ? -1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance,
          TimeUtil.getDayVariance( dateTime1Format, dateTime2Format, h, m ) );
      }
    }
  }

  // '+14:00' and '-12:00' (LINT and AoE, respectively) are the extreme Timezones that currently exist
  @Test
  public void testGetDayVariance_dateTimeFormat_LINT_AOE() {
    String dateTime1Format = "2018-01-01T07:30:00+14:00";
    String dateTime2Format = "2018-01-01T07:30:00-12:00";

    // (+14:00) vs (-12:00) is equivalent to (-26:00)

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting minus two ('-2') for times between 0:00 and 1:59 and one ('1') for times between 2:00 and 23:59
        int expectedDayVariance = ( h < 2 ) ? -2 : -1;

        assertEquals( h + ":" + m, expectedDayVariance,
          TimeUtil.getDayVariance( dateTime1Format, dateTime2Format, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_dateTimeFormat_AOE_LINT() {
    String dateTime1Format = "2018-01-01T07:30:00-12:00";
    String dateTime2Format = "2018-01-01T07:30:00+14:00";

    // (-12:00) vs (+14:00) is equivalent to (+26:00)

    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting one ('1') for times between 0:00 and 21:59 and two ('2') for times between 22:00 and 23:59
        int expectedDayVariance = ( h < 22 ) ? 1 : 2;

        assertEquals( h + ":" + m, expectedDayVariance,
          TimeUtil.getDayVariance( dateTime1Format, dateTime2Format, h, m ) );
      }
    }
  }

  @Test
  public void testGetDayVariance_dateTimeFormat_Atlanta_SFrancisco() {
    String dateTime1Format = "2018-01-01T07:30:00+00:00";
    String dateTime2Format = "2018-01-01T07:30:00+05:30";

    // (-04:00) vs (-07:00) is equivalent to (+03:00)


    for ( int h = 0; h < 24; ++h ) {
      for ( int m = 0; m < 60; ++m ) {
        // Expecting zero ('0') for times between 0:00 and 18:29 and one ('1') for times between 18:30 and 23:59
        int expectedDayVariance = ( h > 18 || ( h == 18 && m >= 30 ) ) ? 1 : 0;

        assertEquals( h + ":" + m, expectedDayVariance, TimeUtil.getDayVariance( dateTime1Format, dateTime2Format, h, m ) );
      }
    }
  }
  @Test
  public void testGetDayOfWeek_next() {
    int nextDayOfWeek = TimeUtil.getDayOfWeek( TimeUtil.DayOfWeek.MON, 1 );
    assertEquals( TimeUtil.DayOfWeek.TUE.ordinal(), nextDayOfWeek );
  }

  @Test
  public void testGetDayOfWeek_next_3() {
    int nextDayOfWeek = TimeUtil.getDayOfWeek( TimeUtil.DayOfWeek.MON, 3 );
    assertEquals( TimeUtil.DayOfWeek.THU.ordinal(), nextDayOfWeek );
  }

  @Test
  public void testGetDayOfWeek_previous() {
    int previousDayOfWeek = TimeUtil.getDayOfWeek( TimeUtil.DayOfWeek.MON, -1 );
    assertEquals( TimeUtil.DayOfWeek.SUN.ordinal(), previousDayOfWeek );
  }

  @Test
  public void testGetDayOfWeek_previous_4() {
    int previousDayOfWeek = TimeUtil.getDayOfWeek( TimeUtil.DayOfWeek.MON, -3 );
    assertEquals( TimeUtil.DayOfWeek.FRI.ordinal(), previousDayOfWeek );
  }

  @Test
  public void testDaysToSec() {
    assertEquals( 1123200, TimeUtil.daysToSecs( 13 ) );
    assertNotEquals( 1123201, TimeUtil.daysToSecs( 13 ) );
    assertNotEquals( 1123199, TimeUtil.daysToSecs( 13 ) );
  }

  @Test
  public void testHoursToSecs() {
    assertEquals( 46800, hoursToSecs( 13 ) );
    assertNotEquals( 46801, hoursToSecs( 13 ) );
    assertNotEquals( 46799, hoursToSecs( 13 ) );
  }

  @Test
  public void testMinutesToSecs() {
    assertEquals( 780, minutesToSecs( 13 ) );
    assertNotEquals( 781, minutesToSecs( 13 ) );
    assertNotEquals( 779, minutesToSecs( 13 ) );
  }

  @Test
  public void testSecsToDays() {
    assertEquals( 13, secsToDays( 1123200 ) );
    assertEquals( 13, secsToDays( 1123201 ) );
    assertNotEquals( 13, secsToDays( 1123199 ) );
  }

  @Test
  public void testSecsToHours() {
    assertEquals( 13, secsToHours( 46800 ) );
    assertEquals( 13, secsToHours( 46801 ) );
    assertNotEquals( 13, secsToHours( 46799 ) );
  }

  @Test
  public void testSecsToMinutes() {
    assertEquals( 13, secsToMinutes( 780 ) );
    assertEquals( 13, secsToMinutes( 781 ) );
    assertNotEquals( 13, secsToMinutes( 779 ) );
  }

  @Test
  public void testIsSecondsWholeDay() {
    assertTrue( isSecondsWholeDay( 1123200 ) );
    assertFalse( isSecondsWholeDay( 1123201 ) );
    assertFalse( isSecondsWholeDay( 1123199 ) );
  }

  @Test
  public void testIsSecondsWholeHour() {
    assertTrue( isSecondsWholeHour( 46800 ) );
    assertFalse( isSecondsWholeHour( 46801 ) );
    assertFalse( isSecondsWholeHour( 46799 ) );
  }

  @Test
  public void testIsSecondsWholeMinute() {
    assertTrue( isSecondsWholeMinute( 780 ) );
    assertFalse( isSecondsWholeMinute( 781 ) );
    assertFalse( isSecondsWholeMinute( 779 ) );
  }

  @Test
  public void testGetTimeOfDayBy0To23Hour() {
    assertEquals( TimeUtil.TimeOfDay.AM, getTimeOfDayBy0To23Hour( "0" ) );
    assertEquals( TimeUtil.TimeOfDay.AM, getTimeOfDayBy0To23Hour( "11" ) );
    assertEquals( TimeUtil.TimeOfDay.PM, getTimeOfDayBy0To23Hour( "12" ) );
    assertEquals( TimeUtil.TimeOfDay.PM, getTimeOfDayBy0To23Hour( "13" ) );
    assertEquals( TimeUtil.TimeOfDay.PM, getTimeOfDayBy0To23Hour( "23" ) );
  }

  @Test
  public void testTo12HourClock() {
    assertEquals( 0, to12HourClock( 0 ) );
    assertEquals( 11, to12HourClock( 11 ) );
    assertEquals( 0, to12HourClock( 12 ) );
    assertEquals( 11, to12HourClock( 23 ) );
  }
}
