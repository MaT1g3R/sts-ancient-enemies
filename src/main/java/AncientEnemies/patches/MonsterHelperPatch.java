package AncientEnemies.patches;

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
            switch (key) {
                case "3 Sentries":
                case "Sentries":
                    return SpireReturn.Return(new MonsterGroup(new AbstractMonster[]{new AncientEnemies.monsters.exordium.Sentry(-330.0F, 25.0F), new AncientEnemies.monsters.exordium.Sentry(-85.0F, 10.0F), new AncientEnemies.monsters.exordium.Sentry(140.0F, 30.0F)}));
                case "Sentry and Sphere":
                case "City Ancients":
                    return SpireReturn.Return(new MonsterGroup(new AbstractMonster[]{new Sentry(-305.0F, 30.0F), new SphericGuardian()}));
                case "Sphere and 2 Shapes":
                    return SpireReturn.Return(new MonsterGroup(new AbstractMonster[]{MonsterHelper.getAncientShape(-435.0F, 10.0F), MonsterHelper.getAncientShape(-210.0F, 0.0F), new SphericGuardian(110.0F, 10.0F)}));
                case "SphericGuardian":
                    return SpireReturn.Return(new MonsterGroup(new SphericGuardian()));
                default:
                    return SpireReturn.Continue();
            }
        }
    }
}
