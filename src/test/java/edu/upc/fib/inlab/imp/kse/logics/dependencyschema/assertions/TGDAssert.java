package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.AtomAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Atom;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.OrdinaryLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.utils.LiteralParser;
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

    @SuppressWarnings("UnusedReturnValue")
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

    @SuppressWarnings("UnusedReturnValue")
    public TGDAssert hasAtom(String atomString) {
        Atom expectedAtom = ((OrdinaryLiteral) LiteralParser.parseLiteral(atomString)).getAtom();

        Assertions.assertThat(actual.getHead())
                .anySatisfy(atom -> AtomAssert.assertThat(atom)
                        .hasPredicateName(expectedAtom.getPredicate().getName())
                        .hasTerms(expectedAtom.getTerms()));
        return this;
    }
}
