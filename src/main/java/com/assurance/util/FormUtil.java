package com.assurance.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Parsing tolérant des champs de formulaire : renvoie null si vide ou invalide. */
public final class FormUtil {

    private FormUtil() {}

    public static boolean isBlank(String s) { return s == null || s.isBlank(); }

    public static Long parseLong(String s) {
        if (isBlank(s)) return null;
        try { return Long.valueOf(s.trim()); } catch (NumberFormatException e) { return null; }
    }

    public static Integer parseInt(String s) {
        if (isBlank(s)) return null;
        try { return Integer.valueOf(s.trim()); } catch (NumberFormatException e) { return null; }
    }

    public static LocalDate parseDate(String s) {
        if (isBlank(s)) return null;
        try { return LocalDate.parse(s.trim()); } catch (DateTimeParseException e) { return null; }
    }

    public static BigDecimal parseBigDecimal(String s) {
        if (isBlank(s)) return null;
        try { return new BigDecimal(s.trim().replace(',', '.')); } catch (NumberFormatException e) { return null; }
    }

    public static <E extends Enum<E>> E parseEnum(Class<E> type, String s) {
        if (isBlank(s)) return null;
        try { return Enum.valueOf(type, s.trim()); } catch (IllegalArgumentException e) { return null; }
    }
}