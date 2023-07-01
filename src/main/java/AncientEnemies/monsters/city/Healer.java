package AncientEnemies.monsters.city;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class Healer extends AbstractMonster {
    public static final String ID = "Healer";
    public static final String ENC_NAME = "HealerTank";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Healer");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final byte ATTACK = 1;
    private static final byte HEAL = 2;
    private static final byte BUFF = 3;
    private final int MAGIC_ATTACK = 8;
    private final int HEAL_AMT = 16;
    private final int STR_AMOUNT = 2;

    public Healer(float x, float y) {
        super(NAME, "Healer", 52, 0.0F, -20.0F, 230.0F, 250.0F, null, x, y);

        this.damage.add(new DamageInfo(this, this.MAGIC_ATTACK));

        loadAnimation("images/monsters/theCity/healer/skeleton.atlas", "images/monsters/theCity/healer/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 2, true), 2));
                break;


            case 2:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "STAFF_RAISE"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.25F));
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    if (!m.isDying && !m.isEscaping) {
                        AbstractDungeon.actionManager.addToBottom(new HealAction(m, this, this.HEAL_AMT));
                    }
                }
                break;
            case 3:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "STAFF_RAISE"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.25F));
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    if (!m.isDying && !m.isEscaping) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, this.STR_AMOUNT), this.STR_AMOUNT));
                    }
                }
                break;
        }


        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private void playSfx() {
        if (MathUtils.randomBoolean()) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_HEALER_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_HEALER_1B"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_HEALER_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_HEALER_2B");
        } else {
            CardCrawlGame.sound.play("VO_HEALER_2C");
        }
    }


    public void changeState(String key) {
        if (key.equals("STAFF_RAISE")) {
            this.state.setAnimation(0, "Attack", false);
            this.state.setTimeScale(2.5F);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }


    protected void getMove(int num) {
        int needToHeal = 0;

        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            if (!m.isDying && !m.isEscaping) {
                needToHeal += m.maxHealth - m.currentHealth;
            }
        }

        if (needToHeal > 15 && !lastTwoMoves((byte) 2)) {
            setMove((byte) 2, AbstractMonster.Intent.BUFF);

            return;
        }

        if (num >= 40 && !lastTwoMoves((byte) 1)) {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK_DEBUFF, this.MAGIC_ATTACK);

            return;
        }

        if (!lastTwoMoves((byte) 3)) {
            setMove((byte) 3, AbstractMonster.Intent.BUFF);
            return;
        }
        setMove((byte) 1, AbstractMonster.Intent.ATTACK_DEBUFF, this.MAGIC_ATTACK);
    }


    public void die() {
        playDeathSfx();
        this.state.setTimeScale(0.1F);
        useShakeAnimation(5.0F);
        super.die();
    }
}





