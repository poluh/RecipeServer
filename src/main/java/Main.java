import logic.image.ImagePreprocessor;
import logic.json.JSON;
import logic.network.Network;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    private static final String resultFileName = "networkResult.json";
    private static final String pathToImage = "/home/sergey/image.png";
    private static final String userDirectory = new File("").getAbsolutePath() + "/";

    public static void main(String[] args) {
        System.out.println("Network started");
        try {
            var image = ImageIO.read(new File(pathToImage));
            var network = new Network(image);
            ImagePreprocessor imagePreprocessor = new ImagePreprocessor(image);
            imagePreprocessor.saveImage(imagePreprocessor.getImage(), "Image.jpg");
            new JSON(network.getAllResult(), network.getMostLikelyResult()).saveJSON(resultFileName);

            System.out.println("Most likely result = " + network.getMostLikelyResult());
            System.out.println("Look all results on " +  userDirectory + resultFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
