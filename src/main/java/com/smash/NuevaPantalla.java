package com.smash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.util.List;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import java.util.ArrayList;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class NuevaPantalla implements Screen {
    
    private final Main game;
    private Music musicaFondo;
    private SpriteBatch batch;
    private Texture fondoNuevo;
    private Stage stage;
    
    private ImageButton botonVS;
    private Texture botonnormal;
    private Texture botonmouse;
    
    private List<Personaje> personajes;
    //VARIABLES PARA CONTROLAR LA SELECCIÓN
    private Personaje jugador1 = null;
    private Personaje jugador2 = null;
    //CONTROLA EL TURNO ACTUAL
    private int turnoJugador = 1;
    
    //COORDENADAS DE RENDERIZADO DE LA PLATAFORMA
    private float p1x = 140, p1Y = 500;
    private float p2x = 1470, p2Y = 500;
    
    public NuevaPantalla(Main game){
        this.game = game;
        this.batch = game.getBatch();
    }
    
    @Override
    //SE CARGAN Y PREPARAN IMAGENES, SONIDOS, FUENTES Y ESCENARIOS
    public void show() {
        fondoNuevo = new Texture("FondoSelecciones.png");
        //CONFIGURACIÓN MUSICA
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("Musica/PistaPersonajes.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.2f);
        musicaFondo.play();
        
        stage = new Stage(new FillViewport(1920, 1080), batch);
        Gdx.input.setInputProcessor(stage);
        
        botonnormal = new Texture("vsNormal.png");
        botonmouse = new Texture("vsMouse.png");
        
        ImageButton.ImageButtonStyle estiloSiguiente = new ImageButton.ImageButtonStyle();
        estiloSiguiente.imageUp = new TextureRegionDrawable(new TextureRegion(botonnormal));
        estiloSiguiente.imageOver = new TextureRegionDrawable(new TextureRegion(botonmouse));
        
        botonVS = new ImageButton(estiloSiguiente);
        // X y Y ADEMÁS EL ANCHO Y ALTO
        botonVS.setBounds((1920 - 300) / 2f, 40, 300, 150);
        botonVS.setVisible(false);
        botonVS.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor){
                if (jugador1 != null && jugador2 != null) {
                    game.setScreen(new PantallaEscenarios(game, jugador1, jugador2));
                }
            }
        });
        stage.addActor(botonVS);
        
        personajes = new ArrayList<>();
        personajes.add(new Gladiador());
        personajes.add(new ArthurMorgan());
        personajes.add(new HarleyQuinn());
        personajes.add(new Homelander());
        personajes.add(new EllieWiliams());
        personajes.add(new Jinx());
        personajes.add(new JoelMiller());
        personajes.add(new AbbyAnderson());
        personajes.add(new Kratos());
        personajes.add(new Catwoman());
        personajes.add(new IliaTopuria());
        personajes.add(new Invencible());
        personajes.add(new BugsBunny());
        personajes.add(new PatoLucas());
        
        for(final Personaje p : personajes){
            ImageButton btn = p.crearBotonMenu();
            btn.addListener(new ChangeListener(){
               @Override
               public void changed(ChangeEvent event, Actor actor){
                   p.reproducirSeleccion();
                   if (turnoJugador == 1) {
                       jugador1 = p;
                       turnoJugador = 2;
                   } else {
                       jugador2 = p;
                       turnoJugador = 1;
                   }
                   if (jugador1 != null && jugador2 != null) {
                       botonVS.setVisible(true);
                   }
               }
            });
            stage.addActor(btn);
        }
    }

    @Override
    //ES EL BUCLE DEL JUEGO, DELTA REPRESENTA LOS SEGUNDOS DEL ÚLTIMO FOTOGRAMA
    //SE LIMPIAN Y SE ACTUALIZAN PATALLAS DE ELEMENTOS
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0f);
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        //SE DIBUJAN NUEVOS ELEMENTOS EN LA PANTALLA
        batch.begin();
        batch.draw(fondoNuevo, 0, 0, stage.getWidth(), stage.getHeight());
        
        float anchoRecuadro = 330f;
        float altoRender = 330f;
        
        if (jugador1 != null && jugador1.getRender2d() != null) {
            float ratio1 = (float) jugador1.getRender2d().getWidth() / jugador1.getRender2d().getHeight();
            float anchoReal = altoRender * ratio1;
            float posx1 = p1x + (anchoRecuadro - anchoReal) / 2f;
            jugador1.renderizarModelo(batch, posx1, p1Y, altoRender);
        }
        if (jugador2 != null) {
            float ratio2 = (float) jugador2.getRender2d().getWidth() / jugador2.getRender2d().getHeight();
            float anchoReal2 = altoRender * ratio2;
            float posX2 = p2x + (anchoRecuadro - anchoReal2) / 2f;
            jugador2.renderizarModelo(batch, posX2, p2Y, altoRender);
        }
        
        batch.end();
        stage.act(delta);
        stage.draw();
    }
    @Override
    //RECIBE LAS NUEVAS DIMENSIONES DE LA PANTALLA
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    //GUARDA EL ESTADO DEL JUEGO, PAUSAR MÚSICA O DETENER TEMPORIZADORES
    public void pause() {
        if (musicaFondo != null && musicaFondo.isPlaying()) {
            musicaFondo.pause();
        }
    }

    @Override
    //PERMITE RESTAURAR AL JUEGO A SU ESTADO ACTIVO, REANUDA TODO LO QUE SE QUEDÓ EN PAUSA
    public void resume() {
        if (musicaFondo != null && !musicaFondo.isPlaying()) {
            musicaFondo.play();
        }
    }

    @Override
    //PAUSA ELEMENTOS O QUITAR PROCESADORES
    public void hide() {
        if (musicaFondo != null) {
            musicaFondo.stop();
        }
    }

    @Override
    //LIBERA LA TARJETA GRÁFICA Y LA RAM DE TODO EL CONTENIDO
    public void dispose() {
        if (fondoNuevo != null) fondoNuevo.dispose();
        if (stage != null) stage.dispose();
        if (musicaFondo != null) {
            musicaFondo.stop();
            musicaFondo.dispose();
        }
        if (personajes != null) {
            for(Personaje p : personajes){
                p.dispose();
            }
        }
        if (botonnormal != null) botonnormal.dispose();
        if (botonmouse != null) botonmouse.dispose();
    }
}
