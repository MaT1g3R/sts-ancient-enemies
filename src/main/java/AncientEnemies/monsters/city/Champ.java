package AncientEnemies.monsters.city;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateJumpAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.unique.RemoveAllPowersAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.monsters.AbstractMonster.Intent;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Champ extends AbstractMonster {
    private static final Logger logger = LogManager.getLogger(Champ.class.getName());
    public static final String ID = "Champ";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    public static final int HP = 420;
    private static final byte HEAVY_SLASH = 1;
    private static final byte DEFENSIVE_STANCE = 2;
    private static final byte EXECUTE = 3;
    private static final byte FACE_SLAP = 4;
    private static final byte GLOAT = 5;
    private static final byte TAUNT = 6;
    private static final String STANCE_NAME;
    private static final String EXECUTE_NAME;
    private static final String SLAP_NAME;
    public static final int SLASH_DMG = 18;
    public static final int EXECUTE_DMG = 7;
    public static final int SLAP_DMG = 10;
    private static final int BLOCK_AMT = 16;
    private static final int FORGE_AMT = 5;
    private static final int DEBUFF_AMT = 2;
    private static final int STR_AMT = 2;
    private static final int EXEC_COUNT = 3;
    private int numTurns = 0;
    private boolean thresholdReached = false;

    public Champ() {
        super(NAME, "Champ", 420, 0.0F, -60.0F, 400.0F, 410.0F, (String)null, -90.0F, 40.0F);
        this.type = EnemyType.BOSS;
        this.dialogX = -100.0F * Settings.scale;
        this.dialogY = 10.0F * Settings.scale;
        this.loadAnimation("images/monsters/theCity/champ/skeleton.atlas", "images/monsters/theCity/champ/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(1.5F);
        this.damage.add(new DamageInfo(this, 18));
        this.damage.add(new DamageInfo(this, 7));
        this.damage.add(new DamageInfo(this, 10));
    }

    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_CITY");
        UnlockTracker.markBossAsSeen("CHAMP");
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(0), AttackEffect.SLASH_DIAGONAL));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 16));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MetallicizePower(this, 5), 5));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new AnimateJumpAction(this));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(1), AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(1), AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(1), AttackEffect.SLASH_DIAGONAL));
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(2), AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 2, true), 2));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 2, true), 2));
                break;
            case 5:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 2), 2));
                break;
            case 6:
                AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_CHAMP_2A"));
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, this.getTaunt()));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 2, true), 2));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 2, true), 2));
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private String getTaunt() {
        ArrayList<String> derp = new ArrayList();
        derp.add(DIALOG[0]);
        derp.add(DIALOG[1]);
        derp.add(DIALOG[2]);
        derp.add(DIALOG[3]);
        return (String)derp.get(MathUtils.random(derp.size() - 1));
    }

    private String getLimitBreak() {
        ArrayList<String> derp = new ArrayList();
        derp.add(DIALOG[4]);
        derp.add(DIALOG[5]);
        return (String)derp.get(MathUtils.random(derp.size() - 1));
    }

    private String getDeathQuote() {
        ArrayList<String> derp = new ArrayList();
        derp.add(DIALOG[6]);
        derp.add(DIALOG[7]);
        return (String)derp.get(MathUtils.random(derp.size() - 1));
    }

    protected void getMove(int num) {
        logger.info("TURN: " + this.numTurns);
        if (this.numTurns == 3 && this.currentHealth > this.maxHealth / 2) {
            this.setMove((byte)6, Intent.DEBUFF);
            this.numTurns = 0;
        } else {
            if (num >= 80) {
                if (!this.lastTwoMoves((byte)1)) {
                    this.setMove((byte)1, Intent.ATTACK, 18);
                } else {
                    this.getMove(MathUtils.random(0, 79));
                }
            } else if (num >= 70) {
                if (!this.lastMove((byte)2)) {
                    this.setMove(STANCE_NAME, (byte)2, Intent.DEFEND_BUFF);
                } else if (MathUtils.randomBoolean(0.2F)) {
                    this.getMove(99);
                } else {
                    this.getMove(MathUtils.random(0, 69));
                }
            } else if (num >= 50) {
                if (!this.lastMove((byte)5)) {
                    this.setMove((byte)5, Intent.BUFF);
                } else if (MathUtils.randomBoolean()) {
                    this.getMove(MathUtils.random(69, 99));
                } else {
                    this.getMove(MathUtils.random(0, 49));
                }
            } else if (num >= 30) {
                if (!this.lastTwoMoves((byte)4)) {
                    this.setMove(SLAP_NAME, (byte)4, Intent.ATTACK_DEBUFF, 10);
                } else if (this.currentHealth < this.maxHealth / 2 && MathUtils.randomBoolean(0.3F)) {
                    this.getMove(0);
                } else {
                    this.getMove(MathUtils.random(50, 99));
                }
            } else if (!this.lastMove((byte)3) && this.thresholdReached) {
                AbstractDungeon.actionManager.addToTop(new TalkAction(this, this.getDeathQuote(), 2.0F, 2.0F));
                this.setMove(EXECUTE_NAME, (byte)3, Intent.ATTACK, 7, 3, true);
            } else {
                this.getMove(MathUtils.random(31, 99));
            }

            ++this.numTurns;
        }
    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth < this.maxHealth / 2 && !this.thresholdReached) {
            this.thresholdReached = true;
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_CHAMP_1A"));
            AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, this.getLimitBreak(), 2.0F, 3.0F));
            AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new InflameEffect(this), 0.25F));
            AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new InflameEffect(this), 0.25F));
            AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new InflameEffect(this), 0.25F));
            AbstractDungeon.actionManager.addToBottom(new RemoveAllPowersAction(this, true));
        }

    }

    public void die() {
        this.useFastShakeAnimation(5.0F);
        CardCrawlGame.screenShake.rumble(4.0F);
        ++this.deathTimer;
        super.die();
        if (MathUtils.randomBoolean()) {
            CardCrawlGame.sound.play("VO_CHAMP_3A");
        } else {
            CardCrawlGame.sound.play("VO_CHAMP_3B");
        }

        AbstractDungeon.scene.fadeInAmbiance();
        this.onBossVictoryLogic();
        UnlockTracker.hardUnlockOverride("CHAMP");
        UnlockTracker.unlockAchievement("CHAMP");
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Champ");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
        STANCE_NAME = MOVES[0];
        EXECUTE_NAME = MOVES[1];
        SLAP_NAME = MOVES[2];
    }
}
