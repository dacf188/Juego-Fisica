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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.Controller;
import com.smash.Main;
import com.smash.NuevaPantalla;
import com.smash.Personaje;
import com.smash.PantallaPelea;
import com.smash.AdministradorMando;

public class PantallaResultados {

    private Stage stageResultados;
    private Label labelAnuncio;
    private BitmapFont fuenteTitulo;
    private Texture texturaFondoOscuro;
    private Texture texturaPanel;

    // Control de audio y secuencia de victoria
    private boolean audioReproducido = false;
    private boolean wins = false;
    private float temporizador = 0f;

    // Referencias de efectos de sonido
    private Sound soundWins;
    private Sound soundTie;
    private Sound soundNombre;

    // Control de mandos y opciones del menú
    private Array<TextButton> botonesOpciones;
    private int indiceCursor = 0; // 0: Revancha, 1: Selección, 2: Menú Principal
    private final Main gameRef;
    private final Personaje p1Ref;
    private final Personaje p2Ref;
    private final String mapaRef;
    
    // Control de velocidad de desplazamiento del cursor con el mando
    private float temporizadorMovimiento = 0f;
    private final float retardoMovimiento = 0.18f;

    public PantallaResultados(Viewport viewport, SpriteBatch batch, String resultadoTexto, String ganadorNombre,
            Main game, Personaje jugador1, Personaje jugador2, String rutaMapa) {
        this.gameRef = game;
        this.p1Ref = jugador1;
        this.p2Ref = jugador2;
        this.mapaRef = rutaMapa;

        stageResultados = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stageResultados);

        // 1. Fondo oscuro semitransparente para desvanecer la escena de combate de fondo
        Pixmap pixmapOscuro = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapOscuro.setColor(new Color(0, 0, 0, 0.75f));
        pixmapOscuro.fill();
        texturaFondoOscuro = new Texture(pixmapOscuro);
        pixmapOscuro.dispose();

        Image fondoOscuro = new Image(texturaFondoOscuro);
        fondoOscuro.setSize(1920, 1080);
        stageResultados.addActor(fondoOscuro);

        // 2. Textura pixelada para el panel central
        Pixmap pixmapPanel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapPanel.setColor(new Color(0.1f, 0.12f, 0.2f, 0.95f));
        pixmapPanel.fill();
        texturaPanel = new Texture(pixmapPanel);
        pixmapPanel.dispose();
        texturaPanel.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Configuración de fuentes y estilo del título de resultados
        fuenteTitulo = new BitmapFont();
        fuenteTitulo.getData().setScale(2.5f);

        Label.LabelStyle estiloLabel = new Label.LabelStyle();
        estiloLabel.font = fuenteTitulo;
        estiloLabel.fontColor = Color.YELLOW;

        String textoFinal = (ganadorNombre != null && !ganadorNombre.isEmpty())
                ? (ganadorNombre.toUpperCase() + " WINS")
                : resultadoTexto;

        labelAnuncio = new Label(textoFinal, estiloLabel);

        // 3. Estilos y creación de botones de opciones
        BitmapFont fuenteBoton = new BitmapFont();
        fuenteBoton.getData().setScale(1.5f);

        TextButton.TextButtonStyle estiloBoton = new TextButton.TextButtonStyle();
        estiloBoton.font = fuenteBoton;
        estiloBoton.fontColor = Color.WHITE;
        estiloBoton.overFontColor = Color.YELLOW;
        estiloBoton.downFontColor = Color.GRAY;

        TextButton btnRevancha = new TextButton("REVANCHA", estiloBoton);
        TextButton btnSeleccion = new TextButton("SELECCIONAR PERSONAJES", estiloBoton);
        TextButton btnMenu = new TextButton("MENU PRINCIPAL", estiloBoton);

        botonesOpciones = new Array<>();
        botonesOpciones.add(btnRevancha);
        botonesOpciones.add(btnSeleccion);
        botonesOpciones.add(btnMenu);

        // Listeners de clics de ratón sincronizados con el índice del cursor
        btnRevancha.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                indiceCursor = 0;
                ejecutarAccion(0);
            }
        });

        btnSeleccion.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                indiceCursor = 1;
                ejecutarAccion(1);
            }
        });

        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                indiceCursor = 2;
                ejecutarAccion(2);
            }
        });

        // 4. Tabla contenedora de la interfaz del menú de resultados
        Table panelCentral = new Table();
        panelCentral.setSize(900, 600);
        panelCentral.setPosition((1920 - 900) / 2f, (1080 - 600) / 2f);

        panelCentral.center();
        panelCentral.add(labelAnuncio).padBottom(40f).row();
        panelCentral.add(btnRevancha).padBottom(25f).row();
        panelCentral.add(btnSeleccion).padBottom(25f).row();
        panelCentral.add(btnMenu).row();

        stageResultados.addActor(panelCentral);

        // Carga de recursos de audio para resultados y locuciones de nombres
        try {
            soundWins = Gdx.audio.newSound(Gdx.files.internal("SonidosNombres/AudioWins.wav"));
            soundTie = Gdx.audio.newSound(Gdx.files.internal("SonidosNombres/Tie.wav"));
            if (ganadorNombre != null && !ganadorNombre.isEmpty()) {
                String rutaAudio = "SonidosNombres/Audio" + ganadorNombre + ".wav";
                if (Gdx.files.internal(rutaAudio).exists()) {
                    soundNombre = Gdx.audio.newSound(Gdx.files.internal(rutaAudio));
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR CARGANDO AUDIOS DE RESULTADOS");
        }

        AdministradorMando.reiniciarEstados();
    }

    /** Reproduce los efectos de audio correspondientes al resultado del combate (victoria o empate). */
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
                temporizador = 1.3f;
            }
        }
    }

    /** Bucle principal: gestiona la navegación por mando de PS4, efectos sonoros encadenados y renderizado del Stage. */
    public void render(float delta) {
        // Lectura del mando de PS4 para navegar entre las opciones del menú de resultados
        Controller mandoJ1 = Controllers.getControllers().size > 0 ? Controllers.getControllers().get(0) : null;
        if (mandoJ1 != null) {
            if (AdministradorMando.esAbajo(mandoJ1)) {
                indiceCursor = (indiceCursor + 1) % botonesOpciones.size;
                temporizadorMovimiento = retardoMovimiento;
            } else if (AdministradorMando.esArriba(mandoJ1)) {
                indiceCursor = (indiceCursor - 1 + botonesOpciones.size) % botonesOpciones.size;
                temporizadorMovimiento = retardoMovimiento;
            }

            // Confirmación de la opción seleccionada usando el botón del mando de PS4
            if (AdministradorMando.esConfirmar(mandoJ1)) {
                ejecutarAccion(indiceCursor);
            }
        }

        // Resalta visualmente el botón sobre el cual se encuentra posicionado el cursor
        for (int i = 0; i < botonesOpciones.size; i++) {
            TextButton btn = botonesOpciones.get(i);
            if (i == indiceCursor) {
                btn.getLabel().setColor(Color.YELLOW);
            } else {
                btn.getLabel().setColor(Color.WHITE);
            }
        }

        // Temporizador para reproducir el sonido secundario de victoria tras la locución del nombre
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

    /** Ejecuta la acción correspondiente a la opción seleccionada (Revancha, Selección o Menú). */
    private void ejecutarAccion(int opcion) {
        dispose();
        switch (opcion) {
            case 0: // Revancha
                gameRef.setScreen(new PantallaPelea(gameRef, p1Ref, p2Ref, mapaRef));
                break;
            case 1: // Seleccionar Personajes
                gameRef.setScreen(new NuevaPantalla(gameRef));
                break;
            case 2: // Menú Principal
                gameRef.volverMenu();
                break;
        }
    }

    /** Actualiza el viewport del escenario al redimensionar la ventana. */
    public void resize(int width, int height) {
        stageResultados.getViewport().update(width, height, true);
    }

    /** Libera de la memoria RAM y GPU todos los recursos de interfaz, fuentes y audios de resultados. */
    public void dispose() {
        if (stageResultados != null) {
            stageResultados.dispose();
        }
        if (fuenteTitulo != null) {
            fuenteTitulo.dispose();
        }
        if (texturaFondoOscuro != null) {
            texturaFondoOscuro.dispose();
        }
        if (texturaPanel != null) {
            texturaPanel.dispose();
        }

        if (soundWins != null) {
            soundWins.dispose();
        }
        if (soundTie != null) {
            soundTie.dispose();
        }
        if (soundNombre != null) {
            soundNombre.dispose();
        }
    }
}