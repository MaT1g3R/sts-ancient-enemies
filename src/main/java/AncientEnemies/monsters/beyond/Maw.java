package AncientEnemies.monsters.beyond;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class Maw extends AbstractMonster {
    public static final String ID = "Maw";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Maw");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP = 300;
    private static final float HB_X = 0.0F;
    private static final float HB_Y = -40.0F;
    private static final float HB_W = 430.0F;
    private static final float HB_H = 360.0F;
    private static final int TERRIFY_DUR = 3;
    private static final int SLAM_DMG = 25;
    private static final int NOM_DMG = 5;
    private static final int DROOL_STR = 3;
    private static final byte ROAR = 2;
    private static final byte SLAM = 3;
    private static final byte DROOL = 4;
    private static final byte NOMNOMNOM = 5;
    private int turnCount = 1;
    private boolean roared = false;

    public Maw(float x, float y) {
        super(NAME, "Maw", 300, 0.0F, -40.0F, 430.0F, 360.0F, null, x, y);
        this.type = AbstractMonster.EnemyType.ELITE;

        loadAnimation("images/monsters/theForest/maw/skeleton.atlas", "images/monsters/theForest/maw/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.dialogX = -160.0F * Settings.scale;
        this.dialogY = 40.0F * Settings.scale;
        this.damage.add(new DamageInfo(this, 25));
        this.damage.add(new DamageInfo(this, 5));
    }

    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 2:
                AbstractDungeon.actionManager.addToBottom(new SFXAction("MAW_DEATH", 0.1F));
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[0], 1.0F, 2.0F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 3, true), 3));


                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 3, true), 3));


                this.roared = true;
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;

            case 4:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 3), 3));
                break;

            case 5:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                for (i = 0; i < this.turnCount / 2; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                            .get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                }
                break;
        }


        rollMove();
    }


    protected void getMove(int num) {
        this.turnCount++;
        if (!this.roared) {
            setMove((byte) 2, AbstractMonster.Intent.STRONG_DEBUFF);

            return;
        }
        if (num < 50 && !lastMove((byte) 5)) {
            if (this.turnCount / 2 <= 1) {
                setMove((byte) 5, AbstractMonster.Intent.ATTACK, this.damage.get(1).base);
            } else {
                setMove((byte) 5, AbstractMonster.Intent.ATTACK, this.damage.get(1).base, this.turnCount / 2, true);
            }

            return;
        }
        if (lastMove((byte) 3) || lastMove((byte) 5)) {
            setMove((byte) 4, AbstractMonster.Intent.BUFF);
            return;
        }
        setMove((byte) 3, AbstractMonster.Intent.ATTACK, this.damage.get(0).base);
    }


    public void die() {
        super.die();
        CardCrawlGame.sound.play("MAW_DEATH");
    }
}





