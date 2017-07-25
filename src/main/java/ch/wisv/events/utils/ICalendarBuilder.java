package ch.wisv.events.utils;

import biweekly.ICalendar;
import biweekly.component.VEvent;
import ch.wisv.events.core.model.event.Event;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class ICalendarBuilder {

    /**
     * Generates a iCal object for a list of events
     *
     * @param events
     * @return
     */
    public static ICalendar createIcalEventList(List<Event> events) {
        ICalendar iCal = new ICalendar();
        events.forEach(event -> iCal.addEvent(createICalEvent(event)));

        iCal.addName("CH Event Calendar");
        return iCal;
    }

    /**
     * Creates a biweekly VEvent for a Event object
     *
     * @param event
     * @return
     */
    private static VEvent createICalEvent(Event event) {
        VEvent vEvent = new VEvent();

        vEvent.setSummary(event.getTitle());
        vEvent.setDescription(event.getDescription());
        vEvent.setLocation(event.getLocation());

        vEvent.setDateStart(dateFromLocalDateTime(event.getStart()));
        vEvent.setDateEnd(dateFromLocalDateTime(event.getEnding()));

        return vEvent;
    }


    /**
     * Converts a LocalDateTime to a date object
     * <p>
     * Source: http://stackoverflow.com/questions/19431234/converting-between-java-time-localdatetime-and-java-util-date
     *
     * @param date
     * @return The date object representing the LocalDateTime object
     */
    private static Date dateFromLocalDateTime(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

}

