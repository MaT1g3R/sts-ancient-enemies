package AncientEnemies.monsters.city;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import com.megacrit.cardcrawl.vfx.combat.LaserBeamEffect;

public class BronzeAutomaton extends AbstractMonster {
    public static final String ID = "BronzeAutomaton";
    public static final int HEALTH = 300;
    public static final int FLAIL_DMG = 7;
    public static final int BEAM_DMG = 40;
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("BronzeAutomaton");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    private static final String BEAM_NAME = MOVES[0];
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final byte FLAIL = 1;
    private static final byte HYPER_BEAM = 2;
    private static final byte STUNNED = 3;
    private static final byte SPAWN_ORBS = 4;
    private static final byte BOOST = 5;
    private static final int BLOCK_AMT = 9;
    private static final int STR_AMT = 3;
    private int numTurns = 0;
    private boolean firstTurn = true;

    public BronzeAutomaton() {
        super(NAME, "BronzeAutomaton", 300, 0.0F, -30.0F, 270.0F, 400.0F, null, 0.0F, 10.0F);
        loadAnimation("images/monsters/theCity/automaton/skeleton.atlas", "images/monsters/theCity/automaton/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        this.type = AbstractMonster.EnemyType.BOSS;
        this.dialogX = -100.0F * Settings.scale;
        this.dialogY = 10.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, 7));
        this.damage.add(new DamageInfo(this, 40));
    }


    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_CITY");
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ArtifactPower(this, 3)));
        UnlockTracker.markBossAsSeen("AUTOMATON");
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 4:
                AbstractDungeon.actionManager.addToBottom(new SFXAction("AUTOMATON_ORB_SPAWN",
                        MathUtils.random(-0.1F, 0.1F)));
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new BronzeOrb(-250.0F, 200.0F, 0), true));
                AbstractDungeon.actionManager.addToBottom(new SFXAction("AUTOMATON_ORB_SPAWN",
                        MathUtils.random(-0.1F, 0.1F)));
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new BronzeOrb(250.0F, 120.0F, 1), true));
                break;
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                break;
            case 5:
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 9));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 3), 3));
                break;

            case 2:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new LaserBeamEffect(this.hb.cX, this.hb.cY + 60.0F * Settings.scale), 1.5F));

                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.NONE));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new TextAboveCreatureAction(this, DIALOG[0]));
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }


    protected void getMove(int num) {
        if (this.firstTurn) {
            setMove((byte) 4, AbstractMonster.Intent.UNKNOWN);
            this.firstTurn = false;

            return;
        }
        if (this.numTurns == 4) {
            setMove(BEAM_NAME, (byte) 2, AbstractMonster.Intent.ATTACK, 40);
            this.numTurns = 0;

            return;
        }

        if (lastMove((byte) 2)) {
            setMove((byte) 3, AbstractMonster.Intent.STUN);

            return;
        }
        if (lastMove((byte) 3) || lastMove((byte) 5) || lastMove((byte) 4)) {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, 7, 2, true);
        } else {
            setMove((byte) 5, AbstractMonster.Intent.DEFEND_BUFF);
        }

        this.numTurns++;
    }


    public void die() {
        useFastShakeAnimation(5.0F);
        CardCrawlGame.screenShake.rumble(4.0F);
        this.deathTimer += 1.5F;
        super.die();

        onBossVictoryLogic();

        for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
            if (!m.isDead && !m.isDying) {
                AbstractDungeon.actionManager.addToTop(new HideHealthBarAction(m));
                AbstractDungeon.actionManager.addToTop(new SuicideAction(m));
                AbstractDungeon.actionManager.addToTop(new VFXAction(m, new InflameEffect(m), 0.2F));
            }
        }
        UnlockTracker.hardUnlockOverride("AUTOMATON");
        UnlockTracker.unlockAchievement("AUTOMATON");
    }
}





