package isp.lab7.safehome;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoorLockController implements ControllerInterface {
    private Map<Tenant, AccessKey> validAccess;
    private ArrayList<AccessLog> accessLogs;
    private Door door;
    private int attempts = 0;

    public DoorLockController(Map<Tenant, AccessKey> validAccess, ArrayList<AccessLog> accessLogs, Door door) {
        this.validAccess = validAccess;
        this.accessLogs = accessLogs;
        this.door = door;
    }

    @Override
    public DoorStatus enterPin(String pin) throws InvalidPinException, TooManyAttemptsException {
        Map.Entry<Tenant, AccessKey> item = validAccess.entrySet().stream().filter(code -> code.getValue().getPin().equals(pin)).findFirst().orElse(null);

        if (MASTER_KEY.equals(pin)) {
            door.unlockDoor();
            attempts = 0;
            log("MasterTenant", "master pin inserted", null);
            return door.getStatus();
        } else {

            if (item == null || attempts > 2) { // case that a pin doesnt exists or exits but we are in lock mode
                attempts++;
                if (attempts >= 3) {
                    log("Unknown tenant", "pin too many attempts exception", "Too many wrong pin insertions");
                    throw new TooManyAttemptsException("Too many wrong pin insertions");
                } else {
                    log("Unknown tenant", "pin invalid pin exception", "Inserted pin is incorrect");
                    throw new InvalidPinException("Inserted pin is incorrect");
                }
            } else { // case that the pin exits
                if (door.getStatus() == DoorStatus.CLOSE) {
                    door.unlockDoor();
                } else {
                    door.lockDoor();
                }
                log(item.getKey().getName(), "successful pin insertion", null);
                return door.getStatus();
            }
        }
    }

    @Override
    public void addTenant(String pin, String name) throws TenantAlreadyExistsException {
        if (validAccess.containsKey(new Tenant(name))) {
            log(name, "add tenant exception", "This tenant already exist");
            throw new TenantAlreadyExistsException("This tenant already exist");
        } else {
            log(name, "add tenant", null);
            validAccess.put(new Tenant(name), new AccessKey(pin));
        }
    }

    @Override
    public void removeTenant(String name) throws TenantNotFoundException {
        if (validAccess.containsKey(new Tenant(name))) {
            validAccess.remove(new Tenant(name)); // or first we get him and then delete, but we have equals and this will work
            log(name, "remove tenant", null);
        } else {
            log(name, "remove tenant exception", "Tenant doesn't exist");
            throw new TenantNotFoundException("Tenant doesn't exist");
        }
    }

    public void log(String tenantName, String operation, String errorMessage) {
        accessLogs.add(new AccessLog(tenantName, LocalDateTime.now(), operation, door.getStatus(), errorMessage));
    }

    public List<AccessLog> getAccessLogs() {
        return accessLogs;
    }
}
