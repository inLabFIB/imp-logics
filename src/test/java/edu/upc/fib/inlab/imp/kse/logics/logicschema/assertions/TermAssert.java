package edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Constant;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Term;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.ConstantSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.TermSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.VariableSpec;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class TermAssert extends AbstractAssert<TermAssert, Term> {

    public TermAssert(Term actual) {
        super(actual, TermAssert.class);
    }

    public static TermAssert assertThat(Term actual) {
        return new TermAssert(actual);
    }

    @SuppressWarnings("UnusedReturnValue")
    public TermAssert correspondsSpec(TermSpec spec) {
        Assertions.assertThat(actual.getName()).isEqualTo(spec.getName());

        if (actual instanceof Variable) {
            Assertions.assertThat(spec).isInstanceOf(VariableSpec.class);
        } else if (actual instanceof Constant) {
            Assertions.assertThat(spec).isInstanceOf(ConstantSpec.class);
        } else {
            throw new RuntimeException("Unrecognized Term class");
        }

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public TermAssert isVariable(String variableName) {
        Assertions.assertThat(actual).isInstanceOf(Variable.class);
        Assertions.assertThat(actual.getName()).isEqualTo(variableName);
        return this;
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public TermAssert isVariable() {
        Assertions.assertThat(actual).isInstanceOf(Variable.class);
        return this;
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public TermAssert isNotVariable() {
        Assertions.assertThat(actual).isNotInstanceOf(Variable.class);
        return this;
    }


    @SuppressWarnings("UnusedReturnValue")
    public TermAssert isConstant(String constantName) {
        Assertions.assertThat(actual).isInstanceOf(Constant.class);
        Assertions.assertThat(actual.getName()).isEqualTo(constantName);
        return this;
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public TermAssert isConstant() {
        Assertions.assertThat(actual).isInstanceOf(Constant.class);
        return this;
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public TermAssert isNotConstant() {
        Assertions.assertThat(actual).isNotInstanceOf(Constant.class);
        return this;
    }

}
