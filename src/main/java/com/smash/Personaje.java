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
import com.badlogic.gdx.math.Rectangle;

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
        idle, caminar, saltar, agachar, atacar, herido
    }
    protected Estado estadoActual = Estado.idle;

    protected Animation<TextureRegion> poseIdle;
    protected Animation<TextureRegion> poseCaminar;
    protected Animation<TextureRegion> poseSaltar;
    protected Animation<TextureRegion> poseAgachado;
    protected Animation<TextureRegion> poseAtacar1;
    protected Animation<TextureRegion> poseAtacar2;
    protected Animation<TextureRegion> poseAtacar3;
    protected Animation<TextureRegion> poseHerido;

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
    protected float altoCombate = 650;
    
    protected boolean jugador2 = false;
    protected boolean botonAnterior = false;
    
    //CONTROL DE ATAQUE
    protected int combo = 0;
    protected float temporizadorCombo = 0f;
    protected final float tiempoMax = 0.6f;
    
    //CONTROL DE DAÑO Y GOLPES
    protected float vida = 100f;
    protected boolean golpeConectado = false;
    
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
    
    public void procesarEntrada (Personaje oponente){
        if (mando != null) {
            procesarEntradaMando(oponente);
        } else {
            procesarEntradaTeclado(oponente);
        }
    }
    
    private void procesarEntradaMando(Personaje oponente) {
        if (estadoActual == Estado.herido) {
            velocidadX = 0;
            return;
        }
        // En mandos de PS/Xbox, el Axis 0 corresponde al D-Pad horizontal o Stick analógico izquierdo
        float ejeX = mando.getAxis(0); 

        // 1. MOVIMIENTO HORIZONTAL (Stick analógico o Cruceta)
        if (ejeX < -0.3f || mando.getButton(mando.getMapping().buttonDpadLeft)) {
            velocidadX = -velocidad_Movimiento;
            mirandoIzquierda = true;
            if (enSuelo && estadoActual != Estado.atacar) setEstado(Estado.caminar);
        } else if (ejeX > 0.3f || mando.getButton(mando.getMapping().buttonDpadRight)) {
            velocidadX = velocidad_Movimiento;
            mirandoIzquierda = false;
            if (enSuelo && estadoActual != Estado.atacar) setEstado(Estado.caminar);
        } else {
            velocidadX = 0;
            if (enSuelo && estadoActual != Estado.agachar && estadoActual != Estado.atacar) {
                setEstado(Estado.idle);
            }
        }

        // 2. AGACHARSE (Cruceta Abajo o Stick hacia abajo)
        float ejeY = mando.getAxis(1);
        boolean presionaAbajo = ejeY > 0.4f || mando.getButton(mando.getMapping().buttonDpadDown);
        if (presionaAbajo) {
            if (enSuelo && estadoActual != Estado.atacar) {
                setEstado(Estado.agachar);
                velocidadX = 0;
            }
        } else {
            if (estadoActual == Estado.agachar && enSuelo && estadoActual != Estado.atacar) {
                setEstado(Estado.idle);
            }
        }

        // 3. SALTAR (Botón Equis 'X' / 'A' en mapping estándar)
        if (mando.getButton(mando.getMapping().buttonA) && enSuelo && estadoActual != Estado.atacar) {
            velocidadY = fuerza_Salto;
            enSuelo = false;
            setEstado(Estado.saltar);
        }
        //ATAQUE
        boolean botonAhora = mando.getButton(mando.getMapping().buttonX);
        if (botonAhora && !botonAnterior && enSuelo) {
            if (estadoActual != Estado.atacar) {
                if (oponente != null) {
                    this.mirandoIzquierda = (this.x > oponente.getX());
                }
                combo = 1;
                setEstado(Estado.atacar);
                velocidadX = 0;
                temporizadorCombo = tiempoMax;
                golpeConectado = false;
            } else if (estadoActual == Estado.atacar && combo < 3) {
                if (stateTime > 0.12f) {
                    combo++;
                    stateTime = 0f;
                    temporizadorCombo = tiempoMax;
                    golpeConectado = false;
                }
            }
        }
        botonAnterior = botonAhora;
    }
    
    private void procesarEntradaTeclado(Personaje oponente) {
        if (estadoActual == Estado.herido) {
            velocidadX = 0;
            return;
        }
        if (!jugador2) {
            // --- JUGADOR 1: WASD ---
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                velocidadX = -velocidad_Movimiento;
                mirandoIzquierda = true;
                if (enSuelo && estadoActual != Estado.atacar) {
                    setEstado(Estado.caminar);
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                velocidadX = velocidad_Movimiento;
                mirandoIzquierda = false;
                if (enSuelo && estadoActual != Estado.atacar) {
                    setEstado(Estado.caminar);
                }
            } else {
                velocidadX = 0;
                if (enSuelo && estadoActual != Estado.agachar && estadoActual != Estado.atacar) {
                    setEstado(Estado.idle);
                }
            }
            // AGACHARSE (S)
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                if (enSuelo && estadoActual != Estado.atacar) {
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
            //ATAQUE
            if (Gdx.input.isKeyJustPressed(Input.Keys.F) && enSuelo) {
                if (estadoActual != Estado.atacar) {
                    if (oponente != null) {
                        this.mirandoIzquierda = (this.x > oponente.getX());
                    }
                    combo = 1;
                    setEstado(Estado.atacar);
                    velocidadX = 0;
                    temporizadorCombo = tiempoMax;
                    golpeConectado = false;
                } else if (estadoActual == Estado.atacar && combo < 3) {
                    if (stateTime > 0.12f) {
                        combo++;
                        stateTime = 0f;
                        temporizadorCombo = tiempoMax;
                        golpeConectado = false;
                    }
                }
            }
            
        } else {
            // --- JUGADOR 2: FLECHAS ---
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velocidadX = -velocidad_Movimiento;
                mirandoIzquierda = true;
                if (enSuelo && estadoActual != Estado.atacar) {
                    setEstado(Estado.caminar);
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velocidadX = velocidad_Movimiento;
                mirandoIzquierda = false;
                if (enSuelo && estadoActual != Estado.atacar) {
                    setEstado(Estado.caminar);
                }
            } else {
                velocidadX = 0;
                if (enSuelo && estadoActual != Estado.agachar && estadoActual != Estado.atacar) {
                    setEstado(Estado.idle);
                }
            }
            // AGACHARSE (FLECHA ABAJO)
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (enSuelo && estadoActual != Estado.atacar) {
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
            //ATAQUE CON K
            if (Gdx.input.isKeyJustPressed(Input.Keys.K) && enSuelo) {
                if (estadoActual != Estado.atacar) {
                    if (oponente != null) {
                        this.mirandoIzquierda = (this.x > oponente.getX());
                    }
                    combo = 1;
                    setEstado(Estado.atacar);
                    velocidadX = 0;
                    temporizadorCombo = tiempoMax;
                    golpeConectado = false;
                } else if (estadoActual == Estado.atacar && combo < 3) {
                    if (stateTime > 0.12f) {
                        combo++;
                        stateTime = 0f;
                        temporizadorCombo = tiempoMax;
                        golpeConectado = false;
                    }
                }
            }
        }
    }
    
    
    //-------------------MRU------------------------------------
    //MOVIMIENTO RECTILÍNEO UNIFORME
    //-------------------MRUv-----------------------------------
    //MOVIMIENTO RECTILÍNEO UNIFORMEMENTE VARIADO
    public void actualizarFisicas(float delta, float sueloY) {
    // ---------------------------------------------------------------------
    // 1. MRUV (Movimiento Rectilíneo Uniforme Variado) - EJE VERTICAL (Y)
    //----------------------------------------------------------------------
    // PRINCIPIO FÍSICO: La gravedad actúa como una aceleración constante (g).
    // FÓRMULA DE LA VELOCIDAD: vf = v0 + a * t
    // EN CÓDIGO: Delta representa el incremento del tiempo.
    // Mientras el personaje esté en el aire (!enSuelo), su velocidad vertical no es estática,
    // sino que disminuye al subir y aumenta al caer debido a esta aceleración constante.
        if (!enSuelo) {
            velocidadY -= gravedad * delta;
            setEstado(Estado.saltar);
        }
        //SE ACTUALIZAN LAS POSICIONES SUMANDO LA VELOCIDAD MULTIPLICADA POR EL TIEMPO
        // D = V * T
        // La velocidad horizontal (velocidadX) es constante mientras se camina, 
        // generando un desplazamiento lineal y uniforme en la pantalla.
        x += velocidadX * delta; // -> POSICIÓN HORIZONTAL QUE CAMBIA LINEAL Y UNIFORMEMENTE
        
        // La posición en 'y' se actualiza utilizando una velocidad que ya fue modificada por la gravedad,
        // lo que dibuja matemáticamente la trayectoria parabólica del salto.
        y += velocidadY * delta; // -> MRUV: LA POSICIÓN CAMBIA DEPENDIENDO LA ACELERACIÓN

        // LÍMITE CON EL SUELO
        //DETECTA LA COLISIÓN CONTRA EL PISO, DE ESA MANERA LA ANULA
        if (y <= sueloY) {
            y = sueloY;
            velocidadY = 0;
            enSuelo = true;
            if (velocidadX == 0 && estadoActual == Estado.saltar) {
                setEstado(Estado.idle);
            }
        }

        // LÍMITES LATERALES
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
        //DECREMENTO TEMPORIZADOR COMBO
        if (combo > 0 && estadoActual != Estado.atacar) {
            temporizadorCombo -= delta;
            if (temporizadorCombo <= 0) {
                combo = 0;
            }
        }
    }
    //----------------------------------------------------------
    
    public Rectangle getHitBox(){
        float altoReal = altoCombate;
        if (estadoActual == Estado.saltar) {
            altoReal = altoCombate * 0.75f;
        } else if (estadoActual == Estado.agachar){
            altoReal = 0.65f;
        }
        Animation<TextureRegion> poseActual = poseIdle;
        switch (estadoActual){
            case caminar: poseActual = poseCaminar; break;
            case saltar: poseActual = poseSaltar; break;
            case agachar: poseActual = poseAgachado; break;
            case idle: default: poseActual = poseIdle; break;
        }
        
        float anchoSprite = 80f;
        if (poseActual != null) {
            TextureRegion frame = poseActual.getKeyFrame(stateTime, true);
            if (frame != null) {
                float ratio = (float) frame.getRegionWidth() / frame.getRegionHeight();
                anchoSprite = altoReal * ratio;
            }
        }
        return new Rectangle(x + 25f, y ,anchoSprite - 50f, altoReal);
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
            case atacar:
                Animation<TextureRegion> animacionActual = poseAtacar1;
                if (combo == 2) animacionActual = poseAtacar2;
                else if (combo == 3) animacionActual = poseAtacar3;
                
                if (animacionActual != null) {
                    poseActiva = animacionActual.getKeyFrame(stateTime, false);
                    if (animacionActual.isAnimationFinished(stateTime)) {
                        combo = 0;
                        setEstado(Estado.idle);
                    }
                }
                break;
            case herido:
                if (poseHerido != null) {
                    poseActiva = poseHerido.getKeyFrame(stateTime, false);
                    if (poseHerido.isAnimationFinished(stateTime)) {
                        setEstado(Estado.idle);
                    }
                } else {
                    if (stateTime > 0.25f) {
                        setEstado(Estado.idle);
                    }
                    if (poseIdle != null) poseActiva = poseIdle.getKeyFrame(stateTime, true);
                }
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

    public Rectangle getHitboxAtaque(){
        if (estadoActual != Estado.atacar) {
            return new Rectangle(0, 0, 0, 0);
        }
        Rectangle hitboxBase = getHitBox();
        float anchoAtaque = 90f;
        float altoAtaque = 60f;
        float ataqueY = y + (altoCombate * 0.5f);
        float ataqueX;
        
        if (mirandoIzquierda) {
            ataqueX = x - anchoAtaque;
        } else {
            ataqueX = x + hitboxBase.width;
        }
        return new Rectangle(ataqueX, ataqueY, anchoAtaque, altoAtaque);
    }
    
    public Texture getRender2d() {
        return render2d;
    }

    public void setEstado(Estado nuevoEstado) {
        if (this.estadoActual != nuevoEstado) {
            this.estadoActual = nuevoEstado;
            this.stateTime = 0f;
            
            if (nuevoEstado == Estado.atacar) {
                golpeConectado = false;
            }
        }
    }

    public Estado getEstado() {
        return this.estadoActual;
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX (float x){
        this.x = x;
    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    
    //--------------------------MRUV-----------------------------------
    public void recibirDaño (float cantidad, boolean atacanteIzquierda){
        //Se resta la cantidad de daño a la vida total asegurando que no baje de 0
        this.vida -= cantidad;
        if (this.vida < 0) this.vida = 0;
        //Se actualiza el estado a 'herido', de esa manera bloqueamos acciones
        setEstado(Estado.herido);
        velocidadX = 0;
        
        //Simula una fuerza externa instantánea, para transferirla
        // Se transfiere al personaje, de esa manera provocamos un desplzamiento en el eje x
        // DE ESTA MANERA TIENE UNA VELOCIDAD INICIAL DE RETROCESO
        // Podriamos tratar de averiguar, la velocidad final, la distancia que recorrerá y su aceleración
        float fuerzaRetroceso = 40f;
        if (atacanteIzquierda) {
            x += fuerzaRetroceso;
        } else {
            x -= fuerzaRetroceso;
        }
    }
    public float getVida (){
        return vida;
    }
    public void setVida (float vida){
        this.vida = vida;
    }
    public boolean getGolpeConectado(){
        return golpeConectado;
    }
    public void setGolpeConectado(boolean golpeConectado) {
        this.golpeConectado = golpeConectado;
    }
    public int getCombo (){
        return combo;
    }
    public String getNombre (){
        return this.nombre;
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
