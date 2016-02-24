package com.pokebreedingmod.pokebreeding.commands;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumPokeballs;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerNotLoadedException;
import com.pokebreedingmod.pokebreeding.PokeBreeding;
import com.pokebreedingmod.pokebreeding.breeding.Breed;
import com.pokebreedingmod.pokebreeding.breeding.PokeEgg;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandBreed extends CommandBase{
	@Override
	public String getCommandName() {
		return "breed";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/breed (player) <parent1Slot> <parent2Slot>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length < 2) {
			sender.addChatMessage(new ChatComponentTranslation("Incorrect usage. " + getCommandUsage(sender)));
			return;
		}
		
		int slot1 = 0;
		int slot2 = 0;
		EntityPlayer player;
		
		if(args.length == 3) {
			try {
				slot1 = Integer.parseInt(args[1]);
				slot2 = Integer.parseInt(args[2]);
			} catch(NumberFormatException nfe) {
				sender.addChatMessage(new ChatComponentTranslation("The slot must be a number between 1 and 6"));
				return;
			}
			player = getPlayer(sender, args[0]);
			if (player == null) {
				sender.addChatMessage(new ChatComponentTranslation(args[0] + " does not exist."));
				return;
			}
		}
		else {
			try {
				slot1 = Integer.parseInt(args[0]);
				slot2 = Integer.parseInt(args[1]);
			} catch(NumberFormatException nfe) {
				sender.addChatMessage(new ChatComponentTranslation("The slot must be a number between 1 and 6"));
				return;
			}
			player = getPlayer(sender, sender.getCommandSenderName());
		}
		
		//if the player is the command sender, no cooldown
		if(PokeBreeding.cooldowns.containsKey(player.getCommandSenderName()) && player.getCommandSenderName() != sender.getCommandSenderName()) {
			if(player.worldObj.getTotalWorldTime()-24000 <= PokeBreeding.cooldowns.get(player.getCommandSenderName())) {
				//long timeLeft = (long)(PokeBreeding.cooldowns.get(player.getCommandSenderName()))-(player.worldObj.getTotalWorldTime()-24000)/10;
				//long minsLeft = (long)timeLeft/60;
				//int secsLeft = (int) (timeLeft%60);
				sender.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.YELLOW+"Pokemon can only be bred once per MC day"));
				return;
			}
		}


		
		if(slot1 == slot2) {
			player.addChatMessage(new ChatComponentTranslation("You can't choose the same slot twice."));
			return;
		}
	
		NBTTagCompound parent1 = null;
		NBTTagCompound parent2 = null;
		
		if( slot1 < 1 || slot1 > 6) {
			player.addChatMessage(new ChatComponentTranslation("The slot number must be between 1 and 6"));
			return;
		}
		if( slot2 < 1 || slot2 > 6) {
			player.addChatMessage(new ChatComponentTranslation("The slot number must be between 1 and 6"));
			return;
		}
		
		
		
		try {
			parent1 = PixelmonStorage.PokeballManager.getPlayerStorage((EntityPlayerMP) player).getList()[slot1-1];
			parent2 = PixelmonStorage.PokeballManager.getPlayerStorage((EntityPlayerMP) player).getList()[slot2-1];
		} catch (PlayerNotLoadedException e) {
			FMLLog.severe("[PokeBreeding] Error retrieving pokemon in slot");
			e.printStackTrace();
			return;
		}
		if(parent1 == null || parent2 == null) {
			player.addChatMessage(new ChatComponentTranslation("There isn't a pokemon in that slot."));
			return;
		}
		
		boolean isDitto = false;
		boolean isNidoCombo = false;
		
		//Check if a parent is a ditto
		if(parent1.getString("Name").equalsIgnoreCase("Ditto") && parent2.getString("Name").equalsIgnoreCase("Ditto")) 
		{
			player.addChatMessage(new ChatComponentTranslation("Ditto can't breed with another Ditto"));
			return;
		}
		else if(parent1.getString("Name").equalsIgnoreCase("Ditto"))
			isDitto = true;
		else if(parent2.getString("Name").equalsIgnoreCase("Ditto"))
			isDitto = true;
		
		//Check if parents are nidoking/nidoqueen
		if(parent1.getString("Name").equalsIgnoreCase("Nidoking") && parent2.getString("Name").equalsIgnoreCase("Nidoqueen"))
			isNidoCombo = true;
		else if(parent2.getString("Name").equalsIgnoreCase("Nidoking") && parent1.getString("Name").equalsIgnoreCase("Nidoqueen"))
			isNidoCombo = true;
		
		
		
		//check for special conditions, Nidoking/nidoqueen, ditto+another, ect. before breeding
		if(parent1 == null || parent2 == null) {
			player.addChatMessage(new ChatComponentTranslation("That slot is currently empty"));
			return;
		}
		else if(isLegendary(parent1, parent2)) {
			player.addChatMessage(new ChatComponentTranslation("Sorry! You can't breed that pokemon!"));
			return;
		}
		else if(!parent1.getString("Name").equalsIgnoreCase(parent2.getString("Name")) && !isNidoCombo && !isDitto) {
			player.addChatMessage(new ChatComponentTranslation("Only two of the same species pokemon can be bred"));
			return;
		}
		else if(isSameGender(parent1, parent2) && !isDitto) {
			player.addChatMessage(new ChatComponentTranslation("Only a male and female pokemon can breed."));
			return;
		}
		else {
			EntityPixelmon newborn = Breed.getChild(player, parent1, parent2);
			try {
				newborn.caughtBall = EnumPokeballs.LoveBall;
				player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN+"You found "+parent1.getString("Name")+" holding an egg!"));
				new PokeEgg(newborn, player);
				PokeBreeding.cooldowns.put(player.getCommandSenderName(), player.worldObj.getTotalWorldTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private boolean isLegendary(NBTTagCompound p1, NBTTagCompound p2) {
		String[] legends = {"Articuno", "Zapdos", "Moltres", "Mewtwo", "Mew", "Rayquaza",
				"Groudon", "Kyogre"};
		for(int i=0; i < legends.length; i++) {
			if(legends[i].equalsIgnoreCase(p1.getString("Name")))
				return true;
			else if(legends[i].equalsIgnoreCase(p2.getString("Name")))
				return true;
		}
		return false;
	}
	
	private boolean isSameGender(NBTTagCompound p1, NBTTagCompound p2) {
		if(p1.getBoolean("IsMale") == true && p2.getBoolean("IsMale") == true)
			return true;
		else if(p1.getBoolean("IsMale") == false && p2.getBoolean("IsMale") == false)
			return true;
		else
			return false;
	}
	
	
}
