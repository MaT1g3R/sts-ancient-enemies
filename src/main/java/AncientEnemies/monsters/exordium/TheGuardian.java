package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ModeShiftPower;
import com.megacrit.cardcrawl.powers.SharpHidePower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;
import com.megacrit.cardcrawl.vfx.combat.IntenseZoomEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TheGuardian extends AbstractMonster {
    public static final String ID = "TheGuardian";
    private static final Logger logger = LogManager.getLogger(TheGuardian.class.getName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("TheGuardian");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    private static final String CLOSEUP_NAME = MOVES[0], FIERCEBASH_NAME = MOVES[1], TWINSLAM_NAME = MOVES[3];
    private static final String WHIRLWIND_NAME = MOVES[4], CHARGEUP_NAME = MOVES[5], VENTSTEAM_NAME = MOVES[6];
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final String DEFENSIVE_MODE = "Defensive Mode";
    private static final String OFFENSIVE_MODE = "Offensive Mode";
    private static final byte CLOSE_UP = 1;
    private static final byte FIERCE_BASH = 2;
    private static final byte ROLL_ATTACK = 3;
    private static final byte TWIN_SLAM = 4;
    private static final byte WHIRLWIND = 5;
    private static final byte CHARGE_UP = 6;
    private static final byte VENT_STEAM = 7;
    private int dmgThreshold = 30;
  private final int dmgThresholdIncrease = 10;
    private int dmgTaken;
    private final int fierceBashDamage = 32;
  private final int whirlwindDamage = 5;
  private final int twinSlamDamage = 8;
  private final int rollDamage = 9;
  private final int whirlwindCount = 4;
    private final int blockAmount = 9;
    private final int thornsDamage = 3;
    private final int VENT_DEBUFF = 2;
    private boolean isOpen = true;
    private boolean closeUpTriggered = false;

    public TheGuardian() {
        super(NAME, "TheGuardian", 220, 0.0F, 95.0F, 440.0F, 350.0F, null, -50.0F, -100.0F);
        this.type = AbstractMonster.EnemyType.BOSS;
        this.dialogX = -100.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, this.fierceBashDamage));
        this.damage.add(new DamageInfo(this, this.rollDamage));
        this.damage.add(new DamageInfo(this, this.whirlwindDamage));
        this.damage.add(new DamageInfo(this, this.twinSlamDamage));

        loadAnimation("images/monsters/theBottom/boss/guardian/skeleton.atlas", "images/monsters/theBottom/boss/guardian/skeleton.json", 2.0F);


        this.state.setAnimation(0, "idle", true);
    }


    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BOTTOM");
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ModeShiftPower(this, this.dmgThreshold)));
        UnlockTracker.markBossAsSeen("GUARDIAN");
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                useCloseUp();
                return;
            case 2:
                useFierceBash();
                return;
            case 7:
                useVentSteam();
                return;
            case 3:
                useRollAttack();
                return;
            case 4:
                useTwinSmash();
                return;
            case 5:
                useWhirlwind();
                return;
            case 6:
                useChargeUp();
                return;
        }
        logger.info("ERROR");
    }


    private void useFierceBash() {
        AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
        AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        setMove(VENTSTEAM_NAME, (byte) 7, AbstractMonster.Intent.STRONG_DEBUFF);
    }

    private void useVentSteam() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.VENT_DEBUFF, true), this.VENT_DEBUFF));


        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, this.VENT_DEBUFF, true), this.VENT_DEBUFF));


        setMove(WHIRLWIND_NAME, (byte) 5, AbstractMonster.Intent.ATTACK, this.whirlwindDamage, this.whirlwindCount, true);
    }

    private void useCloseUp() {
        AbstractDungeon.actionManager.addToBottom(new TextAboveCreatureAction(this, DIALOG[1]));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new SharpHidePower(this, this.thornsDamage)));

        setMove((byte) 3, AbstractMonster.Intent.ATTACK, this.rollDamage);
    }

    private void useTwinSmash() {
        AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "Offensive Mode"));
        AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                .get(3), AbstractGameAction.AttackEffect.SLASH_HEAVY));
        AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                .get(3), AbstractGameAction.AttackEffect.SLASH_HEAVY));
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, "Sharp Hide"));
        setMove(WHIRLWIND_NAME, (byte) 5, AbstractMonster.Intent.ATTACK, this.whirlwindDamage, this.whirlwindCount, true);
    }

    private void useRollAttack() {
        AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
        AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                .get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        setMove(TWINSLAM_NAME, (byte) 4, AbstractMonster.Intent.ATTACK_BUFF, this.twinSlamDamage, 2, true);
    }

    private void useWhirlwind() {
        AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
        for (int i = 0; i < this.whirlwindCount; i++) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("ATTACK_HEAVY"));
            AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new CleaveEffect(true), 0.15F));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                    .get(2), AbstractGameAction.AttackEffect.NONE, true));
        }

        setMove(CHARGEUP_NAME, (byte) 6, AbstractMonster.Intent.DEFEND_BUFF);
    }


    private void useChargeUp() {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.blockAmount));
        AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[2], 1.0F, 2.5F));

        setMove(FIERCEBASH_NAME, (byte) 2, AbstractMonster.Intent.ATTACK, this.fierceBashDamage);
    }


    protected void getMove(int num) {
        if (this.isOpen) {
            setMove(CHARGEUP_NAME, (byte) 6, AbstractMonster.Intent.DEFEND_BUFF);
        } else {
            setMove((byte) 3, AbstractMonster.Intent.ATTACK, this.rollDamage);
        }
    }


    public void changeState(String stateName) {
        switch (stateName) {
            case "Defensive Mode":
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, "Mode Shift"));

                CardCrawlGame.sound.play("GUARDIAN_ROLL_UP");
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.thornsDamage));
                this.stateData.setMix("idle", "transition", 0.1F);
                this.state.setTimeScale(2.0F);
                this.state.setAnimation(0, "transition", false);
                this.state.addAnimation(0, "defensive", true, 0.0F);
                this.dmgThreshold += this.dmgThresholdIncrease;
                setMove(CLOSEUP_NAME, (byte) 1, AbstractMonster.Intent.BUFF);
                createIntent();
                this.isOpen = false;
                updateHitbox(0.0F, 95.0F, 440.0F, 250.0F);
                healthBarUpdatedEvent();
                break;
            case "Offensive Mode":
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ModeShiftPower(this, this.dmgThreshold)));

                if (this.currentBlock != 0) {
                    AbstractDungeon.actionManager.addToBottom(new LoseBlockAction(this, this, this.currentBlock));
                }
                this.stateData.setMix("defensive", "idle", 0.2F);
                this.state.setTimeScale(1.0F);
                this.state.setAnimation(0, "idle", true);
                this.isOpen = true;
                this.closeUpTriggered = false;
                updateHitbox(0.0F, 95.0F, 440.0F, 350.0F);
                healthBarUpdatedEvent();
                break;
        }
    }


    public void damage(DamageInfo info) {
        int tmpHealth = this.currentHealth;
        super.damage(info);

        if (this.isOpen && !this.closeUpTriggered &&
                tmpHealth > this.currentHealth && !this.isDying) {
            this.dmgTaken += tmpHealth - this.currentHealth;
            if (getPower("Mode Shift") != null) {
                (getPower("Mode Shift")).amount -= tmpHealth - this.currentHealth;
                getPower("Mode Shift").updateDescription();
            }

            if (this.dmgTaken >= this.dmgThreshold) {
                this.dmgTaken = 0;
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new IntenseZoomEffect(this.hb.cX, this.hb.cY, false), 0.05F, true));

                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "Defensive Mode"));
                this.closeUpTriggered = true;
            }
        }
    }


    public void render(SpriteBatch sb) {
        super.render(sb);
    }


    public void die() {
        useFastShakeAnimation(5.0F);
        CardCrawlGame.screenShake.rumble(4.0F);
        this.deathTimer += 1.5F;
        super.die();
        onBossVictoryLogic();
        UnlockTracker.hardUnlockOverride("GUARDIAN");
        UnlockTracker.unlockAchievement("GUARDIAN");
    }
}





