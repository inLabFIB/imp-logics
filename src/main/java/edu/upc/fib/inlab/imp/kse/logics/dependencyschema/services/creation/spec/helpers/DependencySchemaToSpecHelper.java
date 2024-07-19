package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.helpers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.DependencySpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.EGDSpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.HeadAtomsSpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.TGDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Atom;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.EqualityComparisonBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Term;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.OrdinaryLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.TermSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.LogicSchemaToSpecHelper;

import java.util.List;

public class DependencySchemaToSpecHelper {

    private DependencySchemaToSpecHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static DependencySpec buildEGDSpec(EGD egd) {
        return new EGDSpec(buildBodySpec(egd.getBody()), buildBuiltInSpec(egd.getHead()));
    }

    public static BodySpec buildBodySpec(List<Literal> body) {
        return LogicSchemaToSpecHelper.buildBodySpec(body);
    }

    private static BuiltInLiteralSpec buildBuiltInSpec(EqualityComparisonBuiltInLiteral equality) {
        return new BuiltInLiteralSpec(equality.getOperationName(), buildTermsSpecs(equality.getTerms()));
    }

    public static List<TermSpec> buildTermsSpecs(List<Term> terms) {
        return LogicSchemaToSpecHelper.buildTermsSpecs(terms);
    }

    public static TGDSpec buildTGDSpec(TGD originalTGD) {
        return new TGDSpec(buildBodySpec(originalTGD.getBody()), buildHeadAtomsSpec(originalTGD.getHead()));
    }

    public static HeadAtomsSpec buildHeadAtomsSpec(List<Atom> newHead) {
        List<OrdinaryLiteralSpec> literalSpecs = newHead.stream().map(atom ->
                                                                              new OrdinaryLiteralSpec(atom.getPredicateName(), buildTermsSpecs(atom.getTerms()), true))
                .toList();
        return new HeadAtomsSpec(literalSpecs);
    }

    public static TermSpec buildTermSpec(Term t) {
        return LogicSchemaToSpecHelper.buildTermSpec(t);
    }
}
