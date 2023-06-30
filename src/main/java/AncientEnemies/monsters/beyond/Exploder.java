package AncientEnemies.monsters.beyond;

import AncientEnemies.powers.ExplosivePower;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Exploder extends AbstractMonster {
    public static final String ID = "Exploder";
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    public static final String ENCOUNTER_NAME = "Ancient Shapes";
    private static final MonsterStrings monsterStrings;
    private static final int HP_MAX = 54;
    private static final int HP_MIN = 44;
    private static final float HB_X = -8.0F;
    private static final float HB_Y = -10.0F;
    private static final float HB_W = 150.0F;
    private static final float HB_H = 150.0F;
    private static final byte ATTACK = 1;
    private static final int ATTACK_DMG = 9;
    private static final byte BIGGEN = 2;
    private static final int EXPLODE_BASE = 8;
    private static final int EXPLODE_AMP = 4;

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Exploder");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }

    public Exploder(float x, float y) {
        super(NAME, "Exploder", MathUtils.random(44, 54), -8.0F, -10.0F, 150.0F, 150.0F, null, x, y + 10.0F);
        this.loadAnimation("images/monsters/theForest/exploder/skeleton.atlas", "images/monsters/theForest/exploder/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.damage.add(new DamageInfo(this, 9));
    }

    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ExplosivePower(this, 8)));
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AttackEffect.FIRE));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ExplosivePower(this, 4), 4));
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    protected void getMove(int num) {
        if (num < 66) {
            if (!this.lastTwoMoves((byte) 1)) {
                this.setMove((byte) 1, Intent.ATTACK, this.damage.get(0).base);
            } else {
                this.setMove((byte) 2, Intent.BUFF);
            }
        } else if (!this.lastTwoMoves((byte) 2)) {
            this.setMove((byte) 2, Intent.BUFF);
        } else {
            this.setMove((byte) 1, Intent.ATTACK, this.damage.get(0).base);
        }

    }
}
