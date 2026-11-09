package com.smash.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.smash.Personaje;

//CADA COMENTARIO E INFORMACIÓN SE IRÍA REPITIENDO EN CADA CLASE HIJA

public class HarleyQuinn extends Personaje {

    /** Constructor: inicializa los datos de identidad, icono, render 2D, posición en menú y locución de Harley Quinn. */
    public HarleyQuinn() {
        super("HarleyQuinn", "Iconos/Icono-HarleyQuinn.png", "Renders2D/Render2dHarleyQuinn.png", 647, 740, "AudioHarleyQuinn.wav");
    }

    @Override
    /** Carga y configura todas las texturas, secuencias de animación y registro de recursos de combate. */
    public void cargarPosesCombate() {
        // Carga de texturas para caminar
        Texture cam1 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Cam1-G.png"));
        Texture cam2 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Cam2-G.png"));
        Texture cam3 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Cam3-G.png"));
        
        // Carga de texturas para salto
        Texture salto1 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Salto-G.png"));
        Texture salto2 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Salto2-G.png"));
        Texture salto3 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Salto3-G.png"));
        
        // Carga de texturas de estado estático y agachado
        Texture agachar = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Agacharse-G.png"));
        Texture idle = new Texture(Gdx.files.internal("Renders2D/Render2dHarleyQuinn.png"));
        
        // Carga de texturas para ataques normales (cadena de combos)
        Texture ataque1 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/ataque1.png"));
        Texture ataque2 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/ataque2.png"));
        Texture ataque3 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/ataque3.png"));
        
        // Carga de texturas de daño, bloqueo y muerte
        Texture herido = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/herido.png"));
        Texture bloqueo = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/bloqueo.png"));
        Texture muerto = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/muerte1.png"));
        Texture muerto1 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/muerte2.png"));
        
        // Carga de textura para el ataque especial
        Texture especial = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/especial.png"));
        
        // Agrega todas las texturas creadas al arreglo de control para su posterior liberación de memoria
        texturasCombate.addAll(cam1, cam2, cam3, salto1, salto2, salto3, agachar, idle, ataque1, ataque2, ataque3, herido, bloqueo, muerto, muerto1, especial);
    
        // Configuración de la animación de caminar
        Array<TextureRegion> framesCaminar = new Array<>();
        framesCaminar.add(new TextureRegion(cam1));
        framesCaminar.add(new TextureRegion(cam2));
        framesCaminar.add(new TextureRegion(cam3));
        this.poseCaminar = new Animation<TextureRegion>(0.12f, framesCaminar);
    
        // Configuración de la animación de salto
        Array<TextureRegion> framesSaltar = new Array<>();
        framesSaltar.add(new TextureRegion(salto1));
        framesSaltar.add(new TextureRegion(salto2));
        framesSaltar.add(new TextureRegion(salto3));
        this.poseSaltar = new Animation<TextureRegion>(0.15f, framesSaltar);
        
        // Configuración de poses estáticas (agachado e idle)
        this.poseAgachado = new Animation<TextureRegion>(1f, new TextureRegion(agachar));
        this.poseIdle = new Animation<TextureRegion>(1f, new TextureRegion(idle));
        
        // Configuración de las animaciones de la cadena de ataques normales
        this.poseAtacar1 = new Animation<TextureRegion>(0.35f, new TextureRegion(ataque1));
        this.poseAtacar2 = new Animation<TextureRegion>(0.35f, new TextureRegion(ataque2));
        this.poseAtacar3 = new Animation<TextureRegion>(0.35f, new TextureRegion(ataque3));
        
        // Configuración de poses de daño y bloqueo
        this.poseHerido = new Animation<TextureRegion>(0.25f, new TextureRegion(herido));
        this.poseBloquear = new Animation<TextureRegion>(1f, new TextureRegion(bloqueo));
        
        // Configuración de la animación de muerte
        Array<TextureRegion> frameMuerte = new Array<>();
        frameMuerte.add(new TextureRegion(muerto));
        frameMuerte.add(new TextureRegion(muerto1));
        this.poseMuerto = new Animation<TextureRegion>(0.25f, frameMuerte);
        
        // Configuración de la animación del ataque especial
        this.poseAtaqueEspecial = new Animation<TextureRegion>(0.3f, new TextureRegion(especial));
    }

    @Override
    /** Ejecuta la lógica de impacto y cálculo de daño del ataque especial sobre el oponente. */
    public void ejecutarAtaqueEspecial(Personaje oponente) {
        if (oponente != null && getHitboxAtaque().overlaps(oponente.getHitBox())) {
            boolean atacanteIzquierda = this.x < oponente.getX();
            oponente.recibirDaño(dañoEspecial, atacanteIzquierda);
            golpeConectado = true;
        }
    }
}