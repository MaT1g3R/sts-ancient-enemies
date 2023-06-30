package AncientEnemies.monsters.city;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ConfusionPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.IntimidateEffect;

public class Snecko extends AbstractMonster {
    public static final String ID = "Snecko";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Snecko");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final byte GLARE = 1;
    private static final byte BITE = 2;
    private static final byte TAIL = 3;
    private static final int BITE_DAMAGE = 14;
    private static final int TAIL_DAMAGE = 7;
    private static final int VULNERABLE_AMT = 2;
    private static final int MIN_HP = 106;
    private static final int MAX_HP = 112;
    private boolean firstTurn = true;

    public Snecko() {
        super(NAME, "Snecko", MathUtils.random(106, 112), -30.0F, 0.0F, 310.0F, 305.0F, null);
        loadAnimation("images/monsters/theCity/reptile/skeleton.atlas", "images/monsters/theCity/reptile/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(0.6F);
        e.setTime(e.getEndTime() * MathUtils.random());

        this.damage.add(new DamageInfo(this, 14));
        this.damage.add(new DamageInfo(this, 7));
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new IntimidateEffect(this.hb.cX, this.hb.cY), 0.5F));

                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(AbstractDungeon.player, 1.0F, 1.0F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new ConfusionPower(AbstractDungeon.player)));
                break;

            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                break;

            case 3:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 2, true), 2));
                break;
        }


        rollMove();
    }


    protected void getMove(int num) {
        if (this.firstTurn) {
            this.firstTurn = false;
            setMove(MOVES[0], (byte) 1, AbstractMonster.Intent.STRONG_DEBUFF);

            return;
        }

        if (num < 40) {
            setMove(MOVES[1], (byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, 7);

            return;
        }

        if (lastTwoMoves((byte) 2)) {
            setMove(MOVES[1], (byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, 7);
        } else {
            setMove(MOVES[2], (byte) 2, AbstractMonster.Intent.ATTACK, 14);
        }
    }


    public void die() {
        super.die();
        CardCrawlGame.sound.play("SNECKO_DEATH");
    }
}





