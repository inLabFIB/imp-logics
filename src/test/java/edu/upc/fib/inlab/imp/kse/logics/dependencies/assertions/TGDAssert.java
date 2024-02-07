package edu.upc.fib.inlab.imp.kse.logics.dependencies.assertions;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.TGD;
import edu.upc.fib.inlab.imp.kse.logics.schema.Atom;
import edu.upc.fib.inlab.imp.kse.logics.schema.OrdinaryLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.AtomAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.LiteralParser;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class TGDAssert extends AbstractAssert<TGDAssert, TGD> {

    public TGDAssert(TGD tgd) {
        super(tgd, TGDAssert.class);
    }

    public static TGDAssert assertThat(TGD actual) {
        return new TGDAssert(actual);
    }

    @SuppressWarnings("UnusedReturnValue")
    public TGDAssert headOfSize(int expectedSize) {
        Assertions.assertThat(actual.getHead()).hasSize(expectedSize);
        return this;
    }

    public TGDAssert hasAtom(int index, String atomString) {
        Assertions.assertThat(actual.getHead())
                .withFailMessage("Expecting to have some element at index %d", index)
                .hasSizeGreaterThan(index);

        Atom expectedAtom = ((OrdinaryLiteral) LiteralParser.parseLiteral(atomString)).getAtom();
        Atom actualAtom = actual.getHead().get(index);

        AtomAssert.assertThat(actualAtom)
                .hasPredicateName(expectedAtom.getPredicate().getName())
                .hasTerms(expectedAtom.getTerms());
        return this;
    }
}
