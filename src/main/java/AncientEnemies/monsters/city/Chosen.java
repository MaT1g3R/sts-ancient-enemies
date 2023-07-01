package AncientEnemies.monsters.city;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.HexPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class Chosen extends AbstractMonster {
    public static final String ID = "Chosen";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Chosen");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int HP_MIN = 88;
    private static final int HP_MAX = 92;
    private static final float HB_X = 5.0F;
    private static final float HB_Y = -10.0F;
    private static final float HB_W = 200.0F;
    private static final float HB_H = 280.0F;
    private static final int ZAP_DMG = 18;
    private static final int DEBILITATE_DMG = 10;
    private static final int DEBILITATE_VULN = 2;
    private static final int DRAIN_STR = 3;
    private static final int DRAIN_WEAK = 3;
    private static final byte ZAP = 1;
    private static final byte DRAIN = 2;
    private static final byte DEBILITATE = 3;
    private static final byte HEX = 4;
    private static final int HEX_AMT = 1;
    private boolean firstMove = true, usedHex = false;
    public Chosen() {
        this(0.0F, 0.0F);
    }

    public Chosen(float x, float y) {
        super(NAME, "Chosen", MathUtils.random(88, 92), 5.0F, -10.0F, 200.0F, 280.0F, null, x, -20.0F + y);
        this.dialogX = -10.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, 18));
        this.damage.add(new DamageInfo(this, 10));

        loadAnimation("images/monsters/theCity/chosen/skeleton.atlas", "images/monsters/theCity/chosen/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.3F, 0.5F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 3, true), 3));


                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 3), 3));
                break;

            case 3:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.SLASH_HEAVY));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 2, true), 2));
                break;


            case 4:
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0]));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new HexPower(AbstractDungeon.player, 1)));
                break;
        }


        rollMove();
    }


    protected void getMove(int num) {
        if (!this.usedHex && MathUtils.randomBoolean(0.75F)) {
            this.usedHex = true;
            this.firstMove = false;
            setMove((byte) 4, AbstractMonster.Intent.STRONG_DEBUFF);

            return;
        }

        if (this.firstMove) {
            this.firstMove = false;
            if (MathUtils.randomBoolean(0.4375F)) {
                setMove((byte) 2, AbstractMonster.Intent.DEBUFF);
            } else {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, 10);
            }

            return;
        }

        if (num < 20) {
            if (lastMove((byte) 1)) {
                if (MathUtils.randomBoolean(0.4375F)) {
                    setMove((byte) 2, AbstractMonster.Intent.DEBUFF);
                } else {
                    setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, 10);
                }
            } else {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, 18);
            }

        } else if (num < 65) {
            if (lastTwoMoves((byte) 3)) {
                if (MathUtils.randomBoolean(0.36F)) {
                    setMove((byte) 1, AbstractMonster.Intent.ATTACK, 18);
                } else {
                    setMove((byte) 2, AbstractMonster.Intent.DEBUFF);
                }
            } else {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, 10);

            }

        } else if (lastTwoMoves((byte) 2)) {
            if (MathUtils.randomBoolean(0.3077F)) {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, 18);
            } else {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, 10);
            }
        } else {
            setMove((byte) 2, AbstractMonster.Intent.DEBUFF);
        }
    }


    public void die() {
        super.die();
        CardCrawlGame.sound.play("CHOSEN_DEATH");
    }
}





