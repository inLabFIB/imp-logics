package edu.upc.imp.logics.services.comparator;

import edu.upc.imp.logics.schema.Term;
import edu.upc.imp.logics.schema.Variable;
import edu.upc.imp.logics.schema.utils.TermMother;

public class SubstitutionBuilder {

    private final Substitution substitution = new Substitution();

    public SubstitutionBuilder addMapping(String domainTermName, String rangeTermName) {
        Variable domainVariable = new Variable(domainTermName);
        Term rangeTerm = TermMother.createTerm(rangeTermName);
        substitution.addMapping(domainVariable, rangeTerm);
        return this;
    }

    public Substitution build() {
        return substitution;
    }


}
