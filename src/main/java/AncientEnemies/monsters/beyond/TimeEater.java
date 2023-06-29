package AncientEnemies.monsters.beyond;

import AncientEnemies.patches.TimeWarpPowerPatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.RemoveAllPowersAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect.ShockWaveType;

public class TimeEater extends AbstractMonster {
    public static final String ID = "TimeEater";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    public static final int STAGE_1_HP = 456;
    private static final byte REVERBERATE = 2;
    private static final byte RIPPLE = 3;
    private static final byte HEAD_SLAM = 4;
    private static final byte HASTE = 5;
    private static final int REVERB_DMG = 6;
    private static final int REVERB_AMT = 3;
    private static final int RIPPLE_BLOCK = 20;
    private static final int HEAD_SLAM_DMG = 25;
    private static final int HEAD_SLAM_STICKY = 1;
    private static final int RIPPLE_DEBUFF_TURNS = 1;
    private boolean usedHaste = false;
    private boolean firstTurn = true;

    public TimeEater() {
        super(NAME, "TimeEater", 456, -30.0F, -20.0F, 476.0F, 410.0F, "AncientEnemies/mrSlug.png", 0.0F, 0.0F);
        this.type = EnemyType.BOSS;
        this.dialogX = -200.0F * Settings.scale;
        this.dialogY = 10.0F * Settings.scale;
        this.damage.add(new DamageInfo(this, 6, DamageType.NORMAL));
        this.damage.add(new DamageInfo(this, 25, DamageType.NORMAL));
    }

    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BEYOND");
        UnlockTracker.markBossAsSeen("WIZARD");
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new TimeWarpPowerPatch(this)));
    }

    public void takeTurn() {
        if (this.firstTurn) {
            AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0], 0.5F, 2.0F));
            this.firstTurn = false;
        }

        label21:
        switch (this.nextMove) {
            case 2:
                int i = 0;

                while(true) {
                    if (i >= 3) {
                        break label21;
                    }

                    AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, Settings.BLUE_TEXT_COLOR, ShockWaveType.CHAOTIC), 0.75F));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(0), AttackEffect.FIRE));
                    ++i;
                }
            case 3:
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 20));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 1, true), 1));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 1, true), 1));
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(1), AttackEffect.POISON));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DrawReductionPower(AbstractDungeon.player, 1)));
                break;
            case 5:
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[1], 0.5F, 2.0F));
                AbstractDungeon.actionManager.addToBottom(new RemoveAllPowersAction(this, true));
                AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, this.maxHealth / 2 - this.currentHealth));
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    protected void getMove(int num) {
        if (this.currentHealth < this.maxHealth / 2 && !this.usedHaste) {
            this.usedHaste = true;
            this.setMove((byte)5, Intent.BUFF);
        } else if (num < 45) {
            if (!this.lastTwoMoves((byte)2)) {
                this.setMove((byte)2, Intent.ATTACK, 6, 3, true);
            } else {
                this.getMove(MathUtils.random(50, 99));
            }
        } else if (num < 80) {
            if (!this.lastMove((byte)4)) {
                this.setMove((byte)4, Intent.ATTACK_DEBUFF, 25);
            } else if (MathUtils.randomBoolean(0.66F)) {
                this.setMove((byte)2, Intent.ATTACK, 6, 3, true);
            } else {
                this.setMove((byte)3, Intent.DEFEND_DEBUFF);
            }
        } else if (!this.lastMove((byte)3)) {
            this.setMove((byte)3, Intent.DEFEND_DEBUFF);
        } else {
            this.getMove(MathUtils.random(74));
        }
    }

    public void die() {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            this.useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            ++this.deathTimer;
            super.die();
            this.onBossVictoryLogic();
            UnlockTracker.hardUnlockOverride("WIZARD");
            UnlockTracker.unlockAchievement("TIME_EATER");
        }

    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("TimeEater");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}
