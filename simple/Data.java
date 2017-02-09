package simple;

import java.util.Arrays;

public class Data {
	static int[][] getDataSet() {
		
		//data[pattern][first 5 - input, last 2 - output]
		int[][] data = new int[100][7];
		int count1 = 0, count0 = 0;
		
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < 5; j++) {
				data[i][j] = Math.random() > 0.5 ? 1 : 0;
				if (data[i][j] == 1) {
					count1++;
				} else {
					count0++;
				}
			}
			if (count1 > count0) {
				data[i][5] = 1;
			} else {
				data[i][6] = 1;
			}
			count1 = count0 = 0;
		}
		
		return data;
	}
	
	public static void printString(int[][] data) {
		
		for (int[] row : data) {
			System.out.println(Arrays.toString(row));
		}
	}
	
	public static void printString(double[][] data) {
		
		for (double[] row : data) {
			System.out.println(Arrays.toString(row));
		}
	}
}
