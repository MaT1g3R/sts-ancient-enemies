package AncientEnemies;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;

import java.io.IOException;
import java.util.Properties;

@SpireInitializer
public class AncientEnemies implements EditStringsSubscriber, PostInitializeSubscriber {
    public static SpireConfig modConfig;

    public AncientEnemies() {
        BaseMod.subscribe(this);
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(PowerStrings.class, "AncientEnemies/localization/powers.json");
    }

    public static void initialize() {
        AncientEnemies modInitializer = new AncientEnemies();

        Properties defaults = new Properties();
        defaults.put("ascension", Boolean.toString(false));
        try {
            modConfig = new SpireConfig("AncientEnemies", "Config", defaults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean ascension() {
        return modConfig != null && modConfig.getBool("ascension");
    }

    @Override
    public void receivePostInitialize() {
        ModPanel settingsPanel = new ModPanel();
        ModLabeledToggleButton ascensionButton = new ModLabeledToggleButton("Enable Ascension", 350, 700, Settings.CREAM_COLOR, FontHelper.charDescFont, ascension(), settingsPanel, l -> {
        }, button -> {
            if (modConfig != null) {
                modConfig.setBool("ascension", button.enabled);
                try {
                    modConfig.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        settingsPanel.addUIElement(ascensionButton);
        BaseMod.registerModBadge(ImageMaster.loadImage("AncientEnemies/modBadge.png"), "AncientEnemies", "Byron, vmService", "Reverts all enemy encounters to their November 2017 versions.", settingsPanel);
    }

    public static boolean afterAscension(int level) {
        return ascension() && AbstractDungeon.ascensionLevel >= level;
    }
}
