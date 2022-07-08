package edu.upc.mpi.tpch;

import edu.upc.mpi.augmented_logicschema.LogicSchemaAugmenter;
import edu.upc.mpi.logicschema.LogicSchema;
import edu.upc.mpi.logicschema_normalizer.LogicSchemaNormalizer;
import edu.upc.mpi.parser.LogicSchemaParser;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class TpcHGuillemTest {
   
    private void assertEqualsNoSpaces(String expected, String result) {
        expected = expected.replaceAll(" ","");
        result = result.replaceAll(" ", "");
        assertEquals(expected, result);
    }
    
    @Test @Ignore
    public void test() throws URISyntaxException {
        File tpcFile = new File(ClassLoader.getSystemResource("tpc-h-guillem.txt").toURI());
        assert tpcFile.exists():"File "+tpcFile.getAbsolutePath()+" does not exists";
        
        System.out.println("Parsing file");
        LogicSchemaParser parser = new LogicSchemaParser(tpcFile);
        parser.parse();
        LogicSchema schema = parser.getLogicSchema();
        checkParsedSchema(schema);
        System.out.println("Everything is ok");
        
        System.out.println("Augmenting schema");
        LogicSchemaAugmenter augmenter = new LogicSchemaAugmenter(schema);
        augmenter.augment();
        schema = augmenter.getAugmentedLogicSchema();
        checkAugmentedConstraints(schema);
        checkDerivationRules(schema);
        
        System.out.println("Normalizing schema");
        LogicSchemaNormalizer normalizer = new LogicSchemaNormalizer(schema);
        normalizer.normalize();
        schema = normalizer.getNormalizedLogicSchema();
        checkNormalizedConstraints(schema);
        checkNormalizedDerivationRules(schema);
    }
    
    private void checkParsedSchema(LogicSchema schema){
        String expected1 = "@1 :- LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_COMMITDATE(_0, _2), ORDERS_O_ORDERKEY(_3, _4), ORDERS_O_ORDERDATE(_3, _5), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected1, schema.getConstraintByNumber(1).toString());
        
        String expected2 = "@2 :- ORDERS_O_ORDERKEY(_0, _1), not(??aux4(_1))";
        assertEqualsNoSpaces(expected2, schema.getConstraintByNumber(2).toString());
        
        String expected3 = "@3 :- LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_SUPPKEY(_0, _2), SUPPLIER_S_SUPPKEY(_3, _4), SUPPLIER_S_NAME(_3, _5), _2=_4, ORDERS_O_ORDERKEY(_6, _7), ORDERS_O_CUSTKEY(_6, _8), _1=_7, CUSTOMER_C_CUSTKEY(_9, _10), CUSTOMER_C_NAME(_9, _11), _8=_10, _5=_11";
        assertEqualsNoSpaces(expected3, schema.getConstraintByNumber(3).toString());
        
        String expected4 = "@4 :- LINEITEM_L_PARTKEY(_0, _1), LINEITEM_L_SUPPKEY(_0, _2), SUPPLIER_S_SUPPKEY(_3, _4), SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, not(??aux13(_1, _5))";
        assertEqualsNoSpaces(expected4, schema.getConstraintByNumber(4).toString());
        
        String expected5 = "??aux4(_0):- LINEITEM_L_ORDERKEY(_1, _2), _2=_0";
        assertEqualsNoSpaces(expected5, schema.getDerivationRulesByHead("??aux4").get(0).toString());
        
        String expected6 = "??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected6, schema.getDerivationRulesByHead("??aux13").get(0).toString());
        
        int numberOfRules = schema.getAllConstraints().size();
        assertEquals(4, numberOfRules);
        
        numberOfRules = schema.getAllDerivationRules().size();
        assertEquals(2, numberOfRules);
    }

    private void checkAugmentedConstraints(LogicSchema augmentedLogicSchema) {
        String expected;
        expected = "@1000 :- ins_LINEITEM_L_ORDERKEY(_0, _1), ins_LINEITEM_L_COMMITDATE(_0, _2), ins_ORDERS_O_ORDERKEY(_3, _4), ins_ORDERS_O_ORDERDATE(_3, _5), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1000).toString());
        expected = "@1001 :- ins_LINEITEM_L_ORDERKEY(_0, _1), ins_LINEITEM_L_COMMITDATE(_0, _2), ins_ORDERS_O_ORDERKEY(_3, _4), ORDERS_O_ORDERDATE(_3, _5), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1001).toString());
        expected = "@1002 :- ins_LINEITEM_L_ORDERKEY(_0, _1), ins_LINEITEM_L_COMMITDATE(_0, _2), ORDERS_O_ORDERKEY(_3, _4), not(del_ORDERS_O_ORDERKEY(_3, _4)), ins_ORDERS_O_ORDERDATE(_3, _5), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1002).toString());
        expected = "@1003 :- ins_LINEITEM_L_ORDERKEY(_0, _1), ins_LINEITEM_L_COMMITDATE(_0, _2), ORDERS_O_ORDERKEY(_3, _4), not(del_ORDERS_O_ORDERKEY(_3, _4)), ORDERS_O_ORDERDATE(_3, _5), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1003).toString());
        expected = "@1004 :- ins_LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_COMMITDATE(_0, _2), not(del_LINEITEM_L_COMMITDATE(_0, _2)), ins_ORDERS_O_ORDERKEY(_3, _4), ins_ORDERS_O_ORDERDATE(_3, _5), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1004).toString());
        expected = "@1005 :- ins_LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_COMMITDATE(_0, _2), not(del_LINEITEM_L_COMMITDATE(_0, _2)), ins_ORDERS_O_ORDERKEY(_3, _4), ORDERS_O_ORDERDATE(_3, _5), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1005).toString());
        expected = "@1006 :- ins_LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_COMMITDATE(_0, _2), not(del_LINEITEM_L_COMMITDATE(_0, _2)), ORDERS_O_ORDERKEY(_3, _4), not(del_ORDERS_O_ORDERKEY(_3, _4)), ins_ORDERS_O_ORDERDATE(_3, _5), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1006).toString());
        expected = "@1007 :- ins_LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_COMMITDATE(_0, _2), not(del_LINEITEM_L_COMMITDATE(_0, _2)), ORDERS_O_ORDERKEY(_3, _4), not(del_ORDERS_O_ORDERKEY(_3, _4)), ORDERS_O_ORDERDATE(_3, _5), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1007).toString());
        expected = "@1008 :- LINEITEM_L_ORDERKEY(_0, _1), not(del_LINEITEM_L_ORDERKEY(_0, _1)), ins_LINEITEM_L_COMMITDATE(_0, _2), ins_ORDERS_O_ORDERKEY(_3, _4), ins_ORDERS_O_ORDERDATE(_3, _5), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1008).toString());
        expected = "@1009 :- LINEITEM_L_ORDERKEY(_0, _1), not(del_LINEITEM_L_ORDERKEY(_0, _1)), ins_LINEITEM_L_COMMITDATE(_0, _2), ins_ORDERS_O_ORDERKEY(_3, _4), ORDERS_O_ORDERDATE(_3, _5), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1009).toString());
        expected = "@1010 :- LINEITEM_L_ORDERKEY(_0, _1), not(del_LINEITEM_L_ORDERKEY(_0, _1)), ins_LINEITEM_L_COMMITDATE(_0, _2), ORDERS_O_ORDERKEY(_3, _4), not(del_ORDERS_O_ORDERKEY(_3, _4)), ins_ORDERS_O_ORDERDATE(_3, _5), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1010).toString());
        expected = "@1011 :- LINEITEM_L_ORDERKEY(_0, _1), not(del_LINEITEM_L_ORDERKEY(_0, _1)), ins_LINEITEM_L_COMMITDATE(_0, _2), ORDERS_O_ORDERKEY(_3, _4), not(del_ORDERS_O_ORDERKEY(_3, _4)), ORDERS_O_ORDERDATE(_3, _5), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1011).toString());
        expected = "@1012 :- LINEITEM_L_ORDERKEY(_0, _1), not(del_LINEITEM_L_ORDERKEY(_0, _1)), LINEITEM_L_COMMITDATE(_0, _2), not(del_LINEITEM_L_COMMITDATE(_0, _2)), ins_ORDERS_O_ORDERKEY(_3, _4), ins_ORDERS_O_ORDERDATE(_3, _5), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1012).toString());
        expected = "@1013 :- LINEITEM_L_ORDERKEY(_0, _1), not(del_LINEITEM_L_ORDERKEY(_0, _1)), LINEITEM_L_COMMITDATE(_0, _2), not(del_LINEITEM_L_COMMITDATE(_0, _2)), ins_ORDERS_O_ORDERKEY(_3, _4), ORDERS_O_ORDERDATE(_3, _5), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1013).toString());
        expected = "@1014 :- LINEITEM_L_ORDERKEY(_0, _1), not(del_LINEITEM_L_ORDERKEY(_0, _1)), LINEITEM_L_COMMITDATE(_0, _2), not(del_LINEITEM_L_COMMITDATE(_0, _2)), ORDERS_O_ORDERKEY(_3, _4), not(del_ORDERS_O_ORDERKEY(_3, _4)), ins_ORDERS_O_ORDERDATE(_3, _5), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1014).toString());

        expected = "@2000 :- ins_ORDERS_O_ORDERKEY(_0, _1), del_??aux4(_1)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(2000).toString());
        expected = "@2001 :- ins_ORDERS_O_ORDERKEY(_0, _1), not(??aux4(_1)), not(ins_??aux4(_1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(2001).toString());
        expected = "@2002 :- ORDERS_O_ORDERKEY(_0, _1), not(del_ORDERS_O_ORDERKEY(_0, _1)), del_??aux4(_1)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(2002).toString());
        
        //We do not check the rules @3xxx because there are 255 rules!
        
        expected = "@4000 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4000).toString());
        expected = "@4001 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4001).toString());
        expected = "@4002 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)),_2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4002).toString());
        expected = "@4003 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4003).toString());
        expected = "@4004 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4004).toString());
        expected = "@4005 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4005).toString());
        expected = "@4006 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4006).toString());
        expected = "@4007 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4007).toString());     
        expected = "@4008 :- ins_LINEITEM_L_PARTKEY(_0, _1), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4008).toString());
        expected = "@4009 :- ins_LINEITEM_L_PARTKEY(_0, _1), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4009).toString());
        expected = "@4010 :- ins_LINEITEM_L_PARTKEY(_0, _1), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), ins_SUPPLIER_S_SUPPKEY(_3, _4), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4010).toString());
        expected = "@4011 :- ins_LINEITEM_L_PARTKEY(_0, _1), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), ins_SUPPLIER_S_SUPPKEY(_3, _4), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4011).toString());
        expected = "@4012 :- ins_LINEITEM_L_PARTKEY(_0, _1), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4012).toString());
        expected = "@4013 :- ins_LINEITEM_L_PARTKEY(_0, _1), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4013).toString());
        expected = "@4014 :- ins_LINEITEM_L_PARTKEY(_0, _1), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4014).toString());
        expected = "@4015 :- ins_LINEITEM_L_PARTKEY(_0, _1), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4015).toString());    
        expected = "@4016 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4016).toString());
        expected = "@4017 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4017).toString());
        expected = "@4018 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4018).toString());
        expected = "@4019 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4019).toString());
        expected = "@4020 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), ins_LINEITEM_L_SUPPKEY(_0, _2), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4020).toString());
        expected = "@4021 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), ins_LINEITEM_L_SUPPKEY(_0, _2), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4021).toString());
        expected = "@4022 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), ins_LINEITEM_L_SUPPKEY(_0, _2), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4022).toString());
        expected = "@4023 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), ins_LINEITEM_L_SUPPKEY(_0, _2), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4023).toString());     
        expected = "@4024 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4024).toString());
        expected = "@4025 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4025).toString());
        expected = "@4026 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), ins_SUPPLIER_S_SUPPKEY(_3, _4), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4026).toString());
        expected = "@4027 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), ins_SUPPLIER_S_SUPPKEY(_3, _4), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4027).toString());
        expected = "@4028 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4028).toString());
        expected = "@4029 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), ins_SUPPLIER_S_NATIONKEY(_3, _5), _2=_4, not(??aux13(_1, _5)), not(ins_??aux13(_1, _5))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4029).toString());
        expected = "@4030 :- LINEITEM_L_PARTKEY(_0, _1), not(del_LINEITEM_L_PARTKEY(_0, _1)), LINEITEM_L_SUPPKEY(_0, _2), not(del_LINEITEM_L_SUPPKEY(_0, _2)), SUPPLIER_S_SUPPKEY(_3, _4), not(del_SUPPLIER_S_SUPPKEY(_3, _4)), SUPPLIER_S_NATIONKEY(_3, _5), not(del_SUPPLIER_S_NATIONKEY(_3, _5)), _2=_4, del_??aux13(_1, _5)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4030).toString());      
        
        int numberOfRules = augmentedLogicSchema.getAllConstraints().size();
        assertEquals(304, numberOfRules);
    }

    private void checkDerivationRules(LogicSchema augmentedLogicSchema) {
        String expected;
        
        expected = "??aux4(_0):- LINEITEM_L_ORDERKEY(_1, _2), _2=_0";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux4").get(0).toString());
        
        expected = "??aux4'(_0) :- ins_LINEITEM_L_ORDERKEY(_1, _2), _2=_0";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux4'").get(0).toString());
        expected = "??aux4'(_0) :- LINEITEM_L_ORDERKEY(_1, _2), not(del_LINEITEM_L_ORDERKEY(_1, _2)), _2=_0";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux4'").get(1).toString());
        
        expected = "ins_??aux4(_0) :- ins_LINEITEM_L_ORDERKEY(_1, _2), _2=_0, not(??aux4(_0))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux4").get(0).toString());
        
        expected = "del_??aux4(_0) :- del_LINEITEM_L_ORDERKEY(_1, _2), _2=_0, not(??aux4'(_0))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("del_??aux4").get(0).toString());
        
        expected = "??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13").get(0).toString());
        
        expected = "??aux13'(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5, _6), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(0).toString());
        expected = "??aux13'(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(1).toString());        
        expected = "??aux13'(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(2).toString());
        expected = "??aux13'(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(3).toString());     
        expected = "??aux13'(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), ins_PARTSUPP_PS_PARTKEY(_5, _6), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(4).toString());
        expected = "??aux13'(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), ins_PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(5).toString());        
        expected = "??aux13'(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(6).toString());
        expected = "??aux13'(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(7).toString());         
        expected = "??aux13'(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5, _6), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(8).toString());
        expected = "??aux13'(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(9).toString());        
        expected = "??aux13'(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(10).toString());
        expected = "??aux13'(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(11).toString());     
        expected = "??aux13'(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), ins_PARTSUPP_PS_PARTKEY(_5, _6), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(12).toString());
        expected = "??aux13'(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), ins_PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(13).toString());        
        expected = "??aux13'(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(14).toString());
        expected = "??aux13'(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)),PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("??aux13'").get(15).toString());        
        
        expected = "ins_??aux13(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5, _6), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(0).toString());
        expected = "ins_??aux13(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(1).toString());        
        expected = "ins_??aux13(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(2).toString());
        expected = "ins_??aux13(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(3).toString());     
        expected = "ins_??aux13(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), ins_PARTSUPP_PS_PARTKEY(_5, _6), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(4).toString());
        expected = "ins_??aux13(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), ins_PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(5).toString());        
        expected = "ins_??aux13(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(6).toString());
        expected = "ins_??aux13(_0, _1):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(7).toString());         
        expected = "ins_??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5, _6), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(8).toString());
        expected = "ins_??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(9).toString());        
        expected = "ins_??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(10).toString());
        expected = "ins_??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(11).toString());     
        expected = "ins_??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), ins_PARTSUPP_PS_PARTKEY(_5, _6), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(12).toString());
        expected = "ins_??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), ins_PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), not(del_PARTSUPP_PS_SUPPKEY(_5, _7)), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(13).toString());        
        expected = "ins_??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), SUPPLIER_S_NATIONKEY(_2, _4), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), PARTSUPP_PS_PARTKEY(_5, _6), not(del_PARTSUPP_PS_PARTKEY(_5, _6)), ins_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1, not(??aux13(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_??aux13").get(14).toString());
 
        expected = "del_??aux13(_0, _1):- del_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1, not(??aux13'(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("del_??aux13").get(0).toString());
        expected = "del_??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), del_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1, not(??aux13'(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("del_??aux13").get(1).toString());
        expected = "del_??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), del_PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1, not(??aux13'(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("del_??aux13").get(2).toString());
        expected = "del_??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), del_PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1, not(??aux13'(_0, _1))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("del_??aux13").get(3).toString());               
        
        int numberOfRules = augmentedLogicSchema.getAllDerivationRules().size();
        assertEquals(41, numberOfRules);
    }

    private void checkNormalizedConstraints(LogicSchema schema) {
        String expected;
        expected = "@100000 :- ins_LINEITEM_L_ORDERKEY(_0, _1), ins_LINEITEM_L_COMMITDATE(_0, _2), ins_ORDERS_O_ORDERKEY(_3, _4), ins_ORDERS_O_ORDERDATE(_3, _5), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100000).toString());
        expected = "@100100 :- ins_LINEITEM_L_ORDERKEY(_0, _1), ins_LINEITEM_L_COMMITDATE(_0, _2), ins_ORDERS_O_ORDERKEY(_3, _4), ORDERS_O_ORDERDATE(_3, _5), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100100).toString());
        expected = "@100200 :- ins_LINEITEM_L_ORDERKEY(_0, _1), ins_LINEITEM_L_COMMITDATE(_0, _2), ORDERS_O_ORDERKEY(_3, _4), ins_ORDERS_O_ORDERDATE(_3, _5), not(del_ORDERS_O_ORDERKEY(_3, _4)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100200).toString());
        expected = "@100300 :- ins_LINEITEM_L_ORDERKEY(_0, _1), ins_LINEITEM_L_COMMITDATE(_0, _2), ORDERS_O_ORDERKEY(_3, _4), ORDERS_O_ORDERDATE(_3, _5), not(del_ORDERS_O_ORDERKEY(_3, _4)), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100300).toString());
        expected = "@100400 :- ins_LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_COMMITDATE(_0, _2), ins_ORDERS_O_ORDERKEY(_3, _4), ins_ORDERS_O_ORDERDATE(_3, _5), not(del_LINEITEM_L_COMMITDATE(_0, _2)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100400).toString());
        expected = "@100500 :- ins_LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_COMMITDATE(_0, _2), ins_ORDERS_O_ORDERKEY(_3, _4), ORDERS_O_ORDERDATE(_3, _5), not(del_LINEITEM_L_COMMITDATE(_0, _2)), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100500).toString());
        expected = "@100600 :- ins_LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_COMMITDATE(_0, _2), ORDERS_O_ORDERKEY(_3, _4), ins_ORDERS_O_ORDERDATE(_3, _5), not(del_LINEITEM_L_COMMITDATE(_0, _2)), not(del_ORDERS_O_ORDERKEY(_3, _4)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100600).toString());
        expected = "@100700 :- ins_LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_COMMITDATE(_0, _2), ORDERS_O_ORDERKEY(_3, _4), ORDERS_O_ORDERDATE(_3, _5), not(del_LINEITEM_L_COMMITDATE(_0, _2)), not(del_ORDERS_O_ORDERKEY(_3, _4)), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100700).toString());
        expected = "@100800 :- LINEITEM_L_ORDERKEY(_0, _1), ins_LINEITEM_L_COMMITDATE(_0, _2), ins_ORDERS_O_ORDERKEY(_3, _4), ins_ORDERS_O_ORDERDATE(_3, _5), not(del_LINEITEM_L_ORDERKEY(_0, _1)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100800).toString());
        expected = "@100900 :- LINEITEM_L_ORDERKEY(_0, _1), ins_LINEITEM_L_COMMITDATE(_0, _2), ins_ORDERS_O_ORDERKEY(_3, _4), ORDERS_O_ORDERDATE(_3, _5), not(del_LINEITEM_L_ORDERKEY(_0, _1)), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100900).toString());
        expected = "@101000 :- LINEITEM_L_ORDERKEY(_0, _1), ins_LINEITEM_L_COMMITDATE(_0, _2), ORDERS_O_ORDERKEY(_3, _4), ins_ORDERS_O_ORDERDATE(_3, _5), not(del_LINEITEM_L_ORDERKEY(_0, _1)), not(del_ORDERS_O_ORDERKEY(_3, _4)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(101000).toString());
        expected = "@101100 :- LINEITEM_L_ORDERKEY(_0, _1), ins_LINEITEM_L_COMMITDATE(_0, _2), ORDERS_O_ORDERKEY(_3, _4), ORDERS_O_ORDERDATE(_3, _5), not(del_LINEITEM_L_ORDERKEY(_0, _1)), not(del_ORDERS_O_ORDERKEY(_3, _4)), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(101100).toString());
        expected = "@101200 :- LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_COMMITDATE(_0, _2), ins_ORDERS_O_ORDERKEY(_3, _4), ins_ORDERS_O_ORDERDATE(_3, _5), not(del_LINEITEM_L_ORDERKEY(_0, _1)), not(del_LINEITEM_L_COMMITDATE(_0, _2)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(101200).toString());
        expected = "@101300 :- LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_COMMITDATE(_0, _2), ins_ORDERS_O_ORDERKEY(_3, _4), ORDERS_O_ORDERDATE(_3, _5), not(del_LINEITEM_L_ORDERKEY(_0, _1)), not(del_LINEITEM_L_COMMITDATE(_0, _2)), not(del_ORDERS_O_ORDERDATE(_3, _5)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(101300).toString());
        expected = "@101400 :- LINEITEM_L_ORDERKEY(_0, _1), LINEITEM_L_COMMITDATE(_0, _2), ORDERS_O_ORDERKEY(_3, _4), ins_ORDERS_O_ORDERDATE(_3, _5), not(del_LINEITEM_L_ORDERKEY(_0, _1)), not(del_LINEITEM_L_COMMITDATE(_0, _2)), not(del_ORDERS_O_ORDERKEY(_3, _4)), _1=_4, _2<_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(101400).toString());
        
        expected = "@200000 :- ins_ORDERS_O_ORDERKEY(_0, _1), del_LINEITEM_L_ORDERKEY(_1_0, _2), not(??aux4'1(_1)), not(??aux4'2(_1)), _2=_1";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(200000).toString());
        expected = "@200100 :- ins_ORDERS_O_ORDERKEY(_0, _1), not(??aux4(_1)), not(ins_??aux4(_1))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(200100).toString());
        expected = "@200200 :- ORDERS_O_ORDERKEY(_0, _1), del_LINEITEM_L_ORDERKEY(_1_0, _2), not(del_ORDERS_O_ORDERKEY(_0, _1)), not(??aux4'1(_1)), not(??aux4'2(_1)), _2=_1";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(200200).toString());
        
        //We do not check the rules @3xxx because there are 255 rules!
        
        //We do not check all the ruels @4xxx becaure there are too many
        expected = "@400000 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), del_SUPPLIER_S_SUPPKEY(_2_0, _3_0), SUPPLIER_S_NATIONKEY(_2_0, _4_0), PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(??aux13'1(_1, _5)), not(??aux13'2(_1, _5)), not(??aux13'3(_1, _5)), not(??aux13'4(_1, _5)), not(??aux13'5(_1, _5)), not(??aux13'6(_1, _5)), not(??aux13'7(_1, _5)), not(??aux13'8(_1, _5)), not(??aux13'9(_1, _5)), not(??aux13'10(_1, _5)), not(??aux13'11(_1, _5)), not(??aux13'12(_1, _5)), not(??aux13'13(_1, _5)), not(??aux13'14(_1, _5)), not(??aux13'15(_1, _5)), not(??aux13'16(_1, _5)), _2=_4, _3_0=_7, _6=_1, _4_0<>_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400000).toString());
        expected = "@400001 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), SUPPLIER_S_SUPPKEY(_2_0, _3_0), del_SUPPLIER_S_NATIONKEY(_2_0, _4_0), PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(??aux13'1(_1, _5)), not(??aux13'2(_1, _5)), not(??aux13'3(_1, _5)), not(??aux13'4(_1, _5)), not(??aux13'5(_1, _5)), not(??aux13'6(_1, _5)), not(??aux13'7(_1, _5)), not(??aux13'8(_1, _5)), not(??aux13'9(_1, _5)), not(??aux13'10(_1, _5)), not(??aux13'11(_1, _5)), not(??aux13'12(_1, _5)), not(??aux13'13(_1, _5)), not(??aux13'14(_1, _5)), not(??aux13'15(_1, _5)), not(??aux13'16(_1, _5)), _2=_4, _3_0=_7, _6=_1, _4_0<>_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400001).toString());
        expected = "@400002 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), SUPPLIER_S_SUPPKEY(_2_0, _3_0), SUPPLIER_S_NATIONKEY(_2_0, _4_0), del_PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(??aux13'1(_1, _5)), not(??aux13'2(_1, _5)), not(??aux13'3(_1, _5)), not(??aux13'4(_1, _5)), not(??aux13'5(_1, _5)), not(??aux13'6(_1, _5)), not(??aux13'7(_1, _5)), not(??aux13'8(_1, _5)), not(??aux13'9(_1, _5)), not(??aux13'10(_1, _5)), not(??aux13'11(_1, _5)), not(??aux13'12(_1, _5)), not(??aux13'13(_1, _5)), not(??aux13'14(_1, _5)), not(??aux13'15(_1, _5)), not(??aux13'16(_1, _5)),  _2=_4, _3_0=_7, _6=_1, _4_0<>_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400002).toString());
        expected = "@400003 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), SUPPLIER_S_SUPPKEY(_2_0, _3_0), SUPPLIER_S_NATIONKEY(_2_0, _4_0), PARTSUPP_PS_PARTKEY(_5_0, _6), del_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(??aux13'1(_1, _5)), not(??aux13'2(_1, _5)), not(??aux13'3(_1, _5)), not(??aux13'4(_1, _5)), not(??aux13'5(_1, _5)), not(??aux13'6(_1, _5)), not(??aux13'7(_1, _5)), not(??aux13'8(_1, _5)), not(??aux13'9(_1, _5)), not(??aux13'10(_1, _5)), not(??aux13'11(_1, _5)), not(??aux13'12(_1, _5)), not(??aux13'13(_1, _5)), not(??aux13'14(_1, _5)), not(??aux13'15(_1, _5)), not(??aux13'16(_1, _5)), _2=_4, _3_0=_7, _6=_1, _4_0<>_5";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400003).toString());
        assertNull(schema.getConstraintByNumber(400004));
        
        expected = "@400100 :- ins_LINEITEM_L_PARTKEY(_0, _1), ins_LINEITEM_L_SUPPKEY(_0, _2), ins_SUPPLIER_S_SUPPKEY(_3, _4), ins_SUPPLIER_S_NATIONKEY(_3, _5), not(??aux13(_1, _5)), not(ins_??aux131(_1, _5)), not(ins_??aux132(_1, _5)), not(ins_??aux133(_1, _5)), not(ins_??aux134(_1, _5)), not(ins_??aux135(_1, _5)), not(ins_??aux136(_1, _5)), not(ins_??aux137(_1, _5)), not(ins_??aux138(_1, _5)), not(ins_??aux139(_1, _5)), not(ins_??aux1310(_1, _5)), not(ins_??aux1311(_1, _5)), not(ins_??aux1312(_1, _5)), not(ins_??aux1313(_1, _5)), not(ins_??aux1314(_1, _5)), not(ins_??aux1315(_1, _5)), _2=_4";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400100).toString());
        assertNull(schema.getConstraintByNumber(400101));
    }

    private void checkNormalizedDerivationRules(LogicSchema schema) {
        String expected;
        
        expected = "??aux4(_0):- LINEITEM_L_ORDERKEY(_1, _2), _2=_0";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux4").get(0).toString());
        
        expected = "??aux4'1(_1) :- ins_LINEITEM_L_ORDERKEY(_1_0, _2), _2=_1";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux4'1").get(0).toString());
        expected = "??aux4'2(_1) :- LINEITEM_L_ORDERKEY(_1_0, _2), not(del_LINEITEM_L_ORDERKEY(_1_0, _2)), _2=_1";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux4'2").get(0).toString());
        
        expected = "ins_??aux4(_0) :- ins_LINEITEM_L_ORDERKEY(_1, _2), not(??aux4(_0)), _2=_0";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux4").get(0).toString());
        
        expected = "??aux13(_0, _1):- SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5, _6), PARTSUPP_PS_SUPPKEY(_5, _7), _3=_7, _6=_0, _4<>_1";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13").get(0).toString());
        
        expected = "??aux13'1(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'1").get(0).toString());
        expected = "??aux13'2(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'2").get(0).toString());        
        expected = "??aux13'3(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'3").get(0).toString());
        expected = "??aux13'4(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'4").get(0).toString());     
        expected = "??aux13'5(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_NATIONKEY(_2, _4)),_3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'5").get(0).toString());
        expected = "??aux13'6(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'6").get(0).toString());        
        expected = "??aux13'7(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'7").get(0).toString());
        expected = "??aux13'8(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'8").get(0).toString());         
        expected = "??aux13'9(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'9").get(0).toString());
        expected = "??aux13'10(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'10").get(0).toString());        
        expected = "??aux13'11(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'11").get(0).toString());
        expected = "??aux13'12(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'12").get(0).toString());     
        expected = "??aux13'13(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'13").get(0).toString());
        expected = "??aux13'14(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'14").get(0).toString());        
        expected = "??aux13'15(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'15").get(0).toString());
        expected = "??aux13'16(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("??aux13'16").get(0).toString());        
        
        expected = "ins_??aux131(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(??aux13(_1, _5)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux131").get(0).toString());
        expected = "ins_??aux132(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), not(??aux13(_1, _5)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux132").get(0).toString());        
        expected = "ins_??aux133(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), not(??aux13(_1, _5)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux133").get(0).toString());
        expected = "ins_??aux134(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)),not(??aux13(_1, _5)),  _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux134").get(0).toString());     
        expected = "ins_??aux135(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_NATIONKEY(_2, _4)),not(??aux13(_1, _5)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux135").get(0).toString());
        expected = "ins_??aux136(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)),not(??aux13(_1, _5)),  _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux136").get(0).toString());        
        expected = "ins_??aux137(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), not(??aux13(_1, _5)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux137").get(0).toString());
        expected = "ins_??aux138(_1, _5):- ins_SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), not(??aux13(_1, _5)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux138").get(0).toString());         
        expected = "ins_??aux139(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(??aux13(_1, _5)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux139").get(0).toString());
        expected = "ins_??aux1310(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), not(??aux13(_1, _5)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux1310").get(0).toString());        
        expected = "ins_??aux1311(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), not(??aux13(_1, _5)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux1311").get(0).toString());
        expected = "ins_??aux1312(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), ins_SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), not(??aux13(_1, _5)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux1312").get(0).toString());     
        expected = "ins_??aux1313(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_SUPPLIER_S_NATIONKEY(_2, _4)),not(??aux13(_1, _5)),  _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux1313").get(0).toString());
        expected = "ins_??aux1314(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), ins_PARTSUPP_PS_PARTKEY(_5_0, _6), PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), not(del_PARTSUPP_PS_SUPPKEY(_5_0, _7)), not(??aux13(_1, _5)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux1314").get(0).toString());        
        expected = "ins_??aux1315(_1, _5):- SUPPLIER_S_SUPPKEY(_2, _3), SUPPLIER_S_NATIONKEY(_2, _4), PARTSUPP_PS_PARTKEY(_5_0, _6), ins_PARTSUPP_PS_SUPPKEY(_5_0, _7), not(del_SUPPLIER_S_SUPPKEY(_2, _3)), not(del_SUPPLIER_S_NATIONKEY(_2, _4)), not(del_PARTSUPP_PS_PARTKEY(_5_0, _6)), not(??aux13(_1, _5)), _3=_7, _6=_1, _4<>_5";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_??aux1315").get(0).toString());
 
        int numberOfRules = schema.getAllDerivationRules().size();
        assertEquals(36, numberOfRules);
    }
   


}
