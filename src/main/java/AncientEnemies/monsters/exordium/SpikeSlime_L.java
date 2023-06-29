package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateShakeAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.unique.CannotLoseAction;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.Intent;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.SplitPower;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import java.util.Iterator;

public class SpikeSlime_L extends AbstractMonster {
    public static final String ID = "SpikeSlime_L";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    public static final int MIN_HP = 62;
    public static final int MAX_HP = 72;
    public static final int TACKLE_DAMAGE = 16;
    public static final int FRAIL_TURNS = 2;
    public static final int WOUND_COUNT = 2;
    private static final byte FLAME_TACKLE = 1;
    private static final byte SPLIT = 3;
    private static final byte FRAIL_LICK = 4;
    private static final String FRAIL_NAME;
    private static final String SPLIT_NAME;
    private float saveX;
    private float saveY;
    private boolean firstTurn;
    private boolean splitTriggered;

    public SpikeSlime_L(float x, float y, int poisonAmount) {
        this(x, y, poisonAmount, MathUtils.random(62, 72));
    }

    public SpikeSlime_L(float x, float y, int poisonAmount, int newHealth) {
        super(NAME, "SpikeSlime_L", newHealth, 0.0F, -30.0F, 300.0F, 180.0F, (String)null, x, y);
        this.firstTurn = true;
        this.saveX = x;
        this.saveY = y;
        this.splitTriggered = false;
        this.damage.add(new DamageInfo(this, 16));
        this.powers.add(new SplitPower(this));
        if (poisonAmount >= 1) {
            this.powers.add(new PoisonPower(this, this, poisonAmount));
        }

        this.loadAnimation("images/monsters/theBottom/slimeAltL/skeleton.atlas", "images/monsters/theBottom/slimeAltL/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(0), AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Wound(), 2));
            case 2:
            default:
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new CannotLoseAction());
                AbstractDungeon.actionManager.addToBottom(new AnimateShakeAction(this, 1.0F, 0.1F));
                AbstractDungeon.actionManager.addToBottom(new HideHealthBarAction(this));
                AbstractDungeon.actionManager.addToBottom(new SuicideAction(this, false));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(1.0F));
                AbstractDungeon.actionManager.addToBottom(new SFXAction("SLIME_SPLIT"));
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new AncientEnemies.monsters.exordium.SpikeSlime_M(this.saveX - 134.0F, this.saveY + MathUtils.random(-4.0F, 4.0F), 0, this.currentHealth), false));
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new SpikeSlime_M(this.saveX + 134.0F, this.saveY + MathUtils.random(-4.0F, 4.0F), 0, this.currentHealth), false));
                AbstractDungeon.actionManager.addToBottom(new CanLoseAction());
                this.setMove(SPLIT_NAME, (byte)3, Intent.UNKNOWN);
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 2, true), 2));
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (!this.isDying && (float)this.currentHealth < (float)this.maxHealth / 2.0F && this.nextMove != 3 && !this.splitTriggered) {
            this.setMove(SPLIT_NAME, (byte)3, Intent.UNKNOWN);
            this.createIntent();
            AbstractDungeon.actionManager.addToBottom(new TextAboveCreatureAction(this, DIALOG[0]));
            AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, SPLIT_NAME, (byte)3, Intent.UNKNOWN));
            this.splitTriggered = true;
        }

    }

    protected void getMove(int num) {
        if (this.firstTurn) {
            this.firstTurn = false;
            num = MathUtils.random(30, 99);
        }

        if (num < 30) {
            if (this.lastTwoMoves((byte)1)) {
                this.setMove(FRAIL_NAME, (byte)4, Intent.DEBUFF);
            } else {
                this.setMove((byte)1, Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
            }
        } else if (this.lastTwoMoves((byte)4)) {
            this.setMove((byte)1, Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
        } else {
            this.setMove(FRAIL_NAME, (byte)4, Intent.DEBUFF);
        }

    }

    public void die() {
        super.die();
        Iterator var1 = AbstractDungeon.actionManager.actions.iterator();

        AbstractGameAction a;
        do {
            if (!var1.hasNext()) {
                if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() && AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
                    this.onBossVictoryLogic();
                    UnlockTracker.hardUnlockOverride("SLIME");
                }

                return;
            }

            a = (AbstractGameAction)var1.next();
        } while(!(a instanceof SpawnMonsterAction));

    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SpikeSlime_L");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
        FRAIL_NAME = MOVES[0];
        SPLIT_NAME = MOVES[1];
    }
}
