public class Person {

    int Affinity;
    int ID;

    Person north = null;
    Person south = null;
    Person west = null;
    Person east = null;

    Person(int af, int id) {Affinity = af; ID = id;}

}
