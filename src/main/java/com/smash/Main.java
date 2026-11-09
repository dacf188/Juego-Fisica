package com.smash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.Controller;

public class Main extends Game {

    // Renderizador global para gráficos 2D en pantalla
    private SpriteBatch batch;
    // Textura para la imagen de fondo del menú principal
    private Texture image;
    // Escenario principal para gestionar la interfaz de usuario (UI)
    private Stage stage;
    // Texturas para los estados visuales del botón de comenzar
    private Texture botonNormal;
    private Texture botonMouse;
    // Textura auxiliar para generar el efecto de fundido (transición)
    private Texture transicionTexture;
    
    // Control de la opacidad y estado de la transición entre pantallas
    private float alfaTransicion = 0f;
    private boolean realizandoTransicion = false;
    // Pista de audio para la música de fondo del menú
    private Music musicafondo;
    // Botón interactivo para iniciar el flujo del juego
    private ImageButton botonComenzar;
    // Bandera de control para evitar clics múltiples o duplicados
    private boolean bloquarClic = false;

    @Override
    public void create() {
        // Inicialización de herramientas gráficas y recursos iniciales
        batch = new SpriteBatch();
        image = new Texture("PosibilidadFondo.png");
        
        // Configuración y reproducción de la música de fondo
        musicafondo = Gdx.audio.newMusic(Gdx.files.internal("Musica/CancionMenu.mp3"));
        musicafondo.setLooping(true);
        musicafondo.setVolume(0.5f);
        musicafondo.play();
        
        // Configuración del escenario y procesador de entradas
        stage = new Stage(new FillViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);
        
        // Carga de texturas de imagen para el botón de inicio
        botonNormal = new Texture("Pcomenzar.png");
        botonMouse = new Texture("PcomenzarMouse.png");
        
        // Creación de componentes gráficos legibles para el ImageButton
        TextureRegionDrawable drawableNormal = new TextureRegionDrawable(new TextureRegion(botonNormal));
        TextureRegionDrawable drawableMouse = new TextureRegionDrawable(new TextureRegion(botonMouse));
        
        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.imageUp = drawableNormal;
        buttonStyle.imageOver = drawableMouse;
        botonComenzar = new ImageButton(buttonStyle);
        
        // Generación de una textura negra de 1x1 píxel para efectos de transición
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        transicionTexture = new Texture(pixmap);
        pixmap.dispose();
        
        // Listener del botón para manejar eventos de clic del usuario
        botonComenzar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (bloquarClic) return;
                realizandoTransicion = true;
                botonComenzar.setDisabled(true);
            }
        });
        
        // Distribución y alineación del botón en la interfaz mediante Table
        Table table = new Table();
        table.setFillParent(true);
        table.bottom().padBottom(0);
        table.add(botonComenzar).width(300).height(400);
        
        stage.addActor(table);
        AdministradorMando.reiniciarEstados();
    }

    /** Restaura los valores iniciales y la música al regresar al menú principal. */
    public void volverMenu() {
        setScreen(null);
        realizandoTransicion = false;
        alfaTransicion = 0f;
        bloquarClic = true;
        
        if (botonComenzar != null) {
            botonComenzar.setDisabled(false);
            botonComenzar.setChecked(false);
        }
        Gdx.input.setInputProcessor(stage);
        
        if (musicafondo == null) {
            musicafondo = Gdx.audio.newMusic(Gdx.files.internal("Musica/CancionMenu.mp3"));
            musicafondo.setLooping(true);
            musicafondo.setVolume(0.5f);
            musicafondo.play();
        }
        AdministradorMando.reiniciarEstados();
    }
    
    @Override
    public void render() {
        // Control de flujo: si no hay pantalla activa, se renderiza el menú principal
        if (getScreen() == null) {
            ScreenUtils.clear(0.10f, 0.10f, 0.10f, 1f);
            
            if (bloquarClic) {
                bloquarClic = false;
            }
            
            // Detección de interacción por mando (Gamepad) en el menú principal
            Controller mandoJ1 = Controllers.getControllers().size > 0 ? Controllers.getControllers().get(0) : null;
            if (mandoJ1 != null && !realizandoTransicion && !bloquarClic) {
                if (AdministradorMando.esConfirmar(mandoJ1)) {
                    realizandoTransicion = true;
                    if (botonComenzar != null) {
                        botonComenzar.setDisabled(true);
                        botonComenzar.setChecked(true);
                        TextureRegionDrawable drawableMouse = new TextureRegionDrawable(new TextureRegion(botonMouse));
                        ImageButton.ImageButtonStyle estiloPresionado = new ImageButton.ImageButtonStyle();
                        estiloPresionado.imageUp = drawableMouse;
                        estiloPresionado.imageOver = drawableMouse;
                        estiloPresionado.imageDown = drawableMouse;
                        botonComenzar.setStyle(estiloPresionado);
                    }
                }
            }
            
            // Renderizado de la imagen de fondo del menú
            batch.begin();
            batch.draw(image, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
            
            // Actualización y dibujo de los elementos de la interfaz de usuario
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
            
            // Ejecución y cálculo visual del desvanecimiento (Fade-out)
            if (realizandoTransicion) {
                alfaTransicion += Gdx.graphics.getDeltaTime() * 1.0f;
                batch.begin();
                batch.setColor(1, 1, 1, Math.min(alfaTransicion, 1.0f));
                batch.draw(transicionTexture, 0, 0, stage.getWidth(), stage.getHeight());
                batch.setColor(1, 1, 1, 1);
                batch.end();
                
                // Al finalizar la transición, se libera el audio y se cambia a la pantalla de selección
                if (alfaTransicion >= 1.0f) {
                    if (musicafondo != null) {
                        musicafondo.stop();
                        musicafondo.dispose();
                        musicafondo = null;
                    }
                    realizandoTransicion = false;
                    alfaTransicion = 0f;
                    setScreen(new NuevaPantalla(Main.this));
                }
            }
        } else {
            // Delegación del ciclo de renderizado a la pantalla activa secundaria
            super.render();
        }
    }

    @Override
    /** Ajusta las dimensiones del viewport y matrices al redimensionar la ventana. */
    public void resize(int width, int height) {
        Gdx.gl.glViewport(0, 0, width, height);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        stage.getViewport().update(width, height, true);
        
        if (getScreen() != null) {
            getScreen().resize(width, height);
        }
    }
    
    /** Retorna la instancia global del SpriteBatch para su uso en otras pantallas. */
    public SpriteBatch getBatch() {
        return batch;
    }
    
    @Override
    /** Libera todos los recursos cargados en memoria y GPU al cerrar la aplicación. */
    public void dispose() {
        batch.dispose();
        image.dispose();
        stage.dispose();
        botonNormal.dispose();
        botonMouse.dispose();
        
        if (transicionTexture != null) transicionTexture.dispose();
        
        if (musicafondo != null) {
            musicafondo.dispose();
            musicafondo = null;
        }
    }
}