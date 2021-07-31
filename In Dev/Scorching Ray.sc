!script {{  
  --#title|Scorching Ray
  --#reentrant|ScorchingRay

	--/|Need to add logic to ask user if they want all to hit one target, or to select another target.

	--/|Need to add logic to deduct a spell slot.

  --/|You create three rays of fire and hurl them at targets within range. You can hurl them at one target or several.
  --/|Make a ranged spell attack for each ray. On a hit, the target takes 2d6 fire damage.
  --/|At Higher Levels. When you cast this spell using a spell slot of 3rd level or higher, you create one additional ray for each slot level above 2nd.
  --/|Range: 120ft

  --#emoteText|@{selected|token_name} attacks @{target|token_name}
  --=SpellAtkBonus|@{selected|wisdom_mod} + @{selected|pb}
  --#rightsub|Atk Bonus: +[$SpellAtkBonus.Total]
  --#titleCardBackground|#932729
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF  
  --#debug|1
  --#sourceToken|@{selected|token_id}
  --#targetToken|@{target|token_id}

  --&STokenId|@{selected|token_id}
  --&TTokenId1|@{target|token_id}
  --&LevelSpell|?{What level spell? (Will auto stop when target falls)?|2|3|4|5|6|7|8|9}
  --=ShotCnt|[&LevelSpell] - 2 + 3
  --#leftsub|[$ShotCnt.Total] Rays
  --&AtkDice|?{Roll Type?|Normal,1d20|Advantage,2d20kh1|Disadvantage,2d20kl1}



  --X|


--:SHOOT_RAY|SourceToken; Rays Remaining; AtkDice; Total Damage
  --~Arg|string;split;\;[&reentryval]
  --&STokenId|[&Arg1]
  --=RR|[&Arg2]
  --&AtkDice|[&Arg3]
  --=TD|[&Arg4]
  --#sourceToken|[&STokenId]
  --#targetToken|@{target|token_id}
  --#title|Set Updated Dynamic Lighting
  --#leftsub|[*[&TokenId]:t-name]

  --#oddRowBackground|#ffffff
  --#evenRowBackground|#ffffff

  --=TTokenID|@{target|token_id}
  --=TargetHP|@{target|bar1}
  --=SpellAtkBonus|@{selected|wisdom_mod} + @{selected|pb}

  --=TargetAC|@{target|npc_ac}
	--?[$TargetAC.Total] -gt 0|DONEWITHAC
	--=TargetAC|@{target|ac}
  --:DONEWITHAC|

	--=Shot|[$Shot] + 1

  --=$Damage|0
  --=AttackRoll|[&AtkDice] + [$SpellAtkBonus]

  --+|[br]Spell Attack Roll [b]#[$Shot.Total][/b]: [$AttackRoll]

  --?[$AttackRoll.Base] -eq 20|Crit
  --?[$AttackRoll.Base] -eq 1|Fumble
  --?[$AttackRoll.Total] -ge [$TargetAC.Total]|Hit

	  --+|&#x274C;[b]Missed![/b]So close, but they ducked just in time. Better luck next time (if there is a next time).
	  --vtoken|@{selected|token_id} burn-magic
	  --^NEXT_RAY|

	  --:Fumble|
	  --+|&#x1F613;[b]Fumble![/b]The attack went horribly wrong.
	  --vtoken|@{selected|token_id} glow-magic
	  --^NEXT_RAY|

	  --:Hit|
	  --:/Is vulernable/Immune or Resistant?
		-->CHECK_DAMAGE_MODS|ResistType;fire
	  --=Damage|2d6 [&ResistType]
	  --=TtlDamage|[$TtlDamage] + [$Damage]

	  --+|&#x1F3AF;[b]Hit![/b] The blast did [$Damage] points of Fire damage.
	  --@token-mod|_ids [&TTokenId] _ignore-selected _set bar1_value|-[$Damage.Total] 
	  --vtoken|[&STokenId] burn-magic
	  --vtoken|[&TTokenId] burst-fire
	  --vbetweentokens|[&STokenId] [&TTokenId] beam-fire
	  --@roll20AM|_audio,play,nomenu|EldritchBlast
	  --^IMPACT_ANALYSIS|

	  --:Crit|
		-->CHECK_DAMAGE_MODS|ResistType;fire
	  --=Damage|4d6 [&ResistType] [CRIT]
	  --=TtlDamage|[$TtlDamage] + [$Damage]
	  --+|&#x2757;&#x2757;&#x2757;[b]Critical Hit!!![/b]The blast did [$Damage] points of Force damage.
	  --@token-mod|_ids [&TTokenId] _ignore-selected _set bar1_value|-[$Damage.Total] 
	  --vtoken|[&STokenId] burn-magic
	  --vtoken|[&TTokenId] burst-fire
	  --vbetweentokens|[&STokenId] [&TTokenId1] beam-fire
	  --@roll20AM|_audio,play,nomenu|EldritchBlast
	  --^IMPACT_ANALYSIS|

	--:IMPACT_ANALYSIS|

  --?[$Damage] -lt [$TargetHP]|NOTDEAD
	  --+|[hr]
	  --+|[c][b]*** &#x1F480; You Killed Em! &#x1F480; ***[/b][/c][hr]

	  --~|turnorder;removetoken;[&TTokenId]
	  --@token-mod|_ids [&TTokenId1] _ignore-selected _set statusmarkers|dead bar1_value|0

		--?[$RR.Total] -eq 1|ALLRAYSSNEEDED
			--=drr|[$RR] - 1
			--+|You have [b][$drr][/b] ray(s) remaining.
 			--^FINAL|			
		--:ALLRAYSSNEEDED|
			--+|It took all your designated rays to take Em down. 
 		--^FINAL|

		--:NOTDEAD|
		  --+|[hr]
		  --+|[i]You did your best, but they're still standing.  You did a total of [$TtlDamage] points of fire damage to the @{target|token_name}[/i]

  --:FINAL|

	--?[$TargetHP] -le 0|TARGET_KILLED
	
	--=RR|[$RR] - 1
	--?[$RR.Total] -eq 0|DONE
		-->
	 	

  --/|  See if it hit
  --/| Compute the damage
  --/| Charge the damage
  --/| Reduce the rays remaining and call myself again

  --:DONE|

--X|


--:CHECK_DAMAGE_MODS|damageVariableName;damageType
--&[%1%]|

	--/*|[*T:t-name] immune to: [*T:npc_immunities] vs. [%2%]
--?"[*T:npc_vulnerabilities]" -inc "[%2%]"|>CDM_ISVULNERABLE;[%1%]
--?"[*T:npc_resistances]" -inc "[%2%]"|>CDM_ISRESISTANT;[%1%]
--?"[*T:npc_immunities]" -inc "[%2%]"|>CDM_ISIMMUNE;[%1%]
--<|

--:CDM_ISVULNERABLE| --&[%1%]| * 2 [Vulnerable] --/*|[Vulnerable] to [%2%] --<|
--:CDM_ISRESISTANT| --&[%1%]| \ 2 [Resistant] --/*|[Resistant] to [%2%] --<|
--:CDM_ISIMMUNE| --&[%1%]| * 0 [Immune] --/*|[Immune] to [%2%] --<|

}}