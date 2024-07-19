package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.LogicSchemaBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.DerivationRuleSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.LogicSchemaToSpecHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that removes all those predicates (base or derived) that are not used in any LogicConstraint (neither
 * directly, neither transitively)
 */
public class PredicateCleaner extends LogicSchemaTransformationProcess {

    /**
     * @param logicSchema not null
     * @return a transformation where the resulting logicSchema does not have any unused predicate
     */
    @Override
    public SchemaTransformation executeTransformation(LogicSchema logicSchema) {
        return cleanTransformation(logicSchema);
    }

    /**
     * @param logicSchema not null
     * @return a copy of the logic schema where the unused predicates have been removed
     */
    public LogicSchema clean(LogicSchema logicSchema) {
        return cleanTransformation(logicSchema).transformed();
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
                .filter(OrdinaryLiteral.class::isInstance)
                .map(OrdinaryLiteral.class::cast)
                .map(ol -> ol.getAtom().getPredicateName()).collect(Collectors.toCollection(LinkedHashSet::new));
        return predicateNamesFromConstraint.stream()
                .map(predicateName -> obtainNestedPredicateNames(logicSchema, predicateName))
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Set<String> obtainNestedPredicateNames(LogicSchema logicSchema, String predicateName) {
        Predicate predicate = logicSchema.getPredicateByName(predicateName);
        if (predicate.isBase()) {
            return Set.of(predicateName);
        }

        Set<String> predicateNames = new LinkedHashSet<>();
        predicateNames.add(predicateName);
        for (DerivationRule derivationRule : logicSchema.getDerivationRulesByPredicateName(predicateName)) {
            Set<String> predicateNamesFromDerivationRule = derivationRule.getBody().stream()
                    .filter(OrdinaryLiteral.class::isInstance)
                    .map(OrdinaryLiteral.class::cast)
                    .map(ol -> ol.getAtom().getPredicateName())
                    .map(p -> obtainNestedPredicateNames(logicSchema, p))
                    .flatMap(Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new));

            predicateNames.addAll(predicateNamesFromDerivationRule);
        }
        return predicateNames;
    }

    private static LogicSchema buildLogicSchema(List<DerivationRule> usedDerivationRules, Set<LogicConstraint> logicConstraints, SchemaTraceabilityMap schemaTraceabilityMap) {
        List<LogicConstraintWithIDSpec> logicConstraintsSpecs = buildLogicConstraintSpecs(logicConstraints, schemaTraceabilityMap);
        List<DerivationRuleSpec> derivationRulesSpecs = LogicSchemaToSpecHelper.buildDerivationRuleSpecs(usedDerivationRules);

        return LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addLogicConstraint(logicConstraintsSpecs)
                .addDerivationRule(derivationRulesSpecs)
                .build();
    }

    private static List<LogicConstraintWithIDSpec> buildLogicConstraintSpecs(Set<LogicConstraint> logicConstraints, SchemaTraceabilityMap schemaTraceabilityMap) {
        List<LogicConstraintWithIDSpec> logicConstraintsSpecs = new LinkedList<>();
        for (LogicConstraint lc : logicConstraints) {
            LogicConstraintWithIDSpec logicSchemaSpec = LogicSchemaToSpecHelper.buildLogicConstraintSpec(lc);
            schemaTraceabilityMap.addConstraintIDOrigin(new ConstraintID(logicSchemaSpec.getId()), lc.getID());
            logicConstraintsSpecs.add(logicSchemaSpec);
        }
        return logicConstraintsSpecs;
    }

    private SchemaTransformation cleanTransformation(LogicSchema originalSchema) {
        checkLogicSchema(originalSchema);

        List<DerivationRule> usedDerivationRules = filterUsedDerivationRules(originalSchema);
        Set<LogicConstraint> logicConstraints = originalSchema.getAllLogicConstraints();

        SchemaTraceabilityMap schemaTraceabilityMap = new SchemaTraceabilityMap();
        LogicSchema outputSchema = buildLogicSchema(usedDerivationRules, logicConstraints, schemaTraceabilityMap);

        return new SchemaTransformation(originalSchema, outputSchema, schemaTraceabilityMap);
    }
}
