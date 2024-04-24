package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.HomomorphismFinder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.LogicSchemaBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.DerivationRuleSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.PredicateSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.DerivationRuleSpecBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.LogicConstraintWithIDSpecBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.LogicSchemaToSpecHelper;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is responsible for removing the trivial literals from the logic constraints, and logic derivation rules,
 * but without removing derived predicates, neither removing constraints.
 * <p>
 * A trivial literal is a literal that always evaluates to true, or that always evaluates to false. For instance,
 * BooleanBuiltInLiterals are trivial literals. Hence, this class remove such literals from the bodies of the derivation
 * rules and logic constraints. E.g.: "@1 :- P(x), TRUE()" will be transformed into "@1 :- P(x)"
 * <p>
 * Currently, this class detects the following trivial literals:
 * <ul>
 * <li>BooleanBuiltInLiterals. I.e., "TRUE()", "FALSE()" built in literals.
 * <li>Derived literals whose bodies contains trivial literals. E.g. "P(x) :- B(x), FALSE()"
 * </ul>
 * <p>
 * This class does not remove any logic constraint, nor removes derived predicates. Hence, if a logic constraint
 * is only composed of one trivial literal, it will remain as it is.
 * E.g. "@1 :- TRUE()" will remain as it is.
 * <p>
 * Similarly, the class does not remove any derived predicate. So, if a derived predicate is only composed of one
 * derivation rule which is always false, it will remain as it is.
 * E.g. "P(x) :- FALSE()" will remain as it is.
 */
public class TrivialLiteralCleaner extends LogicSchemaTransformationProcess {

    private static DerivationRuleSpec buildFalseRuleForPredicate(Predicate predicate) {
        return new DerivationRuleSpecBuilder()
                .addHead(predicate.getName(), LogicSchemaToSpecHelper.buildTermsSpecs(predicate.getFirstDerivationRule().getHeadTerms()))
                .addLiteralSpec(LogicSchemaToSpecHelper.buildFalseLiteralSpec())
                .build();
    }

    /**
     * @param logicSchema not null
     * @return a transformation where the final logicSchema a new logicSchema with the trivial literals removed.
     */
    @Override
    public SchemaTransformation executeTransformation(LogicSchema logicSchema) {
        checkLogicSchema(logicSchema);

        SchemaTraceabilityMap schemaTraceabilityMap = new SchemaTraceabilityMap();
        List<PredicateSpec> predicateSpecs = LogicSchemaToSpecHelper.buildPredicatesSpecs(logicSchema.getAllPredicates());
        List<LogicConstraintWithIDSpec> newConstraints = cleanTrivialInLiteralsInLogicConstraints(logicSchema.getAllLogicConstraints(), schemaTraceabilityMap);
        List<DerivationRuleSpec> newRules = cleanTrivialLiteralsInDerivedPredicates(logicSchema.getAllDerivedPredicates());

        LogicSchema transformedSchema = LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addAllPredicates(predicateSpecs)
                .addAllLogicConstraints(newConstraints)
                .addAllDerivationRules(newRules)
                .build();
        return new SchemaTransformation(logicSchema, transformedSchema, schemaTraceabilityMap);
    }

    /**
     * @param logicSchema not null
     * @return a new logicSchema with the trivial literals removed.
     */
    public LogicSchema clean(LogicSchema logicSchema) {
        return executeTransformation(logicSchema).transformed();
    }

    private List<LogicConstraintWithIDSpec> cleanTrivialInLiteralsInLogicConstraints(Set<LogicConstraint> logicConstraints, SchemaTraceabilityMap schemaTraceabilityMap) {
        return logicConstraints.stream()
                .map(lc -> cleanTrivialLiteralsInOneLogicConstraint(lc, schemaTraceabilityMap))
                .toList();
    }

    private LogicConstraintWithIDSpec cleanTrivialLiteralsInOneLogicConstraint(LogicConstraint lc, SchemaTraceabilityMap schemaTraceabilityMap) {
        List<LiteralSpec> literalSpecList = cleanTrivialLiteralsInLiteralList(lc.getBody());
        LogicConstraintWithIDSpec logicConstraintWithIDSpec = new LogicConstraintWithIDSpecBuilder()
                .addConstraintId(lc.getID().id())
                .addAllLiteralSpecs(literalSpecList)
                .build();
        schemaTraceabilityMap.addConstraintIDOrigin(lc.getID(), new ConstraintID(logicConstraintWithIDSpec.getId()));
        return logicConstraintWithIDSpec;
    }

    private List<DerivationRuleSpec> cleanTrivialLiteralsInDerivedPredicates(Set<Predicate> derivedPredicates) {
        return derivedPredicates.stream()
                .flatMap(predicate -> cleanTrivialLiteralsInDerivedPredicate(predicate).stream())
                .toList();
    }

    private List<DerivationRuleSpec> cleanTrivialLiteralsInDerivedPredicate(Predicate predicate) {
        Set<Atom> alwaysTrueHeads = predicate.getDerivationRules().stream()
                .map(DerivationRule::getHead)
                .filter(this::isAlwaysTrue)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<DerivationRule> nonTrivialRules = predicate.getDerivationRules().stream()
                .filter(rule -> !isTrivialRule(rule))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<Atom> necessaryAlwaysTrueAtoms = removeRedundantAtoms(alwaysTrueHeads);

        List<DerivationRuleSpec> result = new LinkedList<>();
        result.addAll(cleanTrivialLiteralsInDerivedPredicate(nonTrivialRules));
        result.addAll(builtTrueRulesForAtoms(necessaryAlwaysTrueAtoms));
        if (result.isEmpty()) {
            result.add(buildFalseRuleForPredicate(predicate));
        }
        return result;
    }

    private boolean isTrivialRule(DerivationRule rule) {
        return isAlwaysTrue(rule.getHead()) || isAlwaysFalse(rule.getBody());
    }

    private Set<Atom> removeRedundantAtoms(Set<Atom> alwaysTrueHeads) {
        Set<Atom> necessaryAtoms = new LinkedHashSet<>(alwaysTrueHeads);
        for (Atom atom : alwaysTrueHeads) {
            necessaryAtoms.remove(atom);
            if (!isRedundantWithAnyOf(atom, necessaryAtoms)) {
                necessaryAtoms.add(atom);
            }
        }
        return necessaryAtoms;
    }

    private boolean isRedundantWithAnyOf(Atom atom, Set<Atom> necessaryAtoms) {
        return necessaryAtoms.stream().anyMatch(necessaryAtom -> existHomomorphism(necessaryAtom, atom));
    }

    private boolean existHomomorphism(Atom domainAtom, Atom rangeAtom) {
        return new HomomorphismFinder()
                .findHomomorphismForTerms(domainAtom.getTerms(), rangeAtom.getTerms())
                .isPresent();
    }

    /**
     * @param nonTrivialRules a set of rules whose head is not always true, nor whose body always evaluates to false
     */
    private List<DerivationRuleSpec> cleanTrivialLiteralsInDerivedPredicate(Set<DerivationRule> nonTrivialRules) {
        return nonTrivialRules.stream()
                .map(this::cleanTrivialLiteralsInOneDerivationRule)
                .toList();
    }

    /**
     * @param nonTrivialRule a rule whose head is not always true, nor whose body always evaluates to false
     */
    private DerivationRuleSpec cleanTrivialLiteralsInOneDerivationRule(DerivationRule nonTrivialRule) {
        List<LiteralSpec> literalSpecList = cleanTrivialLiteralsInLiteralList(nonTrivialRule.getBody());
        return new DerivationRuleSpecBuilder()
                .addHead(
                        nonTrivialRule.getHead().getPredicateName(),
                        LogicSchemaToSpecHelper.buildTermsSpecs(nonTrivialRule.getHeadTerms())
                )
                .addAllLiteralSpecs(literalSpecList)
                .build();
    }

    private List<LiteralSpec> cleanTrivialLiteralsInLiteralList(ImmutableLiteralsList literalsList) {
        if (isAlwaysFalse(literalsList)) {
            return List.of(LogicSchemaToSpecHelper.buildFalseLiteralSpec());
        }

        List<LiteralSpec> cleanedList = literalsList.stream()
                .filter(literal -> !isAlwaysTrue(literal))
                .map(LogicSchemaToSpecHelper::buildLiteralSpec)
                .toList();

        if (cleanedList.isEmpty()) {
            return List.of(LogicSchemaToSpecHelper.buildTrueLiteralSpec());
        }
        return cleanedList;
    }

    private List<DerivationRuleSpec> builtTrueRulesForAtoms(Set<Atom> heads) {
        return heads.stream()
                .map(this::builtTrueRuleForAtom)
                .toList();
    }

    private DerivationRuleSpec builtTrueRuleForAtom(Atom atom) {
        return new DerivationRuleSpecBuilder()
                .addHead(atom.getPredicateName(), LogicSchemaToSpecHelper.buildTermsSpecs(atom.getTerms()))
                .addLiteralSpec(LogicSchemaToSpecHelper.buildTrueLiteralSpec())
                .build();
    }

    private boolean isAlwaysFalse(ImmutableLiteralsList literalsList) {
        for (Literal literal : literalsList) {
            if (isAlwaysFalse(literal)) return true;
        }
        return false;
    }

    private boolean isAlwaysTrue(ImmutableLiteralsList literalsList) {
        boolean allVisitedLiteralsAreAlwaysTrue = true;
        for (Literal literal : literalsList) {
            allVisitedLiteralsAreAlwaysTrue = allVisitedLiteralsAreAlwaysTrue && isAlwaysTrue(literal);
        }
        return allVisitedLiteralsAreAlwaysTrue;
    }


    private boolean isAlwaysTrue(Literal literal) {
        if (literal instanceof BooleanBuiltInLiteral booleanBuiltInLiteral) {
            return booleanBuiltInLiteral.isTrue();
        }
        if (literal instanceof ComparisonBuiltInLiteral cBil && cBil.getOperator().equals(ComparisonOperator.EQUALS)) {
            return cBil.getOperator().equals(ComparisonOperator.EQUALS) && cBil.getRightTerm().equals(cBil.getLeftTerm());
        }
        if (literal instanceof OrdinaryLiteral ordinaryLiteral && ordinaryLiteral.isDerived()) {
            if (ordinaryLiteral.isPositive()) {
                return isAlwaysTrue(ordinaryLiteral.getAtom());
            } else {
                return isAlwaysFalse(ordinaryLiteral.getAtom());
            }
        }
        return false;
    }

    private boolean isAlwaysFalse(Literal literal) {
        if (literal instanceof BooleanBuiltInLiteral booleanBuiltInLiteral) {
            return booleanBuiltInLiteral.isFalse();
        }
        if (literal instanceof OrdinaryLiteral ordinaryLiteral && ordinaryLiteral.isDerived()) {
            if (ordinaryLiteral.isPositive()) {
                return isAlwaysFalse(ordinaryLiteral.getAtom());
            } else {
                return isAlwaysTrue(ordinaryLiteral.getAtom());
            }
        }
        return false;
    }

    private boolean isAlwaysTrue(Atom atom) {
        for (ImmutableLiteralsList immutableLiteralsList : atom.unfold()) {
            if (isAlwaysTrue(immutableLiteralsList)) return true;
        }
        return false;
    }

    private boolean isAlwaysFalse(Atom atom) {
        boolean visitedRulesAreAlwaysFalse = true;
        for (ImmutableLiteralsList immutableLiteralsList : atom.unfold()) {
            visitedRulesAreAlwaysFalse = visitedRulesAreAlwaysFalse && isAlwaysFalse(immutableLiteralsList);
        }
        return visitedRulesAreAlwaysFalse;
    }

}
