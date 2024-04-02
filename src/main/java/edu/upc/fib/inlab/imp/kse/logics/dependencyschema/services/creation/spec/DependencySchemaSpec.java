package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.DerivationRuleSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicElementSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.PredicateSpec;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p> Specification of a whole dependency schema, that is, a specification of a set of predicates,
 * dependencies. </p>
 */
public class DependencySchemaSpec implements LogicElementSpec {
    private final List<PredicateSpec> predicateSpecList;
    private final List<DependencySpec> dependencySpecList;

    public DependencySchemaSpec() {
        predicateSpecList = new LinkedList<>();
        dependencySpecList = new LinkedList<>();
    }

    public void addPredicateSpecs(PredicateSpec... predicateSpecs) {
        this.predicateSpecList.addAll(Arrays.asList(predicateSpecs));
    }

    public void addPredicateSpecs(List<PredicateSpec> predicateSpecs) {
        this.predicateSpecList.addAll(predicateSpecs);
    }

    @SafeVarargs
    public final void addDependencySpecs(DependencySpec... dependencySpecs) {
        this.dependencySpecList.addAll(Arrays.asList(dependencySpecs));
    }

    public final void addDependencySpecs(List<DependencySpec> dependencySpecs) {
        this.dependencySpecList.addAll(dependencySpecs);
    }

    public List<PredicateSpec> getPredicateSpecList() {
        return Collections.unmodifiableList(predicateSpecList);
    }

    public List<DependencySpec> getDependencySpecList() {
        return Collections.unmodifiableList(dependencySpecList);
    }
}
