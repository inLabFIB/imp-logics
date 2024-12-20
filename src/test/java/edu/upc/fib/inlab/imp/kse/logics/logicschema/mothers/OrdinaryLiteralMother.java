package edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.OrdinaryLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;

public class OrdinaryLiteralMother {
    public static OrdinaryLiteral createOrdinaryLiteral(String ordinaryLiteralString, String derivationRuleStrings) {
        ImmutableLiteralsList parsedList = ImmutableLiteralsListMother.create(ordinaryLiteralString, derivationRuleStrings);
        if (parsedList.size() != 1)
            throw new IMPLogicsException("Expecting " + ordinaryLiteralString + " to encode a single literal");
        else return (OrdinaryLiteral) parsedList.get(0);
    }
}
