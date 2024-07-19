package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Atom;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LiteralPosition;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.OrdinaryLiteral;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


class StickyMarkingAnalyzerTest {

    @Nested
    class ArgumentsTests {
        @Test
        void shouldThrowIllegalArgument_whenTGDListIsNull() {
            assertThatThrownBy(() -> StickyMarkingAnalyzer.getStickyMarking(null)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class InitialStickyMarking {
        @Test
        void shouldMark_LiteralPosWithVar_NotInOneHead() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(x,y) -> r(x,a)
                                                                                                     """);
            Literal p = dependencySchema.getAllTGDs().get(0).getBody().get(0);
            Set<LiteralPosition> markedLiteralPositions = StickyMarkingAnalyzer.getInitialStickyMarking(dependencySchema.getAllTGDs());

            Assertions.assertThat(markedLiteralPositions)
                    .hasSize(1)
                    .anyMatch(litPos -> litPos.literal() == p && litPos.position() == 1);
        }

        @Test
        void shouldMark_LiteralPosWithVar_NotInOneHead_ButOnTheOther() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(x,y) -> r(x,y), s(x,a)
                                                                                                     """);
            Literal p = dependencySchema.getAllTGDs().get(0).getBody().get(0);
            Set<LiteralPosition> markedLiteralPositions = StickyMarkingAnalyzer.getInitialStickyMarking(dependencySchema.getAllTGDs());

            Assertions.assertThat(markedLiteralPositions)
                    .hasSize(1)
                    .anyMatch(litPos -> litPos.literal() == p && litPos.position() == 1);
        }

        @Test
        void shouldMark_LiteralPosWithVar_NotInSomeHead_ofSeveralTGDs() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(x,y) -> r(x,a)
                                                                                                     q(x,y) -> s(y,a)
                                                                                                     """);
            Literal p = dependencySchema.getAllTGDs().get(0).getBody().get(0);
            Literal q = dependencySchema.getAllTGDs().get(1).getBody().get(0);
            Set<LiteralPosition> markedLiteralPositions = StickyMarkingAnalyzer.getInitialStickyMarking(dependencySchema.getAllTGDs());

            Assertions.assertThat(markedLiteralPositions)
                    .hasSize(2)
                    .anyMatch(litPos -> litPos.literal() == p && litPos.position() == 1)
                    .anyMatch(litPos -> litPos.literal() == q && litPos.position() == 0);
        }

        @Test
        void shouldNotMark_LiteralPosWithVar_AppearingInEveryHeadAtom() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(x,y) -> r(x,y)
                                                                                                     """);
            Set<LiteralPosition> markedLiteralPositions = StickyMarkingAnalyzer.getInitialStickyMarking(dependencySchema.getAllTGDs());

            Assertions.assertThat(markedLiteralPositions)
                    .isEmpty();
        }

        @Test
        void shouldNotMark_LiteralPosWithConstant() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(x,4) -> r(x,y)
                                                                                                     """);
            Set<LiteralPosition> markedLiteralPositions = StickyMarkingAnalyzer.getInitialStickyMarking(dependencySchema.getAllTGDs());

            Assertions.assertThat(markedLiteralPositions)
                    .isEmpty();
        }
    }

    @Nested
    class StickyMarkingPropagation {
        @Test
        void shouldKeepInitialMarking_asPropagatedMarking() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     a(x,y), b(y) -> r(x)
                                                                                                     """);
            Literal a = dependencySchema.getAllTGDs().get(0).getBody().get(0);
            Literal b = dependencySchema.getAllTGDs().get(0).getBody().get(1);

            Set<LiteralPosition> initialMarking = Set.of(new LiteralPosition(a, 1), new LiteralPosition(b, 0));
            Set<LiteralPosition> markedLiteralPositions = StickyMarkingAnalyzer.getStickyMarkingPropagation(initialMarking, dependencySchema.getAllTGDs());

            Assertions.assertThat(markedLiteralPositions)
                    .hasSize(initialMarking.size())
                    .containsAll(initialMarking);
        }

        @Test
        void shouldPropagateMarking_toOneTGD() {
            //marked variables are m
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     a(x,m), b(m), c(y) -> r(m,y)
                                                                                                     """);
            Literal a = dependencySchema.getAllTGDs().get(0).getBody().get(0);
            Literal b = dependencySchema.getAllTGDs().get(0).getBody().get(1);
            Atom r = dependencySchema.getAllTGDs().get(0).getHead().get(0);

            Set<LiteralPosition> initialMarking = Set.of(new LiteralPosition(new OrdinaryLiteral(r), 0));
            Set<LiteralPosition> markedLiteralPositions = StickyMarkingAnalyzer.getStickyMarkingPropagation(initialMarking, dependencySchema.getAllTGDs());

            Assertions.assertThat(markedLiteralPositions)
                    .hasSize(initialMarking.size() + 2)
                    .anyMatch(litPos -> litPos.literal() == a && litPos.position() == 1)
                    .anyMatch(litPos -> litPos.literal() == b && litPos.position() == 0);
        }

        @Test
        void shouldPropagateMarking_throughSeveralTGDs() {
            //marked variables are m
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     a(x,m), b(m), c(y, z) -> r1(m,y)
                                                                                                     c(m, x), d(m, u, w), e(y) -> r2(y,m)
                                                                                                     """);
            Literal a = dependencySchema.getAllTGDs().get(0).getBody().get(0);
            Literal b = dependencySchema.getAllTGDs().get(0).getBody().get(1);
            Literal c = dependencySchema.getAllTGDs().get(1).getBody().get(0);
            Literal d = dependencySchema.getAllTGDs().get(1).getBody().get(1);
            Atom r1 = dependencySchema.getAllTGDs().get(0).getHead().get(0);
            Atom r2 = dependencySchema.getAllTGDs().get(1).getHead().get(0);

            Set<LiteralPosition> initialMarking = Set.of(
                    new LiteralPosition(new OrdinaryLiteral(r1), 0),
                    new LiteralPosition(new OrdinaryLiteral(r2), 1));
            Set<LiteralPosition> markedLiteralPositions = StickyMarkingAnalyzer.getStickyMarkingPropagation(initialMarking, dependencySchema.getAllTGDs());

            Assertions.assertThat(markedLiteralPositions)
                    .hasSize(initialMarking.size() + 4)
                    .anyMatch(litPos -> litPos.literal() == a && litPos.position() == 1)
                    .anyMatch(litPos -> litPos.literal() == b && litPos.position() == 0)
                    .anyMatch(litPos -> litPos.literal() == c && litPos.position() == 0)
                    .anyMatch(litPos -> litPos.literal() == d && litPos.position() == 0);
        }

        @Test
        void shouldPropagateMarking_WithinTheSameRule() {
            //marked variables are m
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(m,y) -> p(y, z)
                                                                                                     """);
            Literal pBody = dependencySchema.getAllTGDs().get(0).getBody().get(0);

            Set<LiteralPosition> initialMarking = Set.of(new LiteralPosition(pBody, 0));
            Set<LiteralPosition> markedLiteralPositions = StickyMarkingAnalyzer.getStickyMarkingPropagation(initialMarking, dependencySchema.getAllTGDs());

            Assertions.assertThat(markedLiteralPositions)
                    .hasSize(initialMarking.size() + 1)
                    .anyMatch(litPos -> litPos.literal() == pBody && litPos.position() == 1);
        }

        @Test
        void shouldPropagateMarking_Transitively() {
            //marked variables are m
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(m,y) -> p(y, z)
                                                                                                     q(x,y) -> p(x, y)
                                                                                                     """);
            //we will first propagate the initial marking to p[1]
            //then it will be propagated to q[0] and q[1]
            Literal pBody = dependencySchema.getAllTGDs().get(0).getBody().get(0);
            Literal qBody = dependencySchema.getAllTGDs().get(1).getBody().get(0);

            Set<LiteralPosition> initialMarking = Set.of(new LiteralPosition(pBody, 0));
            Set<LiteralPosition> markedLiteralPositions = StickyMarkingAnalyzer.getStickyMarkingPropagation(initialMarking, dependencySchema.getAllTGDs());

            Assertions.assertThat(markedLiteralPositions)
                    .hasSize(initialMarking.size() + 3)
                    .anyMatch(litPos -> litPos.literal() == pBody && litPos.position() == 1)
                    .anyMatch(litPos -> litPos.literal() == qBody && litPos.position() == 0)
                    .anyMatch(litPos -> litPos.literal() == qBody && litPos.position() == 1);
        }
    }
}