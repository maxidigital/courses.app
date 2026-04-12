package main.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 *
 * @author bott_ma
 */
public class MainHandler implements HttpHandler
{

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        for (int i = 0; i < 10; i++) {
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        }
        // Get the query string from the request URI
        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery();

        // Extract the code parameter
        String code = null;
        if (query != null && query.contains("code=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("code=")) {
                    code = param.substring(5); // "code=".length() == 5
                    break;
                }
            }
        }

        // Prepare the response
        String response;
        if (code != null) {
            response = "Received code: " + code;
        } else {
            response = "No code parameter found in the request.";
        }

        // Send the response
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
