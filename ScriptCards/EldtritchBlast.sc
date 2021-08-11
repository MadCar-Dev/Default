!script {{  
  --#title|Eldritch Blast 
  --#emoteText|@{selected|token_name} attacks @{target|token_name}
  --=SpellAtkBonus|@{selected|charisma_mod} + @{selected|pb}
  --#rightsub|Atk Bonus: +[$SpellAtkBonus.Total]
  --#titleCardBackground|#932729
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF  
  --#debug|0

  --#sourceToken|@{selected|token_id}
  --#targetToken|@{target|token_id}

  --&ShowFX|0 --/| Set to 0 to disable effects (visual and audio)

  --&STokenId|@{selected|token_id}
  --&TTokenId1|@{target|token_id}

  --=TargetHP|@{target|bar1}
	--=TtlDamage|0

  --=TargetAC|@{target|npc_ac}
	--?[$TargetAC.Total] -gt 0|DONEWITHAC
	--=TargetAC|@{target|ac}
  --:DONEWITHAC|

  --&AtkDice|?{Roll Type?|Normal,1d20|Advantage,2d20kh1|Disadvantage,2d20kl1}
  --&ShotCnt|?{How many shots at current target (Will auto stop when target falls)?|4|3|2|1}
  --#leftsub|Target [&ShotCnt] blasts 

	--=Shot|0
	--:TOPLOOP|
	--?[$Shot.Total] -eq [&ShotCnt]|EXITLOOP
 		--?[$TargetHP] -le [$TtlDamage]|EXITLOOP

		 --=Shot|[$Shot] + 1
		 --\| Break/Early Out Syntax (--%!|)
		 --\| Can be used w/ conditionals (--?[&Value] -eq 1|%) to return to top and continue
		 --\| 														 (--?[&Value] -eq 1|%!) to break and exit loop
		  --=$Damage|0
		  --=AttackRoll|[&AtkDice] + @{selected|charisma_mod} [Cha] + @{selected|pb} [PROF]


		  --+|[br]Spell Attack Roll [b]#[$Shot.Total][/b]: [$AttackRoll]

		  --?[$AttackRoll.Base] -eq 20|Crit
		  --?[$AttackRoll.Base] -eq 1|Fumble
		  --?[$AttackRoll.Total] -ge [$TargetAC.Total]|Hit

		  --+|&#x274C;[b]Missed![/b]So close, but they ducked just in time. Better luck next time (if there is a next time).
		  
		  --?[&ShowFX] -eq 1|[
		  	--vtoken|@{selected|token_id} burn-magic
		  --]|

		  --^TOPLOOP|

		  --:Fumble|
		  --+|&#x1F613;[b]Fumble![/b]The attack went horribly wrong.
		  --?[&ShowFX] -eq 1|[
		  	--vtoken|@{selected|token_id} glow-magic
		  --]

		  --^TOPLOOP|

		  --:Hit|
		  --=Damage|1d10 + @{selected|charisma_mod} [Cha]
		  --=TtlDamage|[$TtlDamage] + [$Damage]
		  --+|&#x1F3AF;[b]Hit![/b] The blast did [$Damage] points of Force damage.
		  --@token-mod|_ids [&TTokenId1] _ignore-selected _set bar1_value|-[$Damage.Total] 

		  --?[&ShowFX] -eq 1|[
			  --vtoken|@{selected|token_id} burn-magic
			  --vtoken|[&TTokenId1] burst-fire
			  --vbetweentokens|@{selected|token_id} [&TTokenId1] beam-fire
			  --@roll20AM|_audio,play,nomenu|EldritchBlast
			--]|

		  --^TOPLOOP|

		  --:Crit|
		  --=Damage|1d10 + @{selected|charisma_mod} [Cha] + 1d10 [CRIT]
		  --=TtlDamage|[$TtlDamage] + [$Damage]
		  --+|&#x2757;&#x2757;&#x2757;[b]Critical Hit!!![/b]The blast did [$Damage] points of Force damage.
		  --@token-mod|_ids [&TTokenId1] _ignore-selected _set bar1_value|-[$Damage.Total] 
		  --?[&ShowFX] -eq 1|[
			  --vtoken|@{selected|token_id} burn-magic
			  --vtoken|[&TTokenId1] burst-fire
			  --vbetweentokens|@{selected|token_id} [&TTokenId1] beam-fire
			  --@roll20AM|_audio,play,nomenu|EldritchBlast
			--]|
		  --^TOPLOOP|

	--:EXITLOOP|
	
	--=RemainingHP|[$TargetHP] - [$TtlDamage]
  --*|OrigHP:[$TargetHP] Dmg:[$TtlDamage] Rem:[$RemainingHP]  AtkDice:[&AtkDice]
  --?[$RemainingHP] -gt 0|NOTDEAD
	  --+|[hr]
	  --+|[c][b]*** &#x1F480; You Killed Em! &#x1F480; ***[/b][/c][hr]

	  --~|turnorder;removetoken;[&TTokenId1]
	  --@token-mod|_ids [&TTokenId1] _ignore-selected _set statusmarkers|dead bar1_value|0

		--=RemaingShots|[&ShotCnt] - [$Shot]
		--?[$RemaingShots] -eq 0|ALLBLASTSNEEDED
			--+|Only [b][$Shot.Total][/b] blasts needed to take Em down. [b][$RemaingShots.Total][/b] blasts remain.
 			--^FINAL|			
		--:ALLBLASTSNEEDED|
			--+|It took all your designated blasts ([&ShotCnt]) to take Em down. [b][$RemaingShots.Total][/b] blasts remain.
 			--^FINAL|

	--:NOTDEAD|
	  --+|[hr]
	  --+|[i]You did your best, but they're still standing.  You did a total of [$TtlDamage] points of damage to the @{target|token_name}[/i]

  --:FINAL|
}}