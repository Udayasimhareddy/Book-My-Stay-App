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

// Inventory (UC3)
class RoomInventory {
    private HashMap<String, Integer> inventory = new HashMap<>();

    void addRoom(String type, int count) {
        inventory.put(type, count);
    }

    int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }
}

// UC4: Search Service
class SearchService {

    void searchRooms(Room[] rooms, RoomInventory inventory) {
        System.out.println("\n--- Available Rooms ---");

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.type);

            // Filter unavailable rooms
            if (available > 0) {
                room.displayDetails();
                System.out.println("Available: " + available);
            }
        }
    }
}

// Main class
public class BookMyStayApp {
    public static void main(String[] args) {

        // UC1
        System.out.println("=== Book My Stay Application ===");
        System.out.println("Version: 1.0");

        // UC2: Room objects
        Room r1 = new SingleRoom();
        Room r2 = new DoubleRoom();
        Room r3 = new SuiteRoom();

        Room[] rooms = {r1, r2, r3};

        // UC3: Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single", 5);
        inventory.addRoom("Double", 0); // unavailable
        inventory.addRoom("Suite", 2);

        // UC4: Search
        SearchService search = new SearchService();
        search.searchRooms(rooms, inventory);

        System.out.println("\nSearch Completed (Read-Only Operation)");
    }
}