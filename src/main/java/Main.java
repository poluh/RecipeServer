import logic.json.JSON;
import logic.network.Network;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {

    private static final String resultFileName = "networkResult.json";
    private static final String userDirectory = new File("").getAbsolutePath() + "/";

    public static void main(String[] args) {
        var pathToImage = args[0];
        System.out.println("Network started");
        try {
            var image = ImageIO.read(new File(pathToImage));
            var network = new Network(image);
            new JSON(network.getAllResult(), network.getMostLikelyResult()).saveJSON(resultFileName);

            System.out.println("Most likely result = " + network.getMostLikelyResult());
            System.out.println("Look all results on " +  userDirectory + resultFileName);
        } catch (IOException e) {
            System.err.println("Error read image of path = " + pathToImage + " " + e.getLocalizedMessage());
        }
    }

}
