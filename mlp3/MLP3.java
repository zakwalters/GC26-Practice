package mlp3;

import java.util.Scanner;

/**
 * Created by pete on 11/02/17.
 */
public class MLP3 {

    // YOU CAN CHANGE THESE
    // training rate
    private double eta = 0.1;
    // maximum number of epochs
    private static int maxEpochs = 10000;
    // number of training patterns to generate
    private static int numPatterns = 100;
    // target maximum error
    private static double maxError = 0.005;

    // scale sigmoid output
    private final double k = 1;

    // DON'T CHANGE THESE
    private double w[][][] = new double[3][][];
    private double a[][] = new double[3][];
    private double o[][] = new double[3][];
    private double d[][] = new double[3][];

    private int[][] patterns;
    private int[] t;    //target outputs

    /**
     * Initialise a 3 layer net with an input layer, one hidden layer, and an output layer
     * @param numNeurons array representing number of neurons in each layer [inputs, hidden, output]
     * @param numPatterns number of patterns to get from the pattern generator
     */
    public MLP3 (int[] numNeurons, int numPatterns) {

        // initialise arrays for nodes to correct sizes
        for (int i = 0; i < 3; i++) {
            a[i] = new double[numNeurons[i] + 1];
            o[i] = new double[numNeurons[i] + 1];
            d[i] = new double[numNeurons[i] + 1];
            if (i > 0) {
                w[i] = new double[numNeurons[i] + 1][numNeurons[i - 1] + 1];
            }
        }

        // get data set
        patterns = Data.getXORData(numPatterns);
        t = new int[numPatterns];
        for (int i = 0; i < numPatterns; i++) {
            t[i] = patterns[i][numNeurons[0]];
        }
    }

    public static void main(String[] args) {

        // initialise network
        int numNeurons[] = {2, 2, 1};
        MLP3 mlp = new MLP3(numNeurons, numPatterns);

        mlp.initialiseWeights();
        mlp.setBiasNodes();

        // train
        for (int epoch = 0; epoch < maxEpochs; epoch++) {
            double energy = 0;
            for (int patternNo = 0; patternNo < mlp.patterns.length; patternNo++) {

                mlp.loadPattern(patternNo);

                mlp.forwardPass();

//                System.out.println("Target: " + mlp.t[patternNo]);

                // back propagation
                mlp.calcOutputLayerDeltas(patternNo);

                // calculate deltas for hidden layers
                mlp.calcHiddenLayerDeltas();

                // change weights
                mlp.adaptWeights();

                // calculate energy/error
                double diff = mlp.t[patternNo] - mlp.o[2][1];
                energy += 0.5 * Math.pow(diff, 2);
//                System.out.println(energy);
            }

            // average energy/error
            energy /= mlp.patterns.length;
            System.out.println(energy);
            // stop training if error is below maxError
            // otherwise continue to max Epochs
            if(energy < maxError) {
                System.out.println(
                        "Training finished in " + epoch + " epochs."
                );
                break;
            }
        }

        while (true) {
            System.out.println("Enter two bits (one at a time)");
            Scanner scanner = new Scanner(System.in);
            mlp.o[0][1] = scanner.nextInt();
            mlp.o[0][2] = scanner.nextInt();
            mlp.forwardPass();
            System.out.println("My answer is: " + mlp.o[2][1]);

        }

    }

    public void adaptWeights() {
        int oLayer = o.length - 1;
        for (int layer = oLayer; layer > 0; layer--) {
            for (int node = 1; node < w[layer].length; node++) {
                for (int weight = 0; weight < w[layer][node].length; weight++) {
                    w[layer][node][weight] += (eta * d[layer][node] * o[layer - 1][weight]);
//                    System.out.println("new weight: w["+layer+"]["+node+"]["+weight+"]: " + w[layer][node][weight]);
                }
            }
        }
    }

    public void calcHiddenLayerDeltas() {

        // per hidden layer
        for (int layer = o.length - 2; layer > 0; layer--) {
            // calculate deltas for hidden units
//            System.out.println(layer);
            int layerAbove = layer + 1;
            for (int node = 0; node < o[layer].length; node++) {
                double sum = 0;
                for (int nodeAbove = 1; nodeAbove < o[layerAbove].length; nodeAbove++) {
//                    System.out.println("d["+layerAbove+"]["+nodeAbove+"]: " + d[layerAbove][nodeAbove]);
//                    System.out.println("w["+layerAbove+"]["+nodeAbove+"]["+node+"]: " + w[layerAbove][nodeAbove][node]);
                    sum += (d[layerAbove][nodeAbove] * w[layerAbove][nodeAbove][node]);
                }
                d[layer][node] = k * o[layer][node] * (1 - o[layer][node]) * sum;
//                System.out.println("calc : d["+layer+"]["+node+"]: " + d[layer][node]);
            }

        }
    }

    public void calcOutputLayerDeltas(int patternNo) {
        // calculate deltas for output units
        int oLayer = o.length - 1;
        for (int i = 1; i < o[oLayer].length; i++){
//            System.out.println(o[oLayer][i]);
//            System.out.println(t[p]);
            d[oLayer][i] = k *
                    (1 - o[oLayer][i]) * o[oLayer][i] *
                    (t[patternNo] - o[oLayer][i]);
//            System.out.println("\nd[2][1]: " + d[oLayer][i]);
        }
    }

    /**
     * Assigns small random numbers either side of 0 to the weights
     */
    public void initialiseWeights() {
        for (int i = 1; i < w.length; i++) {
            for (int j = 1; j < w[i].length; j++) {
                for (int k = 0; k < w[i][j].length; k++) {
                    w[i][j][k] = Math.random() *  0.01 - 0.005;
                }
            }
        }
    }

    /**
     * Sets all bias nodes' outputs to 1
     */
    public void setBiasNodes() {
        for (int i = 0; i < o.length; i++) {
            o[i][0] = 1;
        }
    }

    /**
     * loads pattern to outputs of layer 0
     * @param patternNo
     */
    public void loadPattern(int patternNo) {
        for (int i = 1; i < o.length; i++) {
            o[0][i] = patterns[patternNo][i - 1];
        }
    }

    public void forwardPass() {
        for (int i = 1; i < 3; i++) {
            for (int j = 1; j < o[i].length; j++) {
                calcOA(i, j);
            }
        }
    }

    public void calcOA(int layer, int neuron) {
        double sum = 0;
        for (int i = 0; i < o[layer - 1].length; i++) {
            sum += (w[layer][neuron][i] * o[layer - 1][i]);
        }
        a[layer][neuron] = sum;
        o[layer][neuron] = Sigmoid.f(sum, k);
    }

    /**
     * print values of all forwardPass - raw weighted sums
     */
    public void printA() {
        System.out.println();
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                System.out.println("a[" + i + "][" + j + "]: " + a[i][j]);
            }
        }
    }

    /**
     * print values of all o - sigmoid function applied to weighted sum
     */
    public void printO() {
        System.out.println();
        for (int i = 0; i < o.length; i++) {
            for (int j = 0; j < o[i].length; j++) {
                System.out.println("o[" + i + "][" + j + "]: " + o[i][j]);
            }
        }
    }

    /**
     * print values of all weights
     */
    public void printW() {
        System.out.println();
        for (int i = 1; i < w.length; i++) {
            for (int j = 1; j < w[i].length; j++) {
                for (int k = 0; k < w[i][j].length; k++) {
                    System.out.println("w[" + i + "][" + j + "][" + k + "]: " + w[i][j][k]);
                }
            }
        }
    }

    /**
     * prints all patterns and their desired output
     */
    public void printPatterns() {
        System.out.println();
        for (int i = 0; i < patterns.length; i++) {
            System.out.println("Inputs: " + patterns[i][0] + patterns[i][1]
//            + " Desired output: " + patterns[i][2]);
            + " Desired output: " + t[i]);
        }
    }

}
