package AncientEnemies.monsters.beyond;

import AncientEnemies.powers.DancePower;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class Puppeteer extends AbstractMonster {
    public static final String ID = "Puppeteer";
    public static final String IMAGE = "images/monsters/theCity/puppet.png";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Puppeteer");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP = 150;

    private static final float HB_X = 5.0F;
    private static final float HB_Y = 15.0F;
    private static final float HB_W = 180.0F;
    private static final float HB_H = 300.0F;
    private static final int WHIPLASH_DMG = 21;
    private static final int DEBILITATE_DMG = 8;
    private static final int DEBILITATE_VULN = 2;
    private static final int ENTANGLE_DMG = 12;
    private static final int ENTANGLE_WEAK = 2;
    private static final byte WHIPLASH = 1;
    private static final byte ENTANGLE = 2;
    private static final byte DEBILITATE = 3;
    private static final byte DANCE = 4;
    private static final int HEX_AMT = 3;
    private boolean firstMove = true, usedDance = false;
    public Puppeteer() {
        this(0.0F, 0.0F);
    }

    public Puppeteer(float x, float y) {
        super(NAME, "Puppeteer", 150, 5.0F, 15.0F, 180.0F, 300.0F, "images/monsters/theCity/puppet.png", x, -20.0F + y);
        this.dialogX = -10.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, 21));
        this.damage.add(new DamageInfo(this, 8));
        this.damage.add(new DamageInfo(this, 12));
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.3F, 0.5F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 2, true), 2));


                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(2), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;

            case 3:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.SLASH_HEAVY));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 2, true), 2));
                break;


            case 4:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DancePower(AbstractDungeon.player, 3)));
                break;
        }


        rollMove();
    }


    protected void getMove(int num) {
        if (!this.usedDance && this.firstMove) {
            this.usedDance = true;
            this.firstMove = false;
            setMove((byte) 4, AbstractMonster.Intent.STRONG_DEBUFF);

            return;
        }
        if (this.firstMove) {
            this.firstMove = false;
            if (MathUtils.randomBoolean(0.4375F)) {
                setMove((byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, 12);
            } else {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, 8);
            }

            return;
        }

        if (num < 20) {
            if (lastMove((byte) 1)) {
                if (MathUtils.randomBoolean(0.4375F)) {
                    setMove((byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, 12);
                } else {
                    setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, 8);
                }
            } else {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, 21);
            }

        } else if (num < 65) {
            if (lastTwoMoves((byte) 3)) {
                if (MathUtils.randomBoolean(0.36F)) {
                    setMove((byte) 1, AbstractMonster.Intent.ATTACK, 21);
                } else {
                    setMove((byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, 12);
                }
            } else {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, 8);

            }

        } else if (lastTwoMoves((byte) 2)) {
            if (MathUtils.randomBoolean(0.3077F)) {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, 21);
            } else {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, 8);
            }
        } else {
            setMove((byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, 12);
        }
    }
}





