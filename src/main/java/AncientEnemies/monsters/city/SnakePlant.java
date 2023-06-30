package AncientEnemies.monsters.city;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.MalleablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;

public class SnakePlant extends AbstractMonster {
    public static final String ID = "SnakePlant";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SnakePlant");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final byte CHOMPY_CHOMPS = 1;
    private static final byte SPORES = 2;
    private static final int CHOMPY_AMT = 3;
    private static final int RAIN_OF_BLOWS_DMG = 7;

    public SnakePlant(float x, float y) {
        super(NAME, "SnakePlant", 77, 0.0F, -44.0F, 350.0F, 360.0F, null, x, y + 50.0F);
        loadAnimation("images/monsters/theCity/snakePlant/skeleton.atlas", "images/monsters/theCity/snakePlant/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        this.damage.add(new DamageInfo(this, 7));
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MalleablePower(this)));
    }


    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.output > 0) {
            AnimationState.TrackEntry e = this.state.setAnimation(0, "hit", false);
            this.state.setTimeScale(1.5F);
            e.setTime(e.getEndTime() * 0.3F);
            this.state.addAnimation(0, "idle", true, 0.0F);
        }
    }

    public void takeTurn() {
        int numBlows, i;
        AbstractPlayer abstractPlayer = AbstractDungeon.player;
        switch (this.nextMove) {
            case 1:
                numBlows = 3;

                for (i = 0; i < numBlows; i++) {
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(new BiteEffect(AbstractDungeon.player.hb.cX +


                            MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                            MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.CHARTREUSE
                            .cpy()), 0.3F));

                    AbstractDungeon.actionManager.addToBottom(new DamageAction(abstractPlayer, this.damage
                            .get(0), AbstractGameAction.AttackEffect.NONE));
                }
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 2, true), 2));


                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 2, true), 2));
                break;
        }


        rollMove();
    }


    protected void getMove(int num) {
        if (num < 65) {
            if (lastTwoMoves((byte) 1)) {
                setMove(MOVES[0], (byte) 2, AbstractMonster.Intent.STRONG_DEBUFF);
            } else {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, 7, 3, true);
            }

        } else if (lastMove((byte) 2)) {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, 7, 3, true);
        } else {
            setMove(MOVES[0], (byte) 2, AbstractMonster.Intent.STRONG_DEBUFF);
        }
    }
}





