package AncientEnemies.monsters.beyond;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAndDeckAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.GenericStrengthUpPower;

public class OrbWalker extends AbstractMonster {
    public static final String ID = "Orb Walker";
    public static final String DOUBLE_ENCOUNTER = "Double Orb Walker";
    public static final int LASER_DMG = 10;
    public static final int CLAW_DMG = 15;
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Orb Walker");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP_MIN = 92;
    private static final int HP_MAX = 96;
    private static final byte LASER = 1;
    private static final byte CLAW = 2;

    public OrbWalker(float x, float y) {
        super(NAME, "Orb Walker", MathUtils.random(92, 96), 0.0F, -40.0F, 280.0F, 250.0F, null, x, y);

        this.damage.add(new DamageInfo(this, 10));
        this.damage.add(new DamageInfo(this, 15));

        loadAnimation("images/monsters/theForest/orbWalker/skeleton.atlas", "images/monsters/theForest/orbWalker/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new GenericStrengthUpPower(this, MOVES[0], 3)));
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                break;

            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAndDeckAction(new Burn()));
                break;
        }
        rollMove();
    }


    protected void getMove(int num) {
        if (num < 40) {
            if (!lastTwoMoves((byte) 2)) {
                setMove((byte) 2, AbstractMonster.Intent.ATTACK, 15);
            } else {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK_DEBUFF, 10);
            }

        } else if (!lastTwoMoves((byte) 1)) {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK_DEBUFF, 10);
        } else {
            setMove((byte) 2, AbstractMonster.Intent.ATTACK, 15);
        }
    }
}





