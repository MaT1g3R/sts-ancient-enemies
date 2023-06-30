package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.SpeechBubble;

public class GremlinFat extends AbstractMonster {
    public static final String ID = "GremlinFat";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinFat");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP_MIN = 13;
    private static final int HP_MAX = 17;
    private static final int BLUNT_DAMAGE = 4;
    private static final int WEAK_AMT = 1;
    private static final byte BLUNT = 2;
    private boolean withNob = false, firstTurn = true;

    public GremlinFat(float x, float y, boolean withNob) {
        super(NAME, "GremlinFat", MathUtils.random(13, 17), 0.0F, 0.0F, 110.0F, 220.0F, null, x, y);
        this.withNob = withNob;

        this.dialogY = 30.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, 4));

        loadAnimation("images/monsters/theBottom/fatGremlin/skeleton.atlas", "images/monsters/theBottom/fatGremlin/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "animation", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void takeTurn() {
        if (this.firstTurn && this.withNob) {
            AbstractDungeon.effectList.add(new SpeechBubble(this.dialogX, this.dialogY, 3.0F, DIALOG[0], false));
            this.firstTurn = false;
        }
        switch (this.nextMove) {
            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 1, true), 1));


                if (this.escapeNext) {
                    AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 99, Intent.ESCAPE));
                    break;
                }
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                break;

            case 99:
                playSfx();
                AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, DIALOG[1], false));

                AbstractDungeon.actionManager.addToBottom(new EscapeAction(this));
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 99, Intent.ESCAPE));
                break;
        }
    }


    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINFAT_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINFAT_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINFAT_1C"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_GREMLINFAT_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_GREMLINFAT_2B");
        } else {
            CardCrawlGame.sound.play("VO_GREMLINFAT_2C");
        }
    }


    public void die() {
        super.die();
        playDeathSfx();
    }


    public void escapeNext() {
        if (!this.cannotEscape &&
                !this.escapeNext) {
            this.escapeNext = true;
            AbstractDungeon.effectList.add(new SpeechBubble(this.dialogX, this.dialogY, 3.0F, DIALOG[2], false));
        }
    }


    protected void getMove(int num) {
        setMove(MOVES[0], (byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, 4);
    }


    public void deathReact() {
        if (this.intent != AbstractMonster.Intent.ESCAPE) {
            AbstractDungeon.effectList.add(new SpeechBubble(this.dialogX, this.dialogY, 3.0F, DIALOG[2], false));
            setMove((byte) 99, AbstractMonster.Intent.ESCAPE);
            createIntent();
        }
    }
}





