package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.DependencySchemaBuilder;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.DependencySpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.HeadAtomsSpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.TGDSpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.helpers.DependencySchemaToSpecHelper;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.processes.utils.PredicateNamingUtils;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Atom;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Term;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.OrdinaryLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.TermSpec;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Process to create an equivalent DependencySchema where each TGD only contains one rule in its head
 */
public class SingleHeadTGDTransformer implements DependencyProcess {
    private static final String AUX_PREDICATE_NAME_SUFFIX = "_SingleHead";

    @Override
    public DependencySchema execute(DependencySchema dependencySchema) {
        DependencySchemaBuilder builder = new DependencySchemaBuilder();
        Set<String> alreadyUsedPredicateNames = new LinkedHashSet<>(PredicateNamingUtils.obtainPredicateNames(dependencySchema));

        for (Dependency dependency : dependencySchema.getAllDependencies()) {
            if (dependency instanceof EGD egd) {
                builder.addDependency(DependencySchemaToSpecHelper.buildEGDSpec(egd));
            } else if (dependency instanceof TGD tgd) {
                if (containsOneHeadAtom(tgd)) {
                    builder.addDependency(DependencySchemaToSpecHelper.buildTGDSpec(tgd));
                } else {
                    String newAuxPredicateName = PredicateNamingUtils.createNewAuxPredicateName(tgd.getHead(), alreadyUsedPredicateNames, AUX_PREDICATE_NAME_SUFFIX);
                    alreadyUsedPredicateNames.add(newAuxPredicateName);
                    builder.addAllDependencies(obtainNewSpecsWithNewSingleHeadAtom(tgd, newAuxPredicateName));
                }
            } else throw new IMPLogicsException("Unknown subclass of dependency: " + dependency.getClass().getName());
        }
        return builder.build();
    }

    boolean containsOneHeadAtom(TGD tgd) {
        return tgd.getHead().size() == 1;
    }

    private List<DependencySpec> obtainNewSpecsWithNewSingleHeadAtom(TGD originalTGD, String newAuxPredicateName) {
        List<DependencySpec> result = new LinkedList<>();
        TGDSpec newTGDWithNewHead = createNewTGDSpecWithNewHead(originalTGD, newAuxPredicateName);
        result.add(newTGDWithNewHead);

        LiteralSpec newCreatedAtom = newTGDWithNewHead.getHeadAtomSpecs().atoms().get(0);
        BodySpec newBody = new BodySpec(List.of(newCreatedAtom));
        for (Atom atom : originalTGD.getHead()) {
            HeadAtomsSpec newHead = new HeadAtomsSpec(List.of(new OrdinaryLiteralSpec(atom.getPredicateName(), DependencySchemaToSpecHelper.buildTermsSpecs(atom.getTerms()))));
            TGDSpec newTGD = new TGDSpec(newBody, newHead);
            result.add(newTGD);
        }
        return result;
    }

    private TGDSpec createNewTGDSpecWithNewHead(TGD originalTGD, String auxPredicateName) {
        List<TermSpec> allVariablesOfHead = new LinkedList<>(obtainVariablesOfHead(originalTGD));
        return new TGDSpec(DependencySchemaToSpecHelper.buildBodySpec(originalTGD.getBody()),
                           new HeadAtomsSpec(List.of(new OrdinaryLiteralSpec(auxPredicateName, allVariablesOfHead))));
    }

    private static Set<TermSpec> obtainVariablesOfHead(TGD tgd) {
        return tgd.getHead().stream()
                .map(Atom::getVariables)
                .flatMap(Set::stream)
                .map(t -> (Term) t)
                .map(DependencySchemaToSpecHelper::buildTermSpec)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }


}
