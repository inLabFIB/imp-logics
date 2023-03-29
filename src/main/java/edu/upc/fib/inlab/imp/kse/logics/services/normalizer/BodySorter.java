package edu.upc.fib.inlab.imp.kse.logics.services.normalizer;

import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicConstraint;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.LogicSchemaBuilder;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.*;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.LogicSchemaToSpecHelper;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Service that returns a copy of the logicSchema where every normal clause (logic constraint, or derivation rule) has
 * its body sorted according to the following order: 1) positive literals, 2) negated literals, 3) built-in literals
 */
public class BodySorter {

    /**
     * @param logicSchema not-null
     * @return a transformation where the final logicSchema has sorted the bodies of its normal clauses
     */
    public SchemaTransformation sortTransformation(LogicSchema logicSchema) {
        if (Objects.isNull(logicSchema)) {
            throw new IllegalArgumentException("LogicSchema cannot be null");
        }
        SchemaTraceabilityMap schemaTraceabilityMap = new SchemaTraceabilityMap();
        List<LogicConstraintWithIDSpec> logicConstraintsSpecs = sortBodyInLogicConstraints(logicSchema.getAllLogicConstraints(), schemaTraceabilityMap);
        List<DerivationRuleSpec> derivationRulesSpecs = sortBodyInDerivationRules(logicSchema.getAllDerivationRules());

        List<PredicateSpec> predicateSpecs = LogicSchemaToSpecHelper.buildPredicatesSpecs(logicSchema.getAllPredicates());
        LogicSchema outputLogicSchema = LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addLogicConstraint(logicConstraintsSpecs)
                .addDerivationRule(derivationRulesSpecs)
                .addAllPredicates(predicateSpecs)
                .build();

        return new SchemaTransformation(logicSchema, outputLogicSchema, schemaTraceabilityMap);
    }

    /**
     * @param logicSchema not-null
     * @return a logicSchema with the bodies of the normal clauses sorted
     */
    public LogicSchema sort(LogicSchema logicSchema) {
        return this.sortTransformation(logicSchema).transformed();
    }

    private static List<LogicConstraintWithIDSpec> sortBodyInLogicConstraints(Set<LogicConstraint> allLogicConstraints, SchemaTraceabilityMap schemaTraceabilityMap) {
        return allLogicConstraints.stream()
                .map(
                        lc -> {
                            ImmutableLiteralsList sortedBody = lc.getBody().getSorted();
                            BodySpec bodySpec = LogicSchemaToSpecHelper.buildBodySpec(sortedBody);
                            schemaTraceabilityMap.addConstraintIDOrigin(lc.getID(), lc.getID());
                            return new LogicConstraintWithIDSpec(lc.getID().id(), bodySpec);
                        }
                )
                .toList();
    }

    private static List<DerivationRuleSpec> sortBodyInDerivationRules(Set<DerivationRule> allDerivationRules) {
        return allDerivationRules.stream()
                .map(
                        dr -> {
                            ImmutableLiteralsList sortedBody = dr.getBody().getSorted();
                            BodySpec bodySpec = LogicSchemaToSpecHelper.buildBodySpec(sortedBody);
                            List<TermSpec> termSpecs = LogicSchemaToSpecHelper.buildTermsSpecs(dr.getHead().getTerms());
                            return new DerivationRuleSpec(dr.getHead().getPredicateName(), termSpecs, bodySpec);
                        }
                )
                .toList();
    }
}

