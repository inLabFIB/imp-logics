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


public class TpcHTest {

    private void assertEqualsNoSpaces(String expected, String result) {
        expected = expected.replaceAll(" ", "");
        result = result.replaceAll(" ", "");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void test() throws URISyntaxException {
        File tpcFile = new File(ClassLoader.getSystemResource("tpc-h.txt").toURI());
        assert tpcFile.exists() : "File " + tpcFile.getAbsolutePath() + " does not exists";

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

    private void checkParsedSchema(LogicSchema schema) {
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
        expected = "@165 :- ins_LINEITEM_COMMITDATE(LI, O, LD), ins_ORDER_ORDERDATE(O, OD), LD < OD";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(165).toString());
        expected = "@166 :- ins_LINEITEM_COMMITDATE(LI, O, LD), ORDER_ORDERDATE(O, OD), not(del_ORDER_ORDERDATE(O, OD)), LD < OD";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(166).toString());
        expected = "@167 :- LINEITEM_COMMITDATE(LI, O, LD), ins_ORDER_ORDERDATE(O, OD),  not(del_LINEITEM_COMMITDATE(LI, O, LD)), LD < OD";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(167).toString());

        expected = "@168 :- ins_ORDERS(O), del_LINEITEM(LI, O), not(Aux'1(O)), not(Aux'2(O))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(168).toString());
        expected = "@169 :- ins_ORDERS(O), not(Aux(O)), not(ins_Aux(O))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(169).toString());
        expected = "@170 :- ORDERS(O), del_LINEITEM(LI, O), not(del_ORDERS(O)), not(Aux'1(O)), not(Aux'2(O))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(170).toString());

        expected = "@171 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N)";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(171).toString());
        expected = "@172 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(172).toString());
        expected = "@173 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_SUPPLIER_NAME(S, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(173).toString());
        expected = "@174 :- ins_LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_SUPPLIER_NAME(S, N)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(174).toString());
        expected = "@175 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_ORDER_CUSTOMER(O, C))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(175).toString());
        expected = "@176 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N),  not(del_ORDER_CUSTOMER(O, C)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(176).toString());
        expected = "@177 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_ORDER_CUSTOMER(O, C)), not(del_SUPPLIER_NAME(S, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(177).toString());
        expected = "@178 :- ins_LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_ORDER_CUSTOMER(O, C)), not(del_SUPPLIER_NAME(S, N)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(178).toString());
        expected = "@179 :- LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N),  not(del_LINEITEM_SUPPKEY(LI, O, S))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(179).toString());
        expected = "@180 :- LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(180).toString());
        expected = "@181 :- LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NAME(S, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(181).toString());
        expected = "@182 :- LINEITEM_SUPPKEY(LI, O, S), ins_ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NAME(S, N)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(182).toString());
        expected = "@183 :- LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_ORDER_CUSTOMER(O, C))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(183).toString());
        expected = "@184 :- LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), ins_SUPPLIER_NAME(S, N), CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_ORDER_CUSTOMER(O, C)), not(del_CUSTOMER_NAME(C, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(184).toString());
        expected = "@185 :- LINEITEM_SUPPKEY(LI, O, S), ORDER_CUSTOMER(O, C), SUPPLIER_NAME(S, N), ins_CUSTOMER_NAME(C, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_ORDER_CUSTOMER(O, C)), not(del_SUPPLIER_NAME(S, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(185).toString());

        expected = "@186 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(186).toString());
        expected = "@187 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(Aux2'1(P, N)),  not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)),N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(187).toString());
        expected = "@188 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(188).toString());
        expected = "@189 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2),  not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(189).toString());
        expected = "@190 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(190).toString());
        expected = "@191 :- ins_LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(191).toString());
        expected = "@192 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(192).toString());
        expected = "@193 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(193).toString());
        expected = "@194 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(194).toString());
        expected = "@195 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(195).toString());
        expected = "@196 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(196).toString());
        expected = "@197 :- ins_LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(197).toString());
        expected = "@198 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(198).toString());
        expected = "@199 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(199).toString());
        expected = "@200 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_PARTKEY(LI, O, P)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(200).toString());
        expected = "@201 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(201).toString());
        expected = "@202 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(202).toString());
        expected = "@203 :- LINEITEM_PARTKEY(LI, O, P), ins_LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2(P, N)), not(ins_Aux21(P, N)), not(ins_Aux22(P, N)), not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(203).toString());
        expected = "@204 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(204).toString());
        expected = "@205 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(205).toString());
        expected = "@206 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), ins_SUPPLIER_NATIONKEY(S, N), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)), not(Aux2(P, N)), not(ins_Aux21(P, N)),not(ins_Aux22(P, N)),not(ins_Aux23(P, N))";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(206).toString());
        expected = "@207 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), del_PARTSUPP(P, S2), SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)),not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(207).toString());
        expected = "@208 :- LINEITEM_PARTKEY(LI, O, P), LINEITEM_SUPPKEY(LI, O, S), SUPPLIER_NATIONKEY(S, N), PARTSUPP(P, S2), del_SUPPLIER_NATIONKEY(S2, N2), not(del_LINEITEM_PARTKEY(LI, O, P)), not(del_LINEITEM_SUPPKEY(LI, O, S)),not(del_SUPPLIER_NATIONKEY(S, N)), not(Aux2'1(P, N)), not(Aux2'2(P, N)), not(Aux2'3(P, N)), not(Aux2'4(P, N)), N<>N2";
        assertEqualsNoSpaces(expected, schema.getConstraintByNumber(208).toString());

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
