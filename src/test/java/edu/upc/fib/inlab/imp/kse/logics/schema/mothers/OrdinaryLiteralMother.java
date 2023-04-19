package edu.upc.fib.inlab.imp.kse.logics.schema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.OrdinaryLiteral;

public class OrdinaryLiteralMother {
    public static OrdinaryLiteral createOrdinaryLiteral(String ordinaryLiteralString, String derivationRuleStrings) {
        ImmutableLiteralsList parsedList = ImmutableLiteralsListMother.create(ordinaryLiteralString, derivationRuleStrings);
        if (parsedList.size() != 1)
            throw new RuntimeException("Expecting " + ordinaryLiteralString + " to encode a single literal");
        else return (OrdinaryLiteral) parsedList.get(0);
    }
}
