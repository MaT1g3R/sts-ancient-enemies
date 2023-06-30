package AncientEnemies.monsters.city;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class SlaverBoss extends AbstractMonster {
    public static final String ID = "SlaverBoss";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SlaverBoss");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int WHIP_DMG = 14;
    private static final int SCOURING_WHIP_DMG = 8;
    private static final int WOUNDS = 1;
    private static final int STR_AMT = 1;
    private static final byte WHIP = 1;
    private static final byte SCOURING_WHIP = 2;
    private static final byte STRENGTHEN = 3;
    private boolean firstTurn = true;
    public SlaverBoss(float x, float y) {
        super(NAME, "SlaverBoss", 64, -10.0F, -8.0F, 200.0F, 280.0F, null, x, y);
        this.type = AbstractMonster.EnemyType.ELITE;
        this.damage.add(new DamageInfo(this, 14));
        this.damage.add(new DamageInfo(this, 8));

        loadAnimation("images/monsters/theCity/slaverMaster/skeleton.atlas", "images/monsters/theCity/slaverMaster/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    public void takeTurn() {
        switch (this.nextMove) {

            case 1:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                break;

            case 2:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.SLASH_HEAVY));

                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Wound(), 1));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 4));
                for (AbstractMonster mo : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(mo, this, new StrengthPower(mo, 1), 1));
                }
                break;
        }


        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }


    protected void getMove(int num) {
        if (this.firstTurn) {
            this.firstTurn = false;
            if (MathUtils.randomBoolean(0.66F)) {
                setMove((byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, 8);
            } else {
                setMove((byte) 3, AbstractMonster.Intent.DEFEND_BUFF);
            }

            return;
        }

        if (num >= 50) {
            if (lastTwoMoves((byte) 2)) {
                if (MathUtils.randomBoolean(0.5F)) {
                    setMove((byte) 1, AbstractMonster.Intent.ATTACK, 14);
                } else {
                    setMove((byte) 3, AbstractMonster.Intent.DEFEND_BUFF);
                }
            } else {
                setMove((byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, 8);
            }

            return;
        }
        if (num >= 25) {
            if (lastMove((byte) 3)) {
                if (MathUtils.randomBoolean(0.66F)) {
                    setMove((byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, 8);
                } else {
                    setMove((byte) 1, AbstractMonster.Intent.ATTACK, 14);
                }
            } else {
                setMove((byte) 3, AbstractMonster.Intent.DEFEND_BUFF);
            }

            return;
        }

        if (lastMove((byte) 1)) {
            if (MathUtils.randomBoolean(0.66F)) {
                setMove((byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, 8);
            } else {
                setMove((byte) 3, AbstractMonster.Intent.DEFEND_BUFF);
            }
            setMove((byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, 8);
        } else {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, 14);
        }
    }


    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_SLAVERLEADER_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_SLAVERLEADER_1B"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_SLAVERLEADER_2A");
        } else {
            CardCrawlGame.sound.play("VO_SLAVERLEADER_2B");
        }
    }


    public void die() {
        super.die();
        playDeathSfx();
    }
}





