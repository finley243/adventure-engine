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
		PISTOL(true, false, AreaLink.DistanceCategory.CLOSE, Set.of(), "rangedHit", "rangedHitRepeat", "rangedMiss", "rangedMissRepeat", "rangedHitLimb", "rangedHitLimbRepeat", "rangedMissLimb", "rangedMissLimbRepeat"),
		SMG(true, false, AreaLink.DistanceCategory.CLOSE, Set.of(AttackType.AUTO), "rangedHit", "rangedHitRepeat", "rangedMiss", "rangedMissRepeat", "rangedHitLimb", "rangedHitLimbRepeat", "rangedMissLimb", "rangedMissLimbRepeat"),
		SHOTGUN(true, true, AreaLink.DistanceCategory.FAR, Set.of(), "rangedHit", "rangedHitRepeat", "rangedMiss", "rangedMissRepeat", "rangedHitLimb", "rangedHitLimbRepeat", "rangedMissLimb", "rangedMissLimbRepeat"),
		ASSAULT_RIFLE(true, true, AreaLink.DistanceCategory.FAR, Set.of(AttackType.AUTO), "rangedHit", "rangedHitRepeat", "rangedMiss", "rangedMissRepeat", "rangedHitLimb", "rangedHitLimbRepeat", "rangedMissLimb", "rangedMissLimbRepeat"),
		SNIPER_RIFLE(true, true, AreaLink.DistanceCategory.DISTANT, Set.of(), "rangedHit", "rangedHitRepeat", "rangedMiss", "rangedMissRepeat", "rangedHitLimb", "rangedHitLimbRepeat", "rangedMissLimb", "rangedMissLimbRepeat"),
		KNIFE(false, false, AreaLink.DistanceCategory.NEAR, Set.of(), "meleeHit", "meleeHitRepeat", "meleeMiss", "meleeMissRepeat", "meleeHitLimb", "meleeHitLimbRepeat", "meleeMissLimb", "meleeMissLimbRepeat"),
		SWORD(false, true, AreaLink.DistanceCategory.NEAR, Set.of(AttackType.THRUST), "meleeHit", "meleeHitRepeat", "meleeMiss", "meleeMissRepeat", "meleeHitLimb", "meleeHitLimbRepeat", "meleeMissLimb", "meleeMissLimbRepeat"),
		CLUB(false, true, AreaLink.DistanceCategory.NEAR, Set.of(), "meleeHit", "meleeHitRepeat", "meleeMiss", "meleeMissRepeat", "meleeHitLimb", "meleeHitLimbRepeat", "meleeMissLimb", "meleeMissLimbRepeat"),
		AXE(false, true, AreaLink.DistanceCategory.NEAR, Set.of(AttackType.SWEEP), "meleeHit", "meleeHitRepeat", "meleeMiss", "meleeMissRepeat", "meleeHitLimb", "meleeHitLimbRepeat", "meleeMissLimb", "meleeMissLimbRepeat");
		
		public final boolean isRanged, isTwoHanded;
		public final AreaLink.DistanceCategory primaryRange;
		public final Set<AttackType> attacks;
		public final String hitPhrase;
		public final String hitPhraseRepeat;
		public final String missPhrase;
		public final String missPhraseRepeat;
		public final String limbHitPhrase;
		public final String limbHitPhraseRepeat;
		public final String limbMissPhrase;
		public final String limbMissPhraseRepeat;
		
		WeaponType(boolean isRanged, boolean isTwoHanded, AreaLink.DistanceCategory primaryRange, Set<AttackType> attacks, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat, String limbHitPhrase, String limbHitPhraseRepeat, String limbMissPhrase, String limbMissPhraseRepeat) {
			this.isRanged = isRanged;
			this.isTwoHanded = isTwoHanded;
			this.primaryRange = primaryRange;
			this.attacks = attacks;
			this.hitPhrase = hitPhrase;
			this.hitPhraseRepeat = hitPhraseRepeat;
			this.missPhrase = missPhrase;
			this.missPhraseRepeat = missPhraseRepeat;
			this.limbHitPhrase = limbHitPhrase;
			this.limbHitPhraseRepeat = limbHitPhraseRepeat;
			this.limbMissPhrase = limbMissPhrase;
			this.limbMissPhraseRepeat = limbMissPhraseRepeat;
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

	public AreaLink.DistanceCategory getPrimaryRange() {
		return type.primaryRange;
	}

	public String getHitPhrase() {
		return type.hitPhrase;
	}

	public String getHitRepeatPhrase() {
		return type.hitPhraseRepeat;
	}

	public String getMissPhrase() {
		return type.missPhrase;
	}

	public String getMissRepeatPhrase() {
		return type.missPhraseRepeat;
	}

	public String getLimbHitPhrase() {
		return type.limbHitPhrase;
	}

	public String getLimbHitRepeatPhrase() {
		return type.limbHitPhraseRepeat;
	}

	public String getLimbMissPhrase() {
		return type.limbMissPhrase;
	}

	public String getLimbMissRepeatPhrase() {
		return type.limbMissPhraseRepeat;
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
