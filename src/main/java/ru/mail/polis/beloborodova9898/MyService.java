package ru.mail.polis.beloborodova9898;

import com.sun.corba.se.spi.activation.NoSuchEndPoint;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import ru.mail.polis.KVService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;

public class MyService implements KVService {
    @NotNull
    private final HttpServer server;
    @NotNull
    private final MyDAO dao;
    @NotNull
    private static String extractID(@NotNull final String string) {
        String prefix = "id=";
        if (!string.startsWith(prefix)) throw new IllegalArgumentException("No id");
        return string.substring(prefix.length());
    }

    public MyService(int port, @NotNull final MyDAO dao_input) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        dao = dao_input;

        server.createContext("/v0/status", httpExchange -> {
                final String response = "ONLINE";
                httpExchange.sendResponseHeaders(200, response.length());
                httpExchange.getResponseBody().write(response.getBytes());
                httpExchange.close();
        });

        server.createContext("/v0/entity", httpExchange -> {
            final String id = extractID(httpExchange.getRequestURI().getQuery());
            if (id.isEmpty()) {
                httpExchange.sendResponseHeaders(400, 0);
                httpExchange.close();
                return;
            }
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    try {
                        final byte[] value = dao.get(id);
                        httpExchange.sendResponseHeaders(200, value.length);
                        httpExchange.getResponseBody().write(value);
                    } catch (NoSuchElementException e) {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case "DELETE":
                    dao.delete(id);
                    httpExchange.sendResponseHeaders(202, 0);
                    break;
                case "PUT":
                    final int content_length =
                            Integer.valueOf(httpExchange.getRequestHeaders().getFirst("Content-Length"));
                    final byte[] value2 = new byte[content_length];

                    int declaredLength = httpExchange.getRequestBody().read(value2);
                    if (declaredLength < 0) declaredLength = 0;
                    if (declaredLength != value2.length)
                        throw new IOException("Cant read");

                    dao.upsert(id, value2);
                    httpExchange.sendResponseHeaders(201, 0);
                    break;
                default:
                    httpExchange.sendResponseHeaders(100, 0);
            }

            httpExchange.close();
        });
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void stop() {
        //server.stop(1); //- Вот так проходится тест.
    }
}
