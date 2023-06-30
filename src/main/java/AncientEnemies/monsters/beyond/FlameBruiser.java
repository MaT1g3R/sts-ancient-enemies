package AncientEnemies.monsters.beyond;

import AncientEnemies.actions.common.ReviveMonsterAction;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlameBruiser extends AbstractMonster {
    public static final String ID = "FlameBruiser";
    public static final String ONE_ORB = "Flame Bruiser (One Orb)";
    public static final String TWO_ORB = "Flame Bruiser (Two Orb)";
    private static final Logger logger = LogManager.getLogger(FlameBruiser.class.getName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("FlameBruiser");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP_MIN = 120;
    private static final int HP_MAX = 130;
    private static final float HB_X_F = 0.0F;
    private static final float HB_Y_F = 10.0F;
    private static final float HB_W = 220.0F;
    private static final float HB_H = 320.0F;
    private static final int HEAL_AMT = 30;
    private static final int BURN_STRIKE_DMG = 11;
    private static final byte BURN_STRIKE = 1;
    private static final byte SPAWN_FLAME_ORB = 2;
    private static final byte HEAL = 3;
    private static final float pos1X = -180.0F;
    private static final float pos1Y = 80.0F;
    private static final float pos2X = 310.0F;
    private static final float pos2Y = 90.0F;
    private static final float pos3X = -360.0F;
    private static final float pos3Y = 40.0F;
    private boolean firstMove = true;
    public FlameBruiser() {
        super(NAME, "FlameBruiser",
                MathUtils.random(120, 130), 0.0F, 10.0F, 220.0F, 320.0F, "AncientEnemies/fireBro.png", 0.0F, 0.0F);
        this.damage.add(new DamageInfo(this, 11));
    }

    public void usePreBattleAction() {
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.id.equals(this.id)) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new MinionPower(this)));
            }
        }
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Burn(), 1));
                break;
            case 2:
                spawn();
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, 30));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private void spawn() {
        int count = 0;
        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            if (m != this) {
                if (m.isDying) {
                    AbstractDungeon.actionManager.addToBottom(new ReviveMonsterAction(m, this, false));
                    return;
                }
                count++;
            }
        }

        logger.info("SPAWN COUNT: " + count);
        if (count == 1) {
            AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new FireOrb(-180.0F, 80.0F), true));
        } else if (count == 2) {
            AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new FireOrb(310.0F, 90.0F), true));
        } else if (count == 3) {
            AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new FireOrb(-360.0F, 40.0F), true));
        }
    }

    private boolean canSpawn() {
        int aliveCount = 0;
        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            if (m != this && !m.isDying) {
                aliveCount++;
            }
        }
        return aliveCount <= 3;
    }


    public void die() {
        for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
            if (!m.isDead && !m.isDying) {
                AbstractDungeon.actionManager.addToTop(new HideHealthBarAction(m));
                AbstractDungeon.actionManager.addToTop(new SuicideAction(m));
                AbstractDungeon.actionManager.addToTop(new VFXAction(m, new InflameEffect(m), 0.2F));
            }
        }
    }


    protected void getMove(int num) {
        if (this.firstMove) {
            this.firstMove = false;
            setMove((byte) 2, Intent.UNKNOWN);

            return;
        }

        if (num < 33) {
            if (!lastMove((byte) 1)) {
                setMove((byte) 1, Intent.ATTACK, 11, 2, true);
            } else {
                getMove(MathUtils.random(33, 99));
            }

        } else if (num < 66) {
            if (!lastTwoMoves((byte) 2)) {
                if (canSpawn()) {
                    setMove((byte) 2, Intent.UNKNOWN);
                } else {
                    setMove((byte) 3, Intent.BUFF);
                }
            } else {
                setMove((byte) 1, Intent.ATTACK, 11, 2, true);

            }

        } else if (!lastTwoMoves((byte) 3) && this.currentHealth < this.maxHealth / 2) {
            setMove((byte) 3, Intent.BUFF);
        } else {
            getMove(MathUtils.random(65));
        }
    }
}

