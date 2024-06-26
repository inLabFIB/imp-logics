package edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaParser;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaWithIDsParser;

import java.util.Set;

public class DerivationRuleMother {

    private final static LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();

    public static DerivationRule create(String derivationRuleString) {
        LogicSchema domainSchema = parser.parse(derivationRuleString);
        Predicate predicate = domainSchema.getAllPredicates().stream()
                .filter(Predicate::isDerived)
                .findFirst().orElseThrow();
        return domainSchema.getDerivationRulesByPredicateName(predicate.getName()).get(0);
    }

    public static DerivationRule create(String schema, String predicateName) {
        LogicSchema domainSchema = parser.parse(schema);
        return domainSchema.getDerivationRulesByPredicateName(predicateName).get(0);
    }

    public static DerivationRule create(String schema, String derivedPredicateName, Set<String> customBuiltInOperators) {
        LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schema, customBuiltInOperators);
        return logicSchema.getDerivationRulesByPredicateName(derivedPredicateName).get(0);
    }
}
