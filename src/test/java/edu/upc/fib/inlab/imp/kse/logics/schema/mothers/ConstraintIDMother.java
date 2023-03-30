package edu.upc.fib.inlab.imp.kse.logics.schema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;

public class ConstraintIDMother {
    public static ConstraintID createConstraintID(String i) {
        return new ConstraintID(i);
    }
}
