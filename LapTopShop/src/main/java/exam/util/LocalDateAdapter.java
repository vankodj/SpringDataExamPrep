package exam.util;


import com.google.gson.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Component
public class LocalDateAdapter {
    @Bean
    public Gson createGson() {

        final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        JsonDeserializer<LocalDate> toLocalTime =
                (json, t, c) -> LocalDate.parse(json.getAsString(), dateFormat);

        JsonSerializer<String> fromLocalTime =
                (date, t, c) -> new JsonPrimitive(date);

        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, toLocalTime)
                .registerTypeAdapter(LocalDate.class, fromLocalTime)
                .create();
    }
}