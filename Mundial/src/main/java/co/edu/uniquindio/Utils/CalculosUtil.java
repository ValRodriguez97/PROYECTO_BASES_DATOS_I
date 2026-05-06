package co.edu.uniquindio.Utils;

import co.edu.uniquindio.model.Jugador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Métodos estáticos de apoyo para cálculos frecuentes.
 * Se usan desde la capa de servicios o directamente desde la UI.
 */
public class CalculosUtil {

    private CalculosUtil() { /* util class */ }

    /**
     * Suma el valor de mercado de una lista de jugadores.
     */
    public static BigDecimal valorTotalEsquadra(List<Jugador> jugadores) {
        return jugadores.stream()
                .map(Jugador::getValorMercado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el promedio de valor de mercado.
     */
    public static BigDecimal valorPromedio(List<Jugador> jugadores) {
        if (jugadores.isEmpty()) return BigDecimal.ZERO;
        BigDecimal total = valorTotalEsquadra(jugadores);
        return total.divide(
                BigDecimal.valueOf(jugadores.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Cuenta cuántos jugadores son menores de edad límite.
     */
    public static long contarMenoresDe(List<Jugador> jugadores, int edad) {
        return jugadores.stream().filter(j -> j.getEdad() < edad).count();
    }

    /**
     * Formatea un valor en millones de euros para mostrar en UI.
     */
    public static String formatearValor(BigDecimal valor) {
        if (valor == null) return "€0,00 M";
        return String.format("€%,.2f M", valor);
    }
}