package AncientEnemies.monsters.city;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;

public class BanditBear extends AbstractMonster {
    public static final String ID = "BanditBear";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("BanditBear");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int MAUL_DAMAGE = 18;
    private static final int LUNGE_DAMAGE = 9;
    private static final int LUNGE_DEFENSE = 9;
    private static final int CON_AMT = -2;
    private static final byte MAUL = 1;
    private static final byte BEAR_HUG = 2;
    private static final byte LUNGE = 3;

    public BanditBear(float x, float y) {
        super(NAME, "BanditBear", 40, 0.0F, 0.0F, 180.0F, 200.0F, "images/monsters/theCity/fatBandit.png", x, y);

        this.damage.add(new DamageInfo(this, 18));
        this.damage.add(new DamageInfo(this, 9));
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DexterityPower(AbstractDungeon.player, -2), -2));


                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 3, Intent.ATTACK_DEFEND, 9));
                break;

            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 3, Intent.ATTACK_DEFEND, 9));
                break;

            case 3:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 9));
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 1, Intent.ATTACK, 18));
                break;
        }
    }

    public void die() {
        super.die();
        for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
            if (!m.isDead && !m.isDying) {
                m.deathReact();
            }
        }
    }


    protected void getMove(int num) {
        setMove((byte) 2, AbstractMonster.Intent.STRONG_DEBUFF);
    }
}





