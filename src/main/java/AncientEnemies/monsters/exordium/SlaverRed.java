package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.EntanglePower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class SlaverRed extends AbstractMonster {
    public static final String ID = "SlaverRed";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SlaverRed");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    private static final String SCRAPE_NAME = MOVES[0];
    private static final String ENTANGLE_NAME = MOVES[1];
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final byte STAB = 1;
    private static final byte ENTANGLE = 2;
    private static final byte SCRAPE = 3;
    private static final int HP_MIN = 46;
    private static final int HP_MAX = 50;
    private final int STAB_DMG = 13;
    private final int SCRAPE_DMG = 8;
    private final int VULN_AMT = 1;
    private boolean usedEntangle = false;
    private boolean firstTurn = true;

    public SlaverRed(float x, float y) {
        super(NAME, "SlaverRed", MathUtils.random(46, 50), 0.0F, 0.0F, 170.0F, 230.0F, null, x, y);


        this.damage.add(new DamageInfo(this, this.STAB_DMG));
        this.damage.add(new DamageInfo(this, this.SCRAPE_DMG));

        loadAnimation("images/monsters/theBottom/redSlaver/skeleton.atlas", "images/monsters/theBottom/redSlaver/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.firstTurn = true;
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 2:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "Use Net"));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new EntanglePower(AbstractDungeon.player)));

                this.usedEntangle = true;
                break;
            case 1:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                break;

            case 3:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, this.VULN_AMT, true), this.VULN_AMT));
                break;
        }


        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_SLAVERRED_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_SLAVERRED_1B"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_SLAVERRED_2A");
        } else {
            CardCrawlGame.sound.play("VO_SLAVERRED_2B");
        }
    }


    public void changeState(String stateName) {
        float tmp = this.state.getCurrent(0).getTime();
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idleNoNet", true);
        e.setTime(tmp);
    }


    protected void getMove(int num) {
        if (num >= 55 && !this.usedEntangle && !this.firstTurn) {
            setMove(ENTANGLE_NAME, (byte) 2, AbstractMonster.Intent.STRONG_DEBUFF);
            return;
        }
        this.firstTurn = false;

        if (num >= 55 && this.usedEntangle && !lastTwoMoves((byte) 1)) {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.STAB_DMG);
            return;
        }
        setMove(SCRAPE_NAME, (byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, this.SCRAPE_DMG);


        if (!lastTwoMoves((byte) 3)) {
            setMove(SCRAPE_NAME, (byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, this.SCRAPE_DMG);
            return;
        }
        setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.STAB_DMG);
    }


    public void die() {
        super.die();
        playDeathSfx();
    }
}





