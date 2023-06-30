package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ThieveryPower;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;

public class Looter extends AbstractMonster {
    public static final String ID = "Looter";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Looter");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final String SLASH_MSG1 = DIALOG[0];
    private static final String DEATH_MSG1 = DIALOG[1];
    private static final String SMOKE_BOMB_MSG = DIALOG[2];
    private static final String RUN_MSG = DIALOG[3];
    private static final int HP_MAX = 48;
    private static final int HP_MIN = 44;
    private static final int SWIPE_DMG = 10;
    private static final int STEAL_GOLD_AMT = 15;
    private static final byte MUG = 1;
    private static final byte SMOKE_BOMB = 2;
    private static final byte ESCAPE = 3;
    private static final byte LUNGE = 4;
    private final int swipeDmg = 10;
    private final int lungeDmg = 12;
    private final int escapeDef = 6;
    private int slashCount = 0;

    public Looter(float x, float y) {
        super(NAME, "Looter", MathUtils.random(44, 48), 0.0F, 0.0F, 200.0F, 220.0F, null, x, y);


        this.dialogX = -30.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, this.swipeDmg));
        this.damage.add(new DamageInfo(this, this.lungeDmg));

        loadAnimation("images/monsters/theBottom/looter/skeleton.atlas", "images/monsters/theBottom/looter/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ThieveryPower(this, -1)));
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                if (this.slashCount == 0 && MathUtils.randomBoolean(0.6F)) {
                    AbstractDungeon.actionManager.addToBottom(new TalkAction(this, SLASH_MSG1, 0.3F, 2.0F));
                }

                playSfx();
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), 15));

                this.slashCount++;
                if (this.slashCount == 2) {
                    if (MathUtils.randomBoolean(0.5F)) {
                        setMove((byte) 2, AbstractMonster.Intent.DEFEND);
                        break;
                    }
                    AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, MOVES[0], (byte) 4, Intent.ATTACK, this.damage
                            .get(1).base));
                    break;
                }
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, MOVES[1], (byte) 1, Intent.ATTACK, this.damage
                        .get(0).base));
                break;

            case 4:
                playSfx();
                this.slashCount++;
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(1), 15));
                setMove((byte) 2, AbstractMonster.Intent.DEFEND);
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, SMOKE_BOMB_MSG, 0.75F, 2.5F));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.escapeDef));
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 3, Intent.ESCAPE));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, RUN_MSG, 0.3F, 2.5F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new SmokeBombEffect(this.hb.cX, this.hb.cY)));
                AbstractDungeon.actionManager.addToBottom(new EscapeAction(this));
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 3, Intent.ESCAPE));
                break;
        }
    }


    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_LOOTER_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_LOOTER_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_LOOTER_1C"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_LOOTER_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_LOOTER_2B");
        } else {
            CardCrawlGame.sound.play("VO_LOOTER_2C");
        }
    }


    public void die() {
        playDeathSfx();
        this.state.setTimeScale(0.1F);
        useShakeAnimation(5.0F);
        if (MathUtils.randomBoolean(0.3F)) {
            AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.0F, DEATH_MSG1, false));
            this.deathTimer += 1.5F;
        }
        if (this.slashCount > 0) {
            AbstractDungeon.getCurrRoom().addStolenGoldToRewards(15 * this.slashCount);
        }
        super.die();
    }


    protected void getMove(int num) {
        setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.damage.get(0).base);
    }
}





