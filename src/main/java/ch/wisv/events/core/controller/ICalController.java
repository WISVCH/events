package ch.wisv.events.core.controller;

import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.io.text.ICalWriter;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.utils.ICalendarBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
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

    /**
     * Field eventService
     */
    private final EventService eventService;

    /**
     * Default constructor
     */
    public ICalController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get request on "/events/iCal" will present the ical with the events
     */
    @GetMapping(value = "/iCal", produces = "text/calendar; charset=utf-8")
    public void getAllEvents(HttpServletResponse response) {
        response.setContentType("text/calendar");
        // Getting the iCal with the current available events
        ICalendar ical = ICalendarBuilder.createIcalEventList(eventService.getAvailableEvents());
        presentIcalFile(ical, response);
    }

    /**
     * Get request on /iCal/upcoming will present the ical with the upcoming events
     *
     * @param response of type HttpServletResponse.
     */
    @GetMapping(value = "/iCal/upcoming", produces = "text/calendar; charset=utf-8")
    public void getUpcomingEvents(HttpServletResponse response) {
        response.setContentType("text/calendar");
        ICalendar ical = ICalendarBuilder.createIcalEventList(eventService.getUpcomingEvents());
        presentIcalFile(ical, response);
    }

    /**
     * Attaches the ICal to the HttpServletResponse, providing the user with an iCal file.
     *
     * @param ical     of type ICalendar.
     * @param response of type HttpServletResponse.
     * @throws RuntimeException
     */
    private void presentIcalFile(ICalendar ical, HttpServletResponse response) {
        try {
            // Create a temporary file to for the iCalWriter to write to.
            ICalWriter writer = new ICalWriter(response.getOutputStream(), ICalVersion.V2_0);
            writer.write(ical);
            writer.close();
            // Attaches the file to the response
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("IOError writing ICalendar to response output stream", e);
        }
    }


}
