package axiom;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class CategoryTest
{
    Category category = new Category("Category");
    String serializedCategory = category.serialize();
    Category deserializedCategory = category.deserialize(serializedCategory);

    @Test
    public void categoryCreation() { assertNotNull(category); }
    @Test
    public void categoryName() { assertEquals("Category", category.getName()); }
    @Test
    public void deserializedCategory() { assertEquals(deserializedCategory, category); }
}