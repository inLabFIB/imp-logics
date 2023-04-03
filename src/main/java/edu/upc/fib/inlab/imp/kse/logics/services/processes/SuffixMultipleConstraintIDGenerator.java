package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;

import java.util.LinkedList;
import java.util.List;

public class SuffixMultipleConstraintIDGenerator implements MultipleConstraintIDGenerator {

    public static final String CONSTRAINT_ID_PATTERN = "%s_%d";

    @Override
    public List<ConstraintID> generateNewConstraintsIDs(ConstraintID originalID, int numberOfRequiredIDs) {
        if (numberOfRequiredIDs == 1) return List.of(originalID);
        List<ConstraintID> result = new LinkedList<>();
        for (int i = 1; i <= numberOfRequiredIDs; ++i) {
            String constraintIdString = String.format(CONSTRAINT_ID_PATTERN, originalID.id(), i);
            result.add(new ConstraintID(constraintIdString));
        }
        return result;
    }
}
