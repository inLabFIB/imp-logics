package edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.PredicateComparator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * Class to recognize whether two list of literals (or derivation rules, or logic constraints) are isomorphic.
 * Two list of literals are isomorphic, iff they have a bijective map respecting:
 * - kind of literal (OrdinaryLiterals can only be mapped to OrdinaryLiterals, the same with BuiltInLiterals, etc).
 * - Polarity of mapped literals
 * - Names of base predicates
 * - the induced mapping of the variables is also an isomorphism (there is a bijective map of terms, where variables
 * are mapped to variables, and constants to the same value constants)
 * <p>
 * This comparator can be customized in the following ways:
 * - changeVariableNamesAllowed: whether the induced map of variables can map variables of different names
 * - changeLiteralOrderAllowed: whether the literals map can map literals occupying different positions of the lists
 * - changeDerivedPredicateNameAllowed: whether the literals map can map derived literals with different predicate names.
 * <p>
 * When allowing to change the name of derived predicates, two derived literals can be mapped iff their derivation rules
 * are isomorphic. In such case, there must also exist a bijective map of derived predicate names (i.e., the same
 * predicate name cannot be mapped twice to two different predicate names, despite all their rules being isomorphic).
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

    public boolean areIsomorphic(ImmutableLiteralsList literals1, ImmutableLiteralsList literals2) {
        return areIsomorphic(literals1, literals2, new PredicateMap(), new LiteralMap(), new TermMap(), () -> true);
    }

    public boolean areIsomorphic(LogicConstraint constraint1, LogicConstraint constraint2) {
        return areIsomorphic(constraint1.getBody(), constraint2.getBody());
    }

    public boolean areIsomorphic(LogicSchema schema1, LogicSchema schema2) {
        if (!areBasePredicatesIsomorphic(schema1, schema2)) return false;
        return areNormalClausesIsomorphic(schema1, schema2);
    }

    public boolean areIsomorphic(DerivationRule dr1, DerivationRule dr2) {
        Atom head1 = dr1.getHead();
        Atom head2 = dr2.getHead();
        Optional<TermMap> termMap = areDerivationRuleHeadsIsomorphic(head1, head2);
        return termMap
                .filter(map -> areIsomorphic(dr1.getBody(), dr2.getBody(), new PredicateMap(), new LiteralMap(), map, () -> true))
                .isPresent();
    }

    private Optional<TermMap> areDerivationRuleHeadsIsomorphic(Atom head1, Atom head2) {
        if (!changingDerivedPredicateNameAllowed && haveDifferentPredicateName(head1, head2)) return Optional.empty();
        return computeTermMap(head1.getTerms(), head2.getTerms());
    }

    private static boolean haveDifferentPredicateName(Atom atom1, Atom atom2) {
        return !atom1.getPredicateName().equals(atom2.getPredicateName());
    }

    /**
     * @param rules1       defines some predicate P1 with arity n
     * @param rules2       defines some predicate P2 with the same arity n
     * @param predicateMap already includes a map between P1 and P2. When exiting the function, it includes, also
     *                     a possible combination of predicate maps that proves such isomorphism.
     * @param remainingJob
     * @return whether there is an isomorphism between rules1, and rules2, satisfying the predicateMap given and the remainingJob
     */
    private boolean areIsomorphic(List<DerivationRule> rules1, List<DerivationRule> rules2, PredicateMap predicateMap, BooleanSupplier remainingJob) {
        if (rules1.size() != rules2.size()) return false;
        if (rules1.isEmpty()) return remainingJob.getAsBoolean();

        DerivationRule rule1 = rules1.get(0);
        for (DerivationRule rule2 : getRuleCandidates(rules2, rule1)) {
            Optional<TermMap> termMap = computeTermMap(rule1.getHeadTerms(), rule2.getHeadTerms());
            if (termMap.isPresent()) {
                boolean bodiesAndRestOfDerivationRulesAreIsomorphic = areIsomorphic(rule1.getBody(), rule2.getBody(), predicateMap, new LiteralMap(), termMap.get(),
                        () -> areIsomorphic(removeFrom(rules1, rule1), removeFrom(rules2, rule2), predicateMap, remainingJob)
                );
                if (bodiesAndRestOfDerivationRulesAreIsomorphic) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param remainingJob
     * @return whether there is an isomorphism between rules1, and rules2, satisfying the predicateMap given and the remainingJob
     */
    private boolean areIsomorphicNormalClausesLists(List<NormalClause> clauses1, List<NormalClause> clauses2, PredicateMap predicateMap, BooleanSupplier remainingJob) {
        if (clauses1.size() != clauses2.size()) return false;
        if (clauses1.isEmpty()) return remainingJob.getAsBoolean();

        NormalClause clause1 = clauses1.get(0);
        for (NormalClause clause2 : getClauseCandidates(clauses2, clause1)) {
            Optional<TermMap> termMap = computeTermMapFromClause(clause1, clause2);
            if (termMap.isPresent()) {
                boolean bodiesAndRestOfNormalClausesAreIsomorphic = areIsomorphic(clause1.getBody(), clause2.getBody(), predicateMap, new LiteralMap(), termMap.get(),
                        () -> areIsomorphicNormalClausesLists(removeFrom(clauses1, clause1), removeFrom(clauses2, clause2), predicateMap, remainingJob)
                );
                if (bodiesAndRestOfNormalClausesAreIsomorphic) {
                    return true;
                }
            }
        }
        return false;
    }

    private Optional<TermMap> computeTermMapFromClause(NormalClause clause1, NormalClause clause2) {
        if (clause1 instanceof LogicConstraint && clause2 instanceof LogicConstraint) {
            return Optional.of(new TermMap());
        } else if (clause1 instanceof DerivationRule rule1 && clause2 instanceof DerivationRule rule2) {
            return computeTermMap(rule1.getHeadTerms(), rule2.getHeadTerms());
        }
        return Optional.empty();
    }

    private <T extends NormalClause> List<T> getClauseCandidates(List<NormalClause> clauseList, T clause) {
        List<T> result = new LinkedList<>();
        for (NormalClause candidate : clauseList) {
            if (candidate instanceof LogicConstraint && clause instanceof LogicConstraint) {
                result.add((T) candidate);
            } else if (candidate instanceof DerivationRule && clause instanceof DerivationRule) {
                result.add((T) candidate);
            }
        }
        return result;
    }

    /**
     * @param literals1
     * @param literals2
     * @param predicateMap will contain, when exiting the function, the predicates map that proves such isomorphism,
     *                     if exists, or the same predicateMap, with no modification, if it does not exist.
     * @param termMap
     * @param remainingJob
     * @return whether there is isomorphism between literals1, and literals2, respecting the given predicateMap, and termMap, and satisfying the remainingJob
     */
    private boolean areIsomorphic(ImmutableLiteralsList literals1, ImmutableLiteralsList literals2, PredicateMap predicateMap, LiteralMap literalMap, TermMap termMap, BooleanSupplier remainingJob) {
        if (literals1.size() != literals2.size()) return false;
        if (literals1.isEmpty()) return remainingJob.getAsBoolean();

        Literal literal1 = literals1.get(0);
        for (LiteralCandidate literalCandidate : getLiteralCandidates(literal1, literals2, predicateMap, literalMap, termMap)) {
            Optional<Predicate> newAddedPredicateInMap = updatePredicateMap(literal1, literalCandidate.literal(), predicateMap);
            LiteralMap newLiteralMap = computeNewLiteralMap(literal1, literalCandidate.literal(), literalMap);
            ImmutableLiteralsList newLiterals1 = removeFromLiteralsList(literals1, literal1);
            ImmutableLiteralsList newLiterals2 = removeFromLiteralsList(literals2, literalCandidate.literal());
            boolean brothersAreIsomorphicRec = areIsomorphic(newLiterals1, newLiterals2, predicateMap, newLiteralMap, literalCandidate.termMap(), () -> {
                if (literal1 instanceof OrdinaryLiteral ol1 && ol1.isDerived() && literalCandidate.literal() instanceof OrdinaryLiteral ol2) {
                    return areSonsIsomorphic(ol1, ol2, predicateMap, remainingJob);
                } else return remainingJob.getAsBoolean();
            });
            if (brothersAreIsomorphicRec) {
                return true;
            }

            newAddedPredicateInMap.ifPresent(predicateMap::removeDomain);
        }
        return false;
    }

    /**
     * @param literal1
     * @param literal2
     * @param literalMap does not map literal1 neither literal2
     * @return a new literalMap containing the given literal map and a map literal1<->literal2
     */
    private LiteralMap computeNewLiteralMap(Literal literal1, Literal literal2, LiteralMap literalMap) {
        LiteralMap result = new LiteralMap(literalMap);
        result.put(literal1, literal2);
        return result;
    }

    private static Optional<Predicate> updatePredicateMap(Literal literal1, Literal literal2, PredicateMap predicateMap) {
        if (literal1 instanceof OrdinaryLiteral ol1 && literal2 instanceof OrdinaryLiteral ol2) {
            if (ol1.isDerived()) {
                predicateMap.put(ol1.getPredicate(), ol2.getPredicate());
                return Optional.of(ol1.getPredicate());
            }
        }
        return Optional.empty();
    }


    private boolean areSonsIsomorphic(OrdinaryLiteral ol1, OrdinaryLiteral ol2, PredicateMap predicateMap, BooleanSupplier remainingJob) {
        return areIsomorphic(ol1.getPredicate().getDerivationRules(), ol2.getPredicate().getDerivationRules(), predicateMap, remainingJob);
    }

    private ImmutableLiteralsList removeFromLiteralsList(ImmutableLiteralsList literals, Literal literal) {
        return new ImmutableLiteralsList(literals.stream()
                .filter(l -> l != literal)
                .collect(Collectors.toCollection(LinkedList::new)));
    }

    /**
     * @param rules defines a predicate P2
     * @param rule  defines a predicate P
     * @return all those derivation rules from rules that might be isomorphic to rule
     */
    private List<DerivationRule> getRuleCandidates(List<DerivationRule> rules, DerivationRule rule) {
        return rules.stream()
                .filter(candidateRule -> hasSameBodySize(rule, candidateRule))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private static boolean hasSameBodySize(DerivationRule rule1, DerivationRule rule2) {
        return rule2.getBody().size() == rule1.getBody().size();
    }

    /**
     * @param literal
     * @param literals
     * @param predicateMap
     * @param literalMap
     * @param termMap
     * @return all those literals from literals that might be isomorphic to literal according to the predicateMap, the termMap, and the literalMap
     */
    private List<LiteralCandidate> getLiteralCandidates(Literal literal, ImmutableLiteralsList literals, PredicateMap predicateMap, LiteralMap literalMap, TermMap termMap) {
        List<LiteralCandidate> result = new LinkedList<>();
        if (changeLiteralOrderAllowed) {
            for (Literal candidateLiteral : literals) {

                Optional<TermMap> resultTermMap = findTermMapForLiterals(literal, candidateLiteral, predicateMap, literalMap, termMap);
                resultTermMap.ifPresent(map -> result.add(new LiteralCandidate(candidateLiteral, map)));

            }
        } else {
            Literal candidateLiteral = literals.get(0);
            Optional<TermMap> resultTermMap = findTermMapForLiterals(literal, candidateLiteral, predicateMap, literalMap, termMap);
            resultTermMap.ifPresent(map -> result.add(new LiteralCandidate(candidateLiteral, map)));

        }
        return result;
    }

    /**
     * @param l1
     * @param l2
     * @param predicateMap
     * @param literalMap
     * @param termMap
     * @return a new termMap, containing the map given in the input, that makes the terms of l1 isomorphic to the terms of l2,
     * only if l1 can be isomorphic to l2 (e.g., they have the same predicate name, etc)
     */
    private Optional<TermMap> findTermMapForLiterals(Literal l1, Literal l2, PredicateMap predicateMap, LiteralMap literalMap, TermMap termMap) {
        if (literalMap.containsInRange(l2)) return Optional.empty();
        if (l1 instanceof OrdinaryLiteral ol1 && l2 instanceof OrdinaryLiteral ol2) {
            return findTermMapForLiterals(ol1, ol2, predicateMap, termMap);
        } else if (l1 instanceof BuiltInLiteral bl1 && l2 instanceof BuiltInLiteral bl2) {
            return findTermMapForLiterals(bl1, bl2, termMap);
        } else if (!l1.getClass().getName().equals(l2.getClass().getName())) {
            return Optional.empty();
        } else {
            throw new RuntimeException("To be implemented");
        }
    }

    /**
     * Precondition:
     * - ol1 should not map to ol2
     *
     * @param ol1 is an ordinary literal
     * @param ol2 is an ordinary literal
     */
    private Optional<TermMap> findTermMapForLiterals(OrdinaryLiteral ol1, OrdinaryLiteral ol2, PredicateMap predicateMap, TermMap termMap) {
        if (haveDifferentArity(ol1, ol2)) return Optional.empty();
        if (ol1.isBase() != ol2.isBase()) return Optional.empty();
        if (ol1.isBase() && haveDifferentNames(ol1, ol2)) return Optional.empty();
        if (ol1.isDerived() && predicateMap.isIncompatibleWithMap(ol1.getPredicate(), ol2.getPredicate()))
            return Optional.empty();
        if (!changingDerivedPredicateNameAllowed && ol1.isDerived() && haveDifferentNames(ol1, ol2))
            return Optional.empty();
        if (haveDifferentPolarity(ol1, ol2)) return Optional.empty();
        return computeNewTermMap(ol1.getTerms(), ol2.getTerms(), termMap);
    }

    private static boolean haveDifferentPolarity(OrdinaryLiteral ol1, OrdinaryLiteral ol2) {
        return ol1.isPositive() != ol2.isPositive();
    }

    private static boolean haveDifferentArity(OrdinaryLiteral ol1, OrdinaryLiteral ol2) {
        return ol1.getArity() != ol2.getArity();
    }

    private static boolean haveDifferentNames(OrdinaryLiteral ol1, OrdinaryLiteral ol2) {
        return !ol1.getPredicateName().equals(ol2.getPredicateName());
    }

    private Optional<TermMap> findTermMapForLiterals(BuiltInLiteral bl1, BuiltInLiteral bl2, TermMap termMap) {
        if (bl1 instanceof ComparisonBuiltInLiteral cbl1 && bl2 instanceof ComparisonBuiltInLiteral cbl2) {
            return findTermMapForLiterals(cbl1, cbl2, termMap);
        } else if (bl1 instanceof BooleanBuiltInLiteral bbl1 && bl2 instanceof BooleanBuiltInLiteral bbl2) {
            return findTermMapForLiterals(bbl1, bbl2, termMap);
        } else if (bl1 instanceof CustomBuiltInLiteral cbl1 && bl2 instanceof CustomBuiltInLiteral cbl2) {
            return findTermMapForLiterals(cbl1, cbl2, termMap);
        } else if (!bl1.getClass().getName().equals(bl2.getClass().getName())) {
            return Optional.empty();
        } else {
            throw new RuntimeException("To be implemented");
        }
    }

    private Optional<TermMap> findTermMapForLiterals(ComparisonBuiltInLiteral cbl1, ComparisonBuiltInLiteral cbl2, TermMap termMap) {
        ComparisonOperator operator1 = cbl1.getOperator();
        ComparisonOperator operator2 = cbl2.getOperator();
        Optional<TermMap> newTermMap = computeNewTermMap(cbl1.getTerms(), cbl2.getTerms(), termMap);
        Optional<TermMap> newReverseTermMap = computeNewTermMap(cbl1.getTerms(), reverseTerms(cbl2.getTerms()), termMap);
        if (operator1.equals(operator2)) {
            if (ComparisonOperator.EQUALS.equals(operator1) || ComparisonOperator.NOT_EQUALS.equals(operator1)) {
                return newTermMap.isPresent() ? newTermMap : newReverseTermMap;
            } else {
                return newTermMap;
            }
        } else if (operator1.isSymmetric(operator2)) {
            return newReverseTermMap;
        }
        return Optional.empty();
    }

    private static ImmutableTermList reverseTerms(ImmutableTermList terms2) {
        LinkedList<Term> auxTermsToReverse = new LinkedList<>(terms2);
        Collections.reverse(auxTermsToReverse);
        return new ImmutableTermList(auxTermsToReverse);
    }

    private Optional<TermMap> findTermMapForLiterals(BooleanBuiltInLiteral bbl1, BooleanBuiltInLiteral bbl2, TermMap termMap) {
        if (bbl1.isTrue() && bbl2.isTrue()) return Optional.of(new TermMap(termMap));
        if (bbl1.isFalse() && bbl2.isFalse()) return Optional.of(new TermMap(termMap));
        return Optional.empty();
    }

    private Optional<TermMap> findTermMapForLiterals(CustomBuiltInLiteral cbl1, CustomBuiltInLiteral cbl2, TermMap termMap) {
        if (!cbl1.getOperationName().equals(cbl2.getOperationName())) return Optional.empty();
        return computeNewTermMap(cbl1.getTerms(), cbl2.getTerms(), termMap);
    }

    private Optional<TermMap> computeTermMap(ImmutableTermList terms1, ImmutableTermList terms2) {
        return computeNewTermMap(terms1, terms2, new TermMap());
    }

    private Optional<TermMap> computeNewTermMap(ImmutableTermList terms1, ImmutableTermList terms2, TermMap termMap) {
        if (terms1.size() != terms2.size()) return Optional.empty();
        TermMap newTermMap = new TermMap(termMap);
        for (int i = 0; i < terms1.size(); i++) {
            Term term1 = terms1.get(i);
            Term term2 = terms2.get(i);
            if (changeVariableNamesAllowed) {
                if (newTermMap.isIncompatibleWith(term1, term2)) return Optional.empty();
            } else {
                if (!term1.getName().equals(term2.getName())) return Optional.empty();
            }
            newTermMap.put(term1, term2);
        }
        return Optional.of(newTermMap);
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
        List<NormalClause> normalClauses1 = new LinkedList<>(schema1.getAllNormalClauses());
        List<NormalClause> normalClauses2 = new LinkedList<>(schema2.getAllNormalClauses());
        return areIsomorphicNormalClausesLists(normalClauses1, normalClauses2, new PredicateMap(), () -> true);
    }

    private <T extends NormalClause> List<T> removeFrom(List<T> clauses, T clause) {
        List<T> result = new LinkedList<>(clauses);
        result.remove(clause);
        return result;
    }

}
