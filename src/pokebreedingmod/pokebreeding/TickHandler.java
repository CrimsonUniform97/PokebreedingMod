package com.pokebreedingmod.pokebreeding;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerNotLoadedException;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TickHandler {
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
	    // if (event.phase == TickEvent.Phase.END) {
		EntityPlayer player = event.player;
		if (player.getHeldItem() != null) {
			ItemStack heldItem = player.getHeldItem();
			//if the player is sprinting with a pokemon egg in hand
			
			if (heldItem.getItem() == Items.egg && player.isSprinting() && !player.isRiding()) {
				boolean isHoldingPokeEgg = false;
		        if(heldItem.stackTagCompound == null )
		            return;
		        
				if(heldItem.stackTagCompound.hasKey("isPokemonEgg") != false)
					isHoldingPokeEgg = heldItem.stackTagCompound.getBoolean("isPokemonEgg");
				
				long lastPlayerTime;
				if(PokeBreeding.breedWorldTime.get(player.getCommandSenderName())==null) {
					lastPlayerTime = player.worldObj.getWorldTime();
					PokeBreeding.breedWorldTime.put(player.getCommandSenderName(),  player.worldObj.getWorldTime());
				}
				else
					lastPlayerTime = PokeBreeding.breedWorldTime.get(player.getCommandSenderName());
				
				if(isHoldingPokeEgg == true)
				{
					int currSteps = heldItem.getTagCompound().getInteger("steps");
					long worldTime = player.worldObj.getWorldTime();
					//if steps are below 1, hatch the egg!
					if(currSteps < 1) {
						try {
							EntityPixelmon poke = new EntityPixelmon(player.worldObj);
							poke.readEntityFromNBT(heldItem.getTagCompound());
							PixelmonStorage.PokeballManager.getPlayerStorage((EntityPlayerMP) player).addToParty(poke);
							((EntityPlayer) player).addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN+"Oh Look! A Baby "+poke.getName()+" has hatched!"));
							player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
						} catch (PlayerNotLoadedException e) {}
					}
					else if(worldTime%10 == 0 && worldTime != lastPlayerTime) 
					{
						if(hasFlameBodyInParty(player))
							currSteps -= 2;
						else
							currSteps -= 1;
						heldItem.stackTagCompound.setInteger("steps", currSteps);
						heldItem.setStackDisplayName(EnumChatFormatting.LIGHT_PURPLE+"Poke-Egg "+EnumChatFormatting.AQUA+"(Steps:"+currSteps+")");
						PokeBreeding.breedWorldTime.put(player.getCommandSenderName(), worldTime);
					}
				}
			}
		}
	}
	
	//Returns true if a pokemon in the player's party has the ability flamebody
	private boolean hasFlameBodyInParty(EntityPlayer player) {
		try {
			PlayerStorage pokeStorage = PixelmonStorage.PokeballManager.getPlayerStorage((EntityPlayerMP) player);
			NBTTagCompound[] pokes = pokeStorage.partyPokemon;
			for(int i=0; i<6; i++) {
				if(pokes[i] != null) {
					if(pokes[i].getString("Ability") == "FlameBody")
						return true;
				}
			}
		} catch (PlayerNotLoadedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
			
		return false;
	}

	
}
