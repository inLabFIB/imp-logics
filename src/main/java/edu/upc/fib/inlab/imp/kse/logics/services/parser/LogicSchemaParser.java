package edu.upc.fib.inlab.imp.kse.logics.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.LogicSchemaFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicSchemaSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.DefaultTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.TermTypeCriteria;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.HashSet;
import java.util.Set;

public abstract class LogicSchemaParser<T extends LogicConstraintSpec> {

    private final LogicSchemaGrammarToSpecVisitor<T> visitor;
    private final Set<String> specialSymbols;

    public LogicSchemaParser() {
        this(new DefaultTermTypeCriteria(), new BooleanBuiltInPredicateNameChecker());
    }

    public LogicSchemaParser(TermTypeCriteria termTypeCriteria, BooleanBuiltInPredicateNameChecker builtInPredicateNameChecker) {
        visitor = createVisitor(new StringToTermSpecFactory(termTypeCriteria), builtInPredicateNameChecker);
        specialSymbols = new HashSet<>();
        specialSymbols.add("myCustomBuiltInPredicate");
    }

    protected abstract LogicSchemaGrammarToSpecVisitor<T> createVisitor(StringToTermSpecFactory stringToTermSpecFactory, BooleanBuiltInPredicateNameChecker builtInPredicateNameChecker);

    protected abstract LogicSchemaFactory<T> createLogicSchemaFactory();

    public LogicSchema parse(String schemaString) {
        CharStream input = CharStreams.fromString(schemaString);
        LogicSchemaGrammarLexer lexer = new LogicSchemaGrammarLexer(input, specialSymbols);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LogicSchemaGrammarParser parser = new LogicSchemaGrammarParser(tokens);
        LogicSchemaGrammarParser.ProgContext tree = parser.prog();
        LogicSchemaSpec<T> logicSchemaSpec = visitor.visitProg(tree);
        LogicSchemaFactory<T> factory = createLogicSchemaFactory();
        return factory.createLogicSchema(logicSchemaSpec);
    }
}
