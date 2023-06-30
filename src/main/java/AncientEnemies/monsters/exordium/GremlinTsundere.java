package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.unique.GainBlockRandomMonsterAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.SpeechBubble;

public class GremlinTsundere extends AbstractMonster {
    public static final String ID = "GremlinTsundere";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinTsundere");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int HP_MIN = 12;

    private static final int HP_MAX = 15;
    private static final int BLOCK_AMOUNT = 7;
    private static final int BASH_DAMAGE = 6;
    private static final byte PROTECT = 1;
    private static final byte BASH = 2;
    private final boolean withNob;
    private boolean firstTurn = true;
    public GremlinTsundere(float x, float y, boolean withNob) {
        super(NAME, "GremlinTsundere", MathUtils.random(12, 15), 0.0F, 0.0F, 120.0F, 200.0F, null, x, y);
        this.withNob = withNob;
        this.dialogY = 60.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, 6));

        loadAnimation("images/monsters/theBottom/femaleGremlin/skeleton.atlas", "images/monsters/theBottom/femaleGremlin/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    public void takeTurn() {
        int aliveCount;
        if (this.firstTurn && this.withNob) {
            AbstractDungeon.effectList.add(new SpeechBubble(this.dialogX, this.dialogY, 3.0F, DIALOG[0], false));
            this.firstTurn = false;
        }
        switch (this.nextMove) {

            case 1:
                AbstractDungeon.actionManager.addToBottom(new GainBlockRandomMonsterAction(this, 7));

                aliveCount = 0;


                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    if (!m.isDying && !m.isEscaping) {
                        aliveCount++;
                    }
                }

                if (this.escapeNext) {
                    AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 99, Intent.ESCAPE));
                    break;
                }
                if (aliveCount > 1) {
                    setMove(MOVES[0], (byte) 1, AbstractMonster.Intent.DEFEND);
                    break;
                }
                setMove(MOVES[1], (byte) 2, AbstractMonster.Intent.ATTACK, 6);
                break;


            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

                if (this.escapeNext) {
                    AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 99, Intent.ESCAPE));
                    break;
                }
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, MOVES[1], (byte) 2, Intent.ATTACK, 6));
                break;


            case 99:
                AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, DIALOG[1], false));

                AbstractDungeon.actionManager.addToBottom(new EscapeAction(this));
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 99, Intent.ESCAPE));
                break;
        }
    }


    public void die() {
        super.die();
    }


    public void escapeNext() {
        if (!this.cannotEscape &&
                !this.escapeNext) {
            this.escapeNext = true;
            AbstractDungeon.effectList.add(new SpeechBubble(this.dialogX, this.dialogY, 3.0F, DIALOG[2], false));
        }
    }


    protected void getMove(int num) {
        setMove(MOVES[0], (byte) 1, AbstractMonster.Intent.DEFEND);
    }


    public void deathReact() {
        if (this.intent != AbstractMonster.Intent.ESCAPE) {
            AbstractDungeon.effectList.add(new SpeechBubble(this.dialogX, this.dialogY, 3.0F, DIALOG[2], false));
            setMove((byte) 99, AbstractMonster.Intent.ESCAPE);
            createIntent();
        }
    }
}





