package AncientEnemies.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.monsters.MonsterInfo;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.monsterList;

public class TheBeyondPatch {
    public static ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        switch (monsterList.get(monsterList.size() - 1)) {
            case "Flame Bruiser (One Orb)":
                retVal.add("Flame Bruiser (Two Orb)");
                break;
            case "Darkling Encounter":
                retVal.add("Darkling Encounter");
                break;
            case "Orb Walker":
                retVal.add("Orb Walker");
                break;
        }
        return retVal;
    }

    @SpirePatch(clz = TheBeyond.class, method = "generateMonsters")
    static class GenerateMonsters {
        public static void Replace(TheBeyond __instance) {
            ArrayList<MonsterInfo> monsters = new ArrayList<>();
            monsters.add(new MonsterInfo("Flame Bruiser (One Orb)", 2.0F));
            monsters.add(new MonsterInfo("Darkling Encounter", 2.0F));
            monsters.add(new MonsterInfo("Orb Walker", 2.0F));
            monsters.add(new MonsterInfo("Ancient Shapes Weak", 2.0F));
            MonsterInfo.normalizeWeights(monsters);
            __instance.populateMonsterList(monsters, 2, false);

            monsters.clear();
            monsters.add(new MonsterInfo("Flame Bruiser (Two Orb)", 1.0F));
            monsters.add(new MonsterInfo("Serpent", 1.0F));
//            monsters.add(new MonsterInfo("Puppeteer", 1.0F));
            monsters.add(new MonsterInfo("Darkling Encounter", 1.0F));
            monsters.add(new MonsterInfo("Ancient Shapes", 2.0F));
            monsters.add(new MonsterInfo("Maw", 1.0F));
            MonsterInfo.normalizeWeights(monsters);
            __instance.populateFirstStrongEnemy(monsters, generateExclusions());
            __instance.populateMonsterList(monsters, 12, false);

            monsters.clear();
            monsters.add(new MonsterInfo("Double Orb Walker", 2.0F));
            monsters.add(new MonsterInfo("Nemesis", 2.0F));
            monsters.add(new MonsterInfo("GiantHead", 2.0F));
            MonsterInfo.normalizeWeights(monsters);

            __instance.populateMonsterList(monsters, 10, true);
        }
    }
}
