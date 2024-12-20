package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.DependencySpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.EGDSpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.TGDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.RepeatedPredicateNameException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.ContextTermFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.LiteralFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.OrdinaryLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.PredicateSpec;

import java.util.*;

/**
 * Builder that creates a DependencySchema given progressive info of a DependencySchema.
 */
public class DependencySchemaBuilder {

    private final Map<String, Predicate> predicatesByName = new LinkedHashMap<>();

    private final Set<Dependency> dependencies = new LinkedHashSet<>();

    public DependencySchemaBuilder() {
        this(Set.of());
    }

    public DependencySchemaBuilder(Set<Predicate> relationalSchema) {
        for (Predicate p : relationalSchema) predicatesByName.put(p.getName(), p);
    }

    @SuppressWarnings("UnusedReturnValue")
    @SafeVarargs
    public final DependencySchemaBuilder addDependency(DependencySpec... dependencySpecs) {
        return addDependency(Arrays.stream(dependencySpecs).toList());
    }

    public final DependencySchemaBuilder addDependency(Collection<DependencySpec> dependencySpecs) {
        dependencySpecs.forEach(this::addDependency);
        return this;
    }

    private void addDependency(DependencySpec dependencySpec) {
        ContextTermFactory contextTermFactory = new ContextTermFactory(dependencySpec.getAllVariableNames());
        ImmutableLiteralsList body = buildBody(dependencySpec.getBody(), contextTermFactory);
        if (dependencySpec instanceof TGDSpec tgdSpec) {
            List<LiteralSpec> castedList = tgdSpec.getHeadAtomSpecs().atoms()
                    .stream()
                    .map(LiteralSpec.class::cast)
                    .toList();
            ImmutableLiteralsList headAsLiterals = buildBody(castedList, contextTermFactory);
            List<Atom> atomList = headAsLiterals.stream()
                    .map(l -> (OrdinaryLiteral) l)
                    .map(OrdinaryLiteral::getAtom)
                    .toList();
            ImmutableAtomList head = new ImmutableAtomList(atomList);
            dependencies.add(new TGD(body, head));
        } else if (dependencySpec instanceof EGDSpec egdSpec) {
            EqualityComparisonBuiltInLiteral head = (EqualityComparisonBuiltInLiteral) (new LiteralFactory(predicatesByName, contextTermFactory)).buildBuiltInLiteral(egdSpec.getHead());
            dependencies.add(new EGD(body, head));
        } else throw new IMPLogicsException("Unknown Dependency type");
    }

    private ImmutableLiteralsList buildBody(List<LiteralSpec> bodySpec, ContextTermFactory contextTermFactory) {
        addPredicatesFromBody(bodySpec);
        return new BodyBuilder(new LiteralFactory(predicatesByName, contextTermFactory)).addLiterals(bodySpec).build();
    }

    private void addPredicatesFromBody(List<LiteralSpec> bodySpec) {
        for (LiteralSpec literalSpec : bodySpec) {
            if (literalSpec instanceof OrdinaryLiteralSpec olSpec) {
                int numberOfTerms = olSpec.getTermSpecList().size();
                addPredicateIfAbsent(olSpec.getPredicateName(), numberOfTerms);
            } else if (!(literalSpec instanceof BuiltInLiteralSpec)) {
                throw new IMPLogicsException("Unrecognized literalSpec " + literalSpec.getClass().getName());
            }
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public DependencySchemaBuilder addPredicate(PredicateSpec... predicateSpecs) {
        Arrays.stream(predicateSpecs).forEach(predicateSpec -> this.addPredicate(predicateSpec.name(), predicateSpec.arity()));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public DependencySchemaBuilder addPredicate(String predicateName, int arity) {
        addPredicateIfAbsent(predicateName, arity);
        return this;
    }

    public DependencySchema build() {
        return new DependencySchema(
                new LinkedHashSet<>(this.predicatesByName.values()),
                new LinkedHashSet<>(this.dependencies)
        );
    }

    public DependencySchemaBuilder addAllDependencies(List<DependencySpec> newDependencies) {
        newDependencies.forEach(this::addDependency);
        return this;
    }

    public DependencySchemaBuilder addAllPredicates(List<PredicateSpec> allPredicates) {
        allPredicates.forEach(this::addPredicate);
        return this;
    }

    private void addPredicateIfAbsent(String predicateName, int arity) {
        checkRepeatedNameWithDifferentArity(predicateName, arity);
        predicatesByName.putIfAbsent(predicateName, new MutablePredicate(predicateName, arity));
    }

    private void checkRepeatedNameWithDifferentArity(String predicateName, int arity) {
        if (predicatesByName.containsKey(predicateName)
                && predicatesByName.get(predicateName).getArity() != arity) {
            throw new RepeatedPredicateNameException(predicateName);
        }
    }
}
