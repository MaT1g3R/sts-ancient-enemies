package AncientEnemies.patches;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class RepulsePowerPatch extends AbstractPower {
    public static final String POWER_ID = "Repulse";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public RepulsePowerPatch(AbstractCreature owner) {
        this.name = NAME;
        this.owner = owner;
        this.amount = -1;
        this.updateDescription();
        this.img = ImageMaster.loadImage("AncientEnemies/generator.png");
        this.ID = "Repulse";
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + FontHelper.colorString(this.owner.name, "y") + DESCRIPTIONS[1];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Repulse");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}
