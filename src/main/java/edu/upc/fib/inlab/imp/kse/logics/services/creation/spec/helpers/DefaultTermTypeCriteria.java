package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers;

/**
 * DefaultTermTypeCriteria that interprets numbers as constants, or quoted strings as constants. The rest of strings
 * are interpreted as variables.
 * Examples of Strings that would be interpreted as Constants: 4, 4.0, "Socrates", 'Plato'.
 * Examples of Strings that would be interpreted as Variables: x, y, Person, etc.
 */
public class DefaultTermTypeCriteria implements TermTypeCriteria {
    @Override
    public boolean isVariable(String name) {
        return !isConstant(name);
    }

    @Override
    public boolean isConstant(String name) {
        return isInteger(name) || isDouble(name) || isQuoted(name);
    }

    private static boolean isInteger(String name) {
        try {
            Integer.parseInt(name);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static boolean isDouble(String name) {
        try {
            Double.parseDouble(name);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean isQuoted(String name) {
        return name.startsWith("\"") && name.endsWith("\"") ||
                name.startsWith("'") && name.endsWith("'");
    }
}
