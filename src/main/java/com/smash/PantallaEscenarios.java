package com.smash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class PantallaEscenarios implements Screen{
    private final Main game;
    private final Personaje jugador1;
    private final Personaje jugador2;
    private SpriteBatch batch;
    private Stage stage;

    private Texture fondoEscenarios;
    
    //BOTON
    private Texture regresarNormal;
    private Texture regresarMouse;
    private ImageButton botonRegresar;
    //RUTA DE ESCENARIOS
    private final String[] rutasEscenarios = {
      "Escenarios/Abandonado.png",  
      "Escenarios/Calle.png",  
      "Escenarios/Castillo.png",  
      "Escenarios/Espacio.png",  
      "Escenarios/Minecraft.png",
      "Escenarios/Monte.png",
    };
    private Array<Texture> texturasCargadas;
    
    public PantallaEscenarios(Main game, Personaje jugador1, Personaje jugador2) {
        this.game = game;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.batch = game.getBatch();
    }
    @Override
    public void show (){
        stage = new Stage(new FillViewport(1920, 1080), batch);
        Gdx.input.setInputProcessor(stage);
        texturasCargadas = new Array<>();
        fondoEscenarios = new Texture("PantallaEscenarios.png");
        //BOTON
        regresarNormal = new Texture("regresarNormal.png");
        regresarMouse = new Texture("regresarMouse.png");
        
        ImageButton.ImageButtonStyle estiloRegresar = new ImageButton.ImageButtonStyle();
        estiloRegresar.imageUp = new TextureRegionDrawable(new TextureRegion(regresarNormal));
        estiloRegresar.imageOver = new TextureRegionDrawable(new TextureRegion(regresarMouse));
        botonRegresar = new ImageButton(estiloRegresar);
        botonRegresar.setBounds(50, 930, 200, 80);
        
        botonRegresar.addListener(new ChangeListener(){
           @Override
           public void changed (ChangeEvent event, Actor actor){
               game.setScreen(new NuevaPantalla(game));
           }
        });
        stage.addActor(botonRegresar);
        
        int columnas = 3;
        float anchoMapa = 380f;
        float altoMapa = 210;
        float espaciadoX = 60f;
        float espaciadoY = 40f;

        float anchoTotal = (columnas * anchoMapa) + ((columnas - 1) * espaciadoX);
        float iniciaX = (1920f - anchoTotal) / 2f;
        float iniciaY = 530f;
        
        for (int i = 0; i < rutasEscenarios.length; i++) {
            final String rutaActual = rutasEscenarios[i];
            
            Texture texturaMapa = new Texture(rutaActual);
            texturasCargadas.add(texturaMapa);
            
            TextureRegionDrawable drawableMapa = new TextureRegionDrawable(new TextureRegion(texturaMapa));
            ImageButton botonMapa = new ImageButton(drawableMapa);
            
            int fila = i / columnas;
            int col = i % columnas;
            
            float posX = iniciaX + col * (anchoMapa + espaciadoX);
            float posY = iniciaY - fila * (anchoMapa + espaciadoY);
            botonMapa.setBounds(posX, posY, anchoMapa, altoMapa);
            botonMapa.addListener(new ChangeListener(){
               @Override
               public void changed(ChangeEvent event, Actor actor){
                   game.setScreen(new PantallaPelea(game, jugador1, jugador2, rutaActual));
               }
            });
            stage.addActor(botonMapa);
        }
    }
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        
        batch.begin();
        if (fondoEscenarios != null) {
            batch.draw(fondoEscenarios, 0, 0, stage.getWidth(), stage.getHeight());
        }
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
        if (stage != null) stage.dispose();
        if (fondoEscenarios != null) fondoEscenarios.dispose();
        if (regresarNormal != null) regresarNormal.dispose();
        if (regresarMouse != null) regresarMouse.dispose();
        for(Texture t : texturasCargadas){
            if (t != null) t.dispose();
        }
    }
}
