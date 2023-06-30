package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BobEffect;

public class HexaghostBody {
    public static final String ID = "HexaghostBody";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("HexaghostBody");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final String IMG_DIR = "images/monsters/theBottom/boss/ghost/";
    private static final int W = 512;
    private static final float BODY_OFFSET_Y = 256.0F * Settings.scale;
    public float targetRotationSpeed = 0.5F;
    private float rotationSpeed = 0.5F;
    private final AbstractMonster m;
    private final Texture plasma1;
    private final BobEffect effect = new BobEffect(0.75F);
    private final Texture plasma2;
    private final Texture plasma3;
    private final Texture shadow;
    private float plasma1Angle = 0.0F;
    private float plasma2Angle = 0.0F;
    private float plasma3Angle = 0.0F;

    public HexaghostBody(AbstractMonster m) {
        this.m = m;
        this.plasma1 = ImageMaster.loadImage("images/monsters/theBottom/boss/ghost/plasma1.png");
        this.plasma2 = ImageMaster.loadImage("images/monsters/theBottom/boss/ghost/plasma2.png");
        this.plasma3 = ImageMaster.loadImage("images/monsters/theBottom/boss/ghost/plasma3.png");
        this.shadow = ImageMaster.loadImage("images/monsters/theBottom/boss/ghost/shadow.png");
    }

    public void update() {
        this.effect.update();
        this.plasma1Angle += this.rotationSpeed;
        this.plasma2Angle += this.rotationSpeed / 2.0F;
        this.plasma3Angle += this.rotationSpeed / 3.0F;

        this.rotationSpeed = MathHelper.fadeLerpSnap(this.rotationSpeed, this.targetRotationSpeed);
        this.effect.speed = this.rotationSpeed;
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.m.tint.color);
        sb.draw(this.plasma3, this.m.drawX - 256.0F + this.m.animX + 12.0F * Settings.scale, this.m.drawY + this.m.animY + AbstractDungeon.sceneOffsetY + this.effect.y * 2.0F - 256.0F + BODY_OFFSET_Y, 256.0F, 256.0F, 512.0F, 512.0F, Settings.scale * 0.95F, Settings.scale * 0.95F, this.plasma3Angle, 0, 0, 512, 512, false, false);


        sb.draw(this.plasma2, this.m.drawX - 256.0F + this.m.animX + 6.0F * Settings.scale, this.m.drawY + this.m.animY + AbstractDungeon.sceneOffsetY + this.effect.y - 256.0F + BODY_OFFSET_Y, 256.0F, 256.0F, 512.0F, 512.0F, Settings.scale, Settings.scale, this.plasma2Angle, 0, 0, 512, 512, false, false);


        sb.draw(this.plasma1, this.m.drawX - 256.0F + this.m.animX, this.m.drawY + this.m.animY + AbstractDungeon.sceneOffsetY + this.effect.y * 0.5F - 256.0F + BODY_OFFSET_Y, 256.0F, 256.0F, 512.0F, 512.0F, Settings.scale, Settings.scale, this.plasma1Angle, 0, 0, 512, 512, false, false);


        sb.draw(this.shadow, this.m.drawX - 256.0F + this.m.animX + 12.0F * Settings.scale, this.m.drawY + this.m.animY + AbstractDungeon.sceneOffsetY + this.effect.y / 4.0F - 15.0F * Settings.scale - 256.0F + BODY_OFFSET_Y, 256.0F, 256.0F, 512.0F, 512.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 512, false, false);
    }
}





