package com.smash;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DescripcionJugador {
    private Stage stageHud;
    private Label infoJ1;
    private Label infoJ2;
    private BitmapFont fuenteInfo;
    
    public DescripcionJugador (Viewport viewport, com.badlogic.gdx.graphics.g2d.SpriteBatch batch){
        stageHud = new Stage(viewport, batch);
        
        fuenteInfo = new BitmapFont();
        fuenteInfo.getData().setScale(1.4f);
        Label.LabelStyle estiloInfo = new Label.LabelStyle();
        estiloInfo.font = fuenteInfo;
        estiloInfo.fontColor = Color.WHITE;
        
        infoJ1 = new Label("", estiloInfo);
        infoJ1.setPosition(20f, 850F);
        stageHud.addActor(infoJ1);
        
        infoJ2 = new Label("", estiloInfo);
        infoJ2.setPosition(1525f, 850F);
        stageHud.addActor(infoJ2);
    }
    public void actualizar (Personaje j1, Personaje j2){
        if (j1 != null) {
            Rectangle hb1 = j1.getHitBox();
            infoJ1.setText(String.format("--- JUGADOR 1: FÍSICAS ---\n" +
                    "- Posición (X, Y): %.1fpx | %.1fpx\n" +
                    "- Vel. X (MRU): %.1f px/s\n" +
                    "- Vel. Y (MRUV): %.1f px/s\n" +
                    "- Gravedad Activa: 1200.0 px/s²\n" +
                    "- Estado Físico: %s\n" +
                    "- En Suelo (Colisión Piso): %b\n" +
                    "- Dimensiones Hitbox: [W: %.1f, H: %.1f]\n" +
                    "- Salud Actual: %.1f HP",
                    j1.getX(), j1.getY(), 
                    j1.velocidadX, j1.velocidadY, 
                    j1.getEstado(), j1.enSuelo,
                    hb1.width, hb1.height,
                    j1.getVida()));
        }
        if (j2 != null) {
            Rectangle hb2 = j2.getHitBox();
            infoJ2.setText(String.format("--- JUGADOR 2: FÍSICAS ---\n" +
                    "- Posición (X, Y): %.1fpx | %.1fpx\n" +
                    "- Vel. X (MRU): %.1f px/s\n" +
                    "- Vel. Y (MRUV): %.1f px/s\n" +
                    "- Gravedad Activa: 1200.0 px/s²\n" +
                    "- Estado Físico: %s\n" +
                    "- En Suelo (Colisión Piso): %b\n" +
                    "- Dimensiones Hitbox: [W: %.1f, H: %.1f]\n" +
                    "- Salud Actual: %.1f HP",
                    j2.getX(), j2.getY(), 
                    j2.velocidadX, j2.velocidadY, 
                    j2.getEstado(), j2.enSuelo,
                    hb2.width, hb2.height,
                    j2.getVida()));
        }
    }
    public void render(float delta) {
        stageHud.act(delta);
        stageHud.draw();
    }

    public void resize(int width, int height) {
        stageHud.getViewport().update(width, height, true);
    }

    public void dispose() {
        if (stageHud != null) stageHud.dispose();
        if (fuenteInfo != null) fuenteInfo.dispose();
    }
}
