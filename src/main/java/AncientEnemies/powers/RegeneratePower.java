package AncientEnemies.powers;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class RegeneratePower extends AbstractPower {
    public static final String POWER_ID = "Regenerate";
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final PowerStrings powerStrings;

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Regenerate");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

    public RegeneratePower(AbstractCreature owner, int regenAmt) {
        this.name = NAME;
        this.ID = "Regenerate";
        this.owner = owner;
        this.amount = regenAmt;
        this.updateDescription();
        this.loadRegion("regen");
        this.type = PowerType.BUFF;
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    public void atEndOfTurn(boolean isPlayer) {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new HealAction(this.owner, this.owner, this.amount));
    }
}
