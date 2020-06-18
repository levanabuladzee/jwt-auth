package ge.ol.ping.util;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class BodyReader {
    public static JsonObject getBody(HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = null;
        String line;
        String data = null;

        try {
            reader = request.getReader(); // Get HTTP request Reader

            while ((line = reader.readLine()) != null) {
                buffer.append(line); // Read Reader line by line and append into buffer
            }

            data = buffer.toString(); // Convert buffer to String
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close(); // Finally close buffer
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return JsonHelper.getJson(data);
    }
}
