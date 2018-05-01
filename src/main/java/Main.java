import logic.network.Network;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        var pathToImage = args[0];
        try {
            var image = ImageIO.read(new File(pathToImage));
            new FileWriter(new File("networkResult.txt")).write((int) new Network(image).getResult());
        } catch (IOException e) {
            System.err.println("Error read image of path = " + pathToImage);
        }
    }

}
