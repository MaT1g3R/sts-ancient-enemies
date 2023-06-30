package AncientEnemies.monsters.beyond;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.SlowPower;
import com.megacrit.cardcrawl.powers.WeakPower;

import java.util.ArrayList;

public class GiantHead extends AbstractMonster {
    public static final String ID = "GiantHead";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GiantHead");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP = 500;
    private static final float HB_X_F = 0.0F;
    private static final float HB_Y_F = -40.0F;
    private static final float HB_W = 460.0F;
    private static final float HB_H = 300.0F;
    private static final int COUNT_DMG = 13;
    private static final int DEATH_DMG = 30;
    private static final int GLARE_WEAK = 1;
    private static final int INCREMENT_DMG = 5;
    private static final byte GLARE = 1;
    private static final byte IT_IS_TIME = 2;
    private static final byte COUNT = 3;
    private int count = 5;

    public GiantHead() {
        super(NAME, "GiantHead", 500, 0.0F, -40.0F, 460.0F, 300.0F, null, -70.0F, 40.0F);
        this.type = AbstractMonster.EnemyType.ELITE;
        this.dialogX = -100.0F * Settings.scale;
        this.dialogY -= 20.0F * Settings.scale;

        loadAnimation("images/monsters/theForest/head/skeleton.atlas", "images/monsters/theForest/head/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle_open", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(0.5F);

        this.damage.add(new DamageInfo(this, 13));
        this.damage.add(new DamageInfo(this, 30));
        this.damage.add(new DamageInfo(this, 35));
        this.damage.add(new DamageInfo(this, 40));
        this.damage.add(new DamageInfo(this, 45));
        this.damage.add(new DamageInfo(this, 50));
        this.damage.add(new DamageInfo(this, 55));
        this.damage.add(new DamageInfo(this, 60));
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new SlowPower(this, 0)));
    }

    public void takeTurn() {
        int index;
        switch (this.nextMove) {
            case 1:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, "#r~" +
                        this.count + "...~", 1.7F, 1.7F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 1, true), 1));
                break;


            case 3:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, "#r~" +
                        this.count + "...~", 1.7F, 1.7F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                break;
            case 2:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, getTimeQuote(), 1.7F, 2.0F));
                index = 1 - this.count;
                if (index > 7) {
                    index = 7;
                }
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(index), AbstractGameAction.AttackEffect.SMASH));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GIANTHEAD_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GIANTHEAD_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GIANTHEAD_1C"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_GIANTHEAD_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_GIANTHEAD_2B");
        } else {
            CardCrawlGame.sound.play("VO_GIANTHEAD_2C");
        }
    }

    private String getTimeQuote() {
        ArrayList<String> quotes = new ArrayList<>();
        quotes.add(DIALOG[0]);
        quotes.add(DIALOG[1]);
        quotes.add(DIALOG[2]);
        quotes.add(DIALOG[3]);
        return quotes.get(MathUtils.random(0, quotes.size() - 1));
    }


    public void die() {
        super.die();
        playDeathSfx();
    }


    protected void getMove(int num) {
        if (this.count <= 1) {
            setMove((byte) 2, AbstractMonster.Intent.ATTACK, 30 + (1 - this.count) * 5);
            this.count--;

            return;
        }
        this.count--;


        if (num < 50) {
            if (!lastTwoMoves((byte) 1)) {
                setMove((byte) 1, AbstractMonster.Intent.DEBUFF);
            } else {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK, 13);

            }

        } else if (!lastTwoMoves((byte) 3)) {
            setMove((byte) 3, AbstractMonster.Intent.ATTACK, 13);
        } else {
            setMove((byte) 1, AbstractMonster.Intent.DEBUFF);
        }
    }
}





