package AncientEnemies.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.monsters.MonsterInfo;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.monsterList;

public class TheCityPatch {
    public static ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        switch (monsterList.get(monsterList.size() - 1)) {
            case "SphericGuardian":
                retVal.add("City Ancients");
                break;
            case "Byrd":
            case "Chosen":
                retVal.add("Chosen and Flock");
                break;
            case "Shelled Parasite":
                retVal.add("City Thugs");
                break;
        }
        return retVal;
    }

    @SpirePatch(clz = TheCity.class, method = "generateMonsters")
    static class GenerateMonsters {
        public static void Replace(TheCity __instance) {
            ArrayList<MonsterInfo> monsters = new ArrayList<>();
            monsters.add(new MonsterInfo("SphericGuardian", 2.0F));
            monsters.add(new MonsterInfo("3_Byrds", 2.0F));
            monsters.add(new MonsterInfo("Chosen", 2.0F));
            monsters.add(new MonsterInfo("Shelled Parasite", 2.0F));
            monsters.add(new MonsterInfo("City Looters", 2.0F));
            MonsterInfo.normalizeWeights(monsters);
            __instance.populateMonsterList(monsters, 2, false);

            monsters.clear();
            monsters.add(new MonsterInfo("Murder of Cultists", 4.0F));
            monsters.add(new MonsterInfo("Snecko", 4.0F));
            monsters.add(new MonsterInfo("SnakePlant", 6.0F));
            monsters.add(new MonsterInfo("HealerTank", 6.0F));
            monsters.add(new MonsterInfo("Chosen and Flock", 2.0F));
            monsters.add(new MonsterInfo("City Thugs", 2.0F));
            monsters.add(new MonsterInfo("City Ancients", 2.0F));
            MonsterInfo.normalizeWeights(monsters);
            __instance.populateFirstStrongEnemy(monsters, generateExclusions());
            __instance.populateMonsterList(monsters, 12, false);

            monsters.clear();
            monsters.add(new MonsterInfo("SlaverBoss", 1.0F));
            monsters.add(new MonsterInfo("BookOfStabbing", 1.0F));
            monsters.add(new MonsterInfo("Gremlin Leader Combat", 1.0F));

            MonsterInfo.normalizeWeights(monsters);
            __instance.populateMonsterList(monsters, 10, true);
        }
    }
}
