package edu.upc.imp.logics.services.normalizer;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.services.creation.LogicSchemaFactory;
import edu.upc.imp.logics.services.creation.spec.DerivationRuleSpec;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithoutIDSpec;
import edu.upc.imp.logics.services.creation.spec.LogicSchemaSpec;
import edu.upc.imp.logics.services.creation.spec.PredicateSpec;

public class SchemaUnfolder {
    public LogicSchema unfold(LogicSchema schema) {
        LogicSchemaSpec<LogicConstraintWithoutIDSpec> logicSchemaSpec = new LogicSchemaSpec<>();
        logicSchemaSpec.addPredicateSpecs(computePredicateSpecs(schema));
        logicSchemaSpec.addDerivationRuleSpecs(computeUnfoldedDerivationRuleSpecs(schema));
        logicSchemaSpec.addLogicConstraintSpecs(computeUnfoldedLogicConstraintSpecs(schema));
        return LogicSchemaFactory.defaultLogicSchemaWithoutIDsFactory().createLogicSchema(logicSchemaSpec);
    }

    private LogicConstraintWithoutIDSpec computeUnfoldedLogicConstraintSpecs(LogicSchema schema) {
        return null;
    }

    private DerivationRuleSpec computeUnfoldedDerivationRuleSpecs(LogicSchema schema) {
        return null;
    }

    private PredicateSpec[] computePredicateSpecs(LogicSchema schema) {
        return (PredicateSpec[]) schema.getAllPredicates().stream()
                .map(predicate -> new PredicateSpec(predicate.getName(), predicate.getArity()))
                .toArray();
    }
}
