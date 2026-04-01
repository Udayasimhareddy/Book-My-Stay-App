import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Custom Exceptions
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

// Booking System
class BookingSystem {
    private Map<String, Integer> inventory;

    public BookingSystem() {
        inventory = new HashMap<>();
        inventory.put("single", 5);
        inventory.put("double", 3);
        inventory.put("suite", 2);
    }

    private void validateRoomType(String roomType) throws InvalidRoomTypeException {
        if (!inventory.containsKey(roomType)) {
            throw new InvalidRoomTypeException(
                    "Invalid room type: '" + roomType + "'. Available: " + inventory.keySet()
            );
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
                    "Not enough '" + roomType + "' rooms. Requested: " + quantity +
                            ", Available: " + inventory.get(roomType)
            );
        }
    }

    public String bookRoom(String roomType, int quantity) throws BookingException {
        // Fail-fast validation
        validateRoomType(roomType);
        validateQuantity(quantity);
        checkAvailability(roomType, quantity);

        // Safe state update
        inventory.put(roomType, inventory.get(roomType) - quantity);

        return "Booking successful! " + quantity + " '" + roomType + "' room(s) reserved.";
    }

    public void showInventory() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

// Main Class
public class BookMyStayApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BookingSystem system = new BookingSystem();

        while (true) {
            system.showInventory();

            try {
                System.out.println("\nEnter booking details:");
                System.out.print("Room type (single/double/suite): ");
                String roomType = scanner.nextLine().trim().toLowerCase();

                System.out.print("Number of rooms: ");
                int quantity = Integer.parseInt(scanner.nextLine().trim());

                String result = system.bookRoom(roomType, quantity);
                System.out.println(result);

            } catch (NumberFormatException e) {
                System.out.println("Invalid input: Please enter a valid number.");

            } catch (BookingException e) {
                // Graceful failure
                System.out.println("Booking failed: " + e.getMessage());

            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }

            System.out.print("\nDo you want to continue? (yes/no): ");
            String cont = scanner.nextLine().trim().toLowerCase();

            if (!cont.equals("yes")) {
                System.out.println("Exiting system. Goodbye!");
                break;
            }
        }

        scanner.close();
    }
}