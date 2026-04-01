import java.util.*;

// ------------------ Custom Exceptions ------------------

class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}

class InvalidRoomTypeException extends BookingException {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

class InvalidQuantityException extends BookingException {
    public InvalidQuantityException(String message) {
        super(message);
    }
}

class InsufficientInventoryException extends BookingException {
    public InsufficientInventoryException(String message) {
        super(message);
    }
}

class BookingNotFoundException extends BookingException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}

class AlreadyCancelledException extends BookingException {
    public AlreadyCancelledException(String message) {
        super(message);
    }
}

// ------------------ Booking Model ------------------

class Booking {
    String bookingId;
    String roomType;
    int quantity;
    boolean isCancelled;

    public Booking(String bookingId, String roomType, int quantity) {
        this.bookingId = bookingId;
        this.roomType = roomType;
        this.quantity = quantity;
        this.isCancelled = false;
    }
}

// ------------------ Booking System ------------------

class BookingSystem {
    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, Booking> bookings = new HashMap<>();

    // Stack for rollback (LIFO)
    private Stack<String> rollbackStack = new Stack<>();

    private int bookingCounter = 1;

    public BookingSystem() {
        inventory.put("single", 5);
        inventory.put("double", 3);
        inventory.put("suite", 2);
    }

    // ----------- Validation Methods -----------

    private void validateRoomType(String roomType) throws InvalidRoomTypeException {
        if (!inventory.containsKey(roomType)) {
            throw new InvalidRoomTypeException("Invalid room type: " + roomType);
        }
    }

    private void validateQuantity(int quantity) throws InvalidQuantityException {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than zero.");
        }
    }

    private void checkAvailability(String roomType, int quantity) throws InsufficientInventoryException {
        if (inventory.get(roomType) < quantity) {
            throw new InsufficientInventoryException(
                    "Not enough rooms. Available: " + inventory.get(roomType)
            );
        }
    }

    private Booking validateBookingExists(String bookingId)
            throws BookingNotFoundException, AlreadyCancelledException {

        if (!bookings.containsKey(bookingId)) {
            throw new BookingNotFoundException("Booking ID not found: " + bookingId);
        }

        Booking booking = bookings.get(bookingId);

        if (booking.isCancelled) {
            throw new AlreadyCancelledException("Booking already cancelled: " + bookingId);
        }

        return booking;
    }

    // ----------- Booking -----------

    public String bookRoom(String roomType, int quantity) throws BookingException {

        validateRoomType(roomType);
        validateQuantity(quantity);
        checkAvailability(roomType, quantity);

        String bookingId = "B" + bookingCounter++;

        // Reduce inventory
        inventory.put(roomType, inventory.get(roomType) - quantity);

        // Save booking
        bookings.put(bookingId, new Booking(bookingId, roomType, quantity));

        return "Booking successful! ID: " + bookingId;
    }

    // ----------- Cancellation with Rollback -----------

    public String cancelBooking(String bookingId) throws BookingException {

        // Validate booking existence
        Booking booking = validateBookingExists(bookingId);

        // Step 1: Record rollback info (simulate room IDs)
        for (int i = 0; i < booking.quantity; i++) {
            rollbackStack.push(booking.roomType + "_ROOM_RELEASED");
        }

        // Step 2: Restore inventory immediately
        inventory.put(booking.roomType,
                inventory.get(booking.roomType) + booking.quantity);

        // Step 3: Mark booking as cancelled
        booking.isCancelled = true;

        // Step 4: Update history (implicit via object state)

        return "Cancellation successful for Booking ID: " + bookingId;
    }

    // ----------- Utility -----------

    public void showInventory() {
        System.out.println("\nInventory:");
        for (String key : inventory.keySet()) {
            System.out.println(key + ": " + inventory.get(key));
        }
    }

    public void showRollbackStack() {
        System.out.println("\nRollback Stack (LIFO): " + rollbackStack);
    }
}

// ------------------ Main ------------------

public class BookMyStayApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BookingSystem system = new BookingSystem();

        while (true) {
            system.showInventory();

            try {
                System.out.println("\n1. Book Room");
                System.out.println("2. Cancel Booking");
                System.out.print("Choose option: ");
                int choice = Integer.parseInt(scanner.nextLine());

                if (choice == 1) {
                    System.out.print("Room type: ");
                    String roomType = scanner.nextLine().trim().toLowerCase();

                    System.out.print("Quantity: ");
                    int qty = Integer.parseInt(scanner.nextLine());

                    String result = system.bookRoom(roomType, qty);
                    System.out.println(result);

                } else if (choice == 2) {
                    System.out.print("Enter Booking ID: ");
                    String bookingId = scanner.nextLine().trim();

                    String result = system.cancelBooking(bookingId);
                    System.out.println(result);

                } else {
                    System.out.println("Invalid choice.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input format.");

            } catch (BookingException e) {
                // Graceful failure handling
                System.out.println("Operation failed: " + e.getMessage());

            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }

            system.showRollbackStack();

            System.out.print("\nContinue? (yes/no): ");
            String cont = scanner.nextLine().trim().toLowerCase();
            if (!cont.equals("yes")) {
                System.out.println("Exiting system...");
                break;
            }
        }

        scanner.close();
    }
}