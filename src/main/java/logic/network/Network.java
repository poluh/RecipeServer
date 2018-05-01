package logic.network;

import logic.image.ImagePreprocessor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Network {

    private static List<Double> neuronsWeight = new ArrayList<>();
    private List<Layer> layers = new ArrayList<>();
    private double result;

    public static int NUMBER_OF_NEURON = 2500;
    public static int IMAGE_SIZE = 50;

    private void deployNetwork() {
        try {
            var neuronsWeightInFile =
                    Files.readAllLines(Paths.get("weights/NeuronsWeight.txt"));

            neuronsWeightInFile
                    .stream()
                    .filter(line -> line.length() != 1 && !line.isEmpty())
                    .forEach(line -> neuronsWeight.add(Double.valueOf(line.replaceAll("\\s+", ""))));
            for (int i = 0; i < 10; ++i) {
                List<Neuron> neurons = new ArrayList<>();
                for (int j = 0; j < NUMBER_OF_NEURON; ++j) {
                    neurons.add(new Neuron(neuronsWeight.get(i * NUMBER_OF_NEURON + j)));
                }
                layers.add(new Layer(neurons));
            }
        } catch (IOException e) {
            System.err.println("Something went wrong");
        }
    }

    private void detecting(int[] values) {
        for (var layer : layers) {
            layer.addAllSignals(values);
            if (layer.getResult() > 0.5) {
                this.result = layers.indexOf(layer);
                return;
            }
        }

    }

    public Network(BufferedImage image) {
        if (layers.isEmpty()) deployNetwork();
        var imagePreprocessor = new ImagePreprocessor(image);
        imagePreprocessor.cropImage();
        imagePreprocessor.resize(imagePreprocessor.getImage(), IMAGE_SIZE, IMAGE_SIZE);
        detecting(imagePreprocessor.getImageSignals());
    }

    public double getResult() {
        return result;
    }
}
