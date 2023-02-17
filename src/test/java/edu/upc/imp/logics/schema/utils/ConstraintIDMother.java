package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.ConstraintID;

public class ConstraintIDMother {
    public static ConstraintID createConstraintID(String i) {
        return new ConstraintID(i);
    }
}
