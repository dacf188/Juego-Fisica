package com.smash.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.smash.Personaje;

public class HarleyQuinn extends Personaje{
    public HarleyQuinn (){
        super("HarleyQuinn", "Iconos/Icono-HarleyQuinn.png", "Renders2D/Render2dHarleyQuinn.png", 647, 740, "AudioHarleyQuinn.wav");
    }
    @Override
    public void cargarPosesCombate (){
    //CAMINAR
        Texture cam1 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Cam1-G.png"));
        Texture cam2 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Cam2-G.png"));
        Texture cam3 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Cam3-G.png"));
    //SALTO
        Texture salto1 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Salto-G.png"));
        Texture salto2 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Salto2-G.png"));
        Texture salto3 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Salto3-G.png"));
    //AGACHARSE
        Texture agachar = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/Agacharse-G.png"));
    //IDLE
        Texture idle = new Texture(Gdx.files.internal("Renders2D/Render2dHarleyQuinn.png"));
    ///ATAQUES COMBO
        Texture ataque1 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/ataque1.png"));
        Texture ataque2 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/ataque2.png"));
        Texture ataque3 = new Texture(Gdx.files.internal("Renders2D/HarleyQuinn/ataque3.png"));
    //HERIDO
    
    //BLOQUEO
    
    //MUERTE
    
        
        texturasCombate.addAll(cam1, cam2, cam3, salto1, salto2, salto3, agachar, idle, ataque1, ataque2, ataque3);
    
        Array<TextureRegion> framesCaminar = new Array<>();
        framesCaminar.add(new TextureRegion(cam1));
        framesCaminar.add(new TextureRegion(cam2));
        framesCaminar.add(new TextureRegion(cam3));
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
    }
}
