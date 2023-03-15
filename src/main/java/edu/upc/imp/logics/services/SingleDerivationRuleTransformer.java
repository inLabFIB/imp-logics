package edu.upc.imp.logics.services;


import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.schema.utils.Level;
import edu.upc.imp.logics.schema.utils.LevelHierarchy;
import edu.upc.imp.logics.services.creation.LogicSchemaBuilder;
import edu.upc.imp.logics.services.creation.spec.*;
import edu.upc.imp.logics.services.creation.spec.helpers.DerivationRuleSpecBuilder;
import edu.upc.imp.logics.services.creation.spec.helpers.LogicConstraintWithoutIDSpecBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleDerivationRuleTransformer {

    private static final String SUFFIX_SEPARATOR = "_";

    public LogicSchema transform(LogicSchema logicSchema) {
        Map<String, List<String>> predicateNameToTransformedPredicateNames = new HashMap<>();
        LogicSchemaBuilder<LogicConstraintWithoutIDSpec> logicSchemaBuilder = LogicSchemaBuilder.defaultLogicSchemaWithoutIDsBuilder();

        LevelHierarchy levelHierarchy = logicSchema.computeLevelHierarchy();

        for (Predicate basePredicate : levelHierarchy.getBasePredicatesLevel().getAllPredicates()) {
            PredicateSpec newPredicate = buildBasePredicateSpec(basePredicate, predicateNameToTransformedPredicateNames);
            logicSchemaBuilder.addPredicate(newPredicate);
        }

        for (Level level : levelHierarchy.getDerivedLevels()) {
            List<DerivationRuleSpec> newRules = buildDerivationRuleSpecsByLevel(level, predicateNameToTransformedPredicateNames);
            newRules.forEach(logicSchemaBuilder::addDerivationRule);
        }

        for (LogicConstraint logicConstraint : logicSchema.getAllLogicConstraints()) {
            List<LogicConstraintWithoutIDSpec> newConstraints = buildLogicConstraintSpecsByConstraint(logicConstraint, predicateNameToTransformedPredicateNames);
            newConstraints.forEach(logicSchemaBuilder::addLogicConstraint);
        }

        return logicSchemaBuilder.build();
    }

    private PredicateSpec buildBasePredicateSpec(Predicate basePredicate,
                                                 Map<String, List<String>> predicateNameToNewPredicateNames
    ) {
        String predicateName = basePredicate.getName();
        predicateNameToNewPredicateNames.put(predicateName, List.of(predicateName));
        return new PredicateSpec(predicateName, basePredicate.getArity());
    }

    private List<DerivationRuleSpec> buildDerivationRuleSpecsByLevel(Level derivedLevel,
                                                                     Map<String, List<String>> predicateNameToNewPredicateNames
    ) {
        List<DerivationRuleSpec> derivationRuleSpecs = new LinkedList<>();
        for (Predicate derivedPredicate : derivedLevel.getAllPredicates()) {
            derivationRuleSpecs.addAll(buildDerivationRuleSpecsByPredicate(derivedPredicate, predicateNameToNewPredicateNames));
        }
        return derivationRuleSpecs;
    }


    private List<DerivationRuleSpec> buildDerivationRuleSpecsByPredicate(Predicate predicate,
                                                                         Map<String, List<String>> predicateNameToNewPredicateNames
    ) {

        List<DerivationRuleSpec> result = new LinkedList<>();

        boolean predicateGeneratesSingleRule = predicateGeneratesSingleRule(predicate, predicateNameToNewPredicateNames);

        if (predicateGeneratesSingleRule) {
            DerivationRule dr = predicate.getDerivationRules().get(0);
            List<List<LiteralSpec>> lists = buildLiteralsListSpecsByLiteralsList(dr.getBody(), predicateNameToNewPredicateNames);
            List<LiteralSpec> literalSpecs = lists.get(0);
            DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpecBuilder()
                    .addHead(predicate.getName(), computeTermNames(dr.getHead().getTerms()))
                    .addAllLiteralSpecs(literalSpecs)
                    .build();
            result.add(derivationRuleSpec);
        } else {
            for (DerivationRule rule : predicate.getDerivationRules()) {
                int numberOfGeneratedRules = result.size();
                List<DerivationRuleSpec> derivationRuleSpecs = buildDerivationRuleSpecsByRule(rule, numberOfGeneratedRules, predicateNameToNewPredicateNames);
                result.addAll(derivationRuleSpecs);
            }
        }

        List<String> newPredicateNames = result.stream()
                .map(DerivationRuleSpec::getPredicateName)
                .toList();
        predicateNameToNewPredicateNames.put(predicate.getName(), newPredicateNames);
        return result;
    }

    private static String[] computeTermNames(ImmutableTermList terms) {
        return terms.stream().map(Term::getName).toArray(String[]::new);
    }

    private static boolean predicateGeneratesSingleRule(Predicate predicate, Map<String, List<String>> predicateNameToNewPredicateNames) {
        boolean isSimple = false;
        if (predicate.getDerivationRules().size() == 1) {
            DerivationRule dr = predicate.getDerivationRules().get(0);
            isSimple = dr.getBody().stream()
                    .filter(l -> l instanceof OrdinaryLiteral)
                    .map(l -> (OrdinaryLiteral) l)
                    .noneMatch(ol -> ol.isPositive() && predicateNameToNewPredicateNames.get(ol.getAtom().getPredicateName()).size() > 1);
        }
        return isSimple;
    }

    private List<DerivationRuleSpec> buildDerivationRuleSpecsByRule(DerivationRule rule, int numberOfGeneratedRules, Map<String, List<String>> predicateNameToNewPredicateNames) {
        ImmutableLiteralsList literalsList = rule.getBody();

        List<List<LiteralSpec>> listOfDerivationRuleBodies = buildLiteralsListSpecsByLiteralsList(literalsList, predicateNameToNewPredicateNames);

        AtomicInteger i = new AtomicInteger(numberOfGeneratedRules + 1);
        return listOfDerivationRuleBodies.stream()
                .map(listOfLiteralSpecs -> {
                    String newPredicateName = rule.getHead().getPredicateName() + SUFFIX_SEPARATOR + i.getAndIncrement();
                    return new DerivationRuleSpecBuilder()
                            .addHead(newPredicateName, buildTermSpecs(rule.getHead().getTerms()))
                            .addAllLiteralSpecs(listOfLiteralSpecs)
                            .build();
                }).toList();
    }


    private List<List<LiteralSpec>> buildLiteralsListSpecsByLiteralsList(ImmutableLiteralsList literalsList,
                                                                         Map<String, List<String>> predicateNameToNewPredicateNames) {
        /*
         * Given a LiteralsList "P, Q",
         * it might be the case that P must be replaced by P1 and P2, and Q by Q1 and Q2.
         * Hence, to compute the new literalsListSpec, we must do the cartesian product:
         * - P1, Q1
         * - P1, Q2
         * - P2, Q1
         * - P2, Q2
         */
        List<List<LiteralSpec>> result = List.of(List.of());
        for (Literal literal : literalsList) {
            List<List<LiteralSpec>> listOfLiteralSpecs = buildLiteralListSpecsByLiteral(literal, predicateNameToNewPredicateNames);
            result = cartesianProduct(result, listOfLiteralSpecs);
        }
        return result;
    }

    private List<List<LiteralSpec>> buildLiteralListSpecsByLiteral(Literal literal, Map<String, List<String>> predicateNameToNewPredicateNames) {
        if (literal instanceof BuiltInLiteral bil) {
            return List.of(List.of(new BuiltInLiteralSpec(bil.getOperationName(), buildTermsSpec(bil))));
        } else if (literal instanceof OrdinaryLiteral ol) {
            List<TermSpec> terms = buildTermsSpec(ol);
            String predicateName = ol.getAtom().getPredicateName();
            List<String> newPredicateNamesList = predicateNameToNewPredicateNames.get(predicateName);
            if (ol.isPositive()) {
                return newPredicateNamesList.stream()
                        .map(newPredicateName -> List.of((LiteralSpec) new OrdinaryLiteralSpec(newPredicateName, terms, true)))
                        .toList();
            } else {
                return List.of(newPredicateNamesList.stream()
                        .map(newPredicateName -> (LiteralSpec) new OrdinaryLiteralSpec(newPredicateName, terms, false))
                        .toList());
            }
        } else {
            throw new RuntimeException("Literal type not supported");
        }
    }

    private List<LogicConstraintWithoutIDSpec> buildLogicConstraintSpecsByConstraint(LogicConstraint logicConstraint,
                                                                                     Map<String, List<String>> predicateNameToNewPredicateNames) {
        List<List<LiteralSpec>> listOfLogicConstraintBodies = buildLiteralsListSpecsByLiteralsList(logicConstraint.getBody(), predicateNameToNewPredicateNames);

        return listOfLogicConstraintBodies.stream()
                .map(listOfLiteralSpecs -> new LogicConstraintWithoutIDSpecBuilder()
                        .addAllLiteralSpecs(listOfLiteralSpecs)
                        .build()).toList();
    }

    private List<List<LiteralSpec>> cartesianProduct(List<List<LiteralSpec>> firstListListLiteral, List<List<LiteralSpec>> secondListListLiteral) {
        List<List<LiteralSpec>> result = new ArrayList<>();
        for (List<LiteralSpec> replacedLiteralSpec : firstListListLiteral) {
            for (List<LiteralSpec> listLiteralsSpec : secondListListLiteral) {
                List<LiteralSpec> newLiteralSpecList = new ArrayList<>();
                newLiteralSpecList.addAll(replacedLiteralSpec);
                newLiteralSpecList.addAll(listLiteralsSpec);
                result.add(newLiteralSpecList);
            }
        }
        return result;
    }

    private TermSpec[] buildTermSpecs(ImmutableTermList terms) {
        return terms.stream().map(this::buildTermSpec).toArray(TermSpec[]::new);
    }

    private List<TermSpec> buildTermsSpec(Literal l) {
        return l.getTerms().stream().map(this::buildTermSpec).toList();
    }

    private TermSpec buildTermSpec(Term term) {
        if (term.isVariable()) {
            return new VariableSpec(term.getName());
        } else if (term.isConstant()) {
            return new ConstantSpec(term.getName());
        } else {
            throw new RuntimeException("Term type not supported");
        }
    }

}
