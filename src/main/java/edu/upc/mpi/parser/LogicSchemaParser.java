/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upc.mpi.parser;


import edu.upc.mpi.logicschema.LogicSchema;
import edu.upc.mpi.parser.LogicSchemaGrammarParser.ProgContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * Parser for loading a new logic schema from a logic schema string or from a logic schema file.
 * 
 * The parser should be used like a Transaction Controller:
 - Create LogicSchemaParser
 - Invoke parse()
 - Invoke getLogicSchema()
 * 
 * @author Xavier Oriol Hilari
 */
public class LogicSchemaParser {
    private final String stringSchema;
    private final LogicSchema logicSchema = new LogicSchema();

    public LogicSchemaParser(File file){
        this.stringSchema = this.getReadFile(file);
        assert stringSchema != null && !stringSchema.equals(""):"The given schema should not be null neither empty: "+stringSchema;
    }
   
    public LogicSchemaParser(String schema){
        assert schema != null && !schema.equals(""):"The given schema should not be null neither empty: "+schema;
        this.stringSchema = schema;
    }
    
    protected String getReadFile(File file){
        assert file != null:"File should not be null";
        assert file.exists():"File "+file.getAbsolutePath()+" does not exists";
        
        //Reading the file
        String result = "";
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String readLine = reader.readLine();
            while(readLine != null){
                result += readLine+"\n";
                readLine = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            assert false:ex.getMessage();
        } catch (IOException ex) {
            assert false:ex.getMessage();
        } 
        return result;
    }

    /**
     * Translates the given logic schema file or string into a LogicSchema instance
     * 
     */
    public void parse() {
        ANTLRInputStream input = new ANTLRInputStream(stringSchema);
        LogicSchemaGrammarLexer lexer = new LogicSchemaGrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LogicSchemaGrammarParser parser = new LogicSchemaGrammarParser(tokens);
//        ParseTree tree = parser.prog(); // parse; start at prog
//        System.out.println(tree.toStringTree(parser)); // print tree as text
        ProgContext tree =(ProgContext)parser.prog();
        LogicSchemaGrammarVisitor visitor = new LogicSchemaGrammarVisitorImpl(logicSchema);
        visitor.visit(tree);
    }
    
    public LogicSchema getLogicSchema(){
        return logicSchema;
    }

}
