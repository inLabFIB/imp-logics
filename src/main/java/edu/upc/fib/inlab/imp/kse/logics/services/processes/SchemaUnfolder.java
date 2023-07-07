package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.LogicSchemaFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.*;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.LogicSchemaToSpecHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for recursively unfolding the positive derived literals of a logic schema.
 * <p>
 * For instance, if the logic schema contains:
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
 * <p>
 * This class, by default, applies the standard unfolding. That is, it only unfolds positive literals.
 * However, this class can also apply, if desired, some unfoldings over negated derived literals.
 * For instance, if the logic schema contains:
 * :- P(x), not(Derived(x)) <br>
 * Derived(x) :- A(x) <br>
 * The unfolding can return: <br>
 * :- P(x), not(A(x))  <br>
 * <p>
 * To apply the positive and also negated literal unfoldings, please, instantiate the class with
 * unfoldNegatedLiterals set to true.
 * <p>
 * To see the cases in which such unfolding can be performed, please,check OrdinaryLiteral class
 *
 * @see edu.upc.fib.inlab.imp.kse.logics.schema.OrdinaryLiteral#unfoldWithNegationExtension
 */
public class SchemaUnfolder extends LogicSchemaTransformationProcess {

    private final MultipleConstraintIDGenerator multipleConstraintIDGenerator;
    private final boolean unfoldNegatedLiterals;

    /**
     * Creates an SchemaUnfolder that will use the given multipleConstraintIDGenerator strategy
     * for creating new constraintIDs, if necessary.
     *
     * @param multipleConstraintIDGenerator not null
     * @param unfoldNegatedLiterals         , if true, the method tries to unfold negated literals too
     */
    public SchemaUnfolder(MultipleConstraintIDGenerator multipleConstraintIDGenerator, boolean unfoldNegatedLiterals) {
        checkParameters(multipleConstraintIDGenerator);
        this.multipleConstraintIDGenerator = multipleConstraintIDGenerator;
        this.unfoldNegatedLiterals = unfoldNegatedLiterals;
    }

    /**
     * Creates an SchemaUnfolder that will use the SuffixMultipleConstraintIDGenerator as a strategy
     * for creating new constraintIDs, if necessary; and use the unfolding specified by parameter
     */
    public SchemaUnfolder(boolean unfoldNegatedLiterals) {
        this(new SuffixMultipleConstraintIDGenerator(), unfoldNegatedLiterals);
    }

    /**
     * Creates an SchemaUnfolder that will use the SuffixMultipleConstraintIDGenerator as a strategy
     * for creating new constraintIDs, if necessary; and use the standard unfolding
     */
    public SchemaUnfolder() {
        this(false);
    }

    private static void checkParameters(MultipleConstraintIDGenerator multipleConstraintIDGenerator) {
        if (Objects.isNull(multipleConstraintIDGenerator))
            throw new IllegalArgumentException("MultipleConstraintIDGenerator cannot be null");
    }

    /**
     * @param logicSchema not null
     * @return a schema transformation where the final schema has the same (based & derived) predicates as the one given but unfolding
     * all the positive derived literals
     */
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

    private SchemaTransformation unfoldTransformation(LogicSchema schema) {
        checkLogicSchema(schema);

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
            for (ImmutableLiteralsList bodyWithUnfoldedLiteral : body.unfold(index, unfoldNegatedLiterals)) {
                result.addAll(computeUnfoldedBodySpec(bodyWithUnfoldedLiteral));
            }
            return result;
        } else return List.of(LogicSchemaToSpecHelper.buildBodySpec(body));
    }

    private Optional<Integer> getIndexOfUnfoldableLiteral(ImmutableLiteralsList body) {
        int index = 0;
        for (Literal literal : body) {
            if (literal instanceof OrdinaryLiteral ordinaryLiteral && ordinaryLiteral.isDerived()) {
                //We try to unfold the literal, and if we see a different one, it means that it can be unfolded
                List<ImmutableLiteralsList> unfoldingResult = ordinaryLiteral.unfold(unfoldNegatedLiterals);
                if (isNotTheSameAs(unfoldingResult, literal)) return Optional.of(index);
            }
            index++;
        }
        return Optional.empty();
    }

    private boolean isNotTheSameAs(List<ImmutableLiteralsList> unfoldingResult, Literal literal) {
        boolean isTheSameAs = unfoldingResult.size() == 1 &&
                unfoldingResult.get(0).size() == 1 &&
                unfoldingResult.get(0).get(0).equals(literal);
        return !isTheSameAs;
    }

    private PredicateSpec[] computePredicateSpecs(LogicSchema schema) {
        Set<Predicate> allPredicates = schema.getAllPredicates();
        List<PredicateSpec> predicateSpecs = LogicSchemaToSpecHelper.buildPredicatesSpecs(allPredicates);
        return predicateSpecs.toArray(predicateSpecs.toArray(new PredicateSpec[0]));
    }

}
