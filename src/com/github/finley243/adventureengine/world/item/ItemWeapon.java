package com.github.finley243.adventureengine.world.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.action.ActionInspect.InspectType;
import com.github.finley243.adventureengine.action.attack.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.template.WeaponTemplate;

public class ItemWeapon extends ItemEquippable {
	
	public static final float CRIT_CHANCE = 0.05f;
	
	private final WeaponTemplate stats;
	private int ammo;
	
	public ItemWeapon(Game game, String ID, Area area, boolean isGenerated, WeaponTemplate stats) {
		super(game, isGenerated, ID, area, stats.getName(), stats.getDescription(), stats.getScripts());
		this.stats = stats;
		this.ammo = stats.getClipSize();
	}

	@Override
	public Set<String> getTags() {
		Set<String> tags = new HashSet<>();
		tags.add("weapon");
		if(stats.getType().isRanged) {
			tags.add("weapon_ranged");
		} else {
			tags.add("weapon_melee");
		}
		switch(getSkill()) {
			case HANDGUNS:
				tags.add("weapon_handgun");
				break;
			case LONG_ARMS:
				tags.add("weapon_long_arm");
				break;
		}
		switch(stats.getType()) {
			case PISTOL:
				tags.add("weapon_pistol");
				break;
			case SMG:
				tags.add("weapon_smg");
				break;
			case SHOTGUN:
				tags.add("weapon_shotgun");
				break;
			case ASSAULT_RIFLE:
				tags.add("weapon_assault_rifle");
				break;
			case SNIPER_RIFLE:
				tags.add("weapon_sniper_rifle");
				break;
			case KNIFE:
				tags.add("weapon_knife");
				break;
			case SWORD:
				tags.add("weapon_sword");
				break;
			case CLUB:
				tags.add("weapon_club");
				break;
			case AXE:
				tags.add("weapon_axe");
				break;
		}
		return tags;
	}
	
	@Override
	public int getPrice() {
		return stats.getPrice();
	}
	
	@Override
	public String getTemplateID() {
		return stats.getID();
	}
	
	public boolean isRanged() {
		return stats.getType().isRanged;
	}
	
	public int getDamage() {
		return stats.getDamage();
	}
	
	public int getRate() {
		return stats.getRate();
	}
	
	public int getCritDamage() {
		return stats.getCritDamage();
	}

	public int getRangeMin() {
		return stats.getRangeMin();
	}

	public int getRangeMax() {
		return stats.getRangeMax();
	}

	public float getAccuracyBonus() {
		return stats.getAccuracyBonus();
	}
	
	public void reloadFull() {
		ammo = stats.getClipSize();
	}

	public int getClipSize() {
		return stats.getClipSize();
	}

	public int getAmmoRemaining() {
		return ammo;
	}

	public float getAmmoFraction() {
		if(stats.getClipSize() == 0) return 1.0f;
		return ((float) ammo) / ((float) stats.getClipSize());
	}
	
	public void consumeAmmo(int amount) {
		ammo -= amount;
	}

	public boolean isSilenced() {
		return stats.isSilenced();
	}

	public Actor.Skill getSkill() {
		switch(stats.getType()) {
			case PISTOL:
			case SMG:
				return Actor.Skill.HANDGUNS;
			case SHOTGUN:
			case ASSAULT_RIFLE:
			case SNIPER_RIFLE:
				return Actor.Skill.LONG_ARMS;
			case KNIFE:
			case SWORD:
			case CLUB:
			case AXE:
				return Actor.Skill.MELEE;
		}
		return null;
	}

	@Override
	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = super.equippedActions(subject);
		for(Actor target : subject.getVisibleActors()) {
			if(target != subject && target.isActive()) {
				if(stats.getType().isRanged) { // Ranged
					actions.add(new ActionRangedAttack(this, target));
					for(Limb limb : target.getLimbs()) {
						actions.add(new ActionRangedAttackTargeted(this, target, limb));
					}
					if(stats.getType().attacks.contains(WeaponTemplate.AttackType.AUTO)) {
						actions.add(new ActionRangedAttackAuto(this, target));
					}
				} else { // Melee
					actions.add(new ActionMeleeAttack(this, target));
					for(Limb limb : target.getLimbs()) {
						actions.add(new ActionMeleeAttackTargeted(this, target, limb));
					}
				}
			}
		}
		if(getClipSize() > 0) {
			actions.add(new ActionWeaponReload(this));
		}
		if(this.getDescription() != null) {
			actions.add(new ActionInspect(this, InspectType.EQUIPPED));
		}
		return actions;
	}

	@Override
	public void loadState(SaveData saveData) {
		switch(saveData.getParameter()) {
			case "ammo":
				this.ammo = saveData.getValueInt();
				break;
			default:
				super.loadState(saveData);
				break;
		}
	}

	@Override
	public List<SaveData> saveState() {
		List<SaveData> state = super.saveState();
		if(ammo != stats.getClipSize()) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "ammo", ammo));
		}
		return state;
	}

}
