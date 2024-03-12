package edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaParser;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaWithIDsParser;

public class ImmutableAtomListMother {

    private final static LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();

    public static ImmutableAtomList create(String listOfAtoms) {
        LogicSchema domainSchema = parser.parse("@1 :- " + listOfAtoms);
        ImmutableLiteralsList body = domainSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
        return new ImmutableAtomList(body.stream().map(l -> (OrdinaryLiteral) l).map(OrdinaryLiteral::getAtom).toList());
    }

}
