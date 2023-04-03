package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.LogicSchemaFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.*;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.LogicSchemaToSpecHelper;

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
public class SchemaUnfolder implements LogicProcess, SchemaTransformationProcess {

    private final MultipleConstraintIDGenerator multipleConstraintIDGenerator;

    public SchemaUnfolder() {
        this(new SuffixMultipleConstraintIDGenerator());
    }

    public SchemaUnfolder(MultipleConstraintIDGenerator multipleConstraintIDGenerator) {
        this.multipleConstraintIDGenerator = multipleConstraintIDGenerator;
    }

    @Override
    public LogicSchema execute(LogicSchema logicSchema) {
        return unfold(logicSchema);
    }

    @Override
    public SchemaTransformation executeTransformation(LogicSchema logicSchema) {
        return unfoldTransformation(logicSchema);
    }

    /**
     * @param schema not null
     * @return a new schema with the same (based & derived) predicates as the one given but unfolding
     * all the positive derived literals
     */
    public LogicSchema unfold(LogicSchema schema) {
        return unfoldTransformation(schema).transformed();
    }

    /**
     * @param schema not null
     * @return a schema transformation where the final schema has the same (based & derived) predicates as the one given but unfolding
     * all the positive derived literals
     */
    public SchemaTransformation unfoldTransformation(LogicSchema schema) {
        if (Objects.isNull(schema)) throw new IllegalArgumentException("Schema cannot be null");

        SchemaTraceabilityMap schemaTraceabilityMap = new SchemaTraceabilityMap();
        LogicSchemaSpec<LogicConstraintWithIDSpec> logicSchemaSpec = computeUnfoldedLogicSchemaSpec(schema, schemaTraceabilityMap);

        LogicSchema unfoldedSchema = LogicSchemaFactory.defaultLogicSchemaWithIDsFactory().createLogicSchema(logicSchemaSpec);
        return new SchemaTransformation(schema, unfoldedSchema, schemaTraceabilityMap);
    }

    private LogicSchemaSpec<LogicConstraintWithIDSpec> computeUnfoldedLogicSchemaSpec(LogicSchema schema, SchemaTraceabilityMap schemaTraceabilityMap) {
        LogicSchemaSpec<LogicConstraintWithIDSpec> logicSchemaSpec = new LogicSchemaSpec<>();
        logicSchemaSpec.addPredicateSpecs(computePredicateSpecs(schema));
        logicSchemaSpec.addDerivationRuleSpecs(computeUnfoldedDerivationRuleSpecs(schema));
        logicSchemaSpec.addLogicConstraintSpecs(computeUnfoldedLogicConstraintSpecs(schema, schemaTraceabilityMap));
        return logicSchemaSpec;
    }

    private LogicConstraintWithIDSpec[] computeUnfoldedLogicConstraintSpecs(LogicSchema schema, SchemaTraceabilityMap schemaTraceabilityMap) {
        List<LogicConstraintWithIDSpec> result = new LinkedList<>();
        for (LogicConstraint logicConstraint : schema.getAllLogicConstraints()) {
            result.addAll(computeUnfoldedLogicConstraintSpecs(logicConstraint, schemaTraceabilityMap));
        }
        return result.toArray(new LogicConstraintWithIDSpec[0]);
    }

    private List<LogicConstraintWithIDSpec> computeUnfoldedLogicConstraintSpecs(LogicConstraint originalConstraint, SchemaTraceabilityMap schemaTraceabilityMap) {
        List<BodySpec> unfoldedBodySpecs = computeUnfoldedBodySpec(originalConstraint.getBody());

        List<ConstraintID> constraintIDsToUse = multipleConstraintIDGenerator.generateNewConstraintsIDs(originalConstraint.getID(),
                unfoldedBodySpecs.size());

        List<LogicConstraintWithIDSpec> result = new LinkedList<>();
        for (int i = 0; i < unfoldedBodySpecs.size(); ++i) {
            ConstraintID newConstraintID = constraintIDsToUse.get(i);
            BodySpec newBodySpec = unfoldedBodySpecs.get(i);
            schemaTraceabilityMap.addConstraintIDOrigin(newConstraintID, originalConstraint.getID());
            result.add(new LogicConstraintWithIDSpec(newConstraintID.id(), newBodySpec));
        }

        return result;
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
