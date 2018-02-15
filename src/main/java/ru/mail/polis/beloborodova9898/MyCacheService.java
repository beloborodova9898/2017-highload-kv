package ru.mail.polis.beloborodova9898;

import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import ru.mail.polis.KVService;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;

public class MyCacheService implements KVService {
    @NotNull
    private final HttpServer server;
    @NotNull
    private final MyCacheDAO dao;

    @NotNull
    private static String extractID(@NotNull final String string) {
        String prefix = "id=";
        if (!string.startsWith(prefix)) return ""; // Чтобы не ловить кругом исключения
        return string.substring(prefix.length());
    }

    private static final int nThreads = 4;
    private static final int maxPutLength = 1024;


    public MyCacheService(int port, @NotNull final MyCacheDAO daoInput, final boolean useExecutor) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        dao = daoInput;

        if (useExecutor)
            server.setExecutor(Executors.newFixedThreadPool(nThreads));

        server.createContext("/v0/status", httpExchange -> {
            final String response = "ONLINE";
            httpExchange.sendResponseHeaders(200, response.length());
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.close();
        });

        server.createContext("/v0/entity", httpExchange -> {
            final String query = httpExchange.getRequestURI().getQuery();
            final String id = extractID(query);

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
                    InputStream inp = httpExchange.getRequestBody();
                    byte[] buffer = new byte[maxPutLength];

                    int readBytes = inp.read(buffer);
                    inp.close();

                    if (readBytes < 0) {
                        // Судя по тестам, пустой массив - это нормально и не требует особой обработки
                        readBytes = 0;
                    }


                    byte[] realData = new byte[readBytes];
                    System.arraycopy(buffer, 0, realData, 0, readBytes);
                    dao.upsert(id, realData);

                    httpExchange.sendResponseHeaders(201, 0);
                    break;

                default:
                    httpExchange.sendResponseHeaders(100, 0);
            }

            httpExchange.close();
        });
    }

    public MyCacheService(int port, @NotNull final MyCacheDAO daoInput) throws IOException {
        this(port, daoInput, true);
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void stop() {
        server.stop(0);
    }
}
