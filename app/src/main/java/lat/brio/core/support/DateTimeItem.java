package lat.brio.core.support;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import brio.sdk.logger.util.BrioUtilsFechas;

/**
 * Created by guillermo.ortiz on 07/03/18.
 */

public class DateTimeItem  implements Serializable, Cloneable {
    
    private static final long serialVersionUID = - 7527635803861232454L;
    
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    
    public Object clone () {
        DateTimeItem obj = null;
        try {
            obj = (DateTimeItem) super.clone ();
        } catch (CloneNotSupportedException ex) {
            System.out.println (" no se puede duplicar");
        }
        return obj;
    }
    
    public DateTimeItem () {
        DateTimeItem now = getNow ();
        this.year = now.getYear ();
        this.month = now.getMonth ();
        this.day = now.getDay ();
        this.hour = now.getHour ();
        this.minute = now.getMinute ();
        this.second = now.getSecond ();
    }
    
    public DateTimeItem (int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }
    
    public DateTimeItem (int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        
        DateTimeItem now = getNow ();
        this.hour = now.getHour ();
        this.minute = now.getMinute ();
        this.second = now.getSecond ();
    }
    
    
    public DateTimeItem (EFormatos formato, String date, String hour) {
        switch (formato) {
            case FECHA_HORA_PANTALLA:
                if ((date != null && date.compareTo ("") != 0) && (hour != null && hour.compareTo ("") != 0)) {
                    this.day = Integer.valueOf (date.substring (0, 2));
                    this.month = Integer.valueOf (date.substring (3, 5));
                    this.year = Integer.valueOf (date.substring (6, 10));
                    this.hour = Integer.valueOf (hour.substring (0, 2));
                    this.minute = Integer.valueOf (hour.substring (3, 5));
                }
                break;
            case FECHA_HORA_BASEDATOS:
                if ((date != null && date.compareTo ("") != 0) && (hour != null && hour.compareTo ("") != 0)) {
                    this.year = Integer.valueOf (date.substring (0, 4));
                    this.month = Integer.valueOf (date.substring (4, 6));
                    this.day = Integer.valueOf (date.substring (6, 8));
                    this.hour = Integer.valueOf (hour.substring (0, 2));
                    this.minute = Integer.valueOf (hour.substring (2, 4));
                    this.second = Integer.valueOf (hour.substring (4, 6));
                }
                break;
            case FECHA_BASEDATOS:
                if (date != null && date.compareTo ("") != 0) {
                    this.year = Integer.valueOf (date.substring (0, 4));
                    this.month = Integer.valueOf (date.substring (4, 6));
                    this.day = Integer.valueOf (date.substring (6, 8));
                    DateTimeItem now = getNow ();
                    this.hour = now.getHour ();
                    this.minute = now.getMinute ();
                    this.second = now.getSecond ();
                }
                
                break;
            
            case FECHA_CALENDARIO:
                if (date != null && date.compareTo ("") != 0) {
                    this.year = Integer.valueOf (date.substring (0, 4));
                    this.month = Integer.valueOf (date.substring (6, 7));
                    this.day = Integer.valueOf (date.substring (8, 9));
                    DateTimeItem now = getNow ();
                    this.hour = now.getHour ();
                    this.minute = now.getMinute ();
                    this.second = now.getSecond ();
                }
                break;
            case HORA_MINUTOS_BASEDATOS:
                if (date != null && date.compareTo ("") != 0) {
                    this.year = Integer.valueOf (date.substring (0, 4));
                    this.month = Integer.valueOf (date.substring (6, 7));
                    this.day = Integer.valueOf (date.substring (8, 9));
                    this.hour = Integer.valueOf (hour.substring (0, 2));
                    this.minute = Integer.valueOf (hour.substring (2, 4));
                    
                    DateTimeItem now = getNow ();
                    
                    this.second = now.getSecond ();
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * Devuelve el dia de la semana
     * @return int 1 DOMINGO, 2 LUNES, 3 MARTES, 4 MIERCOLES, 5 JUEVES, 6 VIERNES, 7 SABADO
     */
    public int getDayOfWeek () {
        Calendar cal = new GregorianCalendar (getYear (), getMonth (), getDay (), getHour (), getMinute (), getSecond ());
        int dayOfWeek = cal.get (Calendar.DAY_OF_WEEK);
        
        return dayOfWeek;
    }

	/*public int getDayOfMonth(){
         Calendar cal = new GregorianCalendar(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond());
	     int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

	     return dayOfMonth;
	}*/
    
    public void addMonth (int month) {
        Calendar cal = new GregorianCalendar (getYear (), getMonth (), getDay (), getHour (), getMinute (), getSecond ());
        cal.add (Calendar.MONTH, month);
        
        setYear (cal.get (Calendar.YEAR));
        setMonth (cal.get (Calendar.MONTH));
        setDay (cal.get (Calendar.DAY_OF_MONTH));
        setHour (cal.get (Calendar.HOUR_OF_DAY));
        setMinute (cal.get (Calendar.MINUTE));
        setSecond (cal.get (Calendar.SECOND));
    }
    
    public void addDays (int days) {
        Calendar cal = new GregorianCalendar (getYear (), getMonthOfYear (), getDay (), getHour (), getMinute (), getSecond ());
        /*int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
		int anno = cal.get(Calendar.YEAR);*/
        
        cal.add (Calendar.DATE, days);
        
        setYear (cal.get (Calendar.YEAR));
        setMonth (cal.get (Calendar.MONTH) + 1);
        setDay (cal.get (Calendar.DAY_OF_MONTH));
        setHour (cal.get (Calendar.HOUR_OF_DAY));
        setMinute (cal.get (Calendar.MINUTE));
        setSecond (cal.get (Calendar.SECOND));
    }
    
    public int compareTo (DateTimeItem dateTime) {
        Calendar cal1 = new GregorianCalendar (getYear (), getMonth (), getDay (), getHour (), getMinute (), getSecond ());
        Calendar cal2 = new GregorianCalendar (dateTime.getYear (), dateTime.getMonth (), dateTime.getDay (), dateTime.getHour (), dateTime.getMinute (), dateTime.getSecond ());
        
        return cal1.compareTo (cal2);
    }
    
    public int compareTwo (DateTimeItem dateTime1, DateTimeItem dateTime2) {
        Calendar cal1 = new GregorianCalendar (dateTime1.getYear (), dateTime1.getMonthOfYear (), dateTime1.getDay (), 00, 00, 00);
        
        Calendar cal2 = new GregorianCalendar (dateTime2.getYear (), dateTime2.getMonthOfYear (), dateTime2.getDay (), 00, 00, 00);
        
        return cal1.compareTo (cal2);
    }
    
    
    public int getYear () {
        return year;
    }
    
    public void setYear (int year) {
        this.year = year;
    }
    
    public int getMonth () {
        return month;
    }
    
    public int getMonthOfYear () {
        return month - 1;
    }
    
    public void setMonth (int month) {
        this.month = month;
    }
    
    public int getDay () {
        return day;
    }
    
    public void setDay (int day) {
        this.day = day;
    }
    
    public int getHour () {
        return hour;
    }
    
    public void setHour (int hour) {
        this.hour = hour;
    }
    
    public int getMinute () {
        return minute;
    }
    
    public void setMinute (int minute) {
        this.minute = minute;
    }
    
    public int getSecond () {
        return second;
    }
    
    public void setSecond (int second) {
        this.second = second;
    }
    
    public static DateTimeItem getNow () {
        Calendar cal = new GregorianCalendar ();
        DateTimeItem now = new DateTimeItem (cal.get (Calendar.YEAR), cal.get (Calendar.MONTH) + 1, cal.get (Calendar.DAY_OF_MONTH), cal.get (Calendar.HOUR_OF_DAY), cal
                .get (Calendar.MINUTE), cal.get (Calendar.SECOND));
        
        return now;
    }
    
    public String toString (String format) {
        Calendar cal = new GregorianCalendar (getYear (), getMonthOfYear (), getDay (), getHour (), getMinute (), getSecond ());
        
        SimpleDateFormat sdf = new SimpleDateFormat (format, new Locale ("es", "ES"));
        
        return sdf.format (cal.getTime ());
    }
    
    /**
     * Metodo encargado de devolver el dia de la semana
     * @return int 1 DOMINGO, 2 LUNES, 3 MARTES, 4 MIERCOLES, 5 JUEVES, 6 VIERNES, 7 SABADO
     */
    public int obtenerDiaSemana () {
        Calendar cal = new GregorianCalendar (getYear (), getMonthOfYear (), getDay (), getHour (), getMinute (), getSecond ());
        
        int diaDeLaSemana = cal.get (Calendar.DAY_OF_WEEK);
        
        return diaDeLaSemana;
    }
    
    public Date getTime () {
        Calendar cal = new GregorianCalendar (getYear (), getMonthOfYear (), getDay (), getHour (), getMinute (), getSecond ());
        return cal.getTime ();
    }
    
    public Date getFirstDayOfWeek (DateTimeItem dt) {
        Date devolver = null;
        if (dt != null) {
            Calendar xmas = Calendar.getInstance ();
            xmas.setTime (dt.getTime ());
            while (xmas.get (Calendar.DAY_OF_WEEK) > xmas.getFirstDayOfWeek ()) {
                xmas.add (Calendar.DATE, - 1);
            }
            devolver = xmas.getTime ();
            
        }
        return devolver;
    }
    
    public Date getLastDayOfWeek (DateTimeItem dt) {
        Date devolver = null;
        if (dt != null) {
            Date fechaIni = getFirstDayOfWeek (dt);
            devolver = BrioUtilsFechas.obtenerFechaSumandoDias (fechaIni, 6);
        }
        return devolver;
    }
    
    public String getFmtStrDate () {
        return this.toString (EFormatos.FECHA_PANTALLA.valor ());
    }
    
    public String getFmtStrDateBD () {
        return this.toString (EFormatos.FECHA_BASEDATOS.valor ());
    }
    public String getFmtStrDateHourBD () {
        return this.toString (EFormatos.FECHA_HORA_BASEDATOS.valor ());
    }
}