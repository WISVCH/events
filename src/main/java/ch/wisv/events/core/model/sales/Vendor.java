package ch.wisv.events.core.model.sales;

import ch.wisv.events.core.model.event.Event;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

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
@Entity
public class Vendor {

    /**
     * Field id of the vendor.
     */
    @Id
    @GeneratedValue
    @Getter
    private Long id;

    /**
     * Field key UUId of the vendor.
     */
    @Getter
    @Setter
    private String key;

    /**
     * Field ldapGroup ldapGroup.
     */
    @Getter
    @Setter
    private LDAPGroupEnum ldapGroup;

    /**
     * Field startingTime starting time of the sell access.
     */
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Getter
    @Setter
    private LocalDateTime startingTime;

    /**
     * Field endingTime ending time of the sell access.
     */
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Getter
    @Setter
    private LocalDateTime endingTime;

    /**
     * Field events events that is ldap group is allowed to sell.
     */
    @ManyToMany(targetEntity = Event.class)
    @Getter
    @Setter
    private List<Event> events;

    /**
     * Constructor Vendor creates a new Vendor instance.
     */
    public Vendor() {
        this.key = UUID.randomUUID().toString();
        this.events = new ArrayList<>();
    }

    /**
     * Method add will add an Event to a Vendor.
     *
     * @param event of type Event
     */
    public void addEvent(Event event) {
        this.events.add(event);
    }

}
