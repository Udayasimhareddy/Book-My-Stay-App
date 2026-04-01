import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data class for requests.
 * Name and Constructor must match.
 */
class BookingRequest {
    private final String guestName;
    private final String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

/**
 * Main Class - Must match file name: BookMyStayApp.java
 */
public class BookMyStayApp {

    private final Map<String, AtomicInteger> inventoryService = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> allocatedRooms = new ConcurrentHashMap<>();
    private final Queue<BookingRequest> requestQueue = new LinkedList<>();

    // Constructor name now matches the Class name
    public BookMyStayApp() {
        inventoryService.put("DELUXE", new AtomicInteger(10));
        inventoryService.put("SUITE", new AtomicInteger(5));
        allocatedRooms.put("DELUXE", ConcurrentHashMap.newKeySet());
        allocatedRooms.put("SUITE", ConcurrentHashMap.newKeySet());
    }

    public synchronized String processBooking() {
        BookingRequest request = requestQueue.poll();
        if (request == null) return "Queue empty.";

        String roomType = request.getRoomType();
        AtomicInteger availableCount = inventoryService.get(roomType);

        if (availableCount != null && availableCount.get() > 0) {
            String roomId = roomType + "-" + (100 + (int)(Math.random() * 900));
            Set<String> assignedSet = allocatedRooms.get(roomType);

            if (assignedSet.add(roomId)) {
                availableCount.decrementAndGet();
                return String.format("Confirmed: Room %s assigned to %s", roomId, request.getGuestName());
            }
        }
        return "Booking Failed: No availability for " + roomType;
    }

    public void addRequest(BookingRequest request) {
        requestQueue.add(request);
    }

    // Main method inside the BookMyStayApp class
    public static void main(String[] args) {
        BookMyStayApp system = new BookMyStayApp();

        // Add sample requests
        system.addRequest(new BookingRequest("Alice", "SUITE"));
        system.addRequest(new BookingRequest("Bob", "DELUXE"));
        system.addRequest(new BookingRequest("Charlie", "SUITE"));

        System.out.println("--- Hotel Booking System ---");

        // Process requests until empty
        String result;
        while (!(result = system.processBooking()).equals("Queue empty.")) {
            System.out.println(result);
        }
    }
}