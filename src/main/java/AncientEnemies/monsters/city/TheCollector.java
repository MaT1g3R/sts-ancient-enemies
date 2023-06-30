package AncientEnemies.monsters.city;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.CollectorCurseEffect;
import com.megacrit.cardcrawl.vfx.GlowyFireEyesEffect;
import com.megacrit.cardcrawl.vfx.StaffFireEffect;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class TheCollector extends AbstractMonster {
    public static final String ID = "TheCollector";
    private static final Logger logger = LogManager.getLogger(TheCollector.class.getName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("TheCollector");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final float FIRE_TIME = 0.07F;
    private static final byte SPAWN = 1;
    private static final byte FIREBALL = 2;
    private static final byte BUFF = 3;
    private static final byte MEGA_DEBUFF = 4;
    private static final byte REVIVE = 5;
    private final int RAKE_DMG = 18;
    private final int STRENGTH_AMT = 3;
    private final int BLOCK_AMT = 15;
    private final int MEGA_DEBUFF_AMT = 3;
    private int TURNS_TAKEN = 0;
    private final float spawnX = -100.0F;
    private float fireTimer = 0.0F;
    private boolean ultUsed = false;
    private boolean initialSpawn = true;
    private final HashMap<Integer, AbstractMonster> enemySlots = new HashMap<>();
    public TheCollector() {
        super(NAME, "TheCollector", 282, 15.0F, -40.0F, 300.0F, 390.0F, null, 60.0F, 135.0F);

        this.dialogX = -90.0F * Settings.scale;
        this.dialogY = 10.0F * Settings.scale;
        this.type = AbstractMonster.EnemyType.BOSS;
        this.damage.add(new DamageInfo(this, this.RAKE_DMG));


        loadAnimation("images/monsters/theCity/collector/skeleton.atlas", "images/monsters/theCity/collector/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_CITY");
        UnlockTracker.markBossAsSeen("COLLECTOR");
    }

    public void takeTurn() {
        int i, key;
        AbstractMonster newMonster;
        switch (this.nextMove) {
            case 1:
                for (i = 1; i < 4; i++) {
                    AbstractMonster m = new TorchHead(this.spawnX + -185.0F * i, MathUtils.random(-5.0F, 25.0F));
                    AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(m, true));
                    this.enemySlots.put(Integer.valueOf(i), m);
                }

                this.initialSpawn = false;
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.BLOCK_AMT));
                for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                    if (!m.isDead && !m.isDying && !m.isEscaping) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, this.STRENGTH_AMT), this.STRENGTH_AMT));
                    }
                }
                break;

            case 4:
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0]));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new CollectorCurseEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 2.0F));


                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, this.MEGA_DEBUFF_AMT, true), this.MEGA_DEBUFF_AMT));


                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, this.MEGA_DEBUFF_AMT, true), this.MEGA_DEBUFF_AMT));


                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, this.MEGA_DEBUFF_AMT, true), this.MEGA_DEBUFF_AMT));


                this.ultUsed = true;
                break;
            case 5:
                key = -1;
                newMonster = null;
                for (Map.Entry<Integer, AbstractMonster> m : this.enemySlots.entrySet()) {
                    if (m.getValue().isDying) {
                        newMonster = new TorchHead(this.spawnX + -185.0F * m.getKey().intValue(), MathUtils.random(-5.0F, 25.0F));
                        key = m.getKey().intValue();
                    }
                }

                this.enemySlots.put(Integer.valueOf(key), newMonster);
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(newMonster, true));
                break;
            default:
                logger.info("ERROR: Default Take Turn was called on " + this.name);
                break;
        }
        this.TURNS_TAKEN++;
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }


    protected void getMove(int num) {
        if (this.initialSpawn) {
            setMove((byte) 1, AbstractMonster.Intent.UNKNOWN);

            return;
        }
        if (this.TURNS_TAKEN >= 3 && !this.ultUsed) {
            setMove((byte) 4, AbstractMonster.Intent.STRONG_DEBUFF);

            return;
        }

        if (isMinionDead() && !lastMove((byte) 5)) {
            setMove((byte) 5, AbstractMonster.Intent.UNKNOWN);

            return;
        }
        if (num <= 70 && !lastTwoMoves((byte) 2)) {
            setMove((byte) 2, AbstractMonster.Intent.ATTACK, this.RAKE_DMG);

            return;
        }
        if (!lastMove((byte) 3)) {
            setMove((byte) 3, AbstractMonster.Intent.DEFEND_BUFF);
        } else {
            setMove((byte) 2, AbstractMonster.Intent.ATTACK, this.RAKE_DMG);
        }
    }

    private boolean isMinionDead() {
        for (Map.Entry<Integer, AbstractMonster> m : this.enemySlots.entrySet()) {
            if (m.getValue().isDying) {
                return true;
            }
        }

        return false;
    }


    public void update() {
        super.update();
        if (!this.isDying) {
            this.fireTimer -= Gdx.graphics.getDeltaTime();
            if (this.fireTimer < 0.0F) {
                this.fireTimer = 0.07F;
                AbstractDungeon.effectList.add(new GlowyFireEyesEffect(this.skeleton

                        .getX() + this.skeleton.findBone("lefteyefireslot").getX(), this.skeleton
                        .getY() + this.skeleton.findBone("lefteyefireslot").getY() + 140.0F * Settings.scale));

                AbstractDungeon.effectList.add(new GlowyFireEyesEffect(this.skeleton

                        .getX() + this.skeleton.findBone("righteyefireslot").getX(), this.skeleton
                        .getY() + this.skeleton.findBone("righteyefireslot").getY() + 140.0F * Settings.scale));

                AbstractDungeon.effectList.add(new StaffFireEffect(this.skeleton

                        .getX() + this.skeleton.findBone("fireslot").getX() - 120.0F * Settings.scale, this.skeleton
                        .getY() + this.skeleton.findBone("fireslot").getY() + 390.0F * Settings.scale));
            }
        }
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
        UnlockTracker.hardUnlockOverride("COLLECTOR");
        UnlockTracker.unlockAchievement("COLLECTOR");
    }
}





