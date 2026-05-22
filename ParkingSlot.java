class Vehicle {
    private String vehicleNumber;

    public Vehicle(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }
}

class ParkingLot {
    private ParkingSlot[] parkingSlots;

    public ParkingLot(int capacity) {
        parkingSlots = new ParkingSlot[capacity];

        for (int i = 0; i < capacity; i++) {
            parkingSlots[i] = new ParkingSlot(i + 1);
        }
    }

    public void park(Vehicle vehicle) {
        for (ParkingSlot slot : parkingSlots) {
            if (!slot.isOccupied()) {
                slot.parkVehicle(vehicle);
                System.out.println("Vehicle parked at Slot: " + slot.getSlotNumber());
                return;
            }
        }
        System.out.println("No Parking Slot Available");
    }

    public void remove(String vehicleNumber) {
        for (ParkingSlot slot : parkingSlots) {
            if (slot.isOccupied() &&
                slot.getParkedVehicle().getVehicleNumber().equals(vehicleNumber)) {
                slot.removeVehicle();
                System.out.println("Vehicle Removed");
                return;
            }
        }
        System.out.println("Vehicle Not Found");
    }

    public void showAvailableSlots() {
        for (ParkingSlot slot : parkingSlots) {
            if (!slot.isOccupied()) {
                System.out.println("Available Slot: " + slot.getSlotNumber());
            }
        }
    }
}

public class ParkingSlot {
    private int slotNumber;
    private Vehicle parkedVehicle;
    private boolean occupied;

    public ParkingSlot(int slotNumber) {
        this.slotNumber = slotNumber;
        this.occupied = false;
    }

    public boolean parkVehicle(Vehicle vehicle) {
        if (occupied) {
            return false;
        }
        parkedVehicle = vehicle;
        occupied = true;
        return true;
    }

    public void removeVehicle() {
        parkedVehicle = null;
        occupied = false;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot(3);

        Vehicle vehicle1 = new Vehicle("UP32AB1234");
        Vehicle vehicle2 = new Vehicle("UP32XY5678");
        Vehicle vehicle3 = new Vehicle("UP32ZZ9999");

        parkingLot.park(vehicle1);
        parkingLot.park(vehicle2);
        parkingLot.park(vehicle3);

        System.out.println("\nAvailable Slots:");
        parkingLot.showAvailableSlots();

        System.out.println("\nRemoving Vehicle:");
        parkingLot.remove("UP32AB1234");

        System.out.println("\nAvailable Slots After Removal:");
        parkingLot.showAvailableSlots();
    }
}