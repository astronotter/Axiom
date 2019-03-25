package sample;

import java.util.*;


class Categorize {
   private UUID categoryID;
   private UUID elementID;
   
   public Categorize(UUID categoryID, UUID elementID) {
      this.categoryID = categoryID;
      this.elementID = elementID;
   }
   
   public UUID getCategoryID() {
      return this.categoryID;
   }
   public UUID getElementID() {
      return this.elementID;
   }
   
   public static Categorize deserialize(String text) {
      String parts[] = text.split(",");
      // TODO validate input
      return new Categorize(UUID.fromString(parts[0]),
                            UUID.fromString(parts[1]));
   }
   public String serialize() {
      return String.format("%s,%s", this.categoryID, this.elementID);
   }
}