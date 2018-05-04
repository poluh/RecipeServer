import logic.json.JSON;
import logic.network.Network;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class Main {

    private static String pathToImage;
    private static final String resultFileName = "networkResult.json";
    private static final String userDirectory = new File("").getAbsolutePath() + "/";

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 0 && !args[0].isEmpty()) {
            pathToImage = userDirectory + args[0];
        } else {
            System.err.println("Without args");
            return;
        }
        System.out.println("Network started");
        try {
            var image = ImageIO.read(new File(pathToImage));
            var network = new Network(image);

            var delFilesThread = new Thread(Main::deleteAllFilesFolder);
            delFilesThread.start();

            System.out.println("Most likely result = " + network.getMostLikelyResult());
            var createJSONThread = new Thread(() -> {
                JSON json = new JSON(network.getAllResult(), network.getMostLikelyResult());
                System.out.println("All results = " + json.toString());
            });
            createJSONThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteAllFilesFolder() {
        var pattern = Pattern.compile("(" + userDirectory + ")?.+\\.png");
        for (var file : Objects.requireNonNull(new File(userDirectory).listFiles())) {
            if (file.isFile() && pattern.matcher(file.getName()).matches()) {
                file.delete();
            }
        }
    }

}
