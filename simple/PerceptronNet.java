package simple;

import java.util.Scanner;

public class PerceptronNet {

	public static void main(String[] args) {
	
		double eta = 0.01;	//training rate
		double[] y = new double[2];
		double[] t = new double[2];
		double[] x = new double[6];
		double[][] w = new double[2][6];

		//number of training epochs - will be counted by the program
		int nEpochs = 0;
		
		//set biases (x_0) to 1
		x[0] = 1;
		
		//initialise weights to small random numbers
		for (int i = 0; i < w.length; i++) {
			for (int j = 0; j < w[i].length; j++) {
				w[i][j] = Math.random() * 0.4 - 0.2;
			}
		}
		
		int[][] data = Data.getDataSet();

		//train
		while (true) {

			double aveError = 0;

			//load each pattern into the net
			for (int p = 0; p < data.length; p++) {

				double currentError = 0;

				//load pattern x_p and note desired outputs t_ip
				for (int i = 1; i < x.length; i++) {
					x[i] = data[p][i - 1];
				}
				for (int i = 0; i < t.length; i++) {
					t[i] = data[p][i + x.length - 1];
				}

				//calculate node outputs for this pattern
				for (int i = 0, total = 0; i < y.length; i++) {
					for (int j = 0; j < x.length; j++) {
						total += w[i][j] * x[j];
					}
					y[i] = total > 0 ? 1 : 0;
					total = 0;
				}

				//adapt weights
				for (int i = 0; i < w.length; i++) {
					for (int j = 0; j < w[i].length; j++) {
						//System.out.println(t[i] - y[i]);
						currentError += Math.abs(t[i] - y[i]);
						w[i][j] = w[i][j] + eta * (t[i] - y[i]) * x[j];
					}
				}

				aveError += 0.5 * currentError / (w.length * w.length);

			}

			aveError /= data.length;

            //print error graph
			int length = 100;
			int ae = (int) (aveError * length);
			String out = ":";
			for (int i = 0; i < length; i++) {
				if (ae > i) out += "+";
				else if (ae == i) out += "|";
				else out += "-";
			}
			out += ":";
			System.out.println(out + "\t (" + aveError + ")");

			nEpochs++;
			if (aveError == 0) break;
		}
		
		//training finished - get user input
		System.out.println("Training finished in " + nEpochs + " epochs! Enter pattern (five 1 or 0s - one at a time): ");
		Scanner scanner = new Scanner(System.in);
		
		while (true) {
			for (int i = 0; i < x.length - 1; i++) {
				x[i + 1] = scanner.nextInt();
			}
			
			//check output
			for (int i = 0, total = 0; i < y.length; i++) {
				for (int j = 0; j < x.length; j++) {
					total += w[i][j] * x[j];
				}
				y[i] = total > 0 ? 1 : 0;
				total = 0;
			}

			if (y[0] > y[1]) {
				System.out.println("More 1s than 0s");
			} else if (y[1] > y[0]) {
				System.out.println("More 0s than 1s");
			} else {
				System.out.println("Something went wrong :/");
			}
		}
		
	}
	
}