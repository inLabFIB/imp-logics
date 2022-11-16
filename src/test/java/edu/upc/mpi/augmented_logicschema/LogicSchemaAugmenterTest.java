package edu.upc.mpi.augmented_logicschema;

import edu.upc.mpi.logicschema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
            LogicConstraint originalConstraint = instance.getOriginalConstraint(newConstraint);
            assertThat(originalConstraint).isEqualTo(logicConstraint);
        });

    }
    
}
