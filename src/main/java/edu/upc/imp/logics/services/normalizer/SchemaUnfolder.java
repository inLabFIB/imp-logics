package edu.upc.imp.logics.services.normalizer;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.services.creation.LogicSchemaFactory;
import edu.upc.imp.logics.services.creation.spec.*;
import edu.upc.imp.logics.services.creation.spec.helpers.LogicSchemaToSpecHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>This class is responsible for recursively unfolding the positive derived literals of a logic schema.
 * For instance, if the logic schema contains:</p>
 * :- Der1(x), Base1(x) <br>
 * Der1(x) :- Base2(x) <br>
 * Der1(x) :- Der2(x) <br>
 * Der2(x) :- Base3(x) <br>
 * <br>
 * This class returns: <br>
 * :- Base2(x), Base1(x) <br>
 * :- Base3(x), Base1(x) <br>
 * Der1(x) :- Base2(x) <br>
 * Der1(x) :- Base3(x) <br>
 * Der2(x) :- Base3(x) <br>
 */
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
        ImmutableTermList terms = head.getTerms();
        List<TermSpec> termSpecs = LogicSchemaToSpecHelper.buildTermsSpecs(terms);
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
        } else return List.of(LogicSchemaToSpecHelper.buildBodySpec(body));
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
        Set<Predicate> allPredicates = schema.getAllPredicates();
        List<PredicateSpec> predicateSpecs = LogicSchemaToSpecHelper.buildPredicatesSpecs(allPredicates);
        return predicateSpecs.toArray(predicateSpecs.toArray(new PredicateSpec[0]));
    }


}
