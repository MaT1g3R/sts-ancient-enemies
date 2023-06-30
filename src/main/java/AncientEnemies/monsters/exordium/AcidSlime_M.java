package AncientEnemies.monsters.exordium;

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
import com.megacrit.cardcrawl.helpers.SlimeAnimListener;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class AcidSlime_M extends AbstractMonster {
    public static final String ID = "AcidSlime_M";
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    public static final int MIN_HP = 28;
    public static final int MAX_HP = 32;
    public static final int W_TACKLE_DMG = 7;
    public static final int WOUND_COUNT = 1;
    public static final int N_TACKLE_DMG = 10;
    public static final int WEAK_TURNS = 1;
    private static final MonsterStrings monsterStrings;
    private static final String WOUND_NAME;
    private static final String WEAK_NAME;
    private static final byte WOUND_TACKLE = 1;
    private static final byte NORMAL_TACKLE = 2;
    private static final byte WEAK_LICK = 4;

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("AcidSlime_M");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
        WOUND_NAME = MOVES[0];
        WEAK_NAME = MOVES[1];
    }

    public AcidSlime_M(float x, float y, int poisonAmount, int newHealth) {
        super(NAME, "AcidSlime_M", newHealth, 0.0F, 0.0F, 170.0F, 130.0F, null, x, y);
        this.damage.add(new DamageInfo(this, 7));
        this.damage.add(new DamageInfo(this, 10));
        if (poisonAmount >= 1) {
            this.powers.add(new PoisonPower(this, this, poisonAmount));
        }

        this.loadAnimation("images/monsters/theBottom/slimeM/skeleton.atlas", "images/monsters/theBottom/slimeM/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.state.addListener(new SlimeAnimListener());
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Wound(), 1));
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
            case 3:
            default:
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 1, true), 1));
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
        }

    }

    protected void getMove(int num) {
        if (num < 30) {
            if (this.lastTwoMoves((byte) 1)) {
                if (MathUtils.randomBoolean()) {
                    this.setMove((byte) 2, Intent.ATTACK, this.damage.get(1).base);
                } else {
                    this.setMove(WEAK_NAME, (byte) 4, Intent.DEBUFF);
                }
            } else {
                this.setMove(WOUND_NAME, (byte) 1, Intent.ATTACK_DEBUFF, this.damage.get(0).base);
            }
        } else if (num < 70) {
            if (this.lastMove((byte) 2)) {
                if (MathUtils.randomBoolean(0.4F)) {
                    this.setMove(WOUND_NAME, (byte) 1, Intent.ATTACK_DEBUFF, this.damage.get(0).base);
                } else {
                    this.setMove(WEAK_NAME, (byte) 4, Intent.DEBUFF);
                }
            } else {
                this.setMove((byte) 2, Intent.ATTACK, this.damage.get(1).base);
            }
        } else if (this.lastTwoMoves((byte) 4)) {
            if (MathUtils.randomBoolean(0.4F)) {
                this.setMove(WOUND_NAME, (byte) 1, Intent.ATTACK_DEBUFF, this.damage.get(0).base);
            } else {
                this.setMove((byte) 2, Intent.ATTACK, this.damage.get(1).base);
            }
        } else {
            this.setMove(WEAK_NAME, (byte) 4, Intent.DEBUFF);
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

