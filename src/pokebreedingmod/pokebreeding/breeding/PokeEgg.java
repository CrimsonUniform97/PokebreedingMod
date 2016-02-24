package com.pokebreedingmod.pokebreeding.breeding;


import java.util.Random;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

public class PokeEgg {
	
	
	
	public PokeEgg(EntityPixelmon baby, EntityPlayer player) {
		ItemStack is = new ItemStack(Items.egg, 1);
        if(is.stackTagCompound == null )
            is.setTagCompound( new NBTTagCompound( ) );
		baby.writeEntityToNBT(is.stackTagCompound);
		
		Random r = new Random();
		int steps = (int)(r.nextInt(550-250)+250);
		is.stackTagCompound.setInteger("steps", steps);
		is.stackTagCompound.setBoolean("isPokemonEgg", true);
		is.setStackDisplayName(EnumChatFormatting.LIGHT_PURPLE+"Poke-Egg "+EnumChatFormatting.AQUA+"(Steps:"+steps+")");
		player.inventory.addItemStackToInventory(is);
		return;
		
	}

	
}
