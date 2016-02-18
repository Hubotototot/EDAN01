package lab5;

import java.util.ArrayList;



import org.jacop.constraints.Constraint;
import org.jacop.constraints.Sum;
import org.jacop.constraints.XplusYeqZ;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;
import org.jacop.search.SmallestDomain;
import org.jacop.constraints.Element;

//Gjordes i minizinc istället

public class UrbanPlanning {

	public static void main(String[] args) {
        long T1, T2, T;
        T1 = System.currentTimeMillis();
        example(2);
        T2 = System.currentTimeMillis();
        T = T2 - T1;
        System.out.println("\n\t*** Execution time = " + T + " ms");
    }
	
	private static void solve(int n, int n_commercial, int n_residential, int[] point_distribution){
		
		IntVar[][] urban = new IntVar[n][n];
		Store store = new Store();
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				urban[i][j] = new IntVar(store, 0, 1);
			}			
		}
		IntVar[] urban_flat = new IntVar[n*n];
		ArrayList<IntVar> urban_temp = new ArrayList<IntVar>();
		for(int i = 0; i<n; i++){
			for(int j = 0; j<n;j++){
				urban_temp.add(urban[i][j]);
			}
		}
		for(int i = 0; i<n*n; i++){
			urban_flat[i] = urban_temp.get(i);
		}
		
		//Residential är ettor och commercial är nollor
		IntVar residential = new IntVar(store, n_residential, n_residential);
		store.impose(new Sum(urban_flat, residential));
		
		//Skapa en matris som är 2*n * n stor. De n första raderna är raderna i urban och 
		//de resterande n raderna är kolumnerna i urban.
		
		IntVar[][] rowAndCols = new IntVar[2*n][n];
		
		//Hämta ut raderna ur urban och stoppa in i nya matrisen
		IntVar[] tempRow;
		for(int i = 0; i<n;i++){
			tempRow = urban[i];
			for(int j = 0; j<n; j++){
				rowAndCols[i][j] = tempRow[j];
			}
		}
		//Hämta ut kolumnerna i urban och stoppa in i nya matrisen
		IntVar[] tempCol;
		for(int i = n; i<2*n;i++){
			tempCol = getColumn(urban, i-n);
			for(int j = 0; j<n; j++){
				rowAndCols[i][j] = tempCol[j];
			}
		}
		
		 // Variables corresponding to the score of each row and column.
        // Indexes 0-4 corresponds to the rows and indexes 5-9 corresponds
        // to the columns.
        IntVar rowcolsums[] = new IntVar[10];
        for (int i = 0; i < 10; i++) {
            rowcolsums[i] = new IntVar(store, "sum" + i);
            rowcolsums[i].addDom(-5,-5);
            rowcolsums[i].addDom(-4,-4);
            rowcolsums[i].addDom(-3,-3);
            rowcolsums[i].addDom(3,3);
            rowcolsums[i].addDom(4,4);
            rowcolsums[i].addDom(5,5);
        }

        int score[] = {-5, -4, -3, 3, 4, 5};

        for (int i = 0; i < 10; i++) {
            IntVar sum = new IntVar(store, "sum"+i, 0, 5);
            Constraint ctr = new Sum(rowAndCols[i], sum);
            store.impose(ctr);
            store.impose(new Element(sum, score, rowcolsums[i], -1));
        }

        // Used to find max score. Minimizing negMaxScore will maximize maxScore
        IntVar maxScore = new IntVar(store, "maxScore", -50, 50);
        IntVar negMaxScore = new IntVar(store, "negMaxScore", -50, 50);
        IntVar zero = new IntVar(store, "zero", 0, 0);

        store.impose(new Sum(rowcolsums, maxScore));
        store.impose(new XplusYeqZ(maxScore, negMaxScore, zero));

        Search<IntVar> label = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(urban_flat,
                                                new SmallestDomain<IntVar>(),
                                                new IndomainMin<IntVar>());


        boolean Result = label.labeling(store, select, negMaxScore);

        if (Result) {
            System.out.println("\n*** Yes");
            System.out.println("Solution:");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (urban_flat[i*n + j].value() == 1) {
                        System.out.print("R ");
                    } else {
                        System.out.print("C ");
                    }
                }
                System.out.println();
            }

            System.out.println("The score for this solution is: " + maxScore.value());
        } else {
            System.out.println("\n*** No");
        }
		
		
		
	}
	
	public static void example(int ex){	
		int n = 0; //adders
		int n_commercial = 0;
		int n_residential = 0;
		
        switch(ex) {
            case 1:
                n = 5;
                n_commercial = 13;
                n_residential = 12;
                int[] point_distribution1 = {-5, -4, -3, 3, 4, 5};
                solve(n, n_commercial, n_residential, point_distribution1);
                break;
            case 2:
                n = 5;
                n_commercial = 7;
                n_residential = 18;
                int[] point_distribution2 = {-5, -4, -3, 3, 4, 5};
                solve(n, n_commercial, n_residential, point_distribution2);
                break;
            case 3:
                n = 7;
                n_commercial = 20;
                n_residential = 29;
                int[] point_distribution3 = {-7, -6, -5, -4, 4, 5, 6, 7};
                solve(n, n_commercial, n_residential, point_distribution3);
                break;
        }
    }
	
    private static IntVar[] getColumn(IntVar[][] matrix, int i) {
        IntVar[] col = new IntVar[matrix.length];
        for (int j = 0; j < matrix.length; j++) {
            col[j] = matrix[j][i];
        }
        return col;
    }

}
