import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

import java.util.ArrayList;

public class Pizza {
    public static void main(String[] args) {
        long T1, T2, T;
        T1 = System.currentTimeMillis();
        calc();
        T2 = System.currentTimeMillis();
        T = T2 - T1;
        System.out.println("\nExecution time = " + T + " ms");
    }

    public static void calc() {
        Store store = new Store();

        int n = 4;
        int[] price = {10, 5, 20, 15};
        int m = 2;
        int[] buy = {1, 2};
        int[] free = {1, 1};
        IntVar[] paid = new IntVar[n];
        IntVar[] forFree = new IntVar[n];
        IntVar[][] voucherUsed = new IntVar[m][n];
        IntVar[][] pizzasWeGet = new IntVar[m][n];
        IntVar nbrVouchers = new IntVar(store, m, m);
        IntVar zero = new IntVar(store, 0, 0);
        for (int i = 0; i < n; i++) {
            paid[i] = new IntVar(store, "Bought Pizza" + i, 0, 1);
            forFree[i] = new IntVar(store, "Free Pizza" + i, 0, 1);
            //En pizza kan inte vara både bought och forfree
            store.impose(new XneqY(paid[i], forFree[i]));
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                voucherUsed[i][j] = new IntVar(store, "Used Vouchers" + i + j, 0, 1);
            }
        }

        //I matrisen: max en etta per kolumn, max m ettor per rad ty
        //en pizza kan max användas en gång för voucher och max m vouchers på alla pizzor

        //Max m ettor per rad (Revised: max buy[i] ettor per rad
        for (int i = 0; i < m; i++) {
            store.impose(new SumInt(store, voucherUsed[i], "<=", new IntVar(store, buy[i], buy[i])));
        }

        //Max en etta per kolumn; Ta ut en kolumn och kolla så max ett värde är ett
        for (int i = 0; i < n; i++) {
            IntVar[] temp = new IntVar[m];
            for (int j = 0; j < m; j++) {
                temp[j] = voucherUsed[j][i];
            }
            store.impose(new SumInt(store, temp, "<=", new IntVar(store, 1, 1)));
        }

        //Om vi har köpt fler eller lika många pizzor som en voucher behöver, då får vi free[i] gratispizzor
        for (int i = 0; i < m; i++) {
            PrimitiveConstraint c1 = new SumInt(store, paid, ">=", new IntVar(store, buy[i], buy[i]));
            PrimitiveConstraint c2 = new SumInt(store, forFree, "<=", new IntVar(store, free[i], free[i]));
            PrimitiveConstraint c3 = new SumInt(store, forFree,  "==", new IntVar(store,0,0));
            store.impose(new IfThenElse(c1, c2,c3));
        }

        /*
        Måste kolla vilka pizzor vi får lov att köpa. Skapa matris med pris på pizzorna ? Värden mellan noll och ett,
        på varje rad så ska det vara lika många eller fler som vi köpt med vouchern, (free[i] ist för buy[i]).
        */
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                pizzasWeGet[i][j] = new IntVar(store, "Pizza prices" + i + j, 0, 1);
            }
        }
        /*
        Max m ettor per rad (Revised: max buy[i] ettor per rad i pizzasWeGet) Får inte få fler pizzor än vad kupongen
        säger.
        */
        for (int i = 0; i < m; i++) {
            store.impose(new SumInt(store, pizzasWeGet[i], "<=", new IntVar(store, free[i], free[i])));
        }

        //Max en etta per kolumn; Ta ut en kolumn och kolla så max ett värde är ett(pizzasWeGet)
        for (int i = 0; i < n; i++) {
            IntVar[] temp = new IntVar[m];
            for (int j = 0; j < m; j++) {
                temp[j] = pizzasWeGet[j][i];
            }
            store.impose(new SumInt(store, temp, "<=", new IntVar(store, 1, 1)));
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                store.impose(new XneqY(voucherUsed[i][j], pizzasWeGet[i][j]));
                //Anmärkning: Följande constraint gör att lösning saknas.
                // store.impose(new XneqY(paid[j],pizzasWeGet[i][j]));
            }
        }
        int[] cheapestPizza = new int[n];
        int[] mostExpensiveFree = new int[n];
        for (int i = 0; i < n; i++) {
            cheapestPizza[i] = paid[i].value() * price[i];
        }
        for (int i = 0; i < n; i++) {
            mostExpensiveFree[i] = forFree[i].value() * price[i];
        }
        // Måste egentligen vara ifthenelse  ?  
        store.impose(new XlteqY(new IntVar(store,getMaxValue(mostExpensiveFree),getMaxValue(mostExpensiveFree)),new IntVar(store,getMaxValue(cheapestPizza),getMaxValue(cheapestPizza))));

        /*
        Skapar en ArrayList med "viktade IntVars" och lägger in i en ArrayList för paid.
        Skapar en ArrayList med "viktade IntVars" och lägger in i en ArrayList för forFree.
        ArrayList<IntVar> a = new ArrayList<IntVar>();
        ArrayList<IntVar> b = new ArrayList<IntVar>();
        IntVar[] a = new IntVar[n];
        IntVar[] b = new IntVar[n];
        for(int i = 0; i< n;i++){
            a[i] = new IntVar(store, paid[i].value()*price[i], paid[i].value()*price[i]);
            b[i] = new IntVar(store, forFree[i].value()*price[i], forFree[i].value()*price[i]);
        }
        */



        IntVar[] vouchers = new IntVar[m];
        for (int i = 0; i < m; i++) {
            vouchers[i] = new IntVar(store, "Voucher" + i, 0, 1);
        }
        // Less than or equals to.
        store.impose(new LinearInt(store, paid, price, "<", getSumOfArray(price)));

        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(paid,
                new SmallestDomain<>(),
                new IndomainMin<IntVar>());
        search.setSolutionListener(new PrintOutListener<IntVar>());

        search.getSolutionListener().searchAll(true);
        boolean Result = search.labeling(store, select);
        if (Result) {
            System.out.println("\n*** Yes");
            System.out.println("Solution : " + java.util.Arrays.asList(paid));
        } else System.out.println("\n*** No");

    }


    public static int getSumOfArray(int[] array) {
        int sumOfArray = 0;
        for (int i = 0; i < array.length; i++) {
            sumOfArray += array[i];
        }
        return sumOfArray;
    }

    public static int getSmallestNonZero(int[] array) {
        int small = Integer.MAX_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < small && array[i] != 0) {
                small = array[i];
            }
        }
        return small;
    }

    public static int getMaxValue(int[] array){
        int maxValue = Integer.MIN_VALUE;
        for(int i=1;i < array.length;i++){
            if(array[i] > maxValue){
                maxValue = array[i];
            }
        }
        return maxValue;
    }

}