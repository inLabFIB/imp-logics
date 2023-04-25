package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.LogicSchemaBuilder;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.*;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.DerivationRuleSpecBuilder;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.LogicConstraintWithIDSpecBuilder;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.LogicSchemaToSpecHelper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class EqualityReplacer extends LogicSchemaTransformationProcess {

    @Override
    public SchemaTransformation executeTransformation(LogicSchema logicSchema) {
        checkLogicSchema(logicSchema);

        SchemaTraceabilityMap schemaTraceabilityMap = new SchemaTraceabilityMap();
        // TODO: SchemaTraceabilityMap

        List<PredicateSpec> predicateSpecs = LogicSchemaToSpecHelper.buildPredicatesSpecs(logicSchema.getAllPredicates());
        List<DerivationRuleSpec> derivationRulesSpecs = replaceEqualitiesInDerivationRules(logicSchema.getAllDerivationRules());
        List<LogicConstraintWithIDSpec> logicConstraintsSpecs = replaceEqualitiesInLogicConstraints(logicSchema.getAllLogicConstraints());

        LogicSchema outputLogicSchema = LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addLogicConstraint(logicConstraintsSpecs)
                .addDerivationRule(derivationRulesSpecs)
                .addAllPredicates(predicateSpecs)
                .build();

        return new SchemaTransformation(logicSchema, outputLogicSchema, schemaTraceabilityMap);
    }

    private List<LogicConstraintWithIDSpec> replaceEqualitiesInLogicConstraints(Set<LogicConstraint> logicConstraints) {
        return logicConstraints.stream()
                .map(logicConstraint -> {
                    ImmutableLiteralsList body = logicConstraint.getBody();
                    Set<ComparisonBuiltInLiteral> equalityLiterals = equalityLiteralsToReplaceFrom(body);
                    SubstitutionResult substitutionResult = substitutionsFrom(equalityLiterals);
                    ImmutableLiteralsList immutableLiteralsList = immutableListWithoutEqualityLiterals(body, substitutionResult.equalityLiterals());
                    ImmutableLiteralsList newBody = immutableLiteralsList.applySubstitution(substitutionResult.substitution());
                    return new LogicConstraintWithIDSpecBuilder()
                            .addConstraintId(logicConstraint.getID().id())
                            .addAllLiteralSpecs(LogicSchemaToSpecHelper.buildBodySpec(newBody).literals())
                            .build();
                })
                .toList();
    }

    private List<DerivationRuleSpec> replaceEqualitiesInDerivationRules(Set<DerivationRule> derivationRules) {
        return derivationRules.stream()
                .map(rule -> {
                    ImmutableLiteralsList body = rule.getBody();
                    Set<ComparisonBuiltInLiteral> equalityLiterals = equalityLiteralsToReplaceFrom(body);
                    SubstitutionResult substitutionResult = substitutionsFrom(equalityLiterals);
                    ImmutableLiteralsList immutableLiteralsList = immutableListWithoutEqualityLiterals(body, substitutionResult.equalityLiterals());
                    ImmutableLiteralsList newBody = immutableLiteralsList.applySubstitution(substitutionResult.substitution());
                    Atom newHead = rule.getHead().applySubstitution(substitutionResult.substitution());
                    return buildDerivationRuleSpec(newHead, newBody);
                }).toList();
    }

    private DerivationRuleSpec buildDerivationRuleSpec(Atom newHead, ImmutableLiteralsList newBody) {
        List<TermSpec> headTerms = LogicSchemaToSpecHelper.buildTermsSpecs(newHead.getTerms());
        BodySpec bodySpec = LogicSchemaToSpecHelper.buildBodySpec(newBody);
        return new DerivationRuleSpecBuilder()
                .addHead(newHead.getPredicateName(), headTerms)
                .addAllLiteralSpecs(bodySpec.literals())
                .build();
    }

    private static ImmutableLiteralsList immutableListWithoutEqualityLiterals(ImmutableLiteralsList body, Set<ComparisonBuiltInLiteral> equalityLiterals) {
        List<Literal> literalsWithoutEqualityLiterals = body.stream()
                .filter(not(l -> l instanceof ComparisonBuiltInLiteral builtInLiteral && equalityLiterals.contains(builtInLiteral)))
                .toList();
        return new ImmutableLiteralsList(literalsWithoutEqualityLiterals);
    }

    private static Set<ComparisonBuiltInLiteral> equalityLiteralsToReplaceFrom(ImmutableLiteralsList body) {
        return body.stream()
                .filter(ComparisonBuiltInLiteral.class::isInstance)
                .map(ComparisonBuiltInLiteral.class::cast)
                .filter(comparisonBuiltInLiteral -> ComparisonOperator.EQUALS.equals(comparisonBuiltInLiteral.getOperator()))
                .filter(comparisonBuiltInLiteral -> comparisonBuiltInLiteral.getTerms().stream().anyMatch(Term::isVariable))
                .collect(Collectors.toSet());
    }

    /**
     * Returns a substitution from a set of equality literals.
     *
     * @param equalityLiterals should be a set of equality literals with at least one variable.
     * @return substitution from the equality literals.
     */
    private SubstitutionResult substitutionsFrom(Set<ComparisonBuiltInLiteral> equalityLiterals) {
        PartitionOfEqualityLiterals setOfGroupedTerms = new PartitionOfEqualityLiterals(equalityLiterals);
        return setOfGroupedTerms.computeSubstitutionResult();
    }

}
