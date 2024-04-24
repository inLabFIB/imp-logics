package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.LogicSchemaBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.DerivationRuleSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.PredicateSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.LogicSchemaToSpecHelper;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

/**
 * This class is responsible for removing, when it is possible, the equality built-in literals from the body of a normal
 * clause and apply a substitution that corresponds to such built-in literals.
 * <p>
 * E.g.: :- P(x), x = 1 will be transformed into: :- P(1)
 * <p>
 * However, a case such as: :- P(x), x = 1, x = 2 will not be transformed.
 */
public class EqualityReplacer extends LogicSchemaTransformationProcess {

    @Override
    public SchemaTransformation executeTransformation(LogicSchema logicSchema) {
        checkLogicSchema(logicSchema);

        List<PredicateSpec> predicateSpecs = LogicSchemaToSpecHelper.buildPredicatesSpecs(logicSchema.getAllPredicates());
        List<DerivationRuleSpec> derivationRulesSpecs = replaceEqualitiesInDerivationRules(logicSchema.getAllDerivationRules());
        List<LogicConstraintWithIDSpec> logicConstraintsSpecs = replaceEqualitiesInLogicConstraints(logicSchema.getAllLogicConstraints());
        SchemaTraceabilityMap schemaTraceabilityMap = buildSchemaTraceabilityMap(logicSchema.getAllLogicConstraints());

        LogicSchema outputLogicSchema = LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addLogicConstraint(logicConstraintsSpecs)
                .addDerivationRule(derivationRulesSpecs)
                .addAllPredicates(predicateSpecs)
                .build();

        return new SchemaTransformation(logicSchema, outputLogicSchema, schemaTraceabilityMap);
    }

    private static SchemaTraceabilityMap buildSchemaTraceabilityMap(Set<LogicConstraint> allLogicConstraints) {
        SchemaTraceabilityMap schemaTraceabilityMap = new SchemaTraceabilityMap();
        allLogicConstraints.forEach(
                logicConstraint -> schemaTraceabilityMap.addConstraintIDOrigin(logicConstraint.getID(), logicConstraint.getID())
        );
        return schemaTraceabilityMap;
    }

    private List<LogicConstraintWithIDSpec> replaceEqualitiesInLogicConstraints(Set<LogicConstraint> logicConstraints) {
        return logicConstraints.stream()
                .map(this::buildLogicConstraintSpec)
                .toList();
    }

    private List<DerivationRuleSpec> replaceEqualitiesInDerivationRules(Set<DerivationRule> derivationRules) {
        return derivationRules.stream()
                .map(this::buildDerivationRuleSpec).toList();
    }

    private DerivationRuleSpec buildDerivationRuleSpec(DerivationRule rule) {
        ImmutableLiteralsList body = rule.getBody();
        Set<ComparisonBuiltInLiteral> equalityLiterals = equalityLiteralsToReplaceFrom(body);
        SubstitutionForEqualities substitutionForEqualities = substitutionsFrom(equalityLiterals);
        ImmutableLiteralsList immutableLiteralsList = immutableListWithoutEqualityLiterals(body, substitutionForEqualities.equalityLiterals());
        ImmutableLiteralsList newBody = immutableLiteralsList.applySubstitution(substitutionForEqualities.substitution());
        Atom newHead = rule.getHead().applySubstitution(substitutionForEqualities.substitution());
        return LogicSchemaToSpecHelper.buildDerivationRuleSpec(newHead, newBody);
    }

    private LogicConstraintWithIDSpec buildLogicConstraintSpec(LogicConstraint logicConstraint) {
        ImmutableLiteralsList body = logicConstraint.getBody();
        Set<ComparisonBuiltInLiteral> equalityLiterals = equalityLiteralsToReplaceFrom(body);
        SubstitutionForEqualities substitutionForEqualities = substitutionsFrom(equalityLiterals);
        ImmutableLiteralsList immutableLiteralsList = immutableListWithoutEqualityLiterals(body, substitutionForEqualities.equalityLiterals());
        ImmutableLiteralsList newBody = immutableLiteralsList.applySubstitution(substitutionForEqualities.substitution());
        ConstraintID id = logicConstraint.getID();
        return LogicSchemaToSpecHelper.buildLogicConstraintSpec(id, newBody);
    }

    private static Set<ComparisonBuiltInLiteral> equalityLiteralsToReplaceFrom(ImmutableLiteralsList body) {
        return body.stream()
                .filter(ComparisonBuiltInLiteral.class::isInstance)
                .map(ComparisonBuiltInLiteral.class::cast)
                .filter(comparisonBuiltInLiteral -> ComparisonOperator.EQUALS.equals(comparisonBuiltInLiteral.getOperator()))
                .filter(comparisonBuiltInLiteral -> comparisonBuiltInLiteral.getTerms().stream().anyMatch(Term::isVariable))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Returns a substitution from a set of equality literals.
     *
     * @param equalityLiterals should be a set of equality literals with at least one variable.
     * @return substitution from the equality literals.
     */
    private SubstitutionForEqualities substitutionsFrom(Set<ComparisonBuiltInLiteral> equalityLiterals) {
        PartitionOfEqualityLiterals setOfGroupedTerms = new PartitionOfEqualityLiterals(equalityLiterals);
        return setOfGroupedTerms.computeSubstitutionResult();
    }

    private static ImmutableLiteralsList immutableListWithoutEqualityLiterals(ImmutableLiteralsList body, Set<ComparisonBuiltInLiteral> equalityLiterals) {
        List<Literal> literalsWithoutEqualityLiterals = body.stream()
                .filter(not(l -> l instanceof ComparisonBuiltInLiteral builtInLiteral && equalityLiterals.contains(builtInLiteral)))
                .toList();
        return new ImmutableLiteralsList(literalsWithoutEqualityLiterals);
    }

}
