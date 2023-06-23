package edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.PredicateComparator;

import java.util.*;

/**
 * Order of derivation rules of a predicate is not important.
 */
public class IsomorphismComparator {

    private final boolean changeVariableNamesAllowed;
    private final boolean changeLiteralOrderAllowed;
    private final boolean changingDerivedPredicateNameAllowed;

    public IsomorphismComparator(boolean changeVariableNamesAllowed, boolean changeLiteralOrderAllowed, boolean changingDerivedPredicateNameAllowed) {
        this.changeVariableNamesAllowed = changeVariableNamesAllowed;
        this.changeLiteralOrderAllowed = changeLiteralOrderAllowed;
        this.changingDerivedPredicateNameAllowed = changingDerivedPredicateNameAllowed;
    }

    public boolean areIsomorphic(DerivationRule dr1, DerivationRule dr2) {
        return new DerivedPredicateIsomorphism(changeLiteralOrderAllowed, changeVariableNamesAllowed, changingDerivedPredicateNameAllowed)
                .areIsomorphic(dr1, dr2);
    }


    public boolean areIsomorphic(ImmutableLiteralsList literals1, ImmutableLiteralsList literals2) {
        LiteralIsomorphism literalIsomorphism = newLiteralIsomorphism();
        return computeIsomorphismRecursive(literals1, literals2, literalIsomorphism).isPresent();
    }


    Optional<LiteralIsomorphism> computeIsomorphismRecursive(ImmutableLiteralsList literals1, ImmutableLiteralsList literals2, LiteralIsomorphism literalIsomorphism) {
        if (literals1.size() != literals2.size()) return Optional.empty();
        if (literals1.isEmpty()) return Optional.of(newLiteralIsomorphism());

        Literal l1 = literals1.get(0);
        List<Literal> secondLiteralCandidates = obtainLiteralCandidates(l1, literals2, literalIsomorphism);
        for (Literal l2 : secondLiteralCandidates) {
            LiteralIsomorphism newLiteralIsomorphism = createNewIsomorphism(l1, l2, literalIsomorphism);
            Optional<LiteralIsomorphism> isomorphicRecursive = computeIsomorphismRecursive(newListRemovingLiteral(literals1, l1), newListRemovingLiteral(literals2, l2), newLiteralIsomorphism);
            if (isomorphicRecursive.isPresent()) return isomorphicRecursive;
        }
        return Optional.empty();
    }

    private List<Literal> obtainLiteralCandidates(Literal l1, ImmutableLiteralsList literals2, LiteralIsomorphism literalIsomorphism) {
        List<Literal> secondLiteralCandidates = new ArrayList<>();
        if (changeLiteralOrderAllowed) {
            for (Literal l2 : literals2) {
                if (literalIsomorphism.canBeIsomorphic(l1, l2)) {
                    secondLiteralCandidates.add(l2);
                }
            }
        } else {
            Literal l2 = literals2.get(0);
            if (literalIsomorphism.canBeIsomorphic(l1, l2)) {
                secondLiteralCandidates.add(l2);
            }
        }
        return secondLiteralCandidates;
    }

    private LiteralIsomorphism createNewIsomorphism(Literal l1, Literal l2, LiteralIsomorphism literalIsomorphism) {
        LiteralIsomorphism newLiteralIsomorphism = newLiteralIsomorphism(literalIsomorphism);
        newLiteralIsomorphism.add(l1, l2);
        return newLiteralIsomorphism;
    }

    private ImmutableLiteralsList newListRemovingLiteral(ImmutableLiteralsList literals, Literal l) {
        List<Literal> newLiteralList = new ArrayList<>(literals);
        newLiteralList.remove(l);
        return new ImmutableLiteralsList(newLiteralList);
    }


    private LiteralIsomorphism newLiteralIsomorphism() {
        return new LiteralIsomorphism(changeVariableNamesAllowed, changeLiteralOrderAllowed, changingDerivedPredicateNameAllowed);
    }

    private LiteralIsomorphism newLiteralIsomorphism(LiteralIsomorphism literalIsomorphism) {
        return new LiteralIsomorphism(literalIsomorphism);
    }

    public boolean areIsomorphic(LogicConstraint constraint1, LogicConstraint constraint2) {
        return areIsomorphic(constraint1.getBody(), constraint2.getBody());
    }

    public boolean areIsomorphic(LogicSchema schema1, LogicSchema schema2) {
        if (!areBasePredicatesIsomorphic(schema1, schema2)) return false;
        return areNormalClausesIsomorphic(schema1, schema2);
    }

    private boolean areBasePredicatesIsomorphic(LogicSchema schema1, LogicSchema schema2) {
        List<Predicate> basePredicates1 = schema1.getAllPredicates().stream().filter(Predicate::isBase).toList();
        List<Predicate> basePredicates2 = schema2.getAllPredicates().stream().filter(Predicate::isBase).toList();
        return allContainedIn(basePredicates2, basePredicates1) && allContainedIn(basePredicates1, basePredicates2);
    }

    private boolean allContainedIn(List<Predicate> basePredicates1, List<Predicate> basePredicates2) {
        return basePredicates2.stream().allMatch(p -> anyMatchPredicate(basePredicates1, p));
    }

    private boolean anyMatchPredicate(List<Predicate> basePredicates, Predicate predicate) {
        return basePredicates.stream().anyMatch(otherPredicate -> PredicateComparator.hasSameNameAndArityAs(predicate, otherPredicate));
    }

    private boolean areNormalClausesIsomorphic(LogicSchema schema1, LogicSchema schema2) {
        if (!areLogicConstraintsIsomorphic(schema1.getAllLogicConstraints(), schema2.getAllLogicConstraints()))
            return false;
        return !areDerivationRulesIsomorphic(schema1.getAllDerivationRules(), schema2.getAllDerivationRules());
    }

    private boolean areLogicConstraintsIsomorphic(Set<LogicConstraint> constraints1, Set<LogicConstraint> constraints2) {
        if (constraints1.size() != constraints2.size()) return false;
        if (constraints1.isEmpty()) return true;

        LogicConstraint constraint1 = constraints1.iterator().next();
        for (LogicConstraint constraint2 : constraints2) {
            if (areIsomorphic(constraint1, constraint2)) {
                Set<LogicConstraint> newConstraints1 = removeFrom(constraints1, constraint1);
                Set<LogicConstraint> newConstraints2 = removeFrom(constraints2, constraint2);
                if (areLogicConstraintsIsomorphic(newConstraints1, newConstraints2)) return true;
            }
        }
        return false;
    }

    private boolean areDerivationRulesIsomorphic(Set<DerivationRule> rules1, Set<DerivationRule> rules2) {
        if (rules1.size() != rules2.size()) return false;
        if (rules1.isEmpty()) return true;

        DerivationRule rule1 = rules1.stream().findFirst().orElseThrow();
        for (DerivationRule rule2 : rules2) {
            if (areIsomorphic(rule1, rule2)) {
                Set<DerivationRule> newRules1 = removeFrom(rules1, rule1);
                Set<DerivationRule> newRules2 = removeFrom(rules2, rule2);
                if (areDerivationRulesIsomorphic(newRules1, newRules2)) return true;
            }
        }
        return false;
    }

    private <T extends NormalClause> Set<T> removeFrom(Set<T> clauses, T clause) {
        Set<T> result = new LinkedHashSet<>(clauses);
        result.remove(clause);
        return result;
    }

}
