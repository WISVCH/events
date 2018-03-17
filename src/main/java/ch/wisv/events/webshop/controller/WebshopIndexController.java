package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.OrderProductDTO;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.webshop.service.WebshopService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
@Controller
public class WebshopIndexController {

    /**
     * Field eventService.
     */
    private final EventService eventService;

    /**
     *
     */
    private final WebshopService webshopService;

    /**
     * Constructor WebshopController.
     *
     * @param eventService   of type EventService
     * @param webshopService of type WebshopService
     */
    public WebshopIndexController(EventService eventService, WebshopService webshopService) {
        this.eventService = eventService;
        this.webshopService = webshopService;
    }

    /**
     * Front page of the webshop.
     *
     * @param model of type Model
     *
     * @return String
     */
    @GetMapping
    public String index(Model model) {
        List<Event> upcoming = eventService.getUpcoming();
        model.addAttribute("events", webshopService.filterNotSalableProducts(upcoming));
        model.addAttribute("orderProduct", new OrderProductDTO());

        return "webshop/index";
    }
}
