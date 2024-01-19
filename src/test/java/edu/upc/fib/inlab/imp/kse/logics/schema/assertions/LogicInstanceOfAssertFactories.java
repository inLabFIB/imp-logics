package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import org.assertj.core.api.InstanceOfAssertFactory;

/**
 * A utils class to group all the InstanceOfAssertFactory of the IMP Logics project
 */
public interface LogicInstanceOfAssertFactories {
    InstanceOfAssertFactory<ImmutableLiteralsList, ImmutableLiteralsListAssert> IMMUTABLE_LITERALS_LIST = new InstanceOfAssertFactory<>(ImmutableLiteralsList.class, ImmutableLiteralsListAssert::assertThat);
}
