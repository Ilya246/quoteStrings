package quotestrings;

import mindustry.game.EventType.*;
import mindustry.mod.Mod;
import mindustry.gen.*;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.Router;
import mindustry.world.blocks.ConstructBlock.*;
import arc.*;
import arc.util.*;
import arc.math.*;
import arc.struct.*;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.graphics.*;
import arc.fx.FxProcessor;
import arc.fx.filters.ChromaticAberrationFilter;
import arc.fx.filters.LevelsFilter;

import static mindustry.Vars.*;

public class quoteStrings extends Mod{

    public float chance = 0.5f;
    public FxProcessor fx;
    public float clampFloor = 0.02f;
    public float clampCeil = 5f;

    public static String quote(int length){
        String[] quotes = new String[]{"you cannot kill me in a way that matters.", "just think, every step taken is another soul left behind", "everything burns every single day until it's reduced to dust", "this doesn't end well", "you think you're safe?", "one cannot create beauty without destruction", "every single moment has consequence", "you wouldn't want anyone to know what you're hiding.", "where are you right now? what do you fear?", "it doesn't make sense to save now.", "it's too late.", "where is it.", "there is no threat", "it's always been there", "never make another wish ever again.", "where are you right now?", "why? it will never end now.", "do not.", "they are not your enemy", "this is your fault.", "we are not dead yet.", "it's finally happening", "please verify your humanity", "no one will matter", "this is not a matter of caring.", "are you okay with what you just did?", "stop reading this.", "watch your head.", "if you see this", "do not look at it", "observation is prohibited.", "your mind is nonexistent"};
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            String[] quote = Structs.select(quotes).split(" ");
            int from = Mathf.random(0, Math.max(quote.length - 3, 0));
            int to = Mathf.random(from, quote.length - 1);
            for (int j = from; j <= to; ++j) {
                result.append(quote[j]);
                result.append(" ");
            }
        }
        return result.toString();
    }

    public static boolean isRouter(Building build){
        Block rblock;
        if(build instanceof ConstructBuild){
            ConstructBuild cbuild = (ConstructBuild)build;
            rblock = cbuild.current == null ? cbuild.prevBuild.get(0).block : cbuild.current;
        }else{
            rblock = build.block;
        };
        return rblock instanceof Router && rblock.size == 1;
    }

    @Override
    public void init(){
        fx = new FxProcessor();
        final ObjectMap<String, String> map = Core.bundle.getProperties();
        Core.bundle.setProperties(new ObjectMap<String, String>(){
            {
                this.putAll(map);
            }

            @Override
            public String get(String key){
                if(Mathf.chance(chance)){
                    Tmp.c1.set(Color.scarlet).lerp(Color.yellow, Mathf.absin(10.0f - chance * 3.0f, 1.0f)).lerp(Color.white, 1.0f - chance);
                    String par = (String)super.get(key);
                    return "[#" + Tmp.c1 + "]" + quote(Mathf.round(Mathf.random(1f, Math.max((float)par.length() / 7f, 2f)) * Math.max(chance / 2f, 1)));
                }
                return (String)super.get(key);
            }
        });

        Events.on(BlockBuildEndEvent.class, e -> {
            Building b = e.tile.build;
            if(b != null && isRouter(b)){
                float amplify = e.breaking ? -2f : 1f;
                for(Building pblock : b.proximity){
                    if(isRouter(pblock)){
                        amplify *= 2f;
                    };
                };
                Player p = e.unit.getPlayer();
                if(p != null && p != player){
                    amplify = 0f;
                };
                chance += amplify / 400f;
            };
        });

        Events.run(Trigger.update, () -> {
            chance = Mathf.clamp(chance + Mathf.random(-0.00008f, 0.00008f), clampFloor, clampCeil);
        });

        fx.addEffect(new LevelsFilter(){
            @Override
            public void update() {
                this.saturation = 1.0f + Math.max((chance - 0.6f) * 1.5f, 0f);
                this.contrast = 1.0f + Math.max((chance - 0.6f) * 1.5f, 0f);
                this.rebind();
            }
        });
        fx.addEffect(new ChromaticAberrationFilter(4){
            @Override
            public void update() {
                this.maxDistortion = Math.max((chance - 0.8f) * 1.5f, 0.0f);
                boolean bl = this.disabled = chance < 0.8f;
                if(!this.disabled){
                    this.rebind();
                }
            }
        });
        Events.run(Trigger.preDraw, () -> {
            fx.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            fx.clear(Color.black);
            fx.begin();
        });
        Events.run(Trigger.uiDrawBegin, () -> {
	           if(state.isMenu()){
                  fx.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
                  fx.clear(Color.black);
                  fx.begin();
	           };
        });
        Events.run(Trigger.uiDrawEnd, () -> {
            fx.end();
            fx.applyEffects();
            fx.render();
        });
    }
}
