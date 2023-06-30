package AncientEnemies.monsters.city;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.TorchHeadFireEffect;

public class TorchHead extends AbstractMonster {
    public static final String ID = "TorchHead";
    public static final int MIN_HP = 24;
    public static final int MAX_HP = 31;
    public static final int ATTACK_DMG = 6;
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("TorchHead");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final byte TACKLE = 1;
    private static final float FIRE_TIME = 0.04F;
    private float fireTimer = 0.0F;

    public TorchHead(float x, float y) {
        super(NAME, "TorchHead", MathUtils.random(24, 31), -5.0F, -20.0F, 145.0F, 240.0F, null, x, y);
        setMove((byte) 1, AbstractMonster.Intent.ATTACK, 6);
        this.damage.add(new DamageInfo(this, 6));


        loadAnimation("images/monsters/theCity/torchHead/skeleton.atlas", "images/monsters/theCity/torchHead/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void takeTurn() {
        if (this.nextMove == 1) {
            AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage


                    .get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

            AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, (byte) 1, Intent.ATTACK, 6));
        }
    }


    public void update() {
        super.update();
        if (!this.isDying) {
            this.fireTimer -= Gdx.graphics.getDeltaTime();
            if (this.fireTimer < 0.0F) {
                this.fireTimer = 0.04F;
                AbstractDungeon.effectList.add(new TorchHeadFireEffect(this.skeleton

                        .getX() + this.skeleton.findBone("fireslot").getX() + 10.0F * Settings.scale, this.skeleton
                        .getY() + this.skeleton.findBone("fireslot").getY() + 110.0F * Settings.scale));
            }
        }
    }


    protected void getMove(int num) {
        setMove((byte) 1, AbstractMonster.Intent.ATTACK, 6);
    }
}





