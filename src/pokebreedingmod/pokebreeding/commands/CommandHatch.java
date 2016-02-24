package com.pokebreedingmod.pokebreeding.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandHatch extends CommandBase{
	@Override
	public String getCommandName() {
		return "hatch";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/hatch";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		EntityPlayer player = getPlayer(sender, sender.getCommandSenderName());
		if (player.getHeldItem() != null) 
		{
			ItemStack heldItem = player.getHeldItem();
			//if the player is sprinting with a pokemon egg in hand
			if (Item.getIdFromItem(heldItem.getItem()) == Item.getIdFromItem(Items.egg)) 
			{
				boolean isHoldingPokeEgg = false;
		        if(heldItem.stackTagCompound == null ) {
					player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.YELLOW+"That isn't a pokemon egg"));
		        	return;
		        }
		        else if(heldItem.stackTagCompound.hasKey("isPokemonEgg") != false) {
					isHoldingPokeEgg = heldItem.stackTagCompound.getBoolean("isPokemonEgg");	
		        }
					
				if(isHoldingPokeEgg == true) {
					player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN+"One step left!"));
					heldItem.setStackDisplayName(EnumChatFormatting.LIGHT_PURPLE+"Poke-Egg "+EnumChatFormatting.AQUA+"(Steps:1)");
					heldItem.stackTagCompound.setInteger("steps", 1);
				}
		   
			}
			else {
				player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.YELLOW+"That isn't a pokemon egg."));
				return;
			}
		}
		else 
		{
		player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.YELLOW+"Your hand is empty"));
		return;
		}
	}

}
