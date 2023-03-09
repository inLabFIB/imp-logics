package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.exceptions.ArityMismatch;
import edu.upc.imp.logics.schema.operations.Substitution;
import edu.upc.imp.logics.schema.visitor.Visitable;
import edu.upc.imp.logics.schema.visitor.Visitor;

import java.util.*;

/**
 * Implementation of a logic Atom.
 * An Atom consists of a Predicate (e.g. "Employee") together with a list of Terms (e.g. "x", "y").
 * An atom should belong, at most, to one NormalClause, or one literal. That is,
 * atoms should not be reused several times.
 */
public class Atom implements Visitable {
    /**
     * Invariants:
     * - Predicate must not be null
     * - terms must not be null
     * - The arity of the predicate should coincide with its list of terms
     * - Terms list is immutable
     */
    private final Predicate predicate;
    private final ImmutableTermList terms;

    public Atom(Predicate predicate, List<Term> terms) {
        if (Objects.isNull(predicate)) throw new IllegalArgumentException("Predicate cannot be null");
        if (Objects.isNull(terms)) throw new IllegalArgumentException("Terms cannot be null");
        checkArityMatches(predicate.getArity(), terms);

        this.predicate = predicate;
        this.terms = new ImmutableTermList(terms);
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public ImmutableTermList getTerms() {
        return terms;
    }

    private static void checkArityMatches(int arity, List<Term> terms) {
        if (arity != terms.size()) throw new ArityMismatch(arity, terms.size());
    }

    public List<ImmutableLiteralsList> unfold() {
        if (this.isBase()) {
            return List.of(new ImmutableLiteralsList(new OrdinaryLiteral(new Atom(this.predicate, this.terms))));
        } else {
            List<ImmutableLiteralsList> result = new LinkedList<>();
            for (DerivationRule derivationRule : this.getPredicate().getDerivationRules()) {
                ImmutableLiteralsList bodyLiteralsAvoidingClashWithThisTerms = computeListThatAvoidsClash(derivationRule.getBody(), this.terms);
                Substitution substitutionOfHeadTerms = new Substitution(derivationRule.getHead().terms, this.terms);
                result.add(bodyLiteralsAvoidingClashWithThisTerms.applySubstitution(substitutionOfHeadTerms));
            }
            return result;
        }
    }

    private ImmutableLiteralsList computeListThatAvoidsClash(ImmutableLiteralsList literalsList, ImmutableTermList potentiallyClashingTerms) {
        Substitution substitutionForClashingTerms = new Substitution();
        Set<Variable> currentlyUsedVariables = computeCurrentlyUsedVariables(literalsList, potentiallyClashingTerms);
        for (Term potentiallyClashingTerm : potentiallyClashingTerms) {
            if (potentiallyClashingTerm.isVariable()) {
                Variable newFreshVariable = computeNewFreshVariable(potentiallyClashingTerm.getName(), currentlyUsedVariables);
                substitutionForClashingTerms.addMapping(new Variable(potentiallyClashingTerm.getName()), newFreshVariable);
                currentlyUsedVariables.add(newFreshVariable);
            }
        }
        return literalsList.applySubstitution(substitutionForClashingTerms);
    }

    private Set<Variable> computeCurrentlyUsedVariables(ImmutableLiteralsList literalsList, ImmutableTermList potentiallyClashingTerms) {
        Set<Variable> usedVariables = new HashSet<>();
        usedVariables.addAll(literalsList.getUsedVariables());
        usedVariables.addAll(potentiallyClashingTerms.getUsedVariables());
        return usedVariables;
    }

    private Variable computeNewFreshVariable(String variableNamePrefix, Set<Variable> usedVariables) {
        String proposedNewVariableName = variableNamePrefix;
        while (usedVariables.contains(new Variable(proposedNewVariableName))) {
            proposedNewVariableName = proposedNewVariableName + "'";
        }
        return new Variable(proposedNewVariableName);
    }

    @Override
    public <T, R> T accept(Visitor<T, R> visitor, R context) {
        return visitor.visitAtom(this, context);
    }

    public String getPredicateName() {
        return predicate.getName();
    }

    public boolean isDerived() {
        return predicate.isDerived();
    }

    public boolean isBase() {
        return predicate.isBase();
    }

    public Atom applySubstitution(Substitution substitution) {
        return new Atom(this.predicate, this.terms.applySubstitution(substitution));
    }
}
