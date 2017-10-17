package ch.wisv.events.core.model.webhook;

import lombok.Getter;

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
public enum WebhookTrigger {

    EVENT_CREATE_UPDATE("EVENT_CREATE_UPDATE", "Event create or update", "Creation or update of an Event."),
    EVENT_DELETE("EVENT_DELETE", "Event delete", "Deletion of an existing event."),
    PRODUCT_CREATE_UPDATE("PRODUCT_CREATE_UPDATE", "Product create or update", "Creation or update of an Product."),
    PRODUCT_DELETE("PRODUCT_DELETE", "Product delete", "Deletion of an existing product.");

    /**
     * Field tag
     */
    @Getter
    private final String tag;

    /**
     * Field name
     */
    @Getter
    private final String name;

    /**
     * Field description
     */
    @Getter
    private final String description;

    /**
     * Constructor WebhookTrigger creates a new WebhookTrigger instance.
     *
     * @param tag
     * @param name        of type String
     * @param description of type String
     */
    WebhookTrigger(String tag, String name, String description) {
        this.tag = tag;
        this.name = name;
        this.description = description;
    }
}
