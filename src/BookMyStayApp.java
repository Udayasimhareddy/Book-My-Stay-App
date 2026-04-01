import java.util.*;

// -------------------- Add-On Service Interface --------------------
interface AddOnService {
    String getName();
    double getCost();
}

// -------------------- Dynamic User-Defined Service --------------------
class CustomService implements AddOnService {
    private String name;
    private double cost;

    public CustomService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }
}

// -------------------- Add-On Service Manager --------------------
class AddOnServiceManager {

    private Map<String, List<AddOnService>> reservationServicesMap = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        reservationServicesMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    public List<AddOnService> getServices(String reservationId) {
        return reservationServicesMap.getOrDefault(reservationId, Collections.emptyList());
    }

    public double calculateTotalServiceCost(String reservationId) {
        double total = 0.0;
        for (AddOnService service : getServices(reservationId)) {
            total += service.getCost();
        }
        return total;
    }

    public void printServices(String reservationId) {
        List<AddOnService> services = getServices(reservationId);

        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        System.out.println("\nServices for Reservation ID: " + reservationId);
        for (AddOnService service : services) {
            System.out.println("- " + service.getName() + ": ₹" + service.getCost());
        }
    }
}

// -------------------- Main (User Input Flow) --------------------
public class BookMyStayApp {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        AddOnServiceManager manager = new AddOnServiceManager();

        System.out.print("Enter Reservation ID: ");
        String reservationId = scanner.nextLine();

        while (true) {
            System.out.print("\nEnter service name (or type 'done' to finish): ");
            String name = scanner.nextLine();

            if (name.equalsIgnoreCase("done")) {
                break;
            }

            System.out.print("Enter service cost: ");
            double cost = scanner.nextDouble();
            scanner.nextLine(); // consume newline

            // Create user-defined service
            AddOnService service = new CustomService(name, cost);

            manager.addService(reservationId, service);
            System.out.println("Service added!");
        }

        // Display results
        manager.printServices(reservationId);

        double totalCost = manager.calculateTotalServiceCost(reservationId);
        System.out.println("\nTotal Add-On Cost: ₹" + totalCost);

        scanner.close();
    }
}