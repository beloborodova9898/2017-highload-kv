package ru.mail.polis.beloborodova9898;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public  class MyCacheDAO {
    @NotNull
    private final File dir;
    @NotNull
    private Map<String, byte[]> cache;
    private static final int cacheSize = 300;

    private File getFile(@NotNull final String key) {
        return new File(dir, key);
    }

    public MyCacheDAO(@NotNull final File dirInput) {
        dir = dirInput;
        cache = new HashMap<>(cacheSize);

    }

    @NotNull
    byte[] get(@NotNull final String key) throws NoSuchElementException, IllegalArgumentException, IOException {
        if (cache.containsKey(key))
                    return cache.get(key);

        final File file = getFile(key);
        if (!file.exists()) throw new NoSuchElementException("Cant find this file");

        final byte [] value = new byte[(int) file.length()];

        InputStream is = new FileInputStream(file);
        is.read(value);
        is.close();

        if (cache.size() == cacheSize) cache.clear();
        cache.put(key, value);
        return value;
    }

    void upsert(@NotNull final String key, @NotNull final byte[] value) throws IllegalArgumentException, IOException {
        OutputStream os = new FileOutputStream(getFile(key));
        os.write(value);
        os.close();

        cache.remove(key);
    }

    void delete(@NotNull final String key) throws IllegalArgumentException, IOException {
        //noinspection ResultOfMethodCallIgnored
        getFile(key).delete();

        cache.remove(key);
    }
}
