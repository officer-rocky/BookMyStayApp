import java.util.*;

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

class BookingRequestQueue {
    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
    }

    public Reservation pollNextRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

class InventoryService {
    private Map<String, Integer> inventory;
    private Map<String, Set<String>> allocatedRooms; // roomType -> set of room IDs

    public InventoryService() {
        inventory = new HashMap<>();
        allocatedRooms = new HashMap<>();

        // Initialize inventory counts
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);

        // Initialize allocatedRooms map
        allocatedRooms.put("Single Room", new HashSet<>());
        allocatedRooms.put("Double Room", new HashSet<>());
        allocatedRooms.put("Suite Room", new HashSet<>());
    }

    public boolean isAvailable(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public String allocateRoom(String roomType) {
        if (!isAvailable(roomType)) {
            return null; // no room available
        }

        // Generate a unique room ID that has not been assigned
        Set<String> assignedIds = allocatedRooms.get(roomType);
        int nextId = assignedIds.size() + 1;

        String roomId;
        do {
            roomId = roomType.substring(0, 2).toUpperCase() + String.format("%03d", nextId);
            nextId++;
        } while (assignedIds.contains(roomId));

        // Assign room and update inventory
        assignedIds.add(roomId);
        inventory.put(roomType, inventory.get(roomType) - 1);

        return roomId;
    }

    public int getAvailableCount(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }
}

class BookingService {
    private BookingRequestQueue requestQueue;
    private InventoryService inventoryService;

    public BookingService(BookingRequestQueue requestQueue, InventoryService inventoryService) {
        this.requestQueue = requestQueue;
        this.inventoryService = inventoryService;
    }

    public void processBookings() {
        while (!requestQueue.isEmpty()) {
            Reservation reservation = requestQueue.pollNextRequest();
            String roomType = reservation.getRoomType();

            if (inventoryService.isAvailable(roomType)) {
                String roomId = inventoryService.allocateRoom(roomType);
                reservation.setAssignedRoomId(roomId);
                System.out.println("Booking confirmed for " + reservation.getGuestName() +
                        " | Room Type: " + roomType +
                        " | Room ID: " + roomId);
            } else {
                System.out.println("Booking failed for " + reservation.getGuestName() +
                        ". No " + roomType + " available.");
            }
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        InventoryService inventoryService = new InventoryService();
        BookingService bookingService = new BookingService(bookingQueue, inventoryService);

        // Guests submit booking requests
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Suite Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Double Room"));
        bookingQueue.addRequest(new Reservation("Diana", "Single Room"));
        bookingQueue.addRequest(new Reservation("Evan", "Suite Room"));
        bookingQueue.addRequest(new Reservation("Fiona", "Single Room"));
        bookingQueue.addRequest(new Reservation("George", "Double Room"));
        bookingQueue.addRequest(new Reservation("Hannah", "Single Room"));
        bookingQueue.addRequest(new Reservation("Ian", "Single Room"));

        System.out.println("Processing booking requests...\n");
        bookingService.processBookings();
    }
}