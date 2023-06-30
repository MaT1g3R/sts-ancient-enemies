package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.CurlUpPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class FuzzyLouseNormal extends AbstractMonster {
    public static final String ID = "FuzzyLouseNormal";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("FuzzyLouseNormal");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP_MIN = 10;
    private static final int HP_MAX = 15;
    private static final byte BITE = 3;
    private static final byte STRENGTHEN = 4;
    private static final String CLOSED_STATE = "CLOSED";
    private static final String OPEN_STATE = "OPEN";
    private static final String REAR_IDLE = "REAR_IDLE";
    private static final int STR_AMOUNT = 3;
    private final int biteDamage = MathUtils.random(5, 7);
    private boolean isOpen = true;

    public FuzzyLouseNormal(float x, float y) {
        super(NAME, "FuzzyLouseNormal", MathUtils.random(10, 15), 0.0F, -5.0F, 180.0F, 140.0F, null, x, y);

        loadAnimation("images/monsters/theBottom/louseRed/skeleton.atlas", "images/monsters/theBottom/louseRed/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        this.damage.add(new DamageInfo(this, this.biteDamage));
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new CurlUpPower(this, AbstractDungeon.monsterHpRng.random(3, 7))));
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 3:
                if (!this.isOpen) {
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "OPEN"));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.5F));
                }
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;

            case 4:
                if (!this.isOpen) {
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "REAR"));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(1.2F));
                } else {
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "REAR_IDLE"));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.9F));
                }
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 3), 3));
                break;
        }


        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }


    public void changeState(String stateName) {
        if (stateName.equals("CLOSED")) {
            this.state.setAnimation(0, "transitiontoclosed", false);
            this.state.addAnimation(0, "idle closed", true, 0.0F);
            this.isOpen = false;
        } else if (stateName.equals("OPEN")) {
            this.state.setAnimation(0, "transitiontoopened", false);
            this.state.addAnimation(0, "idle", true, 0.0F);
            this.isOpen = true;
        } else if (stateName.equals("REAR_IDLE")) {
            this.state.setAnimation(0, "rear", false);
            this.state.addAnimation(0, "idle", true, 0.0F);
            this.isOpen = true;
        } else {
            this.state.setAnimation(0, "transitiontoopened", false);
            this.state.addAnimation(0, "rear", false, 0.0F);
            this.state.addAnimation(0, "idle", true, 0.0F);
            this.isOpen = true;
        }
    }


    protected void getMove(int num) {
        if (num < 25) {
            if (lastTwoMoves((byte) 4)) {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK, this.damage.get(0).base);
            } else {
                setMove(MOVES[0], (byte) 4, AbstractMonster.Intent.BUFF);
            }

        } else if (lastTwoMoves((byte) 3)) {
            setMove(MOVES[0], (byte) 4, AbstractMonster.Intent.BUFF);
        } else {
            setMove((byte) 3, AbstractMonster.Intent.ATTACK, this.damage.get(0).base);
        }
    }
}





