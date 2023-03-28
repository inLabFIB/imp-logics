package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.Literal;
import edu.upc.imp.logics.schema.Predicate;
import edu.upc.imp.logics.services.creation.LiteralFactory;
import edu.upc.imp.logics.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.imp.logics.services.creation.spec.LogicElementSpec;
import edu.upc.imp.logics.services.creation.spec.OrdinaryLiteralSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.imp.logics.services.parser.LogicSchemaWithIDsGrammarToSpecVisitor;
import edu.upc.imp.parser.LogicSchemaGrammarLexer;
import edu.upc.imp.parser.LogicSchemaGrammarParser;
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
