package AncientEnemies.monsters.city;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.unique.GainBlockRandomMonsterAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Centurion extends AbstractMonster {
    public static final String ID = "Centurion";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Centurion");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final byte SLASH = 1;
    private static final byte PROTECT = 2;
    private static final byte FURY = 3;
    private final int SLASH_DMG = 12;
    private final int FURY_DMG = 6;
    private final int FURY_HITS = 3;
    private final int BLOCK_AMOUNT = 15;
    public Centurion(float x, float y) {
        super(NAME, "Centurion", 78, -14.0F, -20.0F, 250.0F, 330.0F, null, x, y);

        this.damage.add(new DamageInfo(this, this.SLASH_DMG));
        this.damage.add(new DamageInfo(this, this.FURY_DMG));

        loadAnimation("images/monsters/theCity/tank/skeleton.atlas", "images/monsters/theCity/tank/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.state.setTimeScale(2.0F);
    }

    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 1:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;

            case 2:
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "BLOCK_ANIM"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.25F));
                AbstractDungeon.actionManager.addToBottom(new GainBlockRandomMonsterAction(this, this.BLOCK_AMOUNT));
                break;
            case 3:
                for (i = 0; i < this.FURY_HITS; i++) {
                    playSfx();
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "MACE_HIT"));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.4F));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                            .get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                }
                break;
        }


        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_TANK_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_TANK_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_TANK_1C"));
        }
    }


    public void changeState(String key) {
        switch (key) {
            case "BLOCK_ANIM":
                this.state.setAnimation(0, "block", false);
                this.state.addAnimation(0, "idle", true, 0.0F);
                break;
            case "MACE_HIT":
                this.state.setAnimation(0, "mace_hit", false);
                this.state.addAnimation(0, "idle", true, 0.0F);
                this.state.setTimeScale(2.5F);
                break;
        }
    }


    protected void getMove(int num) {
        if (num >= 65 && !lastTwoMoves((byte) 2) && !lastTwoMoves((byte) 3)) {
            int i = 0;


            for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                if (!m.isDying && !m.isEscaping) {
                    i++;
                }
            }

            if (i > 1) {
                setMove((byte) 2, AbstractMonster.Intent.DEFEND);
                return;
            }
            setMove((byte) 3, AbstractMonster.Intent.ATTACK, this.FURY_DMG, this.FURY_HITS, true);

            return;
        }

        if (!lastTwoMoves((byte) 1)) {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.SLASH_DMG);
            return;
        }
        int aliveCount = 0;


        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            if (!m.isDying && !m.isEscaping) {
                aliveCount++;
            }
        }

        if (aliveCount > 1) {
            setMove((byte) 2, AbstractMonster.Intent.DEFEND);
            return;
        }
        setMove((byte) 3, AbstractMonster.Intent.ATTACK, this.FURY_DMG, this.FURY_HITS, true);
    }


    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.output > 0) {
            AnimationState.TrackEntry e = this.state.setAnimation(0, "damaged", false);
            this.state.setTimeScale(1.5F);
            e.setTime(e.getEndTime() * 0.3F);
            this.state.addAnimation(0, "idle", true, 0.0F);
        }
    }


    public void die() {
        this.state.setTimeScale(0.1F);
        useShakeAnimation(5.0F);
        super.die();
    }
}





