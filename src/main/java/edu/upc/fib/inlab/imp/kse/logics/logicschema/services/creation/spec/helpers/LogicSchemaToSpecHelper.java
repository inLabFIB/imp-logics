package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.*;

import java.util.*;

public class LogicSchemaToSpecHelper {

    private LogicSchemaToSpecHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static List<DerivationRuleSpec> buildDerivationRuleSpecs(List<DerivationRule> usedDerivationRules) {
        return usedDerivationRules.stream()
                .map(LogicSchemaToSpecHelper::buildDerivationRuleSpec)
                .toList();
    }

    public static DerivationRuleSpec buildDerivationRuleSpec(DerivationRule dr) {
        BodySpec bodySpec = buildBodySpec(dr.getBody());
        List<TermSpec> termSpecs = buildTermsSpecs(dr.getHeadTerms());
        return new DerivationRuleSpec(dr.getHead().getPredicateName(), termSpecs, bodySpec);
    }

    public static DerivationRuleSpec buildDerivationRuleSpec(Atom head, ImmutableLiteralsList body) {
        List<TermSpec> headTerms = LogicSchemaToSpecHelper.buildTermsSpecs(head.getTerms());
        BodySpec bodySpec = LogicSchemaToSpecHelper.buildBodySpec(body);
        return new DerivationRuleSpecBuilder()
                .addHead(head.getPredicateName(), headTerms)
                .addAllLiteralSpecs(bodySpec.literals())
                .build();
    }

    @SuppressWarnings("unused")
    public static List<LogicConstraintWithIDSpec> buildLogicConstraintSpecs(Set<LogicConstraint> logicConstraints) {
        return logicConstraints.stream()
                .map(LogicSchemaToSpecHelper::buildLogicConstraintSpec)
                .toList();
    }

    public static LogicConstraintWithIDSpec buildLogicConstraintSpec(LogicConstraint lc) {
        BodySpec bodySpec = buildBodySpec(lc.getBody());
        return new LogicConstraintWithIDSpec(lc.getID().id(), bodySpec);
    }

    public static LogicConstraintWithIDSpec buildLogicConstraintSpec(ConstraintID id, List<Literal> body) {
        BodySpec bodySpec = LogicSchemaToSpecHelper.buildBodySpec(body);
        return new LogicConstraintWithIDSpecBuilder()
                .addConstraintId(id.id())
                .addAllLiteralSpecs(bodySpec.literals())
                .build();
    }

    public static List<PredicateSpec> buildPredicatesSpecs(Set<Predicate> allPredicates) {
        return allPredicates.stream()
                .map(LogicSchemaToSpecHelper::buildPredicateSpec)
                .toList();
    }

    private static PredicateSpec buildPredicateSpec(Predicate predicate) {
        return new PredicateSpec(predicate.getName(), predicate.getArity());
    }

    public static List<TermSpec> buildTermsSpecs(List<Term> terms) {
        return terms.stream().map(LogicSchemaToSpecHelper::buildTermSpec).toList();
    }

    public static TermSpec buildTermSpec(Term t) {
        if (t instanceof Constant) {
            return new ConstantSpec(t.getName());
        } else if (t instanceof Variable) {
            return new VariableSpec(t.getName());
        } else throw new IMPLogicsException("Unknown term type: " + t.getClass().getName());
    }

    public static BodySpec buildBodySpec(List<Literal> body) {
        return new BodySpec(body.stream()
                .map(l -> {
                    if (l instanceof OrdinaryLiteral ordinaryLiteral) {
                        return buildOrdinaryLiteralSpec(ordinaryLiteral);
                    } else if (l instanceof BuiltInLiteral comparisonBuiltInLiteral) {
                        return buildBuiltInLiteralSpec(comparisonBuiltInLiteral);
                    } else throw new IMPLogicsException("Unknown literal type: " + l.getClass().getName());
                })
                .toList());
    }

    public static OrdinaryLiteralSpec buildOrdinaryLiteralSpec(OrdinaryLiteral ordinaryLiteral) {
        ImmutableTermList terms = ordinaryLiteral.getTerms();
        List<TermSpec> termSpecs = buildTermsSpecs(terms);
        return new OrdinaryLiteralSpec(ordinaryLiteral.getAtom().getPredicateName(),
                termSpecs,
                ordinaryLiteral.isPositive());
    }

    public static BuiltInLiteralSpec buildBuiltInLiteralSpec(BuiltInLiteral builtInLiteral) {
        ImmutableTermList terms = builtInLiteral.getTerms();
        List<TermSpec> termSpecs = buildTermsSpecs(terms);
        return new BuiltInLiteralSpec(builtInLiteral.getOperationName(), termSpecs);
    }

    public static LiteralSpec buildLiteralSpec(Literal literal) {
        if (literal instanceof OrdinaryLiteral ol)
            return LogicSchemaToSpecHelper.buildOrdinaryLiteralSpec(ol);
        if (literal instanceof BuiltInLiteral bl)
            return LogicSchemaToSpecHelper.buildBuiltInLiteralSpec(bl);
        throw new IllegalArgumentException("Literal type not supported");
    }

    public static BuiltInLiteralSpec buildFalseLiteralSpec() {
        return LogicSchemaToSpecHelper.buildBuiltInLiteralSpec(new BooleanBuiltInLiteral(false));
    }

    public static BuiltInLiteralSpec buildTrueLiteralSpec() {
        return LogicSchemaToSpecHelper.buildBuiltInLiteralSpec(new BooleanBuiltInLiteral(true));
    }

    public static List<BodySpecFragment> cartesianProduct(List<BodySpecFragment> firstListOfFragments, List<BodySpecFragment> secondListOfFragments) {
        List<BodySpecFragment> result = new ArrayList<>();
        for (BodySpecFragment firstFragment : firstListOfFragments) {
            for (BodySpecFragment secondFragment : secondListOfFragments) {
                BodySpecFragment newFragment = new BodySpecFragment();
                newFragment.addAll(firstFragment);
                newFragment.addAll(secondFragment);
                result.add(newFragment);
            }
        }
        return result;
    }

    public static final class BodySpecFragment extends LinkedList<LiteralSpec> {

        public BodySpecFragment() {
        }

        @SuppressWarnings("unused")
        public BodySpecFragment(LiteralSpec... literals) {
            super(List.of(literals));
        }

        @SuppressWarnings("unused")
        public BodySpecFragment(Collection<? extends LiteralSpec> c) {
            super(c);
        }
    }
}
