package edu.upc.imp.old.logicschema;

import edu.upc.imp.old.utils.LogicSchemaMother;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PredicateTest {


    @Test
    public void should_BeBase_WhenPredicateHasNoDerivationRule() {
        Predicate predicate = new PredicateImpl("P",2);
        assertThat(predicate.isBase()).isTrue();
    }

    @Test
    public void should_BeNotBase_WhenPredicateHasDerivationRule() {
        LogicSchema schema = LogicSchemaMother.buildLogicSchema(" P(x) :- Q(x, y)");
        Predicate predicateP = schema.getPredicate("P");
        assertThat(predicateP.isBase()).isFalse();
    }

    @Test
    public void should_computePredicateClosure_WhenPredicateIsBase() {
        Predicate predicate = new PredicateImpl("P", 2);
        Set<Predicate> result = predicate.getAllPredicatesClosureInDefinitionRules();
        assertThat(result).isEmpty();
    }

    @Test
    public void should_computePredicateClosure_WhenPredicateHasOneDerivationRule(){
        LogicSchema schema = LogicSchemaMother.buildLogicSchema(" P(x) :- Q(x, y), R(y, z)");
        Predicate predicateP = schema.getPredicate("P");
        Predicate predicateQ = schema.getPredicate("Q");
        Predicate predicateR = schema.getPredicate("R");
        Set<Predicate> result = predicateP.getAllPredicatesClosureInDefinitionRules();
        assertThat(result).containsExactlyInAnyOrder(predicateQ,predicateR);
    }

    @Test
    public void should_computePredicateClosure_WhenPredicateHasSeveralDerivationRules() {
        String schemaString = """
                P(x, y) :- Q(x, y)
                P(x, y) :- R(x), S(x, y)
                """;
        LogicSchema schema = LogicSchemaMother.buildLogicSchema(schemaString);
        Predicate p = schema.getPredicate("P");
        Predicate q = schema.getPredicate("Q");
        Predicate r = schema.getPredicate("R");
        Predicate s = schema.getPredicate("S");

        Set<Predicate> result = p.getAllPredicatesClosureInDefinitionRules();
        assertThat(result).containsExactlyInAnyOrder(q,r,s);
    }

    @Test
    public void should_computePredicateClosure_WhenDerivationRulesContainsMoreDerivations() {
        String schemaString = """
                P(x, y) :- Q(x, y)
                P(x, y) :- R(x), S(x, y)
                R(x) :- T(x, y), U(y)
                """;
        LogicSchema schema = LogicSchemaMother.buildLogicSchema(schemaString);
        Predicate p = schema.getPredicate("P");
        Predicate q = schema.getPredicate("Q");
        Predicate r = schema.getPredicate("R");
        Predicate s = schema.getPredicate("S");
        Predicate t = schema.getPredicate("T");
        Predicate u = schema.getPredicate("U");

        Set<Predicate> result = p.getAllPredicatesClosureInDefinitionRules();
        assertThat(result).containsExactlyInAnyOrder(q,r,s,t,u);
    }

    @Test
    public void should_computePredicateClosure_WhenPredicateIsRecursive() {
        String schemaString = """
                P(x, y) :- P(x, z), Q(z), P(z, y)
                P(x, y) :- R(x, y)
                """;
        LogicSchema schema = LogicSchemaMother.buildLogicSchema(schemaString);
        Predicate p = schema.getPredicate("P");
        Predicate q = schema.getPredicate("Q");
        Predicate r = schema.getPredicate("R");

        Set<Predicate> result = p.getAllPredicatesClosureInDefinitionRules();
        assertThat(result).containsExactlyInAnyOrder(p,q,r);
    }

    @Test
    public void should_computePredicateClosure_WhenThereArePredicatesNotInClosure() {
        String schemaString = """
                P(x, y) :- Q(z)
                R(x, y) :- S(x, y)
                """;
        LogicSchema schema = LogicSchemaMother.buildLogicSchema(schemaString);
        Predicate p = schema.getPredicate("P");
        Predicate q = schema.getPredicate("Q");

        Set<Predicate> result = p.getAllPredicatesClosureInDefinitionRules();
        assertThat(result).containsExactlyInAnyOrder(q);
    }
}
