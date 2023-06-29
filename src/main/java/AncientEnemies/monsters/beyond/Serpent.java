package AncientEnemies.monsters.beyond;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ConstrictedPower;

public class Serpent extends AbstractMonster {
    public static final String ID = "Serpent";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    public static final String IMAGE = "AncientEnemies/serpent.png";
    private static final int START_HP = 170;
    private static final float HB_X = 0.0F;
    private static final float HB_Y = 0.0F;
    private static final float HB_W = 220.0F;
    private static final float HB_H = 280.0F;
    private int biteDmg = 16;
    private int constrictDmg = 22;
    private int latchedDmg = 10;
    private static final byte BITE = 1;
    private static final byte LATCHON = 2;
    private static final byte CONSTRICT = 3;

    public Serpent() {
        super(NAME, "Serpent", 170, 0.0F, 0.0F, 220.0F, 280.0F, "AncientEnemies/serpent.png");
        this.damage.add(new DamageInfo(this, this.biteDmg));
        this.damage.add(new DamageInfo(this, this.constrictDmg));
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(0), AttackEffect.SLASH_DIAGONAL));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new ConstrictedPower(AbstractDungeon.player, this, this.latchedDmg)));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(1), AttackEffect.BLUNT_HEAVY));
        }

        this.rollMove();
    }

    protected void getMove(int num) {
        if (num < 50 && !this.lastTwoMoves((byte)1)) {
            this.setMove((byte)1, Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
        } else if (!AbstractDungeon.player.hasPower("Constricted") && !this.lastMove((byte)2)) {
            this.setMove((byte)2, Intent.STRONG_DEBUFF);
        } else if (!this.lastTwoMoves((byte)3)) {
            this.setMove((byte)3, Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
        } else {
            this.setMove((byte)1, Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Serpent");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}
