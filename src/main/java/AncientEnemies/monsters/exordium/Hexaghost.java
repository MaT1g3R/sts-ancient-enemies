package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.BurnIncreaseAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.FireballEffect;
import com.megacrit.cardcrawl.vfx.combat.GhostIgniteEffect;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class Hexaghost extends AbstractMonster {
    public static final String ID = "Hexaghost";
    public static final String IMAGE = "images/monsters/theBottom/boss/ghost/core.png";
    private static final Logger logger = LogManager.getLogger(Hexaghost.class.getName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hexaghost");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    private static final String STRENGTHEN_NAME = MOVES[0];
    private static final String SEAR_NAME = MOVES[1];
    private static final String BURN_NAME = MOVES[2];
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP = 250;
    private static final byte DIVIDER = 1;
    private static final byte TACKLE = 2;
    private static final byte INFLAME = 3;
    private static final byte SEAR = 4;
    private static final byte ACTIVATE = 5;
    private static final byte INFERNO = 6;
    private static final String ACTIVATE_STATE = "Activate";
    private static final String ACTIVATE_ORB = "Activate Orb";
    private static final String DEACTIVATE_ALL_ORBS = "Deactivate";
    private final ArrayList<HexaghostOrb> orbs = new ArrayList<>();
    private final int searDamage = 6;
    private final int strengthenBlockAmt = 12;
    private final int strAmount = 2;
    private final int searBurnCount = 1;
    private final int fireTackleDamage = 5;
    private final int fireTackleCount = 2;
    private final int infernoDamage = 3;
    private final int infernoHits = 6;
    private boolean activated = false;
    private boolean burnUpgraded = false;
    private int orbActiveCount = 0;
    private int strengthTurnCount = 0;
    private final HexaghostBody body;

    public Hexaghost() {
        super(NAME, "Hexaghost", 250, 20.0F, 0.0F, 450.0F, 450.0F, "images/monsters/theBottom/boss/ghost/core.png");
        this.type = AbstractMonster.EnemyType.BOSS;
        this.body = new HexaghostBody(this);
        createOrbs();

        this.damage.add(new DamageInfo(this, this.fireTackleDamage));
        this.damage.add(new DamageInfo(this, this.searDamage));
        this.damage.add(new DamageInfo(this, -1));
        this.damage.add(new DamageInfo(this, this.infernoDamage));
    }


    public void usePreBattleAction() {
        UnlockTracker.markBossAsSeen("GHOST");
    }

    private void createOrbs() {
        this.orbs.add(new HexaghostOrb(-90.0F, 380.0F, this.orbs.size()));
        this.orbs.add(new HexaghostOrb(90.0F, 380.0F, this.orbs.size()));
        this.orbs.add(new HexaghostOrb(160.0F, 250.0F, this.orbs.size()));
        this.orbs.add(new HexaghostOrb(90.0F, 120.0F, this.orbs.size()));
        this.orbs.add(new HexaghostOrb(-90.0F, 120.0F, this.orbs.size()));
        this.orbs.add(new HexaghostOrb(-160.0F, 250.0F, this.orbs.size()));
    }

    public void takeTurn() {
        int d, i;
        Burn c;
        int j;
        this.strengthTurnCount++;
        switch (this.nextMove) {

            case 5:
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "Activate"));
                d = AbstractDungeon.player.currentHealth / 12 + 1;
                this.damage.get(2).base = d;
                applyPowers();
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.damage.get(2).base, 6, true);
                return;


            case 1:
                for (i = 0; i < 6; i++) {
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new GhostIgniteEffect(AbstractDungeon.player.hb.cX +


                            MathUtils.random(-120.0F, 120.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                            MathUtils.random(-120.0F, 120.0F) * Settings.scale), 0.05F));

                    if (MathUtils.randomBoolean()) {
                        AbstractDungeon.actionManager.addToBottom(new SFXAction("GHOST_ORB_IGNITE_1", 0.3F));
                    } else {
                        AbstractDungeon.actionManager.addToBottom(new SFXAction("GHOST_ORB_IGNITE_2", 0.3F));
                    }
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                            .get(2), AbstractGameAction.AttackEffect.BLUNT_HEAVY, true));
                }


                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "Deactivate"));
                rollMove();
                return;


            case 2:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new BorderFlashEffect(Color.CHARTREUSE)));
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "Activate Orb"));
                rollMove();
                return;


            case 4:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new FireballEffect(this.hb.cX, this.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.5F));


                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.FIRE));
                c = new Burn();
                if (this.burnUpgraded) {
                    c.upgrade();
                }
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(c, this.searBurnCount));
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "Activate Orb"));
                rollMove();
                return;


            case 3:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new InflameEffect(this), 0.5F));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.strengthenBlockAmt));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.strAmount), this.strAmount));

                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "Activate Orb"));
                rollMove();
                return;


            case 6:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new ScreenOnFireEffect(), 1.0F));
                for (j = 0; j < this.infernoHits; j++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                            .get(3), AbstractGameAction.AttackEffect.FIRE));
                }
                AbstractDungeon.actionManager.addToBottom(new BurnIncreaseAction());
                if (!this.burnUpgraded) {
                    this.burnUpgraded = true;
                }
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "Deactivate"));
                rollMove();
                return;
        }
        logger.info("ERROR: Default Take Turn was called on " + this.name);
    }


    protected void getMove(int num) {
        if (!this.activated) {
            this.activated = true;
            setMove((byte) 5, AbstractMonster.Intent.UNKNOWN);
        } else {

            if (this.strengthTurnCount >= 4) {
                if (lastMove((byte) 3)) {
                    if (MathUtils.randomBoolean()) {
                        setMove(SEAR_NAME, (byte) 4, AbstractMonster.Intent.ATTACK_DEBUFF, this.searDamage);
                        return;
                    }
                    setMove((byte) 2, AbstractMonster.Intent.ATTACK, this.fireTackleDamage, this.fireTackleCount, true);

                    return;
                }
                setMove(STRENGTHEN_NAME, (byte) 3, AbstractMonster.Intent.DEFEND_BUFF);
                this.strengthTurnCount = 0;


                return;
            }


            if (num >= 50) {
                if (lastTwoMoves((byte) 4)) {
                    if (MathUtils.randomBoolean()) {
                        setMove(STRENGTHEN_NAME, (byte) 3, AbstractMonster.Intent.DEFEND_BUFF);
                    } else {
                        setMove((byte) 2, AbstractMonster.Intent.ATTACK, this.fireTackleDamage, this.fireTackleCount, true);
                    }
                } else {
                    setMove(SEAR_NAME, (byte) 4, AbstractMonster.Intent.ATTACK_DEBUFF, this.searDamage);
                }

            } else if (lastTwoMoves((byte) 2)) {
                if (MathUtils.randomBoolean()) {
                    setMove(SEAR_NAME, (byte) 4, AbstractMonster.Intent.ATTACK_DEBUFF, this.searDamage);
                } else {
                    setMove(STRENGTHEN_NAME, (byte) 3, AbstractMonster.Intent.DEFEND_BUFF);
                }
            } else {
                setMove((byte) 2, AbstractMonster.Intent.ATTACK, this.fireTackleDamage, this.fireTackleCount, true);
            }
        }
    }


    public void changeState(String stateName) {
        switch (stateName) {

            case "Activate":
                AbstractDungeon.scene.fadeOutAmbiance();
                AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BOTTOM");

                for (HexaghostOrb orb : this.orbs) {
                    orb.activate(this.drawX + this.animX, this.drawY + this.animY);
                }
                this.orbActiveCount = 6;
                this.body.targetRotationSpeed = 2.0F;
                break;


            case "Activate Orb":
                for (HexaghostOrb orb : this.orbs) {
                    if (!orb.activated) {
                        orb.activate(this.drawX + this.animX, this.drawY + this.animY);
                        break;
                    }
                }
                this.orbActiveCount++;
                if (this.orbActiveCount == 6) {
                    setMove(BURN_NAME, (byte) 6, AbstractMonster.Intent.ATTACK_DEBUFF, this.infernoDamage, this.infernoHits, true);
                }
                break;


            case "Deactivate":
                for (HexaghostOrb orb : this.orbs) {
                    orb.deactivate();
                }
                CardCrawlGame.sound.play("CARD_EXHAUST", 0.2F);
                CardCrawlGame.sound.play("CARD_EXHAUST", 0.2F);
                this.orbActiveCount = 0;
                break;
        }
    }


    public void die() {
        useFastShakeAnimation(5.0F);
        CardCrawlGame.screenShake.rumble(4.0F);
        this.deathTimer += 1.5F;
        super.die();

        for (HexaghostOrb orb : this.orbs) {
            orb.hide();
        }

        onBossVictoryLogic();

        UnlockTracker.hardUnlockOverride("GHOST");
        UnlockTracker.unlockAchievement("GHOST_GUARDIAN");
    }


    public void update() {
        super.update();
        this.body.update();

        for (HexaghostOrb orb : this.orbs) {
            orb.update(this.drawX + this.animX, this.drawY + this.animY);
        }
    }


    public void render(SpriteBatch sb) {
        this.body.render(sb);
        super.render(sb);
    }
}





