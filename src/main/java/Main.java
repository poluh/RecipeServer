import logic.json.JSON;
import logic.network.Network;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    private static String resultFileName = "networkResult.json";

    public static void main(String[] args) {
        var pathToImage = "/Users/sergey/Desktop/imageForEducation/3/8.png";
        try {
            var image = ImageIO.read(new File(pathToImage));
            var network = new Network(image);

            new JSON(network.getAllResult(), network.getMostLikelyResult()).saveJSON(resultFileName);

        } catch (IOException e) {
            System.err.println("Error read image of path = " + pathToImage + " " + e.getLocalizedMessage());
        }
    }

}
