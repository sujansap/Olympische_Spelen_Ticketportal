package utility;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import domain.Sport;

import java.io.IOException;

public class SportSerializer extends JsonSerializer<Sport> {

    @Override
    public void serialize(Sport stadium, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(stadium.getSportNaam());
    }
}