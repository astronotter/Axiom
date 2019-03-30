package axiom;

import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;
import java.lang.reflect.*;
import java.nio.charset.*;

public class FlatDB {
    String filename;
    private List<Object> entities;

    public FlatDB(String filename) throws Exception {
        this.filename = filename;
        this.entities = new ArrayList<Object>();

        try {
            String lines[] = Files.lines(Paths.get(filename)).toArray(String[]::new);
            for (String line : lines) {
                String parts[] = line.split(":");
                Class tableClass = Class.forName(parts[0]);
                if (tableClass == null) {
                    System.err.println(String.format(
                        "Warning: Cannot find class '%s'. Ignoring entry.",
                        parts[0]));
                    continue;
                }
                Method deserializeMethod = tableClass.getDeclaredMethod("deserialize", String.class);
                if (deserializeMethod == null) {
                    System.err.println(String.format(
                        "Warning: Cannot find deserialize method for class '%s'. Ignoring entry.",
                        parts[0]));
                    continue;
                }
                this.entities.add(deserializeMethod.invoke(tableClass, parts[1]));
            }
        }
        catch (NoSuchFileException notfound) {
            // Silently ignore, the file not existing is treated the same as an
            // empty db
        }
    }

    public void save() throws Exception {
      save(this.filename);
    }
    public void save(String filename) throws Exception {
        List<String> lines = new ArrayList<String>();
        for (Object entity : entities) {
            Class tableClass = entity.getClass();
            Method serializeMethod = tableClass.getDeclaredMethod("serialize");
            if (serializeMethod == null) {
                System.err.println(String.format(
                    "Warning: No serialize method for class '%s'. Ignoring instance.",
                    tableClass.getName()));
            continue;
            }
            String text = (String)serializeMethod.invoke(entity);
            lines.add(String.format("%s:%s", tableClass.getName(), text));
        }
        Files.write(Paths.get(filename), lines, StandardCharsets.UTF_8);
    }
    // Due to type erasure we require a dummy variable (a la toArray). Usage is
    // then select(new Type[0]). This is a tradeoff between DRY and the inherent
    // problems with generics in Java.
    public <T> Stream<T> select(T type[]) {
        Stream.Builder<T> result = Stream.builder();
        for (Object entity : entities) {
            if (entity.getClass() == type.getClass().getComponentType())
                result.add((T)entity);
        }
        return result.build();
    }
    public <T> T insert(T entity) {
        entities.add(entity);
        return entity;
    }
}