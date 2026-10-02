package com.smash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public abstract class Personaje {
    protected String nombre;
    protected Texture icono;
    protected Texture render2d;
    protected float menuX, menuY, anchoIcono, altoIcono;
    protected Sound seleccion;

    public enum Estado {
        idle, caminar, saltar, agachar
    }
    protected Estado estadoActual = Estado.idle;

    protected TextureRegion poseIdle;
    protected Animation<TextureRegion> poseCaminar;
    protected TextureRegion poseSaltar;
    protected TextureRegion poseAgachado;
    protected TextureRegion poseAtacar;

    protected Array<Texture> texturasCombate = new Array<>();

    protected float x, y;
    protected float velocidadX = 0;
    protected float velocidadY = 0;
    protected boolean enSuelo = true;
    protected boolean mirandoIzquierda = false;

    protected final float velocidad_Movimiento = 250f;
    protected final float fuerza_Salto = 500f;
    protected final float gravedad = 1200f;

    public Personaje(String nombre, String icono, String render2d, float menuX, float menuY, String seleccion) {
        this.nombre = nombre;
        this.icono = new Texture(icono);
        this.render2d = new Texture(render2d);
        this.menuX = menuX;
        this.menuY = menuY;
        this.anchoIcono = 130;
        this.altoIcono = 130;
        if (seleccion != null && !seleccion.isEmpty()) {
            this.seleccion = Gdx.audio.newSound(Gdx.files.internal("SonidosNombres/" + seleccion));
        }
    }
    
    public abstract void cargarPosesCombate();
    
    public void procesarEntrada() {
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (enSuelo) {
                setEstado(Estado.agachar);
                velocidadX = 0;
                return; 
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocidadX = -velocidad_Movimiento;
            mirandoIzquierda = true;
            if (enSuelo) {
                setEstado(Estado.caminar);
            }
        } 
        else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocidadX = velocidad_Movimiento;
            mirandoIzquierda = false;
            if (enSuelo) {
                setEstado(Estado.caminar);
            }
        }
        else {
            velocidadX = 0;
            if (enSuelo) {
                setEstado(Estado.idle);
            }
        }
        if ((Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) && enSuelo) {
            velocidadY = fuerza_Salto;
            enSuelo = false;
            setEstado(Estado.saltar);
        }
    }
    public void actualizarFisicas(float delta, float sueloY) {
        if (!enSuelo) {
            velocidadY -= gravedad * delta;
            setEstado(Estado.saltar);
        }
        x += velocidadX * delta;
        y += velocidadY * delta;
        if (y <= sueloY) {
            y = sueloY;
            velocidadY = 0;
            enSuelo = true;
            if (velocidadX == 0 && estadoActual == Estado.saltar) {
                setEstado(Estado.idle);
            }
        }
        if (x < 0) {
            x = 0;
        }
        float limiteDerecho = Gdx.graphics.getWidth() - ((poseIdle != null) ? poseIdle.getRegionWidth() : 50);
        if (x > limiteDerecho) {
            x = limiteDerecho;
        }
    }
    public void renderizarPelea(SpriteBatch batch) {
        TextureRegion poseActiva;
        switch (estadoActual) {
            case caminar:
                poseActiva = (poseCaminar != null) ? poseCaminar : poseIdle;
                break;
            case saltar:
                poseActiva = (poseSaltar != null) ? poseSaltar : poseIdle;
                break;
            case agachar:
                poseActiva = (poseAgachado != null) ? poseAgachado : poseIdle;
                break;
            case idle:
            default:
                poseActiva = poseIdle;
                break;
        }
        if (poseActiva == null) {
            return;
        }
        // DIBUJADO LIMPIO SIN Afectar la textura original
        float ancho = poseActiva.getRegionWidth();
        float alto = poseActiva.getRegionHeight();
        if (mirandoIzquierda) {
            // Dibuja volteado en X invirtiendo la escala de origen
            batch.draw(poseActiva, x + ancho, y, -ancho, alto);
        } else {
            // Dibuja en orientación normal
            batch.draw(poseActiva, x, y, ancho, alto);
        }
    }
    
    public void reproducirSeleccion() {
        if (seleccion != null) {
            seleccion.play(1.0f);
        }
    }

    public ImageButton crearBotonMenu() {
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(icono));
        ImageButton boton = new ImageButton(drawable);
        boton.setBounds(menuX, menuY, anchoIcono, altoIcono);
        return boton;
    }

    public void renderizarModelo(SpriteBatch batch, float x, float y, float alto) {
        if (render2d != null) {
            float ratio = (float) render2d.getWidth() / render2d.getHeight();
            float ancho = alto * ratio;
            batch.draw(render2d, x, y, ancho, alto);
        }
    }

    public Texture getRender2d() {
        return render2d;
    }

    public void setEstado(Estado nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }

    public Estado getEstado() {
        return this.estadoActual;
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    
    public void dispose() {
        if (icono != null) {
            icono.dispose();
        }
        if (render2d != null) {
            render2d.dispose();
        }
        if (seleccion != null) {
            seleccion.dispose();
        }
        for (Texture t : texturasCombate){
            if (t != null) t.dispose();
        }
    }
}
