package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import com.megacrit.cardcrawl.vfx.combat.SmallLaserEffect;

public class Sentry extends AbstractMonster {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Sentry");
    public static final String ID = "Sentry";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final String ENC_NAME = "Sentries";
    private static final int HP_MIN = 34;
    private static final int HP_MAX = 38;
    private static final byte BOLT = 3;
    private static final byte BEAM = 4;
    private static final int BEAM_DAMAGE = 8;
    private static final int WOUND_AMT = 2;


    public Sentry(float x, float y) {
        super(NAME, "Sentry", MathUtils.random(34, 38), 0.0F, -5.0F, 180.0F, 310.0F, null, x, y);
        this.type = AbstractMonster.EnemyType.ELITE;
        this.damage.add(new DamageInfo((AbstractCreature) this, 8));
        loadAnimation("images/monsters/theBottom/sentry/skeleton.atlas", "images/monsters/theBottom/sentry/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("idle", "spaz1", 0.3F);
    }

    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this, (AbstractCreature) this, (AbstractPower) new ArtifactPower((AbstractCreature) this, 1)));
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SFXAction("THUNDERCLAP"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new VFXAction((AbstractCreature) this, (AbstractGameEffect) new ShockWaveEffect(this.hb.cX, this.hb.cY, Color.ROYAL, ShockWaveEffect.ShockWaveType.ADDITIVE), 0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new FastShakeAction((AbstractCreature) AbstractDungeon.player, 0.6F, 0.2F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new MakeTempCardInDiscardAction((AbstractCard) new Wound(), 2));
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SFXAction("ATTACK_MAGIC_BEAM_SHORT", 0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new VFXAction((AbstractGameEffect) new BorderFlashEffect(Color.SKY)));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new VFXAction((AbstractGameEffect) new SmallLaserEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, this.hb.cX, this.hb.cY), 0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new DamageAction((AbstractCreature) AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.NONE));
                break;
        }
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new RollMoveAction(this));
    }

    protected void getMove(int num) {
        if (num < 66) {
            if (lastTwoMoves((byte) 4)) {
                setMove((byte) 3, AbstractMonster.Intent.DEBUFF);
            } else {
                setMove((byte) 4, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
            }
        } else if (lastMove((byte) 3)) {
            setMove((byte) 4, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
        } else {
            setMove((byte) 3, AbstractMonster.Intent.DEBUFF);
        }
    }
}
