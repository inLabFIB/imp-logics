package edu.upc.mpi.tpch;

import edu.upc.mpi.augmented_logicschema.LogicSchemaAugmenter;
import edu.upc.mpi.logicschema.LogicConstraint;
import edu.upc.mpi.logicschema.LogicSchema;
import edu.upc.mpi.logicschema_normalizer.LogicSchemaNormalizer;
import edu.upc.mpi.parser.LogicSchemaParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class TpcHTest {
   
    private void assertEqualsNoSpaces(String expected, String result) {
        expected = expected.replaceAll(" ","");
        result = result.replaceAll(" ", "");
        assertThat(result).isEqualTo(expected);
    }
    
    @Test
    public void test() throws URISyntaxException {
        File tpcFile = new File(ClassLoader.getSystemResource("tpc-h.txt").toURI());
        assert tpcFile.exists():"File "+tpcFile.getAbsolutePath()+" does not exists";

        LogicConstraint.reset();

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
        assertThat(numberOfRules).isEqualTo(4);
        
        numberOfRules = schema.getAllDerivationRules().size();
        assertThat(numberOfRules).isEqualTo(2);
    }

    private void checkAugmentedConstraints(LogicSchema augmentedLogicSchema) {
        String expected;
        expected = "@5 :- ins_LINEITEM_COMMITDATE(LI, O, LD), ins_ORDER_ORDERDATE(O, OD), LD < OD";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(5).toString());
        expected = "@6 :- ins_LINEITEM_COMMITDATE(LI, O, LD), ORDER_ORDERDATE(O, OD), not(del_ORDER_ORDERDATE(O, OD)), LD < OD";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(6).toString());
        expected = "@7 :- LINEITEM_COMMITDATE(LI, O, LD), not(del_LINEITEM_COMMITDATE(LI, O, LD)), ins_ORDER_ORDERDATE(O, OD), LD < OD";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(7).toString());
        
        expected = "@8 :- ins_ORDERS(O), del_Aux(O)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(8).toString());
        expected = "@9 :- ins_ORDERS(O), not(Aux(O)), not(ins_Aux(O))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(9).toString());
        expected = "@10 :- ORDERS(O), not(del_ORDERS(O)), del_Aux(O)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(10).toString());
        
        expected = "@11 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(11).toString());
        expected = "@12 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(12).toString());
        expected = "@13 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(13).toString());
        expected = "@14 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(14).toString());
        expected = "@15 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(15).toString());
        expected = "@16 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(16).toString());
        expected = "@17 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(17).toString());
        expected = "@18 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(18).toString());
        expected = "@19 :- LINEITEM_SUPPKEY(LI, O, S),  not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(19).toString());
        expected = "@20 :- LINEITEM_SUPPKEY(LI, O, S),  not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(20).toString());
        expected = "@21 :- LINEITEM_SUPPKEY(LI, O, S),  not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(21).toString());
        expected = "@22 :- LINEITEM_SUPPKEY(LI, O, S),  not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(22).toString());
        expected = "@23 :- LINEITEM_SUPPKEY(LI, O, S),  not(del_LINEITEM_SUPPKEY(LI, O, S)), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(23).toString());
        expected = "@24 :- LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(24).toString());
        expected = "@25 :- LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), ORDER_CUSTOMER(O, C), not(del_ORDER_CUSTOMER(O, C)), SUPPLIER_NAME(S, N), not(del_SUPPLIER_NAME(S, N)), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(25).toString());
        
        expected = "@26 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(26).toString());
        expected = "@27 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(27).toString());
        expected = "@28 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(28).toString());
        expected = "@29 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(29).toString());
        expected = "@30 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_SUPPLIER_NATIONKEY(S, N), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(30).toString());
        expected = "@31 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_SUPPLIER_NATIONKEY(S, N), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(31).toString());
        expected = "@32 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(32).toString());
        expected = "@33 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(33).toString());
        expected = "@34 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(34).toString());
        expected = "@35 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(35).toString());
        expected = "@36 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(36).toString());
        expected = "@37 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(37).toString());
        expected = "@38 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_SUPPLIER_NATIONKEY(S, N), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(38).toString());
        expected = "@39 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), ins_SUPPLIER_NATIONKEY(S, N), not(Aux2(P, N)), not(ins_Aux2(P, N))";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(39).toString());
        expected = "@40 :- LINEITEM_PARTKEY(LI, O, P), not(del_LINEITEM_PARTKEY(LI, O, P)), LINEITEM_SUPPKEY(LI, O, S), not(del_LINEITEM_SUPPKEY(LI, O, S)), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), del_Aux2(P, N)";
        assertEqualsNoSpaces(expected, augmentedLogicSchema.getConstraintByNumber(40).toString());
    
        int numberOfRules = augmentedLogicSchema.getAllConstraints().size();
        assertThat(numberOfRules).isEqualTo(36);
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
        assertThat(numberOfRules).isEqualTo(15);
    }

    private void checkNormalizedConstraints(LogicSchema schema) {
        String expected;
        expected = "@129 :- ins_LINEITEM_COMMITDATE(LI, O, LD), ins_ORDER_ORDERDATE(O, OD), LD < OD";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(129).toString());
        expected = "@130 :- ins_LINEITEM_COMMITDATE(LI, O, LD), ORDER_ORDERDATE(O, OD), not(del_ORDER_ORDERDATE(O, OD)), LD < OD";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(130).toString());
        expected = "@131 :- LINEITEM_COMMITDATE(LI, O, LD), ins_ORDER_ORDERDATE(O, OD),  not(del_LINEITEM_COMMITDATE(LI, O, LD)), LD < OD";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(131).toString());
        
        expected = "@132 :- ins_ORDERS(O), del_LINEITEM(LI, O), not(Aux'1(O)), not(Aux'2(O))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(132).toString());
        expected = "@133 :- ins_ORDERS(O), not(Aux(O)), not(ins_Aux(O))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(133).toString());
        expected = "@134 :- ORDERS(O), del_LINEITEM(LI, O), not(del_ORDERS(O)), not(Aux'1(O)), not(Aux'2(O))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(134).toString());
        
        expected = "@135 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(135).toString());
        expected = "@136 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(136).toString());
        expected = "@137 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_SUPPLIER_NAME(S, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(137).toString());
        expected = "@138 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_SUPPLIER_NAME(S, N)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(138).toString());
        expected = "@139 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_ORDER_CUSTOMER(O, C))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(139).toString());
        expected = "@140 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N),  not(del_ORDER_CUSTOMER(O, C)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(140).toString());
        expected = "@141 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_ORDER_CUSTOMER(O, C)), not(del_SUPPLIER_NAME(S, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(141).toString());
        expected = "@142 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_ORDER_CUSTOMER(O, C)), not(del_SUPPLIER_NAME(S, N)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(142).toString());
        expected = "@143 :- LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N),  not(del_LINEITEM_SUPPKEY(LI, O, S))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(143).toString());
        expected = "@144 :- LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(144).toString());
        expected = "@145 :- LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NAME(S, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(145).toString());
        expected = "@146 :- LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NAME(S, N)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(146).toString());
        expected = "@147 :- LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_ORDER_CUSTOMER(O, C))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(147).toString());
        expected = "@148 :- LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_ORDER_CUSTOMER(O, C)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(148).toString());
        expected = "@149 :- LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_ORDER_CUSTOMER(O, C)), not(del_SUPPLIER_NAME(S, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(149).toString());
        
        expected = "@150 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(150).toString());
        expected = "@151 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(Aux2'1(P, N)),  not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)),N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(151).toString());
        expected = "@152 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(152).toString());
        expected = "@153 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2),  not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(153).toString());
        expected = "@154 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(154).toString());
        expected = "@155 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(155).toString());
        expected = "@156 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(156).toString());
        expected = "@157 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(157).toString());
        expected = "@158 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(158).toString());
        expected = "@159 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(159).toString());
        expected = "@160 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(160).toString());
        expected = "@161 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(161).toString());
        expected = "@162 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(162).toString());
        expected = "@163 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(163).toString());
        expected = "@164 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_PARTKEY(LI, O, P)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(164).toString());
        expected = "@165 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(165).toString());
        expected = "@166 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(166).toString());
        expected = "@167 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(167).toString());
        expected = "@168 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(168).toString());
        expected = "@169 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(169).toString());
        expected = "@170 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2(P, N)), not(ins_Aux21(P, N)),not(ins_Aux22(P, N)),not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(170).toString());
        expected = "@171 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)),not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(171).toString());
        expected = "@172 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)),not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(172).toString());
    
        int numberOfRules = schema.getAllConstraints().size();
        assertThat(numberOfRules).isEqualTo(44);
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
        assertThat(numberOfRules).isEqualTo(12);
    }
   


}
