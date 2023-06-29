package AncientEnemies.patches;

import AncientEnemies.monsters.beyond.*;
import AncientEnemies.monsters.city.BookOfStabbing;
import AncientEnemies.monsters.city.Champ;
import AncientEnemies.monsters.city.SphericGuardian;
import AncientEnemies.monsters.exordium.*;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.city.Chosen;
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
    public static AbstractMonster getAncientShape(float x, float y) {
        switch (AbstractDungeon.miscRng.random(2)) {
            case 0:
                return new Spiker(x, y);
            case 1:
                return new Repulsor(x, y);
            default:
                return new Exploder(x, y);
        }
    }
    private static MonsterGroup spawnShapes(boolean weak) {
        ArrayList<String> shapePool = new ArrayList();
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
    @SpirePatch2(clz = MonsterHelper.class, method = "getEncounter", paramtypez = {String.class})
    static class GetEncounter {
        public static SpireReturn<MonsterGroup> Prefix(String key) {
            MonsterGroup monsterGroup = getEncounter(key);
            if (monsterGroup != null) {
                return SpireReturn.Return(monsterGroup);
            }
            return SpireReturn.Continue();
        }

        private static MonsterGroup getEncounter(String key) {
            switch (key) {
                case "Sphere and 2 Shapes":
                    return new MonsterGroup(new AbstractMonster[]{getAncientShape(-435.0F, 10.0F), getAncientShape(-210.0F, 0.0F), new SphericGuardian(110.0F, 10.0F)});
                case "Awakened One":
                    return new MonsterGroup(new AbstractMonster[]{new Cultist(-590.0F, 10.0F, false), new Cultist(-298.0F, -10.0F, false), new AwakenedOne(100.0F, 15.0F)});
                case "Time Eater":
                    return new MonsterGroup(new TimeEater());
                case "Champ":
                    return new MonsterGroup(new Champ());
                case "AcidSlime_L":
                    return new MonsterGroup(new AcidSlime_L(0.0F, 0.0F, 0));
                case "SpikeSlime_L":
                    return new MonsterGroup(new SpikeSlime_L(0.0F, 0.0F, 0));
                case "Ancient Shapes Weak":
                    return spawnShapes(true);
                case "Spire Growth":
                    return new MonsterGroup(new Serpent());
                case "Book of Stabbing":
                    return new MonsterGroup(new BookOfStabbing());
                case "Nemesis":
                    return new MonsterGroup(new Nemesis());
                case "Slime Boss":
                    return new MonsterGroup(new SlimeBoss());
                case "Cultist":
                    return new MonsterGroup(new Cultist(0.0F, -10.0F));
                case "Cultist and Chosen":
                    return new MonsterGroup(new AbstractMonster[]{new Cultist(-230.0F, 15.0F, false), new Chosen(100.0F, 25.0F)});
                case "3 Cultists":
                    return new MonsterGroup(new AbstractMonster[]{new Cultist(-465.0F, -20.0F, false), new Cultist(-130.0F, 15.0F, false), new Cultist(200.0F, -5.0F)});
                case "3 Sentries":
                case "Sentries":
                    return new MonsterGroup(new AbstractMonster[]{new Sentry(-330.0F, 25.0F), new Sentry(-85.0F, 10.0F), new Sentry(140.0F, 30.0F)});
                case "Sentry and Sphere":
                case "City Ancients":
                    return new MonsterGroup(new AbstractMonster[]{new Sentry(-305.0F, 30.0F), new SphericGuardian()});
                case "SphericGuardian":
                    return new MonsterGroup(new SphericGuardian());
                case "Flame Bruiser 1 Orb":
                    return new MonsterGroup(new AbstractMonster[]{new FlameBruiser(), new FireOrb(210.0F, 50.0F)});
                case "Flame Bruiser 2 Orb":
                case "Reptomancer":
                    return new MonsterGroup(new AbstractMonster[]{new FlameBruiser(), new FireOrb(210.0F, 50.0F), new FireOrb(-220.0F, 90.0F)});
                case "Flame Bruiser (One Orb)":
                    return new MonsterGroup(new AbstractMonster[]{new FlameBruiser(), new FireOrb(180.0F, 50.0F)});
                case "Flame Bruiser (Two Orb)":
                    return new MonsterGroup(new AbstractMonster[]{new FlameBruiser(), new FireOrb(180.0F, 50.0F), new FireOrb(-180.0F, 80.0F)});
                default:
                    return null;
            }
        }

}}
