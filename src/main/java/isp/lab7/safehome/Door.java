package isp.lab7.safehome;

public class Door {
    private DoorStatus status;

    public Door(DoorStatus status) {
        this.status = status;
    }

    public DoorStatus getStatus() {
        return status;
    }

    public void setStatus(DoorStatus status) {
        this.status = status;
    }

    public void lockDoor() {
        status = DoorStatus.CLOSE;
    }

    public void unlockDoor() {
        status = DoorStatus.OPEN;
    }
}
