!script {{  
  --/|Script Name : Shove
  --/|Version     : 1.0
  --/|Requires SC : 1.3.9+, TokenMod
  --/|Author      : Will M.

  --/|Description : Action card that lets player or foe attempt a 5e Shove Attack action
  --/|              

  --#title|Shove
  --#reentrant|Shove
  --#emoteText|attempts to shove
  --#bodyFontSize|11px 

--+|[b][c]Shoving a Creature[/c][/b]
--+|Using the Attack action, you can make a special melee attack to shove a creature, either to knock it prone or push it away from you. If you're able to make multiple attacks with the Attack action, this attack replaces one of them.
--+|The target must be no more than one size larger than you and must be within your reach. Instead of making an attack roll, you make a Strength (Athletics) check contested by the target's Strength (Athletics) or Dexterity (Acrobatics) check (the target chooses the ability to use). You succeed automatically if the target is incapacitated. 
--+|If you succeed, you either knock the target prone or push it 5 feet away from you.

  --#sourceToken|@{selected|token_id}
  --#targetToken|@{target|token_id}
  --#debug|1

  --&STid|@{selected|token_id}
  --&TTid|@{target|token_id}

  --#leftsub|[*[&STid]:t-name]
  --#rightsub|[*[&TTid]:t-name]

  -->GET_SKILL_BONUS|[&STid];athletics
  --&SBonus|[&Bonus]

  -->GET_SKILL_BONUS|[&TTid];athletics
  --&TAthBonus|[&Bonus]

  -->GET_SKILL_BONUS|[&TTid];acrobatics
  --&TAcrBonus|[&Bonus]

  --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;"
  --&trStyle1|style="border-top:0px dashed black;"
  --&tdStyle1|style="width:100%;text-align:center;background-color:#FFFFFF;font-size:80%"


--:STEP1| Setup the Shove Attack, ask if attacker is using advantage

  --+|Select the Roll Type for the attacker([*[&STid]:t-name]) using [b]athletics[/b]:
  --+|[t [&tStyle]]
      [tr [&trStyle1]][td [&tdStyle1]][rbutton]Normal::STEP2;N[/rbutton][/td][/tr]
      [tr [&trStyle1]][td [&tdStyle1]][rbutton]Advantage::STEP2;A[/rbutton][/td][/tr]
      [tr [&trStyle1]][td [&tdStyle1]][rbutton]Disadvantage::STEP2;D[/rbutton][/td][/tr]
      [/t]
  --X|

--:STEP2| Process result from step 1 and Get defender info
  --#emotetext|
  --#emoteState|1
  --#hideTitleCard|1

  --&SRT|1d20
  --?[&reentryval] -eq A|&SRT;2d20kh1
  --?[&reentryval] -eq D|&SRT;2d20kl1

  --+|Defender ([*[&TTid]:t-name]) preferred skill for countering shove?
  --+|[t [&tStyle]]
      [tr [&trStyle1]][td [&tdStyle1]][rbutton]Athletics/Str([&TAthBonus])::STEP3;athletics[/rbutton][/td][/tr]
      [tr [&trStyle1]][td [&tdStyle1]][rbutton]Acrobatics/Dex([&TAcrBonus])::STEP3;acrobatics[/rbutton][/td][/tr]
      [/t]
  --X|

--:STEP3| Process result from step 2 and Get defender roll type
  --&TSkill|athletics
  --&TBonus|[&TAthBonus]
  --?[&reentryval] -eq athletics|STEP3_ASK
    --&TSkill|acrobatics
    --&TBonus|[&TAcrBonus]

  --:STEP3_ASK|  

  --+|Select the Roll Type for the defender([*[&TTid]:t-name]) using [b][&TSkill][/b]:
  --+|[t [&tStyle]]
      [tr [&trStyle1]][td [&tdStyle1]][rbutton]Normal::STEP4;N[/rbutton][/td][/tr]
      [tr [&trStyle1]][td [&tdStyle1]][rbutton]Advantage::STEP4;A[/rbutton][/td][/tr]
      [tr [&trStyle1]][td [&tdStyle1]][rbutton]Disadvantage::STEP4;D[/rbutton][/td][/tr]
      [/t]
  --X|

--:STEP4| Process result from step 3 and execute the roll
  --&TRT|1d20
  --?[&reentryval] -eq A|&TRT;2d20kh1
  --?[&reentryval] -eq D|&TRT;2d20kl1

  --=SResult|[&SRT] + [&SBonus] 
  --=TResult|[&TRT] + [&TBonus]
  
  --?[$SResult.Raw] -ge [$TResult.Raw]|WON|LOST

  --:WON|

  --+|Attacker [b]won[/b] ([$SResult] vs. [$TResult])the contest, select the desired effect:
  --+|[t [&tStyle]]
      [tr [&trStyle1]][td [&tdStyle1]][rbutton]Knock it Prone::STEP5;PRONE[/rbutton][/td][/tr]
      [tr [&trStyle1]][td [&tdStyle1]][rbutton]Push it away 5'::STEP5;PUSH[/rbutton][/td][/tr]
      [/t]

  --x|
  --:LOST|
  --+|Defender [b]won[/b] ([$SResult] vs. [$TResult]) the contest, better luck next time!
--x|

--:STEP5| If attacker won the contest, decide on prone or shoved back 5 feet
  --?[&reentryval] -eq PUSH|PUSH|PRONE

  --:PUSH|

    -->MOVE_TOKEN|[&STid];[&TTid];1
    --+|[*[&STid]:t-name] has pushed [*[&TTid]:t-name] back five feet!

  --X|
  --:PRONE|

    --@token-mod|_set statusmarkers|Prone _ids [&TTid] _ignore-selected
    --+|[*[&TTid]:t-name] is now [b]prone[/b]!
    --+|A [b][i]prone[/b][/i] creature’s only movement option is to crawl, unless it stands up and thereby ends the condition.
    --+|The creature has **Disadvantage** on attack rolls.
    --+|An attack roll against the creature has **Advantage** if the attacker is within 5 feet of the creature. Otherwise, the attack roll has **Disadvantage**.

--X|

--:GET_SKILL_BONUS|TokenID, skill
  --&Tid|[%1%]
  --&Skill|[%2%]
  --&SB|[&Skill]_bonus

  --/|*Token|[*[&Tid]:t-name]
  --/|*Skill|[&Skill]
  --/|*SB|[&SB]: [*[&Tid]:[&SB]]
  --/|*NPC|[*[&Tid]:npc]

  --?"[*[&Tid]:npc]" -eq 1|NPC|CHAR
  --:NPC|
    --&NSF|npc_[&Skill]_flag
    --&NSB|npc_[&Skill]
    --/|*NSF|[&NSF]: [*[&Tid]:[&NSF]] 
    --/|*NSB|[&NSB]: [*[&Tid]:[&NSB]]: [*[&Tid]:[&NSB]]
    --?[*[&Tid]:[&NSF]] -eq 1|&Bonus;[*[&Tid]:[&NSB]]|&Bonus;[*[&Tid]:[&SB]]
        --/|*Bonus|[&Bonus]
    --<|Return
  --:CHAR|
    --&Bonus|[*[&Tid]:[&SB]]
    --/|*Bonus|[&Bonus]   
    --<|Return
  --x|

--:MOVE_TOKEN|STokenid, TTokenId, Units
  --&TId1|[%1%]
  --&TId2|[%2%]
  --&Units|[%3%]

  --/| Angle function returns a string variable (&) with lots of decimal points.
  --~A|math;angle;[&TId1];[&TId2]

  --/| I've found that TokenMod chokes on a movement angle with lots of decimal points, so rounding is needed
  --/|$Ar will now contain a rounded integer version of &A
  --~Ar|math;round;[&A]


  --/|&Angle will end up being one of the common directions which may work better for grids
  --?[$Ar] -gt 337.5 -or [$Ar] -le 22.5|&Angle;0
  --?[$Ar] -gt 22.5 -and [$Ar] -le 67.5|&Angle;45
  --?[$Ar] -gt 67.5 -and [$Ar] -le 112.5|&Angle;90
  --?[$Ar] -gt 112.5 -and [$Ar] -le 157.5|&Angle;135
  --?[$Ar] -gt 157.5 -and [$Ar] -le 202.5|&Angle;180
  --?[$Ar] -gt 202.5 -and [$Ar] -le 247.5|&Angle;225
  --?[$Ar] -gt 247.5 -and [$Ar] -le 292.5|&Angle;270
  --?[$Ar] -gt 292.5 -and [$Ar] -le 337.5|&Angle;315

  --/| Added for dealing with a SC 1.39a bug (returns negative angles beteen 270 and 360)
  --?[$Ar] -gt -90 -and [$Ar] -le -67.5|&Angle;270
  --?[$Ar] -gt -67.5 -and [$Ar] -le -22.5|&Angle;315
  --?[$Ar] -gt -22.5 -and [$Ar] -le 0|&Angle;0

  --&UnitsFactor|1
  --?[&Angle] -eq 45|&UnitsFactor;1.4142
  --?[&Angle] -eq 135|&UnitsFactor;1.4142
  --?[&Angle] -eq 225|&UnitsFactor;1.4142
  --?[&Angle] -eq 315|&UnitsFactor;1.4142

  --=U|[&Units] * [&UnitsFactor] 

  --*Angles|[&Angle] ([&A]) ([$Ar]) 
  --/| Time to call the magic TokenMod function to make the target token move across the screen
  --@token-mod|_move =[&Angle]|[$U.Raw]g _ids [&TId2] _ignore-selected

--<|Return

}}
