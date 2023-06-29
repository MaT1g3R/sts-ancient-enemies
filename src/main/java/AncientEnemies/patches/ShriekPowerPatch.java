package AncientEnemies.patches;

import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ShriekPowerPatch extends AbstractPower {
    public static final String POWER_ID = "Shriek From Beyond";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public ShriekPowerPatch(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "Shriek From Beyond";
        this.owner = owner;
        this.amount = -1;
        this.updateDescription();
        this.img = ImageMaster.loadImage("AncientEnemies/shriek.png");
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == CardType.ATTACK) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new WaitAction(0.5F));
            AbstractDungeon.actionManager.addToBottom(new DiscardAction(AbstractDungeon.player, this.owner, 1, true, false));
        }

    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Shriek From Beyond");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}
