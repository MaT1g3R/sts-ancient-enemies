package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.SporeCloudPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class FungiBeast extends AbstractMonster {
    public static final String ID = "FungiBeast";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("FungiBeast");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP_MAX = 28;
    private static final int HP_MIN = 22;
    private static final float HB_X = 0.0F;
    private static final float HB_Y = -36.0F;
    private static final float HB_W = 260.0F;
    private static final float HB_H = 170.0F;
    private static final byte BITE = 1;
    private static final byte GROW = 2;
    private static final int VULN_AMT = 2;
    private final int biteDamage = 6;
    private final int strAmt = 3;

    public FungiBeast(float x, float y) {
        super(NAME, "FungiBeast", MathUtils.random(22, 28), 0.0F, -36.0F, 260.0F, 170.0F, null, x, y);

        loadAnimation("images/monsters/theBottom/fungi/skeleton.atlas", "images/monsters/theBottom/fungi/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(3.0F);

        this.damage.add(new DamageInfo(this, this.biteDamage));
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new SporeCloudPower(this, 2)));
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;

            case 2:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.strAmt), this.strAmt));
                break;
        }


        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }


    protected void getMove(int num) {
        if (num < 60) {
            if (lastTwoMoves((byte) 1)) {
                setMove(MOVES[0], (byte) 2, AbstractMonster.Intent.BUFF);
            } else {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.damage.get(0).base);

            }

        } else if (lastMove((byte) 2)) {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.damage.get(0).base);
        } else {
            setMove(MOVES[0], (byte) 2, AbstractMonster.Intent.BUFF);
        }
    }
}





