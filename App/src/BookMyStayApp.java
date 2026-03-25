import java.util.LinkedList;
import java.util.Queue;

class Reservation {
    private String guestName;
    private String roomType;

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

    @Override
    public String toString() {
        return "Reservation{" +
                "guestName='" + guestName + '\'' +
                ", roomType='" + roomType + '\'' +
                '}';
    }
}

class BookingRequestQueue {
    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        this.queue = new LinkedList<>();
    }

    // Add a new reservation request to the queue
    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Added booking request: " + reservation);
    }

    // Peek at the next reservation to process without removing
    public Reservation peekNextRequest() {
        return queue.peek();
    }

    // Poll (remove and return) the next reservation for processing
    public Reservation pollNextRequest() {
        return queue.poll();
    }

    // Check if queue is empty
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // Get queue size
    public int getSize() {
        return queue.size();
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Simulate guests submitting booking requests
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Suite Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Double Room"));

        System.out.println("\nCurrent booking requests in queue: " + bookingQueue.getSize());

        System.out.println("\nPeek next request to process: " + bookingQueue.peekNextRequest());

        // Processing requests in order (simulated)
        while (!bookingQueue.isEmpty()) {
            Reservation next = bookingQueue.pollNextRequest();
            System.out.println("Processing booking for guest: " + next.getGuestName() + ", Room: " + next.getRoomType());
        }

        System.out.println("\nAll booking requests processed.");
    }
}