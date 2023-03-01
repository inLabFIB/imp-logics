package edu.upc.imp.logics.services.comparator;

import edu.upc.imp.logics.schema.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p> This class is responsible for checking whether there is an homomorphism between two NormalClauses.
 * The homomorphism is agnostic from the schemas of the predicates. That is, two NormalClauses from different
 * logic schemas (and hence, using different predicate objects) might be homomorphic if there is an homomorphism
 * between the literals with the same predicate name. </p>
 *
 * <p>
 * Hence, this service can be used to:
 * - Compare two normal clauses from the same schema
 * - Compare two normal clauses of different schemas
 * to identify whether they are equivalent up to renaming the name of variables </p>
 *
 * <p>
 * It is worth to mention that the checker can realize homomorphisms between built-in literals with their symmetric
 * comparison operations.
 * E.g. the checker can realize that "a < b" is homomorphic to "b > a".
 * However, do note that the checker will assert that there is no homomorphism from "P(x,x)" to "P(x, y), x=y".</p>
 */
public class HomomorphismChecker {
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

    /**
     * Return a homomorphism from the domainRule terms to the rangeRule terms, if exists
     *
     * @param domainRule not null
     * @param rangeRule  not null
     * @return optional containing a homomorphism between the two, if exists
     */
    public Optional<Substitution> computeHomomorphism(DerivationRule domainRule, DerivationRule rangeRule) {
        if (Objects.isNull(domainRule)) throw new IllegalArgumentException("DomainRule cannot be null");
        if (Objects.isNull(rangeRule)) throw new IllegalArgumentException("RangeRule cannot be null");

        Optional<Substitution> homomorphism = computeHomomorphismForHead(domainRule.getHead(), rangeRule.getHead());
        if (homomorphism.isEmpty()) return Optional.empty();
        return computeHomomorphism(homomorphism.get(), domainRule.getBody(), rangeRule.getBody());
    }

    private Optional<Substitution> computeHomomorphismForHead(Atom domainHead, Atom rangeHead) {
        return computeHomomorphismForAtom(new Substitution(), domainHead, rangeHead);
    }

    /**
     * @param currentSubstitution is not null
     * @param domainLiterals      is not null, but might be empty
     * @param rangeLiterals       is not null
     * @return an extension of the currentSubstitution, if exists, that would make domainLiterals to be contained
     * in rangeLiterals
     */
    private Optional<Substitution> computeHomomorphism(Substitution currentSubstitution, List<Literal> domainLiterals, List<Literal> rangeLiterals) {
        if (domainLiterals.isEmpty()) return Optional.of(currentSubstitution);
        else {
            Literal domainLiteral = domainLiterals.get(0);
            List<Substitution> possibleSubstitutionsForDomainLiteral = computeAllPossibleHomomorphisms(currentSubstitution, domainLiteral, rangeLiterals);
            for (Substitution possibleSubstitutionForDomainLiteral : possibleSubstitutionsForDomainLiteral) {
                List<Literal> restOfDomainLiterals = domainLiterals.subList(1, domainLiterals.size());
                Optional<Substitution> homomorphism = computeHomomorphism(possibleSubstitutionForDomainLiteral, restOfDomainLiterals, rangeLiterals);
                if (homomorphism.isPresent()) return homomorphism;
            }
            return Optional.empty();
        }
    }

    /**
     * @param currentSubstitution is not null
     * @param domainLiteral       is not null
     * @param rangeLiterals       is not null
     * @return an extension of the currentSubstitution that makes domainLiteral to be included in rangeLiterals, if exists
     */
    private List<Substitution> computeAllPossibleHomomorphisms(Substitution currentSubstitution, Literal domainLiteral, List<Literal> rangeLiterals) {
        List<Substitution> allPossibleHomorphisms = new LinkedList<>();
        for (Literal rangeLiteral : rangeLiterals) {
            Optional<Substitution> homomorphism = computeHomomorphism(currentSubstitution, domainLiteral, rangeLiteral);
            homomorphism.ifPresent(allPossibleHomorphisms::add);
        }
        return allPossibleHomorphisms;
    }

    /**
     * @param currentSubstitution is not null
     * @param domainLiteral       is not null
     * @param rangeLiteral        is not null
     * @return an extension of the currentSubstitution that makes domainLiteral to be equal to rangeLiteral, if exists
     */
    private Optional<Substitution> computeHomomorphism(Substitution currentSubstitution, Literal domainLiteral, Literal rangeLiteral) {
        if (domainLiteral instanceof OrdinaryLiteral domainOrdinaryLiteral) {
            if (rangeLiteral instanceof OrdinaryLiteral rangeOrdinaryLiteral) {
                return computeHomomorphismForOrdinaryLiteral(currentSubstitution, domainOrdinaryLiteral, rangeOrdinaryLiteral);
            } else return Optional.empty();
        } else if (domainLiteral instanceof BuiltInLiteral domainBuiltInLiteral) {
            if (rangeLiteral instanceof BuiltInLiteral rangeBuiltInLiteral) {
                return computeHomomorphismForBuiltInLiteral(currentSubstitution, domainBuiltInLiteral, rangeBuiltInLiteral);
            } else return Optional.empty();
        } else throw new RuntimeException("Unrecognized literal " + domainLiteral.getClass().getName());
    }

    /**
     * @param currentSubstitution  is not null
     * @param domainBuiltInLiteral is not null
     * @param rangeBuiltInLiteral  is not null
     * @return an extension of the currentSubstitution that makes domainBuiltInLiteral to be equal to rangeBuiltInLiteral,
     * or its symmetric, if exists
     */
    private Optional<Substitution> computeHomomorphismForBuiltInLiteral(Substitution currentSubstitution, BuiltInLiteral domainBuiltInLiteral, BuiltInLiteral rangeBuiltInLiteral) {
        if (!domainBuiltInLiteral.getOperationName().equals(rangeBuiltInLiteral.getOperationName())) {
            if (domainBuiltInLiteral instanceof ComparisonBuiltInLiteral domainComparison &&
                    rangeBuiltInLiteral instanceof ComparisonBuiltInLiteral rangeComparison) {
                return computeHomomorphismForSymmetricBuiltInLiteral(currentSubstitution, domainComparison, rangeComparison);
            } else return Optional.empty();
        }

        return computeHomomorphismForTerms(currentSubstitution, domainBuiltInLiteral.getTerms(), rangeBuiltInLiteral.getTerms());

    }

    /**
     * The symmetric built-in literal of a built-in literals "a < b" is "b > a". Similarly, we would define the symmetric built-in literal
     * for the built-in literals using the comparison operators =, <, <=, >=, >, <>
     *
     * @param currentSubstitution is not null
     * @param domainComparison    is not null
     * @param rangeComparison     is not null
     * @return an extension of the currentSubstitution that makes domainComparison to be equal to the symmetric of rangeComparison, if it exists
     */
    private Optional<Substitution> computeHomomorphismForSymmetricBuiltInLiteral(Substitution currentSubstitution, ComparisonBuiltInLiteral domainComparison, ComparisonBuiltInLiteral rangeComparison) {
        ComparisonOperator domainOperator = domainComparison.getOperator();
        ComparisonOperator rangeOperator = rangeComparison.getOperator();
        if (domainOperator.isSymmetric(rangeOperator)) {
            List<Term> reversedRangeTerms = List.of(rangeComparison.getRightTerm(), rangeComparison.getLeftTerm());
            return computeHomomorphismForTerms(currentSubstitution, domainComparison.getTerms(), reversedRangeTerms);
        } else return Optional.empty();
    }

    /**
     * @param currentSubstitution not null
     * @param domainTerms         not null
     * @param rangeTerms          not null
     * @return an extension of the currentSubstitution that makes domainTerms to be equal to rangeTerms, if exists
     */
    private Optional<Substitution> computeHomomorphismForTerms(Substitution currentSubstitution, List<Term> domainTerms, List<Term> rangeTerms) {
        try {
            Substitution homomorphismForBuiltIn = new Substitution(domainTerms, rangeTerms);
            Substitution unionSubstitution = currentSubstitution.union(homomorphismForBuiltIn);
            return Optional.of(unionSubstitution);
        } catch (SubstitutionException ex) {
            return Optional.empty();
        }
    }

    /**
     * @param currentSubstitution is not null
     * @param domainLiteral       is not null
     * @param rangeLiteral        is not null
     * @return an extension of the currentSubstitution that makes domainLiteral to be equal to rangeLiteral, if exists
     */
    private Optional<Substitution> computeHomomorphismForOrdinaryLiteral(Substitution currentSubstitution, OrdinaryLiteral domainLiteral, OrdinaryLiteral rangeLiteral) {
        if (domainLiteral.isPositive() != rangeLiteral.isPositive()) return Optional.empty();
        return computeHomomorphismForAtom(currentSubstitution, domainLiteral.getAtom(), rangeLiteral.getAtom());

    }

    /**
     * @param currentSubstitution is not null
     * @param domainAtom          is not null
     * @param rangeAtom           is not null
     * @return an extension of the currentSubstitution that makes domainAtom to be equal to rangeAtom, if exists
     */
    private Optional<Substitution> computeHomomorphismForAtom(Substitution currentSubstitution, Atom domainAtom, Atom rangeAtom) {
        if (!domainAtom.getPredicateName().equals(rangeAtom.getPredicateName())) {
            return Optional.empty();
        }

        return computeHomomorphismForTerms(currentSubstitution, domainAtom.getTerms(), rangeAtom.getTerms());
    }
}
