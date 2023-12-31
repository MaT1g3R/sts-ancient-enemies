package AncientEnemies.monsters.exordium;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.unique.CannotLoseAction;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake.ShakeDur;
import com.megacrit.cardcrawl.helpers.ScreenShake.ShakeIntensity;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import com.megacrit.cardcrawl.powers.SplitPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

public class SlimeBoss extends AbstractMonster {
    public static final String ID = "SlimeBoss";
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    public static final int HP = 150;
    public static final int TACKLE_DAMAGE = 9;
    public static final int SLAM_DAMAGE = 35;
    public static final int STICKY_TURNS = 2;
    private static final Logger logger = LogManager.getLogger(SlimeBoss.class.getName());
    private static final MonsterStrings monsterStrings;
    private static final byte SLAM = 1;
    private static final byte PREP_SLAM = 2;
    private static final byte SPLIT = 3;
    private static final byte STICKY_TACKLE = 4;
    private static final String SLAM_NAME;
    private static final String PREP_NAME;
    private static final String SPLIT_NAME;
    private static final String STICKY_NAME;

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SlimeBoss");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
        SLAM_NAME = MOVES[0];
        PREP_NAME = MOVES[1];
        SPLIT_NAME = MOVES[2];
        STICKY_NAME = MOVES[3];
    }

    private boolean firstTurn = true;

    public SlimeBoss() {
        super(NAME, "SlimeBoss", 150, 0.0F, -30.0F, 400.0F, 350.0F, null, 0.0F, 28.0F);
        this.type = EnemyType.BOSS;
        this.dialogX = -150.0F * Settings.scale;
        this.dialogY = -70.0F * Settings.scale;
        this.damage.add(new DamageInfo(this, 9));
        this.damage.add(new DamageInfo(this, 35));
        this.powers.add(new SplitPower(this));
        this.loadAnimation("images/monsters/theBottom/boss/slime/skeleton.atlas", "images/monsters/theBottom/boss/slime/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BOTTOM");
        UnlockTracker.markBossAsSeen("SLIME");
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateJumpAction(this));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new WeightyImpactEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, new Color(0.1F, 1.0F, 0.1F, 0.0F))));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.8F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AttackEffect.POISON));
                this.setMove(STICKY_NAME, (byte) 4, Intent.ATTACK_DEBUFF, this.damage.get(0).base);
                break;
            case 2:
                this.playSfx();
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[0], 1.0F, 2.0F));
                AbstractDungeon.actionManager.addToBottom(new ShakeScreenAction(0.3F, ShakeDur.LONG, ShakeIntensity.LOW));
                this.setMove(SLAM_NAME, (byte) 1, Intent.ATTACK, this.damage.get(1).base);
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new CannotLoseAction());
                AbstractDungeon.actionManager.addToBottom(new AnimateShakeAction(this, 1.0F, 0.1F));
                AbstractDungeon.actionManager.addToBottom(new HideHealthBarAction(this));
                AbstractDungeon.actionManager.addToBottom(new SuicideAction(this, false));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(1.0F));
                AbstractDungeon.actionManager.addToBottom(new SFXAction("SLIME_SPLIT"));
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new SpikeSlime_L(-385.0F, 20.0F, 0, this.currentHealth), false));
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new AcidSlime_L(120.0F, -8.0F, 0, this.currentHealth), false));
                AbstractDungeon.actionManager.addToBottom(new CanLoseAction());
                this.setMove(SPLIT_NAME, (byte) 3, Intent.UNKNOWN);
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AttackEffect.POISON));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DrawReductionPower(AbstractDungeon.player, 2), 2));
                this.setMove(PREP_NAME, (byte) 2, Intent.UNKNOWN);
        }

    }

    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_SLIMEBOSS_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_SLIMEBOSS_1B"));
        }

    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (!this.isDying && (float) this.currentHealth < (float) this.maxHealth / 2.0F && this.nextMove != 3) {
            logger.info("SPLIT");
            this.setMove(SPLIT_NAME, (byte) 3, Intent.UNKNOWN);
            this.createIntent();
            AbstractDungeon.actionManager.addToBottom(new TextAboveCreatureAction(this, DIALOG[1]));
            AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, SPLIT_NAME, (byte) 3, Intent.UNKNOWN));
        }

    }

    protected void getMove(int num) {
        if (this.firstTurn) {
            this.firstTurn = false;
            this.setMove(STICKY_NAME, (byte) 4, Intent.ATTACK_DEBUFF, this.damage.get(0).base);
        }
    }

    public void die() {
        super.die();
        CardCrawlGame.sound.play("VO_SLIMEBOSS_2A");
        Iterator var1 = AbstractDungeon.actionManager.actions.iterator();

        AbstractGameAction a;
        do {
            if (!var1.hasNext()) {
                if (this.currentHealth <= 0) {
                    this.useFastShakeAnimation(5.0F);
                    CardCrawlGame.screenShake.rumble(4.0F);
                    ++this.deathTimer;
                    this.onBossVictoryLogic();
                    UnlockTracker.hardUnlockOverride("SLIME");
                    UnlockTracker.unlockAchievement("SLIME_BOSS");
                }

                return;
            }

            a = (AbstractGameAction) var1.next();
        } while (!(a instanceof SpawnMonsterAction));

    }
}
