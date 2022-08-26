package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Damage;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WeaponTemplate extends ItemTemplate {

	public static final float CRIT_CHANCE = 0.05f;

	public enum WeaponType {
		PISTOL(true, false, AreaLink.DistanceCategory.CLOSE, Set.of()),
		SMG(true, false, AreaLink.DistanceCategory.CLOSE, Set.of(AttackType.AUTO)),
		SHOTGUN(true, true, AreaLink.DistanceCategory.FAR, Set.of()),
		ASSAULT_RIFLE(true, true, AreaLink.DistanceCategory.FAR, Set.of(AttackType.AUTO)),
		SNIPER_RIFLE(true, true, AreaLink.DistanceCategory.DISTANT, Set.of()),
		KNIFE(false, false, AreaLink.DistanceCategory.NEAR, Set.of()),
		SWORD(false, true, AreaLink.DistanceCategory.NEAR, Set.of(AttackType.THRUST)),
		CLUB(false, true, AreaLink.DistanceCategory.NEAR, Set.of()),
		AXE(false, true, AreaLink.DistanceCategory.NEAR, Set.of(AttackType.SWEEP));
		
		public final boolean isRanged, isTwoHanded;
		public final AreaLink.DistanceCategory range;
		public final Set<AttackType> attacks;
		
		WeaponType(boolean isRanged, boolean isTwoHanded, AreaLink.DistanceCategory range, Set<AttackType> attacks) {
			this.isRanged = isRanged;
			this.isTwoHanded = isTwoHanded;
			this.range = range;
			this.attacks = attacks;
		}
	}

	public enum AttackType {
		AUTO, SWEEP, THRUST
	}
	
	private final WeaponType type;
	private final int damage;
	private final int rate;
	private final int critDamage;
	private final int clipSize;
	private final float accuracyBonus;
	private final float armorMult;
	private final boolean silenced;
	private final Damage.DamageType damageType;
	private final Set<String> ammoTypes;

	public WeaponTemplate(String ID, String name, Scene description, Map<String, Script> scripts, int price, WeaponType type, int damage, int rate, int critDamage, int clipSize, float accuracyBonus, float armorMult, boolean silenced, Damage.DamageType damageType, Set<String> ammoTypes) {
		super(ID, name, description, scripts, price);
		if(clipSize > 0 && ammoTypes.isEmpty() || clipSize == 0 && !ammoTypes.isEmpty()) throw new IllegalArgumentException("Weapon clip size and ammo type conflict: " + ID);
		if(type == null) throw new IllegalArgumentException("Weapon type cannot be null: " + ID);
		this.type = type;
		this.damage = damage;
		this.rate = rate;
		this.critDamage = critDamage;
		this.clipSize = clipSize;
		this.accuracyBonus = accuracyBonus;
		this.armorMult = armorMult;
		this.silenced = silenced;
		this.damageType = damageType;
		this.ammoTypes = ammoTypes;
	}

	@Override
	public boolean hasState() {
		return true;
	}
	
	public WeaponType getType() {
		return type;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public int getRate() {
		return rate;
	}
	
	public int getCritDamage() {
		return critDamage;
	}

	public AreaLink.DistanceCategory getRange() {
		return type.range;
	}
	
	public int getClipSize() {
		return clipSize;
	}

	public float getAccuracyBonus() {
		return accuracyBonus;
	}

	public float getArmorMult() {
		return armorMult;
	}

	public boolean isSilenced() {
		return silenced;
	}

	public Damage.DamageType getDamageType() {
		return damageType;
	}

	public Set<String> getAmmoTypes() {
		return ammoTypes;
	}

	public Actor.Skill getSkill() {
		switch(getType()) {
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
	public Set<String> getTags() {
		Set<String> tags = new HashSet<>();
		tags.add("weapon");
		if(getType().isRanged) {
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
		switch(getType()) {
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
	
}
