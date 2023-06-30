package AncientEnemies.monsters.city;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BanditChild extends AbstractMonster {
    public static final String ID = "BanditChild";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("BanditChild");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int ATTACK_DMG = 9;
    private static final byte POINTY_SPECIAL = 1;

    public BanditChild(float x, float y) {
        super(NAME, "BanditChild", 27, 0.0F, 0.0F, 180.0F, 200.0F, "images/monsters/theCity/littleBandit.png", x, y);
        this.dialogX = 0.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, 9, DamageInfo.DamageType.NORMAL));
    }


    public void takeTurn() {
        AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
        AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                .get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 1, Intent.ATTACK, 9));
    }


    public void deathReact() {
        AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0]));
    }


    protected void getMove(int num) {
        setMove((byte) 1, AbstractMonster.Intent.ATTACK, 9);
    }
}





