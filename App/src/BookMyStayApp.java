abstract class Room {
    protected int beds;
    protected int size;
    protected double price;

    public Room(int beds, int size, double price) {
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public void displayDetails() {
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sqft");
        System.out.println("Price per night: " + price);
    }

    public abstract String getRoomType();
}

class SingleRoom extends Room {
    public SingleRoom() {
        super(1, 250, 1500.0);
    }

    public String getRoomType() {
        return "Single Room";
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, 400, 2500.0);
    }

    public String getRoomType() {
        return "Double Room";
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, 750, 5000.0);
    }

    public String getRoomType() {
        return "Suite Room";
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {

        System.out.println("Hotel Room Initialization\n");

        int singleAvailability = 5;
        int doubleAvailability = 3;
        int suiteAvailability = 2;

        Room single = new SingleRoom();
        Room dbl = new DoubleRoom();
        Room suite = new SuiteRoom();

        displayRoom(single, singleAvailability);
        displayRoom(dbl, doubleAvailability);
        displayRoom(suite, suiteAvailability);
    }

    public static void displayRoom(Room room, int availability) {
        System.out.println(room.getRoomType() + ":");
        room.displayDetails();
        System.out.println("Available: " + availability);
        System.out.println();
    }
}


