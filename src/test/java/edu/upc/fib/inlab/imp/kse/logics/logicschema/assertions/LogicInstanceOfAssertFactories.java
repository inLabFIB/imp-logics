package edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.BuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ComparisonBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.OrdinaryLiteral;
import org.assertj.core.api.InstanceOfAssertFactory;

/**
 * A utils class to group all the InstanceOfAssertFactory of the IMP Logics project
 */
public interface LogicInstanceOfAssertFactories {
    InstanceOfAssertFactory<ImmutableLiteralsList, ImmutableLiteralsListAssert> IMMUTABLE_LITERALS_LIST = new InstanceOfAssertFactory<>(ImmutableLiteralsList.class, ImmutableLiteralsListAssert::assertThat);
    InstanceOfAssertFactory<OrdinaryLiteral, OrdinaryLiteralAssert> ORDINARY_LITERAL = new InstanceOfAssertFactory<>(OrdinaryLiteral.class, OrdinaryLiteralAssert::assertThat);
    InstanceOfAssertFactory<BuiltInLiteral, BuiltInLiteralAssert> BUILT_IN_LITERAL = new InstanceOfAssertFactory<>(BuiltInLiteral.class, BuiltInLiteralAssert::assertThat);
    InstanceOfAssertFactory<ComparisonBuiltInLiteral, ComparisonBuiltInLiteralAssert> COMPARISON_BUILT_IN_LITERAL = new InstanceOfAssertFactory<>(ComparisonBuiltInLiteral.class, ComparisonBuiltInLiteralAssert::assertThat);
}
