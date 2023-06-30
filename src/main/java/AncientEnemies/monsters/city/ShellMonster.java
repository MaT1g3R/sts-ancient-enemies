package AncientEnemies.monsters.city;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateHopAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;

public class ShellMonster extends AbstractMonster {
    public static final String ID = "Shelled Parasite";
    public static final String ARMOR_BREAK = "ARMOR_BREAK";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Shelled Parasite");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP_MIN = 60;
    private static final int HP_MAX = 65;
    private static final float HB_X_F = 0.0F;
    private static final float HB_Y_F = -16.0F;
    private static final float HB_W = 350.0F;
    private static final float HB_H = 260.0F;
    private static final int PLATED_ARMOR_AMT = 14;
    private static final int FELL_DMG = 18;
    private static final int DOUBLE_STRIKE_DMG = 6;
    private static final int SUCK_DMG = 9;
    private static final int DOUBLE_STRIKE_COUNT = 2;
    private static final int FELL_FRAIL_AMT = 2;
    private static final byte FELL = 1;
    private static final byte DOUBLE_STRIKE = 2;
    private static final byte LIFE_SUCK = 3;
    private static final byte STUNNED = 4;
    private boolean firstMove = true;
    public ShellMonster(float x, float y) {
        super(NAME, "Shelled Parasite", MathUtils.random(60, 65), 0.0F, -16.0F, 350.0F, 260.0F, null, x, y);

        loadAnimation("images/monsters/theCity/shellMonster/skeleton.atlas", "images/monsters/theCity/shellMonster/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(1.3F);

        this.dialogX = -50.0F * Settings.scale;
        this.damage.add(new DamageInfo(this, 6));
        this.damage.add(new DamageInfo(this, 18));
        this.damage.add(new DamageInfo(this, 9));
    }

    public ShellMonster() {
        this(0.0F, 10.0F);
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new PlatedArmorPower(this, 14)));

        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 14));
    }

    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));


                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 2, true), 2));
                break;


            case 2:
                for (i = 0; i < 2; i++) {
                    AbstractDungeon.actionManager.addToBottom(new AnimateHopAction(this));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.2F));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                            .get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                }
                break;

            case 3:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new VampireDamageAction(AbstractDungeon.player, this.damage


                        .get(2), AbstractGameAction.AttackEffect.POISON));
                break;

            case 4:
                setMove((byte) 1, AbstractMonster.Intent.ATTACK_DEBUFF, 18);
                AbstractDungeon.actionManager.addToBottom(new TextAboveCreatureAction(this, "Stunned!"));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }


    public void changeState(String stateName) {
      if (stateName.equals("ARMOR_BREAK")) {
        AbstractDungeon.actionManager.addToBottom(new AnimateHopAction(this));
        AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3F));
        AbstractDungeon.actionManager.addToBottom(new AnimateHopAction(this));
        AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3F));
        AbstractDungeon.actionManager.addToBottom(new AnimateHopAction(this));
        setMove((byte) 4, Intent.STUN);
        createIntent();
      }
    }


    protected void getMove(int num) {
        if (this.firstMove) {
            this.firstMove = false;
            if (MathUtils.randomBoolean()) {
                setMove((byte) 2, AbstractMonster.Intent.ATTACK, 6, 2, true);
            } else {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK_BUFF, 9);
            }

            return;
        }

        if (num < 20) {
            if (!lastMove((byte) 1)) {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK_DEBUFF, 18);
            } else {
                getMove(MathUtils.random(20, 99));
            }

        } else if (num < 60) {
            if (!lastTwoMoves((byte) 2)) {
                setMove((byte) 2, AbstractMonster.Intent.ATTACK, 6, 2, true);
            } else {
                setMove((byte) 3, AbstractMonster.Intent.ATTACK_BUFF, 9);

            }

        } else if (!lastTwoMoves((byte) 3)) {
            setMove((byte) 3, AbstractMonster.Intent.ATTACK_BUFF, 9);
        } else {
            setMove((byte) 2, AbstractMonster.Intent.ATTACK, 6, 2, true);
        }
    }
}





