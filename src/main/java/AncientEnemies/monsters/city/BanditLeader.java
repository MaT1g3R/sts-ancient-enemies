package AncientEnemies.monsters.city;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

import java.util.Objects;

public class BanditLeader extends AbstractMonster {
    public static final String ID = "BanditLeader";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("BanditLeader");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int SLASH_DAMAGE = 8;
    private static final int AGONIZE_DAMAGE = 10;
    private static final int WEAK_AMT = 2;
    private static final byte CROSS_SLASH = 1;
    private static final byte MOCK = 2;
    private static final byte AGONIZING_SLASH = 3;
    public BanditLeader() {
        super(NAME, "BanditLeader", 37, 0.0F, 0.0F, 180.0F, 200.0F, "AncientEnemies/bossBandit.png");
        this.dialogX = 0.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, 8));
        this.damage.add(new DamageInfo(this, 10));
    }

    public void deathReact() {
        AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[2]));
    }

    public void takeTurn() {
        Boolean bearLives;
        switch (this.nextMove) {
            case 2:
                bearLives = Boolean.valueOf(true);
                for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                    if (Objects.equals(m.id, "Bear")) {
                        bearLives = Boolean.valueOf((!m.isDying && !m.isDead));
                    }
                }
                if (bearLives.booleanValue()) {
                    AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0]));
                } else {
                    AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[1]));
                }
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 3, Intent.ATTACK_DEBUFF, 10));
                break;

            case 3:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 2, true), 2));


                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 1, Intent.ATTACK, 8, 2, true));
                break;

            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 3, Intent.ATTACK_DEBUFF, 10));
                break;
        }
    }


    protected void getMove(int num) {
        setMove((byte) 2, AbstractMonster.Intent.UNKNOWN);
    }
}





