import java.io.*;
import java.util.*;

class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String guestName;
    private String roomType;
    private String assignedRoomId;
    private boolean isCancelled;

    public Reservation(String guestName, String roomType, String assignedRoomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.assignedRoomId = assignedRoomId;
        this.isCancelled = false;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getAssignedRoomId() { return assignedRoomId; }
    public boolean isCancelled() { return isCancelled; }
    public void cancel() { this.isCancelled = true; }

    @Override
    public String toString() {
        return "Reservation{" +
                "guestName='" + guestName + '\'' +
                ", roomType='" + roomType + '\'' +
                ", assignedRoomId='" + assignedRoomId + '\'' +
                ", isCancelled=" + isCancelled +
                '}';
    }
}

class InventoryService implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> inventory;

    public InventoryService() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public int getAvailableCount(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public boolean allocate(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        if (available <= 0) return false;
        inventory.put(roomType, available - 1);
        return true;
    }

    public void release(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
    }

    @Override
    public String toString() {
        return "InventoryService{" + inventory + '}';
    }
}

class PersistenceService {
    private static final String FILE_NAME = "system_state.ser";

    public static void saveState(InventoryService inventory, List<Reservation> bookings) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inventory);
            oos.writeObject(bookings);
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    public static Object[] loadState() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No previous state found. Starting fresh.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            InventoryService inventory = (InventoryService) ois.readObject();
            List<Reservation> bookings = (List<Reservation>) ois.readObject();
            System.out.println("System state restored successfully.");
            return new Object[]{inventory, bookings};
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading state: " + e.getMessage());
            return null;
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        Object[] state = PersistenceService.loadState();
        InventoryService inventory;
        List<Reservation> bookingHistory;

        if (state == null) {
            inventory = new InventoryService();
            bookingHistory = new ArrayList<>();
        } else {
            inventory = (InventoryService) state[0];
            bookingHistory = (List<Reservation>) state[1];
        }

        // Sample bookings
        Reservation r1 = new Reservation("Alice", "Single Room", "SI101");
        if (inventory.allocate(r1.getRoomType())) bookingHistory.add(r1);

        Reservation r2 = new Reservation("Bob", "Suite Room", "SU201");
        if (inventory.allocate(r2.getRoomType())) bookingHistory.add(r2);

        System.out.println("Current bookings:");
        bookingHistory.forEach(System.out::println);

        System.out.println("\nCurrent inventory:");
        System.out.println(inventory);

        // Save state
        PersistenceService.saveState(inventory, bookingHistory);
    }
}