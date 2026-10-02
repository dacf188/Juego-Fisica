package com.smash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class PantallaPelea implements Screen{
    private final Main game;
    private final Personaje jugador1;
    private final Personaje jugador2;

    private SpriteBatch batch;
    private Stage stage;
    private Texture mapaCombate;

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
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);

        batch.begin();
        // Dibujar el mapa de combate seleccionado
        batch.draw(mapaCombate, 0, 0, stage.getWidth(), stage.getHeight());

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
        if (stage != null) {
            stage.dispose();
        }
        if (mapaCombate != null) {
            mapaCombate.dispose();
        }
    }
}
