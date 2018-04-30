package logic.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class Layer {
    private List<Neuron> neurons;

    Layer(int amount, int[] values) {
        List<Neuron> neurons = new ArrayList<>();
        for (int i = 0; i < amount; ++i) {
            Neuron neuron = new Neuron();
            neuron.setInput(values[i]);
        }
        this.neurons = neurons;
    }

    public Layer(int amount) {
        List<Neuron> neurons = new ArrayList<>();
        for (int i = 0; i < amount; ++i) {
            Neuron neuron = new Neuron();
            neurons.add(neuron);
        }
        this.neurons = neurons;
    }

    public Layer(List<Neuron> neurons) {
        this.neurons = neurons;
    }

    public void addAllSignals(int[] signals) {
        for (int i = 0; i < Network.NUMBER_OF_NEURON; i++) {
            neurons.get(i).setInput(signals[i]);
        }
    }

    public String getAllWeight() {
        StringBuilder answer = new StringBuilder();
        for (Neuron neuron : this.neurons) {
            answer.append("    ").append(neuron.getWeight()).append("\n");
        }
        return answer.toString();
    }

    public double getResult() {
        return 1 / (1 + Math.exp(-2 * neurons.stream().mapToDouble(Neuron::getOutput).sum()));
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Layer layer = (Layer) o;
        return Objects.equals(neurons, layer.neurons);
    }

    @Override
    public int hashCode() {

        return Objects.hash(neurons);
    }
}
