package com.smash.personajes;

import com.smash.Personaje;

public class Gladiador extends Personaje{
    public Gladiador (){
        super("Gladiator", "Iconos/Icono-Gladiador.png", "Renders2D/Render2d-Gladiador.png", 515, 740, "AudioGladiator.wav");
    }
    @Override
    public void cargarPosesCombate (){
        
    }
}
