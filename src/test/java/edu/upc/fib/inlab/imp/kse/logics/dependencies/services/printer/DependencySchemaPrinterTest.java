package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.printer;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.mothers.DependencySchemaMother;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DependencySchemaPrinterTest {

    @Test
    void should_printDependencySchema_withOneTGD() {
        String schemaString = "p() -> q()";
        DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema(schemaString);

        String result = new DependencySchemaPrinter().print(dependencySchema);

        assertThat(result).isEqualToIgnoringWhitespace(schemaString);
    }

    //TODO: finish testing!


}