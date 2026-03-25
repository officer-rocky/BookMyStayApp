import java.util.ArrayList;
import java.util.List;

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

class BookingHistory {
    private final List<Reservation> confirmedBookings;

    public BookingHistory() {
        confirmedBookings = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(confirmedBookings); // defensive copy
    }

    public int getTotalBookings() {
        return confirmedBookings.size();
    }
}

class BookingReportService {
    private BookingHistory bookingHistory;

    public BookingReportService(BookingHistory bookingHistory) {
        this.bookingHistory = bookingHistory;
    }

    public void printBookingSummary() {
        List<Reservation> bookings = bookingHistory.getAllReservations();

        System.out.println("Booking History Summary");
        System.out.println("------------------------");
        System.out.println("Total confirmed bookings: " + bookings.size());
        System.out.println();

        for (Reservation reservation : bookings) {
            System.out.println(reservation);
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        BookingHistory bookingHistory = new BookingHistory();

        // Sample confirmed reservations
        Reservation r1 = new Reservation("Alice", "Single Room");
        r1.setAssignedRoomId("SI001");

        Reservation r2 = new Reservation("Bob", "Suite Room");
        r2.setAssignedRoomId("SU001");

        Reservation r3 = new Reservation("Charlie", "Double Room");
        r3.setAssignedRoomId("DO001");

        // Add to booking history as reservations are confirmed
        bookingHistory.addReservation(r1);
        bookingHistory.addReservation(r2);
        bookingHistory.addReservation(r3);

        BookingReportService reportService = new BookingReportService(bookingHistory);

        // Admin views booking history summary
        reportService.printBookingSummary();
    }
}