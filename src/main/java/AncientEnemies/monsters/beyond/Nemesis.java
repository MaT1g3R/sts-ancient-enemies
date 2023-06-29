package AncientEnemies.monsters.beyond;

import AncientEnemies.powers.IntangiblePower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Bone;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.NemesisFireParticle;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect.ShockWaveType;

public class Nemesis extends AbstractMonster {
    public static final String ID = "Nemesis";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int HP = 32;
    private static final float HB_X = 5.0F;
    private static final float HB_Y = -10.0F;
    private static final int SCYTHE_DMG = 40;
    private static final int CHARGE_BLOCK = 3;
    private static final int BREATH_DMG = 8;
    private static final int BREATH_TIMES = 3;
    private static final byte SCYTHE = 1;
    private static final byte FIRE = 2;
    private static final byte POWER_STRIP = 3;
    private static final byte CHARGE = 4;
    private boolean firstMove = true;
    private float fireTimer = 0.0F;
    private static final float FIRE_TIME = 0.05F;
    private Bone eye1;
    private Bone eye2;
    private Bone eye3;

    public Nemesis() {
        super(NAME, "Nemesis", 32, 5.0F, -10.0F, 350.0F, 440.0F, (String)null, 0.0F, 0.0F);
        this.type = EnemyType.ELITE;
        this.loadAnimation("images/monsters/theForest/nemesis/skeleton.atlas", "images/monsters/theForest/nemesis/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.1F);
        e.setTimeScale(0.8F);
        this.eye1 = this.skeleton.findBone("eye0");
        this.eye2 = this.skeleton.findBone("eye1");
        this.eye3 = this.skeleton.findBone("eye2");
        this.damage.add(new DamageInfo(this, 40));
        this.damage.add(new DamageInfo(this, 8));
    }

    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new IntangiblePower(this)));
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                this.playSfx();
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(0), AttackEffect.SLASH_HEAVY));
                this.rollMove();
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_NEMESIS_1C"));

                for(int i = 0; i < 3; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(1), AttackEffect.FIRE));
                }

                this.rollMove();
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.3F, 0.5F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, Color.ROYAL, ShockWaveType.ADDITIVE), 0.25F));
                if (!AbstractDungeon.player.powers.isEmpty()) {
                    String toRemove = ((AbstractPower)AbstractDungeon.player.powers.get(MathUtils.random(0, AbstractDungeon.player.powers.size() - 1))).ID;
                    AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(AbstractDungeon.player, this, toRemove));
                }

                this.rollMove();
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 3));
                this.setMove((byte)1, Intent.ATTACK, 40);
        }

    }

    public void damage(DamageInfo info) {
        if (info.output > 0) {
            info.output = 1;
        }

        super.damage(info);
    }

    protected void getMove(int num) {
        if (this.firstMove) {
            this.firstMove = false;
            this.setMove((byte)2, Intent.ATTACK, 8, 3, true);
        } else if (num < 25) {
            this.setMove((byte)4, Intent.DEFEND);
        } else if (num < 75 && !this.lastTwoMoves((byte)2)) {
            this.setMove((byte)2, Intent.ATTACK, 8, 3, true);
        } else {
            if (!AbstractDungeon.player.powers.isEmpty() && !this.lastMove((byte)3)) {
                this.setMove((byte)3, Intent.STRONG_DEBUFF);
            } else {
                this.setMove((byte)4, Intent.DEFEND);
            }

        }
    }

    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_NEMESIS_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_NEMESIS_1B"));
        }

    }

    private void playDeathSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_NEMESIS_2A");
        } else {
            CardCrawlGame.sound.play("VO_NEMESIS_2B");
        }

    }

    public void die() {
        this.playDeathSfx();
        super.die();
    }

    public void update() {
        super.update();
        if (!this.isDying) {
            this.fireTimer -= Gdx.graphics.getDeltaTime();
            if (this.fireTimer < 0.0F) {
                this.fireTimer = 0.05F;
                AbstractDungeon.effectList.add(new NemesisFireParticle(this.skeleton.getX() + this.eye1.getWorldX(), this.skeleton.getY() + this.eye1.getWorldY()));
                AbstractDungeon.effectList.add(new NemesisFireParticle(this.skeleton.getX() + this.eye2.getWorldX(), this.skeleton.getY() + this.eye2.getWorldY()));
                AbstractDungeon.effectList.add(new NemesisFireParticle(this.skeleton.getX() + this.eye3.getWorldX(), this.skeleton.getY() + this.eye3.getWorldY()));
            }
        }

    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Nemesis");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}
