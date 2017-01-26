package ch.wisv.events.app.controller;

import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.io.text.ICalWriter;
import ch.wisv.events.core.factory.ICalendarFactory;
import ch.wisv.events.core.service.event.EventService;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by janwillemm on 26/01/2017.
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
     * Get request on "/events/iCal" will show ical.
     *
     */
    @RequestMapping(value = "/iCal", method = RequestMethod.GET)
    public void getAllEvents(HttpServletResponse response) {
        // Getting the iCal with the current available events
        ICalendar ical = ICalendarFactory.createEventList(eventService.getAvailableEvents());

        try {
            // Create a temporary file to for the iCalWriter to write to.
            File file = File.createTempFile("ical", ".ics");
            ICalWriter writer = new ICalWriter(file, ICalVersion.V2_0);
            writer.write(ical);
            writer.close();

            // Make the outputstream the same as our temporary file
            IOUtils.copy(new FileInputStream(file), response.getOutputStream());

            // And flush the file
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }
    }



}
