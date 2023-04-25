package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.ComparisonBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.ComparisonOperator;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import edu.upc.fib.inlab.imp.kse.logics.schema.Variable;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

class EqualityLiteralsGroup {
    //TODO:
    // - terms is derived from equalities, we might remove it
    private final Set<Term> terms;
    private final Set<ComparisonBuiltInLiteral> equalityLiterals;

    private EqualityLiteralsGroup() {
        this.terms = new LinkedHashSet<>();
        this.equalityLiterals = new LinkedHashSet<>();
    }

    EqualityLiteralsGroup(ComparisonBuiltInLiteral equality) {
        if (!equality.getOperator().equals(ComparisonOperator.EQUALS)) {
            throw new IllegalArgumentException("Cannot create a grouped of equality literals with non-equality literal " + equality);
        }
        this.terms = new LinkedHashSet<>(equality.getTerms());
        this.equalityLiterals = new LinkedHashSet<>();
        equalityLiterals.add(equality);
    }

    public SubstitutionResult computeSubstitutionResult() {
        Set<Term> constantTerms = terms.stream()
                .filter(Term::isConstant)
                .collect(Collectors.toSet());
        if (constantTerms.isEmpty()) {
            Substitution substitution = new Substitution();
            Term rangeTerm = terms.iterator().next();
            for (Term domainTerm : terms) {
                if (!domainTerm.equals(rangeTerm)) {
                    substitution.addMapping((Variable) domainTerm, rangeTerm);
                }
            }
            return new SubstitutionResult(substitution, equalityLiterals);
        } else if (constantTerms.size() == 1) {
            Substitution substitution = new Substitution();
            Term rangeTerm = constantTerms.iterator().next();
            for (Term domainTerm : terms) {
                if (!domainTerm.equals(rangeTerm) && domainTerm.isVariable()) {
                    substitution.addMapping((Variable) domainTerm, rangeTerm);
                }
            }
            return new SubstitutionResult(substitution, equalityLiterals);
        } else {
            return new SubstitutionResult(new Substitution(), Set.of());
        }
    }

    private void addEqualities(Set<ComparisonBuiltInLiteral> equalityLiterals) {
        equalityLiterals.forEach(this::addEquality);
    }

    public void addEquality(ComparisonBuiltInLiteral equalityLiteral) {
        equalityLiterals.add(equalityLiteral);
    }

    private void addTerms(Set<Term> terms) {
        terms.forEach(this::addTerm);
    }

    public void addTerm(Term term) {
        terms.add(term);
    }

    public boolean containsTerm(Term term) {
        return terms.contains(term);
    }

    public EqualityLiteralsGroup union(EqualityLiteralsGroup secondGroup) {
        EqualityLiteralsGroup newGroup = new EqualityLiteralsGroup();
        newGroup.addTerms(this.terms);
        newGroup.addTerms(secondGroup.terms);
        newGroup.addEqualities(this.equalityLiterals);
        newGroup.addEqualities(secondGroup.equalityLiterals);
        return newGroup;
    }
}
