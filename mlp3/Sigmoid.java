package mlp3;

/**
 * Created by pete on 05/02/17.
 */
public class Sigmoid {

    private Sigmoid(){};

    public static double f(double x, double k) {
        return 1 / (1 + Math.pow(Math.E, - (k * x)));
    }

}
