package edu.upc.imp.logics.services.normalizer;

import edu.upc.imp.logics.schema.DerivationRule;
import edu.upc.imp.logics.schema.ImmutableLiteralsList;
import edu.upc.imp.logics.schema.LogicConstraint;
import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.services.creation.LogicSchemaBuilder;
import edu.upc.imp.logics.services.creation.spec.*;
import edu.upc.imp.logics.services.creation.spec.helpers.LogicSchemaToSpecHelper;

import java.util.List;
import java.util.Set;

public class BodySorter {
    public LogicSchema sort(LogicSchema logicSchema) {
        List<LogicConstraintWithIDSpec> logicConstraintsSpecs = sortBodyInLogicConstraints(logicSchema.getAllLogicConstraints());
        List<DerivationRuleSpec> derivationRulesSpecs = sortBodyInDerivationRules(logicSchema.getAllDerivationRules());

        List<PredicateSpec> predicateSpecs = LogicSchemaToSpecHelper.buildPredicatesSpecs(logicSchema.getAllPredicates());
        return LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addLogicConstraint(logicConstraintsSpecs)
                .addDerivationRule(derivationRulesSpecs)
                .addAllPredicates(predicateSpecs)
                .build();
    }

    private static List<LogicConstraintWithIDSpec> sortBodyInLogicConstraints(Set<LogicConstraint> allLogicConstraints) {
        return allLogicConstraints.stream()
                .map(
                        lc -> {
                            ImmutableLiteralsList sortedBody = lc.getBody().getSorted();
                            BodySpec bodySpec = LogicSchemaToSpecHelper.buildBodySpec(sortedBody);
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

