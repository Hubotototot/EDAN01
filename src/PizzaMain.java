import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

import java.util.ArrayList;

public class PizzaMain {

	public static void main(String[] args) {
		long T1, T2, T;

		T1 = System.currentTimeMillis();
		choose(1);
		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Execution time = " + T + " ms");
	}

	private static void execute(int n, int[] price, int m, int[] buy, int[] free) {
		Store store = new Store();
		bubbleSort(price);
		IntVar[][] matrix = new IntVar[n][m * 2 + 1];

		// Fyll matrisen med intvars som kan anta antingen 0 eller 1
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < 2 * m + 1; j++) {
				matrix[i][j] = new IntVar(store, 0, 1);
			}
		}

		// En etta per rad = alla pizzor antingen köpta med voucher, utan
		// voucher eller gratis
		for (int i = 0; i < n; i++) {
			store.impose(new SumInt(store, matrix[i], "==", new IntVar(store, 1, 1)));
		}

		// Kolla så att inte fler ettor i samma kolumn än det får vara (med
		// buy[i] och free[i])
		IntVar[] temp = new IntVar[n];
		for (int i = 0; i < 2 * m; i += 2) {
			for (int j = 0; j < n; j++) {
				temp[j] = matrix[j][i];
			}
			store.impose(new SumInt(store, temp, "<=", new IntVar(store, buy[i / m], buy[i / m])));
			store.impose(new SumInt(store, temp, "<=", new IntVar(store, free[(i + 1) / m], free[(i + 1) / m])));

			PrimitiveConstraint c1 = new SumInt(store, temp, ">=", new IntVar(store, buy[i % m], buy[i % m]));
			PrimitiveConstraint c2 = new SumInt(store, temp, "<=", new IntVar(store, free[i % m], free[i % m]));
			PrimitiveConstraint c3 = new SumInt(store, temp, "==", new IntVar(store, 0, 0));
			store.impose((new IfThenElse(c1, c2, c3)));

		}

		// Pizza som tas gratis får inte vara dyrare än den billigaste som
		// köpts.
		for (int i = 0; i < n-1; i++) {
			for (int j = 0; j < m; j++) {
				for (int k = j - 1; k >= 0; k--) {
					PrimitiveConstraint c1 = new XeqC(matrix[i][j], 1);
					PrimitiveConstraint c2 = new XeqC(matrix[i+1][k], 1);
					store.impose(new Not(new And(c1, c2)));
				}
			}
		}

		// Få ut sista kolumnen (alla pizzor som är betalda)
		IntVar[] paidPizzas = new IntVar[n * (2 * m + 1)];
		int index = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < 2 * m + 1; j++) {
				paidPizzas[index] = matrix[i][j];
				index++;
			}
		}
		IntVar[][] pajas = new IntVar[n][m+1];
		//store.impose(new SumInt());
		for (int i = 0; i < n; i++) {
			for(int j = 0; j < m+1; j+=2){				
					pajas[i][j] = matrix[i][j];
			}
			
		}
		IntVar[] pajas2 = new IntVar[n];
		for(int i = 0; i<n;i++){
				pajas2[i] = new Sum(pajas[i], new IntVar(store,0,1)).sum;
		}
		
		

		IntVar cost = new IntVar(store, "Cost ", 0, summa(price));
		store.impose(new SumWeight(pajas2, price, cost));
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(paidPizzas, null, new IndomainMin<IntVar>());

		boolean result = search.labeling(store, select, cost);

		if (result) {
			System.out.println("Solution : " + java.util.Arrays.asList(paidPizzas));
			System.out.println("Paid pizzas vector:");
			printVector(paidPizzas);
			// System.out.println("Free pizzas vector:");
			// printVector(freePizzas);
			System.out.println("Matrix:");
			printMatrix(matrix);
			// System.out.println("Voucher free matrix:");
			// printMatrix(voucherFree);
		} else {
			System.out.println("No solution found.");
		}
		// System.out.println(store);

	}

	// Sorterar prisvektorn i fallande ordning (störst först)
	public static void bubbleSort(int[] array) {

		int o = array.length;
		int temp = 0;

		for (int i = 0; i < o; i++) {
			for (int j = 1; j < (o - i); j++) {

				if (array[j - 1] < array[j]) {
					temp = array[j - 1];
					array[j - 1] = array[j];
					array[j] = temp;
				}

			}
		}
	}

	// Välj delproblem i main
	public static void choose(int ex) {

		if (ex == 1) {
			int n = 4;
			int[] price = { 10, 5, 20, 15 };
			int m = 2;
			int[] buy = { 1, 2 };
			int[] free = { 1, 1 };
			execute(n, price, m, buy, free);
		} else if (ex == 2) {
			int n2 = 4;
			int[] price2 = { 10, 15, 20, 15 };
			int m2 = 2;
			int[] buy2 = { 1, 2, 2, 8, 3, 1, 4 };
			int[] free2 = { 1, 1, 2, 9, 1, 0, 1 };
			execute(n2, price2, m2, buy2, free2);
		} else if (ex == 3) {
			int n3 = 10;
			int[] price3 = { 70, 10, 60, 60, 30, 100, 60, 40, 60, 20 };
			int m3 = 4;
			int[] buy3 = { 1, 2, 1, 1 };
			int[] free3 = { 1, 1, 1, 0 };
			execute(n3, price3, m3, buy3, free3);
		}

	}

	private static void printMatrix(IntVar[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j].value() + " ");
			}
			System.out.print("\n");
		}
	}

	private static void printVector(IntVar[] vector) {
		for (int i = 0; i < vector.length; i++) {
			System.out.print(vector[i].value() + " ");
		}
		System.out.print("\n");
	}

	public static int summa(int[] array) {
		int sum = 0;
		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

}
