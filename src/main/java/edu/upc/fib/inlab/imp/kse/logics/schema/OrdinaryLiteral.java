package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of a logic OrdinaryLiteral.
 * E.g. "not(Emp(x))"
 * Ordinary literals should not be reused among several normal clauses
 */
public class OrdinaryLiteral extends Literal {
    /**
     * Invariants:
     * - Atom must not be null
     */
    private final Atom atom;
    private final boolean isPositive;

    public OrdinaryLiteral(Atom atom, boolean isPositive) {
        if(Objects.isNull(atom)) throw new IllegalArgumentException("Atom cannot be null");
        this.atom = atom;
        this.isPositive = isPositive;
    }

    /**
     * Creates an ordinary literal with a positive sign
     *
     * @param atom non-null
     */
    public OrdinaryLiteral(Atom atom) {
        this(atom, true);
    }

    public boolean isPositive() {
        return isPositive;
    }

    public boolean isNegative() {
        return !isPositive;
    }

    public Atom getAtom() {
        return atom;
    }

    public Predicate getPredicate() {
        return atom.getPredicate();
    }

    public String getPredicateName() {
        return atom.getPredicateName();
    }

    @Override
    public ImmutableTermList getTerms() {
        return atom.getTerms();
    }


    @Override
    public OrdinaryLiteral applySubstitution(Substitution substitution) {
        if (substitution.replacesSomeVariableOf(this.getUsedVariables())) {
            return new OrdinaryLiteral(atom.applySubstitution(substitution), isPositive);
        } else return this;
    }

    @Override
    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public OrdinaryLiteral buildNegatedLiteral() {
        boolean shouldBePositive = !this.isPositive();
        return new OrdinaryLiteral(new Atom(this.getAtom().getPredicate(), this.getTerms()), shouldBePositive);
    }

    public boolean isDerived() {
        return atom.isDerived();
    }

    public boolean isBase() {
        return atom.isBase();
    }

    /**
     * <p>Unfolding a positive ordinary literal returns a list of literals' list, one for each derivation rule of this ordinary literal.
     * In particular, for each derivation rule, it returns a literalsList replacing the variables of the derivation rule's head
     * for the terms appearing in this literal. </p>
     *
     * <p>For instance, if we have the ordinary literal "P(1)", with derivation rules "P(x) :- R(x), S(x)" and "P(y) :- T(y), U(y)",
     * unfolding "P(1)" will return two literals' list: "R(1), S(1)" and "T(1), U(1)". </p>
     *
     * <p>This unfolding avoids clashing the variables inside the derivation rule's body with the variables appearing in this literal.
     * For instance, if we have the ordinary literal "P(a, b)" with a derivation rule "P(x, y) :- R(x, y, a, b)" it will return
     * "R(a, b, a', b')" </p>
     *
     * <p>If the ordinary literal is base, or it is negated, it returns the very same literal. </p>
     *
     * @return a list of ImmutableLiteralsList representing the result of unfolding this literal
     */
    public List<ImmutableLiteralsList> unfold() {
        return unfold(false);
    }

    /**
     * This is an extension of the unfold method that also applies an unfolding for negated literals whose
     * derivation rules does not contain existential variables.
     * E.g.: Suppose the literal "not(Derived(x))" with derivation rule "Derived(x) :- A(x), not(B(x))"
     * <p>
     * Unfolding not(Derived()) will return two literals list:
     * "not(A(x))" and "B(x)".
     *
     * @return a list of ImmutableLiteralsList representing the result of unfolding the index-th literal
     */
    public List<ImmutableLiteralsList> unfoldWithNegationExtension() {
        return unfold(true);
    }

    public List<ImmutableLiteralsList> unfold(boolean unfoldNegatedLiterals) {
        if (isNegative()) {
            if (unfoldNegatedLiterals &&
                    this.hasNoExistentialVariableInDerivationRules() &&
                    this.allLiteralsFromDerivationRuleCanBeNegated()
            ) {
                return negateAccordingToMorganRules(this.getAtom().unfold());
            } else {
                return List.of(new ImmutableLiteralsList(this));
            }
        }
        return atom.unfold();
    }


    /**
     * The listOfLists is interpreted as an OR of ANDS. This is consistent with the interpretation
     * of a list of derivation rules of some predicate P, which makes a derived literal of P evaluate
     * to true if one of the bodies of the derivation rules of P evaluates to true.
     * <p>
     * This function applies a NOT over such OR of ANDS, and redistribute the ORs to return
     * a listOfLists interpreted as an OR of ANDS.
     * <p>
     * E.g.: assume that we have the list of literals <br>
     * A1(), A2(), A3() <br>
     * B1(), B2()
     * <p>
     * Such list of literals is interpreted as
     * (A1() AND A2() AND A(3))
     * OR
     * (B1() AND B2())
     * <p>
     * Hence, when negating such expression we obtain:
     * NOT(
     * (A1() AND A2() AND A(3))
     * OR
     * (B1() AND B2())
     * )
     * <p>
     * which is equivalent to (Morgan rules) <br>
     * (NOT(A1()) OR NOT(A2()) OR NOT(A(3)))
     * AND
     * (NOT(B1()) OR NOT(B2()))
     * <p>
     * which is equivalent to (redistributing ORs and ANDs) <br>
     * (NOT(A1) AND NOT(B1()))
     * OR
     * (NOT(A1) AND NOT(B2()))
     * OR
     * (NOT(A2) AND NOT(B1()))
     * OR
     * (NOT(A2) AND NOT(B2()))
     * OR
     * (NOT(A3) AND NOT(B1()))
     * OR
     * (NOT(A3) AND NOT(B2()))
     * <p>
     * At the end, the method consists in negating all literals from all the literals list, and pick
     * one of such literals for each list.
     *
     * @param listOfLists not null, not empty
     * @return a new list of ImmutableLiteralsList after applying the morgan rules
     */
    private List<ImmutableLiteralsList> negateAccordingToMorganRules(List<ImmutableLiteralsList> listOfLists) {
        List<Literal> negatedFirstRule = negateLiterals(listOfLists.get(0));
        if (listOfLists.size() == 1) {
            return negatedFirstRule.stream()
                    .map(ImmutableLiteralsList::new)
                    .toList();
        }
        List<ImmutableLiteralsList> result = new LinkedList<>();
        List<ImmutableLiteralsList> restOfRulesNegated = negateAccordingToMorganRules(listOfLists.subList(1, listOfLists.size()));
        for (Literal literal : negatedFirstRule) {
            result.addAll(addLiteralToAllLists(literal, restOfRulesNegated));
        }
        return result;
    }

    private List<ImmutableLiteralsList> addLiteralToAllLists(Literal literal, List<ImmutableLiteralsList> restOfRulesNegated) {
        List<ImmutableLiteralsList> result = new LinkedList<>();
        for (ImmutableLiteralsList list : restOfRulesNegated) {
            List<Literal> mutableList = new LinkedList<>(list);
            mutableList.add(literal);
            result.add(new ImmutableLiteralsList(mutableList));
        }
        return result;
    }

    /**
     * @param literals not null
     * @return a list of literals where all literals have been negated
     */
    private List<Literal> negateLiterals(ImmutableLiteralsList literals) {
        return literals.stream().map(Literal::buildNegatedLiteral).toList();
    }

    private boolean hasNoExistentialVariableInDerivationRules() {
        return this.getAtom().getPredicate().getDerivationRules().stream()
                .flatMap(dr -> dr.getExistentialVariables().stream())
                .findAny()
                .isEmpty();
    }

    private boolean allLiteralsFromDerivationRuleCanBeNegated() {
        return this.getAtom().getPredicate().getDerivationRules().stream()
                .flatMap(dr -> dr.getBody().stream())
                .allMatch(Literal::canBeNegated);
    }

    @Override
    public String toString() {
        if (isPositive) return atom.toString();
        else return "not(" + atom.toString() + ")";
    }
}
