package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.utils;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.LiteralFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicElementSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.OrdinaryLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaGrammarLexer;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaGrammarParser;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaWithIDsGrammarToSpecVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Map;

public class LiteralParser {
    public static Literal parseLiteral(String literalString) {
        LogicSchemaWithIDsGrammarToSpecVisitor visitor = new LogicSchemaWithIDsGrammarToSpecVisitor(new StringToTermSpecFactory());
        CharStream input = CharStreams.fromString(literalString);
        LogicSchemaGrammarLexer lexer = new LogicSchemaGrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LogicSchemaGrammarParser parser = new LogicSchemaGrammarParser(tokens);
        LogicSchemaGrammarParser.LiteralContext context = parser.literal();
        LogicElementSpec logicSchemaSpec = visitor.visitLiteral(context);

        if (logicSchemaSpec instanceof OrdinaryLiteralSpec ols) {
            Map<String, Predicate> map = Map.of(ols.getPredicateName(), new Predicate(ols.getPredicateName(), ols.getTermSpecList().size()));
            LiteralFactory literalFactory = new LiteralFactory(map);
            return literalFactory.buildOrdinaryLiteral(ols);
        } else if (logicSchemaSpec instanceof BuiltInLiteralSpec bls) {
            LiteralFactory literalFactory = new LiteralFactory(Map.of());
            return literalFactory.buildBuiltInLiteral(bls);
        } else {
            throw new RuntimeException("Unexpected logic element spec type: " + logicSchemaSpec.getClass().getSimpleName());
        }
    }
}
