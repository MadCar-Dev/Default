!script {{ 
  --#title|Scorching Ray
  --#sourceToken|@{selected|token_id}
  --#emoteText|@{selected|character_name} Casts Scorching Rays!
  --&TFColor|#FFFFFF
  --&STFColor|#000000
  --&FColor1|#000000
  --&FColor2|#000000
  --&EBColor|#7DEDFF
  --&BColor1|#7C83FD
  --&BColor2|#96BAFF
  --#titlecardbackground|[&BColor1]
  --#titlecardbackgroundimage|linear-gradient(to bottom, #96BAFF, #7C83FD)
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
  --#rightsub| 120' | V S M

  --#debug|1

  --:Setup|
     -->GetAndCheckSlotInformation|
     --&DamageType|fire

    --=MissileCount|[$SlotLevel] + 1
    -->BuildAndAskTargets|[$MissileCount.Total]
    --=DisplayCount|1
    --=MissileDamage|2d6
    --+|[c]Spell Save DC: [*S:spell_save_dc]   Damage: [#ffff00][$MissileDamage][/#][/c]

  --:MissileLoop| 
     -->FireMissile|[$DisplayCount.Total]
     --^NEXT_VICTEM|

  --X|

  --:FireMissile|
     --&ThisTarget|[&target[%1%]]

     -->IS_TARGET_DEAD|[&ThisTarget]
     --?[&TARGET_DEAD] -eq 1|[
       --+New Target|    
       --I You already killed [*[&ThisTarget]:t-name]!!! Click button below to select a new target.;Select new target|t;ThisTarget;Scorching Ray [%1%] Target
     --]|
     -->PlayEffects|@{selected|token_id};[&ThisTarget];none;burst-smoke;beam-magic
     -->Attack|[&ThisTarget]
  --<|

  --:EndMacro|

  --X|


  --/|PROCEDURES

  --:NEXT_VICTEM|
    --=DisplayCount|[$DisplayCount] + 1
    --?[$DisplayCount] -le [$MissileCount]|MissileLoop|EndMacro
  --<|

  --:IS_TARGET_DEAD|TokenID
    --&TARGET_DEAD|0
    --~Ndx|array;indexof;aryU_Targets;[%1%]
    --?[&Ndx] -eq ArrayError|ITD_ERR
      --~|array;setindex;aryU_Targets_HP;[&Ndx]
      --~hp|array;getcurrent;aryU_Targets_HP
      --?[&hp] -le 0|&TARGET_DEAD;1
  --<|
    --:ITD_ERR|
      --+ERR|ITD: Could not locate token
      --<|

  --:GetAndCheckSlotInformation|
     --=SlotLevel|?{Spell Slot Level?|2|3|4|5|6|7|8|9}
     --=SlotsTotal|[*S:lvl[$SlotLevel]_slots_total]
     --=SlotsRemaining|[*S:lvl[$SlotLevel]_slots_expended]
     --?[$SlotsRemaining.Total] -le 0|NoSlotsLeft

  --:DeductSpellSlot|
     --=SlotsRemaining|[$SlotsRemaining] - 1
     --@setattr|_charid [*S:character_id] _lvl[$SlotLevel]_slots_expended|[$SlotsRemaining] _silent
  --<|

  --:NoSlotsLeft|
     --+|[*S:character_name] has no level [$SlotLevel.Total] spell slots available.
  --X|

--:Attack|

    --/| Are they still alive? Maybe a previous ray killed them
    --/| This doesn't work because Token isn't updated with the new
    --/| HP Value quick enough (async issue) when it get read again here.  
    --/| To make this work will need to build my own array of unique-selected tokenids and associated HPs 
    --/| then deduct from this array to take value or ...ask for targets one at a time

    --/|=CurrentHP|[*[&ThisTarget]:t-bar1_value]
    --/|*[*[&ThisTarget]:t-name]|HP [$CurrentHP]
    --/|?[$CurrentHP] -le 0|[
      --/|Select New Target|Because this enemy is dead, please select a new Target #[$DisplayCount.Total]
      --/|iClick button below to select a new target.;Select 1 Target|t;[&ThisTarget];Select a new Target
    --/|]|

    --?"[*[&ThisTarget]:npc_ac]" -gt 0|=TargetAC;[*[&ThisTarget]:npc_ac]|=TargetAC;[*[&ThisTarget]:ac]
    --=TotalDamage|[$MissileDamage]
    --?"[*[&ThisTarget]:npc_immunities]" -inc "[&DamageType]"|Immune
    --?"[*[&ThisTarget]:npc_resistances]" -inc "[&DamageType]"|>DamageResist;[&ThisTarget];[$MissileDamage]
    --?"[*[&ThisTarget]:npc_vulnerabilities]" -inc "[&DamageType]"|>DamageVuln;[&ThisTarget];[$MissileDamage]

    --=AttackRoll|1d20 [BASE] + [*S:spell_attack_bonus]
    --+Attack|[*S:character_name] rolls [$AttackRoll] to attack and...

  --:Determine results|
    --?[$AttackRoll.Base] -eq 1|>Fumble;[&ThisTarget]
    --?[$AttackRoll.Total] -lt [$TargetAC.Total]|>Miss;[&ThisTarget]
    --?[$AttackRoll.Base] -eq 20]>DamageCrit;[&ThisTarget];[$TotalDamage]
    --?[$AttackRoll.Total] -ge [$TargetAC.Total] -and [$AttackRoll.Base] -ne 20|>Hit;[&ThisTarget];[$TotalDamage]
  --X|

  --:DamageResist|
    --=TotalDamage|[%2%] \ 2
    --+[b]Resistant![/b]|[b][*[%1%]:character_name] takes [$TotalDamage] damage![/b]

  --<|

  --:DamageVuln|
    --=TotalDamage|[%2%] * 2
    --+[b]Vulnerable![/b]|[b][*[%1%]:character_name] takes [$TotalDamage] damage![/b]
  --<|

  --:Fumble|
      --+&#x1F613;Fumbles!|The attack on [*[%1%]:t-name] went horribly wrong.
  --^MissileLoop|

  --:Miss|
      --+&#x274C;Misses!|The attack on [*[%1%]:t-name] missed.
  --^MissileLoop|

  --:Hit| pass Damage string 
    --+&#x1F3AF;Hits!|[*S:character_name]'s Spell hits [*[%1%]:t-name] for [$TotalDamage] [&DamageType] damage.
  -->ApplyDamage|[%1%];[$TotalDamage]
  --^MissileLoop|


  --:DamageCrit|
    --=TotalDamage|[%2%] * 2
    --+&#x2757;&#x2757;&#x2757;Crits!|[b][*[%1%]:t-name] takes [$TotalDamage] [&DamageType] damage![/b]
  -->ApplyDamage|[%1%];[$TotalDamage]
  --^MissileLoop|

  --:Immune|
     --=DisplayCount|[$DisplayCount] + 1
     --+|[b][*[%1%]:t-name] appears to be immune to [&DamageType] damage![/b]
  --^MissileLoop|

  --:ApplyDamage|
    --/|@alter|_target|[%1%] _bar|1 _amount|-[%2%]
    
    -->SET_CURRENT_U_HP|[%1%];[%2%]

    --/=CurrentHP|[*[%1%]:t-bar1_value]

    --@token-mod|_ids [%1%] _ignore-selected _set bar1_value|-[%2%] 

    --/|Did that kill them?
    --?[$NEW_HP.Total] -gt 0|ALIVE|DEAD

    --:ALIVE|
      --+|[*[%1%]:character_name] is still alive and kicking!
      --^NEXT_VICTEM|
    --:DEAD|
      --+Congrats|You managed to kill [*[%1%]:character_name]
      --@token-mod|_ids [%1%] _ignore-selected _set statusmarkers|dead bar1_value|0      
      --^NEXT_VICTEM|

  --:BuildAndAskTargets|MissileCount
     --&TargetString|
     --=targetCount|1
     --:TargetLoop|
        --&TargetString|+t;target[$targetCount.Raw];Scorching Ray [$targetCount.Raw] Target
        --/|*TargetString|[&TargetString]
        --=targetCount|[$targetCount.Total] + 1
        --?[$targetCount.Total] -le [%1%]|>AddSeparator
        --?[$targetCount.Total] -le [%1%]|TargetLoop
     --iClick button below to select targets. The same target may be selected more than once;Select [%1%] Targets|[&TargetString]

     -->BUILD_UNIQUE_TARGET_ARRAY|
  --<|

  --:AddSeparator|
     --&TargetString|+||
  --<|

  --:BUILD_UNIQUE_TARGET_ARRAY|
    --=cnt|1
    --~|array;define;aryU_Targets
    --~|array;define;aryU_Targets_HP

    --:BUTA_NEXT|
      
      --&t|[&target[$cnt.Raw]] 
      --~Ndx|array;indexof;aryU_Targets;[&t]
      --?[&Ndx] -ne ArrayError|BUTA_CONTINUE
        --~|array;add;aryU_Targets;[&t]
        --~|array;add;aryU_Targets_HP;[*[&t]:t-bar1_value]
      --:BUTA_CONTINUE|
      --=cnt|[$cnt] + 1

      --?[$cnt] -le [$MissileCount]|BUTA_NEXT

      --/|~s|array;stringify;aryU_Targets --/|*aryU_Targets|[&s]
      --/|~s|array;stringify;aryU_Targets_HP --/|*aryU_Targets_HP|[&s]

  --<|

  --:PlayEffects|Parameters are : sourcetoken; targettoken; source effect; target effect; line effect
     --vtoken|[%1%] [%3%]
     --vtoken|[%2%] [%4%]
     --vbetweentokens|[%1%] [%2%] [%5%]
  --<|

  --:SET_CURRENT_U_HP|token - hp to reduce
    --~Ndx|array;indexof;aryU_Targets;[%1%]
    --?[&Ndx] -eq ArrayError|SCUH_ERR
      --~|array;setindex;aryU_Targets_HP;[&Ndx]
      --~hp|array;getcurrent;aryU_Targets_HP
      --=CURRENT_HP|[&hp]
      --=NEW_HP|[$CURRENT_HP] - [%2%]
      --*|[%1%] [*[%1%]:t-name] [$CURRENT_HP] [$NEW_HP]       
      --~|array;setatindex;aryU_Targets_HP;[&Ndx];[$NEW_HP.Raw]
  --<|

  --:SCUH_ERR| Get here probably because user is selecting new targets
      --/|+ERR|SCUH: Could not locate token
  --<|

}}
