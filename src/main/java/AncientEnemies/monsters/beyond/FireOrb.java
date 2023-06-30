package AncientEnemies.monsters.beyond;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FireOrb extends AbstractMonster {
    public static final String ID = "FireOrb";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("FireOrb");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int HP_MIN = 16;
    private static final int HP_MAX = 20;
    private static final float HB_X_F = 0.0F;
    private static final float HB_Y_F = 0.0F;
    private static final float HB_W = 120.0F;
    private static final float HB_H = 120.0F;
    private static final int BURN_DMG = 9;
    private static final int EXPLODE_DMG = 25;
    private static final byte BURN = 1;
    private static final byte EXPLODE = 2;
    public boolean firstMove = true;
    public FireOrb(float x, float y) {
        super(NAME, "FireOrb",
                MathUtils.random(16, 20), 0.0F, 0.0F, 120.0F, 120.0F, "AncientEnemies/fireball.png", x, y);
        this.damage.add(new DamageInfo(this, 9));
        this.damage.add(new DamageInfo(this, 25));
    }

    public void setImg() {
        this.img = ImageMaster.loadImage("AncientEnemies/fireball.png");
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Burn(), 1));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(this, this.damage
                        .get(1), AbstractGameAction.AttackEffect.FIRE));
                break;
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }


    protected void getMove(int num) {
        if (this.firstMove) {
            this.firstMove = false;
            setMove((byte) 1, Intent.ATTACK_DEBUFF, 9);

            return;
        }
        setMove((byte) 2, Intent.ATTACK, 25);
    }
}

