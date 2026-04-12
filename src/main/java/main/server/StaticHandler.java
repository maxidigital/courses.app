package main.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath(); // e.g. /static/bg.jpg
        String resource = path.startsWith("/") ? path.substring(1) : path; // static/bg.jpg

        InputStream in = getClass().getClassLoader().getResourceAsStream(resource);
        if (in == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        String contentType = contentTypeFor(resource);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Cache-Control", "max-age=86400");
        byte[] bytes = in.readAllBytes();
        in.close();
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String contentTypeFor(String path) {
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".gif")) return "image/gif";
        if (path.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }
}
