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

    public FlatDB(String filename) {
        this.filename = filename;
        this.entities = new ArrayList<Object>();

        load(filename);
    }
    public boolean load(String filename) {
        try {
            String lines[] = Files.lines(Paths.get(filename)).toArray(String[]::new);
            for (String line : lines) {
                line = line.replaceAll("%n", "\n");
                String parts[] = line.split(":");
                try {
                    Class tableClass = Class.forName(parts[0]);
                    Method deserializeMethod = tableClass.getDeclaredMethod("deserialize", String.class);
                    this.entities.add(deserializeMethod.invoke(tableClass, parts[1]));
                }
                catch (ClassNotFoundException ex) {
                    System.err.println(String.format(
                        "Warning: Cannot find class '%s'. Ignoring entry.",
                        parts[0]));
                }
                catch (Exception ex) {
                    System.err.println(String.format(
                        "Warning: Cannot deserialize method for class '%s'. Ignoring entry. (%s)",
                        parts[0], ex));
                }
            }
        }
        catch (IOException ex) {
            // Silently ignore, the file not existing is treated the same as an
            // empty db
            return false;
        }
        return true;
    }
    public void save() throws IOException {
        save(this.filename);
    }
    public void save(String filename) throws IOException {
        List<String> lines = new ArrayList<String>();
        for (Object entity : entities) {
            Class tableClass = entity.getClass();
            try {
                Method serializeMethod = tableClass.getDeclaredMethod("serialize");
                String text = (String)serializeMethod.invoke(entity);
                text = text.replaceAll("\\R", "%n");    // [SO 9849015]
                lines.add(String.format("%s:%s", tableClass.getName(), text));
            }
            catch (Exception ex) {
                System.err.println(String.format(
                    "Warning: Cannot serialize instance of class '%s'. Ignoring instance.",
                    tableClass.getName()));
                continue;
            }
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
    public void remove(Object entity) {
        entities.remove(entity);
    }
}