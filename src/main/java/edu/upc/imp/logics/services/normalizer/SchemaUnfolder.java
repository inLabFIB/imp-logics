package edu.upc.imp.logics.services.normalizer;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.services.creation.LogicSchemaFactory;
import edu.upc.imp.logics.services.creation.spec.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SchemaUnfolder {
    /**
     * @param schema not null
     * @return a new schema with the same (based & derived) predicates as the one given but unfolding
     * all the positive derived literals
     */
    public LogicSchema unfold(LogicSchema schema) {
        if (Objects.isNull(schema)) throw new IllegalArgumentException("Schema cannot be null");

        LogicSchemaSpec<LogicConstraintWithoutIDSpec> logicSchemaSpec = computeUnfoldedLogicSchemaSpec(schema);

        return LogicSchemaFactory.defaultLogicSchemaWithoutIDsFactory().createLogicSchema(logicSchemaSpec);
    }

    private LogicSchemaSpec<LogicConstraintWithoutIDSpec> computeUnfoldedLogicSchemaSpec(LogicSchema schema) {
        LogicSchemaSpec<LogicConstraintWithoutIDSpec> logicSchemaSpec = new LogicSchemaSpec<>();
        logicSchemaSpec.addPredicateSpecs(computePredicateSpecs(schema));
        logicSchemaSpec.addDerivationRuleSpecs(computeUnfoldedDerivationRuleSpecs(schema));
        logicSchemaSpec.addLogicConstraintSpecs(computeUnfoldedLogicConstraintSpecs(schema));
        return logicSchemaSpec;
    }

    private LogicConstraintWithoutIDSpec[] computeUnfoldedLogicConstraintSpecs(LogicSchema schema) {
        List<LogicConstraintWithoutIDSpec> result = new LinkedList<>();
        for (LogicConstraint logicConstraint : schema.getAllLogicConstraints()) {
            result.addAll(computeUnfoldedLogicConstraintSpecs(logicConstraint.getBody()));
        }
        return result.toArray(new LogicConstraintWithoutIDSpec[0]);
    }

    private List<LogicConstraintWithoutIDSpec> computeUnfoldedLogicConstraintSpecs(ImmutableLiteralsList body) {
        List<BodySpec> unfoldedBodySpecs = computeUnfoldedBodySpec(body);

        return unfoldedBodySpecs.stream()
                .map(LogicConstraintWithoutIDSpec::new)
                .collect(Collectors.toList());
    }

    private DerivationRuleSpec[] computeUnfoldedDerivationRuleSpecs(LogicSchema schema) {
        List<DerivationRuleSpec> result = new LinkedList<>();
        for (DerivationRule derivationRule : schema.getAllDerivationRules()) {
            result.addAll(computeUnfoldedDerivationRuleSpecs(derivationRule.getHead(), derivationRule.getBody()));
        }
        return result.toArray(new DerivationRuleSpec[0]);
    }

    private List<DerivationRuleSpec> computeUnfoldedDerivationRuleSpecs(Atom head, ImmutableLiteralsList body) {
        String predicateName = head.getPredicateName();
        List<TermSpec> termSpecs = buildSpec(head.getTerms());
        List<BodySpec> unfoldedBodySpecs = computeUnfoldedBodySpec(body);

        return unfoldedBodySpecs.stream()
                .map(bs -> new DerivationRuleSpec(predicateName, termSpecs, bs))
                .collect(Collectors.toList());
    }


    private List<BodySpec> computeUnfoldedBodySpec(ImmutableLiteralsList body) {
        Optional<Integer> indexOfLiteralToUnfold = getIndexOfUnfoldableLiteral(body);
        if (indexOfLiteralToUnfold.isPresent()) {
            int index = indexOfLiteralToUnfold.get();
            List<BodySpec> result = new LinkedList<>();
            for (ImmutableLiteralsList bodyWithUnfoldedLiteral : body.unfold(index)) {
                result.addAll(computeUnfoldedBodySpec(bodyWithUnfoldedLiteral));
            }
            return result;
        } else return List.of(buildSpec(body));
    }

    private Optional<Integer> getIndexOfUnfoldableLiteral(ImmutableLiteralsList body) {
        int index = 0;
        for (Literal literal : body) {
            if (literal instanceof OrdinaryLiteral ordinaryLiteral) {
                if (ordinaryLiteral.isPositive() && ordinaryLiteral.isDerived()) {
                    return Optional.of(index);
                }
            }
            index++;
        }
        return Optional.empty();
    }

    private PredicateSpec[] computePredicateSpecs(LogicSchema schema) {
        List<PredicateSpec> predicateSpecs = schema.getAllPredicates().stream()
                .map(predicate -> new PredicateSpec(predicate.getName(), predicate.getArity()))
                .toList();
        return predicateSpecs.toArray(predicateSpecs.toArray(new PredicateSpec[0]));
    }

    private List<TermSpec> buildSpec(ImmutableTermList terms) {
        return terms.stream().map(t -> {
            if (t instanceof Constant) {
                return new ConstantSpec(t.getName());
            } else if (t instanceof Variable) {
                return new VariableSpec(t.getName());
            } else throw new RuntimeException("Unknown term type: " + t.getClass().getName());
        }).collect(Collectors.toList());
    }

    private BodySpec buildSpec(ImmutableLiteralsList body) {
        return new BodySpec(body.stream()
                .map(l -> {
                    if (l instanceof OrdinaryLiteral ordinaryLiteral) {
                        List<TermSpec> termSpecs = buildSpec(ordinaryLiteral.getTerms());
                        return new OrdinaryLiteralSpec(ordinaryLiteral.getAtom().getPredicateName(),
                                termSpecs,
                                ordinaryLiteral.isPositive());
                    } else if (l instanceof ComparisonBuiltInLiteral comparisonBuiltInLiteral) {
                        List<TermSpec> termSpecs = buildSpec(comparisonBuiltInLiteral.getTerms());
                        return new BuiltInLiteralSpec(comparisonBuiltInLiteral.getOperationName(), termSpecs);
                    } else throw new RuntimeException("Unknown literal type: " + l.getClass().getName());
                })
                .collect(Collectors.toList()));
    }
}
