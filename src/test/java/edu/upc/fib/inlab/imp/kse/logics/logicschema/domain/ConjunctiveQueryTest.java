package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.AtomMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.QueryMother;
import org.junit.jupiter.api.Test;

import static edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
class ConjunctiveQueryTest {

    @Test
    void isConjunctiveQuery() {
        ConjunctiveQuery query = QueryMother.createBooleanConjunctiveQuery("P(x), Q(x)");
        assertThat(query.isConjunctiveQuery()).isTrue();
    }

    @Test
    void bodyAtoms() {
        ConjunctiveQuery query = QueryMother.createBooleanConjunctiveQuery("P(x), Q(x)");
        assertThat(query.getBodyAtoms())
                .hasSize(2)
                .containsAtomByPredicateName(
                        AtomMother.createAtom("P", "x"))
                .containsAtomByPredicateName(
                        AtomMother.createAtom("Q", "x")
                );
    }

    @Test
    void sizeHeadTerms() {
        ConjunctiveQuery query = QueryMother.createBooleanConjunctiveQuery("P(x), Q(x)");
        assertThat(query.getHeadTerms()).hasSize(0);
    }

}