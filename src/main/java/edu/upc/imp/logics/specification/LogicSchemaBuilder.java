package edu.upc.imp.logics.specification;

import edu.upc.imp.logics.schema.*;

import java.util.*;

public class LogicSchemaBuilder {

    private Set<PredicateSpec> predicateSpecs = new HashSet<>();
    private Set<LogicConstraintSpec> logicConstraintSpecs = new HashSet<>();
    private Set<DerivationRuleSpec> derivationRuleSpecs = new HashSet<>();
    private Set<BasePredicate> basePredicates;
    private Set<DerivedPredicate> derivedPredicates;

    public LogicSchemaBuilder addPredicate(String predicateName, int arity) {
        predicateSpecs.add(new PredicateSpec(predicateName, arity));
        return this;
    }

    public LogicSchemaBuilder addDerivationRuleSpec(DerivationRuleSpec derivationRuleSpec) {
        derivationRuleSpecs.add(derivationRuleSpec);
        return this;
    }

    public LogicSchema build() {
        Set<Predicate> predicates = buildPredicates();
        Set<LogicConstraint> constraints = buildConstraints();
        return new LogicSchema(predicates, constraints);
    }

    private Set<Predicate> buildPredicates() {
        basePredicates = buildBasePredicatesFromNormalClause();
        derivedPredicates = buildDerivedPredicates();
        basePredicates.addAll(buildSpecifiedPredicates());

        Set<Predicate> predicates = new HashSet<>(basePredicates);
        predicates.addAll(derivedPredicates);
        return predicates;
    }

    private Set<BasePredicate> buildSpecifiedPredicates() {
        Set<BasePredicate> specifiedPredicates = new HashSet<>();
        for (PredicateSpec predicateSpec: predicateSpecs) {
            if (basePredicates.stream().noneMatch(bp -> bp.getName().equals(predicateSpec.name())) &&
                    derivedPredicates.stream().noneMatch(bp -> bp.getName().equals(predicateSpec.name()))) {
                BasePredicate newBasePredicate = new BasePredicate(predicateSpec.name(), new Arity(predicateSpec.arity()));
                specifiedPredicates.add(newBasePredicate);
            }
        }
        return specifiedPredicates;
    }

    private Set<BasePredicate> buildBasePredicatesFromNormalClause() {
        Set<BasePredicate> basePredicatesFromNormalClauses = new HashSet<>();
        basePredicatesFromNormalClauses.addAll(buildBasePredicatesFromNormalClauses(logicConstraintSpecs));
        basePredicatesFromNormalClauses.addAll(buildBasePredicatesFromNormalClauses(derivationRuleSpecs));
        return basePredicatesFromNormalClauses;
    }

    private Set<BasePredicate> buildBasePredicatesFromNormalClauses(Set<? extends NormalClauseSpec> normalClauseSpecs) {
        Set<BasePredicate> basePredicatesFromNormalClauses = new HashSet<>();
        for (NormalClauseSpec normalClauseSpec: normalClauseSpecs) {
            Set<BasePredicate> basePredicatesOfNormalClause = buildBasePredicatesFromNormalClause(normalClauseSpec);
            basePredicatesFromNormalClauses.addAll(basePredicatesOfNormalClause);
        }
        return basePredicatesFromNormalClauses;
    }

    private Set<BasePredicate> buildBasePredicatesFromNormalClause(NormalClauseSpec normalClauseSpec) {
        Set<BasePredicate> basePredicates = new HashSet<>();
        for (LiteralSpec literalSpec: normalClauseSpec.getBody()) {
            if (literalSpec instanceof OrdinaryLiteralSpec ordinaryLiteralSpec){
                String predicateName = ordinaryLiteralSpec.getPredicateName();
                int arity = ordinaryLiteralSpec.getTermSpecList().size();
                basePredicates.add(new BasePredicate(predicateName, new Arity(arity)));
            }
        }
        return basePredicates;
    }

    private Set<DerivedPredicate> buildDerivedPredicates() {
        Set<DerivedPredicate> derivationPredicates = new HashSet<>();
        for(DerivationRuleSpec drs: derivationRuleSpecs) {
            List<Query> queries = List.of(buildQuery(drs.getTermSpecList(), drs.getBody()));
            derivationPredicates.add(new DerivedPredicate(drs.getPredicateName(), new Arity(drs.getTermSpecList().size()), queries));
        }

        return derivationPredicates;
    }

    private Query buildQuery(List<TermSpec> termSpecList, List<LiteralSpec> bodySpec) {
        List<Term> headTerms = TermSpecToTermFactory.buildTerms(termSpecList);
        List<Literal> body = buildBody(bodySpec);
        return new Query(headTerms, body);
    }

    private List<Literal> buildBody(List<LiteralSpec> bodySpec) {
        List<Literal> body = new LinkedList<>();
        for (LiteralSpec literalSpec: bodySpec) {
            if (literalSpec instanceof OrdinaryLiteralSpec olSpec) {
                body.add(buildOrdinaryLiteral(olSpec));
            }
            else throw new RuntimeException("Unrecognized literalSpec "+literalSpec.getClass().getName());
        }
        return body;
    }

    private Literal buildOrdinaryLiteral(OrdinaryLiteralSpec olSpec) {
        List<Term> terms = TermSpecToTermFactory.buildTerms(olSpec.getTermSpecList());
        Predicate predicate = findPredicate(olSpec.getPredicateName());
        return new OrdinaryLiteral(new Atom(predicate, terms), olSpec.isPositive());
    }

    private Predicate findPredicate(String predicateName) {
        return basePredicates.stream().filter(bs -> bs.getName().equals(predicateName)).findFirst().orElseThrow();
    }

    private Set<LogicConstraint> buildConstraints() {
        return Collections.emptySet();
    }
}
