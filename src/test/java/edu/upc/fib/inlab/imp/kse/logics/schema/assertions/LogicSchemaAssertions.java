package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.Level;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.LevelHierarchy;

public class LogicSchemaAssertions {

    public static AtomAssert assertThat(Atom actual) {
        return AtomAssert.assertThat(actual);
    }

    public static ComparisonBuiltInLiteralAssert assertThat(ComparisonBuiltInLiteral actual) {
        return ComparisonBuiltInLiteralAssert.assertThat(actual);
    }

    public static DerivationRuleAssert assertThat(DerivationRule actual) {
        return DerivationRuleAssert.assertThat(actual);
    }

    public static ImmutableLiteralsListAssert assertThat(ImmutableLiteralsList actual) {
        return ImmutableLiteralsListAssert.assertThat(actual);
    }

    public static ImmutableTermListAssert assertThat(ImmutableTermList actual) {
        return ImmutableTermListAssert.assertThat(actual);
    }

    public static ImmutableAtomListAssert assertThat(ImmutableAtomList actual) {
        return ImmutableAtomListAssert.assertThat(actual);
    }

    public static LevelAssert assertThat(Level actual) {
        return LevelAssert.assertThat(actual);
    }

    public static LevelHierarchyAssert assertThat(LevelHierarchy actual) {
        return LevelHierarchyAssert.assertThat(actual);
    }

    public static LiteralAssert assertThat(Literal actual) {
        return LiteralAssert.assertThat(actual);
    }

    public static LogicConstraintAssert assertThat(LogicConstraint actual) {
        return LogicConstraintAssert.assertThat(actual);
    }

    public static LogicSchemaAssert assertThat(LogicSchema actual) {
        return LogicSchemaAssert.assertThat(actual);
    }

    public static OrdinaryLiteralAssert assertThat(OrdinaryLiteral actual) {
        return OrdinaryLiteralAssert.assertThat(actual);
    }

    public static BuiltInLiteralAssert assertThat(BuiltInLiteral actual) {
        return new BuiltInLiteralAssert(actual);
    }

    public static PredicateAssert assertThat(Predicate actual) {
        return PredicateAssert.assertThat(actual);
    }

    public static TermAssert assertThat(Term actual) {
        return TermAssert.assertThat(actual);
    }

}
