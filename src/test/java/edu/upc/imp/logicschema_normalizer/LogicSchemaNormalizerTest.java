package edu.upc.imp.logicschema_normalizer;

import edu.upc.imp.logicschema.*;
import edu.upc.imp.parser.LogicSchemaParser;
import edu.upc.imp.pipeline.LogicSchemaProcess;
import edu.upc.imp.utils.LogicSchemaComparator;
import edu.upc.imp.utils.LogicSchemaTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LogicSchemaNormalizerTest extends LogicSchemaTestHelper {
    private LogicSchema logicSchema;

    @BeforeEach
    public void setUp() {
        logicSchema = new LogicSchema();
    }

    /**
     * Test of getSortedLiterals method, of class LogicSchemaNormalizer.
     */
    @Test
    public void testGetSortedLiterals() {
        System.out.println("getSortedLiterals");

        List<Literal> literals = new LinkedList<>();
        literals.add(this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X", "Y"}));
        literals.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<"));
        literals.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X"}, false));
        literals.add(this.getOrdinaryLiteral(logicSchema, "R", new String[]{"X", "Y"}, false));
        literals.add(this.getOrdinaryLiteral(logicSchema, "S", new String[]{"X"}));

        LogicSchemaNormalizer instance = new LogicSchemaNormalizer(this.logicSchema);
        List<Literal> expResult = new LinkedList<>();
        expResult.add(this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X", "Y"}));
        expResult.add(this.getOrdinaryLiteral(logicSchema, "S", new String[]{"X"}));
        expResult.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X"}, false));
        expResult.add(this.getOrdinaryLiteral(logicSchema, "R", new String[]{"X", "Y"}, false));
        expResult.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<"));
        List<Literal> result = instance.getSortedLiterals(literals);
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Test of getMultipleDerivationRuleUnfolding method, of class LogicSchemaNormalizer.
     */
    @Test
    public void testGetMultipleDerivationRuleUnfolding() {
        System.out.println("getMultipleDerivationRuleUnfolding");

        //Testing that a base literal is not modified
        OrdinaryLiteral olit = this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X"});
        LogicSchemaNormalizer instance = new LogicSchemaNormalizer(this.logicSchema);
        List<Literal> expResult = new LinkedList<>();
        expResult.add(olit);
        List<Literal> result = instance.getMultipleDerivationRuleUnfolding(olit);
        assertThat(result).isEqualTo(expResult);

        //Testing that a derived literal with only one derivation rule is not modified
        List<Literal> body1 = new LinkedList<>();
        body1.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}));
        body1.add(this.getOrdinaryLiteral(logicSchema, "R", new String[]{"X", "Y"}));
        new DerivationRule(olit.getAtom(), body1);
        expResult = new LinkedList<>();
        expResult.add(olit);
        result = instance.getMultipleDerivationRuleUnfolding(olit);
        assertThat(result).isEqualTo(expResult);

        //Testing that a derived literal with two derivation rules is modified
        List<Literal> body2 = new LinkedList<>();
        body2.add(this.getOrdinaryLiteral(logicSchema, "S", new String[]{"X"}));
        new DerivationRule(olit.getAtom(), body2);


        result = instance.getMultipleDerivationRuleUnfolding(olit);
        expResult = new LinkedList<>();
        expResult.add(this.getOrdinaryLiteral(logicSchema, "P1", new String[]{"X"}));
        expResult.add(this.getOrdinaryLiteral(logicSchema, "P2", new String[]{"X"}));
        assertThat(result).isEqualTo(expResult);
        OrdinaryLiteral P1 = (OrdinaryLiteral) result.get(0);
        OrdinaryLiteral P2 = (OrdinaryLiteral) result.get(1);
        assertThat(P1.getDefinitionRulesWhenCalled(new LinkedList<>())).size().isEqualTo(1);
        assertThat(P2.getDefinitionRulesWhenCalled(new LinkedList<>())).size().isEqualTo(1);
        assertThat(P1.getDefinitionRulesWhenCalled(new LinkedList<>())).first().isEqualTo(body1);
        assertThat(P2.getDefinitionRulesWhenCalled(new LinkedList<>())).first().isEqualTo(body2);
    }

    /**
     * Test of getUnfoldedLiterals method, of class LogicSchemaNormalizer.
     */
    @Test
    public void testGetUnfoldedLiterals() {
        System.out.println("getUnfoldedLiterals");

        List<Literal> literals = new LinkedList<>();
        LogicSchemaNormalizer instance = new LogicSchemaNormalizer(this.logicSchema);
        literals.add(this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X"}));
        literals.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}));
        literals.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}, false));
        literals.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<"));

        List<Literal> qDerivationBody1 = new LinkedList<>();
        qDerivationBody1.add(this.getOrdinaryLiteral(logicSchema, "S", new String[]{"A", "Y"}));
        new DerivationRule(this.getAtom(logicSchema, "Q", new String[]{"A", "Y"}), qDerivationBody1);
        List<Literal> qDerivationBody2 = new LinkedList<>();
        qDerivationBody2.add(this.getOrdinaryLiteral(logicSchema, "T", new String[]{"A", "Y", "X"}));
        new DerivationRule(this.getAtom(logicSchema, "Q", new String[]{"A", "Y"}), qDerivationBody2);

        List<List<Literal>> expResult = new LinkedList<>();
        List<Literal> result1 = new LinkedList<>();
        result1.add(this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X"}));
        result1.add(this.getOrdinaryLiteral(logicSchema, "S", new String[]{"X", "Y"}));
        result1.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}, false));
        result1.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<"));
        expResult.add(result1);

        List<Literal> result2 = new LinkedList<>();
        result2.add(this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X"}));
        result2.add(this.getOrdinaryLiteral(logicSchema, "T", new String[]{"X", "Y", "X_0"}));
        result2.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}, false));
        result2.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<"));
        expResult.add(result2);

        List<List<Literal>> result = instance.getUnfoldedLiterals(literals);
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Tests 2 problems related to variable name repetition when executing positive unfoldings.
     * It asserts that different variables from the derivation rule do not get mapped to the same new substitution variable.
     * --- 2nd term of head with first term of predicate A of the derivation rule of AUX
     * --- 1st and 2nd terms of predicate B of the derivation rule of AUX
     * <p>
     * INPUT:  [[AUX(_0_0,_2_0), Z(_2,_3)]]
     * AUX(_0,_1) :- A(_2,_0), B(_3,_3_0), _3_0<_1
     * OUTPUT: [[A(_2_1,_0_0), B(_3_0,_3_0_0), _3_0_0<_2_0, Z(_2,_3)]]
     * ERROR:  [[A(_2_0,_0_0), B(_3_0,_3_0), _3_0<_2_0, Z(_2,_3)]]
     */
    @Test
    public void testPositiveDerivationRulesUnfoldingsDontCauseVariableNameRepetitions() {
        System.out.println("Positive Unfoldings and Variable Name Repetitions");

        List<Literal> literals = new LinkedList<>();
        LogicSchemaNormalizer instance = new LogicSchemaNormalizer(this.logicSchema);
        literals.add(this.getOrdinaryLiteral(logicSchema, "AUX", new String[]{"_0_0", "_2_0"}));
        literals.add(this.getOrdinaryLiteral(logicSchema, "Z", new String[]{"_2", "_3"}));

        List<Literal> qDerivationBody1 = new LinkedList<>();
        qDerivationBody1.add(this.getOrdinaryLiteral(logicSchema, "A", new String[]{"_2", "_0"}));
        qDerivationBody1.add(this.getOrdinaryLiteral(logicSchema, "B", new String[]{"_3", "_3_0"}));
        qDerivationBody1.add(new BuiltInLiteral(new Term("_3_0"), new Term("_1"), "<"));
        new DerivationRule(this.getAtom(logicSchema, "AUX", new String[]{"_0", "_1"}), qDerivationBody1);

        List<List<Literal>> expResult = new LinkedList<>();
        List<Literal> result1 = new LinkedList<>();
        result1.add(this.getOrdinaryLiteral(logicSchema, "A", new String[]{"_2_1", "_0_0"}));
        result1.add(this.getOrdinaryLiteral(logicSchema, "B", new String[]{"_3_0", "_3_0_0"}));
        result1.add(new BuiltInLiteral(new Term("_3_0_0"), new Term("_2_0"), "<"));
        result1.add(this.getOrdinaryLiteral(logicSchema, "Z", new String[]{"_2", "_3"}));
        expResult.add(result1);

        List<List<Literal>> result = instance.getUnfoldedLiterals(literals);
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Assert that negative unfolding does not produce duplicate predicate names adding numbers at the end of the old name
     * INPUT:  [[P11(X,Y), not(P1(X,Y))]]
     * OUTPUT: [[P11(X,Y), not(P11.0(X,Y)), not(P12(X,Y))]]
     * ERROR:  [[P11(X,Y), not(P11(X,Y)), not(P12(X,Y))]]
     */
    @Test
    public void testPositiveDerivationRulesNegativeUnfoldingsDontCausePredicateNameRepetitions() {
        LogicSchemaNormalizer instance = new LogicSchemaNormalizer(this.logicSchema);
        OrdinaryLiteral olitP11 = this.getOrdinaryLiteral(logicSchema, "P11", new String[]{"X", "Y"});
        OrdinaryLiteral olitP1 = this.getOrdinaryLiteral(logicSchema, "P1", new String[]{"X", "Y"}, false);

        List<Literal> qDerivationBody1 = new LinkedList<>();
        qDerivationBody1.add(this.getOrdinaryLiteral(logicSchema, "A", new String[]{"X", "Y"}));
        new DerivationRule(this.getAtom(logicSchema, "P1", new String[]{"X", "Y"}), qDerivationBody1);
        List<Literal> qDerivationBody2 = new LinkedList<>();
        qDerivationBody2.add(this.getOrdinaryLiteral(logicSchema, "B", new String[]{"X", "Y"}));
        new DerivationRule(this.getAtom(logicSchema, "P1", new String[]{"X", "Y"}), qDerivationBody2);

        List<Literal> negativeUnfoldingResult = instance.getMultipleDerivationRuleUnfolding(olitP1);
        negativeUnfoldingResult.add(0, olitP11);

        List<List<Literal>> expResult = new LinkedList<>();
        List<Literal> result1 = new LinkedList<>();
        result1.add(this.getOrdinaryLiteral(logicSchema, "P11", new String[]{"X", "Y"}));
        result1.add(this.getOrdinaryLiteral(logicSchema, "P11.0", new String[]{"X", "Y"}, false));
        result1.add(this.getOrdinaryLiteral(logicSchema, "P12", new String[]{"X", "Y"}, false));
        expResult.add(result1);

        List<List<Literal>> result = new LinkedList<>();
        result.add(negativeUnfoldingResult);
        assertThat(result).isEqualTo(expResult);
    }

    @Test
    public void testNormalizerRemembersOriginalConstraintWithoutUnfolding() {
        LogicConstraint logicConstraint = createBasicLogicConstraint(logicSchema);
        logicSchema.addConstraint(logicConstraint);
        LogicSchemaNormalizer instance = new LogicSchemaNormalizer(logicSchema);
        instance.normalize();

        assertThat(instance.getNormalizedLogicSchema().getAllConstraints()).allSatisfy(newConstraint -> {
            //Action
            LogicConstraint originalConstraint = instance.getOriginalConstraint(newConstraint.getID());
            assertThat(originalConstraint).isEqualTo(logicConstraint);
        });
    }

    @Test
    public void testNormalizerRemembersOriginalConstraintWithPositiveUnfolding() {
        LogicSchema logicSchema = createLogicSchemaWithPositiveUnfolding();
        LogicConstraint logicConstraint = logicSchema.getAllConstraints().get(0);
        LogicSchemaNormalizer instance = new LogicSchemaNormalizer(logicSchema);
        instance.normalize();

        assertThat(instance.getNormalizedLogicSchema().getAllConstraints()).allSatisfy(newConstraint -> {
            //Action
            LogicConstraint originalConstraint = instance.getOriginalConstraint(newConstraint.getID());
            assertThat(originalConstraint).isEqualTo(logicConstraint);
        });
    }

    @Test
    public void testNormalizerRemembersOriginalConstraintWithNegativeUnfolding() {
        LogicSchema logicSchema = createLogicSchemaWithNegativeUnfolding();
        LogicConstraint logicConstraint = logicSchema.getAllConstraints().get(0);
        LogicSchemaNormalizer instance = new LogicSchemaNormalizer(logicSchema);
        instance.normalize();

        assertThat(instance.getNormalizedLogicSchema().getAllConstraints()).allSatisfy(newConstraint -> {
            //Action
            LogicConstraint originalConstraint = instance.getOriginalConstraint(newConstraint.getID());
            assertThat(originalConstraint).isEqualTo(logicConstraint);
        });
    }

    @Test
    public void testNormalizerDoesntCorruptInputSchema() {
        String logicSchemaString = "% Constraints\n" +
                "  @1 :- LINEITEM_L_ORDERKEY(_0,_1), LINEITEM_L_COMMITDATE(_0,_2), ORDERS_O_ORDERKEY(_3,_4), ORDERS_O_ORDERDATE(_3,_5), _1=_4, _2<_5\n" +
                "  @2 :- ORDERS_O_ORDERKEY(_0,_1), not(??aux4(_1))\n" +
                "  @3 :- LINEITEM_L_ORDERKEY(_0,_1), LINEITEM_L_SUPPKEY(_0,_2), SUPPLIER_S_SUPPKEY(_3,_4), SUPPLIER_S_NAME(_3,_5), _2=_4, ORDERS_O_ORDERKEY(_6,_7), ORDERS_O_CUSTKEY(_6,_8), _1=_7, CUSTOMER_C_CUSTKEY(_9,_10), CUSTOMER_C_NAME(_9,_11), _8=_10, _5=_11\n" +
                "  @4 :- LINEITEM_L_PARTKEY(_0,_1), LINEITEM_L_SUPPKEY(_0,_2), SUPPLIER_S_SUPPKEY(_3,_4), SUPPLIER_S_NATIONKEY(_3,_5), _2=_4, not(??aux13(_1,_5))\n" +
                "\n" +
                "% DerivationRules\n" +
                "  ??aux13(_0,_1) :- SUPPLIER_S_SUPPKEY(_2,_3), SUPPLIER_S_NATIONKEY(_2,_4), PARTSUPP_PS_PARTKEY(_5,_6), PARTSUPP_PS_SUPPKEY(_5,_7), _3=_7, _6=_0, _4<>_1\n" +
                "  ??aux4(_0) :- LINEITEM_L_ORDERKEY(_1,_2), _2=_0\n" +
                "\n";
        LogicSchemaParser parser = new LogicSchemaParser(logicSchemaString);
        parser.parse();
        LogicSchema logicSchema = parser.getLogicSchema();
        LogicSchemaProcess normalizer = new LogicSchemaNormalizer(logicSchema);
        normalizer.execute();
        LogicSchema normalizedSchema = normalizer.getOutputSchema();

        LogicSchemaComparator comparator = new LogicSchemaComparator(logicSchema);

        assertFalse(comparator.checkRepeatedObjectsWith(normalizedSchema));
    }

}
