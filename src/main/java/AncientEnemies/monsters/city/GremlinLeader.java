package AncientEnemies.monsters.city;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.unique.SummonGremlinAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

import java.util.ArrayList;

public class GremlinLeader extends AbstractMonster {
    public static final String ID = "GremlinLeader";
    public static final String ENC_NAME = "Gremlin Leader Combat";
    public static final float POSX1 = -366.0F;
    public static final float POSY1 = -4.0F;
    public static final float POSX2 = -170.0F;
    public static final float POSY2 = 6.0F;
    public static final float POSX3 = -532.0F;
    public static final float POSY3 = 0.0F;
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinLeader");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    private static final String RALLY_NAME = MOVES[0];
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final byte RALLY = 2;
    private static final byte ENCOURAGE = 3;
    private static final int STR_AMT = 3;
    private static final int BLOCK_AMT = 6;
    private static final byte STAB = 4;
    public AbstractMonster[] gremlins = new AbstractMonster[3];
    private final int STAB_DMG = 6;
    private final int STAB_AMT = 3;

    public GremlinLeader() {
        super(NAME, "GremlinLeader", 160, 0.0F, -15.0F, 200.0F, 310.0F, "images/monsters/theCity/gremlinLeader.png", 35.0F, 0.0F);
        this.type = AbstractMonster.EnemyType.ELITE;

        loadAnimation("images/monsters/theCity/gremlinleader/skeleton.atlas", "images/monsters/theCity/gremlinleader/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "animation", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(1.5F);

        this.dialogX = -80.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;
        this.damage.add(new DamageInfo(this, this.STAB_DMG));
    }


    public void usePreBattleAction() {
        this.gremlins[0] = (AbstractDungeon.getMonsters()).monsters.get(0);
        this.gremlins[1] = (AbstractDungeon.getMonsters()).monsters.get(1);
        this.gremlins[2] = null;
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 2:
                AbstractDungeon.actionManager.addToBottom(new SummonGremlinAction(this.gremlins));
                AbstractDungeon.actionManager.addToBottom(new SummonGremlinAction(this.gremlins));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, getEncourageQuote()));
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    if (m == this) {


                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, 3), 3));

                        continue;
                    }
                    if (!m.isDying) {


                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, 3), 3));

                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this, 6));
                    }
                }
                break;


            case 4:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.2F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL, true));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.2F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_VERTICAL, true));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                break;
        }
        rollMove();
    }

    private String getEncourageQuote() {
        ArrayList<String> list = new ArrayList<>();
        list.add(DIALOG[0]);
        list.add(DIALOG[1]);
        list.add(DIALOG[2]);

        return list.get(MathUtils.random(0, list.size() - 1));
    }


    protected void getMove(int num) {
        if (numAliveGremlins() == 0) {
            if (num < 75) {
                if (!lastMove((byte) 2)) {
                    setMove(RALLY_NAME, (byte) 2, AbstractMonster.Intent.UNKNOWN);
                } else {
                    setMove((byte) 4, AbstractMonster.Intent.ATTACK, this.STAB_DMG, this.STAB_AMT, true);
                }

            } else if (!lastMove((byte) 4)) {
                setMove((byte) 4, AbstractMonster.Intent.ATTACK, this.STAB_DMG, this.STAB_AMT, true);
            } else {
                setMove(RALLY_NAME, (byte) 2, AbstractMonster.Intent.UNKNOWN);

            }

        } else if (numAliveGremlins() < 2) {
            if (num < 50) {
                if (!lastMove((byte) 2)) {
                    setMove(RALLY_NAME, (byte) 2, AbstractMonster.Intent.UNKNOWN);
                } else {
                    getMove(MathUtils.random(50, 99));
                }
            } else if (num < 80) {
                if (!lastMove((byte) 3)) {
                    setMove((byte) 3, AbstractMonster.Intent.DEFEND_BUFF);
                } else {
                    setMove((byte) 4, AbstractMonster.Intent.ATTACK, this.STAB_DMG, this.STAB_AMT, true);
                }

            } else if (!lastMove((byte) 4)) {
                setMove((byte) 4, AbstractMonster.Intent.ATTACK, this.STAB_DMG, this.STAB_AMT, true);
            } else {
                getMove(MathUtils.random(0, 80));

            }

        } else if (numAliveGremlins() > 1) {
            if (num < 66) {
                if (!lastMove((byte) 3)) {
                    setMove((byte) 3, AbstractMonster.Intent.DEFEND_BUFF);
                } else {
                    setMove((byte) 4, AbstractMonster.Intent.ATTACK, this.STAB_DMG, this.STAB_AMT, true);
                }

            } else if (!lastMove((byte) 4)) {
                setMove((byte) 4, AbstractMonster.Intent.ATTACK, this.STAB_DMG, this.STAB_AMT, true);
            } else {
                setMove((byte) 3, AbstractMonster.Intent.DEFEND_BUFF);
            }
        }
    }


    private int numAliveGremlins() {
        int count = 0;
        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            if (m != null && m != this && !m.isDying) {
                count++;
            }
        }
        return count;
    }


    public void die() {
        super.die();
        boolean first = true;
        for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
            if (!m.isDying) {
                if (first) {
                    AbstractDungeon.actionManager.addToBottom(new ShoutAction(m, DIALOG[3], 0.5F, 1.2F));
                    first = false;
                    continue;
                }
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(m, DIALOG[4], 0.5F, 1.2F));
            }
        }

        for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
            if (!m.isDying)
                AbstractDungeon.actionManager.addToBottom(new EscapeAction(m));
        }
    }
}





