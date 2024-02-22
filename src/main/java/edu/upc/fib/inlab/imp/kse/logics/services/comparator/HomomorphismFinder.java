package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.exceptions.DerivedLiteralInHomomorphismCheck;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.exceptions.SubstitutionException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * <p> This class is responsible for checking whether there is an homomorphism between two NormalClauses.
 * The homomorphism is agnostic from the schemas of the predicates. That is, two NormalClauses from different
 * logic schemas (and hence, using different predicate objects) might be homomorphic if there is an homomorphism
 * between the literals with the same predicate name. </p>
 *
 * <p>
 * This class can only search for homomorphisms between list of literals (or derivation rules, or logic constraints)
 * which do not have derived literals in their bodies. This is because the traditional notion of homomorphism is
 * only defined over base literals. It can handle, however, negated ordinary literals and built-in literals.
 * </p>
 *
 * <p>
 * If you want to search an homomorphism considering, also, derived literals, you can inject a DerivedOrdinaryLiteralHomomorphismCriteria
 * and define its behavior.
 * </p>
 *
 * <p>
 * It is worth to mention that the finder can find homomorphisms between built-in literals with their symmetric
 * comparison operations.
 * E.g. the finder can realize that "a < b" is homomorphic to "b > a".
 * However, do note that the finder will assert that there is no homomorphism from "P(x,x)" to "P(x, y), x=y".</p>
 */
public class HomomorphismFinder {
    /*
     * This class is implemented by means of computing, and extending, an initial substitution.
     *
     * Such initial substitution is determined by the head of the normal clause. Hence, such initial substitution is:
     * - empty: when checking logic constraints
     * - a substitution that unifies the heads of one logic derivation rule to the other: when checking logic derivation rules
     *
     * From here, we extend the initial substitution to make the body of one normal clause be contained in the body
     * of the second normal clause.
     * As expected, we say that a substitution s1 is an extension of a substitution s2 if all the mappings of s2 are contained in s1.
     */

    private final DerivedOrdinaryLiteralHomomorphismCriteria derivedOrdinaryLiteralHomomorphismCriteria;

    public HomomorphismFinder() {
        this(null);
    }

    public HomomorphismFinder(DerivedOrdinaryLiteralHomomorphismCriteria derivedOrdinaryLiteralHomomorphismCriteria) {
        this.derivedOrdinaryLiteralHomomorphismCriteria = derivedOrdinaryLiteralHomomorphismCriteria;
    }

    /**
     * Return a homomorphism from the domainRule terms to the rangeRule terms, if exists
     *
     * @param domainRule not null, nor containing derived literals
     * @param rangeRule  not null, nor containing derived literals
     * @return optional containing a homomorphism between the two, if exists
     */
    public Optional<Substitution> findHomomorphism(DerivationRule domainRule, DerivationRule rangeRule) {
        if (isNull(domainRule)) throw new IllegalArgumentException("DomainRule cannot be null");
        if (isNull(rangeRule)) throw new IllegalArgumentException("RangeRule cannot be null");
        checkIfExistDerivedOrdinaryLiteralWithoutDerivedLiteralCriteria(domainRule.getBody());
        checkIfExistDerivedOrdinaryLiteralWithoutDerivedLiteralCriteria(rangeRule.getBody());

        Optional<Substitution> homomorphism = findHomomorphismForHead(domainRule.getHead(), rangeRule.getHead());
        if (homomorphism.isEmpty()) return Optional.empty();
        return computeHomomorphismExtensionForLiteralsList(homomorphism.get(), domainRule.getBody(), rangeRule.getBody());
    }

    /**
     * Return a homomorphism from the domainLogicConstraint terms to the rangeLogicConstraint terms, if exists
     *
     * @param domainLogicConstraint not null, nor containing derived literals
     * @param rangeLogicConstraint  not null, nor containing derived literals
     * @return optional containing a homomorphism between the two, if exists
     */
    public Optional<Substitution> findHomomorphism(LogicConstraint domainLogicConstraint, LogicConstraint rangeLogicConstraint) {
        if (isNull(domainLogicConstraint)) throw new IllegalArgumentException("DomainLiterals cannot be null");
        if (isNull(rangeLogicConstraint)) throw new IllegalArgumentException("RangeLiterals cannot be null");
        return findHomomorphism(domainLogicConstraint.getBody(), rangeLogicConstraint.getBody());
    }

    /**
     * @param domainLiterals is not null, but might be empty. Does not contain derived literals
     * @param rangeLiterals  is not null, neither contains derived literals
     * @return a substitution, if exists, that would make domainLiterals to be contained
     * in rangeLiterals
     */
    public Optional<Substitution> findHomomorphism(List<Literal> domainLiterals, List<Literal> rangeLiterals) {
        return this.findHomomorphism(domainLiterals, rangeLiterals, new Substitution());
    }

    /**
     * @param domainLiterals      is not null, but might be empty. Does not contain derived literals
     * @param rangeLiterals       is not null, neither contains derived literals
     * @param initialSubstitution is not null
     * @return a new substitution containing the given one, if exists, that would make domainLiterals to be contained
     * in rangeLiterals
     */
    public Optional<Substitution> findHomomorphism(List<Literal> domainLiterals, List<Literal> rangeLiterals, Substitution initialSubstitution) {
        if (isNull(domainLiterals)) throw new IllegalArgumentException("DomainLiterals cannot be null");
        if (isNull(rangeLiterals)) throw new IllegalArgumentException("RangeLiterals cannot be null");
        if (isNull(initialSubstitution)) throw new IllegalArgumentException("InitialSubstitution cannot be null");
        checkIfExistDerivedOrdinaryLiteralWithoutDerivedLiteralCriteria(domainLiterals);
        checkIfExistDerivedOrdinaryLiteralWithoutDerivedLiteralCriteria(rangeLiterals);

        return computeHomomorphismExtensionForLiteralsList(initialSubstitution, new ImmutableLiteralsList(domainLiterals), new ImmutableLiteralsList(rangeLiterals));
    }

    private Optional<Substitution> findHomomorphismForHead(Atom domainHead, Atom rangeHead) {
        return computeHomomorphismExtensionForAtom(new Substitution(), domainHead, rangeHead);
    }

    public Optional<Substitution> findHomomorphismForTerms(ImmutableTermList domainTerms, ImmutableTermList rangeTerms) {
        return computeHomomorphismExtensionForTerms(new Substitution(), domainTerms, rangeTerms);
    }

    private void checkIfExistDerivedOrdinaryLiteralWithoutDerivedLiteralCriteria(List<Literal> literals) {
        if (nonNull(derivedOrdinaryLiteralHomomorphismCriteria)) return;
        if (literals.stream()
                .filter(OrdinaryLiteral.class::isInstance)
                .map(OrdinaryLiteral.class::cast)
                .anyMatch(OrdinaryLiteral::isDerived)) throw new DerivedLiteralInHomomorphismCheck();
    }

    /**
     * @param currentSubstitution is not null
     * @param domainLiterals      is not null, but might be empty
     * @param rangeLiterals       is not null
     * @return an extension of the currentSubstitution, if exists, that would make domainLiterals to be contained
     * in rangeLiterals
     */
    protected Optional<Substitution> computeHomomorphismExtensionForLiteralsList(Substitution currentSubstitution, List<Literal> domainLiterals, ImmutableLiteralsList rangeLiterals) {
        if (domainLiterals.isEmpty()) return Optional.of(currentSubstitution);
        else {
            Literal domainLiteral = domainLiterals.get(0);
            List<Substitution> possibleSubstitutionsForDomainLiteral = computeAllPossibleHomomorphismsExtensions(currentSubstitution, domainLiteral, rangeLiterals);
            for (Substitution possibleSubstitutionForDomainLiteral : possibleSubstitutionsForDomainLiteral) {
                ImmutableLiteralsList restOfDomainLiterals = new ImmutableLiteralsList(domainLiterals.subList(1, domainLiterals.size()));
                Optional<Substitution> homomorphism = computeHomomorphismExtensionForLiteralsList(possibleSubstitutionForDomainLiteral, restOfDomainLiterals, rangeLiterals);
                if (homomorphism.isPresent()) return homomorphism;
            }
            return Optional.empty();
        }
    }

    /**
     * @param currentSubstitution is not null
     * @param domainLiteral       is not null
     * @param rangeLiterals       is not null
     * @return a list of extensions of the currentSubstitution that makes domainLiteral to be included in rangeLiterals
     */
    private List<Substitution> computeAllPossibleHomomorphismsExtensions(Substitution currentSubstitution, Literal domainLiteral, ImmutableLiteralsList rangeLiterals) {
        List<Substitution> allPossibleHomomorphisms = new LinkedList<>();
        for (Literal rangeLiteral : rangeLiterals) {
            List<Substitution> homomorphisms = computeHomomorphismExtensionForLiteral(currentSubstitution, domainLiteral, rangeLiteral);
            allPossibleHomomorphisms.addAll(homomorphisms);
        }
        return allPossibleHomomorphisms;
    }

    /**
     * It is a list because built-in literals might generate several homomorphism. E.g. "a = b" and "x = y" generates
     * the homomorphisms "{a->x, b->y}" and "{a->y, b->x}".
     *
     * @param currentSubstitution is not null
     * @param domainLiteral       is not null
     * @param rangeLiteral        is not null
     * @return a list of extension of the currentSubstitution that makes domainLiteral to be equal to rangeLiteral, if exists
     */
    private List<Substitution> computeHomomorphismExtensionForLiteral(Substitution currentSubstitution, Literal domainLiteral, Literal rangeLiteral) {
        List<Substitution> result = new LinkedList<>();
        if (domainLiteral instanceof OrdinaryLiteral domainOrdinaryLiteral) {
            if (rangeLiteral instanceof OrdinaryLiteral rangeOrdinaryLiteral) {
                Optional<Substitution> substitution = computeHomomorphismExtensionForOrdinaryLiteral(currentSubstitution, domainOrdinaryLiteral, rangeOrdinaryLiteral);
                substitution.ifPresent(result::add);
            }
        } else if (domainLiteral instanceof BuiltInLiteral domainBuiltInLiteral) {
            if (rangeLiteral instanceof BuiltInLiteral rangeBuiltInLiteral) {
                result.addAll(computeHomomorphismExtensionForBuiltInLiteral(currentSubstitution, domainBuiltInLiteral, rangeBuiltInLiteral));
            }
        } else throw new RuntimeException("Unrecognized literal " + domainLiteral.getClass().getName());
        return result;
    }

    /**
     * @param currentSubstitution  is not null
     * @param domainBuiltInLiteral is not null
     * @param rangeBuiltInLiteral  is not null
     * @return an extension of the currentSubstitution that makes domainBuiltInLiteral to be equal to rangeBuiltInLiteral,
     * or its symmetric, if exists (e.g. "a < 1" is not homomorphic to "1 > b", but, it is with the symmetric "b < 1")
     */
    private List<Substitution> computeHomomorphismExtensionForBuiltInLiteral(Substitution currentSubstitution, BuiltInLiteral domainBuiltInLiteral, BuiltInLiteral rangeBuiltInLiteral) {
        List<Substitution> result = new LinkedList<>();
        if (domainBuiltInLiteral.getOperationName().equals(rangeBuiltInLiteral.getOperationName())) {
            Optional<Substitution> substitution = computeHomomorphismExtensionForTerms(currentSubstitution, domainBuiltInLiteral.getTerms(), rangeBuiltInLiteral.getTerms());
            substitution.ifPresent(result::add);
        }
        if (domainBuiltInLiteral instanceof ComparisonBuiltInLiteral domainComparison &&
                rangeBuiltInLiteral instanceof ComparisonBuiltInLiteral rangeComparison) {
            Optional<Substitution> substitution = computeHomomorphismExtensionForSymmetricBuiltInLiteral(currentSubstitution, domainComparison, rangeComparison);
            substitution.ifPresent(result::add);
        }
        return result;
    }

    /**
     * The symmetric built-in literal of a built-in literals "a < b" is "b > a". Similarly, we would define the symmetric built-in literal
     * for the built-in literals using the comparison operators =, <, <=, >=, >, <>
     *
     * @param currentSubstitution is not null
     * @param domainComparison    is not null
     * @param rangeComparison     is not null
     * @return an extension of the currentSubstitution that makes domainComparison to be equal to the symmetric rangeComparison, if it exists
     */
    private Optional<Substitution> computeHomomorphismExtensionForSymmetricBuiltInLiteral(Substitution currentSubstitution, ComparisonBuiltInLiteral domainComparison, ComparisonBuiltInLiteral rangeComparison) {
        ComparisonOperator domainOperator = domainComparison.getOperator();
        ComparisonOperator rangeOperator = rangeComparison.getOperator();
        if (domainOperator.isSymmetric(rangeOperator)) {
            ImmutableTermList reversedRangeTerms = new ImmutableTermList(rangeComparison.getRightTerm(), rangeComparison.getLeftTerm());
            return computeHomomorphismExtensionForTerms(currentSubstitution, domainComparison.getTerms(), reversedRangeTerms);
        } else return Optional.empty();
    }

    /**
     * @param currentSubstitution is not null
     * @param domainLiteral       is not null
     * @param rangeLiteral        is not null
     * @return an extension of the currentSubstitution that makes domainLiteral to be equal to rangeLiteral, if exists
     */
    protected Optional<Substitution> computeHomomorphismExtensionForOrdinaryLiteral(Substitution currentSubstitution, OrdinaryLiteral domainLiteral, OrdinaryLiteral rangeLiteral) {
        if (domainLiteral.isBase() && rangeLiteral.isBase()) {
            if (domainLiteral.isPositive() != rangeLiteral.isPositive()) return Optional.empty();
            return computeHomomorphismExtensionForAtom(currentSubstitution, domainLiteral.getAtom(), rangeLiteral.getAtom());
        } else {
            if (isNull(derivedOrdinaryLiteralHomomorphismCriteria)) throw new DerivedLiteralInHomomorphismCheck();
            return derivedOrdinaryLiteralHomomorphismCriteria
                    .computeHomomorphismExtensionForDerivedOrdinaryLiteral(this, currentSubstitution, domainLiteral, rangeLiteral);
        }
    }

    /**
     * @param currentSubstitution is not null
     * @param domainAtom          is not null
     * @param rangeAtom           is not null
     * @return an extension of the currentSubstitution that makes domainAtom to be equal to rangeAtom, if exists
     */
    protected Optional<Substitution> computeHomomorphismExtensionForAtom(Substitution currentSubstitution, Atom domainAtom, Atom rangeAtom) {
        if (!domainAtom.getPredicateName().equals(rangeAtom.getPredicateName())) {
            return Optional.empty();
        }

        return computeHomomorphismExtensionForTerms(currentSubstitution, domainAtom.getTerms(), rangeAtom.getTerms());
    }

    /**
     * @param currentSubstitution not null
     * @param domainTerms         not null
     * @param rangeTerms          not null
     * @return an extension of the currentSubstitution that makes domainTerms to be equal to rangeTerms, if exists
     */
    protected Optional<Substitution> computeHomomorphismExtensionForTerms(Substitution currentSubstitution, ImmutableTermList domainTerms, ImmutableTermList rangeTerms) {
        try {
            Substitution homomorphismForBuiltIn = new Substitution(domainTerms, rangeTerms);
            Substitution unionSubstitution = currentSubstitution.union(homomorphismForBuiltIn);
            return Optional.of(unionSubstitution);
        } catch (SubstitutionException ex) {
            return Optional.empty();
        }
    }


}
