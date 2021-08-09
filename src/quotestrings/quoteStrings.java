package quotestrings;

import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.content.*;
import mindustry.mod.*;
import mindustry.mod.Mod.*;
import mindustry.gen.*;
import mindustry.world.Block;
import mindustry.world.blocks.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.ConstructBlock.*;
import arc.Core.*;
import arc.util.*;
import arc.util.Log;
import arc.math.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.struct.Seq;
import arc.graphics.*;
import arc.Events.*;

import static mindustry.Vars.*;

public class quoteStrings extends Mod{

    public float chance = 1f;

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

    public static boolean isRouter(Block block){
        return block instanceof Router && block.size == 1;
    }

    @Override
    public void init(){
        Timer.schedule(() -> {chance = 0.5f;}, 1f);
        final ObjectMap<String, String> map = arc.Core.bundle.getProperties();
        arc.Core.bundle.setProperties(new ObjectMap<String, String>(){
            {
                this.putAll(map);
            }

            @Override
            public String get(String key){
                if(Mathf.chance(chance)){
                    Tmp.c1.set(Color.scarlet).lerp(Color.yellow, Mathf.absin(10.0f - chance * 3.0f, 1.0f)).lerp(Color.white, 1.0f - chance);
                    String par = (String)super.get(key);
                    return "[#" + Tmp.c1 + "]" + quote(Mathf.random(1, Math.max(par.length() / 7, 2)));
                }
                return (String)super.get(key);
            }
        });

        arc.Events.on(BlockBuildEndEvent.class, e -> {
            Building b = e.tile.build;
            if(b != null && !e.breaking && isRouter(b.block)){
                float[] amplify = new float[]{1f};
                b.proximity.each(pblock -> {
                    if(pblock.block == Blocks.router){
                        amplify[0] += 2f;
                    };
                });
                Player p = e.unit.getPlayer();
                if(p != null && p != player){
                    amplify[0] *= 0.1f;
                };
                chance = Mathf.clamp(chance + amplify[0] / 200f, 0.02f, 1f);
            };
        });

        arc.Events.run(Trigger.update, () -> {
            chance = Mathf.clamp(chance + Mathf.random(-0.00008f, 0.00008f), 0.02f, 1f);
        });
    }
}
