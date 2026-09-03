package com.smash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class NuevaPantalla implements Screen {
    
    private final Main game;
    private SpriteBatch batch;
    private Texture fondoNuevo;
    
    public NuevaPantalla(Main game){
        this.game = game;
        this.batch = game.getBatch();
    }
    
    @Override
    //SE CARGAN Y PREPARAN IMAGENES, SONIDOS, FUENTES Y ESCENARIOS
    public void show() {
        fondoNuevo = new Texture("Modos.png");
        
    }

    @Override
    //ES EL BUCLE DEL JUEGO, DELTA REPRESENTA LOS SEGUNDOS DEL ÚLTIMO FOTOGRAMA
    //SE LIMPIAN Y SE ACTUALIZAN PATALLAS DE ELEMENTOS
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.2f, 0.4f, 1f);
        //SE DIBUJAN NUEVOS ELEMENTOS EN LA PANTALLA
        batch.begin();
        batch.draw(fondoNuevo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    //RECIBE LAS NUEVAS DIMENSIONES DE LA PANTALLA
    public void resize(int width, int height) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    //GUARDA EL ESTADO DEL JUEGO, PAUSAR MÚSICA O DETENER TEMPORIZADORES
    public void pause() {
        
    }

    @Override
    //PERMITE RESTAURAR AL JUEGO A SU ESTADO ACTIVO, REANUDA TODO LO QUE SE QUEDÓ EN PAUSA
    public void resume() {
        
    }

    @Override
    //PAUSA ELEMENTOS O QUITAR PROCESADORES
    public void hide() {
        
    }

    @Override
    //LIBERA LA TARJETA GRÁFICA Y LA RAM DE TODO EL CONTENIDO
    public void dispose() {
        if (fondoNuevo != null) fondoNuevo.dispose();
        
    }
}
