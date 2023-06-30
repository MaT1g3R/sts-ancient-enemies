package AncientEnemies.monsters.exordium;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.vfx.BobEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TheGuardianOrb {
    public static final String ID = "TheGuardianOrb";
    private static final Logger logger = LogManager.getLogger(TheGuardianOrb.class.getName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("TheGuardianOrb");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int RAW_W = 512;
    public boolean activated = false;
    public boolean closeUp = false;
    public boolean justClosedUp = false;
    public int threshold;
    private final BobEffect effect = new BobEffect(0.5F);
    private final Texture fgImg;
    private final Texture bgImg;
    private final Color color = new Color(1.0F, 1.0F, 1.0F, 0.0F);
    private final Color textColor = new Color(0.3F, 1.0F, 1.0F, 0.0F);
    public TheGuardianOrb() {
        this.fgImg = ImageMaster.loadImage("images/monsters/theBottom/boss/guardian_orb_f.png");
        this.bgImg = ImageMaster.loadImage("images/monsters/theBottom/boss/guardian_orb_b.png");
    }

    public void activate(int threshold) {
        this.activated = true;
        this.threshold = threshold;
        this.justClosedUp = false;
        this.closeUp = false;
    }

    public void deactivate() {
        this.activated = false;
    }

    public void update() {
        this.effect.update();
        if (this.activated) {
            this.color.a = MathHelper.fadeLerpSnap(this.color.a, 1.0F);
            this.textColor.a = this.color.a;
        } else {
            this.color.a = MathHelper.fadeLerpSnap(this.color.a, 0.0F);
            this.textColor.a = this.color.a;
        }
    }

    public void subtract(int amount) {
        this.threshold -= amount;
        if (this.threshold <= 0) {
            this.threshold = 0;
            if (!this.closeUp) {
                this.justClosedUp = true;
                this.closeUp = true;
            }
        }
        logger.info(Integer.valueOf(this.threshold));
    }

    public void render(SpriteBatch sb, float x, float y) {
        sb.setColor(this.color);
        sb.draw(this.bgImg, x - 512.0F * Settings.scale / 2.0F, y + this.effect.y * 3.0F - 50.0F * Settings.scale, 512.0F * Settings.scale, 512.0F * Settings.scale, 0, 0, 512, 512, false, false);


        sb.draw(this.fgImg, x - 512.0F * Settings.scale / 2.0F, y + this.effect.y * 3.2F - 50.0F * Settings.scale, 512.0F * Settings.scale, 512.0F * Settings.scale, 0, 0, 512, 512, false, false);


        FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont,


                Integer.toString(this.threshold), x + 168.0F * Settings.scale, y + 332.0F * Settings.scale + this.effect.y * 3.2F, this.textColor);
    }
}





