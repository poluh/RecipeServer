package logic.network;

import logic.image.ImagePreprocessor;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Network {

    private static List<Double> neuronsWeight = new ArrayList<>();
    private Layer[] layers= new Layer[10];
    private Thread[] threads = new Thread[10];
    private Map<Double, Integer> allResult = new HashMap<>();

    public final static int NUMBER_OF_NEURON = 2500;
    private final static int IMAGE_SIZE = 50;
    private final static String pathToWeights = "/weights/NeuronsWeight.txt";

    private void deployNetwork() {
        try {

            var inputStream = getClass().getResourceAsStream(pathToWeights);
            var inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            var neuronsWeightInFile = new BufferedReader(inputStreamReader).lines();

            neuronsWeightInFile
                    .filter(line -> line.length() != 1 && !line.isEmpty())
                    .forEach(line -> neuronsWeight.add(Double.valueOf(line.replaceAll("\\s+", ""))));


            for (int i = 0; i < 10; ++i) {
                int finalI = i;
                threads[i] = new Thread(() -> addWeights(finalI));
                threads[i].start();
            }
        } catch (IOException e) {
            System.err.println("Something went wrong " + e.getLocalizedMessage());
        }
    }

    private void addWeights(int indexLayer) {
        List<Neuron> neurons = new ArrayList<>();
        for (int j = 0; j < NUMBER_OF_NEURON; ++j) {
            neurons.add(new Neuron(neuronsWeight.get(indexLayer * NUMBER_OF_NEURON + j)));
        }
        layers[indexLayer] = new Layer(neurons);
    }

    private void detecting(int[] values) {

        for (int i = 0; i < layers.length; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> layerDetecting(values, finalI));
            threads[i].start();
        }
        if (allResult.isEmpty()) allResult.put(-1.0, -1);
    }

    private void layerDetecting(int[] values, int layerIndex) {
        Layer layer = layers[layerIndex];
        layer.addAllSignals(values);
        if (layer.getResult() > 0.47) {
            allResult.put(layer.getResult(), layerIndex);
        }
    }

    public Network(BufferedImage image) {
        deployNetwork();
        waitThreads();
        var imagePreprocessor = new ImagePreprocessor(image);
        detecting(imagePreprocessor.getImageSignals());
        waitThreads();
    }

    private void waitThreads() {
        for (var thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<Double, Integer> getAllResult() {
        return allResult;
    }

    public int getMostLikelyResult() {
        var maxLikely = -1.0;
        for (var entry : allResult.entrySet()) {
            maxLikely = Math.max(entry.getKey(), maxLikely);
        }
        return allResult.get(maxLikely);
    }
}
