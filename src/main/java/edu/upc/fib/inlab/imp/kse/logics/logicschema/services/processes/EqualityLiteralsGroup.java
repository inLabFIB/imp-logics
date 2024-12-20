package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ComparisonBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Term;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class EqualityLiteralsGroup {

    private final Set<ComparisonBuiltInLiteral> equalityLiterals;

    EqualityLiteralsGroup() {
        equalityLiterals = new LinkedHashSet<>();
    }

    boolean containsTerm(Term term) {
        return getTerms().contains(term);
    }

    private Set<Term> getTerms() {
        return equalityLiterals.stream()
                .flatMap(equality -> equality.getTerms().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    EqualityLiteralsGroup union(EqualityLiteralsGroup secondGroup) {
        EqualityLiteralsGroup newGroup = new EqualityLiteralsGroup();
        newGroup.addEqualities(equalityLiterals);
        newGroup.addEqualities(secondGroup.equalityLiterals);
        return newGroup;
    }

    private void addEqualities(Set<ComparisonBuiltInLiteral> equalityLiterals) {
        equalityLiterals.forEach(this::addEquality);
    }

    void addEquality(ComparisonBuiltInLiteral equalityLiteral) {
        equalityLiterals.add(equalityLiteral);
    }

    SubstitutionForEqualities computeSubstitutionResult() {
        Optional<Term> optionalRangeTerm = obtainRangeTerm();
        return optionalRangeTerm
                .map(this::createSubstitutionResult)
                .orElseGet(SubstitutionForEqualities::empty);

    }

    private Optional<Term> obtainRangeTerm() {
        Set<Term> terms = getTerms();
        Set<Term> constantTerms = terms.stream()
                .filter(Term::isConstant)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (constantTerms.size() == 1) {
            return Optional.of(constantTerms.iterator().next());
        } else if (constantTerms.isEmpty()) {
            return Optional.of(terms.iterator().next());
        }
        return Optional.empty();
    }

    private SubstitutionForEqualities createSubstitutionResult(Term rangeTerm) {
        Substitution substitution = new Substitution();
        Set<Variable> variables = this.getVariables();
        for (Variable domainVariable : variables) {
            if (!domainVariable.equals(rangeTerm)) {
                substitution.addMapping(domainVariable, rangeTerm);
            }
        }
        return new SubstitutionForEqualities(substitution, equalityLiterals);
    }

    private Set<Variable> getVariables() {
        return getTerms().stream()
                .filter(Term::isVariable)
                .map(Variable.class::cast)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
