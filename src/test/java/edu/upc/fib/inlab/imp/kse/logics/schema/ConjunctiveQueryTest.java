package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.AtomMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.QueryMother;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
class ConjunctiveQueryTest {

    @Test
    void isConjunctiveQuery() {
        ConjunctiveQuery query = QueryMother.createConjunctiveQuery("P(x), Q(x)");
        assertThat(query.isConjunctiveQuery()).isTrue();
    }

    @Test
    void bodyAtoms() {
        ConjunctiveQuery query = QueryMother.createConjunctiveQuery("P(x), Q(x)");
        assertThat(query.getBodyAtoms())
                .hasSize(2)
                .containsAtomWithPredicateName(
                        AtomMother.createAtom("P", "x"))
                .containsAtomWithPredicateName(
                        AtomMother.createAtom("Q", "x")
                );
    }

    @Test
    void sizeHeadTerms() {
        ConjunctiveQuery query = QueryMother.createConjunctiveQuery("P(x), Q(x)");
        assertThat(query.getHeadTerms()).hasSize(0);
    }

}