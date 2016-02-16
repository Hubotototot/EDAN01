package lab4;

//https://github.com/Contemptio/EDAN01/blob/master/Laborations/3-AutoRegressionFilter/autoRegressionFilter.mzn
//https://github.com/RobertBorg/EDAN01/blob/master/src/minizink/AutoRegressionFilter.mzn
//https://github.com/eliasbrange/edan01/blob/master/arf.mzn elias brange hax

public class OperationScheduling {

	public static void main(String[] args) {
        long T1, T2, T;
        T1 = System.currentTimeMillis();
        example(3);
        T2 = System.currentTimeMillis();
        T = T2 - T1;
        System.out.println("\n\t*** Execution time = " + T + " ms");
    }
	
	private static void solve(int n, int m, int[] mulOp, int[] addOp, int addCycle, int mulCycle){
		
		
		
		
	}
	
	
	public static void example(int ex){	
		int n = 0; //adders
		int m = 0; //multipliers
		int[] mulOp = {0,1,2,3,4,5,6,7,14,15,16,17,20,21,22,23};
		int[] addOp = {8,9,10,11,12,13,18,19,24,25,26,27};
		int addCycle = 1;
		int mulCycle = 2;
        switch(ex) {
            case 1:
                n = 1;
                m = 1;
                solve(n,m,mulOp,addOp, addCycle,mulCycle);
                break;
            case 2:
                n = 1;
                m = 2;
                solve(n,m,mulOp,addOp,addCycle,mulCycle);
                break;
            case 3:
                n = 1;
                m = 3;
                solve(n,m,mulOp,addOp,addCycle,mulCycle);
                break;
            case 4:
                n = 2;
                m = 2;
                solve(n,m,mulOp,addOp,addCycle,mulCycle);
                break;
            case 5:
                n = 2;
                m = 3;
                solve(n,m,mulOp,addOp,addCycle,mulCycle);
                break;
            case 6:
                n = 2;
                m = 4;
                solve(n,m,mulOp,addOp,addCycle,mulCycle);
                break;
        }
    }
}
