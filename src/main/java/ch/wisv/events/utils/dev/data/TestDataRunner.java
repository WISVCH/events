package ch.wisv.events.utils.dev.data;

import java.io.FileReader;
import lombok.Setter;
import org.fluttercode.datafactory.impl.DataFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
abstract class TestDataRunner implements CommandLineRunner {

    /** DataFactory. */
    final DataFactory df;

    /** Json file name. */
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
     * Method loop.
     *
     * @param jsonObject of type JSONObject
     */
    protected abstract void loop(JSONObject jsonObject);
}
