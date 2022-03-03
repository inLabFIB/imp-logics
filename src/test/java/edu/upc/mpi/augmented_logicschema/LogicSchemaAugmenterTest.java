package edu.upc.mpi.augmented_logicschema;

import edu.upc.mpi.logicschema.BuiltInLiteral;
import edu.upc.mpi.logicschema.Literal;
import edu.upc.mpi.logicschema.LogicSchema;
import edu.upc.mpi.logicschema.Term;
import edu.upc.mpi.logicschema.LogicSchemaTestHelper;
import org.junit.*;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class LogicSchemaAugmenterTest extends LogicSchemaTestHelper {
    private LogicSchema logicSchema;
    
    public LogicSchemaAugmenterTest() {
    }
    
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
     * Test of getAumgentedLiterals method, of class LogicSchemaAugmenter.
     */
    @Test
    public void testGetAugmentedLiterals() {
        System.out.println("getAugmentedLiterals");
        
        List<Literal> body = new LinkedList();
        body.add(this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}, false));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        
        LogicSchemaAugmenter instance = new LogicSchemaAugmenter(logicSchema);
        instance.augment(); //It is mandatory to call augment so that the ins/del literals exists
        List<List<Literal>> result = instance.getAugmentedLiterals(body);
        System.out.println(result);
        
        List<List<Literal>> expResult = new LinkedList();
        body = new LinkedList();
        body.add(this.getOrdinaryLiteral(logicSchema, "ins_P", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(logicSchema, "del_Q", new String[]{"X", "Y"}));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        expResult.add(body);
        
        body = new LinkedList();
        body.add(this.getOrdinaryLiteral(logicSchema, "ins_P", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}, false));
        body.add(this.getOrdinaryLiteral(logicSchema, "ins_Q", new String[]{"X", "Y"}, false));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        expResult.add(body);
        
        body = new LinkedList();
        body.add(this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(logicSchema, "del_P", new String[]{"X","Y"}, false));
        body.add(this.getOrdinaryLiteral(logicSchema, "del_Q", new String[]{"X", "Y"}));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        expResult.add(body);
        
        body = new LinkedList();
        body.add(this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(logicSchema, "del_P", new String[]{"X","Y"}, false));
        body.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}, false));
        body.add(this.getOrdinaryLiteral(logicSchema, "ins_Q", new String[]{"X", "Y"}, false));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        expResult.add(body);
        
        assertEquals(expResult, result);
    }
    
}
