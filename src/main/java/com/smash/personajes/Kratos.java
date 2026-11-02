package com.smash.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.smash.Personaje;

public class Kratos extends Personaje{
    public Kratos (){
        super("Kratos", "Iconos/IconoKratos.png", "Renders2D/Render2dKratos.png", 647, 575, "AudioKratos.wav");
    }
    @Override
    public void cargarPosesCombate (){
    //CAMINAR
        Texture cam1 = new Texture(Gdx.files.internal("Renders2D/Kratos/Caminar.png"));
        Texture cam2 = new Texture(Gdx.files.internal("Renders2D/Kratos/Caminar2.png"));
        Texture cam3 = new Texture(Gdx.files.internal("Renders2D/Kratos/Caminar3.png"));
    //SALTO
        Texture salto1 = new Texture(Gdx.files.internal("Renders2D/Kratos/Salto.png"));
        Texture salto2 = new Texture(Gdx.files.internal("Renders2D/Kratos/Salto2.png"));
        Texture salto3 = new Texture(Gdx.files.internal("Renders2D/Kratos/Salto3.png"));
    //AGACHARSE
        Texture agachar1 = new Texture(Gdx.files.internal("Renders2D/Kratos/Agachar.png"));
        Texture agachar2 = new Texture(Gdx.files.internal("Renders2D/Kratos/Agachar2.png"));
        Texture agachar3 = new Texture(Gdx.files.internal("Renders2D/Kratos/Agachar3.png"));
    //IDLE
        Texture idle = new Texture(Gdx.files.internal("Renders2D/Kratos/idle.png"));
    //ATAQUES COMBO
        Texture ataque1 = new Texture(Gdx.files.internal("Renders2D/Kratos/atacar1.png"));
        Texture ataque2 = new Texture(Gdx.files.internal("Renders2D/Kratos/Atacar2.png"));
        Texture ataque3 = new Texture(Gdx.files.internal("Renders2D/Kratos/atacar3.png"));
        
        texturasCombate.addAll(cam1, cam2, cam3,salto1, salto2, salto3, agachar1, agachar2, agachar3, idle, ataque1, ataque2, ataque3);
    
        //ARREGLO DE CAMINAR
        Array<TextureRegion> framesCaminar = new Array<>();
        framesCaminar.add(new TextureRegion(cam1));
        framesCaminar.add(new TextureRegion(cam2));
        framesCaminar.add(new TextureRegion(cam3));
        this.poseCaminar = new Animation<TextureRegion>(0.12f, framesCaminar);
    
        //ARREGLO DE SALTAR
        Array<TextureRegion> framesSaltar = new Array<>();
        framesSaltar.add(new TextureRegion(salto1));
        framesSaltar.add(new TextureRegion(salto2));
        framesSaltar.add(new TextureRegion(salto3));
        this.poseSaltar = new Animation<TextureRegion>(0.15f, framesSaltar);
        
        //ARREGLO DE AGACHAR
        Array<TextureRegion> framesAgachar = new Array<>();
        framesAgachar.add(new TextureRegion(agachar1));
        framesAgachar.add(new TextureRegion(agachar2));
        framesAgachar.add(new TextureRegion(agachar3));
        this.poseAgachado = new Animation<TextureRegion>(0.18f, framesAgachar);
        
        //ARREGLO DE IDLE
        this.poseIdle = new Animation<TextureRegion>(1f, new TextureRegion(idle));
        
        //ARREGLO DE ATAQUES
        this.poseAtacar1 = new Animation<TextureRegion>(0.35f, new TextureRegion(ataque1));
        this.poseAtacar2 = new Animation<TextureRegion>(0.35f, new TextureRegion(ataque2));
        this.poseAtacar3 = new Animation<TextureRegion>(0.35f, new TextureRegion(ataque3));
    }
    
}
