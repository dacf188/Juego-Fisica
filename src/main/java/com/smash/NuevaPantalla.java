package com.smash;

import com.smash.personajes.JoelMiller;
import com.smash.personajes.PatoLucas;
import com.smash.personajes.Jinx;
import com.smash.personajes.Kratos;
import com.smash.personajes.Invencible;
import com.smash.personajes.IliaTopuria;
import com.smash.personajes.Gladiador;
import com.smash.personajes.Homelander;
import com.smash.personajes.BugsBunny;
import com.smash.personajes.Catwoman;
import com.smash.personajes.HarleyQuinn;
import com.smash.personajes.ArthurMorgan;
import com.smash.personajes.EllieWiliams;
import com.smash.personajes.AbbyAnderson;
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
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.Controller;

public class NuevaPantalla implements Screen {
    
    private final Main game;
    private Music musicaFondo;
    private SpriteBatch batch;
    private Texture fondoNuevo;
    private Stage stage;
    
    private ImageButton botonVS;
    private Texture botonnormal;
    private Texture botonmouse;
    
    private Texture marcoJ1;
    private Texture marcoJ2;
    
    private List<ImageButton> botonesPersonajes;
    
    // Cursores independientes para la selección de cada jugador
    private int indiceCursorJ1 = 0;
    private int indiceCursorJ2 = 0;
    private boolean cursorVs = false;
    
    // Posiciones fijas de los marcos de selección confirmada
    private int indiceJ1 = -1;
    private int indiceJ2 = -1;
    
    private List<Personaje> personajes;
    // Referencias a los personajes elegidos por cada jugador
    private Personaje jugador1 = null;
    private Personaje jugador2 = null;
    // Control del turno actual (para modos de un solo mando/ratón)
    private int turnoJugador = 1;
    
    // Temporizadores y retardo para evitar desplazamientos descontrolados del cursor por frame
    private float temporizadorJ1 = 0f;
    private float temporizadorJ2 = 0f;
    private final float retardoMovimiento = 0.18f;
    private boolean esperarBoton = false;
    
    // Coordenadas fijas para renderizar los modelos 2D de los personajes seleccionados
    private float p1x = 140, p1Y = 500;
    private float p2x = 1470, p2Y = 500;
    
    public NuevaPantalla(Main game) {
        this.game = game;
        this.batch = game.getBatch();
    }
    
    @Override
    /** Carga texturas, configura el audio y genera la cuadrícula de botones de personajes. */
    public void show() {
        fondoNuevo = new Texture("FondoSelecciones.png");
        marcoJ1 = new Texture("jugador1.png");
        marcoJ2 = new Texture("jugador2.png");
        
        // Configuración y reproducción de la música de la pantalla de selección
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
        
        // Configuración inicial del botón VS (oculto hasta que ambos jugadores elijan)
        botonVS = new ImageButton(estiloSiguiente);
        botonVS.setBounds((1920 - 300) / 2f, 40, 300, 150);
        botonVS.setVisible(false);
        botonVS.setProgrammaticChangeEvents(false);
        
        botonVS.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (jugador1 != null && jugador2 != null) {
                    game.setScreen(new PantallaEscenarios(game, jugador1, jugador2));
                }
            }
        });
        stage.addActor(botonVS);
        
        // Inicialización y registro de la lista completa de personajes jugables
        personajes = new ArrayList<>();
        personajes.add(new Gladiador());
        personajes.add(new HarleyQuinn());
        personajes.add(new ArthurMorgan());
        personajes.add(new EllieWiliams());
        personajes.add(new Homelander());
        personajes.add(new Jinx());
        personajes.add(new JoelMiller());
        personajes.add(new AbbyAnderson());
        personajes.add(new Kratos());
        personajes.add(new Catwoman());
        personajes.add(new IliaTopuria());
        personajes.add(new PatoLucas());
        personajes.add(new BugsBunny());
        personajes.add(new Invencible());        
        
        botonesPersonajes = new ArrayList<>();
        
        // Creación automática de los botones de interfaz para cada personaje
        for (final Personaje p : personajes) {
            ImageButton btn = p.crearBotonMenu();
            botonesPersonajes.add(btn);
            btn.addListener(new ChangeListener() {
               @Override
               public void changed(ChangeEvent event, Actor actor) {
                   int clickedIndex = botonesPersonajes.indexOf(btn);
                   cursorVs = false;
                   p.reproducirSeleccion();
                   if (turnoJugador == 1) {
                       jugador1 = p;
                       indiceJ1 = clickedIndex;
                       indiceCursorJ1 = clickedIndex;
                       turnoJugador = 2;
                   } else {
                       jugador2 = p;
                       indiceJ2 = clickedIndex;
                       indiceCursorJ2 = clickedIndex;
                       turnoJugador = 1;
                   }
                   if (jugador1 != null && jugador2 != null) {
                       botonVS.setVisible(true);
                   }
               }
            });
            stage.addActor(btn);
        }
        AdministradorMando.reiniciarEstados();
    }

    @Override
    /** Bucle principal: gestiona entradas por mandos físicos, estados de selección y renderizado gráfico. */
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0f);
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        
        int totalMandos = Controllers.getControllers().size;
        Controller mandoJ1 = totalMandos > 0 ? Controllers.getControllers().get(0) : null;
        Controller mandoJ2 = totalMandos > 1 ? Controllers.getControllers().get(1) : null;
        
        boolean dosMandosActivos = (totalMandos >= 2);

        // Decremento de temporizadores de movimiento en cada frame
        if (temporizadorJ1 > 0) temporizadorJ1 -= delta;
        if (temporizadorJ2 > 0) temporizadorJ2 -= delta;

        if (dosMandosActivos) {
            // ==========================================
            // MODO: DOS MANDOS INDEPENDIENTES SIMULTÁNEOS
            // ==========================================
            
            // --- CONTROLADOR 1 (Jugador 1) ---
            if (mandoJ1 != null && jugador1 == null && !cursorVs) {
                if (temporizadorJ1 <= 0) {
                    if (AdministradorMando.esDerecha(mandoJ1)) {
                        indiceCursorJ1 = (indiceCursorJ1 + 1) % personajes.size();
                        temporizadorJ1 = retardoMovimiento;
                    } else if (AdministradorMando.esIzquierda(mandoJ1)) {
                        indiceCursorJ1 = (indiceCursorJ1 - 1 + personajes.size()) % personajes.size();
                        temporizadorJ1 = retardoMovimiento;
                    } else if (AdministradorMando.esAbajo(mandoJ1)) {
                        indiceCursorJ1 = (indiceCursorJ1 + 7) % personajes.size();
                        temporizadorJ1 = retardoMovimiento;
                    } else if (AdministradorMando.esArriba(mandoJ1)) {
                        indiceCursorJ1 = (indiceCursorJ1 - 7 + personajes.size()) % personajes.size();
                        temporizadorJ1 = retardoMovimiento;
                    }
                }

                if (AdministradorMando.esConfirmar(mandoJ1)) {
                    Personaje pSeleccionado = personajes.get(indiceCursorJ1);
                    pSeleccionado.reproducirSeleccion();
                    jugador1 = pSeleccionado;
                    indiceJ1 = indiceCursorJ1;
                    
                    if (jugador1 != null && jugador2 != null) {
                        botonVS.setVisible(true);
                    }
                }
            }

            // --- CONTROLADOR 2 (Jugador 2) ---
            if (mandoJ2 != null && jugador2 == null && !cursorVs) {
                if (temporizadorJ2 <= 0) {
                    if (AdministradorMando.esDerecha(mandoJ2)) {
                        indiceCursorJ2 = (indiceCursorJ2 + 1) % personajes.size();
                        temporizadorJ2 = retardoMovimiento;
                    } else if (AdministradorMando.esIzquierda(mandoJ2)) {
                        indiceCursorJ2 = (indiceCursorJ2 - 1 + personajes.size()) % personajes.size();
                        temporizadorJ2 = retardoMovimiento;
                    } else if (AdministradorMando.esAbajo(mandoJ2)) {
                        indiceCursorJ2 = (indiceCursorJ2 + 7) % personajes.size();
                        temporizadorJ2 = retardoMovimiento;
                    } else if (AdministradorMando.esArriba(mandoJ2)) {
                        indiceCursorJ2 = (indiceCursorJ2 - 7 + personajes.size()) % personajes.size();
                        temporizadorJ2 = retardoMovimiento;
                    }
                }

                if (AdministradorMando.esConfirmar(mandoJ2)) {
                    Personaje pSeleccionado = personajes.get(indiceCursorJ2);
                    pSeleccionado.reproducirSeleccion();
                    jugador2 = pSeleccionado;
                    indiceJ2 = indiceCursorJ2;
                    
                    if (jugador1 != null && jugador2 != null) {
                        botonVS.setVisible(true);
                    }
                }
            }

            // Navegación hacia el botón VS cuando ambos jugadores han seleccionado personaje
            if (jugador1 != null && jugador2 != null && mandoJ1 != null) {
                if (!cursorVs && AdministradorMando.esAbajo(mandoJ1)) {
                    cursorVs = true;
                    esperarBoton = true;
                } else if (cursorVs && AdministradorMando.esArriba(mandoJ1)) {
                    cursorVs = false;
                    esperarBoton = false;
                }
                if (cursorVs) {
                    if (esperarBoton) {
                        // Paso 1: Espera a que el usuario suelte el botón de confirmar con el que llegó
                        if (!AdministradorMando.esConfirmar(mandoJ1)) {
                            esperarBoton = false;
                        }
                    } else {
                        // Paso 2: Detecta nueva pulsación para avanzar a la pantalla de escenarios
                        if (AdministradorMando.esConfirmar(mandoJ1)) {
                            game.setScreen(new PantallaEscenarios(game, jugador1, jugador2));
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // MODO: UN SOLO MANDO / RATÓN (POR TURNOS)
            // ==========================================
            if (mandoJ1 != null) {
                boolean ambosSeleccionados = (jugador1 != null && jugador2 != null);

                if (!cursorVs) {
                    if (temporizadorJ1 <= 0) {
                        if (AdministradorMando.esDerecha(mandoJ1)) {
                            indiceCursorJ1 = (indiceCursorJ1 + 1) % personajes.size();
                            temporizadorJ1 = retardoMovimiento;
                        } else if (AdministradorMando.esIzquierda(mandoJ1)) {
                            indiceCursorJ1 = (indiceCursorJ1 - 1 + personajes.size()) % personajes.size();
                            temporizadorJ1 = retardoMovimiento;
                        } else if (AdministradorMando.esAbajo(mandoJ1)) {
                            if (ambosSeleccionados) {
                                cursorVs = true; // Salto directo al botón VS desde cualquier fila si ya eligieron
                            } else {
                                indiceCursorJ1 = (indiceCursorJ1 + 7) % personajes.size();
                                temporizadorJ1 = retardoMovimiento;
                            }
                        } else if (AdministradorMando.esArriba(mandoJ1)) {
                            indiceCursorJ1 = (indiceCursorJ1 - 7 + personajes.size()) % personajes.size();
                            temporizadorJ1 = retardoMovimiento;
                        }
                    }

                    if (!cursorVs && AdministradorMando.esConfirmar(mandoJ1)) {
                        Personaje pSeleccionado = personajes.get(indiceCursorJ1);
                        pSeleccionado.reproducirSeleccion();
                        
                        if (jugador1 == null) {
                            jugador1 = pSeleccionado;
                            indiceJ1 = indiceCursorJ1;
                            turnoJugador = 2;
                        } else if (jugador2 == null) {
                            jugador2 = pSeleccionado;
                            indiceJ2 = indiceCursorJ1;
                            turnoJugador = 1;
                        }
                        
                        if (jugador1 != null && jugador2 != null) {
                            botonVS.setVisible(true);
                        }
                    }
                } else {
                    if (AdministradorMando.esArriba(mandoJ1)) {
                        cursorVs = false;
                    }
                    if (AdministradorMando.esConfirmar(mandoJ1)) {
                        if (jugador1 != null && jugador2 != null) {
                            game.setScreen(new PantallaEscenarios(game, jugador1, jugador2));
                        }
                    }
                }
            }
        }
        
        // Actualización visual del estado 'hover' del botón VS según la posición del cursor
        if (botonVS.isVisible()) {
            ImageButton.ImageButtonStyle estiloActual = botonVS.getStyle();
            if (cursorVs) {
                TextureRegionDrawable drawableMouse = new TextureRegionDrawable(new TextureRegion(botonmouse));
                estiloActual.imageUp = drawableMouse;
                estiloActual.imageOver = drawableMouse;
            } else {
                TextureRegionDrawable drawableNormal = new TextureRegionDrawable(new TextureRegion(botonnormal));
                estiloActual.imageUp = drawableNormal;
                estiloActual.imageOver = drawableNormal;
            }
            botonVS.setStyle(estiloActual);
        }
        
        // Renderizado gráfico de fondo, modelos de personajes y marcos de selección
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
        
        // --- DIBUJO DE MARCOS DE SELECCIÓN INDEPENDIENTES PARA J1 Y J2 ---
        if (botonesPersonajes != null && !botonesPersonajes.isEmpty()) {
            
            // 1. Marco indicador del Jugador 1
            if (indiceJ1 != -1) {
                ImageButton btnJ1 = botonesPersonajes.get(indiceJ1);
                batch.draw(marcoJ1, btnJ1.getX(), btnJ1.getY(), btnJ1.getWidth(), btnJ1.getHeight());
            } else if (jugador1 == null && !cursorVs) {
                ImageButton botonActual = botonesPersonajes.get(indiceCursorJ1);
                batch.draw(marcoJ1, botonActual.getX(), botonActual.getY(), botonActual.getWidth(), botonActual.getHeight());
            }

            // 2. Marco indicador del Jugador 2
            if (indiceJ2 != -1) {
                ImageButton btnJ2 = botonesPersonajes.get(indiceJ2);
                batch.draw(marcoJ2, btnJ2.getX(), btnJ2.getY(), btnJ2.getWidth(), btnJ2.getHeight());
            } else if (jugador2 == null && !cursorVs) {
                if (dosMandosActivos || jugador1 != null) {
                    ImageButton botonActual = botonesPersonajes.get(indiceCursorJ2);
                    batch.draw(marcoJ2, botonActual.getX(), botonActual.getY(), botonActual.getWidth(), botonActual.getHeight());
                }
            }
        }
        
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override
    /** Ajusta el viewport del escenario al redimensionar la ventana. */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    /** Pausa la música de fondo al minimizar o perder el foco de la pantalla. */
    public void pause() {
        if (musicaFondo != null && musicaFondo.isPlaying()) {
            musicaFondo.pause();
        }
    }

    @Override
    /** Reanuda la música de fondo al volver a la pantalla activa. */
    public void resume() {
        if (musicaFondo != null && !musicaFondo.isPlaying()) {
            musicaFondo.play();
        }
    }

    @Override
    /** Detiene la música de fondo al ocultar o salir de esta pantalla. */
    public void hide() {
        if (musicaFondo != null) {
            musicaFondo.stop();
        }
    }

    @Override
    /** Libera los recursos de texturas, audio y escenario almacenados en RAM y GPU. */
    public void dispose() {
        if (fondoNuevo != null) fondoNuevo.dispose();
        if (stage != null) stage.dispose();
        if (musicaFondo != null) {
            musicaFondo.stop();
            musicaFondo.dispose();
        }
        if (personajes != null) {
            for (Personaje p : personajes) {
                p.dispose();
            }
        }
        if (botonnormal != null) botonnormal.dispose();
        if (botonmouse != null) botonmouse.dispose();
    }
}