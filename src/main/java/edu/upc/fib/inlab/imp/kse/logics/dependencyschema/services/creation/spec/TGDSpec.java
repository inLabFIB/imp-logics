package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.OrdinaryLiteralSpec;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Specification of a derivation rule.
 */
public class TGDSpec extends DependencySpec {

    private final HeadAtomsSpec headAtomSpecs;

    public TGDSpec(List<LiteralSpec> bodySpec, List<OrdinaryLiteralSpec> termSpecList) {
        this(new BodySpec(bodySpec), new HeadAtomsSpec(termSpecList));
    }

    public TGDSpec(BodySpec body, HeadAtomsSpec headAtomsSpecs) {
        super(body);
        if (Objects.isNull(headAtomsSpecs)) throw new IllegalArgumentException("Head terms cannot be null");

        this.headAtomSpecs = headAtomsSpecs;
    }

    public HeadAtomsSpec getHeadAtomSpecs() {
        return headAtomSpecs;
    }

    @Override
    public Set<String> getAllVariableNames() {
        Set<String> result = new LinkedHashSet<>(headAtomSpecs.getAllVariableNames());
        result.addAll(new BodySpec(getBody()).getAllVariableNames());
        return result;
    }
}
