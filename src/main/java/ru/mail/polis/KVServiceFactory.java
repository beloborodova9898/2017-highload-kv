package ru.mail.polis;

import ru.mail.polis.beloborodova9898.MyCacheDAO;
import ru.mail.polis.beloborodova9898.MyCacheService;
import ru.mail.polis.beloborodova9898.MyDAO;
import ru.mail.polis.beloborodova9898.MyService;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Constructs {@link KVService} instances.
 *
 * @author Vadim Tsesko <mail@incubos.org>
 */
final class KVServiceFactory {
    private static final long MAX_HEAP = 1024 * 1024 * 1024;

    private KVServiceFactory() {
        // Not supposed to be instantiated
    }

    /**
     * Construct a storage instance.
     *
     * @param port     port to bind HTTP server to
     * @param data     local disk folder to persist the data to
     * @param topology a list of all cluster endpoints {@code http://<host>:<port>} (including this one)
     * @return a storage instance
     */
    @NotNull
    static KVService create(
            final int port,
            @NotNull final File data,
            @NotNull final Set<String> topology) throws IOException {
        if (Runtime.getRuntime().maxMemory() > MAX_HEAP) {
            throw new IllegalStateException("The heap is too big. Consider setting Xmx.");
        }

        if (port <= 0 || 65536 <= port) {
            throw new IllegalArgumentException("Port out of range");
        }

        if (!data.exists()) {
            throw new IllegalArgumentException("Path doesn't exist: " + data);
        }

        if (!data.isDirectory()) {
            throw new IllegalArgumentException("Path is not a directory: " + data);
        }

        // V Выбрать нужное
        // return new MyService(port, new MyDAO(data), false);
        // return new MyService(port, new MyDAO(data), true);
        // return new MyCacheService(port, new MyCacheDAO(data), false);
         return new MyCacheService(port, new MyCacheDAO(data), true);
    }
}
