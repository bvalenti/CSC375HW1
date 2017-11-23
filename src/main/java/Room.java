import java.util.ArrayList;

public final class Room {
    int width;
    int length;
    int count = 1;
    ArrayList<ArrayList<Person>> seats;

    Room(int w, int l) {
        width = w;
        length = l;
        seats = new ArrayList<>(length);

        for (int i = 0; i < w; i++) {
            seats.add(new ArrayList<Person>(l));
        }
    }

    Room(int w, int l, ArrayList<ArrayList<Person>> inputSeats) {
        width = w;
        length = l;
        seats = new ArrayList<>(length);

        for (int i = 0; i < w; i++) {
            seats.add(new ArrayList<Person>(l));
        }

        for (int j = 0; j < length; j++) {
            for (int i = 0; i < width; i++) {
                seats.get(i).add(j, inputSeats.get(i).get(j));
            }
        }
    }

    //===========================================================
    public void initRoom() {
        for (int j = 0; j < length; j++) {
            for (int i = 0; i < width; i++) {
//                seats.get(i).add(j, new Person((int) (Math.round(Math.random() * 9)) + 1, count));
                seats.get(i).add(j, new Person(((int) (Math.round(Math.random() * 4)) + 1)*2, count));
                count++;
            }
        }
        assignNeighbors();
    }

    //===========================================================
    public void assignNeighbors() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {

                if (i == 0 && j == 0) {
                    seats.get(i).get(j).east = seats.get(i + 1).get(j);
                    seats.get(i).get(j).south = seats.get(i).get(j + 1);
                } else if (i == width - 1 && j == 0) {
                    seats.get(i).get(j).west = seats.get(i - 1).get(j);
                    seats.get(i).get(j).south = seats.get(i).get(j + 1);
                } else if (i == 0 && j == length - 1) {
                    seats.get(i).get(j).east = seats.get(i + 1).get(j);
                    seats.get(i).get(j).north = seats.get(i).get(j - 1);
                } else if (i == width - 1 && j == length - 1) {
                    seats.get(i).get(j).west = seats.get(i - 1).get(j);
                    seats.get(i).get(j).north = seats.get(i).get(j - 1);

                } else if (i < width - 1 && j == 0) {
                    seats.get(i).get(j).east = seats.get(i + 1).get(j);
                    seats.get(i).get(j).west = seats.get(i - 1).get(j);
                    seats.get(i).get(j).south = seats.get(i).get(j + 1);
                } else if (i == 0 && j < length - 1) {
                    seats.get(i).get(j).east = seats.get(i + 1).get(j);
                    seats.get(i).get(j).north = seats.get(i).get(j - 1);
                    seats.get(i).get(j).south = seats.get(i).get(j + 1);
                } else if (i == width - 1 && j < length - 1) {
                    seats.get(i).get(j).west = seats.get(i - 1).get(j);
                    seats.get(i).get(j).north = seats.get(i).get(j - 1);
                    seats.get(i).get(j).south = seats.get(i).get(j + 1);
                } else if (i < width - 1 && j == length - 1) {
                    seats.get(i).get(j).east = seats.get(i + 1).get(j);
                    seats.get(i).get(j).west = seats.get(i - 1).get(j);
                    seats.get(i).get(j).north = seats.get(i).get(j - 1);
                } else {
                    seats.get(i).get(j).east = seats.get(i + 1).get(j);
                    seats.get(i).get(j).west = seats.get(i - 1).get(j);
                    seats.get(i).get(j).north = seats.get(i).get(j - 1);
                    seats.get(i).get(j).south = seats.get(i).get(j + 1);
                }
            }
        }
    }

    //===========================================================
    public void applySwaps(Solution soln) {
        int tp[];
        for (int i = 0; i < soln.swaps.size(); i++) {
            tp = soln.swaps.get(i);
            swapSeats(tp[0],tp[1],tp[2],tp[3]);
        }
    }

    //===========================================================
    //Swaps locations of two people (a1,a2) and (b1,b2).
    public void swapSeats(int a1, int a2, int b1, int b2) {
        Person tmp1 = new Person(seats.get(a1).get(a2).Affinity,seats.get(a1).get(a2).ID);
        Person tmp2 = new Person(seats.get(b1).get(b2).Affinity,seats.get(b1).get(b2).ID);

        tmp1.north = seats.get(b1).get(b2).north;
        tmp1.south = seats.get(b1).get(b2).south;
        tmp1.west = seats.get(b1).get(b2).west;
        tmp1.east = seats.get(b1).get(b2).east;

        tmp2.north = seats.get(a1).get(a2).north;
        tmp2.south = seats.get(a1).get(a2).south;
        tmp2.west = seats.get(a1).get(a2).west;
        tmp2.east = seats.get(a1).get(a2).east;

        seats.get(a1).set(a2,tmp2);
        seats.get(b1).set(b2,tmp1);
        }
}
