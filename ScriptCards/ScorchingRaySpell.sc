!script {{ 
  
  --/|Script Name : Scoring Ray
  --/|Version     : 1.0
  --/|Requires SC : 1.3.7+, TokenMod
  --/|Author      : Will M.
  --/|Updated     : 8/14/2021

  --/|Description : Scorching Ray Spell
  --/|              
  --/|              

  --#title|Scorching Ray
  --#sourceToken|@{selected|token_id}
  --#emoteText|@{selected|character_name} Casts Scorching Rays!
  --&TFColor|#FFFFFF
  --&STFColor|#000000
  --&FColor1|#000000
  --&FColor2|#000000
  --&EBColor|#FFFFFF
  --&BColor1|#FFFFFF
  --&BColor2|#FFFFFF

  --#titlecardbackground|[&BColor1]
  --#titlecardbackgroundimage|linear-gradient(to bottom, #FFF000, #FF0000)
  --#titleFontColor|[&TFColor]
  --#subtitleFontColor|[&STFColor]
  --#emoteBackground|[&EBColor]
  --#titlefontsize|2.0em
  --#titlefontlineheight|1.2em
  --#titlefontface|Times New Roman
  --#norollhighlight|0
  --#hideTitleCard|0
  --#bodyfontsize|12px

  --#oddrowbackground|[&BColor1]
  --#oddrowfontcolor|[&FColor2]
  --#evenrowbackground|[&BColor2]
  --#evenrowfontcolor|[&FColor1]
  --#leftsub|Level 2 | Evocation | Action
  --#rightsub| 120' | V S | Fire

  --#debug|1

  --:STEP1| Setup 

    --&DamageType|fire
    --=MissileDamage|2d6
    --=DisplayCount|0

    --+|You create three rays of fire and hurl them at targets within range. You can hurl them at one target or several.  Make a ranged spell attack for each ray. On a hit, the target takes 2d6 fire damage.
    --+|[b]At Higher Levels.[/b] When you cast this spell using a spell slot of 3rd level or higher, you create one additional ray for each slot level above 2nd.
    --+|[br]
    --+|[c][b]Spell Save DC: [#FF0000][*S:spell_save_dc][/#]   Damage: [#FF0000][$MissileDamage.Raw][/#][/b][/c]
    --+|[br]

    --+|[b][c]Spell Level?[/c][/b]
    --+|[c][rbutton]2nd::STEP2;2[/rbutton][rbutton]3rd::STEP2;3[/rbutton][rbutton]4th::STEP2;4[/rbutton][rbutton]5th::STEP2;5[/rbutton][/c]
    --+|[c][rbutton]6th::STEP2;6[/rbutton][rbutton]7th::STEP2;7[/rbutton][rbutton]8th::STEP2;8[/rbutton][rbutton]9th::STEP2;9[/rbutton][/c]
    --+|[br]
    --+|After picking a level above, you will be asked to select a series of targets until all your rays are delivered - [b]Good Luck![/b]

    --X|

  --:STEP2|
    --&SlotLevel|[&reentryval]
    --=SlotsTotal|[*S:lvl[&SlotLevel]_slots_total]
    --=SlotsRemaining|[*S:lvl[&SlotLevel]_slots_expended]
    --?[$SlotsRemaining.Total] -le 0|NoSlotsLeft
    --=MissileCount|[&SlotLevel] - 2 + 3


  --:MissileLoop| 
    --=DisplayCount|[$DisplayCount] + 1

    --?[$DisplayCount] -gt [$MissileCount]|EndMacro
    --?[$DisplayCount] -ne 1|[
      --+|[hr]
      --+|[br]
      --+|[c][rbutton]Next Target::NEXT_TARGET[/rbutton][/c]
      --+|[br]
      --+|[c][b]Spell Save DC: [#FF0000][*S:spell_save_dc][/#]   Damage: [#FF0000][$MissileDamage.Raw][/#][/b][/c]
      --+|[br]
      --X|
    --]|

    --:NEXT_TARGET|
    --I Select the target of your Scoring Ray.;Scoring Ray #[$DisplayCount].|t;ThisTarget;Scorching Ray #[$DisplayCount]:
    --#leftsub|Ray # [$DisplayCount.Raw]
    --#rightsub|[*[&ThisTarget]:t-name]
    --#targetToken|[&ThisTarget]
    --#emoteText|Scorching Ray #[$DisplayCount.Raw]
    -->PlayEffects|@{selected|token_id};[&ThisTarget];none;burst-smoke;beam-magic

    --&DamageResistNote|
    --=TotalDamage|[$MissileDamage]

    --?"[*[&ThisTarget]:npc_ac]" -gt 0|[
      --=TargetAC|[*[&ThisTarget]:npc_ac]
      --?"[*[&ThisTarget]:npc_immunities]" -inc "[&DamageType]"|>Immune
      --?"[*[&ThisTarget]:npc_resistances]" -inc "[&DamageType]"|>DamageResist
      --?"[*[&ThisTarget]:npc_vulnerabilities]" -inc "[&DamageType]"|>DamageVuln
    --]|[
      --=TargetAC|[*[&ThisTarget]:ac]
    --]|
    --=AttackRoll|1d20 [BASE] + [*S:spell_attack_bonus]

    --/|Determine results|
    --?[$AttackRoll.Base] -eq 1|FUMBLE
    --?[$AttackRoll.Base] -eq 20|CRITICAL
    --?[$AttackRoll.Total] -lt [$TargetAC.Total]|MISS
    --?[$AttackRoll.Total] -ge [$TargetAC.Total]|HIT

    --X|
  --<|

  --:EndMacro|
  --X|

  --/|PROCEDURES

  --:DeductSpellSlot|
     --=SlotsRemaining|[$SlotsRemaining] - 1
     --@setattr|_charid [*S:character_id] _lvl[&SlotLevel]_slots_expended|[$SlotsRemaining] _silent
  --<|

  --:NoSlotsLeft|
     --+|[*S:character_name] has no level [&SlotLevel] spell slots available.
  --X|

  --:DamageResist|
    --=TotalDamage|[$MissileDamage] \ 2
    --&DamageResistNote|(Resistant)
  --<|
  --:DamageVuln|
    --=TotalDamage|[$MissileDamage] * 2
    --&DamageResistNote|(Vulnerable)
  --<|
  --:Immune|
    --=TotalDamage|0    
    --&DamageResistNote|(Immune)
  --<|


  --:FUMBLE|
      --+&#x1F613;Ray #[$DisplayCount.Raw] Fumbles! |[*S:character_name] rolls [$AttackRoll] to attack [*[&ThisTarget]:t-name] and something went horribly wrong.
  --^MissileLoop|

  --:MISS|
      --+&#x274C;Ray #[$DisplayCount.Raw] Misses! |[*S:character_name] rolls [$AttackRoll] to attack [*[&ThisTarget]:t-name] and missed.
  --^MissileLoop|

  --:HIT| pass Damage string 
    --+&#x1F3AF;Ray #[$DisplayCount.Raw] hits! |[*S:character_name] rolls [$AttackRoll] to attack and hits [*[&ThisTarget]:t-name] for [$TotalDamage] [&DamageType] damage. [&DamageResistNote]
  -->ApplyDamage|
  --^MissileLoop|

  --:CRITICAL|
    --=TotalDamage|[$TotalDamage] * 2
    --+&#x2757;&#x2757;&#x2757;Crits!|[*S:character_name] rolls [$AttackRoll] to attack and CRITS [*[&ThisTarget]:t-name] for [$TotalDamage] [&DamageType] damage. [&DamageResistNote]
  -->ApplyDamage|
  --^MissileLoop|

  --:ApplyDamage|
    
    --=CurrentHP|[*[&ThisTarget]:t-bar1_value]
    --@token-mod|_ids [&ThisTarget] _ignore-selected _set bar1_value|-[$TotalDamage.Raw] 
    --/|@alter|_target|[&ThisTarget] _bar|1 _amount|-[$TotalDamage.Raw]

    --/|Did that kill them?
    --?[$CurrentHP.Raw] -gt [$TotalDamage.Raw]|ALIVE|DEAD

    --:ALIVE|
      --/+|[*[&ThisTarget]:character_name] is still alive and kicking!
      --<|
    --:DEAD|
      --+Congrats|You managed to kill [*[&ThisTarget]:character_name]
      --@token-mod|_ids [&ThisTarget] _ignore-selected _set statusmarkers|dead bar1_value|0      
      --<|

  --:PlayEffects|Parameters are : sourcetoken; targettoken; source effect; target effect; line effect
     --vtoken|[%1%] [%3%]
     --vtoken|[%2%] [%4%]
     --vbetweentokens|[%1%] [%2%] [%5%]
  --<|
}}
