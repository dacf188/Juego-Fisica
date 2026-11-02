package com.smash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.smash.Pantallas.PantallaResultados;

public class PantallaPelea implements Screen{
    private final Main game;
    private final Personaje jugador1;
    private final Personaje jugador2;
    private final String rutamapa;

    private SpriteBatch batch;
    private Stage stage;
    private Texture mapaCombate;
    
    private final float suelo_Y = 100f;
    
    //CRONOMETRO
    private float tiempoPelea = 90f;
    private Label labelCronometro;
    private Label.LabelStyle estiloLabel;
    private BitmapFont fuenteCronometro;
    
    //VARIABLES BARRAS DE VIDA
    private float vidaMax = 100f;
    private float vidaActualJ1 = 100f;
    private float vidaActualJ2 = 100f;
    
    private float anchoBarra = 650f;
    private float altoBarra = 35f;
    private Texture texturaPixel;
    
    //IMAGEN PARA LAS BARRAS DE VIDA
    private Texture texturaBarra;
    
    //VISUAL J1
    private Image fondoJ1;
    private Image vidaJ1;
    private float posXJ1 = 200f;
    
    //VISUAL J2
    private Image fondoJ2;
    private Image vidaJ2;
    private float posXJ2 = 1070f;
    
    //VARIABLES PARA MENÚ DE PAUSA
    private boolean juegoPausado = false;
    private Stage stagePausa;
    private Label labelPausa;
    private Texture texturaFondoOscuro;
    private boolean solicitarPausa = false;
    
    //PANTALLA DE CARGA
    private Transicion transicion;

    //DESCRIPCIÓN JUGADORES
    private DescripcionJugador descripcionJugador;
    
    //VARIABLES DE TERMINAR BATALLA
    private boolean combateTerminado = false;
    private String mensaje = "";
    private float temporizadorResultado = 0f;
    
    //PANTALLA RESULTADOS
    private PantallaResultados pantallaResultados;
    
    private Music musicaFondo;

    public PantallaPelea(Main game, Personaje jugador1, Personaje jugador2, String rutaMapa) {
        this.game = game;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.rutamapa = rutaMapa;
        this.batch = game.getBatch();
        this.mapaCombate = new Texture(rutaMapa);
    }

    @Override
    public void show() {
        combateTerminado = false; // Limpiamos la bandera de combate terminado
        tiempoPelea = 90f;        // Reiniciamos el cronómetro
        
        // 👉 RESTABLECEMOS LAS VIDAS AL MÁXIMO AL ENTRAR A LA PANTALLA (Y EN REVANCHA)
        vidaActualJ1 = vidaMax;
        vidaActualJ2 = vidaMax;

        if (jugador1 != null) {
            jugador1.setVida(vidaMax); // Asegura que el objeto del jugador recuperesu salud interna
        }
        if (jugador2 != null) {
            jugador2.setVida(vidaMax); // Asegura que el objeto del jugador recupere su salud interna
        }

        transicion = new Transicion();
        transicion.iniciar();
        stage = new Stage(new FillViewport(1920, 1080), batch);
        Gdx.input.setInputProcessor(stage);

        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("Musica/Pelea.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.2f);
        musicaFondo.play();
        
        descripcionJugador = new DescripcionJugador(stage.getViewport(), batch);

        fuenteCronometro = new BitmapFont();
        fuenteCronometro.getData().setScale(6.0f);

        estiloLabel = new Label.LabelStyle();
        estiloLabel.font = fuenteCronometro;
        estiloLabel.fontColor = Color.WHITE;

        labelCronometro = new Label("90", estiloLabel);
        labelCronometro.setPosition(910f, 950f);
        stage.addActor(labelCronometro);
        
        transicion = new Transicion();
        transicion.iniciar();
        stage = new Stage(new FillViewport(1920, 1080), batch);
        Gdx.input.setInputProcessor(stage);

        descripcionJugador = new DescripcionJugador(stage.getViewport(), batch);

        fuenteCronometro = new BitmapFont();
        fuenteCronometro.getData().setScale(6.0f);

        estiloLabel = new Label.LabelStyle();
        estiloLabel.font = fuenteCronometro;
        estiloLabel.fontColor = Color.WHITE;

        labelCronometro = new Label("90", estiloLabel);
        labelCronometro.setPosition(910f, 950f);
        stage.addActor(labelCronometro);

        //TEXTURA DE PIXEL BARRA
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        texturaPixel = new Texture(pixmap);
        pixmap.dispose();
        texturaPixel.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        texturaBarra = new Texture("BarraVida.png");
        texturaBarra.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        float paddingX = 17f;
        float paddingY = 10f;

        float anchoInterno = anchoBarra - (paddingX * 2);
        float altoInterno = altoBarra - (paddingY * 2);

        Image marcoJ1 = new Image(texturaBarra);
        marcoJ1.setSize(anchoBarra, altoBarra + 10f);
        marcoJ1.setPosition(posXJ1, 980f);
        stage.addActor(marcoJ1);

        //JUGADOR 1 -- BARRA DE VIDA
        fondoJ1 = new Image(texturaPixel);
        fondoJ1.setSize(anchoInterno, altoInterno);
        fondoJ1.setPosition(posXJ1 + paddingX, 980f + paddingY);
        fondoJ1.setColor(Color.BLACK);
        stage.addActor(fondoJ1);
        //VIDA ROJA ACTIVA
        vidaJ1 = new Image(texturaPixel);
        vidaJ1.setSize(anchoInterno, altoInterno);
        vidaJ1.setPosition(posXJ1 + paddingX, 980f + paddingY);
        vidaJ1.setColor(Color.RED);
        stage.addActor(vidaJ1);

        Image marcoJ2 = new Image(texturaBarra);
        marcoJ2.setSize(anchoBarra, altoBarra + 10f);
        marcoJ2.setPosition(posXJ2, 980f);
        stage.addActor(marcoJ2);

        //JUGADOR 2 -- BARRA DE VIDA
        fondoJ2 = new Image(texturaPixel);
        fondoJ2.setSize(anchoInterno, altoInterno);
        fondoJ2.setPosition(posXJ2 + paddingX, 980f + paddingY);
        fondoJ2.setColor(Color.BLACK);
        stage.addActor(fondoJ2);
        //VIDA ROJA ACTIVA
        vidaJ2 = new Image(texturaPixel);
        vidaJ2.setSize(anchoInterno, altoInterno);
        vidaJ2.setPosition(posXJ2 + paddingX, 980f + paddingY);
        vidaJ2.setColor(Color.RED);
        stage.addActor(vidaJ2);

        //INTERFAZ DEL MENÚ DE PAUSA
        stagePausa = new Stage(new FillViewport(1920, 1080), batch);

        Pixmap pixmapPausa = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapPausa.setColor(new Color(0, 0, 0, 0.8f)); // Fondo semitransparente
        pixmapPausa.fill();
        texturaFondoOscuro = new Texture(pixmapPausa);
        pixmapPausa.dispose();

        Image fondoPausa = new Image(texturaFondoOscuro);
        fondoPausa.setSize(1920, 1080);
        stagePausa.addActor(fondoPausa);

        BitmapFont fuentePausa = new BitmapFont();
        fuentePausa.getData().setScale(3.0f);
        Label.LabelStyle estiloPausa = new Label.LabelStyle();
        estiloPausa.font = fuentePausa;
        estiloPausa.fontColor = Color.YELLOW;

        labelPausa = new Label("JUEGO PAUSADO", estiloPausa);
        labelPausa.setPosition(200f, 550f);
        stagePausa.addActor(labelPausa);

        Controllers.addListener(new ControllerAdapter() {
            @Override
            public boolean buttonDown(Controller controller, int buttonIndex) {
                // El índice 7 suele ser Start en la mayoría de mandos estándar de juego
                if (buttonIndex == 7) {
                    solicitarPausa = true;
                }
                return false;
            }
        });

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
        if (jugador2 != null) {
            jugador2.cargarPosesCombate();
            jugador2.setPosicion(1500f, suelo_Y);
            jugador2.setMirandoIzquierda(true);
            jugador2.setJugador2(true);
            if (mandos.size > 1) {
                jugador2.setMando(mandos.get(1));
            } else if (mandos.size == 1) {
                jugador2.setMando(null);
            }
        }
    }

    @Override
    public void render(float delta) {

        // 1. GESTIÓN DE PAUSA
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || solicitarPausa) {
            juegoPausado = !juegoPausado;
            solicitarPausa = false;
        }

        ScreenUtils.clear(0, 0, 0, 1);
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        
        descripcionJugador.actualizar(jugador1, jugador2);

        if (juegoPausado) {
            String infoJ1 = (jugador1 != null)
                    ? String.format("JUGADOR 1 (Cinemática y Estado):\n- Posición X: %.2f | Y: %.2f\n- Velocidad X: %.2f | Y: %.2f\n- Estado: %s\n- Vida: %.1f\n- En Suelo: %b",
                            jugador1.getX(), jugador1.getY(), jugador1.velocidadX, jugador1.velocidadY, jugador1.getEstado(), jugador1.getVida(), jugador1.enSuelo)
                    : "Jugador 1 no disponible";

            String infoJ2 = (jugador2 != null)
                    ? String.format("\n\nJUGADOR 2 (Cinemática y Estado):\n- Posición X: %.2f | Y: %.2f\n- Velocidad X: %.2f | Y: %.2f\n- Estado: %s\n- Vida: %.1f\n- En Suelo: %b",
                            jugador2.getX(), jugador2.getY(), jugador2.velocidadX, jugador2.velocidadY, jugador2.getEstado(), jugador2.getVida(), jugador2.enSuelo)
                    : "\n\nJugador 2 no disponible";

            labelPausa.setText("=== JUEGO PAUSADO: FÍSICA Y ESTADOS ===\n\n" + infoJ1 + infoJ2);

            batch.begin();
            batch.draw(mapaCombate, 0, 0, stage.getWidth(), stage.getHeight());
            if (jugador1 != null) jugador1.renderizarPelea(batch, 0);
            if (jugador2 != null) jugador2.renderizarPelea(batch, 0);
            batch.end();

            stagePausa.act(delta);
            stagePausa.draw();
            return;
        }

        // 2. PANTALLA DE RESULTADOS (PRIORIDAD ABSOLUTA Y SEGURA)
        if (combateTerminado && pantallaResultados != null) {
            batch.begin();
            batch.draw(mapaCombate, 0, 0, stage.getWidth(), stage.getHeight());
            if (jugador1 != null) jugador1.renderizarPelea(batch, 0);
            if (jugador2 != null) jugador2.renderizarPelea(batch, 0);
            batch.end();

            pantallaResultados.render(delta);
            return; // Detenemos totalmente el render para congelar el combate bajo los resultados
        }

        // 3. TRANSICIÓN INICIAL
        transicion.actualizar(delta);

        if (!transicion.haTerminado()) {
            batch.setColor(Color.WHITE);
            batch.begin();
            batch.draw(mapaCombate, 0, 0, stage.getWidth(), stage.getHeight());
            if (jugador1 != null) jugador1.renderizarPelea(batch, 0);
            if (jugador2 != null) jugador2.renderizarPelea(batch, 0);
            batch.end();

            transicion.renderizar(batch, stage.getWidth(), stage.getHeight());

        } else {
            // 4. FLUJO NORMAL DE COMBATE
            if (!combateTerminado) {
                if (tiempoPelea > 0) {
                    tiempoPelea -= delta;
                    if (tiempoPelea <= 0) {
                        tiempoPelea = 0;
                        combateTerminado = true;
                        String nombreGanador = null;
                        if (vidaActualJ1 > vidaActualJ2) {
                            mensaje = "GANA JUGADOR 1";
                            nombreGanador = (jugador1 != null) ? jugador1.getNombre() : "Jugador 1";
                        } else if (vidaActualJ2 > vidaActualJ1) {
                            mensaje = "GANA JUGADOR 2";
                            nombreGanador = (jugador2 != null) ? jugador2.getNombre() : "Jugador 2";
                        } else {
                            mensaje = "EMPATE";
                            nombreGanador = null;
                        }
                        pantallaResultados = new PantallaResultados(stage.getViewport(), batch, mensaje, nombreGanador, game, jugador1, jugador2, rutamapa);
                        
                        // 👉 DISPARAMOS EL AUDIO UNA SOLA VEZ AQUÍ AL ACABAR POR TIEMPO
                        boolean esEmpate = mensaje.equals("EMPATE");
                        pantallaResultados.reproducirAudioResultado(esEmpate, nombreGanador);
                    }
                    int segundos = (int) Math.ceil(tiempoPelea);
                    labelCronometro.setText(String.valueOf(segundos));
                }
            }

            if ((vidaActualJ1 <= 0 || vidaActualJ2 <= 0) && !combateTerminado) {
                combateTerminado = true;
                String nombreGanador = null;
                boolean esEmpate = false;
                if (vidaActualJ1 <= 0 && vidaActualJ2 <= 0) {
                    mensaje = "DOBLE KO";
                    nombreGanador = null;
                    esEmpate = true;
                } else if (vidaActualJ1 <= 0) {
                    mensaje = "GANA JUGADOR 2";
                    nombreGanador = (jugador2 != null) ? jugador2.getNombre() : "Jugador 2";
                } else {
                    mensaje = "GANA JUGADOR 1";
                    nombreGanador = (jugador1 != null) ? jugador1.getNombre() : "Jugador 1";
                }
                pantallaResultados = new PantallaResultados(stage.getViewport(), batch, mensaje, nombreGanador, game, jugador1, jugador2, rutamapa);
                
                // 👉 DISPARAMOS EL AUDIO UNA SOLA VEZ AQUÍ AL QUEDARSE SIN VIDA
                pantallaResultados.reproducirAudioResultado(esEmpate, nombreGanador);
            }
            
            if (!combateTerminado) {
                if (jugador1 != null) {
                    jugador1.procesarEntrada(jugador2);
                    jugador1.actualizarFisicas(delta, suelo_Y);
                }
                if (jugador2 != null) {
                    jugador2.procesarEntrada(jugador1);
                    jugador2.actualizarFisicas(delta, suelo_Y);
                }

                // COLISIONES
                if (jugador1 != null && jugador2 != null) {
                    Rectangle hitbox1 = jugador1.getHitBox();
                    Rectangle hitbox2 = jugador2.getHitBox();

                    if (hitbox1.overlaps(hitbox2)) {
                        float centro1 = jugador1.getX() + (hitbox1.width / 2);
                        float centro2 = jugador2.getX() + (hitbox1.width / 2);
                        Rectangle interseccion = new Rectangle();
                        Intersector.intersectRectangles(hitbox1, hitbox2, interseccion);

                        if (centro1 < centro2) {
                            jugador1.setX(jugador1.getX() - (interseccion.width / 2f));
                            jugador2.setX(jugador2.getX() + (interseccion.width / 2f));
                        } else {
                            jugador1.setX(jugador1.getX() + (interseccion.width / 2f));
                            jugador2.setX(jugador2.getX() - (interseccion.width / 2f));
                        }
                    }
                    if (jugador1.getEstado() == Personaje.Estado.atacar && !jugador1.getGolpeConectado()) {
                        if (jugador1.getHitboxAtaque().overlaps(jugador2.getHitBox())) {
                            boolean atacanteIzquierda = jugador1.getX() < jugador2.getX();
                            jugador2.recibirDaño(7.5f, atacanteIzquierda);
                            jugador1.setGolpeConectado(true);
                        }
                    }
                    if (jugador2.getEstado() == Personaje.Estado.atacar && !jugador2.getGolpeConectado()) {
                        if (jugador2.getHitboxAtaque().overlaps(jugador1.getHitBox())) {
                            boolean atacanteIzquierda = jugador2.getX() < jugador1.getX();
                            jugador1.recibirDaño(7.5f, atacanteIzquierda);
                            jugador2.setGolpeConectado(true);
                        }
                    }
                }
                if (jugador1 != null) vidaActualJ1 = jugador1.getVida();
                if (jugador2 != null) vidaActualJ2 = jugador2.getVida();
            }

            // SINCRONIZAR BARRAS DE VIDA GRÁFICAMENTE
            float anchoInterno = anchoBarra - 34f;

            float porcentajeJ1 = vidaActualJ1 / vidaMax;
            if (porcentajeJ1 < 0) porcentajeJ1 = 0;
            vidaJ1.setWidth(anchoInterno * porcentajeJ1);

            float porcentajeJ2 = vidaActualJ2 / vidaMax;
            if (porcentajeJ2 < 0) porcentajeJ2 = 0;
            float nuevoAnchoJ2 = anchoInterno * porcentajeJ2;
            vidaJ2.setWidth(nuevoAnchoJ2);
            vidaJ2.setX((posXJ2 + 10f) + (anchoInterno - nuevoAnchoJ2));

            // RENDERIZADO VISUAL DEL COMBATE
            batch.setColor(Color.WHITE);
            batch.begin();
            batch.draw(mapaCombate, 0, 0, stage.getWidth(), stage.getHeight());

            if (jugador1 != null) jugador1.renderizarPelea(batch, delta);
            if (jugador2 != null) jugador2.renderizarPelea(batch, delta);
            batch.end();
        }

        // 5. INTERFAZ GENERAL DE LA PELEA (HUD) - Solo si el combate sigue activo
        if (!combateTerminado) {
            stage.act(delta);
            stage.draw();
            descripcionJugador.render(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stagePausa.getViewport().update(width, height, true);
        descripcionJugador.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        if (musicaFondo != null) {
            musicaFondo.stop();
        }
    }

    @Override
    public void dispose() {
        if (descripcionJugador != null) descripcionJugador.dispose();
        if (transicion != null) transicion.dispose();
        if (stage != null)stage.dispose();
        if (stagePausa != null) stagePausa.dispose();
        if (texturaFondoOscuro != null) texturaFondoOscuro.dispose();
        if (mapaCombate != null) mapaCombate.dispose();
        if (jugador1 != null) jugador1.dispose();
        if (jugador2 != null) jugador2.dispose();
        if (fuenteCronometro != null) fuenteCronometro.dispose();
        if (texturaPixel != null) texturaPixel.dispose();
        if (texturaBarra != null) texturaBarra.dispose();
        if (musicaFondo != null) {
            musicaFondo.stop();
            musicaFondo.dispose();
        }
    }
}
