package edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;

public class ConstraintIDMother {
    public static ConstraintID createConstraintID(String i) {
        return new ConstraintID(i);
    }
}
