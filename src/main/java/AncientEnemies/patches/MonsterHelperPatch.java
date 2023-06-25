package AncientEnemies.patches;

import AncientEnemies.monsters.beyond.FireOrb;
import AncientEnemies.monsters.beyond.FlameBruiser;
import AncientEnemies.monsters.exordium.Sentry;
import AncientEnemies.monsters.city.SphericGuardian;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class MonsterHelperPatch {
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
                case "3 Sentries":
                case "Sentries":
                    return new MonsterGroup(new AbstractMonster[]{new Sentry(-330.0F, 25.0F), new Sentry(-85.0F, 10.0F), new Sentry(140.0F, 30.0F)});
                case "Sentry and Sphere":
                case "City Ancients":
                    return new MonsterGroup(new AbstractMonster[]{new Sentry(-305.0F, 30.0F), new SphericGuardian()});
                case "Sphere and 2 Shapes":
                    return new MonsterGroup(new AbstractMonster[]{MonsterHelper.getAncientShape(-435.0F, 10.0F), MonsterHelper.getAncientShape(-210.0F, 0.0F), new SphericGuardian(110.0F, 10.0F)});
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
    }
}
