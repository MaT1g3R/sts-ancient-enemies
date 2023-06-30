package AncientEnemies.monsters.beyond;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.RegrowPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Darkling extends AbstractMonster {
    public static final String ID = "Darkling";
    public static final String DARKLING_ENCOUNTER = "Darkling Encounter";
    public static final int MIN_HP = 48;
    public static final int MAX_HP = 56;
    private static final Logger logger = LogManager.getLogger(Darkling.class.getName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Darkling");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final float HB_X = 0.0F;
    private static final float HB_Y = -40.0F;
    private static final float HB_W = 260.0F;
    private static final float HB_H = 200.0F;
    private static final int BITE_DMG = 8;
    private static final int DASH_DMG = 8;
    private static final int BLOCK_AMT = 12;
    private static final int CHOMP_AMT = 2;
    private static final byte CHOMP = 1;
    private static final byte HARDEN = 2;
    private static final byte NIP = 3;
    private static final byte COUNT = 4;
    private static final byte REINCARNATE = 5;
    private boolean firstMove = true;
    public Darkling(float x, float y) {
        super(NAME, "Darkling", MathUtils.random(48, 56), 0.0F, -40.0F, 260.0F, 200.0F, null, x, y + 40.0F);

        loadAnimation("images/monsters/theForest/darkling/skeleton.atlas", "images/monsters/theForest/darkling/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(MathUtils.random(1.2F, 2.3F));

        this.dialogX = -50.0F * Settings.scale;
        this.damage.add(new DamageInfo(this, 8));
        this.damage.add(new DamageInfo(this, 8));
    }

    public void usePreBattleAction() {
        (AbstractDungeon.getCurrRoom()).cannotLose = true;
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new RegrowPower(this)));
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;

            case 2:
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 12));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;

            case 4:
                AbstractDungeon.actionManager.addToBottom(new TextAboveCreatureAction(this, DIALOG[0]));
                break;
            case 5:
                if (MathUtils.randomBoolean()) {
                    AbstractDungeon.actionManager.addToBottom(new SFXAction("DARKLING_REGROW_2",
                            MathUtils.random(-0.1F, 0.1F)));
                } else {
                    AbstractDungeon.actionManager.addToBottom(new SFXAction("DARKLING_REGROW_1",
                            MathUtils.random(-0.1F, 0.1F)));
                }
                AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, this.maxHealth / 2));
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "REVIVE"));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new RegrowPower(this), 1));
                if (AbstractDungeon.player.hasRelic("Philosopher's Stone")) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 2), 2));
                }
                break;
        }


        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }


    protected void getMove(int num) {
        if (this.halfDead) {
            setMove((byte) 5, AbstractMonster.Intent.BUFF);

            return;
        }
        if (this.firstMove) {
            if (num < 50) {
                setMove((byte) 2, AbstractMonster.Intent.DEFEND);
            } else {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK, 8);
            }

            this.firstMove = false;

            return;
        }

        if (num < 20) {
            if (!lastMove((byte) 1)) {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, 8, 2, true);
                return;
            }
            getMove(MathUtils.random(20, 99));
            return;
        }
        if (num < 70) {
            if (!lastMove((byte) 2)) {
                setMove((byte) 2, AbstractMonster.Intent.DEFEND);
                return;
            }
            if (MathUtils.randomBoolean(0.4F)) {
                getMove(0);
            } else {
                getMove(70);
            }

            return;
        }
        if (!lastTwoMoves((byte) 3)) {
            setMove((byte) 3, AbstractMonster.Intent.ATTACK, 8);
        }
    }


    public void changeState(String key) {
        if (key.equals("REVIVE")) {
            this.halfDead = false;
        }
    }


    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            this.powers.clear();

            logger.info("This monster is now half dead.");
            boolean allDead = true;
            for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                if (!m.halfDead) {
                    allDead = false;
                    break;
                }
            }

            logger.info("All dead: " + allDead);
            if (!allDead) {
                if (this.nextMove != 4) {
                    setMove((byte) 4, AbstractMonster.Intent.UNKNOWN);
                    createIntent();
                    AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 4, Intent.UNKNOWN));
                }
            } else {
                (AbstractDungeon.getCurrRoom()).cannotLose = false;
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    m.die();
                }
            }
        }
    }


    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose)
            super.die();
    }
}

