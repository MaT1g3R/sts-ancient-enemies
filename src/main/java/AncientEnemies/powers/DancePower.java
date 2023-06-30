package AncientEnemies.powers;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DancePower
        extends AbstractPower {
    public static final String POWER_ID = "Dance Puppet";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Dance Puppet");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private AbstractCard.CardType typeToCompare = AbstractCard.CardType.SKILL;
    private boolean firstTurnApplied = true;


    public DancePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "Dance Puppet";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        this.type = PowerType.DEBUFF;
    }

    public void updateDescription() {
        if (this.typeToCompare == AbstractCard.CardType.SKILL) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
            this.img = ImageMaster.loadImage("images/powers/32/puppetSkill.png");
        } else {
            this.img = ImageMaster.loadImage("images/powers/32/puppetAttack2.png");
            this.description = DESCRIPTIONS[2] + this.amount + DESCRIPTIONS[1];
        }
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type != this.typeToCompare) {
            flash();
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new DamageAction(this.owner, new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
            this.amount++;
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new TextAboveCreatureAction(this.owner, DESCRIPTIONS[3]));
            updateDescription();
        }
    }

    public void atStartOfTurn() {
        if (this.firstTurnApplied) {
            this.firstTurnApplied = false;
        } else {
            flash();
            if (this.typeToCompare == AbstractCard.CardType.SKILL) {
                this.typeToCompare = AbstractCard.CardType.ATTACK;
                updateDescription();
            } else {
                this.typeToCompare = AbstractCard.CardType.SKILL;
                updateDescription();
            }
        }
    }
}

