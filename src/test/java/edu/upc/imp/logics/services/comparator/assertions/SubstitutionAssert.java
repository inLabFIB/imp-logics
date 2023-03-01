package edu.upc.imp.logics.services.comparator.assertions;


import edu.upc.imp.logics.schema.Term;
import edu.upc.imp.logics.schema.Variable;
import edu.upc.imp.logics.schema.assertions.TermAssert;
import edu.upc.imp.logics.services.comparator.Substitution;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.Optional;

public class SubstitutionAssert extends AbstractAssert<SubstitutionAssert, Substitution> {
    protected SubstitutionAssert(Substitution substitution) {
        super(substitution, SubstitutionAssert.class);
    }

    public static SubstitutionAssert assertThat(Substitution actual) {
        return new SubstitutionAssert(actual);
    }

    public SubstitutionAssert mapsToConstant(String domainVariableName, String rangeConstantName) {
        Optional<Term> actualTermImageOpt = actual.getTerm(new Variable(domainVariableName));
        Assertions.assertThat(actualTermImageOpt).isPresent();
        Term actualImage = actualTermImageOpt.get();
        TermAssert.assertThat(actualImage).isConstant(rangeConstantName);
        return this;
    }

    public SubstitutionAssert mapsToVariable(String domainVariableName, String rangeVariableName) {
        Optional<Term> actualTermImageOpt = actual.getTerm(new Variable(domainVariableName));
        Assertions.assertThat(actualTermImageOpt).isPresent();
        Term actualImage = actualTermImageOpt.get();
        TermAssert.assertThat(actualImage).isVariable(rangeVariableName);
        return this;
    }
}
