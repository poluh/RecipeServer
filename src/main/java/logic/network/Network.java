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

    private void deployNetwork() {
        try {
            List<String> neuronsWeightInFile;
            Pattern pattern = Pattern.compile("\\d");
            neuronsWeightInFile =
                    Files.readAllLines(Paths.get("src/com/main/java/logic/network/NeuronsWeight.txt"));

            neuronsWeightInFile.forEach(string -> {
                if (!pattern.matcher(string).matches() && !string.isEmpty()) {
                    neuronsWeight.add(Double.valueOf(string.replace("    ", "")));
                }
            });
            for (int i = 0; i < 10; ++i) {
                List<Neuron> neurons = new ArrayList<>();
                for (int j = 0; j < NUMBER_OF_NEURON; ++j) {
                    neurons.add(new Neuron(neuronsWeight.get(i * NUMBER_OF_NEURON + j)));
                }
                layers.add(new Layer(neurons));
            }
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }

    private void detecting(int[] values) {
        for (Layer layer : layers) {
            layer.addAllSignals(values);
            if (layer.getResult() > 0.5) {
                this.result = layers.indexOf(layer);
                return;
            }
        }

    }

    public Network(BufferedImage image) {
        if (layers.isEmpty()) deployNetwork();
        ImagePreprocessor imagePreprocessor = new ImagePreprocessor(image);
        /*imagePreprocessor.cropImage();
        *//*imagePreprocessor.devidedIntoSeveralImages();
        System.out.println(imagePreprocessor.getOtherImagePreprocessors().size());
        for (ImagePreprocessor value : imagePreprocessor.getOtherImagePreprocessors()) {
            value.cropImage();
            imagePreprocessor.resize(imagePreprocessor.getImage(), 50, 50);
            imagePreprocessor.saveImage(imagePreprocessor.getImage(), imagePreprocessor.getImage().toString() + ".png");
            detecting(imagePreprocessor.getImageSignals());
        }*/
        imagePreprocessor.cropImage();
        imagePreprocessor.resize(imagePreprocessor.getImage(), 50, 50);
        detecting(imagePreprocessor.getImageSignals());
    }

    public double getResult() {
        return result;
    }
}
