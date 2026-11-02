package com.smash.Pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.smash.Main;
import com.smash.NuevaPantalla;
import com.smash.Personaje;
import com.smash.PantallaPelea; // Asegúrate de importar tu pantalla de pelea

public class PantallaResultados {
    private Stage stageResultados;
    private Label labelAnuncio;
    private BitmapFont fuenteTitulo;
    private Texture texturaFondoOscuro;
    private Texture texturaPanel;
    
    //AUDIO Y SECUENCIA
    private boolean audioReproducido = false;
    private boolean wins = false;
    private float temporizador = 0f;
    
    //REFERENCIAS AUDIOS
    private Sound soundWins;
    private Sound soundTie;
    private Sound soundNombre;

    public PantallaResultados(Viewport viewport, SpriteBatch batch, String resultadoTexto, String ganadorNombre,
                              Main game, Personaje jugador1, Personaje jugador2, String rutaMapa) {
        stageResultados = new Stage(viewport, batch);

        Gdx.input.setInputProcessor(stageResultados);
        
        // 1. FONDO OSCURO FINO (Desvanece la pelea de atrás)
        Pixmap pixmapOscuro = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapOscuro.setColor(new Color(0, 0, 0, 0.75f)); // 75% de opacidad
        pixmapOscuro.fill();
        texturaFondoOscuro = new Texture(pixmapOscuro);
        pixmapOscuro.dispose();

        Image fondoOscuro = new Image(texturaFondoOscuro);
        fondoOscuro.setSize(1920, 1080);
        stageResultados.addActor(fondoOscuro);

        // 2. TEXTURA PIXELADA PARA LA CAJA / PANEL CENTRAL
        Pixmap pixmapPanel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapPanel.setColor(new Color(0.1f, 0.12f, 0.2f, 0.95f)); // Color azul oscuro arcade
        pixmapPanel.fill();
        texturaPanel = new Texture(pixmapPanel);
        pixmapPanel.dispose();
        texturaPanel.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Fuente para el título
        fuenteTitulo = new BitmapFont();
        fuenteTitulo.getData().setScale(2.5f);

        Label.LabelStyle estiloLabel = new Label.LabelStyle();
        estiloLabel.font = fuenteTitulo;
        estiloLabel.fontColor = Color.YELLOW;

        // Texto dinámico 
        String textoFinal = (ganadorNombre != null && !ganadorNombre.isEmpty()) 
                ? (ganadorNombre.toUpperCase() + " WINS") 
                : resultadoTexto;
        
        labelAnuncio = new Label(textoFinal, estiloLabel);

        // 3. ESTILOS Y CREACIÓN DE BOTONES
        BitmapFont fuenteBoton = new BitmapFont();
        fuenteBoton.getData().setScale(1.5f);
        
        TextButton.TextButtonStyle estiloBoton = new TextButton.TextButtonStyle();
        estiloBoton.font = fuenteBoton;
        estiloBoton.fontColor = Color.WHITE;
        estiloBoton.overFontColor = Color.YELLOW; // Color al pasar el cursor
        estiloBoton.downFontColor = Color.GRAY;   // Color al presionar

        TextButton btnRevancha = new TextButton("REVANCHA", estiloBoton);
        TextButton btnSeleccion = new TextButton("SELECCIONAR PERSONAJES", estiloBoton);
        TextButton btnMenu = new TextButton("MENU PRINCIPAL", estiloBoton);

        // Acciones de los botones (Listeners)
        btnRevancha.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PantallaPelea(game, jugador1, jugador2, rutaMapa));
            }
        });

        btnSeleccion.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new NuevaPantalla(game));
            }
        });

        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.volverMenu();
            }
        });

        // 4. TABLA CONTENEDORA (Caja central con diseño y botones)
        Table panelCentral = new Table();
        panelCentral.setSize(900, 600);
        panelCentral.setPosition((1920 - 900) / 2f, (1080 - 600) / 2f);
        
        panelCentral.center();
        panelCentral.add(labelAnuncio).padBottom(40f).row();
        panelCentral.add(btnRevancha).padBottom(25f).row();
        panelCentral.add(btnSeleccion).padBottom(25f).row();
        panelCentral.add(btnMenu).row();
        
        stageResultados.addActor(panelCentral);
       
        try {
            soundWins = Gdx.audio.newSound(Gdx.files.internal("SonidosNombres/AudioWins.wav"));
            soundTie = Gdx.audio.newSound(Gdx.files.internal("SonidosNombres/Tie.wav"));
            if (ganadorNombre != null && !ganadorNombre.isEmpty()) {
                String rutaAudio = "SonidosNombres/Audio" + ganadorNombre + ".wav";
                
                if (Gdx.files.internal(rutaAudio).exists()) {
                    soundNombre = Gdx.audio.newSound(Gdx.files.internal(rutaAudio));
                } else {
                    System.out.println("NO SE ENCONTRO EL AUDIO" + rutaAudio);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }

    public void reproducirAudioResultado(boolean esEmpate, String ganador) {
        if (!audioReproducido) {
            audioReproducido = true;
            if (esEmpate) {
                if (soundTie != null) {
                    soundTie.play(1.0f);
                }
            } else {
                if (soundNombre != null) {
                    soundNombre.play(1.0f);
                }
                temporizador = 1.3f; // Tiempo de pausa ideal para que termine de decir el nombre
            }
        }
    }

    public void render(float delta) {
        if (temporizador > 0 && !wins) {
            temporizador -= delta;
            if (temporizador <= 0) {
                if (soundWins != null) {
                    soundWins.play(1.3f);
                }
                wins = true;
            }
        }

        stageResultados.act(delta);
        stageResultados.draw();
    }

    public void resize(int width, int height) {
        stageResultados.getViewport().update(width, height, true);
    }

    public void dispose() {
        if (stageResultados != null) stageResultados.dispose();
        if (fuenteTitulo != null) fuenteTitulo.dispose();
        if (texturaFondoOscuro != null) texturaFondoOscuro.dispose();
        if (texturaPanel != null) texturaPanel.dispose();
        
        if (soundWins != null) soundWins.dispose();
        if (soundTie != null) soundTie.dispose();
        if (soundNombre != null) soundNombre.dispose();
    }
}