package AncientEnemies.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.monsters.MonsterInfo;

import java.util.ArrayList;

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
}
