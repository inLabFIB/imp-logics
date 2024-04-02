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
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.OrdinaryLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.TermSpec;

import java.util.*;

import static edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.processes.SingleHeadTGDTransformer.obtainPredicateNames;

public class SingleExistentialVarTGDTransformer implements DependencyProcess {
    //todo: change this aux string for using original predicate name adding numbering at the end (collaborator should be
    // created)
    private static final String AUX_PREDICATE_NAME = "AUX";
    private int auxPredicateNameIndex = 1;

    @Override
    public DependencySchema execute(DependencySchema dependencySchema) {
        DependencySchemaBuilder builder = new DependencySchemaBuilder();
        Set<String> alreadyUsedPredicateNames = new HashSet<>(obtainPredicateNames(dependencySchema));

        for (Dependency dependency : dependencySchema.getAllDependencies()) {
            if (dependency instanceof EGD egd) {
                builder.addDependency(DependencySchemaToSpecHelper.buildEGDSpec(egd));
            } else if (dependency instanceof TGD tgd) {
                if (containsOneExistentiallyQuantifiedVariable(tgd)) {
                    builder.addDependency(DependencySchemaToSpecHelper.buildTGDSpec(tgd));
                } else {
                    builder.addAllDependencies(normalizeExistentiallyQuantifiedVariables(tgd, alreadyUsedPredicateNames));
                }
            } else throw new RuntimeException("Unrecognized kind of dependency: " + dependency.getClass().getName());
        }

        return builder.build();
    }

    public static boolean containsOneExistentiallyQuantifiedVariable(TGD tgd) {
        return tgd.getExistentialVariables().size() == 1;
    }

    private List<DependencySpec> normalizeExistentiallyQuantifiedVariables(TGD originalTGD, Set<String> alreadyUsedPredicateNames) {
        List<DependencySpec> result = new LinkedList<>();
        Set<Variable> allExistentiallyQuantifiedVariables = originalTGD.getExistentialVariables();

        Set<Variable> allVariablesOfBodyAndHead = originalTGD.getFrontierVariables();
        List<TermSpec> previousTermList = DependencySchemaToSpecHelper.buildTermsSpecs(new LinkedList<>(allVariablesOfBodyAndHead));
        BodySpec nextBody = DependencySchemaToSpecHelper.buildBodySpec(originalTGD.getBody());

        for (Variable nextVariable : allExistentiallyQuantifiedVariables) {
            List<TermSpec> newTermList = new ArrayList<>(previousTermList);
            newTermList.add(DependencySchemaToSpecHelper.buildTermSpec(nextVariable));

            String newAuxPredicateName = createNewAuxPredicateName(alreadyUsedPredicateNames);
            alreadyUsedPredicateNames.add(newAuxPredicateName);

            HeadAtomsSpec newHead = createNewHead(newTermList, newAuxPredicateName);
            result.add(new TGDSpec(nextBody, newHead));

            previousTermList = newTermList;
            nextBody = createBodyFromHead(newHead);
        }

        //Case with same head
        TGDSpec newTgd = new TGDSpec(nextBody, DependencySchemaToSpecHelper.buildHeadAtomsSpec(originalTGD.getHead()));
        result.add(newTgd);
        return result;
    }

    private String createNewAuxPredicateName(Set<String> usedPredicateNames) {
        String newAuxPredicate = AUX_PREDICATE_NAME + auxPredicateNameIndex++;
        while (usedPredicateNames.contains(newAuxPredicate)) {
            newAuxPredicate = AUX_PREDICATE_NAME + auxPredicateNameIndex++;
        }
        return newAuxPredicate;
    }

    private BodySpec createBodyFromHead(HeadAtomsSpec newHead) {
        List<LiteralSpec> literalSpecs = new LinkedList<>(newHead.atoms());
        return new BodySpec(literalSpecs);
    }

    private HeadAtomsSpec createNewHead(List<TermSpec> newTermList, String newAuxPredicateName) {
        return new HeadAtomsSpec(List.of(new OrdinaryLiteralSpec(newAuxPredicateName, newTermList)));
    }
}
