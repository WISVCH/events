package ch.wisv.events.utils.dev.data;

import lombok.Setter;
import org.fluttercode.datafactory.impl.DataFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;

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
@Component
abstract class TestDataRunner implements CommandLineRunner {

    /**
     * Field df
     */
    final DataFactory df;

    /**
     * Field jsonFileName
     */
    @Setter
    private String jsonFileName;

    /**
     * Constructor EventTestDataRunner creates a new EventTestDataRunner instance.
     */
    public TestDataRunner() {
        this.df = new DataFactory();
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     *
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("src/main/resources/dev/data/" + this.jsonFileName));

        for (Object object : jsonArray) {
            JSONObject json = (JSONObject) object;

            this.loop(json);
        }
    }

    /**
     * Method loop
     *
     * @param jsonObject of type JSONObject
     */
    protected abstract void loop(JSONObject jsonObject);
}
