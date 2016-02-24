package com.pokebreedingmod.pokebreeding;

import com.pokebreedingmod.pokebreeding.TickHandler;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
//import cpw.mods.fml.common.registry.TickRegistry;
//import cpw.mods.fml.relauncher.Side;

public class CommonProxy implements IGuiHandler {

	public void registerRenderers() {
	}

	public World GetClientWorld() {
		return null;
	}
	
	public void registerPacketHandlers() {
	}

	public void registerKeyBindings() {
	}

	public ModelBase loadModel(String name) {
		return null;
	}
	
	public ModelBase loadFlyingModel(String name) {
		return null;
	}


	public void registerSounds() {
	}


	public void loadEvents() {
	}

	
	public void registerInteractions(){
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}
}
