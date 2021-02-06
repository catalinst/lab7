package isp.lab7.safehome;

import java.io.*;
import java.util.ArrayList;

public class Operations {
    private final ArrayList<AccessLog> logs = new ArrayList<>();

    public void writeLog(AccessLog accessLog, String file) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("accesslog-" + file + ".dat"));
        out.writeObject(accessLog);
        System.out.println("Writing: " + accessLog);
    }

    // 8 cases: remove-tenant, remove-tenant-exception
    public void readLogs() {
        String[] cases = {"remove-tenant", "remove-tenant-exception", "add-tenant", "add-tenant-exception", "master-tenant",
                "too-many-exception", "invalid-pin-exception", "successful-insertion"};
        for (String aCase : cases) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream("accesslog-" + aCase + ".dat"));
                while (true) {
                    try {
                        AccessLog obj = (AccessLog)in.readObject();
                        System.out.println(obj);
                        logs.add(obj);
                    } catch (Exception ex) {
                        System.err.println("end of reader file ");
                        break;
                    }
                }
            } catch (Exception ex) {
                System.err.println("failed to read the file");
            }

        }

    }

    public void displayLogs() {
        System.out.println(logs);
    }
}
