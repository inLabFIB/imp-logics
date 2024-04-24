package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.LogicSchemaBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.LogicSchemaToSpecHelper;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes.utils.LiteralComparator;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Service that returns a copy of the logicSchema where every normal clause (logic constraint, or derivation rule) has
 * its body sorted according to the following order: 1) positive literals, 2) negated literals, 3) built-in literals.
 * <p>
 * Sorting the body literals in this way is useful, for instance, for evaluating the body. Indeed, we cannot evaluate a
 * built-in literal or negated literal until we know which values can take its variables, and such values can be
 * obtained by first evaluating the positive literals.
 */
public class BodySorter extends LogicSchemaTransformationProcess {

    private final Comparator<Literal> literalComparator;

    public BodySorter() {
        this(new LiteralComparator());
    }

    public BodySorter(Comparator<Literal> literalComparator) {
        this.literalComparator = literalComparator;
    }

    /**
     * @param logicSchema not-null
     * @return a transformation where the final logicSchema has sorted the bodies of its normal clauses
     */
    @Override
    public SchemaTransformation executeTransformation(LogicSchema logicSchema) {
        return sortTransformation(logicSchema);
    }

    private SchemaTransformation sortTransformation(LogicSchema logicSchema) {
        checkLogicSchema(logicSchema);
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

    private List<LogicConstraintWithIDSpec> sortBodyInLogicConstraints(Set<LogicConstraint> allLogicConstraints, SchemaTraceabilityMap schemaTraceabilityMap) {
        return allLogicConstraints.stream()
                .map(
                        lc -> {
                            ImmutableLiteralsList sortedBody = lc.getBody().sortLiterals(literalComparator);
                            BodySpec bodySpec = LogicSchemaToSpecHelper.buildBodySpec(sortedBody);
                            schemaTraceabilityMap.addConstraintIDOrigin(lc.getID(), lc.getID());
                            return new LogicConstraintWithIDSpec(lc.getID().id(), bodySpec);
                        }
                )
                .toList();
    }

    private List<DerivationRuleSpec> sortBodyInDerivationRules(Set<DerivationRule> allDerivationRules) {
        return allDerivationRules.stream()
                .map(
                        dr -> {
                            ImmutableLiteralsList sortedBody = dr.getBody().sortLiterals(literalComparator);
                            BodySpec bodySpec = LogicSchemaToSpecHelper.buildBodySpec(sortedBody);
                            List<TermSpec> termSpecs = LogicSchemaToSpecHelper.buildTermsSpecs(dr.getHeadTerms());
                            return new DerivationRuleSpec(dr.getHead().getPredicateName(), termSpecs, bodySpec);
                        }
                )
                .toList();
    }

    /**
     * @param logicSchema not-null
     * @return a logicSchema with the bodies of the normal clauses sorted
     */
    public LogicSchema sort(LogicSchema logicSchema) {
        return this.sortTransformation(logicSchema).transformed();
    }
}

