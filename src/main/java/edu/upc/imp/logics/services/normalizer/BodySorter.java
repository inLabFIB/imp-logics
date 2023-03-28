package edu.upc.imp.logics.services.normalizer;

import edu.upc.imp.logics.schema.DerivationRule;
import edu.upc.imp.logics.schema.ImmutableLiteralsList;
import edu.upc.imp.logics.schema.LogicConstraint;
import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.services.creation.LogicSchemaBuilder;
import edu.upc.imp.logics.services.creation.spec.*;
import edu.upc.imp.logics.services.creation.spec.helpers.LogicSchemaToSpecHelper;

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
     * @return a logicSchema with the bodies of the normal clauses sorted
     */
    public LogicSchema sort(LogicSchema logicSchema) {
        if (Objects.isNull(logicSchema)) {
            throw new IllegalArgumentException("LogicSchema cannot be null");
        }
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

