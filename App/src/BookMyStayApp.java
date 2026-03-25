import java.util.*;

class Reservation {
    private String guestName;
    private String roomType;
    private String assignedRoomId;
    private boolean isCancelled;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.assignedRoomId = null;
        this.isCancelled = false;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getAssignedRoomId() {
        return assignedRoomId;
    }

    public void setAssignedRoomId(String roomId) {
        this.assignedRoomId = roomId;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancel() {
        this.isCancelled = true;
    }

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

class InventoryService {
    private Map<String, Integer> inventory;
    private Map<String, Stack<String>> releasedRooms;

    public InventoryService() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);

        releasedRooms = new HashMap<>();
        releasedRooms.put("Single Room", new Stack<>());
        releasedRooms.put("Double Room", new Stack<>());
        releasedRooms.put("Suite Room", new Stack<>());
    }

    public boolean isAvailable(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public String allocateRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        if (available <= 0) return null;
        inventory.put(roomType, available - 1);

        Stack<String> stack = releasedRooms.get(roomType);
        String roomId;
        if (!stack.isEmpty()) {
            roomId = stack.pop(); // reuse released room
        } else {
            roomId = roomType.substring(0, 2).toUpperCase() + UUID.randomUUID().toString().substring(0, 4);
        }
        return roomId;
    }

    public void rollbackRoom(String roomType, String roomId) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
        releasedRooms.get(roomType).push(roomId);
    }

    public int getAvailableCount(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }
}

class CancellationService {
    private InventoryService inventoryService;

    public CancellationService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void cancelReservation(Reservation reservation) {
        if (reservation.isCancelled()) {
            System.out.println("Reservation already cancelled: " + reservation.getAssignedRoomId());
            return;
        }

        String roomType = reservation.getRoomType();
        String roomId = reservation.getAssignedRoomId();

        inventoryService.rollbackRoom(roomType, roomId);
        reservation.cancel();
        System.out.println("Reservation cancelled successfully: " + reservation);
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        InventoryService inventoryService = new InventoryService();
        CancellationService cancellationService = new CancellationService(inventoryService);

        // Sample reservations
        Reservation r1 = new Reservation("Alice", "Single Room");
        r1.setAssignedRoomId(inventoryService.allocateRoom(r1.getRoomType()));

        Reservation r2 = new Reservation("Bob", "Suite Room");
        r2.setAssignedRoomId(inventoryService.allocateRoom(r2.getRoomType()));

        System.out.println("Initial reservations:");
        System.out.println(r1);
        System.out.println(r2);

        // Cancel one reservation
        System.out.println("\nCancelling Alice's reservation...");
        cancellationService.cancelReservation(r1);

        // Attempt to cancel the same reservation again
        System.out.println("\nAttempting to cancel Alice's reservation again...");
        cancellationService.cancelReservation(r1);

        // Check inventory after cancellation
        System.out.println("\nCurrent inventory:");
        System.out.println("Single Room: " + inventoryService.getAvailableCount("Single Room"));
        System.out.println("Suite Room: " + inventoryService.getAvailableCount("Suite Room"));
    }
}