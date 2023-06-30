package AncientEnemies.monsters.beyond;

import AncientEnemies.monsters.exordium.Cultist;
import AncientEnemies.powers.DarknessPower;
import AncientEnemies.powers.RegeneratePower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Bone;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.ClearCardQueueAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.CuriosityPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AwakenedEyeParticle;
import com.megacrit.cardcrawl.vfx.AwakenedWingParticle;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import com.megacrit.cardcrawl.vfx.combat.IntenseZoomEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect.ShockWaveType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class AwakenedOne extends AbstractMonster {
    public static final String ID = "AwakenedOne";
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    public static final int STAGE_1_HP = 300;
    public static final int STAGE_2_HP = 200;
    private static final Logger logger = LogManager.getLogger(AwakenedOne.class.getName());
    private static final MonsterStrings monsterStrings;
    private static final byte SLASH = 1;
    private static final byte SOUL_STRIKE = 2;
    private static final byte REBIRTH = 3;
    private static final String SS_NAME;
    private static final int SLASH_DMG = 10;
    private static final int SS_DMG = 6;
    private static final int SS_AMT = 3;
    private static final int REGEN_AMT = 10;
    private static final int STR_AMT = 3;
    private static final byte DARK_ECHO = 5;
    private static final byte SLUDGE = 6;
    private static final byte CLEANSE = 7;
    private static final byte TACKLE = 8;
    private static final String DARK_ECHO_NAME;
    private static final String CLEANSE_NAME;
    private static final String SLUDGE_NAME;
    private static final int ECHO_DMG = 40;
    private static final int SLUDGE_DMG = 16;
    private static final int TACKLE_DMG = 12;
    private static final int TACKLE_AMT = 2;
    private static final float FIRE_TIME = 0.1F;

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("AwakenedOne");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
        SS_NAME = MOVES[0];
        DARK_ECHO_NAME = MOVES[1];
        CLEANSE_NAME = MOVES[2];
        SLUDGE_NAME = MOVES[3];
    }

    private boolean form1 = true;
    private boolean firstTurn = true;
    private final boolean saidPower = false;
    private int cleanseCount = 3;
    private float fireTimer = 0.0F;
    private final Bone eye;
    private final Bone back;
    private boolean animateParticles = false;
    private final ArrayList<AwakenedWingParticle> wParticles = new ArrayList();

    public AwakenedOne(float x, float y) {
        super(NAME, "AwakenedOne", 300, 40.0F, -30.0F, 460.0F, 250.0F, null, x, y);
        this.loadAnimation("images/monsters/theForest/awakenedOne/skeleton.atlas", "images/monsters/theForest/awakenedOne/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle_1", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle_1", 0.3F);
        this.stateData.setMix("Hit", "Idle_2", 0.2F);
        this.stateData.setMix("Attack_1", "Idle_1", 0.2F);
        this.stateData.setMix("Attack_2", "Idle_2", 0.2F);
        this.state.getData().setMix("Idle_1", "Idle_2", 1.0F);
        this.eye = this.skeleton.findBone("Eye");
        Iterator var4 = this.skeleton.getBones().iterator();

        while (var4.hasNext()) {
            Bone b = (Bone) var4.next();
            logger.info(b.getData().getName());
        }

        this.back = this.skeleton.findBone("Hips");
        this.type = EnemyType.BOSS;
        this.dialogX = -200.0F * Settings.scale;
        this.dialogY = 10.0F * Settings.scale;
        this.damage.add(new DamageInfo(this, 10));
        this.damage.add(new DamageInfo(this, 6));
        this.damage.add(new DamageInfo(this, 40));
        this.damage.add(new DamageInfo(this, 16));
        this.damage.add(new DamageInfo(this, 12));
    }

    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BEYOND");
        AbstractDungeon.getCurrRoom().cannotLose = true;
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new RegeneratePower(this, 10)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new CuriosityPower(this, 3)));
        UnlockTracker.markBossAsSeen("CROW");
    }

    public void takeTurn() {
        int i;
        label28:
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AttackEffect.SLASH_DIAGONAL));
                break;
            case 2:
                i = 0;

                while (true) {
                    if (i >= 3) {
                        break label28;
                    }

                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AttackEffect.FIRE));
                    ++i;
                }
            case 3:
                AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_AWAKENEDONE_1"));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new IntenseZoomEffect(this.hb.cX, this.hb.cY, true), 0.05F, true));
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "REBIRTH"));
            case 4:
            default:
                break;
            case 5:
                this.firstTurn = false;
                AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_AWAKENEDONE_3"));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, new Color(0.1F, 0.0F, 0.2F, 1.0F), ShockWaveType.CHAOTIC), 0.3F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, new Color(0.3F, 0.2F, 0.4F, 1.0F), ShockWaveType.CHAOTIC), 1.0F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AttackEffect.SMASH));
                break;
            case 6:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3), AttackEffect.POISON));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DarknessPower(1), 1));
                break;
            case 7:
                AbstractDungeon.actionManager.addToBottom(new RemoveDebuffsAction(this));
                break;
            case 8:
                for (i = 0; i < 2; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(4), AttackEffect.FIRE));
                }
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    public void changeState(String key) {
        this.maxHealth = 200;
        this.state.setAnimation(0, "Idle_2", true);
        this.halfDead = false;
        this.animateParticles = true;
        AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, this.maxHealth));
        AbstractDungeon.actionManager.addToBottom(new CanLoseAction());
    }

    protected void getMove(int num) {
        if (this.form1) {
            if (this.firstTurn) {
                this.setMove((byte) 1, Intent.ATTACK, 10);
                this.firstTurn = false;
                return;
            }

            if (num < 25) {
                if (!this.lastMove((byte) 2)) {
                    this.setMove(SS_NAME, (byte) 2, Intent.ATTACK, 6, 3, true);
                } else {
                    this.setMove((byte) 1, Intent.ATTACK, 10);
                }
            } else if (!this.lastTwoMoves((byte) 1)) {
                this.setMove((byte) 1, Intent.ATTACK, 10);
            } else {
                this.setMove(SS_NAME, (byte) 2, Intent.ATTACK, 6, 3, true);
            }
        } else {
            if (this.firstTurn) {
                this.setMove(DARK_ECHO_NAME, (byte) 5, Intent.ATTACK, 40);
                return;
            }

            if (this.powers.size() > 2 && this.cleanseCount != 0) {
                --this.cleanseCount;
                this.setMove(CLEANSE_NAME, (byte) 7, Intent.BUFF);
            } else if (num < 50) {
                if (!this.lastTwoMoves((byte) 6)) {
                    this.setMove(SLUDGE_NAME, (byte) 6, Intent.ATTACK_DEBUFF, 16);
                } else {
                    this.setMove((byte) 8, Intent.ATTACK, 12, 2, true);
                }
            } else if (!this.lastTwoMoves((byte) 8)) {
                this.setMove((byte) 8, Intent.ATTACK, 12, 2, true);
            } else {
                this.setMove(SLUDGE_NAME, (byte) 6, Intent.ATTACK_DEBUFF, 16);
            }
        }

    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            Iterator var2 = AbstractDungeon.player.relics.iterator();

            while (var2.hasNext()) {
                AbstractRelic r = (AbstractRelic) var2.next();
                r.onMonsterDeath(this);
            }

            AbstractDungeon.actionManager.addToTop(new ClearCardQueueAction());
            this.powers.clear();
            this.setMove((byte) 3, Intent.UNKNOWN);
            this.createIntent();
            AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[0]));
            AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 3, Intent.UNKNOWN));
            this.firstTurn = true;
            this.form1 = false;
        }

    }

    public void die() {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die();
            this.useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            if (this.saidPower) {
                AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, DIALOG[1], false));
                ++this.deathTimer;
            }

            Iterator var1 = AbstractDungeon.getCurrRoom().monsters.monsters.iterator();

            while (var1.hasNext()) {
                AbstractMonster m = (AbstractMonster) var1.next();
                if (!m.isDying && m instanceof Cultist) {
                    AbstractDungeon.actionManager.addToBottom(new EscapeAction(m));
                }
            }

            this.onBossVictoryLogic();
            UnlockTracker.hardUnlockOverride("CROW");
            UnlockTracker.unlockAchievement("CROW");
        }

    }

    public void update() {
        super.update();
        if (!this.isDying && this.animateParticles) {
            this.fireTimer -= Gdx.graphics.getDeltaTime();
            if (this.fireTimer < 0.0F) {
                this.fireTimer = 0.1F;
                AbstractDungeon.effectList.add(new AwakenedEyeParticle(this.skeleton.getX() + this.eye.getWorldX(), this.skeleton.getY() + this.eye.getWorldY()));
                this.wParticles.add(new AwakenedWingParticle());
            }
        }

        Iterator<AwakenedWingParticle> p = this.wParticles.iterator();

        while (p.hasNext()) {
            AwakenedWingParticle e = p.next();
            e.update();
            if (e.isDone) {
                p.remove();
            }
        }

    }

    public void render(SpriteBatch sb) {
        Iterator var2 = this.wParticles.iterator();

        AwakenedWingParticle p;
        while (var2.hasNext()) {
            p = (AwakenedWingParticle) var2.next();
            if (p.renderBehind) {
                p.render(sb, this.skeleton.getX() + this.back.getWorldX(), this.skeleton.getY() + this.back.getWorldY());
            }
        }

        super.render(sb);
        var2 = this.wParticles.iterator();

        while (var2.hasNext()) {
            p = (AwakenedWingParticle) var2.next();
            if (!p.renderBehind) {
                p.render(sb, this.skeleton.getX() + this.back.getWorldX(), this.skeleton.getY() + this.back.getWorldY());
            }
        }

    }
}
