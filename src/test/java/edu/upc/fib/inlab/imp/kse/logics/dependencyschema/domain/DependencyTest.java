package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.visitor.DependencySchemaVisitor;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LiteralMother;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class DependencyTest {

    private static class DummyDependency extends Dependency {

        protected DummyDependency(List<Literal> body) {
            super(body);
        }

        @Override
        public Set<Variable> getUniversalVariables() {
            return null;
        }

        public Set<Variable> getExistentialVariables() {
            return null;
        }

        @Override
        public <T> T accept(DependencySchemaVisitor<T> visitor) {
            return null;
        }
    }

    @Test
    void should_throwException_whenCreatingADependency_withNullBody() {
        assertThatThrownBy(() -> new DummyDependency(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_whenCreatingADependency_withEmptyBody() {
        List<Literal> body = List.of();
        assertThatThrownBy(() -> new DummyDependency(body)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_makeBodyImmutable_whenCreatingDependency_withMutableListInput() {
        Literal literal = LiteralMother.createOrdinaryLiteralWithVariableNames("P", List.of("x"));
        List<Literal> body = createMutableLiteralList(literal);
        Dependency dependency = new DummyDependency(body);

        assertThat(dependency.getBody()).isUnmodifiable();
    }

    private static List<Literal> createMutableLiteralList(Literal... literals) {
        return new LinkedList<>(List.of(literals));
    }

}