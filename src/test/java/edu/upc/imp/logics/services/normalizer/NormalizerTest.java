package edu.upc.imp.logics.services.normalizer;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.schema.assertions.LogicSchemaAssert;
import edu.upc.imp.logics.schema.utils.LogicSchemaMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class NormalizerTest {

    @Test
    public void should_throwException_whenNormalizingNullSchema() {
        assertThatThrownBy(() -> new Normalizer().normalize(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Normalizer should invoke Unfold, Transform, Sort, and Clean in this order")
    public void should_invokeNormalizingServicesInOrder() {
        LogicSchema inputSchema = LogicSchemaMother.createEmptySchema();
        LogicSchema unfoldedSchema = new LogicSchema(Set.of(), Set.of());
        SchemaUnfolder mockedUnfolder = spy(new SchemaUnfolder());
        when(mockedUnfolder.unfold(inputSchema)).thenReturn(unfoldedSchema);

        LogicSchema transformedSchema = LogicSchemaMother.createEmptySchema();
        SingleDerivationRuleTransformer mockedTransformer = spy(new SingleDerivationRuleTransformer());
        when(mockedTransformer.transform(unfoldedSchema)).thenReturn(transformedSchema);

        LogicSchema sortedSchema = LogicSchemaMother.createEmptySchema();
        BodySorter mockedSorter = spy(new BodySorter());
        when(mockedSorter.sort(transformedSchema)).thenReturn(sortedSchema);

        LogicSchema cleanedSchema = LogicSchemaMother.createEmptySchema();
        PredicateCleaner mockedCleaner = spy(new PredicateCleaner());
        when(mockedCleaner.clean(sortedSchema)).thenReturn(cleanedSchema);

        Normalizer normalizer = new Normalizer(mockedUnfolder, mockedTransformer, mockedSorter, mockedCleaner);

        normalizer.normalize(inputSchema);

        InOrder servicesInvokedInOrder = inOrder(mockedUnfolder, mockedTransformer, mockedSorter, mockedCleaner);
        servicesInvokedInOrder.verify(mockedUnfolder).unfold(inputSchema);
        servicesInvokedInOrder.verify(mockedTransformer).transform(unfoldedSchema);
        servicesInvokedInOrder.verify(mockedSorter).sort(transformedSchema);
        servicesInvokedInOrder.verify(mockedCleaner).clean(sortedSchema);
    }

    @Test
    public void should_normalizeSchema_whenSchemaIsNotNormalized() {
        LogicSchema inputSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                @1 :- not(U(x)), P(x), R(x)
                R(x) :- S(x)
                R(x) :- T(x)
                U(x) :- V(x)
                U(x) :- W(x)
                """);

        LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                @1 :- P(x), S(x), not(U_1(x)), not(U_2(x))
                @2 :- P(x), T(x), not(U_1(x)), not(U_2(x))
                U_1(x) :- V(x)
                U_2(x) :- W(x)
                """);

        LogicSchema normalizedSchema = new Normalizer().normalize(inputSchema);
        LogicSchemaAssert.assertThat(normalizedSchema).assertAllLogicConstraintsAreEquivalent(expectedSchema);
    }
}
