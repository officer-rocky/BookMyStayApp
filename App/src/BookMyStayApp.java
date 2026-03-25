import java.util.*;
import java.util.concurrent.*;

class Reservation {
    private String guestName;
    private String roomType;
    private String assignedRoomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
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

class InventoryService {
    private final Map<String, Integer> inventory;
    private final Map<String, Stack<String>> releasedRooms;

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

    public synchronized String allocateRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        if (available <= 0) return null;
        inventory.put(roomType, available - 1);

        Stack<String> stack = releasedRooms.get(roomType);
        String roomId;
        if (!stack.isEmpty()) {
            roomId = stack.pop();
        } else {
            roomId = roomType.substring(0, 2).toUpperCase() + UUID.randomUUID().toString().substring(0, 4);
        }
        return roomId;
    }

    public synchronized void rollbackRoom(String roomType, String roomId) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
        releasedRooms.get(roomType).push(roomId);
    }

    public synchronized int getAvailableCount(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }
}

class BookingService implements Runnable {
    private Reservation reservation;
    private InventoryService inventoryService;

    public BookingService(Reservation reservation, InventoryService inventoryService) {
        this.reservation = reservation;
        this.inventoryService = inventoryService;
    }

    @Override
    public void run() {
        synchronized (inventoryService) {
            String roomId = inventoryService.allocateRoom(reservation.getRoomType());
            if (roomId != null) {
                reservation.setAssignedRoomId(roomId);
                System.out.println(Thread.currentThread().getName() + " booked: " + reservation);
            } else {
                System.out.println(Thread.currentThread().getName() + " failed to book: " + reservation.getGuestName() + " (" + reservation.getRoomType() + ")");
            }
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) throws InterruptedException {
        InventoryService inventoryService = new InventoryService();

        List<Reservation> reservations = Arrays.asList(
                new Reservation("Alice", "Single Room"),
                new Reservation("Bob", "Single Room"),
                new Reservation("Charlie", "Single Room"),
                new Reservation("David", "Suite Room"),
                new Reservation("Eve", "Suite Room"),
                new Reservation("Frank", "Suite Room")
        );

        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (Reservation res : reservations) {
            executor.execute(new BookingService(res, inventoryService));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\nFinal inventory:");
        System.out.println("Single Room: " + inventoryService.getAvailableCount("Single Room"));
        System.out.println("Double Room: " + inventoryService.getAvailableCount("Double Room"));
        System.out.println("Suite Room: " + inventoryService.getAvailableCount("Suite Room"));
    }
}