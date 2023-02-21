package edu.upc.imp.logics.specification;


public class DefaultStringToTermSpecFactory extends StringToTermSpecFactory {

    @Override
    public boolean isVariable(String name) {
        return !isConstant(name);
    }

    @Override
    public boolean isConstant(String name) {
        return isInteger(name) || isDouble(name) || isQuoted(name);
    }

    private static boolean isInteger(String name) {
        try{
            Integer.parseInt(name);
            return true;
        } catch (NumberFormatException ex){
            return false;
        }
    }

    private static boolean isDouble(String name) {
        try{
            Double.parseDouble(name);
            return true;
        } catch (NumberFormatException ex){
            return false;
        }
    }

    private boolean isQuoted(String name) {
        return name.startsWith("\"") && name.endsWith("\"") ||
                name.startsWith("'") && name.endsWith("'");
    }
}
