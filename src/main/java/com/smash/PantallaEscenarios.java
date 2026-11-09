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
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

public class PantallaEscenarios implements Screen {
    
    private final Main game;
    private final Personaje jugador1;
    private final Personaje jugador2;
    private SpriteBatch batch;
    private Stage stage;

    private Texture fondoEscenarios;
    private Music musicaFondo;
    
    // Botón de retorno al menú anterior
    private Texture regresarNormal;
    private Texture regresarMouse;
    private ImageButton botonRegresar;
    
    // Control de mandos y selección de mapas en cuadrícula
    private Array<ImageButton> botonesMapas;
    private int indiceCurso = 0;
    private Texture marcoMapa;
    
    // Rutas de los archivos de fondo (escenario completo) y miniaturas para la UI
    private final String[] rutasEscenarios = {
      "Escenarios/Abandonado.png",  
      "Escenarios/Calle.png",  
      "Escenarios/Castillo.png",  
      "Escenarios/Espacio.png",  
      "Escenarios/Minecraft.png",
      "Escenarios/Monte.png",
    };
    private final String[] rutasMiniaturas = {
      "Miniaturas/Abandonado.png",  
      "Miniaturas/Calle.png",  
      "Miniaturas/Castillo.png",  
      "Miniaturas/Espacio.png",  
      "Miniaturas/Minecraft.png",
      "Miniaturas/Monte.png",
    };
    private Array<Texture> texturasCargadas;
    
    public PantallaEscenarios(Main game, Personaje jugador1, Personaje jugador2) {
        this.game = game;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.batch = game.getBatch();
    }

    @Override
    /** Inicializa el escenario, texturas de mapas, audio y la disposición en cuadrícula de la UI. */
    public void show() {
        stage = new Stage(new FillViewport(1920, 1080), batch);
        Gdx.input.setInputProcessor(stage);
        texturasCargadas = new Array<>();
        fondoEscenarios = new Texture("PantallaEscenarios.png");
        marcoMapa = new Texture("mapasSelector.png");
        
        // Configuración y reproducción de la música de fondo
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("Musica/PistaPersonajes.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.2f);
        musicaFondo.play();
        
        // Configuración del botón de retorno
        regresarNormal = new Texture("regresarNormal.png");
        regresarMouse = new Texture("regresarMouse.png");
        
        ImageButton.ImageButtonStyle estiloRegresar = new ImageButton.ImageButtonStyle();
        estiloRegresar.imageUp = new TextureRegionDrawable(new TextureRegion(regresarNormal));
        estiloRegresar.imageOver = new TextureRegionDrawable(new TextureRegion(regresarMouse));
        botonRegresar = new ImageButton(estiloRegresar);
        botonRegresar.setBounds(50, 930, 200, 80);
        
        botonRegresar.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               game.setScreen(new NuevaPantalla(game));
           }
        });
        stage.addActor(botonRegresar);
        
        // Parámetros de distribución para la cuadrícula de mapas (3 columnas)
        int columnas = 3;
        float anchoMapa = 380f;
        float altoMapa = 210;
        float espaciadoX = 60f;
        float espaciadoY = 40f;

        float anchoTotal = (columnas * anchoMapa) + ((columnas - 1) * espaciadoX);
        float iniciaX = (1920f - anchoTotal) / 2f;
        float iniciaY = 530f;
        
        botonesMapas = new Array<>();
        
        // Generación dinámica de los botones de mapas a partir de sus miniaturas
        for (int i = 0; i < rutasEscenarios.length; i++) {
            final String rutaActual = rutasEscenarios[i];
            String rutaMiniatura = rutasMiniaturas[i];
            
            Texture texturaMapa = new Texture(Gdx.files.internal(rutaMiniatura));
            texturasCargadas.add(texturaMapa);
            
            TextureRegionDrawable drawableMapa = new TextureRegionDrawable(new TextureRegion(texturaMapa));
            ImageButton botonMapa = new ImageButton(drawableMapa);
            
            int fila = i / columnas;
            int col = i % columnas;
            
            float posX = iniciaX + col * (anchoMapa + espaciadoX);
            float posY = iniciaY - fila * (altoMapa + espaciadoY);
            botonMapa.setBounds(posX, posY, anchoMapa, altoMapa);
        
            botonMapa.addListener(new ChangeListener() {
               @Override
               public void changed(ChangeEvent event, Actor actor) {
                   dispose();
                   game.setScreen(new PantallaPelea(game, jugador1, jugador2, rutaActual));
               }
            });
            stage.addActor(botonMapa);
            botonesMapas.add(botonMapa);
        }
        AdministradorMando.reiniciarEstados();
    }

    @Override
    /** Bucle principal: maneja la navegación por mando en la cuadrícula de mapas y el renderizado gráfico. */
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        
        // Lectura del mando para mover el cursor entre los mapas disponibles
        Controller mandoJ1 = Controllers.getControllers().size > 0 ? Controllers.getControllers().get(0) : null;
        if (mandoJ1 != null) {
            if (AdministradorMando.esDerecha(mandoJ1)) {
                indiceCurso = (indiceCurso + 1) % botonesMapas.size;
            } else if (AdministradorMando.esIzquierda(mandoJ1)) {
                indiceCurso = (indiceCurso - 1 + botonesMapas.size) % botonesMapas.size;
            } else if (AdministradorMando.esAbajo(mandoJ1)) {
                indiceCurso = (indiceCurso + 3) % botonesMapas.size; // Salto a la fila inferior
            } else if (AdministradorMando.esArriba(mandoJ1)) {
                indiceCurso = (indiceCurso - 3 + botonesMapas.size) % botonesMapas.size; // Salto a la fila superior
            }

            // Confirmación de selección del mapa activo
            if (AdministradorMando.esConfirmar(mandoJ1)) {
                String rutaSeleccionada = rutasEscenarios[indiceCurso];
                dispose();
                game.setScreen(new PantallaPelea(game, jugador1, jugador2, rutaSeleccionada));
            }
            // Retorno a la pantalla de selección de personajes mediante botón cancelar
            if (AdministradorMando.esCancelar(mandoJ1)) {
                dispose();
                game.setScreen(new NuevaPantalla(game));
            }
        }
        
        // Renderizado del fondo y los marcos indicadores del mapa seleccionado
        batch.begin();
        if (fondoEscenarios != null) {
            batch.draw(fondoEscenarios, 0, 0, stage.getWidth(), stage.getHeight());
        }
        
        if (botonesMapas != null && botonesMapas.size > 0 && marcoMapa != null) {
            ImageButton botonActual = botonesMapas.get(indiceCurso);
            batch.draw(marcoMapa, botonActual.getX(), botonActual.getY(), botonActual.getWidth(), botonActual.getHeight());
        
            float extraAncho = 24f;
            float extraAlto = 24f;

            // Doble marco exterior para resaltar el mapa activo con el teclado/mando
            batch.draw(
                    marcoMapa,
                    botonActual.getX() - (extraAncho / 2f),
                    botonActual.getY() - (extraAlto / 2f),
                    botonActual.getWidth() + extraAncho,
                    botonActual.getHeight() + extraAlto
            );
        }
        
        batch.end();
        
        stage.act(delta);
        stage.draw();
    }

    @Override
    /** Actualiza el viewport al redimensionar la ventana de la aplicación. */
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
    /** Detiene la música al ocultar o cambiar de pantalla. */
    public void hide() {
        if (musicaFondo != null) {
            musicaFondo.stop();
        }
    }

    @Override
    /** Libera de la memoria RAM y GPU todas las texturas, escenarios y recursos de audio. */
    public void dispose() {
        if (stage != null) stage.dispose();
        if (fondoEscenarios != null) fondoEscenarios.dispose();
        if (marcoMapa != null) marcoMapa.dispose();
        if (regresarNormal != null) regresarNormal.dispose();
        if (regresarMouse != null) regresarMouse.dispose();
        if (musicaFondo != null) {
            musicaFondo.stop();
            musicaFondo.dispose();
        }
        for (Texture t : texturasCargadas) {
            if (t != null) t.dispose();
        }
    }
}