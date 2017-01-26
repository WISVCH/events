package ch.wisv.events.app.controller;

import ch.wisv.events.core.service.event.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @ResponseBody
    @RequestMapping(value = "/iCal", method = RequestMethod.GET)
    public String getAllEvents() {
        return "";
    }
}
