import java.io.*;
import java.util.Arrays;

public class ARPNet {

    private final int layerSize[];
    private double x[][]; // contexts
    //private double k[]; // probability of context
    private double t[]; // target for each context
    private double a[][]; // activations
    private double y[][]; // outputs
    private double d[][]; // errors
    private double w[][][]; // weights

    private final double initialTrainRate = 0.1;
    private double trainRate = initialTrainRate;
    private double penalty = 0.05;

    ARPNet (int[] lSize) {
        layerSize = lSize;

        a = new double[layerSize.length][];
        y = new double[layerSize.length][];
        d = new double[layerSize.length][];
        w = new double[layerSize.length][][];

        y[0] = new double[layerSize[0]];

        for(int layer = 1; layer < layerSize.length; layer++) {
            a[layer] = new double[layerSize[layer]];
            y[layer] = new double[layerSize[layer]];
            d[layer] = new double[layerSize[layer]];
            w[layer] = new double[layerSize[layer]][];

            for (int node = 0; node < layerSize[layer]; node++) {
                w[layer][node] = new double[layerSize[layer - 1]];
            }
        }
    }

    private void updateTrainRate(int t) {
        trainRate = initialTrainRate / (Math.pow(t + 1, 0.55));
    }

    private double smallRandom() {
        return (Math.random() - 0.5) / 100;
    }

    private double output(double probA) {
        //System.out.println("probA: " + probA);
//        return probA > Math.random() ? 1 : 0;
        return probA > 0.5 ? 1 : 0;
    }

    private double probOutput(double a) {
        return 1 / (1 + Math.pow(Math.E, -a));
    }

    private double activation(int layer, int node) throws Exception {
        if (layer < 1) throw new Exception("Cannot calculate activation of input layer");

        double sum = 0;
        for (int i = 0; i < w[layer][node].length; i++) {
            sum += w[layer][node][i] * y[layer - 1][i];
        }
        return sum;
    }

    private double getReward(int context) {
        double trueReward = y[layerSize.length - 1][0] == t[context] ? 1 : 0;
        double pReward = 0.9 > Math.random() ? trueReward : 1 - trueReward;
        return pReward;
    }

    private void setDelta(int layer, int node, double r) {
        double out = output(probOutput(a[layer][node]));
        double fa = probOutput(a[layer][node]);

        d[layer][node] = (out - fa) * r + penalty * (1 - out - fa) * (1 - r);
    }

    private void updateWeight(int layer, int node, int weight) {
        w[layer][node][weight] += trainRate * d[layer][node] * y[layer - 1][weight]; // y or x here?
    }

    public void setContexts(double[][] x, double[] t) throws Exception {
        if (x.length != t.length)
            throw new Exception("Must have matching contexts," +
                    "probabilities and targets.");

        if (x[0].length != y[0].length)
            throw new Exception("Input vector size does not match input layer size.");

        this.x = x;
        this.t = t;
    }

    private void initialiseWeights() {
        for (int layer = 1; layer < layerSize.length; layer++) {
            for (int node = 0; node < layerSize[layer]; node++) {
                for (int weight = 0; weight < layerSize[layer - 1]; weight++) {
                    w[layer][node][weight] = smallRandom();
                }
            }
        }
    }

    public void train() throws Exception {

        // set up file for outputs
        File file = new File("log.csv");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

        // stats
        double maxMean = 0;
        double vals[] = new double[100];
        int clock = 0;
        int maxC = 0;

        initialiseWeights();

        // train on every context once
        for (int context = 0; context < x.length; context++) {
            // load input
            y[0] = x[context];

            // set train rate
            updateTrainRate(context);
            System.out.println("i: " + context + ", trainRate: " + trainRate);

            // forward pass
            for (int layer = 1; layer < layerSize.length; layer++) {
                for (int node = 0; node < layerSize[layer]; node++) {
                    a[layer][node] = activation(layer, node);
//                    System.out.println("[" + layer + "][" + node + "]:" + probOutput(a[layer][node]));
                    y[layer][node] = output(probOutput(a[layer][node]));
                }
            }

            double r = getReward(context);

            // update weights
            for (int layer = 1; layer < layerSize.length; layer++) {
                for (int node = 0; node < layerSize[layer]; node++) {
                    setDelta(layer, node, r);
                    for (int weight = 0; weight < layerSize[layer - 1]; weight++) {
                        updateWeight(layer, node, weight);
                    }
                }
            }

            // update mean
            vals[clock] = r;
            double sum = 0;

            for (int i = 0; i < vals.length; i++)
                sum += vals[i];

            double ave = sum / vals.length;
            clock = (clock + 1) % vals.length;

            maxC = ave > maxMean ? context : maxC;
            maxMean = ave > maxMean ? ave : maxMean;


//            System.out.println(
//                    "i: " + context +
//                    ", in: " + Arrays.toString(y[0]) + ", out: " + y[layerSize.length - 1][0]
//                            + " target: " + t[context] + ", reward: "
//                            + r + ", mean: " + ave
//                            + ", max: " + maxMean + " (i = " + maxC + ")");
            writer.write(ave + ",");

        }

        writer.write(";\n");
        writer.close();
    }

    public static void main(String[] args) throws Exception{
        int l[] = {2,2,1};
        for (int s = 0; s < 100; s++) {
            System.out.println(s);
            int data[][] = Data.getXORData(10000);
            double x[][] = new double[data.length][2];
            double t[] = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                x[i][0] = data[i][0];
                x[i][1] = data[i][1];
                t[i] = data[i][2];
            }
            ARPNet m = new ARPNet(l);
            m.setContexts(x, t);
            m.train();
        }

    }
}
