package ch.wisv.events.domain.converter;

import java.time.ZonedDateTime;
import static java.util.Objects.isNull;
import javax.persistence.AttributeConverter;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * ZonedDateTimeConverter.
 */
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, String> {

    /**
     * Converts the value stored in the entity attribute into the
     * data representation to be stored in the database.
     *
     * @param attribute the entity attribute value to be converted
     *
     * @return the converted data to be stored in the database column
     */
    @Override
    public String convertToDatabaseColumn(ZonedDateTime attribute) {
        if (isNull(attribute)) {
            return null;
        }

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
    public ZonedDateTime convertToEntityAttribute(String dbData) {
        if (isEmpty(dbData)) {
            return null;
        }

        return ZonedDateTime.parse(dbData);
    }
}
