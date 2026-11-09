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

    // ==========================================
    // ATRIBUTOS Y VARIABLES
    // ==========================================
    
    protected String nombre;
    protected Texture icono;
    protected Texture render2d;
    protected float menuX, menuY, anchoIcono, altoIcono;
    protected Sound seleccion;

    public enum Estado {
        idle, caminar, saltar, agachar, atacar, herido, bloquear, muerto, ataqueEspecial
    }
    protected Estado estadoActual = Estado.idle;

    // Animaciones de combate
    protected Animation<TextureRegion> poseIdle;
    protected Animation<TextureRegion> poseCaminar;
    protected Animation<TextureRegion> poseSaltar;
    protected Animation<TextureRegion> poseAgachado;
    protected Animation<TextureRegion> poseAtacar1;
    protected Animation<TextureRegion> poseAtacar2;
    protected Animation<TextureRegion> poseAtacar3;
    protected Animation<TextureRegion> poseHerido;
    protected Animation<TextureRegion> poseBloquear;
    protected Animation<TextureRegion> poseMuerto;
    protected Animation<TextureRegion> poseAtaqueEspecial;
    
    protected float dañoEspecial = 10f;
    protected float stateTime = 0f;
    protected Array<Texture> texturasCombate = new Array<>();

    // Posición y físicas
    protected float x, y;
    protected float velocidadX = 0;
    protected float velocidadY = 0;
    protected boolean enSuelo = true;
    protected boolean mirandoIzquierda = false;

    protected final float velocidad_Movimiento = 250f;
    protected final float fuerza_Salto = 500f;
    protected final float gravedad = 1200f;
    protected float altoCombate = 650;

    // Controladores y jugadores
    protected Controller mando;
    protected boolean jugador2 = false;
    protected boolean botonAnterior = false;
    protected boolean botonEspecialAnterior = false;
    
    // Combos y salud
    protected int combo = 0;
    protected float temporizadorCombo = 0f;
    protected final float tiempoMax = 0.6f;
    protected float vida = 100f;
    protected boolean golpeConectado = false;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    
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

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    
    public void setMando(Controller mando) { this.mando = mando; }
    public void setMirandoIzquierda(boolean mirandoIzquierda) { this.mirandoIzquierda = mirandoIzquierda; }
    public void setJugador2(boolean jugador2) { this.jugador2 = jugador2; }
    public Texture getRender2d() { return render2d; }

    public void setEstado(Estado nuevoEstado) {
        if (this.estadoActual != nuevoEstado) {
            this.estadoActual = nuevoEstado;
            this.stateTime = 0f;
            if (nuevoEstado == Estado.atacar || nuevoEstado == Estado.ataqueEspecial) {
                golpeConectado = false;
            }
        }
    }

    public Estado getEstado() { return this.estadoActual; }
    public void setPosicion(float x, float y) { this.x = x; this.y = y; }
    public void setX(float x) { this.x = x; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getVida() { return vida; }
    public void setVida(float vida) { this.vida = vida; }
    public boolean getGolpeConectado() { return golpeConectado; }
    public void setGolpeConectado(boolean golpeConectado) { this.golpeConectado = golpeConectado; }
    public int getCombo() { return combo; }
    public String getNombre() { return this.nombre; }
    
    public float getDañoActual() {
        if (estadoActual == Estado.ataqueEspecial) {
            return dañoEspecial;
        }
        return 0f;
    }

    // ==========================================
    // MÉTODOS DE LA CLASE (LÓGICA Y COMBATE)
    // ==========================================
    
    public abstract void cargarPosesCombate();
    public abstract void ejecutarAtaqueEspecial(Personaje oponente);

    /** Selecciona si procesa la entrada física mediante un gamepad o el teclado. */
    public void procesarEntrada(Personaje oponente) {
        if (mando != null) {
            procesarEntradaMando(oponente);
        } else {
            procesarEntradaTeclado(oponente);
        }
    }

    /** Verifica si el personaje se encuentra ejecutando una acción prioritaria o bloqueante. */
    private boolean estaOcupado() {
        return estadoActual == Estado.atacar || 
               estadoActual == Estado.ataqueEspecial || 
               estadoActual == Estado.bloquear || 
               estadoActual == Estado.herido || 
               estadoActual == Estado.muerto;
    }
    
    /** Restablece los valores iniciales del personaje para iniciar un nuevo combate. */
    public void reiniciarCombate() {
        estadoActual = Estado.idle;
        stateTime = 0f;
        vida = 100f;
        velocidadX = 0;
        velocidadY = 0;
        enSuelo = true;
        combo = 0;
        temporizadorCombo = 0f;
        golpeConectado = false;
        botonAnterior = false;
        botonEspecialAnterior = false;
    }
    
    /** Lee los joysticks y botones de un mando físico de PlayStation 4 para mover o atacar. */
    private void procesarEntradaMando(Personaje oponente) {
        if (estadoActual == Estado.herido || estadoActual == Estado.muerto) {
            velocidadX = 0;
            return;
        }
        
        float ejeX = mando.getAxis(0);

        // 1. Movimiento horizontal mediante stick izquierdo o D-Pad del mando de PS4
        if (ejeX < -0.3f || mando.getButton(mando.getMapping().buttonDpadLeft)) {
            velocidadX = -velocidad_Movimiento;
            mirandoIzquierda = true;
            if (enSuelo && !estaOcupado()) setEstado(Estado.caminar);
        } else if (ejeX > 0.3f || mando.getButton(mando.getMapping().buttonDpadRight)) {
            velocidadX = velocidad_Movimiento;
            mirandoIzquierda = false;
            if (enSuelo && !estaOcupado()) setEstado(Estado.caminar);
        } else {
            velocidadX = 0;
            if (enSuelo && estadoActual != Estado.agachar && !estaOcupado()) setEstado(Estado.idle);
        }

        // 2. Bloqueo mapeado al botón Círculo (buttonB) del mando de PS4
        boolean presionaBloqueo = mando.getButton(mando.getMapping().buttonB);
        if (presionaBloqueo && enSuelo && estadoActual != Estado.atacar && estadoActual != Estado.ataqueEspecial) {
            setEstado(Estado.bloquear);
            velocidadX = 0;
        } else if (estadoActual == Estado.bloquear && !presionaBloqueo) {
            setEstado(Estado.idle);
        }

        // 3. Agacharse mediante stick izquierdo abajo o D-Pad Abajo en el mando de PS4
        float ejeY = mando.getAxis(1);
        boolean presionaAbajo = ejeY > 0.4f || mando.getButton(mando.getMapping().buttonDpadDown);
        if (presionaAbajo) {
            if (enSuelo && !estaOcupado()) {
                setEstado(Estado.agachar);
                velocidadX = 0;
            }
        } else {
            if (estadoActual == Estado.agachar && enSuelo) setEstado(Estado.idle);
        }

        // 4. Salto mapeado al botón Equis / X (buttonA) del mando de PS4
        if (mando.getButton(mando.getMapping().buttonA) && enSuelo && estadoActual != Estado.atacar && estadoActual != Estado.ataqueEspecial && estadoActual != Estado.bloquear) {
            velocidadY = fuerza_Salto;
            enSuelo = false;
            setEstado(Estado.saltar);
        }

        // 5. Ataque normal y cadena de combos mapeados al botón Cuadrado (buttonX) del mando de PS4
        boolean botonAhora = mando.getButton(mando.getMapping().buttonX);
        if (botonAhora && !botonAnterior && enSuelo && estadoActual != Estado.bloquear) {
            if (estadoActual != Estado.atacar && estadoActual != Estado.ataqueEspecial) {
                if (oponente != null) this.mirandoIzquierda = (this.x > oponente.getX());
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

        // 6. Ataque especial mapeado al botón Triángulo (buttonY) del mando de PS4
        boolean botonEspecialAhora = mando.getButton(mando.getMapping().buttonY);
        if (botonEspecialAhora && !botonEspecialAnterior && enSuelo && !estaOcupado()) {
            if (oponente != null) this.mirandoIzquierda = (this.x > oponente.getX());
            setEstado(Estado.ataqueEspecial);
            velocidadX = 0;
            golpeConectado = false;
            ejecutarAtaqueEspecial(oponente);
        }
        botonEspecialAnterior = botonEspecialAhora;
    }

    /** Lee las entradas del teclado diferenciando entre Jugador 1 y Jugador 2. */
    private void procesarEntradaTeclado(Personaje oponente) {
        if (estadoActual == Estado.herido || estadoActual == Estado.muerto) {
            velocidadX = 0;
            return;
        }
        
        if (!jugador2) {
            // Controles Jugador 1 (WASD + E: Bloquear, S: Agachar, W: Salto, F: Atacar, R: Especial)
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                velocidadX = -velocidad_Movimiento;
                mirandoIzquierda = true;
                if (enSuelo && !estaOcupado()) setEstado(Estado.caminar);
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                velocidadX = velocidad_Movimiento;
                mirandoIzquierda = false;
                if (enSuelo && !estaOcupado()) setEstado(Estado.caminar);
            } else {
                velocidadX = 0;
                if (enSuelo && estadoActual != Estado.agachar && !estaOcupado()) setEstado(Estado.idle);
            }
            
            if (Gdx.input.isKeyPressed(Input.Keys.E) && enSuelo && !estaOcupado()) {
                setEstado(Estado.bloquear);
                velocidadX = 0;
            } else if (estadoActual == Estado.bloquear && !Gdx.input.isKeyPressed(Input.Keys.E)) {
                setEstado(Estado.idle);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                if (enSuelo && !estaOcupado()) {
                    setEstado(Estado.agachar);
                    velocidadX = 0;
                }
            } else {
                if (estadoActual == Estado.agachar && enSuelo) setEstado(Estado.idle);
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) && enSuelo && !estaOcupado()) {
                velocidadY = fuerza_Salto;
                enSuelo = false;
                setEstado(Estado.saltar);
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.F) && enSuelo && estadoActual != Estado.bloquear) {
                if (estadoActual != Estado.atacar && estadoActual != Estado.ataqueEspecial) {
                    if (oponente != null) this.mirandoIzquierda = (this.x > oponente.getX());
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
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.R) && enSuelo && !estaOcupado()) {
                if (oponente != null) this.mirandoIzquierda = (this.x > oponente.getX());
                setEstado(Estado.ataqueEspecial);
                velocidadX = 0;
                golpeConectado = false;
                ejecutarAtaqueEspecial(oponente);
            }
        } else {
            // Controles Jugador 2 (Flechas + J: Bloquear, DOWN: Agachar, UP: Salto, K: Atacar, U: Especial)
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velocidadX = -velocidad_Movimiento;
                mirandoIzquierda = true;
                if (enSuelo && !estaOcupado()) setEstado(Estado.caminar);
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velocidadX = velocidad_Movimiento;
                mirandoIzquierda = false;
                if (enSuelo && !estaOcupado()) setEstado(Estado.caminar);
            } else {
                velocidadX = 0;
                if (enSuelo && estadoActual != Estado.agachar && !estaOcupado()) setEstado(Estado.idle);
            }
            
            if (Gdx.input.isKeyPressed(Input.Keys.J) && enSuelo && !estaOcupado()) {
                setEstado(Estado.bloquear);
                velocidadX = 0;
            } else if (estadoActual == Estado.bloquear && !Gdx.input.isKeyPressed(Input.Keys.J)) {
                setEstado(Estado.idle);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (enSuelo && !estaOcupado()) {
                    setEstado(Estado.agachar);
                    velocidadX = 0;
                }
            } else {
                if (estadoActual == Estado.agachar && enSuelo) setEstado(Estado.idle);
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && enSuelo && !estaOcupado()) {
                velocidadY = fuerza_Salto;
                enSuelo = false;
                setEstado(Estado.saltar);
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.K) && enSuelo && estadoActual != Estado.bloquear) {
                if (estadoActual != Estado.atacar && estadoActual != Estado.ataqueEspecial) {
                    if (oponente != null) this.mirandoIzquierda = (this.x > oponente.getX());
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
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.U) && enSuelo && !estaOcupado()) {
                if (oponente != null) this.mirandoIzquierda = (this.x > oponente.getX());
                setEstado(Estado.ataqueEspecial);
                velocidadX = 0;
                golpeConectado = false;
                ejecutarAtaqueEspecial(oponente);
            }
        }
    }

    /** Actualiza las leyes físicas de desplazamiento lateral y gravedad del personaje. */
    public void actualizarFisicas(float delta, float sueloY) {
        if (!enSuelo) {
            velocidadY -= gravedad * delta;
            if (estadoActual != Estado.muerto) setEstado(Estado.saltar);
        }
        
        x += velocidadX * delta;
        y += velocidadY * delta;

        if (y <= sueloY) {
            y = sueloY;
            velocidadY = 0;
            enSuelo = true;
            if (velocidadX == 0 && estadoActual == Estado.saltar) setEstado(Estado.idle);
        }

        if (x < 0) x = 0;
        
        float anchoSprite = 80f;
        if (poseIdle != null) {
            TextureRegion frameIdle = poseIdle.getKeyFrame(0, true);
            if (frameIdle != null) {
                float ratio = (float) frameIdle.getRegionWidth() / frameIdle.getRegionHeight();
                anchoSprite = altoCombate * ratio;
            }
        }

        float limiteDerecho = 1920f - anchoSprite;
        if (x > limiteDerecho) x = limiteDerecho;

        if (combo > 0 && estadoActual != Estado.atacar) {
            temporizadorCombo -= delta;
            if (temporizadorCombo <= 0) combo = 0;
        }
    }

    /** Genera y retorna la caja de colisión física (Hitbox) del cuerpo del personaje. */
    public Rectangle getHitBox() {
        float altoReal = altoCombate;
        if (estadoActual == Estado.saltar) {
            altoReal = altoCombate * 0.75f;
        } else if (estadoActual == Estado.agachar) {
            altoReal = altoCombate * 0.65f;
        }
        
        Animation<TextureRegion> poseActual = poseIdle;
        switch (estadoActual) {
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
        float anchoHitbox = Math.max(anchoSprite - 50f, 40f);
        return new Rectangle(x + 25f, y, anchoHitbox, altoReal);
    }

    /** Genera y retorna la caja de colisión del ataque activo según la dirección de la mirada. */
    public Rectangle getHitboxAtaque() {
        if (estadoActual != Estado.atacar && estadoActual != Estado.ataqueEspecial) {
            return new Rectangle(0, 0, 0, 0);
        }
        Rectangle hitboxBase = getHitBox();
        float anchoAtaque = (estadoActual == Estado.ataqueEspecial) ? 95f : 90f;
        float altoAtaque = 60f;
        float ataqueY = y + (altoCombate * 0.5f);
        float ataqueX = mirandoIzquierda ? (hitboxBase.x - anchoAtaque) : (hitboxBase.x + hitboxBase.width);
        return new Rectangle(ataqueX, ataqueY, anchoAtaque, altoAtaque);
    }

    /** Renderiza en pantalla el fotograma correspondiente a la animación actual del personaje. */
    public void renderizarPelea(SpriteBatch batch, float delta) {
        stateTime += delta;
        TextureRegion poseActiva = null;
        float altoReal = altoCombate;
        
        switch (estadoActual) {
            case caminar:
                if (poseCaminar != null) poseActiva = poseCaminar.getKeyFrame(stateTime, true);
                break;
            case saltar:
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
                    if (poseHerido.isAnimationFinished(stateTime)) setEstado(Estado.idle);
                } else {
                    if (stateTime > 0.25f) setEstado(Estado.idle);
                    if (poseIdle != null) poseActiva = poseIdle.getKeyFrame(stateTime, true);
                }
                break;
            case ataqueEspecial:
                if (poseAtaqueEspecial != null) {
                    poseActiva = poseAtaqueEspecial.getKeyFrame(stateTime, false);
                    if (poseAtaqueEspecial.isAnimationFinished(stateTime)) setEstado(Estado.idle);
                }
                break;
            case bloquear:
                if (poseBloquear != null) poseActiva = poseBloquear.getKeyFrame(stateTime, true);
                break;
            case muerto:
                if (poseMuerto != null) poseActiva = poseMuerto.getKeyFrame(stateTime, false);
                altoReal = altoCombate * 0.4f;
                break;
            case idle:
            default:
                if (poseIdle != null) poseActiva = poseIdle.getKeyFrame(stateTime, true);
                break;
        }

        if (poseActiva == null && poseIdle != null) {
            poseActiva = poseIdle.getKeyFrame(stateTime, true);
        }
        if (poseActiva == null) return;

        float ratio = (float) poseActiva.getRegionWidth() / poseActiva.getRegionHeight();
        float ancho = altoReal * ratio;

        if (mirandoIzquierda) {
            batch.draw(poseActiva, x + ancho, y, -ancho, altoReal);
        } else {
            batch.draw(poseActiva, x, y, ancho, altoReal);
        }
    }

    /** Dibuja la representación 2D estática del personaje en los menús de selección. */
    public void renderizarModelo(SpriteBatch batch, float x, float y, float alto) {
        if (render2d != null) {
            float ratio = (float) render2d.getWidth() / render2d.getHeight();
            float ancho = alto * ratio;
            batch.draw(render2d, x, y, ancho, alto);
        }
    }

    /** Crea y devuelve el botón interactivo con el icono del personaje para la interfaz de selección. */
    public ImageButton crearBotonMenu() {
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(icono));
        ImageButton boton = new ImageButton(drawable);
        boton.setBounds(menuX, menuY, anchoIcono, altoIcono);
        return boton;
    }

    /** Reproduce el efecto sonoro con el nombre del personaje al seleccionarlo. */
    public void reproducirSeleccion() {
        if (seleccion != null) seleccion.play(1.0f);
    }

    /** Aplica daño, reduce la salud y ejecuta retroceso físico (knockback) o mitigación por bloqueo. */
    public void recibirDaño(float cantidad, boolean atacanteIzquierda) {
        if (estadoActual == Estado.muerto) return;
        float dañoFinal = (estadoActual == Estado.bloquear) ? (cantidad * 0.5f) : cantidad;

        this.vida -= dañoFinal;
        if (this.vida <= 0) {
            this.vida = 0;
            setEstado(Estado.muerto);
            velocidadX = 0;
            return;
        }

        if (estadoActual == Estado.bloquear) {
            velocidadX = 0;
        } else {
            setEstado(Estado.herido);
            velocidadX = 0;
            float fuerzaRetroceso = 40f;
            if (atacanteIzquierda) {
                x += fuerzaRetroceso;
            } else {
                x -= fuerzaRetroceso;
            }
        }
    }

    /** Libera los recursos de texturas y sonidos almacenados en memoria RAM y GPU. */
    public void dispose() {
        if (icono != null) icono.dispose();
        if (render2d != null) render2d.dispose();
        if (seleccion != null) seleccion.dispose();
        for (Texture t : texturasCombate) {
            if (t != null) t.dispose();
        }
        texturasCombate.clear();
    }
}