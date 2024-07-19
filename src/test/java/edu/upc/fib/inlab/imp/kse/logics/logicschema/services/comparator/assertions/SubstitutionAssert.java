package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.assertions;


import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.TermAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Constant;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Term;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SubstitutionAssert extends AbstractAssert<SubstitutionAssert, Substitution> {
    protected SubstitutionAssert(Substitution substitution) {
        super(substitution, SubstitutionAssert.class);
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
            } else throw new IMPLogicsException("Unrecognized term type: " + expectedImage.getClass().getName());
        }
        SubstitutionAssert.assertThat(actual).hasSize(expected.size());
        return this;
    }

    public SubstitutionAssert mapsToVariable(String domainVariableName, String rangeVariableName) {
        Optional<Term> actualTermImageOpt = actual.getTerm(new Variable(domainVariableName));
        Assertions.assertThat(actualTermImageOpt).isPresent();
        Term actualImage = actualTermImageOpt.get();
        TermAssert.assertThat(actualImage).isVariable(rangeVariableName);
        return this;
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

    public SubstitutionAssert hasSize(int size) {
        Assertions.assertThat(actual.getSize()).isEqualTo(size);
        return this;
    }

    /**
     * Checks whether the actual substitution replaces each variable of the input to a different variable. The check
     * fails if some variable from the input is not mapped to any term, or if some variable of the input is mapped to a
     * constant.
     *
     * @param variables non null set of variables, might be empty
     * @return this assert
     */
    public SubstitutionAssert mapsToDifferentVariables(Set<Variable> variables) {
        Set<Term> rangeTerms = variables.stream()
                .map(actual::getTerm)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        Assertions.assertThat(rangeTerms)
                .hasSize(variables.size())
                .allMatch(Term::isVariable);

        return this;
    }
}
