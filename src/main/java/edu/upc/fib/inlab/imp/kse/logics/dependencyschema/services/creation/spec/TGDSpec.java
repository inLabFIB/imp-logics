package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LiteralSpec;

import java.util.List;
import java.util.Objects;

/**
 * Specification of a derivation rule.
 */
public class TGDSpec extends DependencySpec {

    private final BodySpec headAtomSpecs;

    public TGDSpec(BodySpec body, BodySpec headAtomsSpecs) {
        super(body);
        if (Objects.isNull(headAtomsSpecs)) throw new IllegalArgumentException("Head terms cannot be null");

        this.headAtomSpecs = headAtomsSpecs;
    }

    public TGDSpec(List<LiteralSpec> bodySpec, List<LiteralSpec> termSpecList) {
        this(new BodySpec(bodySpec), new BodySpec(termSpecList));
    }

    public BodySpec getHeadAtomSpecs() {
        return headAtomSpecs;
    }
}
