package edu.upc.fib.inlab.imp.kse.logics.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.OrdinaryLiteralSpec;

import java.util.LinkedList;
import java.util.List;

/**
 * Class in charge of instantiating the body of some normal clause given the predicates of the schema
 */
class BodyBuilder {
    private final LiteralFactory literalFactory;
    private final List<Literal> body;

    public BodyBuilder(LiteralFactory literalFactory) {
        body = new LinkedList<>();
        this.literalFactory = literalFactory;
    }

    public BodyBuilder addLiteral(LiteralSpec literalSpec) {
        if (literalSpec instanceof OrdinaryLiteralSpec olSpec) {
            body.add(literalFactory.buildOrdinaryLiteral(olSpec));
        } else if (literalSpec instanceof BuiltInLiteralSpec biSpec) {
            body.add(literalFactory.buildBuiltInLiteral(biSpec));
        } else throw new RuntimeException("Unrecognized literalSpec " + literalSpec.getClass().getName());
        return this;
    }

    public BodyBuilder addLiterals(List<LiteralSpec> bodySpec) {
        bodySpec.forEach(this::addLiteral);
        return this;
    }

    public ImmutableLiteralsList build() {
        return new ImmutableLiteralsList(body);
    }
}
