package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.ArityMismatch;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.NewFreshVariable;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of a logic Atom.
 * An Atom consists of a Predicate (e.g. "Employee") together with a list of Terms (e.g. "x", "y").
 * An atom should belong, at most, to one NormalClause, or one literal. That is,
 * atoms should not be reused several times.
 */
public class Atom {
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

    /**
     * <p>Unfolding an atom returns a list of literals' list, one for each derivation rule of (the predicate of) this atom.
     * In particular, for each derivation rule, it returns a literals' list replacing the variables of the derivation rule's head
     * for the terms appearing in this atom. </p>
     *
     * <p>For instance, if we have the atom "P(1)", with derivation rules "P(x) :- R(x), S(x)" and "P(y) :- T(y), U(y)",
     * unfolding "P(1)" will return two literals' list: "R(1), S(1)" and "T(1), U(1)". </p>
     *
     * <p>This unfolding avoids clashing the variables inside the derivation rule's body with the variables appearing in this atom.
     * For instance, if we have the ordinary literal "P(a, b)" with a derivation rule "P(x, y) :- R(x, y, a, b)" it will return
     * "R(a, b, a', b')" </p>
     *
     * <p>If the the derivation rules of such atom contains constants, or repeated variables in the head, they are treated as new
     * built-in literals. E.g. if we have the rule "R(a, 1) :- S(a)", and we unfold "R(x, y)", we obtain "R(x, y), y = 1";
     * similarly, if we have the rule "R(a,a) :- S(a)", and we unfold "R(x,y)" we obtain "R(x,y), x=y"</p>
     *
     * @return a list of ImmutableLiteralsList representing the result of unfolding this atom
     */
    public List<ImmutableLiteralsList> unfold() {
        if (this.isBase()) {
            return List.of(new ImmutableLiteralsList(new OrdinaryLiteral(this)));
        } else {
            List<ImmutableLiteralsList> result = new LinkedList<>();
            for (int derivationRuleIndex = 0; derivationRuleIndex < this.getPredicate().getDerivationRules().size();
                 derivationRuleIndex++) {
                result.add(this.unfold(derivationRuleIndex));
            }
            return result;
        }
    }


    protected ImmutableLiteralsList unfold(int derivationRuleIndex) {
        if (this.isBase()) {
            return new ImmutableLiteralsList(new OrdinaryLiteral(this));
        } else {
            DerivationRule derivationRule = this.getPredicate().getDerivationRules().get(derivationRuleIndex);
            Set<Variable> potentiallyClashingVariables = computePotentiallyClashingVariables(derivationRule);
            ImmutableLiteralsList bodyLiteralsAvoidingClashWithThisTerms = computeListThatAvoidsClash(derivationRule.getBody(), potentiallyClashingVariables);
            return computeLiteralsWhenUnifyingTheHead(derivationRule.getHead().terms, bodyLiteralsAvoidingClashWithThisTerms);
        }
    }

    private ImmutableLiteralsList computeLiteralsWhenUnifyingTheHead(List<Term> headTerms, ImmutableLiteralsList bodyLiterals) {
        SubstitutionAndBuiltInLiterals substitutionAndBuiltInLiterals = computeSubstitutionForHeadAndAdditionalBuiltInLiterals(headTerms);
        ImmutableLiteralsList bodyLiteralsAfterSubstitution = bodyLiterals.applySubstitution(substitutionAndBuiltInLiterals.substitution());
        List<Literal> allLiterals = new LinkedList<>(bodyLiteralsAfterSubstitution);
        allLiterals.addAll(substitutionAndBuiltInLiterals.builtInLiterals);
        return new ImmutableLiteralsList(allLiterals);
    }

    private SubstitutionAndBuiltInLiterals computeSubstitutionForHeadAndAdditionalBuiltInLiterals(List<Term> headTerms) {
        Substitution substitution = new Substitution();
        List<BuiltInLiteral> builtInLiterals = new LinkedList<>();
        Map<Variable, Integer> visitedVariablesToIndex = new HashMap<>(); //Useful for detecting repeated variables
        for (int i = 0; i < headTerms.size(); ++i) {
            Term headTerm = headTerms.get(i);
            Term actualTerm = this.terms.get(i);
            if (headTerm.isConstant()) {
                builtInLiterals.add(new ComparisonBuiltInLiteral(headTerm, actualTerm, ComparisonOperator.EQUALS));
            } else if (headTerm.isVariable()) {
                Variable headVariable = (Variable) headTerm;
                if (visitedVariablesToIndex.containsKey(headVariable)) {
                    //Repeated variable
                    int previousIndex = visitedVariablesToIndex.get(headVariable);
                    builtInLiterals.add(new ComparisonBuiltInLiteral(this.terms.get(i), this.terms.get(previousIndex), ComparisonOperator.EQUALS));
                } else {
                    substitution.addMapping((Variable) headTerm, actualTerm);
                    visitedVariablesToIndex.put(headVariable, i);
                }
            } else throw new RuntimeException("Unrecognized term subclass " + headTerm.getClass().getName());
        }
        return new SubstitutionAndBuiltInLiterals(substitution, builtInLiterals);
    }

    private Set<Variable> computePotentiallyClashingVariables(DerivationRule derivationRule) {
        /*
         * There might be a clash with the terms that are currently in this atom's terms such that
         * are not contained in the head of the derivation rule. Indeed, all those variables appearing
         * in the head of the derivation rule will be replaced during the unfolding, and thus, cannot
         * cause a clash.
         *
         * For instance, assume that we are unfolding "P(a, b, c)"
         * and we have the derivation rule "P(x, y, a) :- R(x, y, a, b, c)"
         *
         * The variables that can clash during the unfolding are: 'b' and 'c'.
         * Indeed, 'a' cannot cause a clash, because any apparition of 'a' in the derivation rule will disappear.
         *
         */
        Set<Variable> potentiallyClashingVariables = this.terms.getUsedVariables();
        potentiallyClashingVariables.removeAll(derivationRule.getHeadTerms().getUsedVariables());
        return potentiallyClashingVariables;
    }


    private ImmutableLiteralsList computeListThatAvoidsClash(ImmutableLiteralsList literalsList, Set<Variable> potentiallyClashingTerms) {
        Substitution substitutionForClashingTerms = new Substitution();
        Set<Variable> currentlyUsedVariables = computeCurrentlyUsedVariables(literalsList, potentiallyClashingTerms);
        for (Term potentiallyClashingTerm : potentiallyClashingTerms) {
            Variable newFreshVariable = NewFreshVariable.computeNewFreshVariable(potentiallyClashingTerm.getName(), currentlyUsedVariables);
            substitutionForClashingTerms.addMapping(new Variable(potentiallyClashingTerm.getName()), newFreshVariable);
            currentlyUsedVariables.add(newFreshVariable);
        }
        return literalsList.applySubstitution(substitutionForClashingTerms);
    }

    private Set<Variable> computeCurrentlyUsedVariables(ImmutableLiteralsList literalsList, Set<Variable> potentiallyClashingTerms) {
        Set<Variable> usedVariables = new LinkedHashSet<>();
        usedVariables.addAll(literalsList.getUsedVariables());
        usedVariables.addAll(potentiallyClashingTerms);
        return usedVariables;
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

    /**
     * @param substitution not null
     * @return an atom after applying the given substitution. The atom will be new if some term has changed.
     * Otherwise, it will be the same
     */
    public Atom applySubstitution(Substitution substitution) {
        if (substitution.replacesSomeVariableOf(this.getVariables())) {
            return new Atom(this.predicate, this.terms.applySubstitution(substitution));
        } else return this;
    }

    @Override
    public String toString() {
        String termsAsString = terms.stream().map(Term::getName).collect(Collectors.joining(", "));
        return this.getPredicateName() + "(" + termsAsString + ")";
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return true if all the terms of this atom are constants, false otherwise
     */
    public boolean isGround() {
        return terms.stream().allMatch(Term::isConstant);
    }

    public Set<Variable> getVariables() {
        return terms.stream().filter(Variable.class::isInstance)
                .map(Variable.class::cast)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private record SubstitutionAndBuiltInLiterals(Substitution substitution, List<BuiltInLiteral> builtInLiterals) {
    }
}
