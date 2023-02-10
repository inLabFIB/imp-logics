package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.exceptions.*;
import edu.upc.imp.logics.schema.utils.ConstraintIDMother;
import edu.upc.imp.logics.schema.utils.DerivedPredicateMother;
import edu.upc.imp.logics.schema.utils.LogicConstraintMother;
import edu.upc.imp.logics.schema.utils.QueryMother;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class LogicSchemaTest {

    @Test
    public void should_throwException_WhenCreatingLogicSchema_WithRepeatedPredicateName(){
        Predicate p1 = new BasePredicate("p", new Arity(1));
        Predicate p2 = new BasePredicate("p", new Arity(1));
        assertThatThrownBy(()->new LogicSchema(Set.of(p1, p2), Set.of()))
                .isInstanceOf(RepeatedPredicateName.class);
    }

    @Test
    public void should_throwException_WhenCreatingLogicSchema_WithRepeatedConstraintID(){
        Predicate p = new BasePredicate("p", new Arity(1));
        LogicConstraint c1 = LogicConstraintMother.createTrivialLogicConstraint(ConstraintIDMother.createConstraintID(1), p);
        LogicConstraint c2 = LogicConstraintMother.createTrivialLogicConstraint(ConstraintIDMother.createConstraintID(1), p);
        assertThatThrownBy(()->new LogicSchema(Set.of(p), Set.of(c1, c2)))
                .isInstanceOf(RepeatedContraintID.class);
    }

    @Test
    public void should_throwException_WhenCreatingLogicSchema_WithConstraint_UsingPredicateNotFromSchema(){
        Predicate predicateNotInSchema = new BasePredicate("p", new Arity(1));
        LogicConstraint c1 = LogicConstraintMother.createTrivialLogicConstraint(ConstraintIDMother.createConstraintID(1), predicateNotInSchema);
        assertThatThrownBy(()->new LogicSchema(Set.of(), Set.of(c1)))
                .isInstanceOf(PredicateOutsideSchema.class);
    }

    @Test
    public void should_throwException_WhenCreatingLogicSchema_WithDerivedPredicate_UsingPredicateNotFromSchema(){
        Query query = QueryMother.createTrivialQuery(1, "predicateNotInSchemaName");
        Predicate derivedPredicate = new DerivedPredicate("p", new Arity(1), List.of(query));

        assertThatThrownBy(()->new LogicSchema(Set.of(derivedPredicate), Set.of()))
                .isInstanceOf(PredicateOutsideSchema.class);
    }

    @Test
    public void should_notThrowException_WhenCreatingTheLogicSchema_BringingFirstDerivedPredicates_AndThenBasePredicates(){
        BasePredicate basePredicate = new BasePredicate("q", new Arity(1));
        String derivedPredicateName = "p";
        DerivedPredicate derivedPredicate = DerivedPredicateMother.createTrivialDerivedPredicate(derivedPredicateName, 1, List.of(basePredicate));
        assertThatNoException().isThrownBy(()->new LogicSchema(Set.of(derivedPredicate, basePredicate), Set.of()));

    }

    @Test
    public void should_retrievePredicate_WhenGivingTheirName(){
        String predicateName = "p";
        Predicate p = new BasePredicate(predicateName, new Arity(1));
        LogicSchema logicSchema = new LogicSchema(Set.of(p), Set.of());
        assertThat(logicSchema.getPredicate(predicateName)).isSameAs(p);
    }

    @Test
    public void should_throwException_WhenRetrievingNonExistentPredicate(){
        LogicSchema logicSchema = new LogicSchema(Set.of(), Set.of());
        assertThatThrownBy(() -> logicSchema.getPredicate("P"));
    }

    @Test
    public void should_retrieveLogicConstraint_WhenGivingItsID(){
        Predicate p = new BasePredicate("p", new Arity(1));
        LogicConstraint logicConstraint = LogicConstraintMother.createTrivialLogicConstraint(ConstraintIDMother.createConstraintID(1), p);

        LogicSchema logicSchema = new LogicSchema(Set.of(p), Set.of(logicConstraint));
        ConstraintID constraintID = logicConstraint.getID();
        assertThat(logicSchema.getLogicConstraintByID(constraintID)).isSameAs(logicConstraint);
    }

    @Test
    public void should_throwException_WhenRetrievingLogicConstraint_WithNonExistentID(){
        LogicSchema logicSchema = new LogicSchema(Set.of(), Set.of());
        ConstraintID constraintID = ConstraintIDMother.createConstraintID(1);
        assertThatThrownBy(() -> logicSchema.getLogicConstraintByID(constraintID))
                .isInstanceOf(LogicConstraintNotExists.class);
    }

    @Test
    public void should_retrieveDerivationRules_WhenGivingTheirPredicateName(){
        BasePredicate basePredicate1 = new BasePredicate("q", new Arity(1));
        BasePredicate basePredicate2 = new BasePredicate("r", new Arity(1));
        String derivedPredicateName = "p";
        DerivedPredicate derivedPredicate = DerivedPredicateMother.createTrivialDerivedPredicate(derivedPredicateName, 1, List.of(basePredicate1, basePredicate2));
        List<DerivationRule> derivationRules = derivedPredicate.getDerivationRules();
        LogicSchema logicSchema = new LogicSchema(Set.of(basePredicate1, basePredicate2, derivedPredicate), Set.of());

        assertThat(logicSchema.getDerivationRules(derivedPredicateName)).containsExactlyInAnyOrderElementsOf(derivationRules);
    }

    @Test
    public void should_throwException_WhenRetrievingDerivationRules_WithNonExistentPredicateName(){
        LogicSchema logicSchema = new LogicSchema(Set.of(), Set.of());
        assertThatThrownBy(() -> logicSchema.getDerivationRules("nonExistantPredicateName"))
                .isInstanceOf(PredicateNotExists.class);
    }

    @Test
    public void should_throwException_WhenRetrievingDerivationRules_WithNonDerivedPredicateName(){
        String basePredicateName = "p";
        BasePredicate basePredicate = new BasePredicate(basePredicateName, new Arity(2));
        LogicSchema logicSchema = new LogicSchema(Set.of(basePredicate), Set.of());
        assertThatThrownBy(() -> logicSchema.getDerivationRules(basePredicateName))
                .isInstanceOf(PredicateIsNotDerived.class);
    }
}
