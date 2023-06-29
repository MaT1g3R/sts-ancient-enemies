package AncientEnemies.monsters.city;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.monsters.AbstractMonster.Intent;
import com.megacrit.cardcrawl.powers.AttackBurnPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import AncientEnemies.patches.ShriekPowerPatch;
import com.megacrit.cardcrawl.powers.SkillBurnPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BookOfStabbing extends AbstractMonster {
    private static final Logger logger = LogManager.getLogger(BookOfStabbing.class.getName());
    public static final String ID = "BookOfStabbing";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int HP_MAX = 150;
    private static final int HP_MIN = 145;
    private static final int SIPHON_DAMAGE = 25;
    private static final int THRASH_DMG = 5;
    private static final int THRASH_TIMES = 3;
    private static final int UNLEASH_STR = 4;
    private static final int UNLEASH_BLOCK = 20;
    private static final byte STAB = 1;
    private static final byte THRASH = 2;
    private static final byte LAUGH = 3;
    private static final byte UNLEASH = 4;
    private boolean unleashed = false;
    private boolean firstMove = true;

    public BookOfStabbing() {
        super(NAME, "BookOfStabbing", MathUtils.random(145, 150), 0.0F, -30.0F, 320.0F, 410.0F, (String)null, 0.0F, 15.0F);
        this.loadAnimation("images/monsters/theCity/bookOfStabbing/skeleton.atlas", "images/monsters/theCity/bookOfStabbing/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "finger_wiggle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.type = EnemyType.ELITE;
        this.dialogX = -70.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;
        this.damage.add(new DamageInfo(this, 25));
        this.damage.add(new DamageInfo(this, 5));
    }

    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ShriekPowerPatch(this)));
    }

    public void takeTurn() {
        label18:
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(0), AttackEffect.SLASH_VERTICAL));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                int i = 0;

                while(true) {
                    if (i >= 3) {
                        break label18;
                    }

                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(1), AttackEffect.BLUNT_HEAVY));
                    ++i;
                }
            case 3:
                this.randomDebuff();
                this.randomDebuff();
                this.randomDebuff();
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 4), 4));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 20));
        }

        this.rollMove();
    }

    private void randomDebuff() {
        int roll = MathUtils.random(7);
        switch (roll) {
            case 0:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DexterityPower(AbstractDungeon.player, -1), -1));
                break;
            case 1:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new StrengthPower(AbstractDungeon.player, -1), -1));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 1, true), 1));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 1, true), 1));
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DrawReductionPower(AbstractDungeon.player, 1), 1));
                break;
            case 5:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new AttackBurnPower(AbstractDungeon.player, 1), 1));
                break;
            case 6:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new SkillBurnPower(AbstractDungeon.player, 1), 1));
                break;
            case 7:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 1, true), 1));
                break;
            default:
                logger.info("uhh");
        }

    }

    protected void getMove(int num) {
        if (!this.unleashed && this.currentHealth < this.maxHealth / 2) {
            this.unleashed = true;
            this.setMove((byte)4, Intent.DEFEND_BUFF);
        } else if (this.firstMove) {
            this.firstMove = false;
            this.setMove((byte)3, Intent.STRONG_DEBUFF);
        } else {
            if (num < 25) {
                if (this.lastMove((byte)2)) {
                    if (MathUtils.randomBoolean(0.66F)) {
                        this.setMove((byte)1, Intent.ATTACK, 25);
                    } else {
                        this.setMove((byte)3, Intent.STRONG_DEBUFF);
                    }
                } else {
                    this.setMove((byte)2, Intent.ATTACK, 5, 3, true);
                }
            } else if (num < 70) {
                if (this.lastTwoMoves((byte)1)) {
                    if (MathUtils.randomBoolean(0.3846F)) {
                        this.setMove((byte)2, Intent.ATTACK, 5, 3, true);
                    } else {
                        this.setMove((byte)3, Intent.STRONG_DEBUFF);
                    }
                } else {
                    this.setMove((byte)1, Intent.ATTACK, 25);
                }
            } else if (this.lastTwoMoves((byte)3)) {
                if (MathUtils.randomBoolean(0.4545F)) {
                    this.setMove((byte)2, Intent.ATTACK, 5, 3, true);
                } else {
                    this.setMove((byte)1, Intent.ATTACK, 25);
                }
            } else {
                this.setMove((byte)3, Intent.STRONG_DEBUFF);
            }

        }
    }

    public void die() {
        super.die();
        CardCrawlGame.sound.play("STAB_BOOK_DEATH");
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("BookOfStabbing");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}
