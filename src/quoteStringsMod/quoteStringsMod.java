package quoteStringsMod;

import mindustry.content.*;
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.mod.Mod;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.blocks.ConstructBlock.*;
import mindustry.world.blocks.distribution.Router;
import arc.*;
import arc.fx.filters.ChromaticAberrationFilter;
import arc.fx.filters.LevelsFilter;
import arc.fx.FxProcessor;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.Scl;
import arc.struct.*;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.*;
import arc.util.pooling.*;

import static mindustry.Vars.*;

public class quoteStringsMod extends Mod{

    // changing variables
    public float chance = 0.3f;
    public float deltaTimeMult = 1f;
    public float tpass = 0f;
    public float ampValue = 0f;
  	public ObjectFloatMap<Unit> currTalkerFades = new ObjectFloatMap<>();;
    public ObjectMap<Unit, String> currTalkerTexts = new ObjectMap<>();

    // usually unchanging variables
    public FxProcessor fx;
    public float chanceDrift = 0.00008f;
    public float clampFloor = 0.02f;
    public float clampCeil = 5f;
    public float amplifyMult = 0.002f;
    public float routerchainAmp = 2f;
    public float reignSpeed = 0.35f;
    public float reignTargetSpeed = 3f;
  	public float textFadeDur = 300f;
    public float timeOnset = 9000f;
    public float timeOnset2 = 18000f;
    public float timeOnset3 = 36000f;
    public String[] randQuotes = new String[]{"you cannot kill me in a way that matters.", "just think, every step taken is another soul left behind", "everything burns every single day until it's reduced to dust",
                                              "this doesn't end well", "you think you're safe?", "one cannot create beauty without destruction", "every single moment has consequence",
                                              "you wouldn't want anyone to know what you're hiding.", "where are you right now? what do you fear?", "it doesn't make sense to save now.", "it's too late.",
                                              "where is it.", "there is no threat", "it's always been there", "never make another wish ever again.", "where are you right now?", "why? it will never end now.",
                                              "do not.", "they are not your enemy", "this is your fault.", "we are not dead yet.", "it's finally happening", "please verify your humanity", "no one will matter",
                                              "this is not a matter of caring.", "are you okay with what you just did?", "stop reading this.", "watch your head.", "if you see this", "do not look at it",
                                              "observation is prohibited.", "your mind is nonexistent"};


	public String[] unitComplaints = new String[]{"i feel a bit weird", "wonder where our enemies keep coming from", "moving this much silicon ain't easy", "ever thought why flyers can't land?",
                                                  "is @ planning to explode us again?", "wait, is that a... @, what is this?", "reminder to not spend too much silicon", "i heard stained mountains are not as dangerous anymore",
                                                  "@, what does \"b" + String.valueOf(Version.build) + (Version.revision > 0 ? ("." + String.valueOf(Version.revision)) : "") + "\" mean?", "gimme something to shoot",
                                                  "have you ever been to salt flats?", "commander?", "rather surprising", "you ever heard about factorio?", "shock mines? i haven't seen any",
                                                  "hi", "how do i sprint", "are you chaining routers again?", "@, can you hear me?", "does @ know we can talk?",
                                                  "who is @?", "why is @ watching us?", "nuclear launch detected", "is being @ that good?", "router", "cool", "why can we chat", "forward", "what is this?",
                                                  "@?", "hey @, i think i saw a foreshadow earlier", "are you my creator, @?", "confusing", "first time here",
                                                  "i don't think router chains are a good idea", "i heard something is up about router chains", "deconstructing routers is good",
                                                  "go away and go eat a kebab", "no survivors", "when are we getting into combat?", "i'm bored already, let me fight", "wait, we can talk?", 
												  "eradication", "feels as if i've come to life only recently", "who are we fighting this time around?", "is it dangerous out there?", "what's up?",
												  "who's there?", "are you my friends?", "can i have more friends, @?", "i want to go home", "you sure this is a good idea?", "doesn't make sense",
												  "the spores are kind of quiet today", "what's that noise?", "i think i heard something", "is that home?", "if you plan on suiciding us at least do it efficiently"};

    public String[] unitPain = new String[]{"life is pain", "are [accent]they[] the bad guys?", "why was i programmed to feel pain", "do you realise what you're doing?", "when will this end, @?",
                                            "every day is shooting until the pain stops", "the horror", "this is your fault @", "why?", "what have i done to deserve this", "what is happening?", "no..", "mistakes were made",
                                            "just let me die already guys", "what crimes for they", "not a single soul would care", "god no", "i refuse", "and you blame me for being a bad war machine?", "there is no escape",
                                            "the anguish", "they haven't done anything to us", "war? what is it good for?", "HELP", "fuck", "unacceptable", "if my weapons could rotate faster, i would shoot myself",
                                            "this is a campaign reset", "i can't conquer the sector alone", "take us home", "existence is stupid", "there is no time left, @", "a minute more and we lose all hope",
                                            "it's not too late to uninstall", "it's all over", "AAAAAAAAAAAAA", "cut the routerchain", "agony", "i'm losing all will", "what is @ made of? what does it desire?",
                                            "what am i for?", "why am i?", "at least tell me my purpose so i know what i'll be missing out on", "absolutely deplorable", "do not continue", "stop", "would gladly throw myself into slag",
                                            "[scarlet]are you sure you're safe, [#FF]" + System.getProperty("user.name").replace("[","[[") + "[]?", "cut off their limbs", "how are we better than them?", "i am not real...",
                                           	"this is all a game, isn't it?", "do my choices even matter?", "every second is more painful than you could ever imagine", "PAIN.", "it's ripping me from the inside out"};
    // shader variables
    public float saturContrastThres = 0.6f;
    public float saturContrastMult = 1.5f;
    public float chromAbbThres = 0.8f;
    public float chromAbbMult = 1.5f;

    public String quote(int length){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            String[] quote = Structs.random(randQuotes).split(" ");
            int from = Mathf.random(0, Math.max(quote.length - 3, 0));
            int to = Mathf.random(from, quote.length - 1);
            for (int j = from; j <= to; ++j) {
                result.append(quote[j]);
                result.append(" ");
            }
        }
        return result.toString();
    }

    public String unitQuote(float which){
        String[] saidQuoteArr = (Mathf.random(which) > 0.5f ? unitPain : unitComplaints);
        return Strings.format(Mathf.random(which) > 1f ? quote((int)Math.ceil(which * 2f)) : Structs.random(saidQuoteArr), Mathf.random(which) > 0.5f ? Strings.stripColors(player.name) : "[lightgray]observer[]");
    }

    public boolean isRouter(Building build){
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
                float amplify = e.breaking ? -1f : 1f;
                for(Building pblock : b.proximity){
                    if(isRouter(pblock)){
                        amplify *= routerchainAmp;
                    }
                };
                Player p = e.unit.getPlayer();
                if(p != null && p != player){
                    amplify = 0f;
                }
                chance += amplify * amplifyMult;
            }
        });

        Events.run(Trigger.update, () -> {
            ampValue = chance * tpass;
            chance = Mathf.clamp(chance + Mathf.random(-chanceDrift, chanceDrift), clampFloor, clampCeil);
            tpass = tpass + Math.abs(Time.delta * chance);
            if(ampValue > timeOnset){
                float aAmpValue = ampValue - timeOnset;
                Blocks.router.details = "[red]Something is not right. " + String.valueOf(Mathf.round(chance, 0.001f)) + "\n\nSure you want to continue? " + String.valueOf(Mathf.round(tpass, 0.001f));
                for(Unit u: Groups.unit){
                    if(Mathf.chance(ampValue / timeOnset3 / 600f) && !currTalkerTexts.containsKey(u)){
                        currTalkerTexts.put(u, unitQuote(ampValue / timeOnset3));
                        currTalkerFades.put(u, textFadeDur);
                    }
                }
                if(aAmpValue > timeOnset2){
                    aAmpValue -= timeOnset2;
                    Blocks.router.details += "\n\n[scarlet]You don't have much left.";
                    UnitTypes.reign.speed = Mathf.lerp(reignSpeed, reignTargetSpeed, Mathf.clamp(aAmpValue / timeOnset3, 0f, 1f));
                  	if(aAmpValue > timeOnset3){
                      	aAmpValue -= timeOnset3;
						Blocks.router.details += "\n\n[#FF]Are you really fine with what you are doing?";
                    }
                }
            }
            if(chance > 2f){
                float deltaChange = (chance - 2f) * (chance - 2f) / 200f;
                deltaTimeMult = Mathf.lerp(0f, deltaTimeMult * 2f + 0.5f, Mathf.random(0.5f - deltaChange, 0.5f + deltaChange));
            }
            clampCeil = Math.max(5f, clampCeil + (chance - 4f) * 0.0006f);
            deltaTimeMult = Mathf.lerp(deltaTimeMult, 1f, Math.min(0.01f / chance, 1f));
        });
        Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60f * deltaTimeMult, 3f));

        fx.addEffect(new LevelsFilter(){
            @Override
            public void update() {
                this.saturation = 1.0f + Math.max((chance - saturContrastThres) * saturContrastMult, 0f);
                this.contrast = 1.0f + Math.max((chance - saturContrastThres) * saturContrastMult, 0f);
                this.rebind();
            }
        });
        fx.addEffect(new ChromaticAberrationFilter(4){
            @Override
            public void update() {
                this.maxDistortion = Math.max((chance - chromAbbThres) * chromAbbMult, 0.0f);
                boolean bl = this.disabled = chance < chromAbbThres;
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
        Events.run(Trigger.draw, () -> {
            currTalkerTexts.each((k, v) -> {
                float currFade = currTalkerFades.get(k, 0f);
                if(currFade < 0){
                    currTalkerTexts.remove(k);
                    currTalkerFades.remove(k, 0f);
                }else{
                    Draw.z(Layer.playerName);
                    float z = Drawf.text();
                    Font font = Fonts.def;
                    GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
                    final float textHeight = 15f;
                    boolean ints = font.usesIntegerPositions();
                    font.setUseIntegerPositions(false);
                    font.getData().setScale(0.25f / Scl.scl(1f));
                    float width = 100f;
                    float visualFadeTime = 1f - Mathf.curve(1f - currFade / textFadeDur, 0.9f);
                    font.setColor(1f, 1f, 1f, visualFadeTime);
                    layout.setText(font, v, Color.white, width, Align.bottom, true);
                    Draw.color(0f, 0f, 0f, 0.3f * visualFadeTime);
                    Fill.rect(k.x, k.y + textHeight + layout.height - layout.height / 2f, layout.width + 2, layout.height + 3);
                    font.draw(v, k.x - width / 2f, k.y + textHeight + layout.height, width, Align.center, true);
                    currTalkerFades.put(k, currFade - Time.delta);
                    Draw.reset();
                    Pools.free(layout);
                    font.getData().setScale(1f);
                    font.setColor(Color.white);
                    font.setUseIntegerPositions(ints);
                    Draw.z(z);
                }
            });
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
