package com.smash.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.smash.Personaje;

public class ArthurMorgan extends Personaje{
    public ArthurMorgan (){
        super("Arthur Morgan", "Iconos/Icono-ArthurMorgan.png", "Renders2D/Render2D-ArthurMorgan.png", 779, 740, "AudioArthur Morgan.wav");
    }
    @Override
    public void cargarPosesCombate (){
    //CAMINAR
        Texture cam1 = new Texture(Gdx.files.internal("Renders2D/ArthurMorgan/Cam1.png"));
        Texture cam2 = new Texture(Gdx.files.internal("Renders2D/ArthurMorgan/Cam2.png"));
        Texture cam3 = new Texture(Gdx.files.internal("Renders2D/ArthurMorgan/Cam3.png"));
        Texture cam4 = new Texture(Gdx.files.internal("Renders2D/ArthurMorgan/Cam4.png"));
    //SALTO
        Texture salto1 = new Texture(Gdx.files.internal("Renders2D/ArthurMorgan/salto1.png"));
        Texture salto2 = new Texture(Gdx.files.internal("Renders2D/ArthurMorgan/salto2.png"));
        Texture salto3 = new Texture(Gdx.files.internal("Renders2D/ArthurMorgan/salto3.png"));
    //AGACHARSE
        Texture agachar = new Texture(Gdx.files.internal("Renders2D/ArthurMorgan/Agachar.png"));
    //IDLE
        Texture idle = new Texture(Gdx.files.internal("Renders2D/ArthurMorgan/Idle.png"));
    //ATAQUES COMBO
        Texture ataque1 = new Texture(Gdx.files.internal("Renders2D/ArthurMorgan/ataque1.png"));
        Texture ataque2 = new Texture(Gdx.files.internal("Renders2D/ArthurMorgan/ataque2.png"));
        Texture ataque3 = new Texture(Gdx.files.internal("Renders2D/ArthurMorgan/ataque3.png"));
    //HERIDO
        Texture herido = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/.png"));
    //BLOQUEO
       Texture bloqueo = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/.png"));
    //MUERTE
        Texture muerto = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/.png"));
    //GOLPE ESPECIAL
        Texture especial = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/.png"));
        
        texturasCombate.addAll(cam1, cam2, cam3, salto1, salto2, salto3, agachar, idle, ataque1, ataque2, ataque3, herido, bloqueo, muerto, especial);
    
        Array<TextureRegion> framesCaminar = new Array<>();
        framesCaminar.add(new TextureRegion(cam1));
        framesCaminar.add(new TextureRegion(cam2));
        framesCaminar.add(new TextureRegion(cam3));
        framesCaminar.add(new TextureRegion(cam4));
        this.poseCaminar = new Animation<TextureRegion>(0.12f, framesCaminar);
    
        Array<TextureRegion> framesSaltar = new Array<>();
        framesSaltar.add(new TextureRegion(salto1));
        framesSaltar.add(new TextureRegion(salto2));
        framesSaltar.add(new TextureRegion(salto3));
        this.poseSaltar = new Animation<TextureRegion>(0.15f, framesSaltar);
        
        this.poseAgachado = new Animation<TextureRegion>(1f, new TextureRegion(agachar));
        this.poseIdle = new Animation<TextureRegion>(1f, new TextureRegion(idle));
        
        this.poseAtacar1 = new Animation<TextureRegion>(0.35f, new TextureRegion(ataque1));
        this.poseAtacar2 = new Animation<TextureRegion>(0.35f, new TextureRegion(ataque2));
        this.poseAtacar3 = new Animation<TextureRegion>(0.35f, new TextureRegion(ataque3));
        
        this.poseHerido = new Animation<TextureRegion>(0.25f, new TextureRegion(herido));
        this.poseBloquear = new Animation<TextureRegion>(1f, new TextureRegion(bloqueo));
        this.poseMuerto = new Animation<TextureRegion>(1f, new TextureRegion(muerto));
        this.poseAtaqueEspecial = new Animation<TextureRegion>(0.3f, new TextureRegion(especial));
    }
    @Override
    public void ejecutarAtaqueEspecial(Personaje oponente) {

    }
}
