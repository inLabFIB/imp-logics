package edu.upc.fib.inlab.imp.kse.logics.schema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaParser;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaWithIDsParser;

public class DerivationRuleMother {

    private final static LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();

    public static DerivationRule create(String schema) {
        LogicSchema domainSchema = parser.parse(schema);
        Predicate predicate = domainSchema.getAllPredicates().stream()
                .filter(Predicate::isDerived)
                .findFirst().orElseThrow();
        return domainSchema.getDerivationRulesByPredicateName(predicate.getName()).get(0);
    }

    public static DerivationRule create(String schema, String predicateName) {
        LogicSchema domainSchema = parser.parse(schema);
        return domainSchema.getDerivationRulesByPredicateName(predicateName).get(0);
    }
}
