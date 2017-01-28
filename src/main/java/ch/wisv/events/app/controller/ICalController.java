package ch.wisv.events.app.controller;

import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.io.text.ICalWriter;
import ch.wisv.events.utils.ICalendarBuilder;
import ch.wisv.events.core.service.event.EventService;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

@Controller
@RequestMapping(value = "/events")
public class ICalController {

    private final EventService eventService;

    /**
     * Default constructor
     */
    public ICalController(EventService eventService){this.eventService = eventService;}

    /**
     * Get request on "/events/iCal" will present the ical with the events
     *
     */
    @RequestMapping(value = "/iCal", method = RequestMethod.GET, produces = "text/calendar; charset=utf-8")
    public void getAllEvents(HttpServletResponse response) {
        response.setContentType("text/calendar");
        // Getting the iCal with the current available events
        ICalendar ical = ICalendarBuilder.createIcalEventList(eventService.getAvailableEvents());
        presentIcalFile(ical, response);
    }

    /**
     * Get request on /iCal/upcoming will present the ical with the upcoming events
     * @param response
     */
    @RequestMapping(value = "/iCal/upcoming", method = RequestMethod.GET, produces = "text/calendar; charset=utf-8")
    public void getUpcomingEvents(HttpServletResponse response) {
        response.setContentType("text/calendar");
        ICalendar ical = ICalendarBuilder.createIcalEventList(eventService.getUpcomingEvents());
        presentIcalFile(ical, response);
    }

    /**
     * Attaches the ICal to the HttpServletResponse, providing the user with an iCal file.
     * The library can only write iCals to a file, so we use a temporary file to store the ical in.
     * Then we copy the contentstream of the file to the output stream of the response.
     * @param ical
     * @param response
     * @throws IOException
     */
    private void presentIcalFile(ICalendar ical, HttpServletResponse response){
        try{
            // Create a temporary file to for the iCalWriter to write to.
            File file = File.createTempFile("ical", ".ics");
            ICalWriter writer = new ICalWriter(file, ICalVersion.V2_0);
            writer.write(ical);
            writer.close();

            // Make the outputstream the same as our temporary file
            IOUtils.copy(new FileInputStream(file), response.getOutputStream());

            // Attaches the file to the response
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("IOError writing ICalendar to response output stream");
        }
    }


}
