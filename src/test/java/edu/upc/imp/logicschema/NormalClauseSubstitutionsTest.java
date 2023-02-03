package edu.upc.imp.logicschema;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class NormalClauseSubstitutionsTest {
    private Predicate P = new PredicateImpl("P", 2);
    private Predicate Q = new PredicateImpl("Q", 2);
    private Predicate R = new PredicateImpl("R", 2);

    public List<Term> createTermsList(String[] termNames) {
        List<Term> result = new LinkedList<>();
        for (String termName : termNames) {
            result.add(new Term(termName));
        }
        return result;
    }

    @Test
    public void getVariableToVariableSubstitutionForLiteralsSimpleTest() {
        //Arrange
        List<Term> termsXY = createTermsList(new String[]{"x", "y"});
        OrdinaryLiteral PolXY = new OrdinaryLiteral(new Atom(P, termsXY));
        OrdinaryLiteral QolXY = new OrdinaryLiteral(new Atom(Q, termsXY));
        List<Literal> listXY = new LinkedList<>();
        listXY.add(PolXY);
        listXY.add(QolXY);

        List<Term> termsAB = createTermsList(new String[]{"a", "b"});
        OrdinaryLiteral PolAB = new OrdinaryLiteral(new Atom(P, termsAB));
        OrdinaryLiteral QolAB = new OrdinaryLiteral(new Atom(Q, termsAB));
        List<Literal> listAB = new LinkedList<>();
        listAB.add(PolAB);
        listAB.add(QolAB);

        //Action
        Map<String, String> substitution = NormalClause.getVariableToVariableSubstitutionForLiterals(new HashMap<>(), listXY, listAB);

        //Assert
        assertThat(substitution).size().isEqualTo(2);
        assertThat(substitution.get("x")).isEqualTo("a");
        assertThat(substitution.get("y")).isEqualTo("b");
    }

    @Test
    public void getVariableToVariableSubstitutionForLiteralsSimpleTest2() {
        //Arrange
        List<Term> termsXY = createTermsList(new String[]{"x", "y"});
        OrdinaryLiteral PolXY = new OrdinaryLiteral(new Atom(P, termsXY));
        OrdinaryLiteral QolXY = new OrdinaryLiteral(new Atom(Q, termsXY));
        List<Literal> listXY = new LinkedList<>();
        listXY.add(PolXY);
        listXY.add(QolXY);

        List<Term> termsAA = createTermsList(new String[]{"a", "a"});
        OrdinaryLiteral PolAB = new OrdinaryLiteral(new Atom(P, termsAA));
        OrdinaryLiteral QolAB = new OrdinaryLiteral(new Atom(Q, termsAA));
        List<Literal> listAA = new LinkedList<>();
        listAA.add(PolAB);
        listAA.add(QolAB);

        //Action
        Map<String, String> substitution = NormalClause.getVariableToVariableSubstitutionForLiterals(new HashMap<>(), listAA, listXY);

        //Assert
        assertThat(substitution).isNull();
    }

    @Test
    public void getVariableToVariableSubstitutionForLiteralsSimpleTest3() {
        //Arrange
        List<Term> termsXY = createTermsList(new String[]{"x", "y"});
        OrdinaryLiteral PolXY = new OrdinaryLiteral(new Atom(P, termsXY));
        OrdinaryLiteral QolXY = new OrdinaryLiteral(new Atom(Q, termsXY));

        List<Literal> listXY = new LinkedList<>();
        listXY.add(PolXY);
        listXY.add(QolXY);

        List<Term> termsAB = createTermsList(new String[]{"a", "b"});
        List<Term> termsAC = createTermsList(new String[]{"a", "c"});
        OrdinaryLiteral PolAC = new OrdinaryLiteral(new Atom(P, termsAC));
        OrdinaryLiteral PolAB = new OrdinaryLiteral(new Atom(P, termsAB));
        OrdinaryLiteral QolAB = new OrdinaryLiteral(new Atom(Q, termsAB));
        List<Literal> listAB = new LinkedList<>();
        listAB.add(PolAC);
        listAB.add(PolAB);
        listAB.add(QolAB);

        //Action
        Map<String, String> substitution = NormalClause.getVariableToVariableSubstitutionForLiterals(new HashMap<>(), listXY, listAB);

        //Assert
        assertThat(substitution).size().isEqualTo(2);
        assertThat(substitution.get("x")).isEqualTo("a");
        assertThat(substitution.get("y")).isEqualTo("b");
    }
}