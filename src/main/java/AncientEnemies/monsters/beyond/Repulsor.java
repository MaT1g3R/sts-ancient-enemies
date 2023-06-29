package AncientEnemies.monsters.beyond;


import AncientEnemies.patches.RepulsePowerPatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.Intent;
import AncientEnemies.patches.RepulsePowerPatch;
import com.megacrit.cardcrawl.powers.StrengthPower;
import java.util.Iterator;

public class Repulsor extends AbstractMonster {
    public static final String ID = "Repulsor";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    public static final String ENCOUNTER_NAME_W = "Ancient Shapes Weak";
    public static final String ENCOUNTER_NAME = "Ancient Shapes";
    private static final int HP_MAX = 34;
    private static final int HP_MIN = 30;
    private static final float HB_X = -8.0F;
    private static final float HB_Y = -10.0F;
    private static final float HB_W = 150.0F;
    private static final float HB_H = 150.0F;
    private static final byte STR_UP = 1;
    private static final int STR_AMT = 3;
    private static final byte ATTACK = 2;
    private static final int ATTACK_DMG = 8;

    public Repulsor(float x, float y) {
        super(NAME, "Repulsor", MathUtils.random(30, 34), -8.0F, -10.0F, 150.0F, 150.0F, (String)null, x, y + 10.0F);
        this.loadAnimation("images/monsters/theForest/repulser/skeleton.atlas", "images/monsters/theForest/repulser/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.damage.add(new DamageInfo(this, 8));
    }

    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new RepulsePowerPatch(this)));
    }

    public void takeTurn() {
        label23:
        switch (this.nextMove) {
            case 1:
                Iterator var1 = AbstractDungeon.getCurrRoom().monsters.monsters.iterator();

                while(true) {
                    if (!var1.hasNext()) {
                        break label23;
                    }

                    AbstractMonster m = (AbstractMonster)var1.next();
                    if (!m.isDead && !m.isDying && !m.isEscaping) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, 3), 3));
                    }
                }
            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(0), AttackEffect.SLASH_HORIZONTAL));
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.type != DamageType.HP_LOSS && info.type != DamageType.THORNS && info.owner != null) {
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Wound(), false));
        }

    }

    protected void getMove(int num) {
        if (num < 20 && !this.lastMove((byte)2)) {
            this.setMove((byte)2, Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
        } else {
            this.setMove((byte)1, Intent.BUFF);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Repulsor");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}
