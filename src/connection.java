import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class connection {
    String usernameHost;
    String password;
    String keyFile;
    boolean useKey;
    int port;

    Stage stage;

    public connection(int hostPort, Stage stage) {
        this.port = hostPort;
        this.stage = stage;
        settingsFile();
    }

    public Process connect(boolean guest) throws IOException {
        Runtime.getRuntime().exec("cmd /K echo y | plink.exe " + usernameHost + " \"exit\"");
        String commend;
        if (guest)
            commend = "plink -batch " + usernameHost + (!useKey ? " -i " + keyFile : " -pw " + password) +
                    " -P 22 -2 -4 -T -N -C -R 0.0.0.0:" + port + ":localhost:3389";
        else
            commend = "plink -batch " + usernameHost + (!useKey ? " -i " + keyFile : " -pw " + password) +
                    " -P 22 -2 -4 -T -N -C -L 3388:localhost:" + port;
        System.out.println(commend);
        return Runtime.getRuntime().exec(commend);
    }

    private void settingsFile() {
        File settingsFile = new File("settings");
        try {
            Scanner scanner = new Scanner(settingsFile);
            usernameHost = scanner.nextLine();
            useKey = scanner.nextBoolean();
            scanner.nextLine();
            if (!useKey)
                keyFile = scanner.nextLine();
            else
                password = scanner.nextLine();
            scanner.close();
        } catch (Exception e) {
        }

    }

}
