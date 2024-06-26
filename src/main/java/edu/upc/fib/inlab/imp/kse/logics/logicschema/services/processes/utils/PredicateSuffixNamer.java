package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PredicateSuffixNamer {
    private static final String SUFFIX_SEPARATOR = "_";
    private static final String PRIMA_CHAR = "'";
    private static final Pattern SUFFIX_PRIME_PATTERN = Pattern.compile("(?<primaSuffix>'*)$");

    private PredicateSuffixNamer() {
        throw new IllegalStateException("Utility class");
    }

    public static String concatSuffix(String predicateName, String suffix) {
        String predicateNameWithoutSuffix = predicateName.replace(PRIMA_CHAR, "");
        Matcher matcher = SUFFIX_PRIME_PATTERN.matcher(predicateName);
        String primaSuffix = matcher.find() ? matcher.group("primaSuffix") : "";
        return predicateNameWithoutSuffix + SUFFIX_SEPARATOR + suffix + primaSuffix;
    }
}
