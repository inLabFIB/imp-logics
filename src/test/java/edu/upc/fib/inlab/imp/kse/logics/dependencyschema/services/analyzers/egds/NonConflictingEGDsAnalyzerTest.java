package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


class NonConflictingEGDsAnalyzerTest {

    private static Stream<Arguments> provideConflictingSchema() {
        return Stream.of(
                Arguments.of("EGD is conflicting KeyDependency",
                             """
                                         Child(name, age) -> Person(name, age)
                                         Person(name, age), Person(name, age2) -> age=age2
                                     """),
                Arguments.of("EGD is not Functional Dependency",
                             """
                                     WorksIn(name, dept) -> Person(name, age)
                                     Person(name, age), Child(name, age2) -> age=age2
                                             """
                ),
                Arguments.of("EGD is Functional Dependency but not Key Dependency",
                             """
                                      WorksIn(name, dept) -> Person(name, city, state)
                                     Person(name, city, state), Person(name2, city, state2) -> state=state2
                                                     """
                ),
                Arguments.of("EGD is conflicting KeyDependency with non linear TGD",
                             """
                                     % If a teacher is expert in a subject from a study plan, the teacher gives the subject
                                     ExpertIn(teacher, subject), ComposesPlan(subject, studyPlan) -> Teaches(teacher, subject)
                                                         
                                     % Just one teacher per subject
                                     Teaches(teacher1, subject), Teaches(teacher2, subject) -> teacher1=teacher2
                                                 """)
        );
    }

    @Test
    void shouldIdentifyAsSeparable_whenEGDsAreKeyDependencies_notConflictingWithTGDs() {
        DependencySchema schema = DependencySchemaMother.buildDependencySchema("""
                                                                                       WorksIn(name, dept) -> Person(name, age)
                                                                                       Person(name, age), Person(name, age2) -> age=age2
                                                                                       """);

        boolean separable = new NonConflictingEGDsAnalyzer().areEGDsNonConflictingWithTGDs(schema);

        Assertions.assertThat(separable).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideConflictingSchema")
    void shouldIdentifyAsNonSeparable_whenEGDsAreConflictingWithTGDs(@SuppressWarnings("unused") String title, String schemaString) {
        DependencySchema schema = DependencySchemaMother.buildDependencySchema(schemaString);

        boolean separable = new NonConflictingEGDsAnalyzer().areEGDsNonConflictingWithTGDs(schema);

        Assertions.assertThat(separable).isFalse();
    }

    @Test
    void shouldIdentifyAsSeparable() {
        DependencySchema schema = DependencySchemaMother.buildDependencySchema("""
                                                                                           % If a student passes a subject, the student has some evaluation
                                                                                           HasPassed(student, subject) -> Exam(teacher, student, subject, data)

                                                                                           % If a teacher teaches a subject a student is coursing, the teacher evaluates the student
                                                                                           Teaches(teacher, subject), Studies(student, subject) -> Exam(teacher, student, subject, data)

                                                                                           % A subject has, at most, one exam per day
                                                                                           Exam(teacher, student, subject, data), Exam(teacher2, student2, subject, data) -> teacher = teacher2
                                                                                           Exam(teacher, student, subject, data), Exam(teacher2, student2, subject, data) -> student = student2
                                                                                          
                                                                                       """);

        boolean separable = new NonConflictingEGDsAnalyzer().areEGDsNonConflictingWithTGDs(schema);

        Assertions.assertThat(separable).isTrue();
    }
}