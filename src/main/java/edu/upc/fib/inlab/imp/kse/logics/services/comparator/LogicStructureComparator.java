package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;

import java.util.List;
import java.util.Objects;

/**
 * This class is responsible to determine whether two list of literals (or derivation rules, or logic constraints)
 * have the same structure, or not.
 * <p>
 * Two list of literals are considered to have the same structure if they have the very same literals (i.e., same
 * predicate name in case of OrdinaryLiterals, or same operation name in case of built-in literals), in their very
 * same order, and with the very same term names.
 * <p>
 * This class ignores the schemas where the predicate belong to. That is, two base ordinary literals are considered
 * to be the same if their predicate names coincide, and their term names coincide, despite the predicates being from
 * different schemas. Hence, this comparator is useful for comparing the structures of two different schemas.
 * <p>
 * The comparator might apply a recursive check of each comparison, or not. Recursion affects derived literals.
 * In particular, when checking whether two list of literals have the same structure recursively consists in checking
 * not only the structure of these list of literals, but also checking the structure of the underlying derivation rules.
 * <p>
 * When applying non-recursive checks, two literals "P(x)", where one is base, and the other derived, are determined
 * to have the same structure, since the comparator will not check their derivation rules.
 */
public class LogicStructureComparator {

    private final boolean recursively;

    public LogicStructureComparator(boolean recursive) {
        this.recursively = recursive;
    }

    public LogicStructureComparator() {
        this(false);
    }

    /**
     * @param first  not null logic constraint
     * @param second not null logic constraint
     * @return whether both logic constraints have the same structure. ConstraintIDs are ignored in the check
     */
    public boolean haveSameStructure(LogicConstraint first, LogicConstraint second) {
        if (Objects.isNull(first)) throw new IllegalArgumentException("First rule cannot be null");
        if (Objects.isNull(second)) throw new IllegalArgumentException("Second rule cannot be null");
        return haveSameStructure(first.getBody(), second.getBody());
    }

    /**
     * @param first  not null derivation rule
     * @param second not null derivation rule
     * @return whether both derivation rules have the same structure
     */
    public boolean haveSameStructure(DerivationRule first, DerivationRule second) {
        if (Objects.isNull(first)) throw new IllegalArgumentException("First rule cannot be null");
        if (Objects.isNull(second)) throw new IllegalArgumentException("second rule cannot be null");
        return haveSameStructure(first.getHead(), second.getHead()) &&
                haveSameStructure(first.getBody(), second.getBody());
    }

    /**
     * @param first  not null literal list
     * @param second not null literal list
     * @return whether both list of literals have the same structure
     */
    public boolean haveSameStructure(List<Literal> first, List<Literal> second) {
        if (recursively) return haveSameStructureRecursively(first, second);
        else return haveSameStructureNonRecursive(first, second);
    }

    /**
     * @param first  not null literal list
     * @param second not null literal list
     * @return whether both list of literals have the same structure, non recursively
     */
    public boolean haveSameStructureNonRecursive(List<Literal> first, List<Literal> second) {
        if (Objects.isNull(first)) throw new IllegalArgumentException("First literals cannot be null");
        if (Objects.isNull(second)) throw new IllegalArgumentException("Second literals cannot be null");
        if (first.size() != second.size()) return false;

        boolean allSameStructure = true;
        for (int i = 0; i < first.size() && allSameStructure; ++i) {
            Literal firstLiteral = first.get(i);
            Literal secondLiteral = second.get(i);
            allSameStructure = haveSameStructure(firstLiteral, secondLiteral);
        }

        return allSameStructure;
    }

    /**
     * @param first  not null literal list
     * @param second not null literal list
     * @return whether both list of literals have the same structure, recursively
     */
    public boolean haveSameStructureRecursively(List<Literal> first, List<Literal> second) {
        if (Objects.isNull(first)) throw new IllegalArgumentException("First literals cannot be null");
        if (Objects.isNull(second)) throw new IllegalArgumentException("Second literals cannot be null");
        if (!haveSameStructureNonRecursive(first, second)) return false;

        boolean allSame = true;
        for (int i = 0; i < first.size() && allSame; ++i) {
            Literal firstLiteral = first.get(i);
            Literal secondLiteral = second.get(i);
            allSame = haveSameDefinitionRulesRecursively(firstLiteral, secondLiteral);
        }

        return allSame;
    }

    private boolean haveSameStructure(Literal firstLiteral, Literal secondLiteral) {
        if (firstLiteral instanceof OrdinaryLiteral firstOLit) {
            if (secondLiteral instanceof OrdinaryLiteral secondOLit) {
                return haveSameStructure(firstOLit, secondOLit);
            } else return false;
        } else if (firstLiteral instanceof BuiltInLiteral firstBIL) {
            if (secondLiteral instanceof BuiltInLiteral secondBIL) {
                return haveSameStructure(firstBIL, secondBIL);
            }
        }
        return false;
    }

    private boolean haveSameDefinitionRulesRecursively(Literal firstLiteral, Literal secondLiteral) {
        if (firstLiteral instanceof OrdinaryLiteral firstOrdinaryLiteral) {
            if (secondLiteral instanceof OrdinaryLiteral secondOrdinaryLiteral) {
                List<DerivationRule> derivationRules1 = firstOrdinaryLiteral.getAtom().getPredicate().getDerivationRules();
                List<DerivationRule> derivationRules2 = secondOrdinaryLiteral.getAtom().getPredicate().getDerivationRules();
                return haveSameDefinitionRulesRecursively(derivationRules1, derivationRules2);
            } else return false;
        } else if (firstLiteral instanceof BuiltInLiteral) {
            return secondLiteral instanceof BuiltInLiteral;
        } else throw new RuntimeException("Unrecognized firstLiteral type: " + firstLiteral.getClass().getName());
    }

    private boolean haveSameStructure(BuiltInLiteral firstBIL, BuiltInLiteral secondBIL) {
        return haveSameOperationName(firstBIL, secondBIL) &&
                haveSameTermsStructure(firstBIL.getTerms(), secondBIL.getTerms());
    }

    private boolean haveSameStructure(OrdinaryLiteral firstOLit, OrdinaryLiteral secondOLit) {
        return haveSameStructure(firstOLit.getAtom(), secondOLit.getAtom()) &&
                firstOLit.isPositive() == secondOLit.isPositive();
    }

    private boolean haveSameStructure(Atom firstAtom, Atom secondAtom) {
        return firstAtom.getPredicateName().equals(secondAtom.getPredicateName()) &&
                haveSameTermsStructure(firstAtom.getTerms(), secondAtom.getTerms());
    }

    private boolean haveSameOperationName(BuiltInLiteral firstBIL, BuiltInLiteral secondBIL) {
        return firstBIL.getOperationName().equals(secondBIL.getOperationName());
    }

    private boolean haveSameTermsStructure(List<Term> firstTerms, List<Term> secondTerms) {
        if (firstTerms.size() != secondTerms.size()) return false;

        boolean allSameStructure = true;
        for (int i = 0; i < firstTerms.size() && allSameStructure; ++i) {
            Term term1 = firstTerms.get(i);
            Term term2 = secondTerms.get(i);
            allSameStructure = haveSameTermStructure(term1, term2);
        }

        return allSameStructure;
    }

    private boolean haveSameTermStructure(Term term1, Term term2) {
        return term1.isVariable() == term2.isVariable() &&
                term1.getName().equals(term2.getName());
    }


    private boolean haveSameDefinitionRulesRecursively(List<DerivationRule> derivationRules1, List<DerivationRule> derivationRules2) {
        for (DerivationRule rule1 : derivationRules1) {
            if (isNotContainedInRecursively(rule1, derivationRules2)) return false;
        }
        for (DerivationRule rule2 : derivationRules2) {
            if (isNotContainedInRecursively(rule2, derivationRules1)) return false;
        }
        return true;
    }

    private boolean isNotContainedInRecursively(DerivationRule rule, List<DerivationRule> derivationRules) {
        return !isContainedInRecursively(rule, derivationRules);
    }

    private boolean isContainedInRecursively(DerivationRule rule, List<DerivationRule> derivationRules) {
        for (DerivationRule ruleFromList : derivationRules) {
            if (haveSameStructure(rule, ruleFromList)) return true;
        }
        return false;
    }


}
