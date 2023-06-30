package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AngerPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class GremlinNob extends AbstractMonster {
    public static final String ID = "GremlinNob";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinNob");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int HP = MathUtils.random(86, 90);
    private static final byte BULL_RUSH = 1;
    private static final byte SKULL_BASH = 2;
    private static final byte BELLOW = 3;
    private static final int ANGRY_LEVEL = 2;
    private final int bashDamage = 8;
    private final int rushDamage = 14;
    private final int debuffAmount = 2;
    private boolean usedBellow = false;

    public GremlinNob() {
        super(NAME, "GremlinNob", HP, -70.0F, -10.0F, 270.0F, 380.0F, null);
        this.intentOffsetX = -30.0F * Settings.scale;
        this.type = AbstractMonster.EnemyType.ELITE;
        this.dialogX = -60.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, this.rushDamage));
        this.damage.add(new DamageInfo(this, this.bashDamage));

        loadAnimation("images/monsters/theBottom/nobGremlin/skeleton.atlas", "images/monsters/theBottom/nobGremlin/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "animation", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("ELITE");
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 3:
                playSfx();
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0], 1.0F, 3.0F));


                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new AngerPower(this, 2), 2));
                break;


            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, this.debuffAmount, true), this.debuffAmount));
                break;


            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
        }


        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINNOB_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINNOB_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINNOB_1C"));
        }
    }


    protected void getMove(int num) {
        if (!this.usedBellow) {
            this.usedBellow = true;
            setMove((byte) 3, AbstractMonster.Intent.BUFF);

            return;
        }

        if (num < 33) {
            setMove(MOVES[0], (byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, this.bashDamage);

            return;
        }

        if (lastTwoMoves((byte) 1)) {
            setMove(MOVES[0], (byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, this.bashDamage);
        } else {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, this.rushDamage);
        }
    }


    public void die() {
        super.die();
        AbstractDungeon.scene.fadeInAmbiance();
        CardCrawlGame.music.fadeOutTempBGM();
    }
}





