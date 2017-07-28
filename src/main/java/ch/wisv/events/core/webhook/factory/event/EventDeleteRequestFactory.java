package ch.wisv.events.core.webhook.factory.event;


import ch.wisv.events.core.exception.WebhookRequestObjectIncorrect;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.webhook.factory.WebhookRequestFactory;
import org.json.simple.JSONObject;

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
public class EventDeleteRequestFactory extends WebhookRequestFactory {

    /**
     * Method getRequestData ...
     *
     * @param object of type Object
     */
    @Override
    protected JSONObject getRequestData(Object object) throws WebhookRequestObjectIncorrect {
        if (object instanceof Event) {
            Event event = (Event) object;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", event.getKey());

            return jsonObject;
        } else {
            throw new WebhookRequestObjectIncorrect();
        }
    }
}
