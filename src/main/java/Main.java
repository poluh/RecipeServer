import logic.network.Network;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        var pathToImage = "/Users/sergey/Desktop/imageForEducation/3/8.png";
        try {
            var image = ImageIO.read(new File(pathToImage));
            var network = new Network(image);
            var networkResult = network.getAllResult();
            var writer = new FileWriter(new File("networkResult.txt"));
            for (var entry : networkResult.entrySet()) {
                writer.write(entry.getValue() + " с вероятностью " + entry.getKey() * 100 + "\n");
            }
            writer.write("Максимально вероятный результат = " + network.getMostLikelyResult());
            writer.close();
        } catch (IOException e) {
            System.err.println("Error read image of path = " + pathToImage + " " + e.getLocalizedMessage());
        }
    }

}
