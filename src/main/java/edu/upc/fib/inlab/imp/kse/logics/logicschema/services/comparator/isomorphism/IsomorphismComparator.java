package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.PredicateComparator;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * Class to recognize whether two list of literals (or derivation rules, or logic constraints) are isomorphic.
 * Two list of literals are isomorphic, iff they have a bijective map respecting:
 * - kind of literal (OrdinaryLiterals can only be mapped to OrdinaryLiterals, the same with BuiltInLiterals, etc).
 * - Polarity of mapped ordinary literals
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
 * <p>
 * Note: the comparison of predicate/term names is case-sensitive.
 */
public class IsomorphismComparator {
    /**
     * This class implements a backtracking search of isomorphisms.
     * Such search is quite tricky. Hence, we first discuss the problem, and then, discuss its current solution.
     * <p>
     * <h2>The problem</h2>
     * Assume the most difficult case. That is, we want to search for an isomorphism between schemas,
     * where we want to permit renaming variables, changing the order of the literals, and changing the name of derived predicates.
     * <p>
     * Coherently, we must backtrack, during the search, with three different things:
     * <ul>
     * <li> Order of NormalClauses (we must try to make isomorphic normalClause1 from schema 1 with normalClause 1,2,3... from schema2)
     * <li> Names of DerivedPredicates (we must try to make isomorphic derivedPredicateName1 from schema 1 with predicatePredicateName 1,2,3... from schema 2)
     * <li> Order of Literals (we must try to make isomorphic literal1 from normalClause1 with literal 1,2,3... from normalClause2)
     * <br> (do not that we do not need to backtrack with terms, because an isomorphism of literals induces an isomorphism of terms.
     * That is, if we map the literal "P(x)" to "P(y)", implicitly, we are defining a terms map "x->y")
     * </ul>
     * <p>
     * A possible (naive) algorithm might have been to blindly backtrack all such mappings. It would have been something like this:
     * </p>
     * <ul>
     * <li> F1: Define an algorithm that tries all possible maps of LogicConstraints from one schema, to the other, and in the base case, invokes F2
     * <li> F2: Define an algorithm that tries all possible maps of DerivedPredicates from one schema, to the other, and in the base case, invokes F3 for each pair of derived predicates
     * <li> F3: Define an algorithm that tries all possible maps of DerivationRules for those derived predicates, and in the base case, invoke F4
     * <li> F4: Define an algorithm that tries all possible maps of Literals from one rule, to the other rule until finding the isomorphism.
     * </ul>
     * <p>
     * This strategy would have been very inefficient since, in essence, it is too blind and tries all possible combinations of almost everything.
     * If the schemas had been too large, such algorithm, probably, would have never work.
     *
     * <h2>The current solution</h2>
     * <h3> The general idea: From top to bottom</h3>
     * We look for a solution where decisions are made on the fly, and propagated from top to bottom.
     * That is, if we decide to make isomorphic the following derivation rules:
     * <ul>
     * <li> Der(x) :- P(x), Q(x)
     * <li> Der(x) :- P'(x), Q'(x)
     * </ul>
     * Do note that, implicitly, we are also deciding to map P to: P' (or Q'), and nothing else. The basic idea, hence,
     * is to propagate such solution downwards. That is, if we decide to map P to P', we must then try to make isomorphic
     * their derivation rules which, in its turn, will decide more predicates mappings, which will make more
     * derivation rules isomorphic, etc. Do note that this is recursive, and somehow, remembers the structure of a tree.
     * <p>
     * The problem is that two derived predicate names cannot be mapped to two different predicate names, and each branch
     * of the tree might try to map the same predicate name to two different predicate names. Consider the following example:
     * <ul>
     * <li> Der(x) :- P(x), Q(x), R(x)
     * <br> P(x) :- A(x)
     * <br> Q(x) :- B(x)
     * <br> R(x) :- A(x)
     * <br> A(x) :- a(x)
     * <br> B(x) :- a(x)
     * <li> Der(x) :- P'(x), Q'(x), R'(x)
     * <br> P'(x) :- B'(x)
     * <br> Q'(x) :- A'(x)
     * <br> R'(x) :- A'(x)
     * <br> A'(x) :- a(x)
     * <br> B'(x) :- a(x)
     * </ul>
     * If we try to map P to P', we are also inducing that A must be mapped to B'.
     * If we try to map Q to Q', we are also inducing that B must be mapped to A'.
     * If we try to map R to R', we are also inducing that A must be mapped to A'.
     * Do note that we have reached a contradiction.
     * <p>
     * Hence, each branch must be aware of the predicate names renaming proposed by other branches.
     * This can be solved by making each branch return its induced predicateMap, and making each branch
     * use the previous inducedMap by the other branch to extend it.
     * <p>
     * The problem gets really difficult when each branch can induce several possible maps. Indeed, we cannot
     * return all the possible induced maps since there might be too many.
     * <p>
     * To avoid retrieving the several possible maps, what we do is to pass a lambda functions during the recursion
     * that, intuitively, checks the remaining part of the tree. That is, when we try to map P to P', and apply a recursive
     * call to make the map of its brothers (Q, and R), we annotate, in the lambda function, that we lack computing
     * the isomorphism of the definition rule of P (that will induce the predicate map A to B').
     * <p>
     * As a result, the recursion tries to map P->P', Q->Q', R->R', and then, reaches a base case where there is no more
     * literals to map. In the base case, the recursion applies the lambdas that contains the remaining jobs. That is,
     * it first checks the derivation rules of P, then the derivation rules of Q, and then the derivation rules of R.
     * If the lambda fails, the algorithm backtracks and checks the remaining part of the tree with another combination.
     */

    private final boolean changeVariableNamesAllowed;
    private final boolean changeLiteralOrderAllowed;
    private final boolean changingDerivedPredicateNameAllowed;

    public IsomorphismComparator(IsomorphismOptions options) {
        this.changeVariableNamesAllowed = options.changeVariableNamesAllowed();
        this.changeLiteralOrderAllowed = options.changeLiteralOrderAllowed();
        this.changingDerivedPredicateNameAllowed = options.changingDerivedPredicateNameAllowed();
    }

    /**
     * Check whether two lists of literals are isomorphic
     *
     * @param literals1 a list of literals
     * @param literals2 a list of literals
     * @return boolean indicating whether the two lists of literals are isomorphic
     */
    public boolean areIsomorphic(List<Literal> literals1, List<Literal> literals2) {
        return areIsomorphic(literals1, literals2, new PredicateMap(), new LiteralMap(), new TermMap(), () -> true);
    }

    /**
     * Check whether two lists of literals are isomorphic without renaming the variable names given by parameter
     *
     * @param literals1           a list of literals
     * @param literals2           a list of literals
     * @param varNamesNotToChange a list of variable names not to change
     * @return boolean indicating whether the two lists of literals are isomorphic
     */
    public boolean areIsomorphic(List<Literal> literals1, List<Literal> literals2, String... varNamesNotToChange) {
        TermMap termMap = new TermMap();
        for (String varName : varNamesNotToChange) {
            Term term = new Variable(varName);
            termMap.put(term, term);
        }
        return areIsomorphic(literals1, literals2, new PredicateMap(), new LiteralMap(), termMap, () -> true);
    }

    /**
     * Check whether two logic constraints are isomorphic
     *
     * @param constraint1 a logic constraint
     * @param constraint2 a logic constraint
     * @return boolean indicating whether the two logic constraints are isomorphic
     */
    public boolean areIsomorphic(LogicConstraint constraint1, LogicConstraint constraint2) {
        return areIsomorphic(constraint1.getBody(), constraint2.getBody());
    }

    /**
     * Check whether two derivation rules are isomorphic
     *
     * @param derivationRule1 a derivation rule
     * @param derivationRule2 a derivation rule
     * @return boolean indicating whether the two derivation rules are isomorphic
     */
    public boolean areIsomorphic(DerivationRule derivationRule1, DerivationRule derivationRule2) {
        Atom head1 = derivationRule1.getHead();
        Atom head2 = derivationRule2.getHead();
        Optional<TermMap> termMap = areDerivationRuleHeadsIsomorphic(head1, head2);
        return termMap
                .filter(map -> areIsomorphic(derivationRule1.getBody(), derivationRule2.getBody(), new PredicateMap(), new LiteralMap(), map, () -> true))
                .isPresent();
    }

    /**
     * Check whether two logic schemas are isomorphic
     *
     * @param schema1 a logic schema
     * @param schema2 a logic schema
     * @return boolean indicating whether the two logic schemas are isomorphic
     */
    public boolean areIsomorphic(LogicSchema schema1, LogicSchema schema2) {
        if (!areBasePredicatesIsomorphic(schema1, schema2)) return false;
        return areNormalClausesIsomorphic(schema1, schema2);
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
     * @param remainingJob a boolean supplier (function without parameter) that must be true when searching the isomorphism
     * @return whether there is an isomorphism between rules1, and rules2, satisfying the predicateMap given and the remainingJob
     */
    private boolean areIsomorphic(List<DerivationRule> rules1, List<DerivationRule> rules2, PredicateMap predicateMap, BooleanSupplier remainingJob) {
        if (rules1.size() != rules2.size()) return false;
        if (rules1.isEmpty()) return remainingJob.getAsBoolean();

        DerivationRule rule1 = rules1.get(0);
        List<DerivationRule> ruleCandidates = getRulesOfSameBodySize(rules2, rule1);
        for (DerivationRule rule2 : ruleCandidates) {
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
     * @param clauses1 not null
     * @param clauses2 not null
     * @param predicateMap not null. When exiting, contains a predicateMap that is compatible with an isomorphism
     *                     between clauses1 and clauses2, if it exists. If there is none, it returns the same
     *                     predicateMap with no modification
     * @param remainingJob not null
     * @return whether there is an isomorphism between clauses1, and clauses2, satisfying the predicateMap given and the remainingJob
     */
    private boolean areIsomorphicNormalClausesLists(List<NormalClause> clauses1, List<NormalClause> clauses2, PredicateMap predicateMap, BooleanSupplier remainingJob) {
        if (clauses1.size() != clauses2.size()) return false;
        if (clauses1.isEmpty()) return remainingJob.getAsBoolean();

        NormalClause clause1 = clauses1.get(0);
        for (NormalClause clause2 : clauses2) {
            Optional<TermMap> termMap = computeInitialTermMapFromClause(clause1, clause2);
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

    /**
     * @param clause1 not null
     * @param clause2 not null
     * @return a termMap that maps the head of clause1 to the head of clause2, if the clauses are derivation rules,
     * or an empty termMap, if the clauses are LogicConstraints. Otherwise, return no termMap.
     */
    private Optional<TermMap> computeInitialTermMapFromClause(NormalClause clause1, NormalClause clause2) {
        if (clause1 instanceof LogicConstraint && clause2 instanceof LogicConstraint) {
            return Optional.of(new TermMap());
        } else if (clause1 instanceof DerivationRule rule1 && clause2 instanceof DerivationRule rule2) {
            return computeTermMap(rule1.getHeadTerms(), rule2.getHeadTerms());
        }
        return Optional.empty();
    }


    /**
     * @param literals1    not null
     * @param literals2    not null
     * @param predicateMap not null. Will contain, when exiting the function, a predicates map
     *                     that is compatible with an isomorphism between literals1 and literals2
     *                     if it exists. If it does not exist, it contains the same predicateMap,
     *                     with no modification.
     * @param termMap      not null
     * @param remainingJob not null
     * @return whether there is isomorphism between literals1, and literals2,
     * respecting the given predicateMap, and termMap, and satisfying the remainingJob
     */
    private boolean areIsomorphic(List<Literal> literals1, List<Literal> literals2, PredicateMap predicateMap, LiteralMap literalMap, TermMap termMap, BooleanSupplier remainingJob) {
        if (literals1.size() != literals2.size()) return false;
        if (literals1.isEmpty()) return remainingJob.getAsBoolean();

        Literal literal1 = literals1.get(0);
        List<IsomorphicLiteral> isomorphicLiteral = obtainIsomorphicLiterals(literal1, literals2, predicateMap, literalMap, termMap);
        for (IsomorphicLiteral literalCandidate : isomorphicLiteral) {
            Optional<Predicate> newAddedPredicateInMap = updatePredicateMap(literal1, literalCandidate.literal(), predicateMap);
            LiteralMap newLiteralMap = computeNewLiteralMap(literal1, literalCandidate.literal(), literalMap);
            List<Literal> newLiterals1 = removeFromLiteralsList(literals1, literal1);
            List<Literal> newLiterals2 = removeFromLiteralsList(literals2, literalCandidate.literal());
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
     * @param literal1 not null
     * @param literal2 not null
     * @param literalMap does not map literal1 neither literal2
     * @return a new literalMap containing the given literal map and a map literal1<->literal2
     */
    private LiteralMap computeNewLiteralMap(Literal literal1, Literal literal2, LiteralMap literalMap) {
        LiteralMap result = new LiteralMap(literalMap);
        result.put(literal1, literal2);
        return result;
    }

    private static Optional<Predicate> updatePredicateMap(Literal literal1, Literal literal2, PredicateMap predicateMap) {
        if (literal1 instanceof OrdinaryLiteral ol1
                && literal2 instanceof OrdinaryLiteral ol2
                && ol1.isDerived()
        ) {
            predicateMap.put(ol1.getPredicate(), ol2.getPredicate());
            return Optional.of(ol1.getPredicate());
        }
        return Optional.empty();
    }

    /**
     * @param p1 not null
     * @param p2 not null
     * @return whether there is an isomorphism between the derivation rules of ol1, and the derivationRules of ol2,
     * satisfying the given predicateName, and remaining job.
     */
    public boolean areIsomorphic(Predicate p1, Predicate p2) {
        if (Objects.isNull(p1)) throw new IllegalArgumentException("p1 cannot be null");
        if (Objects.isNull(p2)) throw new IllegalArgumentException("p2 cannot be null");
        if (p1.getArity() != p2.getArity()) return false;
        if (p1.isDerived() != p2.isDerived()) return false;
        if (this.changingDerivedPredicateNameAllowed && p1.getName().equals(p2.getName())) return false;
        PredicateMap predicateMap = new PredicateMap();
        predicateMap.put(p1, p2);
        return areIsomorphic(p1.getDerivationRules(), p2.getDerivationRules(), predicateMap, () -> true);
    }


    /**
     * @param ol1          not null, and derived
     * @param ol2          not null, and derived
     * @param predicateMap already mapping the predicate of ol1 to the predicate of ol2. After invoking the method,
     *                     it contains a predicateMap compatible
     *                     with an isomorphism between the derivationRules of ol1, and the derivationRules of ol2.
     * @param remainingJob not null
     * @return whether there is an isomorphism between the derivation rules of ol1, and the derivationRules of ol2,
     * satisfying the given predicateName, and remaining job.
     */
    private boolean areSonsIsomorphic(OrdinaryLiteral ol1, OrdinaryLiteral ol2, PredicateMap predicateMap, BooleanSupplier remainingJob) {
        return areIsomorphic(ol1.getPredicate().getDerivationRules(), ol2.getPredicate().getDerivationRules(), predicateMap, remainingJob);
    }

    private List<Literal> removeFromLiteralsList(List<Literal> literals, Literal literal) {
        return literals.stream()
                .filter(l -> l != literal)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * @param rules defines a predicate P2
     * @param rule  defines a predicate P
     * @return all those derivation rules from rules that have the same body size as rule
     */
    private List<DerivationRule> getRulesOfSameBodySize(List<DerivationRule> rules, DerivationRule rule) {
        return rules.stream()
                .filter(candidateRule -> hasSameBodySize(rule, candidateRule))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private static boolean hasSameBodySize(DerivationRule rule1, DerivationRule rule2) {
        return rule2.getBody().size() == rule1.getBody().size();
    }

    /**
     * @param literal      not null
     * @param literalList  not null
     * @param predicateMap not null
     * @param literalMap   not null
     * @param termMap      not null
     * @return all those literals from literalList that are isomorphic to literal satisfying the predicateMap, the literalMap,
     * and the termMap.
     */
    private List<IsomorphicLiteral> obtainIsomorphicLiterals(Literal literal, List<Literal> literalList, PredicateMap predicateMap, LiteralMap literalMap, TermMap termMap) {
        List<IsomorphicLiteral> result = new LinkedList<>();
        if (changeLiteralOrderAllowed) {
            for (Literal candidateLiteral : literalList) {
                List<TermMap> resultTermMap = findTermMapForLiterals(literal, candidateLiteral, predicateMap, literalMap, termMap);
                resultTermMap.forEach(map -> result.add(new IsomorphicLiteral(candidateLiteral, map)));
            }
        } else {
            Literal candidateLiteral = literalList.get(0);
            List<TermMap> resultTermMap = findTermMapForLiterals(literal, candidateLiteral, predicateMap, literalMap, termMap);
            resultTermMap.forEach(map -> result.add(new IsomorphicLiteral(candidateLiteral, map)));
        }
        return result;
    }

    /**
     * @param l1           not null
     * @param l2           not null
     * @param predicateMap not null
     * @param literalMap   not null
     * @param termMap      not null.
     * @return a list of termMap, containing the map given in the input, that makes the terms of l1 isomorphic to the terms of l2,
     * only if l1 can be isomorphic to l2 (e.g., they have the same predicate name, etc). It is a list since "a = b" have two maps with
     * "c = d" (a->b, b->d; a->d, b->c)
     */
    private List<TermMap> findTermMapForLiterals(Literal l1, Literal l2, PredicateMap predicateMap, LiteralMap literalMap, TermMap termMap) {
        if (literalMap.containsInRange(l2)) return List.of();
        if (l1 instanceof OrdinaryLiteral ol1 && l2 instanceof OrdinaryLiteral ol2) {
            return findTermMapForLiterals(ol1, ol2, predicateMap, termMap).stream().toList();
        } else if (l1 instanceof BuiltInLiteral bl1 && l2 instanceof BuiltInLiteral bl2) {
            return findTermMapForLiterals(bl1, bl2, termMap);
        } else if (!l1.getClass().isAssignableFrom(l2.getClass())) {
            return List.of();
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

    private List<TermMap> findTermMapForLiterals(BuiltInLiteral bl1, BuiltInLiteral bl2, TermMap termMap) {
        if (bl1 instanceof ComparisonBuiltInLiteral cbl1 && bl2 instanceof ComparisonBuiltInLiteral cbl2) {
            return findTermMapForLiterals(cbl1, cbl2, termMap);
        } else if (bl1 instanceof BooleanBuiltInLiteral bbl1 && bl2 instanceof BooleanBuiltInLiteral bbl2) {
            return findTermMapForLiterals(bbl1, bbl2, termMap).stream().toList();
        } else if (bl1 instanceof CustomBuiltInLiteral cbl1 && bl2 instanceof CustomBuiltInLiteral cbl2) {
            return findTermMapForLiterals(cbl1, cbl2, termMap).stream().toList();
        } else if (!bl1.getClass().isAssignableFrom(bl2.getClass())) {
            return List.of();
        } else {
            throw new RuntimeException("To be implemented");
        }
    }

    private List<TermMap> findTermMapForLiterals(ComparisonBuiltInLiteral cbl1, ComparisonBuiltInLiteral cbl2, TermMap termMap) {
        List<TermMap> result = new LinkedList<>();
        ComparisonOperator operator1 = cbl1.getOperator();
        ComparisonOperator operator2 = cbl2.getOperator();
        Optional<TermMap> newTermMap = computeNewTermMap(cbl1.getTerms(), cbl2.getTerms(), termMap);
        Optional<TermMap> newReverseTermMap = computeNewTermMap(cbl1.getTerms(), reverseTerms(cbl2.getTerms()), termMap);
        if (operator1.equals(operator2)) {
            newTermMap.ifPresent(result::add);
        }
        if (operator1.isSymmetric(operator2)) {
            newReverseTermMap.ifPresent(result::add);
        }
        return result;
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

    private boolean allContainedIn(List<Predicate> predicateList1, List<Predicate> predicateList2) {
        return predicateList2.stream().allMatch(p -> anyMatchPredicate(predicateList1, p));
    }

    private boolean anyMatchPredicate(List<Predicate> predicateList, Predicate predicate) {
        return predicateList.stream().anyMatch(otherPredicate -> PredicateComparator.hasSameNameAndArityAs(predicate, otherPredicate));
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
