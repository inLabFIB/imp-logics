package edu.upc.imp.logics.assertions;

import edu.upc.imp.logics.schema.Constant;
import edu.upc.imp.logics.schema.Term;
import edu.upc.imp.logics.schema.Variable;
import edu.upc.imp.logics.specification.ConstantSpec;
import edu.upc.imp.logics.specification.TermSpec;
import edu.upc.imp.logics.specification.VariableSpec;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class TermAssert extends AbstractAssert<TermAssert, Term> {

    public TermAssert(Term actual) {
        super(actual, TermAssert.class);
    }

    public static TermAssert assertThat(Term actual) {
        return new TermAssert(actual);
    }

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

}
