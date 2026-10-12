package com.smash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;

public class PantallaPelea implements Screen{
    private final Main game;
    private final Personaje jugador1;
    private final Personaje jugador2;

    private SpriteBatch batch;
    private Stage stage;
    private Texture mapaCombate;
    
    private final float suelo_Y = 100f;

    public PantallaPelea(Main game, Personaje jugador1, Personaje jugador2, String rutaMapa) {
        this.game = game;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.batch = game.getBatch();
        this.mapaCombate = new Texture(rutaMapa);
    }

    @Override
    public void show() {
        stage = new Stage(new FillViewport(1920, 1080), batch);
        Gdx.input.setInputProcessor(stage);
        
        System.out.println("MANDOS DETECTADOS");
        Array<Controller> mandos = Controllers.getControllers();
        
        if (jugador1 != null) {
            jugador1.cargarPosesCombate();
            jugador1.setPosicion(300f, suelo_Y);
            jugador1.setJugador2(false);
            if (mandos.size > 0) {
                jugador1.setMando(mandos.get(0));
            } else {
                jugador1.setMando(null);
            }
        }
        //PROXIMAMENTE AGREGAR A JUGADOR 2
        if (jugador2 != null) {
        jugador2.cargarPosesCombate();
        jugador2.setPosicion(1500f, suelo_Y);
        jugador2.setMirandoIzquierda(true);
        jugador2.setJugador2(true);
        if (mandos.size > 1) {
            jugador2.setMando(mandos.get(1)); // Mando 2
        } else if (mandos.size == 1) {
                jugador2.setMando(null);
            }
    }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);

        if (jugador1 != null) {
            jugador1.procesarEntrada();
            jugador1.actualizarFisicas(delta, suelo_Y);
        }
        //AGREGAR A JUGADOR 2 EN UN FUTURO
        if (jugador2 != null) {
            jugador2.procesarEntrada();
            jugador2.actualizarFisicas(delta, suelo_Y);
        }
        
        batch.begin();
        // Dibujar el mapa de combate seleccionado
        batch.draw(mapaCombate, 0, 0, stage.getWidth(), stage.getHeight());

        if (jugador1 != null) {
            jugador1.renderizarPelea(batch, delta);
        }
        if (jugador2 != null) {
            jugador2.renderizarPelea(batch, delta);
        }
        
        // Lógica de renderizado de personajes, física e interfaz de salud irán aquí
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (stage != null)stage.dispose();
        if (mapaCombate != null) mapaCombate.dispose();
        if (jugador1 != null) jugador1.dispose();
        if (jugador2 != null) jugador2.dispose();
        
    }
}
