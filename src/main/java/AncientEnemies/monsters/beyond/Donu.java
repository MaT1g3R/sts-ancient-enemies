package AncientEnemies.monsters.beyond;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class Donu extends AbstractMonster {
    public static final String ID = "Donu";
    public static final int HP = 250;
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Donu");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    private static final String CIRCLE_NAME = MOVES[0];
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final byte BEAM = 0;
    private static final byte TACKLE = 1;
    private static final byte CIRCLE_OF_PROTECTION = 2;
    private static final int ARTIFACT_AMT = 2;
    private static final int BEAM_DMG = 10;
    private static final int BEAM_AMT = 2;
    private static final int TACKLE_DMG = 11;
    private static final int TACKLE_DEBUFF_AMT = 2;
    private static final int CIRCLE_STR_AMT = 3;

    public Donu() {
        super(NAME, "Donu", 250, 0.0F, -20.0F, 390.0F, 390.0F, null, -350.0F, 30.0F);
        loadAnimation("images/monsters/theForest/donu/skeleton.atlas", "images/monsters/theForest/donu/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(0.66F);
        this.type = AbstractMonster.EnemyType.BOSS;

        this.dialogX = -200.0F * Settings.scale;
        this.dialogY = 10.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, 10));
        this.damage.add(new DamageInfo(this, 11));
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ArtifactPower(this, 2)));
    }


    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 0:
                for (i = 0; i < 2; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                            .get(0), AbstractGameAction.AttackEffect.FIRE));
                }
                break;


            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.SMASH));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 2, true), 2));
                break;


            case 2:
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, 3), 3));
                }
                break;
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }


    protected void getMove(int num) {
        if (num < 50 && !lastMove((byte) 1)) {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK_DEBUFF, 11);
        }


        if (GameActionManager.turn % 2 == 0) {
            setMove((byte) 0, AbstractMonster.Intent.ATTACK, 10, 2, true);
        } else {

            setMove(CIRCLE_NAME, (byte) 2, AbstractMonster.Intent.BUFF);
        }
    }


    public void die() {
        super.die();
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            this.deathTimer += 1.5F;
            onBossVictoryLogic();

            UnlockTracker.hardUnlockOverride("DONUT");
            UnlockTracker.unlockAchievement("SHAPES");
        }
    }
}





