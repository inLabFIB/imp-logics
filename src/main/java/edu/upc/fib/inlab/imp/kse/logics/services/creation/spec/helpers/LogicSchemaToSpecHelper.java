package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.*;

import java.util.*;
import java.util.stream.Collectors;

public class LogicSchemaToSpecHelper {

    public static List<DerivationRuleSpec> buildDerivationRuleSpecs(List<DerivationRule> usedDerivationRules) {
        return usedDerivationRules.stream()
                .map(LogicSchemaToSpecHelper::buildDerivationRuleSpec)
                .toList();
    }

    private static DerivationRuleSpec buildDerivationRuleSpec(DerivationRule dr) {
        BodySpec bodySpec = buildBodySpec(dr.getBody());
        List<TermSpec> termSpecs = buildTermsSpecs(dr.getHead().getTerms());
        return new DerivationRuleSpec(dr.getHead().getPredicateName(), termSpecs, bodySpec);
    }

    public static List<LogicConstraintWithIDSpec> buildLogicConstraintSpecs(Set<LogicConstraint> logicConstraints) {
        return logicConstraints.stream()
                .map(LogicSchemaToSpecHelper::buildLogicConstraintSpec)
                .toList();
    }

    public static LogicConstraintWithIDSpec buildLogicConstraintSpec(LogicConstraint lc) {
        BodySpec bodySpec = buildBodySpec(lc.getBody());
        return new LogicConstraintWithIDSpec(lc.getID().id(), bodySpec);
    }

    public static List<PredicateSpec> buildPredicatesSpecs(Set<Predicate> allPredicates) {
        return allPredicates.stream()
                .map(LogicSchemaToSpecHelper::buildPredicateSpec)
                .toList();
    }

    private static PredicateSpec buildPredicateSpec(Predicate predicate) {
        return new PredicateSpec(predicate.getName(), predicate.getArity());
    }

    public static List<TermSpec> buildTermsSpecs(ImmutableTermList terms) {
        return terms.stream().map(t -> {
            if (t instanceof Constant) {
                return new ConstantSpec(t.getName());
            } else if (t instanceof Variable) {
                return new VariableSpec(t.getName());
            } else throw new RuntimeException("Unknown term type: " + t.getClass().getName());
        }).collect(Collectors.toList());
    }

    public static BodySpec buildBodySpec(ImmutableLiteralsList body) {
        return new BodySpec(body.stream()
                .map(l -> {
                    if (l instanceof OrdinaryLiteral ordinaryLiteral) {
                        return buildOrdinaryLiteralSpec(ordinaryLiteral);
                    } else if (l instanceof BuiltInLiteral comparisonBuiltInLiteral) {
                        return buildBuiltInLiteralSpec(comparisonBuiltInLiteral);
                    } else throw new RuntimeException("Unknown literal type: " + l.getClass().getName());
                })
                .collect(Collectors.toList()));
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


    public static final class BodySpecFragment extends LinkedList<LiteralSpec> {

        public BodySpecFragment() {
        }

        public BodySpecFragment(LiteralSpec... literals) {
            super(List.of(literals));
        }

        public BodySpecFragment(Collection<? extends LiteralSpec> c) {
            super(c);
        }
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
}
