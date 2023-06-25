package AncientEnemies.actions.common;

import AncientEnemies.monsters.beyond.FireOrb;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.TintEffect;

public class ReviveMonsterAction
        extends AbstractGameAction {
    private boolean healingEffect = false;

    public ReviveMonsterAction(AbstractMonster target, AbstractCreature source, boolean healEffect) {
        setValues((AbstractCreature) target, source, 0);
        this.actionType = AbstractGameAction.ActionType.SPECIAL;
        if (AbstractDungeon.player.hasRelic("Philosopher's Stone")) {
            target.addPower((AbstractPower) new StrengthPower((AbstractCreature) target, 2));
        }

        this.healingEffect = healEffect;
    }

    public ReviveMonsterAction(AbstractMonster target, AbstractCreature source) {
        this(target, source, true);
    }


    public void update() {
        if (this.duration == DEFAULT_DURATION &&
                this.target instanceof AbstractMonster) {
            this.target.isDying = false;
            this.target.heal(this.target.maxHealth, this.healingEffect);
            this.target.healthBarRevivedEvent();
            ((AbstractMonster) this.target).deathTimer = 0.0F;
            ((AbstractMonster) this.target).tint = new TintEffect();
            ((AbstractMonster) this.target).tintFadeOutCalled = false;
            ((AbstractMonster) this.target).isDead = false;
            this.target.powers.clear();

            if (this.target instanceof FireOrb) {
                ((FireOrb) this.target).firstMove = true;
                ((FireOrb) this.target).setImg();
            }

            ((AbstractMonster) this.target).intent = AbstractMonster.Intent.NONE;
            ((AbstractMonster) this.target).rollMove();
        }


        tickDuration();
    }
}