package com.pokebreedingmod.pokebreeding.breeding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.comm.PixelmonMovesetData;
import com.pixelmonmod.pixelmon.config.PixelmonEntityList;
import com.pixelmonmod.pixelmon.database.DatabaseMoves;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pokebreedingmod.pokebreeding.enums.EnumPokeCanLearn;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class Breed {
	
	private static EntityPixelmon pokemon;
	private static HashMap<String, String> itemChild = new HashMap<String, String>();
	
	public static EntityPixelmon getChild(EntityPlayer player, NBTTagCompound p1, NBTTagCompound p2) {
		String childName;
		String parentName;
		
		if(p1.getString("Name").equals("Ditto") == true)
			parentName = p2.getString("Name");
		else
			parentName = p1.getString("Name");
		
		fillItemChildrenList(); //adds all the pokes that evo with items to the list itemChild key:parent val:child
		childName = getChildName(parentName, player.worldObj);
		
		pokemon = (EntityPixelmon) PixelmonEntityList.createEntityByName(childName, player.worldObj);
		pokemon.getLvl().setLevel(1);
		inheritNature(p1, p2);
		inheritIVs(p1, p2);
		//inheritMoves(p1, p2);
		shinyChance();
		pokemon.setNickname("Baby "+pokemon.getName());
		return pokemon;
	}
	
	private static void inheritNature(NBTTagCompound p1, NBTTagCompound p2) {
		if(p1.getInteger("HeldItem")==12256)
			pokemon.setNature(EnumNature.getNatureFromIndex(p1.getShort("Nature")));
		else if(p2.getInteger("HeldItem")==12256)
			pokemon.setNature(EnumNature.getNatureFromIndex(p2.getShort("Nature")));
	}
	
	private static void inheritMoves(NBTTagCompound p1, NBTTagCompound p2) {
		//moves that both parents share that the child will inherit (max 3)
		List<String> movesInCommon = new ArrayList<String>();
		
		//Get First Parent Moveset
		List<String> p1MoveNames = new ArrayList<String>();
		PixelmonMovesetData[] p1Moveset = new PixelmonMovesetData[4];
		for (int i = 0; i < p1.getInteger("PixelmonNumberMoves"); i++) {
			int moveID = p1.getInteger("PixelmonMoveID" + Integer.toString(i));
			AttackBase attack = DatabaseMoves.getAttack(moveID).baseAttack;
			p1MoveNames.add(attack.getUnLocalizedName());
		}
		
		//Get Second Parent Moveset
		List<String> p2MoveNames = new ArrayList<String>();
		PixelmonMovesetData[] p2Moveset = new PixelmonMovesetData[4];
		for (int i = 0; i < p2.getInteger("PixelmonNumberMoves"); i++) {
			int moveID = p2.getInteger("PixelmonMoveID" + Integer.toString(i));
			AttackBase attack = DatabaseMoves.getAttack(moveID).baseAttack;
			p2MoveNames.add(attack.getUnLocalizedName());
		}

		//set moves that are present in both lists
		int moveToReplace = 1;
		pokemon.loadMoveset();
		for(int i=0;i<4; i++) 
		{
			try 
			{
				//If it doesn't know 4 moves.
				if(i >= p2MoveNames.size()) {
					break;
				}
				else if(p1MoveNames.contains(p2MoveNames.get(i)) && moveToReplace != 4) 
				{
					//ADD WHEN MOVES LIST IS COMPLETE
					//if(EnumPokeCanLearn.valueOf(pokemon.getName()).canLearn(p2MoveNames.get(i)))
					pokemon.getMoveset().set(moveToReplace, DatabaseMoves.getAttack(p2MoveNames.get(i)));
					moveToReplace+=1;
				}
					
			} catch(Exception e) 
			{
				e.printStackTrace();
			}
		}

	}
	
	private static void inheritIVs(NBTTagCompound p1, NBTTagCompound p2) {
		String[] stats = {"IVSpeed", "IVAttack", "IVDefence", "IVSpDef", "IVSpAtt", "IVHP"};
		//shell bell used as destiny knot (12276)
		if(p1.getInteger("HeldItem")==12276 || p2.getInteger("HeldItem")==12276) {
			IV[] childIVs = {new IV(), new IV(), new IV(), new IV(), new IV() };
			List<String> chosenStats = new ArrayList<String>();
			for(int i=0;i<5;i++) 
			{
				//So that the same stat isn't chosen twice
				String stat = chooseStat(stats);
				do 
				{
					stat = chooseStat(stats);
				} while(chosenStats.contains(stat));
				chosenStats.add(stat);
				//End choosing new stat
				
				childIVs[i]= new IV(stat, chooseParent(p1, p2).getInteger(stat));
			}
			
			//sets child IVs
			for(int i=0;i<5;i++) 
			{
				
				if(childIVs[i].getStat() == "IVSpeed")
					pokemon.stats.IVs.Speed = childIVs[i].getValue();
				else if(childIVs[i].getStat() == "IVAttack")
					pokemon.stats.IVs.Attack = childIVs[i].getValue();
				else if(childIVs[i].getStat() == "IVDefence")
					pokemon.stats.IVs.Defence = childIVs[i].getValue();
				else if(childIVs[i].getStat() == "IVSpDef")
					pokemon.stats.IVs.SpDef = childIVs[i].getValue();
				else if(childIVs[i].getStat() == "IVSpAtt")
					pokemon.stats.IVs.SpAtt = childIVs[i].getValue();
				else if(childIVs[i].getStat() == "IVHP")
					pokemon.stats.IVs.HP = childIVs[i].getValue();	
			}
		}
		//Neither parent is holding a destiny knot, only choose 3 IV stats for child to inherit
		else {
			IV[] childIVs = {new IV(), new IV(), new IV()};
			List<String> chosenStats = new ArrayList<String>();
			for(int i=0;i<3;i++) 
			{
				//So that the same stat isn't chosen twice
				String stat = chooseStat(stats);
				do 
				{
					stat = chooseStat(stats);
				} while(chosenStats.contains(stat));
				chosenStats.add(stat);
				//End choosing new stat
				
				childIVs[i]= new IV(stat, chooseParent(p1, p2).getInteger(stat));
			}
			
			//sets child IVs
			for(int i=0;i<3;i++) 
			{
				
				if(childIVs[i].getStat() == "IVSpeed")
					pokemon.stats.IVs.Speed = childIVs[i].getValue();
				else if(childIVs[i].getStat() == "IVAttack")
					pokemon.stats.IVs.Attack = childIVs[i].getValue();
				else if(childIVs[i].getStat() == "IVDefence")
					pokemon.stats.IVs.Defence = childIVs[i].getValue();
				else if(childIVs[i].getStat() == "IVSpDef")
					pokemon.stats.IVs.SpDef = childIVs[i].getValue();
				else if(childIVs[i].getStat() == "IVSpAtt")
					pokemon.stats.IVs.SpAtt = childIVs[i].getValue();
				else if(childIVs[i].getStat() == "IVHP")
					pokemon.stats.IVs.HP = childIVs[i].getValue();	
			}
		}
		
	}
	
	private static String chooseStat(String[] stats) {
		//chooses a random number between 1 - 6
		Random r = new Random();
		int randNum = (int)(r.nextInt(6-1)+1);
		return stats[randNum-1];
	}
	
	private static NBTTagCompound chooseParent(NBTTagCompound p1, NBTTagCompound p2) {
		//choose a random number between 1-2
		int randNum = 1 + (int)(Math.random() * ((2 - 1) + 1));
		if(randNum==1)
			return(p1);
		else
			return(p2);
	}
	
	private static void shinyChance() {
		Random r = new Random();
		int chance = 8192; //1 in 8192 of being shiny
		int randNum = (int)(r.nextInt(chance-1)+1);
		if(randNum == 67)
			pokemon.setIsShiny(true);
		else
			pokemon.setIsShiny(false);
	}
	
	private boolean calculateChance(int percentSuccess) {
		//chooses a random number between 1 - 100
		int randNum = 1 + (int)(Math.random() * ((100 - 1) + 1));
		
		if(randNum <= percentSuccess)
			return true;
		else
			return false;
	}
	
	private static String getChildName(String parentName, World w) {		
		EntityPixelmon parentSpecies = (EntityPixelmon) PixelmonEntityList.createEntityByName(parentName, w);
		EnumPokemon[] childrenSpecies = parentSpecies.getPreEvolutions();
		
		if(itemChild.containsKey(parentName))
			return itemChild.get(parentName);
		else if(childrenSpecies.length != 0)
			return childrenSpecies[childrenSpecies.length-1].toString(); //first evolution
		else
			return parentName; //child is same species as parent
		
	}
	
	private static void fillItemChildrenList() {
		itemChild.put("Mamoswine", "Swinub");
		itemChild.put("Yanmega", "Yanma");
		itemChild.put("Crobat", "Zubat");
		itemChild.put("Scizor", "Scyther");
		itemChild.put("Wigglytuff", "Igglybuff");
		itemChild.put("Umbreon", "Eevee");
		itemChild.put("Blissey", "Chansey");
		itemChild.put("Chandelure", "Litwick");
		itemChild.put("Raichu", "Pichu");
		itemChild.put("Vileplume", "Oddish");
		itemChild.put("Clefable", "Clefairy");
		itemChild.put("Electivire", "Elekid");
		itemChild.put("Magmortar", "Magby");
		itemChild.put("Poryon-Z", "Porygon");
		itemChild.put("Poryon2", "Porygon");
		itemChild.put("Exeggutor", "Exeggcute");
		itemChild.put("Arcanine", "Growlithe");
		itemChild.put("Starmie", "Staryu");
		itemChild.put("Rhyperior", "Rhyhorn");
		itemChild.put("Bellossom", "Oddish");
		itemChild.put("Victreebell", "Bellsprout");
		itemChild.put("Ninetales", "Vulpix");
		itemChild.put("Poliwrath", "Poliwag");
		itemChild.put("Politoed", "Poliwag");
		itemChild.put("Espeon", "Eevee");
		itemChild.put("Vaporeon", "Eevee");
		itemChild.put("Jolteon", "Eevee");
		itemChild.put("Leafeon", "Eevee");
		itemChild.put("Flareon", "Eevee");
		itemChild.put("Glaceon", "Eevee");
	}
	
	//holds IV data
	private static class IV {
		private String stat;
		private int val;
		
		public IV() {
			stat = "IVSpeed";
			val = 1;
		}
		
		public IV(String s, int n) {
			stat = s;
			val = n;
		}
		
		public String getStat() {
			return stat;
		}
		
		public int getValue() {
			return val;
		}
		
	}

}
