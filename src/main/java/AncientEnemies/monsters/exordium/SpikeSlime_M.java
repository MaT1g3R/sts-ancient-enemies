package AncientEnemies.monsters.exordium;

import AncientEnemies.AncientEnemies;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class SpikeSlime_M extends AbstractMonster {
    public static final String ID = "SpikeSlime_M";
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    public static final int MIN_HP = 28;
    public static final int MAX_HP = 32;
    public static final int TACKLE_DAMAGE = 8;
    public static final int WOUND_COUNT = 1;
    public static final int FRAIL_TURNS = 1;
    private static final MonsterStrings monsterStrings;
    private static final byte FLAME_TACKLE = 1;
    private static final byte FRAIL_LICK = 4;
    private static final String FRAIL_NAME;

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SpikeSlime_M");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
        FRAIL_NAME = MOVES[0];
    }

    public SpikeSlime_M(float x, float y, int poisonAmount, int newHealth) {
        super(NAME, "SpikeSlime_M", newHealth, 0.0F, -25.0F, 170.0F, 130.0F, null, x, y);

        if (AncientEnemies.afterAscension(2)) {
            this.damage.add(new DamageInfo(this, 10));
        } else {
            this.damage.add(new DamageInfo(this, 8));
        }

        if (poisonAmount >= 1) {
            this.powers.add(new PoisonPower(this, this, poisonAmount));
        }

        this.loadAnimation("images/monsters/theBottom/slimeAltM/skeleton.atlas", "images/monsters/theBottom/slimeAltM/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Wound(), 1));
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 1, true), 1));
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    protected void getMove(int num) {
        if (AncientEnemies.afterAscension(17)) {
            if (num < 30) {
                if (this.lastTwoMoves((byte) 1)) {
                    this.setMove(FRAIL_NAME, (byte) 4, Intent.DEBUFF);
                } else {
                    this.setMove((byte) 1, Intent.ATTACK_DEBUFF, this.damage.get(0).base);
                }
            } else if (this.lastMove((byte) 4)) {
                this.setMove((byte) 1, Intent.ATTACK_DEBUFF, this.damage.get(0).base);
            } else {
                this.setMove(FRAIL_NAME, (byte) 4, Intent.DEBUFF);
            }
        } else if (num < 30) {
            if (this.lastTwoMoves((byte) 1)) {
                this.setMove(FRAIL_NAME, (byte) 4, Intent.DEBUFF);
            } else {
                this.setMove((byte) 1, Intent.ATTACK_DEBUFF, this.damage.get(0).base);
            }
        } else if (this.lastTwoMoves((byte) 4)) {
            this.setMove((byte) 1, Intent.ATTACK_DEBUFF, this.damage.get(0).base);
        } else {
            this.setMove(FRAIL_NAME, (byte) 4, Intent.DEBUFF);
        }

    }

    public void die() {
        super.die();
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() && AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
            this.onBossVictoryLogic();
            UnlockTracker.hardUnlockOverride("SLIME");
        }
    }
}
