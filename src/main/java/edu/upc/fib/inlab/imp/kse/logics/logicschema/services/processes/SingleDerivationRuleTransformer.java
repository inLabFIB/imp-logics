package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;


import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.LogicSchemaBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.DerivationRuleSpecBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.LogicConstraintWithIDSpecBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.LogicSchemaToSpecHelper;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.LogicSchemaToSpecHelper.BodySpecFragment;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes.utils.PredicateSuffixNamer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>This class is in charge of transforming one logic schema into an equivalent logic schema where every derived
 * predicate is defined, only, through one derivation rule. </p>
 *
 * <p>To achieve so, this class replaces the derivation rule's head to avoid repeated predicates. E.g.: </p>
 * P :- A, B <br>
 * P :- C, D <br>
 * <p>
 * is replaced by: <br>
 * P_1 :- A, B <br>
 * P_2 :- C, D <br>
 *
 * <p> Consequently, any apparition of predicates which have been transformed (such as the predicate P in the previous
 * example), is replaced by its new transformed predicates. E.g.:</p>
 * :- P, R <br>
 * :- S, not(P) <br>
 * <p>
 * is transformed to: <br>
 * :- P_1, R <br>
 * :- P_2, R <br>
 * :- S, not(P_1), not(P_2) <br>
 */
public class SingleDerivationRuleTransformer extends LogicSchemaTransformationProcess {

    private final MultipleConstraintIDGenerator generatorId;

    private static final class PredicateNameToNewPredicateNamesMap extends HashMap<String, List<String>> {

    }

    /**
     * Creates an SingleDerivationRuleTransformer that will use the SuffixMultipleConstraintIDGenerator as a strategy
     * for creating new constraintIDs, if necessary.
     */
    public SingleDerivationRuleTransformer() {
        this(new SuffixMultipleConstraintIDGenerator());
    }

    /**
     * Creates an SingleDerivationRuleTransformer that will use the given generatorId strategy
     * for creating new constraintIDs, if necessary.
     *
     * @param generatorId not null
     */
    public SingleDerivationRuleTransformer(MultipleConstraintIDGenerator generatorId) {
        if (Objects.isNull(generatorId)) throw new IllegalArgumentException("GeneratorID cannot be null");
        this.generatorId = generatorId;
    }

    /**
     * @param logicSchema not-null
     * @return a transformation where the final logicSchema is a new equivalent logic schema where every derived predicate is defined through only one derivation rule
     */
    @Override
    public SchemaTransformation executeTransformation(LogicSchema logicSchema) {
        return transformTransformation(logicSchema);
    }

    /**
     * @param logicSchema a not null logic schema
     * @return a new equivalent logic schema where every derived predicate is defined through only one derivation rule
     */
    public LogicSchema transform(LogicSchema logicSchema) {
        return transformTransformation(logicSchema).transformed();
    }


    private SchemaTransformation transformTransformation(LogicSchema logicSchema) {
        checkLogicSchema(logicSchema);
        if (logicSchema.isEmpty()) {
            LogicSchema transformedSchema = new LogicSchema(Set.of(), Set.of());
            return new SchemaTransformation(logicSchema, transformedSchema, new SchemaTraceabilityMap());
        }

        PredicateNameToNewPredicateNamesMap predicateTransformMap = new PredicateNameToNewPredicateNamesMap();
        SchemaTraceabilityMap constraintTransformMap = new SchemaTraceabilityMap();

        LevelHierarchy levelHierarchy = logicSchema.computeLevelHierarchy();
        List<PredicateSpec> newPredicates = buildNewPredicates(levelHierarchy, predicateTransformMap);
        List<DerivationRuleSpec> newRules = buildNewDerivationRules(levelHierarchy, predicateTransformMap);
        List<LogicConstraintWithIDSpec> newConstraints = buildNewLogicConstraints(logicSchema, predicateTransformMap, constraintTransformMap);

        LogicSchema transformedSchema = LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addAllPredicates(newPredicates)
                .addAllDerivationRules(newRules)
                .addAllLogicConstraints(newConstraints)
                .build();
        return new SchemaTransformation(logicSchema, transformedSchema, constraintTransformMap);
    }

    private List<LogicConstraintWithIDSpec> buildNewLogicConstraints(LogicSchema logicSchema, PredicateNameToNewPredicateNamesMap predicateTransformMap, SchemaTraceabilityMap constraintTransformMap) {
        List<LogicConstraintWithIDSpec> allConstraints = new LinkedList<>();
        for (LogicConstraint logicConstraint : logicSchema.getAllLogicConstraints()) {
            List<LogicConstraintWithIDSpec> newConstraints = buildLogicConstraintSpecsByConstraint(logicConstraint, predicateTransformMap, constraintTransformMap);
            allConstraints.addAll(newConstraints);
        }
        return allConstraints;
    }

    private List<DerivationRuleSpec> buildNewDerivationRules(LevelHierarchy levelHierarchy, PredicateNameToNewPredicateNamesMap predicateTransformMap) {
        List<DerivationRuleSpec> allRules = new LinkedList<>();
        if (levelHierarchy.getNumberOfLevels() > 0) {
            for (Level level : levelHierarchy.getDerivedLevels()) {
                List<DerivationRuleSpec> newRules = buildDerivationRuleSpecsByLevel(level, predicateTransformMap);
                allRules.addAll(newRules);
            }
        }
        return allRules;
    }

    private List<PredicateSpec> buildNewPredicates(LevelHierarchy levelHierarchy, PredicateNameToNewPredicateNamesMap predicateTransformMap) {
        List<PredicateSpec> allPredicates = new LinkedList<>();
        if (levelHierarchy.getNumberOfLevels() > 0) {
            Level baseLevel = levelHierarchy.getBasePredicatesLevel();
            for (Predicate basePredicate : baseLevel.getAllPredicates()) {
                PredicateSpec newPredicate = buildBasePredicateSpec(basePredicate, predicateTransformMap);
                allPredicates.add(newPredicate);
            }
        }
        return allPredicates;
    }

    private PredicateSpec buildBasePredicateSpec(Predicate basePredicate,
                                                 PredicateNameToNewPredicateNamesMap predicateTransformMap
    ) {
        String predicateName = basePredicate.getName();
        predicateTransformMap.put(predicateName, List.of(predicateName));
        return new PredicateSpec(predicateName, basePredicate.getArity());
    }

    private List<DerivationRuleSpec> buildDerivationRuleSpecsByLevel(Level derivedLevel,
                                                                     PredicateNameToNewPredicateNamesMap predicateTransformMap
    ) {
        List<DerivationRuleSpec> derivationRuleSpecs = new LinkedList<>();
        for (Predicate derivedPredicate : derivedLevel.getAllPredicates()) {
            derivationRuleSpecs.addAll(buildDerivationRuleSpecsByPredicate(derivedPredicate, predicateTransformMap));
        }
        return derivationRuleSpecs;
    }


    private List<DerivationRuleSpec> buildDerivationRuleSpecsByPredicate(Predicate predicate,
                                                                         PredicateNameToNewPredicateNamesMap predicateTransformMap
    ) {

        List<DerivationRuleSpec> result = new LinkedList<>();

        boolean predicateGeneratesSingleRule = predicateGeneratesSingleRule(predicate, predicateTransformMap);

        if (predicateGeneratesSingleRule) {
            DerivationRule dr = predicate.getFirstDerivationRule();
            List<LogicSchemaToSpecHelper.BodySpecFragment> lists = buildBodySpecFragmentsByLiteralsList(dr.getBody(), predicateTransformMap);
            List<LiteralSpec> literalSpecs = lists.get(0);
            DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpecBuilder()
                    .addHead(predicate.getName(), LogicSchemaToSpecHelper.buildTermsSpecs(dr.getHeadTerms()))
                    .addAllLiteralSpecs(literalSpecs)
                    .build();
            result.add(derivationRuleSpec);
        } else {
            for (DerivationRule rule : predicate.getDerivationRules()) {
                int numberOfGeneratedRules = result.size();
                List<DerivationRuleSpec> derivationRuleSpecs = buildDerivationRuleSpecsByRule(rule, numberOfGeneratedRules, predicateTransformMap);
                result.addAll(derivationRuleSpecs);
            }
        }

        List<String> newPredicateNames = result.stream()
                .map(DerivationRuleSpec::getPredicateName)
                .toList();
        predicateTransformMap.put(predicate.getName(), newPredicateNames);
        return result;
    }

    private static boolean predicateGeneratesSingleRule(Predicate predicate, PredicateNameToNewPredicateNamesMap predicateTransformMap) {
        boolean isSimple = false;
        if (predicate.getDerivationRules().size() == 1) {
            DerivationRule dr = predicate.getFirstDerivationRule();
            isSimple = dr.getBody().stream()
                    .filter(OrdinaryLiteral.class::isInstance)
                    .map(OrdinaryLiteral.class::cast)
                    .noneMatch(ol -> ol.isPositive() && predicateTransformMap.get(ol.getAtom().getPredicateName()).size() > 1);
        }
        return isSimple;
    }

    private List<DerivationRuleSpec> buildDerivationRuleSpecsByRule(DerivationRule rule, int numberOfGeneratedRules, PredicateNameToNewPredicateNamesMap predicateTransformMap) {
        ImmutableLiteralsList literalsList = rule.getBody();

        List<BodySpecFragment> listOfDerivationRuleBodies = buildBodySpecFragmentsByLiteralsList(literalsList, predicateTransformMap);

        AtomicInteger i = new AtomicInteger(numberOfGeneratedRules + 1);
        return listOfDerivationRuleBodies.stream()
                .map(listOfLiteralSpecs -> {
                    //TODO - Generate a new name for the predicate
                    String newPredicateName = PredicateSuffixNamer.concatSuffix(
                            rule.getHead().getPredicateName(),
                            Integer.toString(i.getAndIncrement())
                    );
                    return new DerivationRuleSpecBuilder()
                            .addHead(newPredicateName, LogicSchemaToSpecHelper.buildTermsSpecs(rule.getHeadTerms()))
                            .addAllLiteralSpecs(listOfLiteralSpecs)
                            .build();
                }).toList();
    }


    private List<BodySpecFragment> buildBodySpecFragmentsByLiteralsList(ImmutableLiteralsList literalsList,
                                                                        PredicateNameToNewPredicateNamesMap predicateTransformMap) {
        /*
         * Given a LiteralsList "P, Q",
         * it might be the case that P must be replaced by P1 and P2, and Q by Q1 and Q2.
         * Hence, to compute the new literalsListSpec, we must do the cartesian product:
         * - P1, Q1
         * - P1, Q2
         * - P2, Q1
         * - P2, Q2
         */
        List<BodySpecFragment> result = List.of(new BodySpecFragment());
        for (Literal literal : literalsList) {
            List<BodySpecFragment> listOfLiteralSpecs = buildBodySpecFragmentsByLiteral(literal, predicateTransformMap);
            result = LogicSchemaToSpecHelper.cartesianProduct(result, listOfLiteralSpecs);
        }
        return result;
    }

    private List<BodySpecFragment> buildBodySpecFragmentsByLiteral(Literal literal, PredicateNameToNewPredicateNamesMap predicateTransformMap) {
        if (literal instanceof BuiltInLiteral bil) {
            LiteralSpec literalSpec = new BuiltInLiteralSpec(bil.getOperationName(), LogicSchemaToSpecHelper.buildTermsSpecs(bil.getTerms()));
            BodySpecFragment bodyFragment = new BodySpecFragment();
            bodyFragment.add(literalSpec);
            return List.of(bodyFragment);
        } else if (literal instanceof OrdinaryLiteral ol) {
            List<TermSpec> terms = LogicSchemaToSpecHelper.buildTermsSpecs(ol.getTerms());
            String predicateName = ol.getAtom().getPredicateName();
            List<String> newPredicateNamesList = predicateTransformMap.get(predicateName);
            if (ol.isPositive()) {
                return newPredicateNamesList.stream()
                        .map(newPredicateName -> {
                            LiteralSpec literalSpec = new OrdinaryLiteralSpec(newPredicateName, terms, true);
                            BodySpecFragment bodyFragment = new BodySpecFragment();
                            bodyFragment.add(literalSpec);
                            return bodyFragment;
                        })
                        .toList();
            } else {
                BodySpecFragment bodyFragment = new BodySpecFragment();
                newPredicateNamesList.stream()
                        .map(newPredicateName -> (LiteralSpec) new OrdinaryLiteralSpec(newPredicateName, terms, false))
                        .forEach(bodyFragment::add);
                return List.of(bodyFragment);
            }
        } else {
            throw new IMPLogicsException("Literal type not supported");
        }
    }

    private List<LogicConstraintWithIDSpec> buildLogicConstraintSpecsByConstraint(
            LogicConstraint logicConstraint,
            PredicateNameToNewPredicateNamesMap predicateTransformMap,
            SchemaTraceabilityMap constraintTransformMap
    ) {
        List<BodySpecFragment> listOfBodyFragments = buildBodySpecFragmentsByLiteralsList(logicConstraint.getBody(), predicateTransformMap);
        List<ConstraintID> newConstraintIDS = generatorId.generateNewConstraintsIDs(logicConstraint.getID(), listOfBodyFragments.size());

        AtomicInteger index = new AtomicInteger(0);
        return listOfBodyFragments.stream()
                .map(bodyFragment -> {
                            ConstraintID newConstraintId = newConstraintIDS.get(index.getAndIncrement());
                            constraintTransformMap.addConstraintIDOrigin(newConstraintId, logicConstraint.getID());
                            return new LogicConstraintWithIDSpecBuilder()
                                    .addConstraintId(newConstraintId.id())
                                    .addAllLiteralSpecs(bodyFragment)
                                    .build();
                        }
                )
                .toList();
    }

}
