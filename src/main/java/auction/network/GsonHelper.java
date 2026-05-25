package auction.network;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class GsonHelper {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> 
                    new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> 
                    LocalDateTime.parse(json.getAsString()))
            .create();

    public static Gson getGson() {
        return gson;
    }
}
