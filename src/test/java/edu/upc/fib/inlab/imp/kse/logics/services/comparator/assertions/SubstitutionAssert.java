package edu.upc.fib.inlab.imp.kse.logics.services.comparator.assertions;


import edu.upc.fib.inlab.imp.kse.logics.schema.Constant;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import edu.upc.fib.inlab.imp.kse.logics.schema.Variable;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.TermAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.Map;
import java.util.Optional;

public class SubstitutionAssert extends AbstractAssert<SubstitutionAssert, Substitution> {
    protected SubstitutionAssert(Substitution substitution) {
        super(substitution, SubstitutionAssert.class);
    }

    public static SubstitutionAssert assertThat(Substitution actual) {
        return new SubstitutionAssert(actual);
    }

    @SuppressWarnings("UnusedReturnValue")
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

    public SubstitutionAssert hasSize(int size) {
        Assertions.assertThat(actual.getSize()).isEqualTo(size);
        return this;
    }

    public SubstitutionAssert isEmpty() {
        Assertions.assertThat(actual.isEmpty()).isTrue();
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public SubstitutionAssert encodesMapping(Map<Variable, Term> expected) {
        for (Map.Entry<Variable, Term> expectedEntry : expected.entrySet()) {
            Term term = expectedEntry.getKey();
            Term expectedImage = expectedEntry.getValue();
            if (expectedImage instanceof Variable) {
                SubstitutionAssert.assertThat(actual).mapsToVariable(term.getName(), expectedImage.getName());
            } else if (expectedImage instanceof Constant) {
                SubstitutionAssert.assertThat(actual).mapsToConstant(term.getName(), expectedImage.getName());
            } else throw new RuntimeException("Unrecognized term type: " + expectedImage.getClass().getName());
        }
        SubstitutionAssert.assertThat(actual).hasSize(expected.size());
        return this;
    }
}
