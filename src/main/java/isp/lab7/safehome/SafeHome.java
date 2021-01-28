package isp.lab7.safehome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SafeHome {

    public static void main(String[] args) {
        Map<Tenant, AccessKey> map = new HashMap<>();
        ArrayList<AccessLog> accessLogs = new ArrayList<>();
        DoorLockController ct = new DoorLockController(map, accessLogs, new Door(DoorStatus.CLOSE));
        map.put(new Tenant("Zuk"), new AccessKey("1111"));

        try {
            ct.enterPin("1111");
        } catch (InvalidPinException | TooManyAttemptsException e) {
            e.printStackTrace();
        }

        try {
            ct.enterPin("33");
        } catch (InvalidPinException | TooManyAttemptsException e) {
            e.printStackTrace();
        }

        try {
            ct.enterPin("33");
        } catch (InvalidPinException | TooManyAttemptsException e) {
            e.printStackTrace();
        }

        try {
            ct.enterPin("33");
        } catch (InvalidPinException | TooManyAttemptsException e) {
            e.printStackTrace();
        }

        try {
            ct.enterPin("33");
        } catch (InvalidPinException | TooManyAttemptsException e) {
            e.printStackTrace();
        }

        try {
            ct.enterPin("1111");
        } catch (InvalidPinException | TooManyAttemptsException e) {
            e.printStackTrace();
        }

        try {
            ct.enterPin(ControllerInterface.MASTER_KEY) ;
            } catch (TooManyAttemptsException | InvalidPinException e) {
            e.printStackTrace();
        }

        try {
            ct.enterPin("1111");
        } catch (TooManyAttemptsException | InvalidPinException e) {
            e.printStackTrace();
        }

        try {
            ct.addTenant("1212", "potato");
        } catch (TenantAlreadyExistsException e) {
            e.printStackTrace();
        }

        try {
            ct.addTenant("1212", "potato");
        } catch (TenantAlreadyExistsException e) {
            e.printStackTrace();
        }

        try {
            ct.removeTenant("tomato");
        } catch (TenantNotFoundException e) {
            e.printStackTrace();
        }

        try {
            ct.removeTenant("potato");
        } catch (TenantNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(accessLogs);

    }
}
