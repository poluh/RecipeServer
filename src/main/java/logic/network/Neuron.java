package logic.network;

class Neuron {

    private double weight = 0;
    private int input;

    Neuron() {}

    Neuron(double weight) {
        this.weight = weight;
    }

    double getWeight() {
        return weight;
    }

    void setInput(int input) {
        this.input = input;
    }

    double getOutput() {
        return input * weight;
    }
}
