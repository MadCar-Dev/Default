!script {{

  --/|Script Name : Spawn Fey Spirit
  --/|Version     : 2.0
  --/|Requires SC : 1.3.7+, TokenMod, Chatsetattr, SelectManager, SpawnDefaultToken*
  --/|Author      : Will M.

  --/|Description : Spawn Fey Spirt token and assign abilities
  --/|              based on level and type

  --/|Setup: 
  --/| 1 - Duplicate the Fey Spirit sheet and give it it's own name
  --/| 2 - I create a rollable token table with 4 different tokens 
  --/|     1-Fuming, 2-Mirthful, 3-Tricksy, 4-Smoke (used as initial token)
  --/| 3 - Assign this rollable table to the fey spirit character sheet, just as you would
  --/|     for a multi-sided character/npc token
  --/| Possible issue:  I modified a version of the SpawnDefaultToken to let me pass it a new name
  --/| 									I will need to adjust this code to utilize the one-click version that was recently
  --/| 									updated with the same functionality.
  
	--&ASName|Fey Spirit(Eliza)

  --:TOP|
  --#reentrant|SpawnSpirtFey
  --#title|Spawn Spirit Fey
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
  --#debug|1
  --#activepage|[*S:t_-pageid]
  --#leftsub|
  --#rightsub|

--/|======== Query User for Needed info ==================

--/| spawn a generic spirit fey next to the selected user
	-->SPAWN_FEY_SPIRIT|

--/| Ask user for spell_level
	-->ASK_FOR_LEVEL|

		--/| Ask user for mood (Fuming, Mirthful, Tricksy)
		--/| Set spirit fey's AC (12 + Spell_Level [natural armor])
		--/| Set spirit fey's HP (30 + (Spell_Level-3) * 10)
		--/| Set Spirit fey's short-sword attack to add spell_level to piercing damage
		--/| Set Multi Attack equal to Spell_level/2 rounded down 
		--/| Set to appropriate Token
		--/| Show Spirit GM Card /w stats 

--/|======== Subroutines ==================
--:SPAWN_FEY_SPIRIT|
	--@forselected+|Spawn _name|[&ASName] _offset|1,0 _expand|40,20 _size|1,1
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
	--+|[c]  [rbutton]Level 3::ASK_FOR_MOOD;3[/rbutton][/c]
	--+|[c]  [rbutton]Level 4::ASK_FOR_MOOD;4[/rbutton][/c]
	--+|[c]  [rbutton]Level 5::ASK_FOR_MOOD;5[/rbutton][/c]

--X|

--:ASK_FOR_MOOD|
	--#Title|Fey Spirit Mood?
	--&Spell_Level|[&reentryval]
	--/+debug|AFM: [&Spell_Level]	
	--+|[c]  [rbutton]Fuming::CONFIGURE_FEY_SPIRIT;Fuming\[&Spell_Level][/rbutton][/c]
	--+|[c]  [rbutton]Mirthful::CONFIGURE_FEY_SPIRIT;Mirthful\[&Spell_Level][/rbutton][/c]
	--+|[c]  [rbutton]Tricksy::CONFIGURE_FEY_SPIRIT;Tricksy\[&Spell_Level][/rbutton][/c]
--X|

--:CONFIGURE_FEY_SPIRIT|
	--#Title|Fey Spirit Mood?
	--~Arg|string;split;\;[&reentryval]
	--&Mood|[&Arg1]
	--=Spell_Level|[&Arg2]
	--/+debug|CFS: [&Mood] / [$Spell_Level]

	--/| Get the TokenId of the previously spawned fey, returned in FTOKEN_ID
	-->FIND_TOKEN_BY_NAME|[&ASName]

	--/+debug|FTokenId [&FTOKEN_ID]

	--?[&FTOKEN_ID] -eq #NA|TOKEN_NOT_FOUND
	--&FChar_Id|[*[&FTOKEN_ID]:t-represents]
	--/+debug|FCharId [&FChar_Id]

	--/| Set spirit fey's AC (12 + Spell_Level [natural armor])
	--=AC|12 + [$Spell_Level]

	--/| Set spirit fey's HP (30 + (Spell_Level-3) * 10)
	--=HP1|[$Spell_Level] - 3 --=HP|[$HP1] * 10 + 30

	--C[&Mood]|Fuming:MOOD_FUMING|Mirthful:MOOD_MIRTHFUL|Tricksy:MOOD_TRICKSY

		--:MOOD_FUMING|
			--&MoodSide|1
			--&MoodDesc|The fey has advantage on the next attack roll it makes before the end of this turn.
			--^MODIFY_SHEET|
		--:MOOD_MIRTHFUL|
			--&MoodSide|2
			--&MoodDesc|The fey can force one creature it can see within 10 feet of it to make a [b]Wisdom saving throw[/b] against your spell save DC[[b](@{selected|spell_save_dc})[/b]. Unless the save succeeds, the target is [b]charmed[/b] by you and the fey for 1 minute or until the target takes any damage.
			--^MODIFY_SHEET|
		--:MOOD_TRICKSY|
			--&MoodSide|3
			--&MoodDesc|The fey can fill a 5-foot cube within 5 feet of it with magical darkness, which lasts until the end of its next turn.
	    --@forselected+|Spawn _name|Spawn-Generic-AoE-Template _offset|2,0 _side|26 _expand|40,20 _size|1,1 _order|ToBack

	--:MODIFY_SHEET|

	--@token-mod|_set currentside|[&MoodSide] bar1_value|[*[&FChar_Id]:hp^] bar1_max|[*[&FChar_Id]:hp^] bar1_link| bar2_value|[*[&FChar_Id]:npc_ac] bar2_link|npc_ac _off showname showplayers_name showplayers_bar3 _ids [&FTOKEN_ID]

	--@setattr|_charid [&FChar_Id] _spell_level|[$Spell_Level.Total] _hp|[$HP.Total]|[$HP.Total] _npc_hpformula|30 + 10 for each spell level above 3rd _npc_ac|[$AC.Total] _npc_actype|(Natural Armor) _silent


	--/| Set Spirit fey's short-sword attack to add spell_level to piercing damage
	--/| attack_damage = 1d6 + 3 + Spell_Level
	--/| Locate the shortsword repeating repeating_attack
	--Rfind|[&FChar_Id];Shortsword;repeating_npcaction;name
	--/Rdump|
  --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|SS_ATK_NOT_FOUND
	--/&SSRA|[*R>atkname] 
	--/+debug|CFS: [&SSRA] atkdmgtype: [*R>attack_damage]
	--@setattr|_charid [&FChar_Id] _[*R>attack_tohit]|@{selected|spell_attack_bonus} _[*R>attack_damage]|1d6+3+[$Spell_Level.Total][Spell Level]  _silent

	--:SS_ATK_NOT_FOUND|

	--/| Set Multi Attack equal to Spell_level/2 rounded down 
	--/| Locate the Multiattack repeating repeating_attack
	--Rfind|[&FChar_Id];Multiattack;repeating_npcaction;name
	--Rdump|
  --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|MA_ATK_NOT_FOUND
	--&SSRA|[*R>atkname] 
	--\+debug|CFS: [&SSRA] description: [*R:description]

	--=Atks|[$Spell_Level] \ 2
	--&MA_Desc|The fey makes a number of attacks equal to half this spell’s level([$Spell_Level.Raw]) rounded down ([$Atks.Raw] Attack(s)).
	--\+debug|Desc: [&MA_Desc]
	--@setattr|_charid [&FChar_Id] _[*R>description]|[&MA_Desc]  _silent
	--:MA_ATK_NOT_FOUND|


--#Title|Fey Spirit Spawned
--#leftsub|Spell Level [&Spell_Level]
--#whisper|
--#rightsub|[&Mood]
  --#oddrowbackground|#dfc98a
  --#evenrowbackground|#e3b778--+|[c][img width=300]https://s3.amazonaws.com/files.d20.io/images/225023610/IS0FRi9rzTebGjP08SkNDw/original.jpg?16221564845[/img][/c]
--+|[br]
--+|[c][b][i]@{Selected|character_name}[/i] spawned a Fey Spirit[/b][/c]
--+[&Mood].|[&MoodDesc] 
--+Multiattack|[$Atks] attacks
--+Attack Bonus|[b]+@{selected|spell_attack_bonus}[/b]
--+Spell Lvl|[$Spell_Level] 
--+AC|[$AC] (12 + Spell-level)
--+HP|[$HP] (30 + (Spell-level - 3) x 10)
--+Shortsword Damage|1d6 + 3 + [$Spell_Level] [piercing] and 1d6 [force]

--X|
--:TOKEN_NOT_FOUND|
	--+|Spirit Fey Token not found, unable to configure 
--X|

}}