/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.gwt.widgets.client.controls;

import org.pentaho.gwt.widgets.client.ui.ICallback;
import org.pentaho.gwt.widgets.client.ui.IChangeHandler;
import org.pentaho.gwt.widgets.client.utils.TimeUtil;
import org.pentaho.gwt.widgets.client.utils.TimeUtil.TimeOfDay;

import java.util.Date;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

// TODO sbarkdull, this code needs to be locale sensitive

/**
 * Although hours combo displays the range 1...12, the hour setter/getters take/return the range 0...11. So setting the
 * hour to 0 will cause 1 to be selected in the combo.
 * 
 * @author Steven Barkdull
 * 
 */
@SuppressWarnings( "deprecation" )
public class TimePicker extends HorizontalPanel implements IChangeHandler {
  protected ListBox hourLB = new ListBox();
  protected ListBox minuteLB = new ListBox();
  protected ListBox timeOfDayLB = new ListBox();
  private ICallback<IChangeHandler> onChangeHandler = null;

  private static String DATE_PICKER_SELECT = "date-picker-select";

  public TimePicker() {
    setSpacing( 5 );
    // hour list
    initHourLB();

    // minute list
    initMinuteLB();

    // second list
    // nah!

    // AM/PM list
    initAmPmLB();

    add( hourLB );
    add( minuteLB );
    add( timeOfDayLB );
    configureOnChangeHandler();
  }

  public void setTime( Date date ) {
    int hIndex = date.getHours() - 1;
    hIndex = hIndex > 10 ? hIndex - 12 : hIndex;
    hIndex = hIndex == -1 ? 11 : hIndex;
    hourLB.setSelectedIndex( hIndex );
    minuteLB.setSelectedIndex( date.getMinutes() );
    setTimeOfDay( date.getHours() > 11 ? TimeOfDay.PM : TimeOfDay.AM );
  }

  /**
   * format of string should be: HH:MM:SS AM/PM, e.g. 7:12:28 PM
   * 
   * @param time
   */
  public void setTime( String time ) {
    String[] parts = time.split( ":" ); //$NON-NLS-1$
    String[] lastParts = parts[2].split( "\\s" ); //$NON-NLS-1$
    setHour( parts[0] );
    setMinute( parts[1] );
    // setSeconds( lastParts[0] );
    TimeOfDay td = TimeOfDay.stringToTimeOfDay( lastParts[1] );
    setTimeOfDay( td );
  }

  /**
   * format of string should be: HH:MM:SS AM/PM, e.g. 7:12:28 PM
   * 
   * @return
   */
  public String getTime() {
    StringBuilder sb = new StringBuilder( getHour() ).append( ":" ) //$NON-NLS-1$
        .append( getMinute() ).append( ":" ) //$NON-NLS-1$
        .append( "00 " ) //$NON-NLS-1$
        .append( getTimeOfDay().toString() );

    return sb.toString();
  }

  private void initHourLB() {
    hourLB.setVisibleItemCount( 1 );
    for ( int ii = 1; ii <= TimeUtil.MAX_HOUR; ++ii ) {
      String strHrDisplay = Integer.toString( ii );
      strHrDisplay = ( strHrDisplay.length() == 1 ) ? "0" + strHrDisplay : strHrDisplay; // left pad single digit values with 0 //$NON-NLS-1$
      String strHrValue = ( ii == 12 ) ? "0" : Integer.toString( ii ); //$NON-NLS-1$
      hourLB.addItem( strHrDisplay, strHrValue );
    }
    hourLB.setStyleName( DATE_PICKER_SELECT );
  }

  private void initMinuteLB() {
    minuteLB.setVisibleItemCount( 1 );
    for ( int ii = 0; ii < TimeUtil.MAX_MINUTE; ++ii ) {
      String strMinute = Integer.toString( ii );
      strMinute = ( strMinute.length() == 1 ) ? "0" + strMinute : strMinute; // left pad single digit values with 0 //$NON-NLS-1$
      minuteLB.addItem( strMinute );
    }
    minuteLB.setStyleName( DATE_PICKER_SELECT );
  }

  private void initAmPmLB() {
    timeOfDayLB.setVisibleItemCount( 1 );
    timeOfDayLB.addItem( TimeUtil.TimeOfDay.AM.toString() );
    timeOfDayLB.addItem( TimeUtil.TimeOfDay.PM.toString() );
    timeOfDayLB.setStyleName( DATE_PICKER_SELECT );
  }

  /**
   * Get the value (not the text) of the current selected item
   * 
   * NOTE: hours are 0...11, in spite of the fact that the combo displays 1...12 0...11 maps to 12,1,...11
   * 
   * @return
   */
  public String getHour() {
    return hourLB.getValue( hourLB.getSelectedIndex() );
  }

  /**
   * Set the hour you want to see visible in the control (i.e. 1,2,3,..12)
   * 
   * NOTE: hours are 0...11, in spite of the fact that the combo displays 1...12 0...11 maps to 12,1,...11
   * 
   * @return
   */
  public void setHour( String hour ) {
    setHour( Integer.parseInt( hour ) );
  }

  public void setHour( int hour ) {
    hourLB.setSelectedIndex( hour - 1 );
  }

  /**
   * Get the value (not the text) of the current selected item
   * 
   * @return
   */
  public String getMinute() {
    return minuteLB.getValue( minuteLB.getSelectedIndex() );
  }

  /**
   * Set the minute you want to see visible in the control
   * 
   * @param minute
   */
  public void setMinute( String minute ) {
    setMinute( Integer.parseInt( minute ) );
  }

  public void setMinute( int minute ) {
    minuteLB.setSelectedIndex( minute );
  }

  public TimeUtil.TimeOfDay getTimeOfDay() {
    return TimeUtil.TimeOfDay.get( timeOfDayLB.getSelectedIndex() );
  }

  public void setTimeOfDay( TimeUtil.TimeOfDay timeOfDay ) {
    timeOfDayLB.setSelectedIndex( timeOfDay.value() );
  }

  @Override
  public void setOnChangeHandler( ICallback<IChangeHandler> handler ) {
    onChangeHandler = handler;
  }

  private void changeHandler() {
    if ( null != onChangeHandler ) {
      onChangeHandler.onHandle( this );
    }
  }

  private void configureOnChangeHandler() {
    final TimePicker localThis = this;

    ChangeListener changeListener = new ChangeListener() {
      @Override
      public void onChange( Widget sender ) {
        localThis.changeHandler();
      }
    };
    hourLB.addChangeListener( changeListener );
    minuteLB.addChangeListener( changeListener );
    timeOfDayLB.addChangeListener( changeListener );
  }

  public void setEnabled( boolean enabled ) {
    hourLB.setEnabled( enabled );
    minuteLB.setEnabled( enabled );
    timeOfDayLB.setEnabled( enabled );
  }

}
