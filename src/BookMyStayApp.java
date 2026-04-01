import java.util.*;

// -------------------- Reservation Model --------------------
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private double price;

    public Reservation(String reservationId, String guestName, String roomType, double price) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.price = price;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room: " + roomType +
                ", Price: ₹" + price;
    }
}

// -------------------- Booking History --------------------
class BookingHistory {

    // Maintains insertion order
    private List<Reservation> confirmedBookings = new ArrayList<>();

    // Add confirmed booking
    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    // Retrieve all bookings (read-only style)
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(confirmedBookings); // return copy (safe)
    }
}

// -------------------- Booking Report Service --------------------
class BookingReportService {

    // Display all bookings
    public void printAllBookings(List<Reservation> reservations) {
        if (reservations.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        System.out.println("\n--- Booking History ---");
        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }

    // Generate summary report
    public void generateSummary(List<Reservation> reservations) {
        int totalBookings = reservations.size();
        double totalRevenue = 0.0;

        for (Reservation r : reservations) {
            totalRevenue += r.getPrice();
        }

        System.out.println("\n--- Booking Summary ---");
        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Total Revenue: ₹" + totalRevenue);
    }
}

// -------------------- Main (User Input Flow) --------------------
public class BookMyStayApp {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        while (true) {
            System.out.println("\n1. Create Booking");
            System.out.println("2. View Booking History");
            System.out.println("3. View Summary Report");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {

                case 1:
                    // User-defined booking input
                    System.out.print("Enter Reservation ID: ");
                    String id = scanner.nextLine();

                    System.out.print("Enter Guest Name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter Room Type: ");
                    String room = scanner.nextLine();

                    System.out.print("Enter Price: ");
                    double price = scanner.nextDouble();
                    scanner.nextLine();

                    // Simulate booking confirmation
                    Reservation reservation = new Reservation(id, name, room, price);
                    history.addReservation(reservation);

                    System.out.println("Booking confirmed and added to history!");
                    break;

                case 2:
                    // Admin views history
                    reportService.printAllBookings(history.getAllReservations());
                    break;

                case 3:
                    // Admin views summary
                    reportService.generateSummary(history.getAllReservations());
                    break;

                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}