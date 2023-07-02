package AncientEnemies.patches;

import AncientEnemies.monsters.beyond.*;
import AncientEnemies.monsters.city.*;
import AncientEnemies.monsters.exordium.*;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class MonsterHelperPatch {
    private static final Logger logger = LogManager.getLogger(MonsterHelper.class.getName());

    public static AbstractMonster getShape(String key, float xPos, float yPos) {
        switch (key) {
            case "Repulsor":
                return new Repulsor(xPos, yPos);
            case "Spiker":
                return new Spiker(xPos, yPos);
            case "Exploder":
                return new Exploder(xPos, yPos);
            default:
                logger.info("UNKNOWN SHAPE: " + key);
                return null;
        }
    }

    private static MonsterGroup spawnShapes(boolean weak) {
        ArrayList<String> shapePool = new ArrayList<>();
        shapePool.add("Repulsor");
        shapePool.add("Repulsor");
        shapePool.add("Exploder");
        shapePool.add("Exploder");
        shapePool.add("Spiker");
        shapePool.add("Spiker");
        AbstractMonster[] retVal;
        if (weak) {
            retVal = new AbstractMonster[3];
        } else {
            retVal = new AbstractMonster[4];
        }

        int index = AbstractDungeon.miscRng.random(shapePool.size() - 1);
        String key = shapePool.get(index);
        shapePool.remove(index);
        retVal[0] = getShape(key, -480.0F, 6.0F);
        index = AbstractDungeon.miscRng.random(shapePool.size() - 1);
        key = shapePool.get(index);
        shapePool.remove(index);
        retVal[1] = getShape(key, -240.0F, -6.0F);
        index = AbstractDungeon.miscRng.random(shapePool.size() - 1);
        key = shapePool.get(index);
        shapePool.remove(index);
        retVal[2] = getShape(key, 0.0F, -12.0F);
        if (!weak) {
            index = AbstractDungeon.miscRng.random(shapePool.size() - 1);
            key = shapePool.get(index);
            shapePool.remove(index);
            retVal[3] = getShape(key, 240.0F, 12.0F);
        }

        return new MonsterGroup(retVal);
    }

    private static ArrayList<String> gremlinPool() {
        ArrayList<String> gremlinPool = new ArrayList<>();
        gremlinPool.add("GremlinWarrior");
        gremlinPool.add("GremlinWarrior");
        gremlinPool.add("GremlinThief");
        gremlinPool.add("GremlinThief");
        gremlinPool.add("GremlinFat");
        gremlinPool.add("GremlinFat");
        gremlinPool.add("GremlinTsundere");
        gremlinPool.add("GremlinWizard");
        return gremlinPool;
    }

    private static AbstractMonster spawnGremlin(float x, float y) {
        ArrayList<String> gremlinPool = gremlinPool();
        return getGremlin(gremlinPool.get(AbstractDungeon.miscRng.random(0, gremlinPool.size() - 1)), x, y);
    }

    public static AbstractMonster getGremlin(String key, float xPos, float yPos) {
        switch (key) {
            case "GremlinWarrior":
                return new GremlinWarrior(xPos, yPos, false);
            case "GremlinThief":
                return new GremlinThief(xPos, yPos, false);
            case "GremlinFat":
                return new GremlinFat(xPos, yPos, false);
            case "GremlinTsundere":
                return new GremlinTsundere(xPos, yPos, false);
            case "GremlinWizard":
                return new GremlinWizard(xPos, yPos, false);
            default:
                logger.info("UNKNOWN GREMLIN: " + key);
                return null;
        }
    }

    private static AbstractMonster getLouse(float x, float y) {
        return AbstractDungeon.miscRng.randomBoolean() ? new FuzzyLouseNormal(x, y) : new FuzzyLouseDefensive(x, y);
    }

    private static AbstractMonster bottomGetStrongWildlife(float x, float y) {
        ArrayList<AbstractMonster> monsters = new ArrayList<>();
        monsters.add(new FungiBeast(x, y));
        monsters.add(new JawWorm(x, y));
        return monsters.get(AbstractDungeon.miscRng.random(0, monsters.size() - 1));
    }

    private static AbstractMonster bottomGetWeakWildlife(float x, float y) {
        ArrayList<AbstractMonster> monsters = new ArrayList<>();
        monsters.add(getLouse(x, y));
        monsters.add(new SpikeSlime_M(x, y, 0, AbstractDungeon.monsterHpRng.random(28, 32)));
        monsters.add(new AcidSlime_M(x, y, 0, AbstractDungeon.monsterHpRng.random(28, 32)));
        return monsters.get(AbstractDungeon.miscRng.random(0, monsters.size() - 1));
    }

    private static AbstractMonster bottomGetStrongHumanoid(float x, float y) {
        ArrayList<AbstractMonster> monsters = new ArrayList<>();
        monsters.add(new Cultist(x, y));
        monsters.add(getSlaver(x, y));
        monsters.add(new Looter(x, y));
        return monsters.get(AbstractDungeon.miscRng.random(0, monsters.size() - 1));
    }

    private static AbstractMonster getSlaver(float x, float y) {
        return AbstractDungeon.miscRng.randomBoolean() ? new SlaverRed(x, y) : new SlaverBlue(x, y);
    }

    private static MonsterGroup bottomHumanoid() {
        AbstractMonster[] monsters = new AbstractMonster[]{bottomGetWeakWildlife(randomXOffset(-160.0F), randomYOffset(20.0F)), bottomGetStrongHumanoid(randomXOffset(130.0F), randomYOffset(20.0F))};
        return new MonsterGroup(monsters);
    }

    private static MonsterGroup bottomWildlife() {
        int numMonster = 2;
        AbstractMonster[] monsters = new AbstractMonster[numMonster];
        monsters[0] = bottomGetStrongWildlife(randomXOffset(-150.0F), randomYOffset(20.0F));
        monsters[1] = bottomGetWeakWildlife(randomXOffset(150.0F), randomYOffset(20.0F));

        return new MonsterGroup(monsters);
    }

    private static float randomYOffset(float y) {
        return y + MathUtils.random(-20.0F, 20.0F);
    }

    private static float randomXOffset(float x) {
        return x + MathUtils.random(-20.0F, 20.0F);
    }

    private static MonsterGroup spawnGremlins() {
        ArrayList<String> gremlinPool = gremlinPool();
        AbstractMonster[] retVal = new AbstractMonster[4];
        int index = AbstractDungeon.miscRng.random(gremlinPool.size() - 1);
        String key = gremlinPool.get(index);
        gremlinPool.remove(index);
        retVal[0] = getGremlin(key, -320.0F, 25.0F);
        index = AbstractDungeon.miscRng.random(gremlinPool.size() - 1);
        key = gremlinPool.get(index);
        gremlinPool.remove(index);
        retVal[1] = getGremlin(key, -160.0F, -12.0F);
        index = AbstractDungeon.miscRng.random(gremlinPool.size() - 1);
        key = gremlinPool.get(index);
        gremlinPool.remove(index);
        retVal[2] = getGremlin(key, 25.0F, -35.0F);
        index = AbstractDungeon.miscRng.random(gremlinPool.size() - 1);
        key = gremlinPool.get(index);
        gremlinPool.remove(index);
        retVal[3] = getGremlin(key, 205.0F, 40.0F);
        return new MonsterGroup(retVal);
    }

    public static MonsterGroup getEncounter(String key) {
        switch (key) {
            case "Looter":
                return new MonsterGroup(new Looter(0.0F, 0.0F));
            case "JawWorm":
                return new MonsterGroup(new JawWorm(0.0F, 25.0F));
            case "Cultist":
                return new MonsterGroup(new Cultist(0.0F, -10.0F));
            case "Blue Slaver":
                return new MonsterGroup(new SlaverBlue(0.0F, 0.0F));

            case "FungiBeast":
                return new MonsterGroup(new AbstractMonster[]{new FungiBeast(-400.0F, 30.0F), new FungiBeast(-40.0F, 20.0F)});
            case "Louse":
                return new MonsterGroup(new AbstractMonster[]{getLouse(-350.0F, 25.0F), getLouse(-125.0F, 10.0F), getLouse(80.0F, 30.0F)});
            case "Gremlins":
                return spawnGremlins();
            case "Exordium Wildlife":
                return bottomWildlife();
            case "Exordium Thugs":
                return bottomHumanoid();
            case "Red Slaver":
                return new MonsterGroup(new SlaverRed(0.0F, 0.0F));
            case "Slime":
                if (MathUtils.randomBoolean()) {
                    return new MonsterGroup(new AcidSlime_L(0.0F, 0.0F, 0));
                }
                return new MonsterGroup(new SpikeSlime_L(0.0F, 0.0F, 0));

            case "GremlinNob":
                return new MonsterGroup(new GremlinNob());
            case "Lagavulin":
                return new MonsterGroup(new Lagavulin(true));
            case "Sentries":
                return new MonsterGroup(new AbstractMonster[]{new Sentry(-330.0F, 25.0F), new Sentry(-85.0F, 10.0F), new Sentry(140.0F, 30.0F)});

            case "Slime Boss":
                return new MonsterGroup(new SlimeBoss());
            case "The Guardian":
                return new MonsterGroup(new TheGuardian());
            case "Hexaghost":
                return new MonsterGroup(new Hexaghost());

            case "The Mushroom Lair":
                return new MonsterGroup(new AbstractMonster[]{new FungiBeast(-450.0F, 30.0F), new FungiBeast(-145.0F, 20.0F), new FungiBeast(180.0F, 15.0F)});
            case "Lagavulin Event":
            case "Lagavulin Dead Adventurers Fight":
                return new MonsterGroup(new Lagavulin(false));
        }

        switch (key) {
            case "SphericGuardian":
                return new MonsterGroup(new SphericGuardian());
            case "3_Byrds":
                return new MonsterGroup(new AbstractMonster[]{new Byrd(-360.0F), new Byrd(-80.0F), new Byrd(200.0F)});
            case "Chosen":
                return new MonsterGroup(new Chosen());
            case "Shelled Parasite":
                return new MonsterGroup(new ShellMonster());
            case "City Looters":
                return new MonsterGroup(new AbstractMonster[]{new Looter(-200.0F, 15.0F), new Mugger(80.0F, 0.0F)});

            case "Murder of Cultists":
                return new MonsterGroup(new AbstractMonster[]{new Cultist(200.0F, -5.0F), new Cultist(-130.0F, 15.0F, false), new Cultist(-465.0F, -20.0F, false)});
            case "Snecko":
                return new MonsterGroup(new Snecko());
            case "SnakePlant":
                return new MonsterGroup(new SnakePlant(-30.0F, -30.0F));
            case "HealerTank":
                return new MonsterGroup(new AbstractMonster[]{new Centurion(-200.0F, 15.0F), new Healer(120.0F, 0.0F)});
            case "Chosen and Flock":
                return new MonsterGroup(new AbstractMonster[]{new Chosen(80.0F, 0.0F), new Byrd(-170.0F)});
            case "City Thugs":
                return new MonsterGroup(new AbstractMonster[]{getSlaver(-355.0F, -30.0F), new ShellMonster(50.0F, 10.0F)});
            case "City Ancients":
                return new MonsterGroup(new AbstractMonster[]{new Sentry(-305.0F, 30.0F), new SphericGuardian()});

            case "SlaverBoss":
                return new MonsterGroup(new AbstractMonster[]{new SlaverBlue(-385.0F, -15.0F), new SlaverBoss(-133.0F, 0.0F), new SlaverRed(125.0F, -30.0F)});
            case "BookOfStabbing":
                return new MonsterGroup(new BookOfStabbing());
            case "Gremlin Leader Combat":
                return new MonsterGroup(new AbstractMonster[]{spawnGremlin(-366.0F, -4.0F), spawnGremlin(-170.0F, 6.0F), new GremlinLeader()});

            case "Automaton":
                return new MonsterGroup(new BronzeAutomaton());
            case "Champ":
                return new MonsterGroup(new Champ());
            case "Collector":
                return new MonsterGroup(new TheCollector());

            case "Masked Bandits":
            case "The Red Mask Bandits":
                return new MonsterGroup(new AbstractMonster[]{new BanditLeader(), new BanditBear(-200.0F, 0.0F), new BanditChild(200.0F, 0.0F)});
        }

        switch (key) {
            case "Flame Bruiser (One Orb)":
                return new MonsterGroup(new AbstractMonster[]{new FlameBruiser(), new FireOrb(180.0F, 50.0F)});
            case "Darkling Encounter":
                return new MonsterGroup(new AbstractMonster[]{new Darkling(-440.0F, 10.0F), new Darkling(-140.0F, 30.0F), new Darkling(180.0F, -5.0F)});
            case "Orb Walker":
                return new MonsterGroup(new OrbWalker(-30.0F, 30.0F));
            case "Ancient Shapes Weak":
                return spawnShapes(true);

            case "Flame Bruiser (Two Orb)":
                return new MonsterGroup(new AbstractMonster[]{new FlameBruiser(), new FireOrb(180.0F, 50.0F), new FireOrb(-180.0F, 80.0F)});
            case "Serpent":
                return new MonsterGroup(new Serpent());
            case "Ancient Shapes":
                return spawnShapes(false);
            case "Maw":
                return new MonsterGroup(new Maw(-70.0F, 20.0F));

            case "Double Orb Walker":
                return new MonsterGroup(new AbstractMonster[]{new OrbWalker(150.0F, 40.0F), new OrbWalker(-250.0F, 10.0F)});
            case "GiantHead":
                return new MonsterGroup(new GiantHead());
            case "Nemesis":
                return new MonsterGroup(new Nemesis());

            case "Awakened One":
                return new MonsterGroup(new AbstractMonster[]{new AwakenedOne(100.0F, 15.0F), new Cultist(-298.0F, -10.0F, false), new Cultist(-590.0F, 10.0F, false)});
            case "Time Eater":
                return new MonsterGroup(new TimeEater());
            case "Donu and Deca":
                return new MonsterGroup(new AbstractMonster[]{new Deca(), new Donu()});
        }

        logger.info("Unspecified key: " + key + " in MonsterHelper.");
        return null;
    }

    @SpirePatch2(clz = MonsterHelper.class, method = "getEncounter", paramtypez = {String.class})
    static class GetEncounter {
        public static SpireReturn<MonsterGroup> Prefix(String key) {
            MonsterGroup monsterGroup = getEncounter(key);
            if (monsterGroup != null) {
                return SpireReturn.Return(monsterGroup);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = MonsterHelper.class, method = "getEncounterName")
    static class GetEncounterName {
        public static String Postfix(String __result, String key) {
            if (__result == null || __result.isEmpty()) {
                return key;
            }
            return __result;
        }
    }
}
