package com.smash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Transicion {
    private float temporizador = 0f;
    private Sound audioIntro;
    private boolean terminado = false;
    private boolean audioReproducido = false;
    
    //DURACIÓN DE CADA ETAPA
    private final float duracionDesvanecido = 4.0f;
    private final float duracionTotal = 7.5f;
    
    private ShapeRenderer shapeRenderer;
    private BitmapFont fuenteConteo;
    private GlyphLayout layout;
    
    public Transicion(){
        audioIntro = Gdx.audio.newSound(Gdx.files.internal("SonidosNombres/ConteoPelea.wav"));
        shapeRenderer = new ShapeRenderer();
        fuenteConteo = new BitmapFont();
        fuenteConteo.getData().setScale(10.0f);
        layout = new GlyphLayout();
    }
    public void iniciar() {
        temporizador = 0f;
        terminado = false;
        audioReproducido = false;
    }

    public void actualizar(float delta) {
        if (terminado) return;

        temporizador += delta;

        // Dispara el audio justo al terminar el desvanecido
        if (temporizador >= duracionDesvanecido && !audioReproducido) {
            if (audioIntro != null) {
                audioIntro.play(1.0f);
            }
            audioReproducido = true;
        }

        // Finaliza toda la secuencia de intro
        if (temporizador >= duracionTotal) {
            terminado = true;
        }
    }

    /**
     * Dibuja todo el aspecto visual de la intro: el desvanecido negro y los textos.
     * @param batch El SpriteBatch activo de la pantalla de pelea.
     * @param anchoVirt Ancho de la pantalla virtual (ej. 1920)
     * @param altoVirt Alto de la pantalla virtual (ej. 1080)
     */
    public void renderizar(SpriteBatch batch, float anchoVirt, float altoVirt) {
        if (terminado) return;

        // 1. RENDERIZAR FADE NEGRO (Si aún está desvaneciéndose)
        if (temporizador < duracionDesvanecido) {
            float alpha = 1.0f - (temporizador / duracionDesvanecido);
            Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, alpha);
            shapeRenderer.rect(0, 0, anchoVirt, altoVirt);
            shapeRenderer.end();
            Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        }

        // 2. RENDERIZAR TEXTOS DE CONTEO ("1", "2", "3", "FIGHT!")
        String texto = obtenerTextoSegunFase();
        if (!texto.isEmpty()) {
            batch.begin();
            layout.setText(fuenteConteo, texto);
            float x = (anchoVirt - layout.width) / 2f;
            float y = (altoVirt + layout.height) / 2f;

            fuenteConteo.setColor(Color.WHITE);
            fuenteConteo.draw(batch, layout, x, y);
            batch.end();
        }
    }

    private String obtenerTextoSegunFase() {
        float tiempoAudio = temporizador - duracionDesvanecido;
        if (tiempoAudio < 0) return "";
        if (tiempoAudio < 1.0f) return "3";
        if (tiempoAudio < 2.0f) return "2";
        if (tiempoAudio < 3.0f) return "1";
        if (tiempoAudio < 4.2f) return "FIGHT!";
        return "";
    }

    public boolean haTerminado() {
        return terminado;
    }

    public void dispose() {
        if (audioIntro != null) audioIntro.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (fuenteConteo != null) fuenteConteo.dispose();
        }
}
