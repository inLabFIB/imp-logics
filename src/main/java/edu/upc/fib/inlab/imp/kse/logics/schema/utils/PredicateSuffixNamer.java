package edu.upc.fib.inlab.imp.kse.logics.schema.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PredicateSuffixNamer {
    private static final String SUFFIX_SEPARATOR = "_";
    private static final String PRIMA_CHAR = "'";
    private static final Pattern SUFFIX_PRIME_PATTERN = Pattern.compile("(?<primaSuffix>'*)$");

    public static String concatSuffix(String predicateName, String suffix) {
        String predicateNameWithoutSuffix = predicateName.replaceAll(PRIMA_CHAR, "");
        Matcher matcher = SUFFIX_PRIME_PATTERN.matcher(predicateName);
        String primaSuffix = matcher.find() ? matcher.group("primaSuffix") : "";
        return predicateNameWithoutSuffix + SUFFIX_SEPARATOR + suffix + primaSuffix;
    }
}
