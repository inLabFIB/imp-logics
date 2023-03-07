package edu.upc.imp.logics.services.comparator;

import edu.upc.imp.logics.schema.DerivationRule;
import edu.upc.imp.logics.schema.Literal;
import edu.upc.imp.logics.schema.OrdinaryLiteral;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ExtendedHomomorphismFinder extends HomomorphismFinder {

    @Override
    public Optional<Substitution> findHomomorphismForLiteralsList(List<Literal> domainLiterals, List<Literal> rangeLiterals) {
        if (Objects.isNull(domainLiterals)) throw new IllegalArgumentException("DomainLiterals cannot be null");
        if (Objects.isNull(rangeLiterals)) throw new IllegalArgumentException("RangeLiterals cannot be null");

        return computeHomomorphismExtensionForLiteralsList(new Substitution(), domainLiterals, rangeLiterals);
    }

    @Override
    protected Optional<Substitution> computeHomomorphismExtensionForOrdinaryLiteral(Substitution currentSubstitution, OrdinaryLiteral domainLiteral, OrdinaryLiteral rangeLiteral) {
        if (domainLiteral.isDerived() != rangeLiteral.isDerived()) return Optional.empty();

        if (!domainLiteral.isDerived() && !rangeLiteral.isDerived()) {
            return super.computeHomomorphismExtensionForOrdinaryLiteral(currentSubstitution, domainLiteral, rangeLiteral);
        }

        if (domainLiteral.isPositive() != rangeLiteral.isPositive()) return Optional.empty();
        Optional<Substitution> newSubstitution = computeHomomorphismExtensionForTerms(currentSubstitution, domainLiteral.getAtom().getTerms(), rangeLiteral.getAtom().getTerms());
        if (newSubstitution.isPresent()) {
            boolean derivationRulesIncluded = checkDomainDerivationRulesAreIncludedInRangeDerivationRules(domainLiteral, rangeLiteral);
            if (derivationRulesIncluded) return newSubstitution;
            else return Optional.empty();
        } else return Optional.empty();
    }

    private boolean checkDomainDerivationRulesAreIncludedInRangeDerivationRules(OrdinaryLiteral domainLiteral, OrdinaryLiteral rangeLiteral) {
        List<DerivationRule> domainDerivationRules = domainLiteral.getAtom().getPredicate().getDerivationRules();
        List<DerivationRule> rangeDerivationRules = rangeLiteral.getAtom().getPredicate().getDerivationRules();

        return domainDerivationRules.stream().
                allMatch(domainRule -> checkDomainDerivationRuleIsIncludedInRangeDerivationRules(domainRule, rangeDerivationRules));
    }

    private boolean checkDomainDerivationRuleIsIncludedInRangeDerivationRules(DerivationRule domainRule, List<DerivationRule> rangeRules) {
        return rangeRules.stream().anyMatch(rangeRule -> {
            Substitution headSubstitution = new Substitution(domainRule.getHead().getTerms(), rangeRule.getHead().getTerms());
            return super.computeHomomorphismExtensionForLiteralsList(headSubstitution, domainRule.getBody(), rangeRule.getBody()).isPresent();
        });
    }
}
