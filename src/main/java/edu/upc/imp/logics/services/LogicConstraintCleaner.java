package edu.upc.imp.logics.services;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.services.creation.LogicSchemaBuilder;
import edu.upc.imp.logics.services.creation.spec.BodySpec;
import edu.upc.imp.logics.services.creation.spec.DerivationRuleSpec;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.LogicSchemaToSpecHelper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class LogicConstraintCleaner {
    public LogicSchema clean(LogicSchema logicSchema) {
        checkLogicSchema(logicSchema);
        List<DerivationRule> usedDerivationRules = filterUsedDerivationRules(logicSchema);
        return buildLogicSchema(logicSchema.getAllLogicConstraints(), usedDerivationRules);
    }

    private static void checkLogicSchema(LogicSchema logicSchema) {
        if (logicSchema == null) throw new IllegalArgumentException("LogicSchema cannot be null");
    }

    private static List<DerivationRule> filterUsedDerivationRules(LogicSchema logicSchema) {
        List<String> usedPredicates = filterUsedPredicateNames(logicSchema);
        return logicSchema.getAllDerivationRules().stream()
                .filter(dr -> usedPredicates.stream().anyMatch(p -> p.equals(dr.getHead().getPredicateName())))
                .toList();
    }

    private static List<String> filterUsedPredicateNames(LogicSchema logicSchema) {
        return logicSchema.getAllLogicConstraints().stream()
                .map(NormalClause::getBody)
                .flatMap(Collection::stream)
                .filter(l -> l instanceof OrdinaryLiteral)
                .map(l -> (OrdinaryLiteral) l)
                .map(ol -> ol.getAtom().getPredicateName()).toList();
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
