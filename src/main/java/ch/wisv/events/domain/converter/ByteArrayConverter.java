package ch.wisv.events.domain.converter;

import java.util.Base64;
import javax.persistence.AttributeConverter;

/**
 * ByteArrayConverter.
 */
public class ByteArrayConverter implements AttributeConverter<byte[], String> {

    /**
     * Converts the value stored in the entity attribute into the
     * data representation to be stored in the database.
     *
     * @param attribute the entity attribute value to be converted
     *
     * @return the converted data to be stored in the database column
     */
    @Override
    public String convertToDatabaseColumn(byte[] attribute) {
        return Base64.getEncoder().encodeToString(attribute);
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
    public byte[] convertToEntityAttribute(String dbData) {
        return Base64.getDecoder().decode(dbData);
    }
}
