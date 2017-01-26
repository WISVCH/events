package ch.wisv.events.core.factory;

import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.Summary;
import ch.wisv.events.core.model.event.Event;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by janwillemm on 26/01/2017.
 */
public class ICalendarFactory {

    /**
     * Generates a iCal object for a list of events
     * @param events
     * @return
     */
    public static ICalendar createEventList(ArrayList<Event> events){
        ICalendar iCal = new ICalendar();
        events.forEach(event -> {iCal.addEvent(createICalEvent(event));});

        iCal.addName("CH Event Calendar");
        iCal.setColor("turquoise");

        return iCal;
    }

    /**
     * Creates a biweekly VEvent for a Event object
     * @param event
     * @return
     */
    public static VEvent createICalEvent(Event event){
        VEvent vEvent = new VEvent();
        Summary summary = vEvent.setSummary(event.getTitle());
        summary.setLanguage("nl-nl");

        vEvent.setDescription(event.getDescription());

        Date start = dateFromLocalDateTime(event.getStart());
        vEvent.setDateStart(start);

        vEvent.setDateEnd(dateFromLocalDateTime(event.getEnd()));

        return vEvent;
    }


    /**
     * Converts a LocalDateTime to a date object
     *
     * Source: http://stackoverflow.com/questions/19431234/converting-between-java-time-localdatetime-and-java-util-date
     * @param date
     * @return The date object representing the LocalDateTime object
     */
    private static Date dateFromLocalDateTime(LocalDateTime date){
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

}

