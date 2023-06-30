package AncientEnemies.monsters.city;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.FrailPower;

public class SphericGuardian extends AbstractMonster {
    public static final String ID = "SphericGuardian";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SphericGuardian");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final float HB_X = 0.0F;
    private static final float HB_Y = 10.0F;
    private static final float HB_W = 280.0F;
    private static final float HB_H = 280.0F;
    private static final int SLAM_DMG = 20;
    private static final int DEBUFF_DMG = 15;
    private static final int HARDEN_DMG = 10;
    private static final int HARDEN_BLOCK = 20;
    private static final int FRAIL_AMT = 5;
    private static final byte SLAM = 1;
    private static final byte ACTIVATE_CUBE = 2;
    private static final byte HARDEN = 3;
    private static final byte DEBUFF = 4;
    private boolean firstMove = true, secondMove = true;
    public SphericGuardian() {
        this(0.0F, 0.0F);
    }
    public SphericGuardian(float x, float y) {
        super(NAME, "SphericGuardian", 20, 0.0F, 10.0F, 280.0F, 280.0F, null, x, y);
        this.damage.add(new DamageInfo(this, 20));
        this.damage.add(new DamageInfo(this, 15));
        this.damage.add(new DamageInfo(this, 10));
        loadAnimation("images/monsters/theCity/sphere/skeleton.atlas", "images/monsters/theCity/sphere/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(1.5F);
    }

    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BarricadePower(this)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ArtifactPower(this, 3)));
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 50));
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
            case 2:
                if (MathUtils.randomBoolean()) {
                    AbstractDungeon.actionManager.addToBottom(new SFXAction("SPHERE_DETECT_VO_1"));
                } else {
                    AbstractDungeon.actionManager.addToBottom(new SFXAction("SPHERE_DETECT_VO_2"));
                }
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0]));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 30));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 20));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(2), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 5, true), 5));
                break;
        }
        rollMove();
    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.output > 0) {
            this.stateData.setMix("Idle", "Hit", 0.3F);
            this.stateData.setMix("Hit", "Idle", 0.3F);
            this.state.setAnimation(0, "Hit", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }

    protected void getMove(int num) {
        if (this.firstMove) {
            this.firstMove = false;
            setMove((byte) 2, AbstractMonster.Intent.DEFEND);
            return;
        }
        if (this.secondMove) {
            this.secondMove = false;
            setMove((byte) 4, AbstractMonster.Intent.ATTACK_DEBUFF, 15);
            return;
        }
        if (lastMove((byte) 1)) {
            setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEFEND, 10);
        } else {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, 20);
        }
    }

    public void die() {
        super.die();
        CardCrawlGame.sound.playA("SPHERE_DETECT_VO_1", -0.3F);
    }
}


