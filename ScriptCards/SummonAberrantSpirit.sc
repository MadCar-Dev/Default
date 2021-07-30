!script {{

  --/|Script Name : Summon Aberrant Spirit
  --/|Version     : 2.0
  --/|Requires SC : 1.3.7+, TokenMod, Chatsetattr, SelectManager, SpawnDefaultToken*
  --/|Author      : Will M.

  --/|Description : Summon Aberrant Spirt token and assign abilities
  --/|              based on level and type

  --/|Setup: 
  --/| 1 - I use a duplicate of the aberrant spirit sheet and give it it's own name
  --/| 2 - I create a rollable token table with 4 different tokens 
  --/|     1-Beholderkin, 2-Slaad, 3-StarSpawn, 4-Smoke (used as initial token)
  --/| 3 - Assign this rollable table to the character sheet, just as you would
  --/|     for a multi-sided character/npc token

  --/| Set to the name you give your Aberrant Spirt character sheet 
	--&ASName|Aberrant Spirit(Xaral)

  --:TOP|
  --#reentrant|SpawnAberrantSpirit
  --#title|Spawn Aberrant Spirit
  --#titleCardBackground|#03038a
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|#0905f2
  --#buttonbordercolor|#FFFFFF
  --#bodyFontSize|12px
  --#sourceToken|@{selected|token_id}
  --#hidecard|0    
  --#whisper|self
  --#debug|0
  --#activepage|[*S:t_-pageid]
  --#leftsub|
  --#rightsub|

--/|======== Query User for Needed info ==================
--/| Ask user for spell_level

--/| spawn a generic aberrant spirit next to the selected user
	-->SPAWN_ABERRANT_SPIRIT|

	-->ASK_FOR_LEVEL|

		--/| Ask user for Type (Beholderkin, Slaad, Star Spawn)
		--/| Set spirit spirit's AC (11 + Spell_Level [natural armor])
		--/| Set spirit spirit's HP (40 + (Spell_Level-4) * 10)
		--/| Set Spirit spirit's attack to Claws(Slaad), Eye Ray(Beholderkin), Psychic Slam(Star Spawn Only)
		--/| Set Multi Attack equal to Spell_level/2 rounded down 
		--/| Set to appropriate Token
		--/| Show Spirit GM Card /w stats 

--/|======== Subroutines ==================
--:SPAWN_ABERRANT_SPIRIT|
	--@forselected+|Spawn _name|[&ASName] _offset|1,0 _expand|40,20 _size|1,1 _side|4
--<|

--:FIND_TOKEN_BY_NAME|Token Name
--/| Parameters: 
--/| Returns/Sets: [&FTOKEN_ID], returns #NA if not available
	--&TN|[%1%]
	--&FTOKEN_ID|#NA

	--/+debug|FTBN: [&TN]

	--~tc|array;pagetokens;PTAry;@{selected|token_id}
	--~tempid|array;getfirst;PTAry

	--:FT_LOOPSTART|
		--//+debug|FTBN: [&tempid] [*[&tempid]:t-name]
	--?[&tempid] -eq ArrayError|FT_ENDLOOP

		--?"[*[&tempid]:t-name]" -eq "[&TN]"|FT_FOUND

		--~tempid|array;getnext;PTAry
		--?[&tempid] -ne ArrayError|FT_LOOPSTART|FT_EXIT

	--:FT_FOUND|
	--/+debug|FTBN: Found 

	--&FTOKEN_ID|[&tempid]
	--:FT_EXIT|
	--<|

--/|======== Re-Entry Functions ==================
--:ASK_FOR_LEVEL|
	--/+debug|AFL: Location 1
	--+|[c] [rbutton]Level 4::ASK_FOR_TYPE;4[/rbutton][/c]
	--+|[c] [rbutton]Level 5::ASK_FOR_TYPE;5[/rbutton][/c]
	--+|[c] [rbutton]Level 6::ASK_FOR_TYPE;6[/rbutton][/c]

--X|

--:ASK_FOR_TYPE|
	--#Title|Aberrant Spirit Type?
	--&Spell_Level|[&reentryval]
	--/+debug|AFM: [&Spell_Level]	



	--+|[c]  [rbutton]Beholderkin(Genny)::CONFIGURE_SPIRIT;BEHOLDERKIN\[&Spell_Level][/rbutton][/c]
	--+|[c]  [rbutton]Slaad(Sammy)::CONFIGURE_SPIRIT;SLAAD\[&Spell_Level][/rbutton][/c]
	--+|[c]  [rbutton]Star Spawn(Twinkles)::CONFIGURE_SPIRIT;STARSPAWN\[&Spell_Level][/rbutton][/c]
--X|

--:CONFIGURE_SPIRIT|
	--#Title|Setting Up Aberrant Spirit
	--~Arg|string;split;\;[&reentryval]
	--&Type|[&Arg1]
	--=Spell_Level|[&Arg2]
	--/+debug|CFS: [&Mood] / [$Spell_Level]

	--&SpellAttackBonus|@{selected|spell_attack_bonus}

	--/| Get the TokenId of the previously spawned aberration, returned in FTOKEN_ID
	-->FIND_TOKEN_BY_NAME|[&ASName]

	--/+debug|FTokenId [&FTOKEN_ID]

	--?[&FTOKEN_ID] -eq #NA|TOKEN_NOT_FOUND
	--&FChar_Id|[*[&FTOKEN_ID]:t-represents]
	--/+debug|FCharId [&FChar_Id]

	--C[&Type]|BEHOLDERKIN:BEHOLDERKIN|SLAAD:SLAAD|STARSPAWN:STARSPAWN

		--:BEHOLDERKIN|
			--&Side|1
			--&Desc|[b]Eye Ray:[/b]The aberration has an [b]Eye Ray[/b] ranged spell attack with a range of 150ft using your [b][i]Spell Attack Modifier(+[&SpellAttackBonus])[/i][/b] to hit.  On a hit, creature takes [b][i]1d8 + 3 + [$Spell_Level](spell level)[/i][/b] psychic damage. 
			--&ImgLink|https://s3.amazonaws.com/files.d20.io/images/234248274/6MpoqJ8rMa_NWZGTLldijg/original.png?16263644545
			--&Name|Ginny
			--^MODIFY_SHEET|

		--:SLAAD|
			--&Side|2
			--&Desc|[b]Claws:[/b]The aberration has an [b]Claws[/b] melee weapon attack (5ft.) using your [b][i]Spell Attack Modifier(+[&SpellAttackBonus])[/i][/b] to hit.  On a hit, creature takes [b][i]1d10 + 3 + [$Spell_Level](spell level)[/i][/b] slashing damage.[br]
							[b]Regeneration:[/b] The aberration regains 5 hit points at the start of its turn if it has at least 1 hit point.
			--&ImgLink|https://s3.amazonaws.com/files.d20.io/images/234248273/99eK4lX9VfMfkVnfk_6SZw/original.png?16263644545
			--&Name|Sammy
			--^MODIFY_SHEET|

		--:STARSPAWN|
			--&Side|3
			--&Desc|[b]Psychic Slam:[/b]The aberration has an [b]Psychic Slam[/b] melee spell attack (5ft.) using your [b][i]Spell Attack Modifier(+[&SpellAttackBonus])[/i][/b] to hit.  On a hit, creature takes [b][i]1d8 + 3 + spell level[/i][/b] psychic damage.[br]
							[b]Whispering Aura:[/b] At the start of each of the aberration’s turns, each creature within 5 feet of the aberration must succeed on a Wisdom saving throw against your [b][i]Spell Save DC(@{selected|spell_save_dc})[/b][/i] or take [b][i]2d6[/b][/i] psychic damage, provided that the aberration isn’t incapacitated.
			--&ImgLink|https://s3.amazonaws.com/files.d20.io/images/234248272/nZ_PNTOkRUJ3kCvwLjxGPQ/original.png?16263644545
			--&Name|Sparky
			--^MODIFY_SHEET|

	--:MODIFY_SHEET|

	--/| Set spirit's AC (11 + Spell_Level [natural armor])
	--=AC|11 + [$Spell_Level]

	--/| Set spirit's HP (40 + (Spell_Level-4) * 10)
	--=HP1|[$Spell_Level] - 4 --=HP|[$HP1] * 10 + 40

	--/| Establish the common features (HP and AC)
	--@setattr|_charid [&FChar_Id] _silent _spell_level|[$Spell_Level.Total] _hp|[$HP.Total]|[$HP.Total] _npc_hpformula|30 + 10 for each spell level above 3rd _npc_ac|[$AC.Total] _npc_actype|(Natural Armor) 

	--/| Set Multi Attack equal to Spell_level/2 rounded down 
	--/| Locate the Multiattack repeating repeating_attack
	--Rfind|[&FChar_Id];Multiattack;repeating_npcaction;name
	--/|Rdump|
  --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|MA_ATK_NOT_FOUND
		--&SSRA|[*R>atkname] 
		--\+debug|CFS: [&SSRA] description: [*R:description]
		--=Atks|[$Spell_Level] \ 2
		--&MA_Desc|The aberration makes a number of attacks equal to half this spell’s level([$Spell_Level.Raw]) rounded down ([$Atks.Raw] Attacks).
		--\+debug|Desc: [&MA_Desc]
		--@setattr|_charid [&FChar_Id] _silent _[*R>description]|[&MA_Desc] 

	--:MA_ATK_NOT_FOUND| Skip 

	--/| Set Abberation's eye-ray attack to add spell_level 
	--/| attack_damage = 1d8 + 3 + Spell_Level
	--/| Locate the Eye Ray repeating_attack
	--Rfind|[&FChar_Id];Eye Ray (Beholderkin Only);repeating_npcaction;name
	--/Rdump|
  --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|ER_ATK_NOT_FOUND
	--/&SSRA|[*R>atkname] 
	--/+debug|CFS: [&SSRA] atkdmgtype: [*R>attack_damage]
	--@setattr|_charid [&FChar_Id] _silent _[*R>attack_tohit]|@{selected|spell_attack_bonus} _[*R>attack_damage]|1d8+3+[$Spell_Level.Total][Spell Level]
	--:ER_ATK_NOT_FOUND|

	--/| Set Abberation's Claws attack to add spell_level 
	--/| attack_damage = 1d10 + 3 + Spell_Level
	--/| Locate the Claws repeating_attack
	--Rfind|[&FChar_Id];Claws (Slaad Only);repeating_npcaction;name
	--/Rdump|
  --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|CLAWS_ATK_NOT_FOUND
	--/&SSRA|[*R>atkname] 
	--/+debug|CFS: [&SSRA] atkdmgtype: [*R>attack_damage]
	--@setattr|_charid [&FChar_Id] _silent _[*R>attack_tohit]|@{selected|spell_attack_bonus} _[*R>attack_damage]|1d10+3+[$Spell_Level.Total][Spell Level]
	--:CLAWS_ATK_NOT_FOUND|

	--/| Set Abberation's Psychic Slam attack to add spell_level 
	--/| attack_damage = 1d8 + 3 + Spell_Level
	--/| Locate the Claws repeating_attack
	--Rfind|[&FChar_Id];Psychic Slam (Star Spawn Only);repeating_npcaction;name
	--/Rdump|
  --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|CLAWS_ATK_NOT_FOUND
	--/&SSRA|[*R>atkname] 
	--/+debug|CFS: [&SSRA] atkdmgtype: [*R>attack_damage]
	--@setattr|_charid [&FChar_Id] _silent _[*R>attack_tohit]|@{selected|spell_attack_bonus} _[*R>attack_damage]|1d8+3+[$Spell_Level.Total][Spell Level]
	--:CLAWS_ATK_NOT_FOUND|

	--@token-mod|_set currentside|[&Side] name|[&Name] _ids [&FTOKEN_ID] _ignore-selected


--#Title|Aberrant Spirit Summoned
--#leftsub|Spell Level [&Spell_Level]
--#whisper|
--#rightsub|[&Type]
--#oddrowbackground|#dfc98a
--#evenrowbackground|#e3b778 

--+|[c][b][i]@{Selected|character_name}[/i] summoned an Aberant Spirit[/b][/c]
--+|[c][img width=300][&ImgLink][/img][/c]

--+|[c][b][&Type][/b][/c]
--+|[&Desc] 
--+Multiattack:|[$Atks] attacks
--+Attack Bonus:|[b]+@{selected|spell_attack_bonus}[/b]
--+Spell Save DC:|[b]+@{selected|spell_save_dc}[/b]
--+Spell Lvl:|[$Spell_Level]
--+AC:|[$AC] (11 + Spell-level)
--+HP:|[$HP] (40 + (Spell-level - 4) x 10)

--&LogMsg|[*S:t-name] summons a level [&Spell_Level] [&Type] aberration.
-->LOG|[&LogMsg]

--X|

--:TOKEN_NOT_FOUND|
	--+|Aberration Token not found, unable to configure 
--X|

--:LOG|Text to log
  --~DT|system;date;getdatetime
  --@note-log|[&DT]: [%1%]
--<|

}}