package ru.mail.polis.beloborodova9898;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.NoSuchElementException;

public  class MyDAO {
    @NotNull
    private final File dir;

    private File getFile(@NotNull final String key) {
        return new File(dir, key);
    }

    public MyDAO(@NotNull final File dir_input) {
        dir = dir_input;
    }

    @NotNull
    byte[] get(@NotNull final String key) throws NoSuchElementException, IllegalArgumentException, IOException {
        final File file = getFile(key);
        if (!file.exists()) throw new NoSuchElementException("Cant find this file");
        final byte [] value = new byte[(int) file.length()];

        InputStream is = new FileInputStream(file);
        if (is.read(value) != value.length)
            throw new IOException("Cant read");
        is.close();

        return value;
    }

    void upsert(@NotNull final String key, @NotNull final byte[] value) throws IllegalArgumentException, IOException {
        OutputStream os = new FileOutputStream(getFile(key));
        os.write(value);
        os.close();
    }

    void delete(@NotNull final String key) throws IllegalArgumentException, IOException {
        //noinspection ResultOfMethodCallIgnored
        getFile(key).delete();
    }
}
