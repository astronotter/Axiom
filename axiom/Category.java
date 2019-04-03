package axiom;

import java.util.*;

class Category {
    private UUID id;
    private String name;

    public Category(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
    public Category(String name) {
        this(UUID.randomUUID(), name);
    }

    public UUID getID() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public static Category deserialize(String text) {
        String parts[] = text.split(",");
        // TODO validate input
        return new Category(UUID.fromString(parts[0]), parts[1]);
    }
    public String serialize() {
        return String.format("%s,%s", this.id, this.name);
    }
    @Override
    public boolean equals(Object rhs) {
        if (rhs == null)
            return false;
        if (rhs == this)
            return true;
        if (!(rhs instanceof Category))
            return false;
        Category other = (Category)rhs;
        return other.id.equals(id) && other.name.equals(name);
    }
    @Override
    public int hashCode() {
        return id.hashCode() * 31 + name.hashCode();
    }
}