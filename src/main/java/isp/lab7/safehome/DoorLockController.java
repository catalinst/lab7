package isp.lab7.safehome;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoorLockController implements ControllerInterface {
    private Map<Tenant, AccessKey> validAccess;
    private ArrayList<AccessLog> accessLogs;
    private Door door;
    private int attempts = 0;
    private Operations operation = new Operations();

    public DoorLockController(Map<Tenant, AccessKey> validAccess, ArrayList<AccessLog> accessLogs, Door door) {
        this.validAccess = validAccess;
        this.accessLogs = accessLogs;
        this.door = door;
    }

    @Override
    public DoorStatus enterPin(String pin) throws InvalidPinException, TooManyAttemptsException, IOException {
        Map.Entry<Tenant, AccessKey> item = validAccess.entrySet().stream().filter(code -> code.getValue().getPin().equals(pin)).findFirst().orElse(null);

        if (MASTER_KEY.equals(pin)) {
            door.unlockDoor();
            attempts = 0;
            operation.writeLog(log("MasterTenant", "master pin inserted", null),"master-tenant");
            return door.getStatus();
        } else {

            if (item == null || attempts > 2) { // case that a pin doesnt exists or exits but we are in lock mode
                attempts++;
                if (attempts >= 3) {
                    operation.writeLog(log("Unknown tenant", "pin too many attempts exception", "Too many wrong pin insertions"),"too-many-exception");
                    throw new TooManyAttemptsException("Too many wrong pin insertions");
                } else {
                    operation.writeLog(log("Unknown tenant", "pin invalid pin exception", "Inserted pin is incorrect"),"invalid-pin-exception");
                    throw new InvalidPinException("Inserted pin is incorrect");
                }
            } else { // case that the pin exits
                if (door.getStatus() == DoorStatus.CLOSE) {
                    door.unlockDoor();
                } else {
                    door.lockDoor();
                }
                operation.writeLog(log(item.getKey().getName(), "successful pin insertion", null),"successful-insertion");
                return door.getStatus();
            }
        }
    }

    @Override
    public void addTenant(String pin, String name) throws TenantAlreadyExistsException, IOException {
        if (validAccess.containsKey(new Tenant(name))) {
            operation.writeLog(log(name, "add tenant exception", "This tenant already exist"),"add-tenant");
            log(name, "add tenant exception", "This tenant already exist");
            throw new TenantAlreadyExistsException("This tenant already exist");
        } else {
            operation.writeLog(log(name, "add tenant", null),"add-tenant-exception");
            validAccess.put(new Tenant(name), new AccessKey(pin));
        }
    }

    @Override
    public void removeTenant(String name) throws TenantNotFoundException, IOException {
        if (validAccess.containsKey(new Tenant(name))) {
            validAccess.remove(new Tenant(name)); // or first we get him and then delete, but we have equals and this will work
            operation.writeLog(log(name, "remove tenant", null), "remove-tenant");
        } else {
            operation.writeLog(log(name, "remove tenant exception", "Tenant doesn't exist"), "remove-tenant-exception");
            throw new TenantNotFoundException("Tenant doesn't exist");
        }
    }

    public AccessLog log(String tenantName, String operation, String errorMessage) {
        AccessLog accessLog = new AccessLog(tenantName, LocalDateTime.now(), operation, door.getStatus(), errorMessage);
        accessLogs.add(accessLog);
        return accessLog;
    }

    public List<AccessLog> getAccessLogs() {
        return accessLogs;
    }
}
