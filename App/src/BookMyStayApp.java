import java.util.*;

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
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

class InventoryService {
    private Map<String, Integer> inventory;

    public InventoryService() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public boolean isAvailable(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrementInventory(String roomType) throws InvalidBookingException {
        if (!inventory.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }
        int available = inventory.get(roomType);
        if (available <= 0) {
            throw new InvalidBookingException("No availability for room type: " + roomType);
        }
        inventory.put(roomType, available - 1);
    }

    public int getAvailableCount(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }
}

class BookingValidator {
    private Set<String> validRoomTypes;

    public BookingValidator() {
        validRoomTypes = new HashSet<>(Arrays.asList("Single Room", "Double Room", "Suite Room"));
    }

    public void validateReservation(Reservation reservation) throws InvalidBookingException {
        if (reservation.getGuestName() == null || reservation.getGuestName().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }
        if (!validRoomTypes.contains(reservation.getRoomType())) {
            throw new InvalidBookingException("Invalid room type: " + reservation.getRoomType());
        }
    }
}

class BookingService {
    private InventoryService inventoryService;
    private BookingValidator validator;

    public BookingService(InventoryService inventoryService, BookingValidator validator) {
        this.inventoryService = inventoryService;
        this.validator = validator;
    }

    public void processBooking(Reservation reservation) {
        try {
            validator.validateReservation(reservation);

            if (!inventoryService.isAvailable(reservation.getRoomType())) {
                throw new InvalidBookingException("Room not available for type: " + reservation.getRoomType());
            }

            inventoryService.decrementInventory(reservation.getRoomType());
            reservation.setAssignedRoomId(generateRoomId(reservation.getRoomType()));

            System.out.println("Booking confirmed: " + reservation);

        } catch (InvalidBookingException e) {
            System.err.println("Booking failed for " + reservation.getGuestName() + ": " + e.getMessage());
        }
    }

    private String generateRoomId(String roomType) {
        return roomType.substring(0, 2).toUpperCase() + UUID.randomUUID().toString().substring(0, 4);
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        InventoryService inventoryService = new InventoryService();
        BookingValidator validator = new BookingValidator();
        BookingService bookingService = new BookingService(inventoryService, validator);

        // Valid booking
        bookingService.processBooking(new Reservation("Alice", "Single Room"));

        // Invalid booking: wrong room type
        bookingService.processBooking(new Reservation("Bob", "Penthouse"));

        // Invalid booking: empty guest name
        bookingService.processBooking(new Reservation("", "Double Room"));

        // Attempt to book beyond inventory
        for (int i = 0; i < 6; i++) {
            bookingService.processBooking(new Reservation("Guest" + i, "Suite Room"));
        }
    }
}