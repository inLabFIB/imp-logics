package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.DependencySpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.EGDSpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.TGDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.RepeatedPredicateName;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.LiteralFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.TermSpecToTermFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.*;

import java.util.*;

//TODO: document
public class DependencySchemaBuilder {

    private final Set<Dependency> dependencies = new LinkedHashSet<>();
    private final Map<String, MutablePredicate> predicatesByName = new HashMap<>();

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

    private void addPredicateIfAbsent(String predicateName, int arity) {
        checkRepeatedNameWithDifferentArity(predicateName, arity);
        predicatesByName.putIfAbsent(predicateName, new MutablePredicate(predicateName, arity));
    }

    private void checkRepeatedNameWithDifferentArity(String predicateName, int arity) {
        if (predicatesByName.containsKey(predicateName)
                && predicatesByName.get(predicateName).getArity() != arity) {
            throw new RepeatedPredicateName(predicateName);
        }
    }

    private Query buildQuery(List<TermSpec> termSpecList, List<LiteralSpec> bodySpec) {
        ImmutableTermList headTerms = TermSpecToTermFactory.buildTerms(termSpecList);
        ImmutableLiteralsList body = buildBody(bodySpec);
        return QueryFactory.createQuery(headTerms, body);
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
        ImmutableLiteralsList body = buildBody(dependencySpec.getBody());
        if (dependencySpec instanceof TGDSpec tgdSpec) {
            List<LiteralSpec> castedList = tgdSpec.getHeadAtomSpecs().literals()
                    .stream()
                    .map(LiteralSpec.class::cast)
                    .toList();
            ImmutableLiteralsList headAsLiterals = buildBody(castedList);
            List<Atom> atomList = headAsLiterals.stream()
                    .map(l -> (OrdinaryLiteral) l)
                    .map(OrdinaryLiteral::getAtom)
                    .toList();
            ImmutableAtomList head = new ImmutableAtomList(atomList);
            dependencies.add(new TGD(body, head));
        } else if (dependencySpec instanceof EGDSpec egdSpec) {
            EqualityComparisonBuiltInLiteral head = (EqualityComparisonBuiltInLiteral) (new LiteralFactory(predicatesByName)).buildBuiltInLiteral(egdSpec.getHead());
            dependencies.add(new EGD(body, head));
        } else throw new RuntimeException("Unknown Dependency type");
    }

    private ImmutableLiteralsList buildBody(List<LiteralSpec> bodySpec) {
        addPredicatesFromBody(bodySpec);
        return new BodyBuilder(new LiteralFactory(predicatesByName)).addLiterals(bodySpec).build();
    }

    private void addPredicatesFromBody(List<LiteralSpec> bodySpec) {
        for (LiteralSpec literalSpec : bodySpec) {
            if (literalSpec instanceof OrdinaryLiteralSpec olSpec) {
                int numberOfTerms = olSpec.getTermSpecList().size();
                addPredicateIfAbsent(olSpec.getPredicateName(), numberOfTerms);
            } else if (!(literalSpec instanceof BuiltInLiteralSpec)) {
                throw new RuntimeException("Unrecognized literalSpec " + literalSpec.getClass().getName());
            }
        }
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
}
