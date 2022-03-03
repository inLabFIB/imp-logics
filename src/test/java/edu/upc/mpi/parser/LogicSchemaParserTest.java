package edu.upc.mpi.parser;

import edu.upc.mpi.logicschema.*;
import edu.upc.mpi.logicschema.LogicSchemaTestHelper;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class LogicSchemaParserTest extends LogicSchemaTestHelper {
    
    public LogicSchemaParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
   

    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testLoadConstraint0() throws Exception {
        System.out.println("testLoadConstraint0");
        
        String constraint = "@1 :- P(X)\n";
        LogicSchemaParser instance = new LogicSchemaParser(constraint);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        LogicConstraint result = resultLogicSchema.getConstraintByNumber(1);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        List<Literal> body = new LinkedList();
        
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "P", new String[]{"X"}));
        
        LogicConstraint lc = new LogicConstraint(1, body);
        
        if(!result.equals(lc)){
            fail("Expected: "+lc+"\n"+ " but result is: "+result);
        }
        assertEquals(lc.toString(), result.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testLoadConstraint1() throws Exception {
        System.out.println("testLoadConstraint1");
        
        String constraint = "@1 :- P(X), Q(X, Y)\n";
        LogicSchemaParser instance = new LogicSchemaParser(constraint);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        LogicConstraint result = resultLogicSchema.getConstraintByNumber(1);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        List<Literal> body = new LinkedList();
        
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "P", new String[]{"X"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        
        LogicConstraint lc = new LogicConstraint(1, body);
        
        if(!result.equals(lc)){
            fail("Expected: "+lc+"\n"+ " but result is: "+result);
        }
        assertEquals(lc.toString(), result.toString());
    }
    
     /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testLoadConstraint2() throws Exception {
        System.out.println("testLoadConstraint2");
        
        String constraint = "@1 :- P(X), Q(X, Y), not(P(Y))\n";
        LogicSchemaParser instance = new LogicSchemaParser(constraint);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        LogicConstraint result = resultLogicSchema.getConstraintByNumber(1);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        List<Literal> body = new LinkedList();
        
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "P", new String[]{"X"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "P", new String[]{"Y"}, false));
        
        LogicConstraint lc = new LogicConstraint(1, body);
        
        if(!result.equals(lc)){
            fail("Expected: "+lc+"\n"+ " but result is: "+result);
        }
        assertEquals(lc.toString(), result.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testLoadConstraint3() throws Exception {
        System.out.println("testLoadConstraint3");
        
        String constraint = "@1 :- P(X), Q(X, Y), not(P(Y)), R(X, Y, Z)\n";
        LogicSchemaParser instance = new LogicSchemaParser(constraint);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        LogicConstraint result = resultLogicSchema.getConstraintByNumber(1);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        List<Literal> body = new LinkedList();
        
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "P", new String[]{"X"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "P", new String[]{"Y"}, false));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "R", new String[]{"X","Y","Z"}));
        
        LogicConstraint lc = new LogicConstraint(1, body);
        
        if(!result.equals(lc)){
            fail("Expected: "+lc+"\n"+ " but result is: "+result);
        }
        assertEquals(lc.toString(), result.toString());
    }

    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testLoadDerivationRule() throws Exception {
        System.out.println("testLoadDerivationRule");
        
        String derivationRule = "P(X) :- Q(X, Y)\n";
        LogicSchemaParser instance = new LogicSchemaParser(derivationRule);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        DerivationRule result = resultLogicSchema.getDerivationRulesByHead("P").get(0);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        List<Literal> body = new LinkedList();
        
        Atom head = this.getAtom(expectedLogicSchema, "P", new String[]{"X"});
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        
        DerivationRule lc = new DerivationRule(head, body);
        
        if(!result.equals(lc)){
            fail("Expected: "+lc+"\n"+ " but result is: "+result);
        }
        assertEquals(lc.toString(), result.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testLoadDerivationRule1() throws Exception {
        System.out.println("testLoadDerivationRule1");
        
        String derivationRule = "P(X) :- Q(X, Y), R(X, Y, Z)\n";
        LogicSchemaParser instance = new LogicSchemaParser(derivationRule);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        DerivationRule result = resultLogicSchema.getDerivationRulesByHead("P").get(0);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        List<Literal> body = new LinkedList();
        
        Atom head = this.getAtom(expectedLogicSchema, "P", new String[]{"X"});
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "R", new String[]{"X","Y","Z"}));
        
        DerivationRule lc = new DerivationRule(head, body);
        
        if(!result.equals(lc)){
            fail("Expected: "+lc+"\n"+ " but result is: "+result);
        }
        assertEquals(lc.toString(), result.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testLoadDerivationRule2() throws Exception {
        System.out.println("testLoadDerivationRule2");
        
        String derivationRule = "P(X) :- Q(X, Y), R(X, Y, Z), not(S(X,Y))\n";
        LogicSchemaParser instance = new LogicSchemaParser(derivationRule);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        DerivationRule result = resultLogicSchema.getDerivationRulesByHead("P").get(0);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        List<Literal> body = new LinkedList();
        
        Atom head = this.getAtom(expectedLogicSchema, "P", new String[]{"X"});
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "R", new String[]{"X","Y","Z"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "S", new String[]{"X","Y"}, false));
        
        DerivationRule lc = new DerivationRule(head, body);
        
        if(!result.equals(lc)){
            fail("Expected: "+lc+"\n"+ " but result is: "+result);
        }
        assertEquals(lc.toString(), result.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testLoadDerivationRule3() throws Exception {
        System.out.println("testLoadDerivationRule3");
        
        String derivationRule = "P(X) :- not(S(X,Y)), Q(X, Y), R(X, Y, Z)\n";
        LogicSchemaParser instance = new LogicSchemaParser(derivationRule);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        DerivationRule result = resultLogicSchema.getDerivationRulesByHead("P").get(0);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        List<Literal> body = new LinkedList();
        
        Atom head = this.getAtom(expectedLogicSchema, "P", new String[]{"X"});
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "S", new String[]{"X","Y"}, false));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "R", new String[]{"X","Y","Z"}));
        
        DerivationRule lc = new DerivationRule(head, body);
        
        if(!result.equals(lc)){
            fail("Expected: "+lc+"\n"+ " but result is: "+result);
        }
        assertEquals(lc.toString(), result.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testLoadDerivationRule4() throws Exception {
        System.out.println("testLoadDerivationRule4");
        
        String derivationRule1 = "P(X) :- Q(X, Y), R(X, Y, Z), not(S(X,Y))\n";
        String derivationRule2 = "P(Y) :- T(X, Y, Z)\n";
        LogicSchemaParser instance = new LogicSchemaParser(derivationRule1+derivationRule2);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        DerivationRule result1 = resultLogicSchema.getDerivationRulesByHead("P").get(0);
        DerivationRule result2 = resultLogicSchema.getDerivationRulesByHead("P").get(1);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        
        List<Literal> body = new LinkedList();
        Atom head = this.getAtom(expectedLogicSchema, "P", new String[]{"X"});
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "R", new String[]{"X","Y","Z"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "S", new String[]{"X","Y"}, false));
        DerivationRule lc1 = new DerivationRule(head, body);
        
        body = new LinkedList();
        head = this.getAtom(expectedLogicSchema, "P", new String[]{"Y"});
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "T", new String[]{"X","Y","Z"}));
        DerivationRule lc2 = new DerivationRule(head, body);
        
        if(!result1.equals(lc1)){
            fail("Expected: "+lc1+"\n"+ " but result is: "+result1);
        }
        if(!result2.equals(lc2)){
            fail("Expected: "+lc2+"\n"+ " but result is: "+result2);
        }
        assertEquals(lc1.toString(), result1.toString());
        assertEquals(lc2.toString(), result2.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testBuiltInLiteral() throws Exception {
        System.out.println("testBuiltInLiteral");
        
        String constraint = "@1 :- Q(X, Y), R(X, Y, Z), X > Y\n";
        LogicSchemaParser instance = new LogicSchemaParser(constraint);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        LogicConstraint result = resultLogicSchema.getConstraintByNumber(1);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        
        List<Literal> body = new LinkedList();
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "R", new String[]{"X","Y","Z"}));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), ">"));
        LogicConstraint expected = new LogicConstraint(1,body);
        
        if(!result.equals(expected)){
            fail("Expected: "+expected+"\n"+ " but result is: "+result);
        }
        assertEquals(expected.toString(), result.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testBuiltInLiteral1() throws Exception {
        System.out.println("testBuiltInLiteral1");
        
        String constraint = "@1 :- Q(X, Y), R(X, Y, Z), X < Y\n";
        LogicSchemaParser instance = new LogicSchemaParser(constraint);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        LogicConstraint result = resultLogicSchema.getConstraintByNumber(1);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        
        List<Literal> body = new LinkedList();
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "R", new String[]{"X","Y","Z"}));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<"));
        LogicConstraint expected = new LogicConstraint(1,body);
        
        if(!result.equals(expected)){
            fail("Expected: "+expected+"\n"+ " but result is: "+result);
        }
        assertEquals(expected.toString(), result.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testBuiltInLiteral2() throws Exception {
        System.out.println("testBuiltInLiteral2");
        
        String constraint = "@1 :- Q(X, Y), R(X, Y, Z), X = Y\n";
        LogicSchemaParser instance = new LogicSchemaParser(constraint);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        LogicConstraint result = resultLogicSchema.getConstraintByNumber(1);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        
        List<Literal> body = new LinkedList();
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "R", new String[]{"X","Y","Z"}));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "="));
        LogicConstraint expected = new LogicConstraint(1,body);
        
        if(!result.equals(expected)){
            fail("Expected: "+expected+"\n"+ " but result is: "+result);
        }
        assertEquals(expected.toString(), result.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testBuiltInLiteral3() throws Exception {
        System.out.println("testBuiltInLiteral3");
        
        String constraint = "@1 :- Q(X, Y), R(X, Y, Z), X <> Y\n";
        LogicSchemaParser instance = new LogicSchemaParser(constraint);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        LogicConstraint result = resultLogicSchema.getConstraintByNumber(1);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        
        List<Literal> body = new LinkedList();
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "R", new String[]{"X","Y","Z"}));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        LogicConstraint expected = new LogicConstraint(1,body);
        
        if(!result.equals(expected)){
            fail("Expected: "+expected+"\n"+ " but result is: "+result);
        }
        assertEquals(expected.toString(), result.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testBuiltInLiteral4() throws Exception {
        System.out.println("testBuiltInLiteral4");
        
        String constraint = "@1 :- Q(X, Y), R(X, Y, Z), X <= Y\n";
        LogicSchemaParser instance = new LogicSchemaParser(constraint);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        LogicConstraint result = resultLogicSchema.getConstraintByNumber(1);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        
        List<Literal> body = new LinkedList();
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "R", new String[]{"X","Y","Z"}));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<="));
        LogicConstraint expected = new LogicConstraint(1,body);
        
        if(!result.equals(expected)){
            fail("Expected: "+expected+"\n"+ " but result is: "+result);
        }
        assertEquals(expected.toString(), result.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testBuiltInLiteral5() throws Exception {
        System.out.println("testBuiltInLiteral5");
        
        String constraint = "@1 :- Q(X, Y), R(X, Y, Z), X >= Y\n";
        LogicSchemaParser instance = new LogicSchemaParser(constraint);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        LogicConstraint result = resultLogicSchema.getConstraintByNumber(1);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        
        List<Literal> body = new LinkedList();
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "R", new String[]{"X","Y","Z"}));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), ">="));
        LogicConstraint expected = new LogicConstraint(1,body);
        
        if(!result.equals(expected)){
            fail("Expected: "+expected+"\n"+ " but result is: "+result);
        }
        assertEquals(expected.toString(), result.toString());
    }
    
    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testLoadDerivationRule5() throws Exception {
        System.out.println("testLoadDerivationRule5");
        
        String comment = "% :- L(X)\n";
        String derivationRule1 = "P(X) :- Q(X, Y), R(X, Y, Z), not(S(X,Y))\n";
        String derivationRule2 = "P(Y) :- T(X, Y, Z)\n";
        LogicSchemaParser instance = new LogicSchemaParser(comment+derivationRule1+comment+derivationRule2);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        DerivationRule result1 = resultLogicSchema.getDerivationRulesByHead("P").get(0);
        DerivationRule result2 = resultLogicSchema.getDerivationRulesByHead("P").get(1);
        
        LogicSchema expectedLogicSchema = new LogicSchema();
        
        List<Literal> body = new LinkedList();
        Atom head = this.getAtom(expectedLogicSchema, "P", new String[]{"X"});
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "Q", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "R", new String[]{"X","Y","Z"}));
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "S", new String[]{"X","Y"}, false));
        DerivationRule lc1 = new DerivationRule(head, body);
        
        body = new LinkedList();
        head = this.getAtom(expectedLogicSchema, "P", new String[]{"Y"});
        body.add(this.getOrdinaryLiteral(expectedLogicSchema, "T", new String[]{"X","Y","Z"}));
        DerivationRule lc2 = new DerivationRule(head, body);
        
        if(!result1.equals(lc1)){
            fail("Expected: "+lc1+"\n"+ " but result is: "+result1);
        }
        if(!result2.equals(lc2)){
            fail("Expected: "+lc2+"\n"+ " but result is: "+result2);
        }
        assertEquals(lc1.toString(), result1.toString());
        assertEquals(lc2.toString(), result2.toString());
    }

    /**
     * Test of parse method, of class LogicSchemaParser.
     */
    @Test
    public void testLoadConstants() throws Exception {
        System.out.println("testLoadDerivationRule5");
        
        String derivationRule1 = "P(X) :- Q(X, Y), Y='true'\n";
        LogicSchemaParser instance = new LogicSchemaParser(derivationRule1);
        instance.parse();
        LogicSchema resultLogicSchema = instance.getLogicSchema();
        DerivationRule result1 = resultLogicSchema.getDerivationRulesByHead("P").get(0);
        BuiltInLiteral lit = (BuiltInLiteral) result1.getLiterals().get(1);
        System.out.println(lit.getTermNamesList().get(1));
        assertTrue(lit.getRightTerm().isConstant());
    }
    
    /**
     * Test of getReadFile method, of class LogicSchemaParser.
     */
    @Test
    public void testGetReadFile() {
        System.out.println("getReadFile");
        
        String schema = ":- P(X), Q(X)\n";
        schema += "Q(X) :- R(X, Y)\n";
        
        File file = null;
        try{
            file = File.createTempFile("testGetReadFile", "");
        } catch(Exception ex){
            fail(ex.getMessage());
        }
        assert file != null:"File should not bu null a this point";
        
        try(PrintWriter writer = new PrintWriter(file)) {
            writer.write(schema);
            writer.flush();
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
        
        LogicSchemaParser instance = new LogicSchemaParser(file);
        String result = instance.getReadFile(file);
        assertEquals(schema, result);
    }
}
