package edu.upc.mpi.logicschema_normalizer;

import edu.upc.mpi.logicschema.*;
import edu.upc.mpi.logicschema.LogicSchemaTestHelper;
import org.junit.*;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class LogicSchemaNormalizerTest extends LogicSchemaTestHelper {
    private LogicSchema logicSchema;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        logicSchema = new LogicSchema();
    }

    @After
    public void tearDown() {
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
        assertEquals(expResult, result);
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
        assertEquals(expResult, result);

        //Testing that a derived literal with only one derivation rule is not modified
        List<Literal> body1 = new LinkedList<>();
        body1.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}));
        body1.add(this.getOrdinaryLiteral(logicSchema, "R", new String[]{"X", "Y"}));
        new DerivationRule(olit.getAtom(), body1);
        expResult = new LinkedList<>();
        expResult.add(olit);
        result = instance.getMultipleDerivationRuleUnfolding(olit);
        assertEquals(expResult, result);

        //Testing that a derived literal with two derivation rules is modified
        List<Literal> body2 = new LinkedList<>();
        body2.add(this.getOrdinaryLiteral(logicSchema, "S", new String[]{"X"}));
        new DerivationRule(olit.getAtom(), body2);


        result = instance.getMultipleDerivationRuleUnfolding(olit);
        expResult = new LinkedList<>();
        expResult.add(this.getOrdinaryLiteral(logicSchema, "P1", new String[]{"X"}));
        expResult.add(this.getOrdinaryLiteral(logicSchema, "P2", new String[]{"X"}));
        assertEquals(expResult, result);
        OrdinaryLiteral P1 = (OrdinaryLiteral) result.get(0);
        OrdinaryLiteral P2 = (OrdinaryLiteral) result.get(1);
        assertEquals(1, P1.getDefinitionRulesWhenCalled(new LinkedList<>()).size());
        assertEquals(1, P2.getDefinitionRulesWhenCalled(new LinkedList<>()).size());
        assertEquals(body1, P1.getDefinitionRulesWhenCalled(new LinkedList<>()).get(0));
        assertEquals(body2, P2.getDefinitionRulesWhenCalled(new LinkedList<>()).get(0));
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
        assertEquals(expResult, result);
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
        assertEquals(expResult, result);
    }
}
