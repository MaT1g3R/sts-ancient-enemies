package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Lagavulin extends AbstractMonster {
    public static final String ID = "Lagavulin";
    private static final Logger logger = LogManager.getLogger(Lagavulin.class.getName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Lagavulin");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    private static final String DEBUFF_NAME = MOVES[0];
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final byte DEBUFF = 1;
    private static final byte STRONG_ATK = 3;
    private static final byte OPEN = 4;
    private static final byte IDLE = 5;
    private static final byte OPEN_NATURAL = 6;
    private static final int STRONG_ATK_DMG = 18;
    private static final int DEBUFF_AMT = -1;
    private static final int ARMOR_AMT = 8;
    private boolean isOut = false;
    private final boolean asleep;
    private boolean isOutTriggered = false;
    private int idleCount = 0, debuffTurnCount = 0;

    public Lagavulin(boolean setAsleep) {
        super(NAME, "Lagavulin", 110, 0.0F, -65.0F, 320.0F, 220.0F, null, 0.0F, 40.0F);
        this.type = AbstractMonster.EnemyType.ELITE;
        this.dialogX = -100.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, 18));
        this.asleep = setAsleep;

        loadAnimation("images/monsters/theBottom/lagavulin/skeleton.atlas", "images/monsters/theBottom/lagavulin/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = null;
        if (!this.asleep) {
            this.isOut = true;
            this.isOutTriggered = true;
            e = this.state.setAnimation(0, "idle_awake", true);
            updateHitbox(0.0F, -65.0F, 320.0F, 370.0F);
        } else {
            e = this.state.setAnimation(0, "idle_asleep", true);
        }

        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void usePreBattleAction() {
        if (this.asleep) {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 8));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MetallicizePower(this, 8), 8));
        } else {

            CardCrawlGame.music.unsilenceBGM();
            AbstractDungeon.scene.fadeOutAmbiance();
            AbstractDungeon.getCurrRoom().playBgmInstantly("ELITE");
            setMove(DEBUFF_NAME, (byte) 1, AbstractMonster.Intent.STRONG_DEBUFF);
        }
    }


    public void takeTurn() {
        switch (this.nextMove) {


            case 1:
                this.debuffTurnCount = 0;
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "DEBUFF"));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DexterityPower(AbstractDungeon.player, -1), -1));


                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new StrengthPower(AbstractDungeon.player, -1), -1));


                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                break;
            case 3:
                this.debuffTurnCount++;
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.35F));
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                break;
            case 5:
                this.idleCount++;
                if (this.idleCount >= 3) {
                    logger.info("idle happened");
                    this.isOutTriggered = true;
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "Open"));
                    AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 3, Intent.ATTACK, 18));
                } else {

                    setMove((byte) 5, AbstractMonster.Intent.SLEEP);
                }
                switch (this.idleCount) {
                    case 1:
                        AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "LURK"));
                        AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[1], 0.5F, 2.0F));
                        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                        break;
                    case 2:
                        AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[2], 0.5F, 2.0F));
                        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                        break;
                }

                break;

            case 4:
                AbstractDungeon.actionManager.addToBottom(new TextAboveCreatureAction(this, DIALOG[0]));
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                break;
            case 6:
                setMove((byte) 3, AbstractMonster.Intent.ATTACK, 18);
                createIntent();
                this.isOutTriggered = true;
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "Whatever"));
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                break;
        }
    }


    public void changeState(String stateName) {
        if (stateName.equals("ATTACK")) {
            this.stateData.setMix("claw_slam", "idle_awake", 0.2F);
            this.state.setAnimation(0, "claw_slam", false);
            AnimationState.TrackEntry e = this.state.addAnimation(0, "idle_awake", true, 0.0F);
            e.setTimeScale(3.0F);
        } else if (stateName.equals("LURK")) {
            this.stateData.setMix("idle_asleep", "almost_awake", 0.1F);
            this.state.setAnimation(0, "almost_awake", true);
        } else if (stateName.equals("DEBUFF")) {
            this.stateData.setMix("intimidation_claws", "idle_awake", 0.2F);
            AnimationState.TrackEntry e = this.state.setAnimation(0, "intimidation_claws", false);
            e.setTimeScale(2.0F);
            e = this.state.addAnimation(0, "idle_awake", true, 0.0F);
            e.setTimeScale(3.0F);
        } else {
            this.isOut = true;
            updateHitbox(0.0F, -65.0F, 320.0F, 360.0F);
            AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[3], 0.5F, 2.0F));
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this, this, "Metallicize", 8));

            CardCrawlGame.music.unsilenceBGM();
            AbstractDungeon.scene.fadeOutAmbiance();
            AbstractDungeon.getCurrRoom().playBgmInstantly("ELITE");
            AnimationState.TrackEntry e = this.state.setAnimation(0, "idle_awake", true);
            e.setTimeScale(3.0F);
        }
    }


    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth != this.maxHealth && !this.isOutTriggered) {
            setMove((byte) 4, AbstractMonster.Intent.STUN);
            createIntent();
            this.isOutTriggered = true;
            AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "Whatever"));
        }
    }


    protected void getMove(int num) {
        if (this.isOut) {
            if (this.debuffTurnCount < 2) {
                if (lastTwoMoves((byte) 3)) {
                    setMove(DEBUFF_NAME, (byte) 1, AbstractMonster.Intent.STRONG_DEBUFF);
                } else {
                    setMove((byte) 3, AbstractMonster.Intent.ATTACK, 18);
                }
            } else {
                setMove(DEBUFF_NAME, (byte) 1, AbstractMonster.Intent.STRONG_DEBUFF);
            }
        } else {
            setMove((byte) 5, AbstractMonster.Intent.SLEEP);
        }
    }


    public void die() {
        super.die();
        AbstractDungeon.scene.fadeInAmbiance();
        CardCrawlGame.music.fadeOutTempBGM();
    }
}





