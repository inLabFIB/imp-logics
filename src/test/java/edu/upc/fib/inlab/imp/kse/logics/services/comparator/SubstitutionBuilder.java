package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import edu.upc.fib.inlab.imp.kse.logics.schema.Variable;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.TermMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;

public class SubstitutionBuilder {

    private final Substitution substitution = new Substitution();

    public SubstitutionBuilder addMapping(String domainVariableName, String rangeTermName) {
        Variable domainVariable = new Variable(domainVariableName);
        Term rangeTerm = TermMother.createTerm(rangeTermName);
        substitution.addMapping(domainVariable, rangeTerm);
        return this;
    }

    public Substitution build() {
        return substitution;
    }


}
