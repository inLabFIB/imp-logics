package edu.upc.mpi.tpch;

import edu.upc.mpi.augmented_logicschema.LogicSchemaAugmenter;
import edu.upc.mpi.logicschema.LogicSchema;
import edu.upc.mpi.logicschema_normalizer.LogicSchemaNormalizer;
import edu.upc.mpi.parser.LogicSchemaParser;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TpcHTest {
   
    private void assertEqualsNoSpaces(String expected, String result) {
        expected = expected.replaceAll(" ","");
        result = result.replaceAll(" ", "");
        assertEquals(expected, result);
    }
    
    @Test
    public void test(){
        File tpcFile = new File("src\\test\\resources\\tpc-h.txt");
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
        String expected1 = "@1 :-  LINEITEM_COMMITDATE(LI, O, LD), ORDER_ORDERDATE(O, OD), LD < OD";
        assertEqualsNoSpaces(expected1, schema.getConstraintByNumber(1).toString());
        
        String expected2 = "@2 :- ORDERS(O), not(Aux(O))";
        assertEqualsNoSpaces(expected2, schema.getConstraintByNumber(2).toString());
        
        String expected3 = "@3 :- LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected3, schema.getConstraintByNumber(3).toString());
        
        String expected4 = "@4 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(Aux2(P, N))";
        assertEqualsNoSpaces(expected4, schema.getConstraintByNumber(4).toString());
        
        String expected5 = "Aux(O) :- LINEITEM(LI, O)";
        assertEqualsNoSpaces(expected5, schema.getDerivationRulesByHead("Aux").get(0).toString());
        
        String expected6 = "Aux2(P, N) :- PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), N<>N2";
        assertEqualsNoSpaces(expected6, schema.getDerivationRulesByHead("Aux2").get(0).toString());
        
        int numberOfRules = schema.getAllConstraints().size();
        assertEquals(4, numberOfRules);
        
        numberOfRules = schema.getAllDerivationRules().size();
        assertEquals(2, numberOfRules);
    }

    private void checkAugmentedConstraints(LogicSchema augmentedLogicSchema) {
        String expected;
        expected = "@1000 :- ins_LINEITEM_COMMITDATE(LI, O, LD), ins_ORDER_ORDERDATE(O, OD), LD < OD";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1000).toString());
        expected = "@1001 :- ins_LINEITEM_COMMITDATE(LI, O, LD), ORDER_ORDERDATE(O, OD), not(del_ORDER_ORDERDATE(O, OD)), LD < OD";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1001).toString());
        expected = "@1002 :- LINEITEM_COMMITDATE(LI, O, LD), not(del_LINEITEM_COMMITDATE(LI, O, LD)), ins_ORDER_ORDERDATE(O, OD), LD < OD";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(1002).toString());
        
        expected = "@2000 :- ins_ORDERS(O), del_Aux(O)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(2000).toString());
        expected = "@2001 :- ins_ORDERS(O), not(Aux(O)), not(ins_Aux(O))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(2001).toString());
        expected = "@2002 :- ORDERS(O), not(del_ORDERS(O)), del_Aux(O)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(2002).toString());
        
        expected = "@3000 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3000).toString());
        expected = "@3001 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3001).toString());
        expected = "@3002 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3002).toString());
        expected = "@3003 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3003).toString());
        expected = "@3004 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3004).toString());
        expected = "@3005 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3005).toString());
        expected = "@3006 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3006).toString());
        expected = "@3007 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3007).toString());
        expected = "@3008 :- LINEITEM_SUPPKEY(LI, O, S),  not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3008).toString());
        expected = "@3009 :- LINEITEM_SUPPKEY(LI, O, S),  not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3009).toString());
        expected = "@3010 :- LINEITEM_SUPPKEY(LI, O, S),  not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3010).toString());
        expected = "@3011 :- LINEITEM_SUPPKEY(LI, O, S),  not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3011).toString());
        expected = "@3012 :- LINEITEM_SUPPKEY(LI, O, S),  not(del_LINEITEM_SUPPKEY(LI, O, S)), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3012).toString());
        expected = "@3013 :- LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3013).toString());
        expected = "@3014 :- LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(3014).toString());
        
        expected = "@4000 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4000).toString());
        expected = "@4001 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4001).toString());
        expected = "@4002 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4002).toString());
        expected = "@4003 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4003).toString());
        expected = "@4004 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_SUPPLIER_NATIONKEY(S, N), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4004).toString());
        expected = "@4005 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_SUPPLIER_NATIONKEY(S, N), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4005).toString());
        expected = "@4006 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4006).toString());
        expected = "@4007 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4007).toString());
        expected = "@4008 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4008).toString());
        expected = "@4009 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4009).toString());
        expected = "@4010 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4010).toString());
        expected = "@4011 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4011).toString());
        expected = "@4012 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_SUPPLIER_NATIONKEY(S, N), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4012).toString());
        expected = "@4013 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_SUPPLIER_NATIONKEY(S, N), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4013).toString());
        expected = "@4014 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(4014).toString());
    
        int numberOfRules = augmentedLogicSchema.getAllConstraints().size();
        assertEquals(36, numberOfRules);
    }

    private void checkDerivationRules(LogicSchema augmentedLogicSchema) {
        String expected;
        
        expected = "Aux(O) :- LINEITEM(LI, O)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("Aux").get(0).toString());
        
        expected = "Aux'(O) :- ins_LINEITEM(LI, O)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("Aux'").get(0).toString());
        expected = "Aux'(O) :- LINEITEM(LI, O), not(del_LINEITEM(LI, O))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("Aux'").get(1).toString());
        
        expected = "ins_Aux(O) :- ins_LINEITEM(LI, O), not(Aux(O))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_Aux").get(0).toString());
        
        expected = "del_Aux(O) :- del_LINEITEM(LI, O), not(Aux'(O))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("del_Aux").get(0).toString());
        
        
        expected = "Aux2(P, N) :- PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), N<>N2";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("Aux2").get(0).toString());
        
        expected = "Aux2'(P, N) :- ins_PARTSUPP(P, S2), ins_SUPPLIER_NATIONKEY(S2, N2), N<>N2";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("Aux2'").get(0).toString());
        expected = "Aux2'(P, N) :- ins_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_SUPPLIER_NATIONKEY(S2, N2)), N<>N2";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("Aux2'").get(1).toString());
        expected = "Aux2'(P, N) :- PARTSUPP(P, S2), not(del_PARTSUPP(P, S2)), ins_SUPPLIER_NATIONKEY(S2, N2), N<>N2";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("Aux2'").get(2).toString());
        expected = "Aux2'(P, N) :- PARTSUPP(P, S2), not(del_PARTSUPP(P, S2)), SUPPLIER_NATIONKEY(S2, N2), not(del_SUPPLIER_NATIONKEY(S2, N2)), N<>N2";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("Aux2'").get(3).toString());
        
        expected = "ins_Aux2(P, N) :- ins_PARTSUPP(P, S2), ins_SUPPLIER_NATIONKEY(S2, N2), N<>N2, not(Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_Aux2").get(0).toString());
        expected = "ins_Aux2(P, N) :- ins_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_SUPPLIER_NATIONKEY(S2, N2)), N<>N2, not(Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_Aux2").get(1).toString());
        expected = "ins_Aux2(P, N) :- PARTSUPP(P, S2), not(del_PARTSUPP(P, S2)), ins_SUPPLIER_NATIONKEY(S2, N2), N<>N2, not(Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("ins_Aux2").get(2).toString());
 
        expected = "del_Aux2(P, N) :- del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), N<>N2, not(Aux2'(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("del_Aux2").get(0).toString());
        expected = "del_Aux2(P, N) :- PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), N<>N2, not(Aux2'(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getDerivationRulesByHead("del_Aux2").get(1).toString());
        
        int numberOfRules = augmentedLogicSchema.getAllDerivationRules().size();
        assertEquals(15, numberOfRules);
    }

    private void checkNormalizedConstraints(LogicSchema schema) {
        String expected;
        expected = "@100000 :- ins_LINEITEM_COMMITDATE(LI, O, LD), ins_ORDER_ORDERDATE(O, OD), LD < OD";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100000).toString());
        expected = "@100100 :- ins_LINEITEM_COMMITDATE(LI, O, LD), ORDER_ORDERDATE(O, OD), not(del_ORDER_ORDERDATE(O, OD)), LD < OD";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100100).toString());
        expected = "@100200 :- LINEITEM_COMMITDATE(LI, O, LD), ins_ORDER_ORDERDATE(O, OD),  not(del_LINEITEM_COMMITDATE(LI, O, LD)), LD < OD";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(100200).toString());
        
        expected = "@200000 :- ins_ORDERS(O), del_LINEITEM(LI, O), not(Aux'1(O)), not(Aux'2(O))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(200000).toString());
        expected = "@200100 :- ins_ORDERS(O), not(Aux(O)), not(ins_Aux(O))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(200100).toString());
        expected = "@200200 :- ORDERS(O), del_LINEITEM(LI, O), not(del_ORDERS(O)), not(Aux'1(O)), not(Aux'2(O))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(200200).toString());
        
        expected = "@300000 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(300000).toString());
        expected = "@300100 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(300100).toString());
        expected = "@300200 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_SUPPLIER_NAME(S, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(300200).toString());
        expected = "@300300 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_SUPPLIER_NAME(S, N)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(300300).toString());
        expected = "@300400 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_ORDER_CUSTOMER(O, C))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(300400).toString());
        expected = "@300500 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N),  not(del_ORDER_CUSTOMER(O, C)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(300500).toString());
        expected = "@300600 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_ORDER_CUSTOMER(O, C)), not(del_SUPPLIER_NAME(S, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(300600).toString());
        expected = "@300700 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_ORDER_CUSTOMER(O, C)), not(del_SUPPLIER_NAME(S, N)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(300700).toString());
        expected = "@300800 :- LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N),  not(del_LINEITEM_SUPPKEY(LI, O, S))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(300800).toString());
        expected = "@300900 :- LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(300900).toString());
        expected = "@301000 :- LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NAME(S, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(301000).toString());
        expected = "@301100 :- LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NAME(S, N)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(301100).toString());
        expected = "@301200 :- LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_ORDER_CUSTOMER(O, C))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(301200).toString());
        expected = "@301300 :- LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_ORDER_CUSTOMER(O, C)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(301300).toString());
        expected = "@301400 :- LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_ORDER_CUSTOMER(O, C)), not(del_SUPPLIER_NAME(S, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(301400).toString());
        
        expected = "@400000 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400000).toString());
        expected = "@400001 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(Aux2'1(P, N)),  not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)),N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400001).toString());
        expected = "@400100 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400100).toString());
        expected = "@400200 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2),  not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400200).toString());
        expected = "@400201 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400201).toString());
        expected = "@400300 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400300).toString());
        expected = "@400400 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400400).toString());
        expected = "@400401 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400401).toString());
        expected = "@400500 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400500).toString());
        expected = "@400600 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400600).toString());
        expected = "@400601 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400601).toString());
        expected = "@400700 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400700).toString());
        expected = "@400800 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400800).toString());
        expected = "@400801 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400801).toString());
        expected = "@400900 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_PARTKEY(LI, O, P)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(400900).toString());
        expected = "@401000 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(401000).toString());
        expected = "@401001 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(401001).toString());
        expected = "@401100 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(401100).toString());
        expected = "@401200 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(401200).toString());
        expected = "@401201 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(401201).toString());
        expected = "@401300 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2(P, N)), not(ins_Aux21(P, N)),not(ins_Aux22(P, N)),not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(401300).toString());
        expected = "@401400 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)),not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(401400).toString());
        expected = "@401401 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)),not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(401401).toString());
    
        int numberOfRules = schema.getAllConstraints().size();
        assertEquals(44, numberOfRules);
    }

    private void checkNormalizedDerivationRules(LogicSchema schema) {
        String expected;
        
        expected = "Aux(O) :- LINEITEM(LI, O)";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("Aux").get(0).toString());
        
        expected = "Aux'1(O) :- ins_LINEITEM(LI, O)";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("Aux'1").get(0).toString());
        expected = "Aux'2(O) :- LINEITEM(LI, O), not(del_LINEITEM(LI, O))";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("Aux'2").get(0).toString());
        
        expected = "ins_Aux(O) :- ins_LINEITEM(LI, O), not(Aux(O))";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_Aux").get(0).toString());
        
        expected = "Aux2(P, N) :- PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), N<>N2";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("Aux2").get(0).toString());
        
        expected = "Aux2'1(P, N) :- ins_PARTSUPP(P, S2), ins_SUPPLIER_NATIONKEY(S2, N2), N<>N2";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("Aux2'1").get(0).toString());
        expected = "Aux2'2(P, N) :- ins_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_SUPPLIER_NATIONKEY(S2, N2)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("Aux2'2").get(0).toString());
        expected = "Aux2'3(P, N) :- PARTSUPP(P, S2), ins_SUPPLIER_NATIONKEY(S2, N2), not(del_PARTSUPP(P, S2)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("Aux2'3").get(0).toString());
        expected = "Aux2'4(P, N) :- PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_PARTSUPP(P, S2)), not(del_SUPPLIER_NATIONKEY(S2, N2)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("Aux2'4").get(0).toString());
        
        expected = "ins_Aux21(P, N) :- ins_PARTSUPP(P, S2), ins_SUPPLIER_NATIONKEY(S2, N2), not(Aux2(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_Aux21").get(0).toString());
        expected = "ins_Aux22(P, N) :- ins_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_SUPPLIER_NATIONKEY(S2, N2)), not(Aux2(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_Aux22").get(0).toString());
        expected = "ins_Aux23(P, N) :- PARTSUPP(P, S2), ins_SUPPLIER_NATIONKEY(S2, N2), not(del_PARTSUPP(P, S2)), not(Aux2(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getDerivationRulesByHead("ins_Aux23").get(0).toString());
 
        int numberOfRules = schema.getAllDerivationRules().size();
        assertEquals(12, numberOfRules);
    }
   


}
