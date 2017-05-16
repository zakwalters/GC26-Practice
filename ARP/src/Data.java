/**
 * Created by pete on 15/05/17.
 */
public class Data {
    private Data(){};

    /**
     * Generate XOR data set.
     * @param numberOfPatterns
     * @return Array of patterns where first two elements are input and last element is desired output.
     */
    public static int[][] getXORData(int numberOfPatterns){

        int patterns[][] = new int[numberOfPatterns][3];

        for (int p = 0; p < numberOfPatterns; p++) {
            for (int i = 0; i < 2; i++) {
                patterns[p][i] = Math.random() > 0.5 ? 1 : 0;
            }
            patterns[p][2] = (patterns[p][0] + patterns[p][1] == 1) ? 1 : 0;
        }

        return patterns;

    }
}
