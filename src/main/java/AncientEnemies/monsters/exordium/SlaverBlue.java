package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

public class SlaverBlue extends AbstractMonster {
    public static final String ID = "SlaverBlue";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SlaverBlue");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int HP_MAX = 50;

    private static final int HP_MIN = 46;

    private static final int STAB_DMG = 12;
    private static final byte STAB = 1;
    private static final byte RAKE = 4;
    private final int stabDmg = 12;
    private final int rakeDmg = 7;
    private final int weakAmt = 1;

    public SlaverBlue(float x, float y) {
        super(NAME, "SlaverBlue", MathUtils.random(46, 50), 0.0F, 0.0F, 170.0F, 230.0F, null, x, y);


        this.damage.add(new DamageInfo(this, this.stabDmg));
        this.damage.add(new DamageInfo(this, this.rakeDmg));

        loadAnimation("images/monsters/theBottom/blueSlaver/skeleton.atlas", "images/monsters/theBottom/blueSlaver/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                break;

            case 4:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.weakAmt, true), this.weakAmt));
                break;
        }


        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_SLAVERBLUE_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_SLAVERBLUE_1B"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_SLAVERBLUE_2A");
        } else {
            CardCrawlGame.sound.play("VO_SLAVERBLUE_2B");
        }
    }


    protected void getMove(int num) {
        if (num >= 40 && !lastTwoMoves((byte) 1)) {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.stabDmg);

            return;
        }

        if (!lastTwoMoves((byte) 4)) {
            setMove(MOVES[0], (byte) 4, AbstractMonster.Intent.ATTACK_DEBUFF, this.rakeDmg);
            return;
        }
        setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.stabDmg);
    }


    public void die() {
        super.die();
        playDeathSfx();
    }
}





