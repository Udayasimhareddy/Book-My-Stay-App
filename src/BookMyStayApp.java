import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application class demonstrating persistence and state recovery.
 */
public class BookMyStayApp {

    private static final String DATA_FILE = "hotel_state.dat";

    public static void main(String[] args) {
        System.out.println("--- Starting Persistent Hotel Booking System ---\n");

        PersistenceService persistence = new PersistenceService(DATA_FILE);
        
        // 1. System Restarts & Loading Persisted Data
        HotelInventory inventory = persistence.loadState();

        // 2. Failure Tolerance & Graceful Recovery
        if (inventory == null) {
            System.out.println("Starting with a fresh, in-memory inventory...");
            inventory = new HotelInventory(15); // Initialize with 15 rooms
        } else {
            System.out.println("Successfully recovered previous system state!");
            System.out.println("Currently available rooms: " + inventory.getAvailableRooms());
            System.out.println("Previous Bookings: " + inventory.getBookingHistory());
        }

        // 3. System Resumes Operation
        System.out.println("\n--- Processing New Bookings ---");
        // We simulate a few bookings. Run the program multiple times to see the state carry over!
        inventory.bookRoom("Guest-" + System.currentTimeMillis() % 1000); 
        inventory.bookRoom("Guest-" + (System.currentTimeMillis() + 1) % 1000);

        // 4. System Prepares for Shutdown & Serializes State
        System.out.println("\n--- System Shutting Down ---");
        persistence.saveState(inventory);
    }
}

/**
 * Stateful Application Data: The Inventory.
 * Implements Serializable so its state can be converted to a byte stream.
 */
class HotelInventory implements Serializable {
    // Best practice for serialization: ensures version compatibility
    private static final long serialVersionUID = 1L; 
    
    private int availableRooms;
    private final List<String> bookingHistory;

    public HotelInventory(int totalRooms) {
        this.availableRooms = totalRooms;
        this.bookingHistory = new ArrayList<>();
    }

    public boolean bookRoom(String guestName) {
        if (availableRooms > 0) {
            availableRooms--;
            bookingHistory.add(guestName);
            System.out.println("SUCCESS: Booked room for " + guestName + ". Rooms left: " + availableRooms);
            return true;
        } else {
            System.out.println("FAILED: No rooms available for " + guestName);
            return false;
        }
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public List<String> getBookingHistory() {
        return bookingHistory;
    }
}

/**
 * Persistence Service: Handles the heavy lifting of saving and retrieving.
 * Prepares learners for how a database repository might be structured later.
 */
class PersistenceService {
    private final String filePath;

    public PersistenceService(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Deserialization: Restores the object from a file back into memory.
     */
    public HotelInventory loadState() {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[Persistence Service] No existing save file found.");
            return null; // Handled gracefully by the caller
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            System.out.println("[Persistence Service] Loading state from " + filePath + "...");
            // Deserializing the byte stream back into a HotelInventory object
            return (HotelInventory) ois.readObject(); 
        } catch (IOException | ClassNotFoundException e) {
            // Failure Tolerance: If the file is corrupted, we catch the error, log it, and return null
            System.out.println("[Persistence Service] WARNING: Failed to load state. File may be corrupted.");
            System.out.println("Reason: " + e.getMessage());
            return null; 
        }
    }

    /**
     * Serialization & Inventory Snapshot: Captures the current state and writes it.
     */
    public void saveState(HotelInventory inventory) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            // Serializing the object into a byte stream
            oos.writeObject(inventory); 
            System.out.println("[Persistence Service] System snapshot safely written to " + filePath);
        } catch (IOException e) {
            System.out.println("[Persistence Service] ERROR: Failed to save system state.");
            System.out.println("Reason: " + e.getMessage());
        }
    }
}