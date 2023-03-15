package edu.upc.imp.logics.services;


import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.schema.utils.Level;
import edu.upc.imp.logics.services.creation.LogicSchemaBuilder;
import edu.upc.imp.logics.services.creation.spec.*;
import edu.upc.imp.logics.services.creation.spec.helpers.DerivationRuleSpecBuilder;
import edu.upc.imp.logics.services.creation.spec.helpers.LogicConstraintWithoutIDSpecBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleDerivationRuleTransformer {

    private static final String SUFFIX_SEPARATOR = "_";

    public LogicSchema transform(LogicSchema logicSchema) {
        Map<String, List<String>> predicateTransformMap = new HashMap<>();
        LogicSchemaBuilder<LogicConstraintWithoutIDSpec> logicSchemaBuilder = LogicSchemaBuilder.defaultLogicSchemaWithoutIDsBuilder();

        for (Level level : logicSchema.computeLevelHierarchy()) {
            buildDerivationRulesByLevel(level, logicSchemaBuilder, predicateTransformMap);
        }

        for (LogicConstraint logicConstraint : logicSchema.getAllLogicConstraints()) {
            List<LogicConstraintWithoutIDSpec> newLogicConstraints = transformLogicConstraint(logicConstraint, predicateTransformMap);
            newLogicConstraints.forEach(logicSchemaBuilder::addLogicConstraint);
        }

        return logicSchemaBuilder.build();
    }

    private void buildDerivationRulesByLevel(Level level,
                                             LogicSchemaBuilder<LogicConstraintWithoutIDSpec> logicSchemaBuilder,
                                             Map<String, List<String>> predicateTransformMap
    ) {
        Set<Predicate> levelPredicates = level.getAllPredicates();

        for (Predicate p : levelPredicates) {
            if (p.isBase()) {
                String predicateName = p.getName();
                predicateTransformMap.put(predicateName, List.of(predicateName));
                logicSchemaBuilder.addPredicate(predicateName, p.getArity());
            } else {
                List<DerivationRuleSpec> totalGeneratedRulesForPredicate = transformDerivedPredicate(p, predicateTransformMap);
                totalGeneratedRulesForPredicate.forEach(logicSchemaBuilder::addDerivationRule);
            }
        }
    }

    private List<DerivationRuleSpec> transformDerivedPredicate(Predicate p, Map<String, List<String>> predicateTransformMap) {
        String predicateName = p.getName();

        List<DerivationRuleSpec> totalGeneratedRulesForPredicate = new LinkedList<>();

        boolean isSimple = false;
        if (p.getDerivationRules().size() == 1) {
            DerivationRule dr = p.getDerivationRules().get(0);
            isSimple = dr.getBody().stream()
                    .filter(l -> l instanceof OrdinaryLiteral)
                    .map(l -> (OrdinaryLiteral) l)
                    .noneMatch(ol -> ol.isPositive() && predicateTransformMap.get(ol.getAtom().getPredicateName()).size() > 1);
        }

        if (isSimple) {
            DerivationRule dr = p.getDerivationRules().get(0);
            List<List<LiteralSpec>> lists = computeListListLiteralSpec(dr.getBody(), predicateTransformMap);
            List<LiteralSpec> literalSpecs = lists.get(0);
            DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpecBuilder()
                    .addHead(predicateName, dr.getHead().getTerms().stream().map(Term::getName).toArray(String[]::new))
                    .addAllLiteralSpecs(literalSpecs)
                    .build();
            totalGeneratedRulesForPredicate.add(derivationRuleSpec);
        } else {
            for (DerivationRule rule : p.getDerivationRules()) {
                int numberOfGeneratedRules = totalGeneratedRulesForPredicate.size();
                List<DerivationRuleSpec> derivationRuleSpecs = computeDerivationRuleSpecs(rule, numberOfGeneratedRules, predicateTransformMap);
                totalGeneratedRulesForPredicate.addAll(derivationRuleSpecs);
            }
        }

        List<String> predicateTransformList = totalGeneratedRulesForPredicate.stream()
                .map(DerivationRuleSpec::getPredicateName)
                .toList();
        predicateTransformMap.put(predicateName, predicateTransformList);
        return totalGeneratedRulesForPredicate;
    }

    private List<LogicConstraintWithoutIDSpec> transformLogicConstraint(LogicConstraint logicConstraint, Map<String, List<String>> predicateTransformMap) {
        List<List<LiteralSpec>> listOfLogicConstraintBodies = computeListListLiteralSpec(logicConstraint.getBody(), predicateTransformMap);

        return listOfLogicConstraintBodies.stream()
                .map(listOfLiteralSpecs -> new LogicConstraintWithoutIDSpecBuilder()
                        .addAllLiteralSpecs(listOfLiteralSpecs)
                        .build()).toList();
    }

    private List<DerivationRuleSpec> computeDerivationRuleSpecs(DerivationRule rule, int numberOfGeneratedRules, Map<String, List<String>> predicateTransformMap) {
        ImmutableLiteralsList literalsList = rule.getBody();

        List<List<LiteralSpec>> listOfDerivationRuleBodies = computeListListLiteralSpec(literalsList, predicateTransformMap);

        AtomicInteger i = new AtomicInteger(numberOfGeneratedRules + 1);
        return listOfDerivationRuleBodies.stream()
                .map(listOfLiteralSpecs -> {
                    String newPredicateName = rule.getHead().getPredicateName() + SUFFIX_SEPARATOR + i.getAndIncrement();
                    return new DerivationRuleSpecBuilder()
                            .addHead(newPredicateName, rule.getHead().getTerms().stream().map(Term::getName).toArray(String[]::new))
                            .addAllLiteralSpecs(listOfLiteralSpecs)
                            .build();
                }).toList();
    }

    private List<List<LiteralSpec>> computeListListLiteralSpec(ImmutableLiteralsList literalsList, Map<String, List<String>> predicateTransformMap) {
        List<List<LiteralSpec>> listOfDerivationRuleBodies = List.of(List.of());
        for (Literal literal : literalsList) {
            List<List<LiteralSpec>> listOfDerivationRuleBodysToAdd = transformLiteral(literal, predicateTransformMap);
            listOfDerivationRuleBodies = cartesianProduct(listOfDerivationRuleBodies, listOfDerivationRuleBodysToAdd);
        }
        return listOfDerivationRuleBodies;
    }

    private List<List<LiteralSpec>> transformLiteral(Literal literal, Map<String, List<String>> predicateTransformMap) {
        if (literal instanceof BuiltInLiteral bil) {
            return List.of(List.of(new BuiltInLiteralSpec(bil.getOperationName(), buildTermsSpec(bil))));
        } else if (literal instanceof OrdinaryLiteral ol) {
            List<TermSpec> terms = buildTermsSpec(ol);
            String predicateName = ol.getAtom().getPredicateName();
            List<String> newPredicateNamesList = predicateTransformMap.get(predicateName);
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
