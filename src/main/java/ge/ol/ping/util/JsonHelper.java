package ge.ol.ping.util;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonHelper {
    // Parse JSON data
    public static JsonObject getJson(String body) {
        InputStream stream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

        JsonReader jsonReader = Json.createReader(stream);

        JsonObject jsonObject = jsonReader.readObject();

        try {
            jsonReader.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
