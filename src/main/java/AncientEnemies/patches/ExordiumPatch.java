package AncientEnemies.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;
import java.util.Collections;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.bossList;

public class ExordiumPatch {
    public static ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        switch (AbstractDungeon.monsterList.get(AbstractDungeon.monsterList.size() - 1)) {
            case "Looter":
                retVal.add("Exordium Thugs");
                break;
            case "Blue Slaver":
                retVal.add("Red Slaver");
                retVal.add("Exordium Thugs");
                break;
        }
        return retVal;
    }

    @SpirePatch(clz = Exordium.class, method = "generateMonsters")
    static class GenerateMonsters {
        public static void Replace(Exordium __instance) {
            ArrayList<MonsterInfo> monsters = new ArrayList<>();
            monsters.add(new MonsterInfo("Looter", 2.0F));
            monsters.add(new MonsterInfo("JawWorm", 2.0F));
            monsters.add(new MonsterInfo("Cultist", 2.0F));
            monsters.add(new MonsterInfo("Blue Slaver", 2.0F));
            MonsterInfo.normalizeWeights(monsters);
            __instance.populateMonsterList(monsters, 3, false);

            monsters.clear();
            monsters.add(new MonsterInfo("FungiBeast", 2.0F));
            monsters.add(new MonsterInfo("Louse", 2.0F));
            monsters.add(new MonsterInfo("Gremlins", 1.0F));
            monsters.add(new MonsterInfo("Exordium Wildlife", 1.5F));
            monsters.add(new MonsterInfo("Exordium Thugs", 1.5F));
            monsters.add(new MonsterInfo("Red Slaver", 1.0F));
            monsters.add(new MonsterInfo("Slime", 2.0F));
            MonsterInfo.normalizeWeights(monsters);
            __instance.populateFirstStrongEnemy(monsters, ExordiumPatch.generateExclusions());
            __instance.populateMonsterList(monsters, 12, false);

            monsters.clear();
            monsters.add(new MonsterInfo("GremlinNob", 1.0F));
            monsters.add(new MonsterInfo("Lagavulin", 1.0F));
            monsters.add(new MonsterInfo("Sentries", 1.0F));
            MonsterInfo.normalizeWeights(monsters);
            __instance.populateMonsterList(monsters, 5, true);
        }

    }


    @SpirePatch(clz = Exordium.class, method = "initializeBoss")
    static class InitializeBoss {
        public static void Replace(Exordium __instance) {
            if (!UnlockTracker.isBossSeen("GUARDIAN")) {
                bossList.add("The Guardian");
            } else if (!UnlockTracker.isBossSeen("GHOST")) {
                bossList.add("Hexaghost");
            } else if (!UnlockTracker.isBossSeen("SLIME")) {
                bossList.add("Slime Boss");
            } else {
                bossList.add("The Guardian");
                bossList.add("Hexaghost");
                bossList.add("Slime Boss");
                Collections.shuffle(bossList);
            }

            if (Settings.isDemo) {
                bossList.clear();
                bossList.add("Hexaghost");
            }
        }
    }
}
