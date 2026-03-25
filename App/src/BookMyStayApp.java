import java.util.*;

class Service {
    private String serviceName;
    private double price;

    public Service(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return serviceName + " ($" + price + ")";
    }
}

class Reservation {
    private String guestName;
    private String roomType;
    private String assignedRoomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.assignedRoomId = null;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setAssignedRoomId(String roomId) {
        this.assignedRoomId = roomId;
    }

    public String getAssignedRoomId() {
        return assignedRoomId;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "guestName='" + guestName + '\'' +
                ", roomType='" + roomType + '\'' +
                ", assignedRoomId='" + assignedRoomId + '\'' +
                '}';
    }
}

class AddOnServiceManager {
    private Map<String, List<Service>> reservationServices;

    public AddOnServiceManager() {
        reservationServices = new HashMap<>();
    }

    public void addServiceToReservation(String reservationId, Service service) {
        reservationServices.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
        System.out.println("Added service " + service + " to reservation " + reservationId);
    }

    public List<Service> getServicesForReservation(String reservationId) {
        return reservationServices.getOrDefault(reservationId, Collections.emptyList());
    }

    public double calculateAdditionalCost(String reservationId) {
        return getServicesForReservation(reservationId)
                .stream()
                .mapToDouble(Service::getPrice)
                .sum();
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        // Sample reservations
        Reservation res1 = new Reservation("Alice", "Single Room");
        Reservation res2 = new Reservation("Bob", "Suite Room");

        // Assume rooms are assigned already
        res1.setAssignedRoomId("SI001");
        res2.setAssignedRoomId("SU001");

        AddOnServiceManager addOnManager = new AddOnServiceManager();

        // Define some add-on services
        Service breakfast = new Service("Breakfast", 20.0);
        Service airportPickup = new Service("Airport Pickup", 50.0);
        Service spaAccess = new Service("Spa Access", 100.0);

        // Guests add services to their reservations
        addOnManager.addServiceToReservation(res1.getAssignedRoomId(), breakfast);
        addOnManager.addServiceToReservation(res1.getAssignedRoomId(), airportPickup);

        addOnManager.addServiceToReservation(res2.getAssignedRoomId(), spaAccess);

        // Display services and additional costs per reservation
        System.out.println("\nReservation Services and Additional Costs:");
        printReservationServices(res1, addOnManager);
        printReservationServices(res2, addOnManager);
    }

    private static void printReservationServices(Reservation reservation, AddOnServiceManager manager) {
        String roomId = reservation.getAssignedRoomId();
        List<Service> services = manager.getServicesForReservation(roomId);

        System.out.println("Reservation " + roomId + " (" + reservation.getGuestName() + "):");
        if (services.isEmpty()) {
            System.out.println("  No add-on services selected.");
        } else {
            services.forEach(service -> System.out.println("  - " + service));
            System.out.printf("  Total additional cost: $%.2f%n", manager.calculateAdditionalCost(roomId));
        }
    }
}