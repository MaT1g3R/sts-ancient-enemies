package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.SpeechBubble;

public class GremlinWizard extends AbstractMonster {
    public static final String ID = "GremlinWizard";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinWizard");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int MAGIC_DAMAGE = 25;
    private static final int CHARGE_LIMIT = 3;
    private static final byte DOPE_MAGIC = 1;
    private static final byte CHARGE = 2;
    private int currentCharge = 1;
    private boolean withNob = false, firstTurn = true;
    public GremlinWizard(float x, float y, boolean withNob) {
        super(NAME, "GremlinWizard", 23, 40.0F, -5.0F, 130.0F, 180.0F, null, x - 35.0F, y);
        this.withNob = withNob;

        this.dialogX = 0.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, 25));

        loadAnimation("images/monsters/theBottom/wizardGremlin/skeleton.atlas", "images/monsters/theBottom/wizardGremlin/skeleton.json", 1.0F);


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
                this.currentCharge++;
                AbstractDungeon.actionManager.addToBottom(new TextAboveCreatureAction(this, DIALOG[1]));

                if (this.escapeNext) {
                    AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 99, Intent.ESCAPE));
                    break;
                }
                if (this.currentCharge == 3) {
                    playSfx();
                    AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[2], 1.5F, 3.0F));
                    AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, MOVES[1], (byte) 1, Intent.ATTACK, 25));
                    break;
                }
                setMove(MOVES[0], (byte) 2, AbstractMonster.Intent.UNKNOWN);
                break;


            case 1:
                this.currentCharge = 0;
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                if (this.escapeNext) {
                    AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 99, Intent.ESCAPE));
                    break;
                }
                setMove(MOVES[0], (byte) 2, AbstractMonster.Intent.UNKNOWN);
                break;

            case 99:
                AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, DIALOG[3], false));

                AbstractDungeon.actionManager.addToBottom(new EscapeAction(this));
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 99, Intent.ESCAPE));
                break;
        }
    }


    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINDOPEY_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINDOPEY_1B"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_GREMLINDOPEY_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_GREMLINDOPEY_2B");
        } else {
            CardCrawlGame.sound.play("VO_GREMLINDOPEY_2C");
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
            AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 3.0F, DIALOG[4], false));
        }
    }


    protected void getMove(int num) {
        setMove("Charging", (byte) 2, AbstractMonster.Intent.UNKNOWN);
    }


    public void deathReact() {
        if (this.intent != AbstractMonster.Intent.ESCAPE) {
            AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 3.0F, DIALOG[4], false));
            setMove((byte) 99, AbstractMonster.Intent.ESCAPE);
            createIntent();
        }
    }
}





