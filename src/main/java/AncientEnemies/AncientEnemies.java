package AncientEnemies;

import basemod.BaseMod;
import basemod.interfaces.EditStringsSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.localization.PowerStrings;

@SpireInitializer
public class AncientEnemies implements EditStringsSubscriber {
    public AncientEnemies() {
        BaseMod.subscribe(this);
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(
                PowerStrings.class,
                "AncientEnemies/localization/powers.json"
        );
    }

    public static void initialize() {
        AncientEnemies modInitializer = new AncientEnemies();
    }
}
