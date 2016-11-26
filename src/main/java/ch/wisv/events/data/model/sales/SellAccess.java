package ch.wisv.events.data.model.sales;

import ch.wisv.events.data.model.event.Event;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
public class SellAccess implements Serializable {

    @Id
    @GeneratedValue
    @Getter
    private Long id;

    @Getter
    private String key;

    @Getter
    @Setter
    private String ldapGroup;

    @Getter
    @Setter
    private LocalDateTime startingTime;

    @Getter
    @Setter
    private LocalDateTime endingTime;

    @ManyToMany(targetEntity = Event.class)
    @Getter
    @Setter
    private List<Event> events;

    public SellAccess() {
        this.key = UUID.randomUUID().toString();
        this.events = new ArrayList<>();
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

}
