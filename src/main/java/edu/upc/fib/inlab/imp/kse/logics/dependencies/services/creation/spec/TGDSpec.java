package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.creation.spec;

import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicElementSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.OrdinaryLiteralSpec;

import java.util.List;
import java.util.Objects;

/**
 * Specification of a derivation rule.
 */
public class TGDSpec extends DependencySpec implements LogicElementSpec {

    private final List<OrdinaryLiteralSpec> headAtomSpecs;

    public TGDSpec(BodySpec body, List<OrdinaryLiteralSpec> headAtomsSpecs) {
        super(body);
        if (Objects.isNull(headAtomsSpecs)) throw new IllegalArgumentException("Head terms cannot be null");

        this.headAtomSpecs = headAtomsSpecs;
    }

    public TGDSpec(List<LiteralSpec> bodySpec, List<OrdinaryLiteralSpec> termSpecList) {
        this(new BodySpec(bodySpec), termSpecList);
    }

    public List<OrdinaryLiteralSpec> getHeadAtomSpecs() {
        return headAtomSpecs;
    }
}
