package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateHopAction;
import com.megacrit.cardcrawl.actions.animations.SetAnimationAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.ShakeScreenAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;

public class JawWorm extends AbstractMonster {
    public static final String ID = "JawWorm";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("JawWorm");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP_MAX = 44;
    private static final int HP_MIN = 40;
    private static final float HB_X = 0.0F;
    private static final float HB_Y = -25.0F;
    private static final float HB_W = 260.0F;
    private static final float HB_H = 170.0F;
    private static final int CHOMP_DMG = 11;
    private static final int THRASH_DMG = 7;
    private static final int THRASH_BLOCK = 5;
    private static final int BELLOW_STR = 3;
    private static final int BELLOW_BLOCK = 6;
    private static final byte CHOMP = 1;
    private static final byte BELLOW = 2;
    private static final byte THRASH = 3;
    private final int thrashDmg = 7;
    private final int thrashBlock = 5;
    private boolean firstMove = true;

    public JawWorm(float x, float y) {
        super(NAME, "JawWorm", MathUtils.random(40, 44), 0.0F, -25.0F, 260.0F, 170.0F, null, x, y);

        this.damage.add(new DamageInfo(this, 11));
        this.damage.add(new DamageInfo(this, this.thrashDmg));

        loadAnimation("images/monsters/theBottom/jawWorm/skeleton.atlas", "images/monsters/theBottom/jawWorm/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new SetAnimationAction(this, "chomp"));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new BiteEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.3F));

                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.NONE));
                break;
            case 2:
                this.state.setAnimation(0, "tailslam", false);
                this.state.addAnimation(0, "idle", true, 0.0F);
                AbstractDungeon.actionManager.addToBottom(new ShakeScreenAction(0.2F, ScreenShake.ShakeDur.SHORT, ScreenShake.ShakeIntensity.MED));

                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 3), 3));

                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 6));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new AnimateHopAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.thrashBlock));
                break;
        }
        rollMove();
    }


    protected void getMove(int num) {
        if (this.firstMove) {
            this.firstMove = false;
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.damage.get(0).base);

            return;
        }

        if (num < 25) {
            if (lastMove((byte) 1)) {
                if (MathUtils.randomBoolean(0.5625F)) {
                    setMove(MOVES[0], (byte) 2, AbstractMonster.Intent.DEFEND_BUFF);
                } else {
                    setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEFEND, this.thrashDmg);
                }
            } else {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.damage.get(0).base);
            }

        } else if (num < 55) {
            if (lastTwoMoves((byte) 3)) {
                if (MathUtils.randomBoolean(0.357F)) {
                    setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.damage.get(0).base);
                } else {
                    setMove(MOVES[0], (byte) 2, AbstractMonster.Intent.DEFEND_BUFF);
                }
            } else {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEFEND, this.thrashDmg);

            }

        } else if (lastMove((byte) 2)) {
            if (MathUtils.randomBoolean(0.416F)) {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.damage.get(0).base);
            } else {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEFEND, this.thrashDmg);
            }
        } else {
            setMove(MOVES[0], (byte) 2, AbstractMonster.Intent.DEFEND_BUFF);
        }
    }


    public void die() {
        super.die();
        CardCrawlGame.sound.play("JAW_WORM_DEATH");
    }
}





