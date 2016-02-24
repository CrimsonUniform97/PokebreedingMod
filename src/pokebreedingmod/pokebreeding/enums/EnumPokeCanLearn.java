package com.pokebreedingmod.pokebreeding.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum EnumPokeCanLearn {
	
	Eevee(Arrays.asList("Tackle","Withdraw","Sand-Attack")),
	Charmander(Arrays.asList("Tackle","Withdraw","Sand-Attack"));
	
	//private String[] learnableMoves;
	private List<String> learnableMoves = new ArrayList<String>();
	
	private EnumPokeCanLearn(List<String> movesList) 
	{
		for(int i=0; i<movesList.size(); i++) 
		{
			learnableMoves.add(movesList.get(i));
		}
	}
	
	public List<String> getMoves(String poke) {
		return learnableMoves;
	}
	
	public String getPokemonName() {
		return this.toString();
	}
	
	public boolean canLearn(String moveName) {
		if(learnableMoves.contains(moveName))
			return true;
		else
			return false;
	}
	
	public boolean pokeCanLearn(String pokemon, String moveName) {
		if(EnumPokeCanLearn.valueOf(pokemon).canLearn(moveName))
			return true;
		else
			return false;
	}

}
