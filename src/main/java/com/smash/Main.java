//PAQUETE COMUN Y CORRIENTE QUE SIEMPRE HEMOS USADO
package com.smash;

//CLASE BASE PARA MANEJAR EL CICLO DE VIDA DE LA APLICACIÓN
import com.badlogic.gdx.Game;
//CARGA LAS IMAGENES EN LA GPU
import com.badlogic.gdx.graphics.Texture;
//SE ENCARGA DE DIBUJAR LAS TEXTURAS 2D AGUPRANDOLAS EN LA PANTALLA
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//LIMPIA O RELLENA EL FONDO DE LA PANTALLA
import com.badlogic.gdx.utils.ScreenUtils;
//MANDO CENTRAL DE LAS OPERACIONES (DIME PANTALLA, ENTRADAS, ARCHIVOS, AUDIO, CICLO APP)
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
//CLASE BASE PARA ELEMENTOS DE LA INTERFAZ
import com.badlogic.gdx.scenes.scene2d.Actor;
//CONTENEDOR PRINCIPAL DE BOTONES Y UI
import com.badlogic.gdx.scenes.scene2d.Stage;
//ORGANIZA LOS ELEMENTOS EN PANTALLA
import com.badlogic.gdx.scenes.scene2d.ui.Table;
//ESCUCHA EL CLIC DEL USUARIO
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
//ADAPTA LA UI A LA VENTANA
import com.badlogic.gdx.utils.viewport.FillViewport;
//-----------------------------------------------
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
//------------------------------
import com.badlogic.gdx.graphics.Pixmap;


public class Main extends Game {
    //TIPOS DE DATOS
    // MODI ACCESO - TIPO DATO - NOMBRE VARIABLE
    //OPTIMIZACIÓN Y COORDINACIÓN DE GRÁFICOS 2d
    //TOMA LAS IMAGANES PARA REDIRECCIONARLAS A LA PANTALLA
    private SpriteBatch batch;
    //TEXTURE INDICA UNA IMAGEN EN 2D CARGADA CON LA GPU
    private Texture image;
    private Stage stage;
    //VARIABLES PARA EL BOTÓN
    private Texture botonNormal;
    private Texture botonMouse;
    //VARIABLES PARA TRANSICIONAR
    private Texture transicionTexture;
    //OPACIDAD DE LA TRANSICIÓN
    private float alfaTransicion = 0f;
    //CUANDO DEBERÁ OSCURECERSE
    private boolean realizandoTransicion = false;

    @Override
    public void create() {
        //REALIZAMOS LA INSTANCIA DE OBJETOS INDISPENSABLES UNO
        //PARA QUE ENVÍE LA IMAGEN Y EL OTRO LA MISMA IMAGEN
        //ENVIA LAS IMAGANES A LA GPU
        batch = new SpriteBatch();
        //IMAGEN DE FONDO
        image = new Texture("FondoAdicional.png");
        //CONTROL DE CLICS Y TECLADO
        //PREPARA AL ESCENARIO PARA RECIBIR BOTONES
        stage = new Stage(new FillViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);
        
        //CARGAR IMAGENES DE BOTÓN
        botonNormal = new Texture("Comenzar-Normal.png");
        botonMouse = new Texture("Comenzar-Mouse.png");
        //CREA LOS DRAWABLES
        //------------------------------------
        TextureRegionDrawable drawableNormal = new TextureRegionDrawable(new TextureRegion(botonNormal));
        TextureRegionDrawable drawableMouse = new TextureRegionDrawable(new TextureRegion(botonMouse));
        //CREAR EL ESTILO DEL BOTON USANDO LAS IMAGENES EN LUGAR DE TEXTO
        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.imageUp = drawableNormal; //ESTADO NORMAL
        buttonStyle.imageOver = drawableMouse; //ESTADO CUANDO PASA EL MOUSE
        //INSTANCIAMOS EL IMAGE BUTTON
        ImageButton botonComenzar = new ImageButton(buttonStyle);
        
        //CREAMOS LA TEXTURA
        //DIBUJA UNA IMAGEN DIRECTAMENTE DE LA RAM
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        transicionTexture = new Texture(pixmap);
        pixmap.dispose();
        
        //ASIGNAMOS EL LISTENER PARA QUE SE DETECTE EL CLIC DEL USUARIO Y SE CAMBIE LA PANTALLA
        botonComenzar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //ENCENDEMOS EL INTERRUPTOR DE LA TRANSICIÓN
                realizandoTransicion = true;
                //DESACTIVAMOS EL BOTÓN PARA EVITAR MULTIPLES CLICS
                botonComenzar.setDisabled(true);
            }
        });
        //POSICIONAMOS EL BOTON EN LA PARTE INFERIOR
        Table table = new Table();
        table.setFillParent(true);
        //ALINEACIÓN DEL BOTÓN
        table.bottom().padBottom(0);
        table.add(botonComenzar).width(300).height(250);
        //----------------------
        stage.addActor(table);
    }

    @Override
    public void render() {
        //SE ENCARGA DE DIFERENCIAR ENTRE LA PANTALLA PRINCIPAL Y LA NUEVA
        if (getScreen() == null) {
            //LIMPIA EL FOTOGRAMA ANTERIOR Y SE ENFOCA EN RGBA
            ScreenUtils.clear(0.10f, 0.10f, 0.10f, 1f);
            //PREPARAMOS EL ENVIO DE LA IMAGEN
            batch.begin();
            //AGREGA LA IMAGEN Y SU POSICION EN "X" Y "Y"
            batch.draw(image, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            //INDICAR QUE YA TERMINÓ EL PASO DE IMAGEN
            batch.end();
            //DETECTA SI EL PUNTERO DEL MOUSE ESTÁ ENCIMA DEL BOTÓN
            stage.act(Gdx.graphics.getDeltaTime());
            //PONE EL BOTÓN ENCIMA DE LA IMAGEN DEL FONDO
            stage.draw();
            
            if (realizandoTransicion) {
                //CONTROLA LA VELOCIDAD DE LA TRANSICIÓN
                //TIEMPO ENTRE UN FOTOGRAMA Y OTRO
                alfaTransicion += Gdx.graphics.getDeltaTime() * 1.0f;
                batch.begin();
                //QUE LA TRANSICIÓN SEA GRADUAL, ADEMÁS DE ASEGURAR QUE NO PASE DE 1
                batch.setColor(1, 1, 1, Math.min(alfaTransicion, 1.0f));
                batch.draw(transicionTexture, 0, 0, stage.getWidth(), stage.getHeight());
                //LIMPIAMOS EL COLOR PARA NO AFECTAR SIGUEINTES FOTOGRAMAS
                batch.setColor(1, 1, 1, 1);
                batch.end();
                //AL MOMENTO QUE YA NO SE VEA LA PANTALLA SEGUIRÁ CON LA NUEVA PANTALLA
                if (alfaTransicion >= 1.0f) {
                    setScreen(new NuevaPantalla(Main.this));
                }
            }
        } else {
            //PASAMOS A LA NUEVA PANTALLA
            //NO SERÁ NULO YA QUE PRESIONÓ EL BOTON EL USUARIO
            super.render();
        }
    }

    @Override
    //SE ENCARGA DE REDIMENSIONAR LA PANTALLA AL MOMENTO QUE EL USUARIO AGRANDE
    public void resize (int width, int height){
        //DEFINE AREA DE RENDERIZADO Y CUAL ES EL NUEVO TAMAÑO
        Gdx.gl.glViewport(0, 0, width, height);
        //RECONFIGURA LA MATRIZ 2D INTERNA
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        
        //REAJUSTAMOS LA VENTANA PARA LA INTERFAZ Y LAS PANTALLAS
        //RECALCULA LA POSICIÓN DEL BOTÓN
        stage.getViewport().update(width, height, true);
        //NOTIFICA EL CAMBIO DE PANTALLA
        if (getScreen() != null) {
            getScreen().resize(width, height);
        }
    }
    
    public SpriteBatch getBatch(){
        //PERMITE QUE ACCEDA AL OBJETO SIN ROMPER LA VISIBILIDAD
        return batch;
    }
    
    
    @Override
    public void dispose() {
        //LIMIPIAN RAM Y GPU
        batch.dispose();
        image.dispose();
        stage.dispose();
        botonNormal.dispose();
        botonMouse.dispose();
        //LIMPIEZA DE MEMORIA
        if (transicionTexture != null) transicionTexture.dispose();
        //SI NO SE ESCRIBEN ESTAS INSTRUCCIONES HABRÁ UNA FUGA DE MEMORIA
    }
}
