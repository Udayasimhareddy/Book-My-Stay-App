import java.util.HashMap;

// Abstract Room class
abstract class Room {
    int beds;
    double price;
    String type;

    Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Beds: " + beds);
        System.out.println("Price: " + price);
    }
}

// Room types
class SingleRoom extends Room {
    SingleRoom() {
        super("Single", 1, 1000);
    }
}

class DoubleRoom extends Room {
    DoubleRoom() {
        super("Double", 2, 2000);
    }
}

class SuiteRoom extends Room {
    SuiteRoom() {
        super("Suite", 3, 5000);
    }
}

// UC3: RoomInventory class
class RoomInventory {

    // HashMap to store availability
    private HashMap<String, Integer> inventory = new HashMap<>();

    // Register room type
    void addRoom(String type, int count) {
        inventory.put(type, count);
    }

    // Get availability
    int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    // Update availability
    void updateAvailability(String type, int count) {
        inventory.put(type, count);
    }

    // Display all inventory
    void displayInventory() {
        System.out.println("\n--- Room Inventory ---");
        for (String type : inventory.keySet()) {
            System.out.println(type + " Rooms Available: " + inventory.get(type));
        }
    }
}

// Main class (same file)
public class BookMyStayApp {
    public static void main(String[] args) {

        // UC1
        System.out.println("=== Book My Stay Application ===");
        System.out.println("Version: 1.0");

        // UC2
        Room r1 = new SingleRoom();
        Room r2 = new DoubleRoom();
        Room r3 = new SuiteRoom();

        // Display room details
        System.out.println("\n--- Room Details ---");
        r1.displayDetails();
        r2.displayDetails();
        r3.displayDetails();

        // UC3: Inventory using HashMap
        RoomInventory inventory = new RoomInventory();

        // Register rooms
        inventory.addRoom("Single", 5);
        inventory.addRoom("Double", 3);
        inventory.addRoom("Suite", 2);

        // Display inventory
        inventory.displayInventory();

        // Example update
        inventory.updateAvailability("Single", 4);

        System.out.println("\nAfter Update:");
        inventory.displayInventory();

        System.out.println("\nApplication Terminated.");
    }
}