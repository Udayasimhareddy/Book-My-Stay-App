import java.util.LinkedList;
import java.util.Queue;

/**
 * Main application class to bootstrap the simulation.
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("--- Starting Hotel Booking Concurrency Simulation ---\n");

        // 1. Shared Mutable State: The Inventory and the Queue
        HotelInventory sharedInventory = new HotelInventory(15); // Only 15 rooms available
        BookingQueue sharedQueue = new BookingQueue();

        // 2. Concurrent Booking Processors (Worker Threads)
        int processorCount = 3;
        BookingProcessor[] processors = new BookingProcessor[processorCount];
        for (int i = 0; i < processorCount; i++) {
            processors[i] = new BookingProcessor(sharedQueue, sharedInventory, "Processor-" + (i + 1));
            processors[i].start();
        }

        // 3. Multiple Guests submitting requests concurrently
        int totalGuests = 30; // 30 guests competing for 15 rooms
        Thread[] guestThreads = new Thread[totalGuests];
        for (int i = 0; i < totalGuests; i++) {
            final int guestId = i + 1;
            guestThreads[i] = new Thread(() -> {
                BookingRequest request = new BookingRequest("Guest " + guestId);
                sharedQueue.addRequest(request);
            }, "GuestThread-" + guestId);
            guestThreads[i].start();
        }

        // Wait for all guest threads to finish submitting requests
        for (Thread thread : guestThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Signal processors that no more requests are coming
        sharedQueue.stopAcceptingRequests();

        // Wait for processors to finish emptying the queue
        for (BookingProcessor processor : processors) {
            try {
                processor.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\n--- Simulation Complete ---");
        System.out.println("Final Available Rooms: " + sharedInventory.getAvailableRooms());
    }
}

/**
 * Represents a single guest's booking request.
 */
class BookingRequest {
    private final String guestName;

    public BookingRequest(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestName() {
        return guestName;
    }
}

/**
 * Shared Data Structure 1: The Booking Queue.
 * Threads retrieve and add requests using synchronized access.
 */
class BookingQueue {
    private final Queue<BookingRequest> queue = new LinkedList<>();
    private boolean isAcceptingRequests = true;

    // Synchronized Access to prevent Race Conditions when adding to the queue
    public synchronized void addRequest(BookingRequest request) {
        queue.add(request);
        System.out.println("[Queue] Request added for: " + request.getGuestName());
        notifyAll(); // Wake up any waiting processor threads
    }

    // Synchronized Access for retrieving requests
    public synchronized BookingRequest getRequest() throws InterruptedException {
        // Wait if the queue is empty but we are still accepting new requests
        while (queue.isEmpty() && isAcceptingRequests) {
            wait();
        }
        
        // If queue is empty and we are done accepting, signal thread to stop
        if (queue.isEmpty() && !isAcceptingRequests) {
            return null; 
        }
        
        return queue.poll();
    }

    public synchronized void stopAcceptingRequests() {
        isAcceptingRequests = false;
        notifyAll(); // Wake up any threads stuck in wait() so they can terminate safely
    }
}

/**
 * Shared Data Structure 2: The Hotel Inventory.
 * Manages Room allocations inside Critical Sections.
 */
class HotelInventory {
    private int availableRooms;

    public HotelInventory(int totalRooms) {
        this.availableRooms = totalRooms;
    }

    /**
     * CRITICAL SECTION: This entire method is synchronized.
     * This ensures Thread Safety and prevents double-allocation of the same room.
     */
    public synchronized boolean allocateRoom(String guestName, String processorName) {
        if (availableRooms > 0) {
            // Artificial delay to simulate processing time. 
            // Without 'synchronized', this delay would virtually guarantee a Race Condition.
            try { Thread.sleep(20); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            
            availableRooms--;
            System.out.println("[" + processorName + "] SUCCESS: Allocated room to " 
                               + guestName + ". Rooms left: " + availableRooms);
            return true;
        } else {
            System.out.println("[" + processorName + "] FAILED: No rooms available for " + guestName);
            return false;
        }
    }

    public int getAvailableRooms() {
        return availableRooms;
    }
}

/**
 * Concurrent Booking Processor: Runs in a multi-threaded environment.
 */
class BookingProcessor extends Thread {
    private final BookingQueue queue;
    private final HotelInventory inventory;

    public BookingProcessor(BookingQueue queue, HotelInventory inventory, String name) {
        super(name);
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Retrieve request in a thread-safe manner
                BookingRequest request = queue.getRequest();
                
                // Null indicates the queue is empty and no more requests are coming
                if (request == null) {
                    break; 
                }

                // Process room allocation and inventory update
                inventory.allocateRoom(request.getGuestName(), this.getName());
            }
        } catch (InterruptedException e) {
            System.out.println(this.getName() + " was interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}