package com.smash;

import com.badlogic.gdx.controllers.Controller;

public class AdministradorMando {

    // Banderas de control de flanco (evitan repeticiones continuas por frame)
    private static boolean arribaAnterior = false;
    private static boolean abajoAnterior = false;
    private static boolean izquierdaAnterior = false;
    private static boolean derechaAnterior = false;
    private static boolean confirmarAnterior = false;
    private static boolean cancelarAnterior = false;

    /** Resetea los estados anteriores (ideal al cambiar de pantalla). */
    public static void reiniciarEstados() {
        arribaAnterior = false;
        abajoAnterior = false;
        izquierdaAnterior = false;
        derechaAnterior = false;
        confirmarAnterior = false;
        cancelarAnterior = false;
    }

    /** Detecta pulsación en dirección Arriba (D-Pad o Joystick Y < -0.5). */
    public static boolean esArriba(Controller mando) {
        if (mando == null) return false;
        boolean actual = mando.getButton(mando.getMapping().buttonDpadUp) || (mando.getAxis(1) < -0.5f);
        boolean justPressed = actual && !arribaAnterior;
        arribaAnterior = actual;
        return justPressed;
    }

    /** Detecta pulsación en dirección Abajo (D-Pad o Joystick Y > 0.5). */
    public static boolean esAbajo(Controller mando) {
        if (mando == null) return false;
        boolean actual = mando.getButton(mando.getMapping().buttonDpadDown) || (mando.getAxis(1) > 0.5f);
        boolean justPressed = actual && !abajoAnterior;
        abajoAnterior = actual;
        return justPressed;
    }

    /** Detecta pulsación en dirección Izquierda (D-Pad o Joystick X < -0.5). */
    public static boolean esIzquierda(Controller mando) {
        if (mando == null) return false;
        boolean actual = mando.getButton(mando.getMapping().buttonDpadLeft) || (mando.getAxis(0) < -0.5f);
        boolean justPressed = actual && !izquierdaAnterior;
        izquierdaAnterior = actual;
        return justPressed;
    }

    /** Detecta pulsación en dirección Derecha (D-Pad o Joystick X > 0.5). */
    public static boolean esDerecha(Controller mando) {
        if (mando == null) return false;
        boolean actual = mando.getButton(mando.getMapping().buttonDpadRight) || (mando.getAxis(0) > 0.5f);
        boolean justPressed = actual && !derechaAnterior;
        derechaAnterior = actual;
        return justPressed;
    }

    /** Detecta el botón de Confirmación (A en Xbox / X en PlayStation). */
    public static boolean esConfirmar(Controller mando) {
        if (mando == null) return false;
        boolean actual = mando.getButton(mando.getMapping().buttonA);
        boolean justPressed = actual && !confirmarAnterior;
        confirmarAnterior = actual;
        return justPressed;
    }

    /** Detecta el botón de Cancelar/Regresar (B en Xbox / Círculo en PlayStation). */
    public static boolean esCancelar(Controller mando) {
        if (mando == null) return false;
        boolean actual = mando.getButton(mando.getMapping().buttonB);
        boolean justPressed = actual && !cancelarAnterior;
        cancelarAnterior = actual;
        return justPressed;
    }
}