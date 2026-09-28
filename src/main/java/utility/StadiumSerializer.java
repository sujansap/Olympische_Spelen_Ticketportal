package utility;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import domain.Stadium;

import java.io.IOException;

public class StadiumSerializer extends JsonSerializer<Stadium> {

    @Override
    public void serialize(Stadium stadium, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(stadium.getStadiumNaam());
    }
}
