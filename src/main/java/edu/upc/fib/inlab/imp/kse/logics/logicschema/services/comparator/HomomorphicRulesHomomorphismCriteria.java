package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.OrdinaryLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;

import java.util.List;
import java.util.Optional;

/**
 * Under this criteria, two derived ordinary literals are considered homomorphic, even if they have different predicate
 * names, if their derivation rules are homomorphic
 */
public class HomomorphicRulesHomomorphismCriteria implements DerivedOrdinaryLiteralHomomorphismCriteria {

    @Override
    public Optional<Substitution> computeHomomorphismExtensionForDerivedOrdinaryLiteral(HomomorphismFinder homomorphismFinder, Substitution currentSubstitution, OrdinaryLiteral domainLiteral, OrdinaryLiteral rangeLiteral) {
        if (domainLiteral.isDerived() != rangeLiteral.isDerived()) return Optional.empty();

        if (domainLiteral.isPositive() != rangeLiteral.isPositive()) return Optional.empty();
        Optional<Substitution> newSubstitution = homomorphismFinder.computeHomomorphismExtensionForTerms(currentSubstitution, domainLiteral.getAtom().getTerms(), rangeLiteral.getAtom().getTerms());
        if (newSubstitution.isPresent()) {
            boolean derivationRulesIncluded = checkDomainDerivationRulesAreIncludedInRangeDerivationRules(homomorphismFinder, domainLiteral, rangeLiteral);
            if (derivationRulesIncluded) return newSubstitution;
            else return Optional.empty();
        } else return Optional.empty();
    }

    private boolean checkDomainDerivationRulesAreIncludedInRangeDerivationRules(HomomorphismFinder homomorphismFinder, OrdinaryLiteral domainLiteral, OrdinaryLiteral rangeLiteral) {
        List<DerivationRule> domainDerivationRules = domainLiteral.getAtom().getPredicate().getDerivationRules();
        List<DerivationRule> rangeDerivationRules = rangeLiteral.getAtom().getPredicate().getDerivationRules();

        return domainDerivationRules.stream().
                allMatch(domainRule -> checkDomainDerivationRuleIsIncludedInRangeDerivationRules(homomorphismFinder, domainRule, rangeDerivationRules));
    }

    private boolean checkDomainDerivationRuleIsIncludedInRangeDerivationRules(HomomorphismFinder homomorphismFinder, DerivationRule domainRule, List<DerivationRule> rangeRules) {
        return rangeRules.stream().anyMatch(rangeRule -> {
            Substitution headSubstitution = new Substitution(domainRule.getHeadTerms(), rangeRule.getHeadTerms());
            return homomorphismFinder.computeHomomorphismExtensionForLiteralsList(headSubstitution, domainRule.getBody(), rangeRule.getBody()).isPresent();
        });
    }
}
