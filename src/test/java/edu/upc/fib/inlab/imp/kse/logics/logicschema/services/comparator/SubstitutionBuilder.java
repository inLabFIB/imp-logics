package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Term;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.TermMother;

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
