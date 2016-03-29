package clef10;

/**
 Allows sorting of the elements of a list in a descending order based on the value in a <key,value> pair.
 */
class IntDouble implements Comparable<IntDouble> {
    private int id;
    private double val;
    public IntDouble(int i, double d) { id = i; val = d; }
    public int compareTo(IntDouble idp) {
        return - (new Double(val)).compareTo(idp.val);
    }

    public int getId() {
        return id;
    }

    public double getVal() {
        return val;
    }

}
