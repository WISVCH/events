package ch.wisv.events.domain.converter;

import javax.persistence.AttributeConverter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * JSONObjectConverter.
 */
public class JSONObjectConverter implements AttributeConverter<JSONObject, String> {

    /**
     * Converts the value stored in the entity attribute into the
     * data representation to be stored in the database.
     *
     * @param attribute the entity attribute value to be converted
     *
     * @return the converted data to be stored in the database column
     */
    @Override
    public String convertToDatabaseColumn(JSONObject attribute) {
        return attribute.toString();
    }

    /**
     * Converts the data stored in the database column into the
     * value to be stored in the entity attribute.
     * Note that it is the responsibility of the converter writer to
     * specify the correct dbData type for the corresponding column
     * for use by the JDBC driver: i.e., persistence providers are
     * not expected to do such type conversion.
     *
     * @param dbData the data from the database column to be converted
     *
     * @return the converted value to be stored in the entity attribute
     */
    @Override
    public JSONObject convertToEntityAttribute(String dbData) {
        try {
            return (JSONObject) (new JSONParser()).parse(dbData);
        } catch (ParseException e) {
            return new JSONObject();
        }
    }
}
