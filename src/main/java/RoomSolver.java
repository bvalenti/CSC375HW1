import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeoutException;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class RoomSolver implements Runnable {

    Room toSolve;
    RoomPrinter printer;
    ArrayList<Solution> solns = new ArrayList<>();
    int swapnum, solnnum, crossOverNum;
    Exchanger<ArrayList<Solution>> exchanger;
    double mutation;
    Object lock1, lock2;
    ArrayList<Solution> finals;
    MyFrame myInitReference;

    RoomSolver(Room rm, RoomPrinter prnter, int swpnum, int solnum, Exchanger<ArrayList<Solution>> ex,
               int crsovrnum, double mut, Object l1, Object l2, MyFrame myInit) {
        toSolve = rm;
        printer = prnter;
        swapnum = swpnum;
        solnnum = solnum;
        exchanger = ex;
        crossOverNum = crsovrnum;
        mutation = mut;
        lock1 = l1;
        lock2 = l2;
//        finals = finlslns;
        myInitReference = myInit;
    }

    @Override
    public final void run() {

        int af0 = calcTotalAffinity(toSolve);
        ArrayList<Solution> parentSolutions = new ArrayList<>();
        ArrayList<Solution> childSolutions = new ArrayList<>();
        int toDelete[];
        long millis1, millis2;

        initPop();
        System.out.println("I got here");
        Room tmp = new Room(toSolve.width,toSolve.length,toSolve.seats);

        millis1 = System.currentTimeMillis() % 10000;

//        int k = 0;
//        while(calcTotalAffinity(tmp) > calcTotalAffinity(toSolve) - 100) {
        for (int k = 0; k < 10000; k++) {
            parentSolutions = parentSelection(crossOverNum);
            childSolutions = crossOver(parentSolutions);
            if (childSolutions == null) {
                break;
            }
            replaceMutateChildrenPn(childSolutions,mutation);
            toDelete = selectPopToDie(crossOverNum);
            deleteAndAddSolutions(toDelete, childSolutions);

            millis2 = System.currentTimeMillis() % 10000;
            if ((millis2 < millis1) || (millis2 > millis1 + 1000)) {
                millis1 = millis2;
                int afs[] = new int[solns.size()];
                for (int i = 0; i < solns.size()-1; i++) {
                    tmp = new Room(toSolve.width,toSolve.length,toSolve.seats);
                    tmp.applySwaps(solns.get(i));
                    afs[i] = calcTotalAffinity(tmp);
                }
                int tmpind = 0;
                int tmpaf = (int) Double.POSITIVE_INFINITY;
                for (int ii = 0; ii < afs.length-1; ii++) {
                    if (afs[ii] < tmpaf) {
                        tmpaf = afs[ii];
                        tmpind = ii;
                    }
                }
                tmp = new Room(toSolve.width,toSolve.length,toSolve.seats);
                tmp.applySwaps(solns.get(tmpind));

//                synchronized(lock1) {
                    printer.setRoom(tmp);
                    printer.printerRepaint();
//                }
            }
        }

        synchronized(lock2) {
            myInitReference.activeThreadCount--;
            System.out.println(myInitReference.activeThreadCount);
        }
    }

    public ArrayList<Solution> exchange(ArrayList<Solution> sending) {
        ArrayList<Solution> obtained;
        try {
            obtained = exchanger.exchange(sending,1000,MILLISECONDS);
        } catch (InterruptedException e) {
            return exchange(sending);
        } catch (TimeoutException e) {
            synchronized(lock2) {
                if (myInitReference.activeThreadCount == 1) {
                    obtained = null;
                } else {
                    return exchange(sending);
                }
            }
        }
        return obtained;
    }


    //===========================================================
    //Communicates with other threads and trades pieces of solutions to make new solutions.
    public ArrayList<Solution> crossOver(ArrayList<Solution> psln) {
        ArrayList<Solution> pslnObtained = new ArrayList<>(psln.size());
        ArrayList<Solution> childSolutions = new ArrayList<>(psln.size());
        int AFOP, AFGP, rnd, thresh;

        pslnObtained = exchange(psln);
        if (pslnObtained == null) {
            return null;
        }

        for (int i = 0; i < psln.size(); i++) {

            Room tmp1 = new Room(toSolve.width,toSolve.length,toSolve.seats);
            Room tmp2 = new Room(toSolve.width,toSolve.length,toSolve.seats);
            Solution tmpChild = new Solution(swapnum);
            tmp1.applySwaps(psln.get(i));
            tmp2.applySwaps(pslnObtained.get(i));
            AFGP = calcTotalAffinity(tmp1);
            AFOP = calcTotalAffinity(tmp2);
            thresh = AFGP / (AFOP + AFGP) * 100;

            int ind[] = new int[2];
            int flip[] = new int[3];
            ind[0] = (int) Math.round(Math.random() * (swapnum - 1));
//            ind[1] = (int) Math.round(Math.random() * (swapnum - 1));
//            ind[2] = (int) Math.round(Math.random() * (swapnum - 1));
            Arrays.sort(ind);
            flip[0] = (int) Math.round(Math.random() * 99) + 1;
            flip[1] = (int) Math.round(Math.random() * 99) + 1;
//            flip[2] = (int) Math.round(Math.random() * 99) + 1;
//            flip[3] = (int) Math.round(Math.random() * 99) + 1;

            for (int j = 0; j < swapnum; j++) {
                int gene[];
                if (j < ind[0]) {
                    if (flip[0] < thresh) {
                        gene = psln.get(i).swaps.get(j);
                    } else {
                        gene = pslnObtained.get(i).swaps.get(j);
                    }
                }
                else //if (j < ind[1])
                {
                    if (flip[1] < thresh) {
                        gene = psln.get(i).swaps.get(j);
                    } else {
                        gene = pslnObtained.get(i).swaps.get(j);
                    }
                } //else //if (j < ind[2]) {
//                { if (flip[2] < thresh) {
//                        gene = psln.get(i).swaps.get(j);
//                    } else {
//                        gene = pslnObtained.get(i).swaps.get(j);
//                    }
//                }
//                else {
//                    if (flip[3] < thresh) {
//                        gene = psln.get(i).swaps.get(j);
//                    } else {
//                        gene = pslnObtained.get(i).swaps.get(j);
//                    }
//                }
                tmpChild.swaps.add(j,gene);
            }


//            //Uniform crossover weighted by AF metric
//            for (int j = 0; j < swapnum; j++) {
//                int gene[];
//                rnd = (int) Math.round(Math.random() * 100) + 1;
//                if (rnd <= thresh) {
//                    gene = psln.get(i).swaps.get(j);
//                } else {
//                    gene = pslnObtained.get(i).swaps.get(j);
//                }
//                tmpChild.swaps.add(j,gene);
//            }



//            //Divides the swap space into ten equal sections for cross over <- not a good crossover
//            int count = 1;
//            for (int j = 0; j < swapnum; j++) {
//                if (j < count*swapnum/10) {
//                    tmpChild.swaps.add(j, psln.get(i).swaps.get(j));
//                } else if (j < (count+1)*swapnum/10) {
//                    tmpChild.swaps.add(j, pslnObtained.get(i).swaps.get(j));
//                } else {
//                    count = count + 2;
//                    tmpChild.swaps.add(j, psln.get(i).swaps.get(j));
//                }
//            }

            childSolutions.add(i,tmpChild);
        }
        return childSolutions;
    }

    //===========================================================
    //Initializes the solution population.
    public void initPop() {
        for (int i = 0; i < solnnum; i++) {
            Solution tmp = new Solution(swapnum);

            for (int j = 0; j < swapnum; j++) {
                int swap[] = new int[4];
                swap[0] = (int) Math.round(Math.random() * (toSolve.width-1));
                swap[1] = (int) Math.round(Math.random() * (toSolve.length-1));
                swap[2] = (int) Math.round(Math.random() * (toSolve.width-1));
                swap[3] = (int) Math.round(Math.random() * (toSolve.length-1));
                tmp.swaps.add(swap);
            }
            solns.add(tmp);
        }
    }

    //===========================================================
    //Selects the parent solutions that will reproduce tournament style.
    public ArrayList<Solution> parentSelection(int k) {
        ArrayList<Solution> subsolns = new ArrayList<>(k);
        int ind, count;
        int subinds[], comp[] = new int[crossOverNum];
        int subafs[] = new int[crossOverNum];
        Room tmp = new Room(toSolve.width,toSolve.length,toSolve.seats);

        for (int c = 0; c < k; c++) {
            subinds = rankedSelect(crossOverNum);
            for (int j = 0; j < subinds.length; j++) {
                tmp.applySwaps(solns.get(subinds[j]));
                subafs[j] = calcTotalAffinity(tmp);
            }

            count = 0;
            for (int i = 0; i < subinds.length; i++) {
                ind = findLeastGreaterThanComp(subafs, comp);
                if (!subsolns.contains(solns.get(subinds[ind]))) {
                    subsolns.add(c,solns.get(subinds[ind]));
                    break;
                } else {
                    comp[count] = ind;
                    count++;
                }
            }
        }
        return subsolns;
    }


    public int findLeastGreaterThanComp(int subafs[], int comp[]) {
        int tmp = (int) Double.POSITIVE_INFINITY;
        int outind = 0;

        for (int i = 0; i < subafs.length; i++) {
            if (subafs[i] < tmp && !contains(comp, i)) {
                    tmp = subafs[i];
                    outind = i;
                }
        }
        return outind;
    }

    public int findGreatestLessThanComp(int input[], int comp[]) {
        int tmp = (int) Double.NEGATIVE_INFINITY;
        int outind = 0;

        for (int i = 0; i < input.length; i++) {
            if (input[i] > tmp && !contains(comp, i)) {
                tmp = input[i];
                outind = i;
            }
        }
        return outind;
    }

    //Select n number of solutions from the solution space without repeats
    public int[] rankedSelect(int crossoverNum) {
        int selected[] = new int[crossoverNum];
        int indicator, ind, randIndex;

        for (int i = 0; i < crossoverNum; i++) {
            indicator = 1;
            while (indicator != 0) {
                randIndex = (int) Math.round(Math.random() * (solnnum - 1));
                if (contains(selected, randIndex) == false) {
                    selected[i] = randIndex;
                    indicator = 0;
                }
            }
        }
        return selected;
    }

    public Boolean contains(int arr[], int ele) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == ele) {
                return true;
            }
        }
        return false;
    }


    public int[] selectPopToDie(int k) {
        int ind, count = 0;
        int subinds[], comp[] = new int[crossOverNum], out[] = new int[k];
        int subafs[] = new int[crossOverNum];
        Room tmp = new Room(toSolve.width,toSolve.length,toSolve.seats);

        for (int c = 0; c < k; c++) {
            subinds = rankedSelect(crossOverNum);
            for (int j = 0; j < subinds.length; j++) {
                tmp.applySwaps(solns.get(subinds[j]));
                subafs[j] = calcTotalAffinity(tmp);
            }

            count = 0;
            for (int i = 0; i < subinds.length; i++) {
                ind = findGreatestLessThanComp(subafs, comp);

                if (!contains(out,subinds[ind])) {
                    out[c] = subinds[ind];
                    break;
                } else {
                    comp[count] = ind;
                    count++;
                }
            }
        }
        return out;
    }


    //===========================================================
    //Applies mutations across child solutions with probability of Pn > 0.
    public void replaceMutateChildrenPn(ArrayList<Solution> children, double Pn) {
        double rnd;
        int ind, swap[] = new int[4];
        for (int i = 0; i < children.size(); i++) {
            for (int j = 0; j < swapnum; j++ ) {
                rnd = Math.random();
                if (rnd < Pn) {
                    swap[0] = (int) Math.round(Math.random() * (toSolve.width-1));
                    swap[1] = (int) Math.round(Math.random() * (toSolve.length-1));
                    swap[2] = (int) Math.round(Math.random() * (toSolve.width-1));
                    swap[3] = (int) Math.round(Math.random() * (toSolve.length-1));
                    children.get(i).swaps.set(j,swap);
                }
            }
        }
    }


    //===========================================================
    //Applies one random mutation across child solutions.
    public void replaceMutateChildren(ArrayList<Solution> children) {
        int ind1;
        int swap[] = new int[4];

        for (int i = 0; i < children.size(); i++) {
                ind1 = (int) Math.round(Math.random() * (swapnum - 1));
                swap[0] = (int) Math.round(Math.random() * (toSolve.width-1));
                swap[1] = (int) Math.round(Math.random() * (toSolve.length-1));
                swap[2] = (int) Math.round(Math.random() * (toSolve.width-1));
                swap[3] = (int) Math.round(Math.random() * (toSolve.length-1));
                children.get(i).swaps.set(ind1,swap);
        }
    }
    //Applies one random mutation for each solution across full population.
    public void replaceMutatePopulation() {
        int ind1;
        int swap[] = new int[4];

        for (int i = 0; i < solns.size(); i++) {
            ind1 = (int) Math.round(Math.random() * (swapnum - 1));
            swap[0] = (int) Math.round(Math.random() * (toSolve.width-1));
            swap[1] = (int) Math.round(Math.random() * (toSolve.length-1));
            swap[2] = (int) Math.round(Math.random() * (toSolve.width-1));
            swap[3] = (int) Math.round(Math.random() * (toSolve.length-1));
            solns.get(i).swaps.set(ind1,swap);
        }
    }
    //Applies one swap mutation for each solution across total population.
    public void mutateSwap() {
        int ind1, ind2;
        int swap[] = new int[4];

        int tmp[];

        for (int i = 0; i < solns.size(); i++) {
            ind1 = (int) Math.round(Math.random() * (swapnum - 1));
            ind2 = (int) Math.round(Math.random() * (swapnum - 1));
            tmp = solns.get(i).swaps.get(ind1);
            solns.get(i).swaps.set(ind1, solns.get(i).swaps.get(ind2));
            solns.get(i).swaps.set(ind2, tmp);
        }
    }

    //===========================================================
    //Deletes old solutions and replaces with crossover children.
    public void deleteAndAddSolutions(int toDelete[], ArrayList<Solution> children) {
        for (int i = 0; i < toDelete.length; i++) {
            solns.set(toDelete[i],children.get(i));
        }
    }

    //===========================================================
    //Applies swaps to room for each solution.
    public Room applySwaps(Solution soln) {
        Room tmp = new Room(toSolve.width, toSolve.length);

        int tp[];
        for (int i = 0; i < soln.swaps.size(); i++) {
            tp = soln.swaps.get(i);
            tmp = swapSeats(tmp,tp[0],tp[1],tp[2],tp[3]);
        }
        return tmp;
    }

    //===========================================================
    //Calculates the differences in affinity between all combinations of neighbors and sums for a total value.
    public int calcTotalAffinity(Room toSolv) {
        int n; int s; int e; int w;
        int totAf = 0;
        for (int i = 0; i < toSolv.width; i++) {
            for (int j = 0; j < toSolv.length; j++) {
                n = 0; s = 0; w = 0; e = 0;
                if (toSolv.seats.get(i).get(j).north != null) {
                    n = abs(toSolv.seats.get(i).get(j).Affinity - toSolv.seats.get(i).get(j).north.Affinity);
//                    n = toSolv.seats.get(i).get(j).Affinity - toSolv.seats.get(i).get(j).north.Affinity;
//                    n = n*n;
                }
                if (toSolv.seats.get(i).get(j).south != null) {
                    s = abs(toSolv.seats.get(i).get(j).Affinity - toSolv.seats.get(i).get(j).south.Affinity);
//                    s = toSolv.seats.get(i).get(j).Affinity - toSolv.seats.get(i).get(j).south.Affinity;
//                    s = s*s;
                }
                if (toSolv.seats.get(i).get(j).west != null) {
                    w = abs(toSolv.seats.get(i).get(j).Affinity - toSolv.seats.get(i).get(j).west.Affinity);
//                    w = toSolv.seats.get(i).get(j).Affinity - toSolv.seats.get(i).get(j).west.Affinity;
//                    w = w*w;
                }
                if (toSolv.seats.get(i).get(j).east != null) {
                    e = abs(toSolv.seats.get(i).get(j).Affinity - toSolv.seats.get(i).get(j).east.Affinity);
//                    e = toSolv.seats.get(i).get(j).Affinity - toSolv.seats.get(i).get(j).east.Affinity;
//                    e = e*e;
                }
                totAf = totAf + (n+s+w+e);
            }
        }
        return totAf;
    }

    //===========================================================
    //Swaps locations of two people (a1,a2) and (b1,b2).
    public Room swapSeats(Room toSolv, int a1, int a2, int b1, int b2) {
        Person tmp1 = new Person(toSolv.seats.get(a1).get(a2).Affinity,toSolv.seats.get(a1).get(a2).ID);
        Person tmp2 = new Person(toSolv.seats.get(b1).get(b2).Affinity,toSolv.seats.get(b1).get(b2).ID);

        tmp1.north = toSolv.seats.get(b1).get(b2).north;
        tmp1.south = toSolv.seats.get(b1).get(b2).south;
        tmp1.west = toSolv.seats.get(b1).get(b2).west;
        tmp1.east = toSolv.seats.get(b1).get(b2).east;

        tmp2.north = toSolv.seats.get(a1).get(a2).north;
        tmp2.south = toSolv.seats.get(a1).get(a2).south;
        tmp2.west = toSolv.seats.get(a1).get(a2).west;
        tmp2.east = toSolv.seats.get(a1).get(a2).east;

        toSolv.seats.get(a1).set(a2,tmp2);
        toSolv.seats.get(b1).set(b2,tmp1);

        return toSolv;
    }


    //===========================================================
    //Get and set methods
    public Room getRoom() {return toSolve;}



}

