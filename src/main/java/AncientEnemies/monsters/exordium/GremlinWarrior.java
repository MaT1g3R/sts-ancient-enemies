package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AngryPower;
import com.megacrit.cardcrawl.vfx.SpeechBubble;

public class GremlinWarrior extends AbstractMonster {
    public static final String ID = "GremlinWarrior";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinWarrior");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int SCRATCH_DAMAGE = 4;
    private static final byte SCRATCH = 1;
    private static final int HP_MIN = 20;
    private static final int HP_MAX = 24;
    private boolean withNob = false, firstTurn = true;

    public GremlinWarrior(float x, float y, boolean withNob) {
        super(NAME, "GremlinWarrior", MathUtils.random(20, 24), -4.0F, 12.0F, 130.0F, 194.0F, null, x, y);
        this.withNob = withNob;
        this.dialogY = 30.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, 4));

        loadAnimation("images/monsters/theBottom/angryGremlin/skeleton.atlas", "images/monsters/theBottom/angryGremlin/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new AngryPower(this, 1)));
    }


    public void takeTurn() {
        if (this.firstTurn && this.withNob) {
            AbstractDungeon.effectList.add(new SpeechBubble(this.dialogX, this.dialogY, 3.0F, DIALOG[0], false));
            this.firstTurn = false;
        }
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));


                if (this.escapeNext) {
                    AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 99, Intent.ESCAPE));
                    break;
                }
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 1, Intent.ATTACK, 4));
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
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINANGRY_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINANGRY_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINANGRY_1C"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_GREMLINANGRY_2A");
        } else {
            CardCrawlGame.sound.play("VO_GREMLINANGRY_2B");
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
        setMove((byte) 1, AbstractMonster.Intent.ATTACK, 4);
    }


    public void deathReact() {
        if (this.intent != AbstractMonster.Intent.ESCAPE) {
            AbstractDungeon.effectList.add(new SpeechBubble(this.dialogX, this.dialogY, 3.0F, DIALOG[2], false));
            setMove((byte) 99, AbstractMonster.Intent.ESCAPE);
            createIntent();
        }
    }
}





