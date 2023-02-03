package edu.upc.imp.augmented_logicschema;

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

/**
 *
 */
public class LogicSchemaAugmenterTest extends LogicSchemaTestHelper {
    private LogicSchema logicSchema;
    
    public LogicSchemaAugmenterTest() {
    }
    
    @BeforeEach
    public void setUp() {
        logicSchema = new LogicSchema();
    }
    

    /**
     * Test of getAumgentedLiterals method, of class LogicSchemaAugmenter.
     */
    @Test
    public void testGetAugmentedLiterals() {
        List<Literal> body = new LinkedList<>();
        body.add(this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}, false));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        
        LogicSchemaAugmenter instance = new LogicSchemaAugmenter(logicSchema);
        instance.augment(); //It is mandatory to call augment so that the ins/del literals exists
        List<List<Literal>> result = instance.getAugmentedLiterals(body);

        List<List<Literal>> expResult = new LinkedList<>();
        body = new LinkedList<>();
        body.add(this.getOrdinaryLiteral(logicSchema, "ins_P", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(logicSchema, "del_Q", new String[]{"X", "Y"}));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        expResult.add(body);
        
        body = new LinkedList<>();
        body.add(this.getOrdinaryLiteral(logicSchema, "ins_P", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}, false));
        body.add(this.getOrdinaryLiteral(logicSchema, "ins_Q", new String[]{"X", "Y"}, false));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        expResult.add(body);
        
        body = new LinkedList<>();
        body.add(this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(logicSchema, "del_P", new String[]{"X","Y"}, false));
        body.add(this.getOrdinaryLiteral(logicSchema, "del_Q", new String[]{"X", "Y"}));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        expResult.add(body);
        
        body = new LinkedList<>();
        body.add(this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(logicSchema, "del_P", new String[]{"X","Y"}, false));
        body.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}, false));
        body.add(this.getOrdinaryLiteral(logicSchema, "ins_Q", new String[]{"X", "Y"}, false));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        expResult.add(body);

        assertThat(result).isEqualTo(expResult);
    }

    @Test
    public void testAugmenterRemembersOriginalConstraint(){
        List<Literal> body = new LinkedList<>();
        body.add(this.getOrdinaryLiteral(logicSchema, "P", new String[]{"X","Y"}));
        body.add(this.getOrdinaryLiteral(logicSchema, "Q", new String[]{"X", "Y"}, false));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        LogicConstraint logicConstraint = new LogicConstraint(10, body);
        logicSchema.addConstraint(logicConstraint);
        LogicSchemaAugmenter instance = new LogicSchemaAugmenter(logicSchema);
        instance.augment();

        assertThat(instance.getAugmentedLogicSchema().getAllConstraints()).allSatisfy(newConstraint -> {
            //Action
            LogicConstraint originalConstraint = instance.getOriginalConstraint(newConstraint.getID());
            assertThat(originalConstraint).isEqualTo(logicConstraint);
        });

    }

    @Test
    public void testAugmenterNormalizerDoesntCorruptInputSchema() {
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
        LogicSchemaProcess augmenter = new LogicSchemaAugmenter(logicSchema);
        augmenter.execute();
        LogicSchema augmentedSchema = augmenter.getOutputSchema();

        LogicSchemaComparator comparator = new LogicSchemaComparator(logicSchema);

        assertFalse(comparator.checkRepeatedObjectsWith(augmentedSchema));
    }

}
