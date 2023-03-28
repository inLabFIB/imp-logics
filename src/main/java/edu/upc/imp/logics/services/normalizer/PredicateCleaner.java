package edu.upc.imp.logics.services.normalizer;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.services.creation.LogicSchemaBuilder;
import edu.upc.imp.logics.services.creation.spec.BodySpec;
import edu.upc.imp.logics.services.creation.spec.DerivationRuleSpec;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.LogicSchemaToSpecHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PredicateCleaner {
    public LogicSchema clean(LogicSchema logicSchema) {
        checkLogicSchema(logicSchema);
        List<DerivationRule> usedDerivationRules = filterUsedDerivationRules(logicSchema);
        return buildLogicSchema(logicSchema.getAllLogicConstraints(), usedDerivationRules);
    }

    private static void checkLogicSchema(LogicSchema logicSchema) {
        if (logicSchema == null) throw new IllegalArgumentException("LogicSchema cannot be null");
    }

    private static List<DerivationRule> filterUsedDerivationRules(LogicSchema logicSchema) {
        Set<String> usedPredicates = filterUsedPredicateNames(logicSchema);
        return logicSchema.getAllDerivationRules().stream()
                .filter(dr -> usedPredicates.stream().anyMatch(p -> p.equals(dr.getHead().getPredicateName())))
                .toList();
    }

    private static Set<String> filterUsedPredicateNames(LogicSchema logicSchema) {
        Set<LogicConstraint> allLogicConstraints = logicSchema.getAllLogicConstraints();
        Set<String> predicateNamesFromConstraint = allLogicConstraints.stream()
                .map(NormalClause::getBody)
                .flatMap(Collection::stream)
                .filter(l -> l instanceof OrdinaryLiteral)
                .map(l -> (OrdinaryLiteral) l)
                .map(ol -> ol.getAtom().getPredicateName()).collect(Collectors.toSet());
        return predicateNamesFromConstraint.stream()
                .map(predicateName -> obtainNestedPredicateNames(logicSchema, predicateName))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private static Set<String> obtainNestedPredicateNames(LogicSchema logicSchema, String predicateName) {
        Predicate predicate = logicSchema.getPredicateByName(predicateName);
        if (predicate.isBase()) {
            return Set.of(predicateName);
        }

        Set<String> predicateNames = new HashSet<>();
        predicateNames.add(predicateName);
        for (DerivationRule derivationRule : logicSchema.getDerivationRulesByPredicateName(predicateName)) {
            Set<String> predicateNamesFromDerivationRule = derivationRule.getBody().stream()
                    .filter(l -> l instanceof OrdinaryLiteral)
                    .map(l -> (OrdinaryLiteral) l)
                    .map(ol -> ol.getAtom().getPredicateName())
                    .map(p -> obtainNestedPredicateNames(logicSchema, p))
                    .flatMap(Collection::stream).collect(Collectors.toSet());

            predicateNames.addAll(predicateNamesFromDerivationRule);
        }
        return predicateNames;
    }

    private static LogicSchema buildLogicSchema(Set<LogicConstraint> logicConstraints, List<DerivationRule> usedDerivationRules) {
        List<LogicConstraintWithIDSpec> logicConstraintsSpecs = buildLogicConstraints(logicConstraints);
        List<DerivationRuleSpec> derivationRulesSpecs = LogicSchemaToSpecHelper.buildDerivationRuleSpecs(usedDerivationRules);

        return LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addLogicConstraint(logicConstraintsSpecs)
                .addDerivationRule(derivationRulesSpecs)
                .build();
    }

    private static List<LogicConstraintWithIDSpec> buildLogicConstraints(Set<LogicConstraint> logicConstraints) {
        return logicConstraints.stream()
                .map(lc -> {
                    BodySpec bodySpec = LogicSchemaToSpecHelper.buildBodySpec(lc.getBody());
                    return new LogicConstraintWithIDSpec(lc.getID().id(), bodySpec);
                })
                .toList();
    }

}
