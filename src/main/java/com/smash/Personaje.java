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
import com.badlogic.gdx.controllers.Controller;

public abstract class Personaje {
    protected Controller mando;
    public void setMando(Controller mando) {
        this.mando = mando;
    }
    protected String nombre;
    protected Texture icono;
    protected Texture render2d;
    protected float menuX, menuY, anchoIcono, altoIcono;
    protected Sound seleccion;

    public enum Estado {
        idle, caminar, saltar, agachar
    }
    protected Estado estadoActual = Estado.idle;

    protected Animation<TextureRegion> poseIdle;
    protected Animation<TextureRegion> poseCaminar;
    protected Animation<TextureRegion> poseSaltar;
    protected Animation<TextureRegion> poseAgachado;
    protected Animation<TextureRegion> poseAtacar;

    protected float stateTime = 0f;
    
    protected Array<Texture> texturasCombate = new Array<>();

    protected float x, y;
    protected float velocidadX = 0;
    protected float velocidadY = 0;
    protected boolean enSuelo = true;
    protected boolean mirandoIzquierda = false;

    protected final float velocidad_Movimiento = 250f;
    protected final float fuerza_Salto = 500f;
    protected final float gravedad = 1200f;
    protected float altoCombate = 320;
    
    protected boolean jugador2 = false;
    
    public void setMirandoIzquierda(boolean mirandoIzquierda) {
        this.mirandoIzquierda = mirandoIzquierda;
    }
    
    public void setJugador2(boolean jugador2){
        this.jugador2 = jugador2;
    }

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
    
    public void procesarEntrada (){
        if (mando != null) {
            procesarEntradaMando();
        } else {
            procesarEntradaTeclado();
        }
    }
    
    private void procesarEntradaMando() {
        // En mandos de PS/Xbox, el Axis 0 corresponde al D-Pad horizontal o Stick analógico izquierdo
        float ejeX = mando.getAxis(0); 

        // 1. MOVIMIENTO HORIZONTAL (Stick analógico o Cruceta)
        if (ejeX < -0.3f || mando.getButton(mando.getMapping().buttonDpadLeft)) {
            velocidadX = -velocidad_Movimiento;
            mirandoIzquierda = true;
            if (enSuelo) setEstado(Estado.caminar);
        } else if (ejeX > 0.3f || mando.getButton(mando.getMapping().buttonDpadRight)) {
            velocidadX = velocidad_Movimiento;
            mirandoIzquierda = false;
            if (enSuelo) setEstado(Estado.caminar);
        } else {
            velocidadX = 0;
            if (enSuelo && estadoActual != Estado.agachar) {
                setEstado(Estado.idle);
            }
        }

        // 2. AGACHARSE (Cruceta Abajo o Stick hacia abajo)
        float ejeY = mando.getAxis(1);
        boolean presionaAbajo = ejeY > 0.4f || mando.getButton(mando.getMapping().buttonDpadDown);
        if (presionaAbajo) {
            if (enSuelo) {
                setEstado(Estado.agachar);
                velocidadX = 0;
            }
        } else {
            if (estadoActual == Estado.agachar && enSuelo) {
                setEstado(Estado.idle);
            }
        }

        // 3. SALTAR (Botón Equis 'X' / 'A' en mapping estándar)
        if (mando.getButton(mando.getMapping().buttonA) && enSuelo) {
            velocidadY = fuerza_Salto;
            enSuelo = false;
            setEstado(Estado.saltar);
        }
    }
    
    private void procesarEntradaTeclado() {
        if (!jugador2) {
            // --- JUGADOR 1: WASD ---
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                velocidadX = -velocidad_Movimiento;
                mirandoIzquierda = true;
                if (enSuelo) {
                    setEstado(Estado.caminar);
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                velocidadX = velocidad_Movimiento;
                mirandoIzquierda = false;
                if (enSuelo) {
                    setEstado(Estado.caminar);
                }
            } else {
                velocidadX = 0;
                if (enSuelo && estadoActual != Estado.agachar) {
                    setEstado(Estado.idle);
                }
            }

            // AGACHARSE (S)
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                if (enSuelo) {
                    setEstado(Estado.agachar);
                    velocidadX = 0;
                }
            } else {
                if (estadoActual == Estado.agachar && enSuelo) {
                    setEstado(Estado.idle);
                }
            }

            // SALTO (W)
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) && enSuelo) {
                velocidadY = fuerza_Salto;
                enSuelo = false;
                setEstado(Estado.saltar);
            }

        } else {
            // --- JUGADOR 2: FLECHAS ---
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velocidadX = -velocidad_Movimiento;
                mirandoIzquierda = true;
                if (enSuelo) {
                    setEstado(Estado.caminar);
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velocidadX = velocidad_Movimiento;
                mirandoIzquierda = false;
                if (enSuelo) {
                    setEstado(Estado.caminar);
                }
            } else {
                velocidadX = 0;
                if (enSuelo && estadoActual != Estado.agachar) {
                    setEstado(Estado.idle);
                }
            }

            // AGACHARSE (FLECHA ABAJO)
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (enSuelo) {
                    setEstado(Estado.agachar);
                    velocidadX = 0;
                }
            } else {
                if (estadoActual == Estado.agachar && enSuelo) {
                    setEstado(Estado.idle);
                }
            }

            // SALTO (FLECHA ARRIBA)
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && enSuelo) {
                velocidadY = fuerza_Salto;
                enSuelo = false;
                setEstado(Estado.saltar);
            }
        }
    }
    
    public void actualizarFisicas(float delta, float sueloY) {
        if (!enSuelo) {
            velocidadY -= gravedad * delta;
            setEstado(Estado.saltar);
        }

        x += velocidadX * delta;
        y += velocidadY * delta;

        // Limite con el suelo del escenario
        if (y <= sueloY) {
            y = sueloY;
            velocidadY = 0;
            enSuelo = true;
            if (velocidadX == 0 && estadoActual == Estado.saltar) {
                setEstado(Estado.idle);
            }
        }

        // Limites laterales de pantalla
        if (x < 0) {
            x = 0;
        }
        
        // Obtener el ancho de la textura a partir de la animación idle
        float anchoSprite = 80f;
        if (poseIdle != null) {
            TextureRegion frameIdle = poseIdle.getKeyFrame(0, true);
            if (frameIdle != null) {
                float ratio = (float) frameIdle.getRegionWidth() / frameIdle.getRegionHeight();
                anchoSprite = altoCombate * ratio;
            }
        }

        float anchoPantallaVirtual = 1920f;
        float limiteDerecho = anchoPantallaVirtual - anchoSprite;

        if (x > limiteDerecho) {
            x = limiteDerecho;
        }
    }
    public void renderizarPelea(SpriteBatch batch, float delta) {
        stateTime += delta; // Incrementa el contador de la animación activa
        TextureRegion poseActiva = null;
        float altoReal = altoCombate;
        
        switch (estadoActual) {
            case caminar:
                if (poseCaminar != null) poseActiva = poseCaminar.getKeyFrame(stateTime, true);
                break;

            case saltar:
                // NormalMente la animación de salto no hace loop (false) para quedarse en el último cuadro
                if (poseSaltar != null) poseActiva = poseSaltar.getKeyFrame(stateTime, false);
                altoReal = altoCombate * 0.75f;
                break;

            case agachar:
                if (poseAgachado != null) poseActiva = poseAgachado.getKeyFrame(stateTime, false);
                altoReal = altoCombate * 0.65f;               
                break;
            case idle:
            default:
                if (poseIdle != null) poseActiva = poseIdle.getKeyFrame(stateTime, true);
                break;
        }

        // Resguardo si falta alguna animación
        if (poseActiva == null && poseIdle != null) {
            poseActiva = poseIdle.getKeyFrame(stateTime, true);
        }

        if (poseActiva == null) return;

        float alto = altoReal;
        float ratio = (float) poseActiva.getRegionWidth() / poseActiva.getRegionHeight();
        float ancho = alto * ratio;

        if (mirandoIzquierda) {
            batch.draw(poseActiva, x + ancho, y, -ancho, alto);
        } else {
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
        if (this.estadoActual != nuevoEstado) {
            this.estadoActual = nuevoEstado;
            this.stateTime = 0f;
        }
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
