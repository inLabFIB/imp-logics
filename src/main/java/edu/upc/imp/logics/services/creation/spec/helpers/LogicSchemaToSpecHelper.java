package edu.upc.imp.logics.services.creation.spec.helpers;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.services.creation.spec.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LogicSchemaToSpecHelper {

    public static List<DerivationRuleSpec> buildDerivationRuleSpecs(List<DerivationRule> usedDerivationRules) {
        return usedDerivationRules.stream().map(dr -> {
            BodySpec bodySpec = buildBodySpec(dr.getBody());
            List<TermSpec> termSpecs = buildTermsSpecs(dr.getHead().getTerms());
            return new DerivationRuleSpec(dr.getHead().getPredicateName(), termSpecs, bodySpec);
        }).toList();
    }

    public static List<LogicConstraintWithIDSpec> buildLogicConstraintSpecs(Set<LogicConstraint> logicConstraints) {
        return logicConstraints.stream()
                .map(lc -> {
                    BodySpec bodySpec = buildBodySpec(lc.getBody());
                    return new LogicConstraintWithIDSpec(lc.getID().id(), bodySpec);
                })
                .toList();
    }

    public static List<PredicateSpec> buildPredicatesSpecs(Set<Predicate> allPredicates) {
        return allPredicates.stream()
                .map(predicate -> new PredicateSpec(predicate.getName(), predicate.getArity()))
                .toList();
    }

    public static List<TermSpec> buildTermsSpecs(ImmutableTermList terms) {
        return terms.stream().map(t -> {
            if (t instanceof Constant) {
                return new ConstantSpec(t.getName());
            } else if (t instanceof Variable) {
                return new VariableSpec(t.getName());
            } else throw new RuntimeException("Unknown term type: " + t.getClass().getName());
        }).collect(Collectors.toList());
    }

    public static BodySpec buildBodySpec(ImmutableLiteralsList body) {
        return new BodySpec(body.stream()
                .map(l -> {
                    if (l instanceof OrdinaryLiteral ordinaryLiteral) {
                        ImmutableTermList terms = ordinaryLiteral.getTerms();
                        List<TermSpec> termSpecs = buildTermsSpecs(terms);
                        return new OrdinaryLiteralSpec(ordinaryLiteral.getAtom().getPredicateName(),
                                termSpecs,
                                ordinaryLiteral.isPositive());
                    } else if (l instanceof ComparisonBuiltInLiteral comparisonBuiltInLiteral) {
                        ImmutableTermList terms = comparisonBuiltInLiteral.getTerms();
                        List<TermSpec> termSpecs = buildTermsSpecs(terms);
                        return new BuiltInLiteralSpec(comparisonBuiltInLiteral.getOperationName(), termSpecs);
                    } else throw new RuntimeException("Unknown literal type: " + l.getClass().getName());
                })
                .collect(Collectors.toList()));
    }

}
