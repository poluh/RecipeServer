package logic.network;

import java.util.List;
import java.util.Objects;

public class Layer {
    private List<Neuron> neurons;
    private boolean isEmpty = true;

    Layer(List<Neuron> neurons) {
        this.neurons = neurons;
    }

    void addAllSignals(int[] signals) {
        isEmpty = false;
        for (int i = 0; i < Network.NUMBER_OF_NEURON; i++) {
            neurons.get(i).setInput(signals[i]);
        }
    }

    double getResult() {
        return 1 / (1 + Math.exp(-2 * neurons.stream().mapToDouble(Neuron::getOutput).sum()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var layer = (Layer) o;
        return Objects.equals(neurons, layer.neurons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(neurons);
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
