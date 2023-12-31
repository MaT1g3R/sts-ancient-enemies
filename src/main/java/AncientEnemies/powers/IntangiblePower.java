package AncientEnemies.powers;


import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class IntangiblePower extends AbstractPower {
    public static final String POWER_ID = "Intangible";
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final PowerStrings powerStrings;

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Intangible");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

    private boolean justApplied;

    public IntangiblePower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "Intangible";
        this.owner = owner;
        this.updateDescription();
        this.loadRegion("intangible");
        this.priority = 75;
        this.justApplied = true;
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_INTANGIBLE", 0.05F);
    }

    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        if (damage > 1.0F) {
            damage = 1.0F;
        }

        return damage;
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    public void atEndOfTurn(boolean isPlayer) {
        if (this.justApplied) {
            this.justApplied = false;
        } else {
            {
            }
            {

            }

        }
    }
}
