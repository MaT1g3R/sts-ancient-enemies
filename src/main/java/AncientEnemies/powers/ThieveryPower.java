 package AncientEnemies.powers;
 
 import AncientEnemies.monsters.exordium.Looter;
 import com.megacrit.cardcrawl.core.AbstractCreature;
 import com.megacrit.cardcrawl.core.CardCrawlGame;
 import com.megacrit.cardcrawl.localization.PowerStrings;
 import com.megacrit.cardcrawl.powers.AbstractPower;

 public class ThieveryPower extends AbstractPower {
   public static final String POWER_ID = "Thievery";
   private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Thievery");
   public static final String NAME = powerStrings.NAME;
   public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
   
   public ThieveryPower(AbstractCreature owner) {
     this.name = NAME;
     this.ID = "Thievery";
     this.owner = owner;
     this.amount = -1;
     updateDescription();
     loadRegion("thievery");
   }
 
   
   public void updateDescription() {
     this.description = Looter.NAME + DESCRIPTIONS[0];
   }
 }
