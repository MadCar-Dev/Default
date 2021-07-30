!script {{  

  --/|Script Name : Set Token Status Marker
  --/|Version     : 4.0
  --/|Requires SC : 1.3.7+, TokenMod
  --/|Author      : Will M.

  --/|Description : Sets a common condition or status on a token and provides
  --/|              definition back to user on effect/impact of selected condtion/status

   --#reentrant|MarkToken
   --#title|Set Condition/Status
  --#titleCardBackground|#932729
  --#whisper|self
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|#0905f2
  --#buttonbordercolor|#FFFFFF
  --#bodyFontSize|11px 
  --#buttonFontSize|11px
  --#debug|1

  --&TokenId|@{selected|token_id}

  --&tStyle|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;"
  --&trStyle1|style="border:0px dashed black;"
  --&tdStyle1|style="width:50%;text-align:left;background-color:#FFFFFF;font-size:100%"
  --&tdStyle2|style="width:50%;text-align:left;background-color:#FFFFFF;font-size:100%"

  --=Cond_Col|1
  --=Stat_Col|1

  --/| Initialize my defined list of StatusMarkers in pairs of values (StatusMarkerId;Name)

  --~|array;define;arySM;Aid;Asleep;Bane;Beast-Shape;Blessed;Blinded;Charmed;Check;Cold;Concentrating;Confused;Cross;Cursed;Deafened;Disarmed;Diseased;Drunk;Exhaustion-1;Exhaustion-2;Exhaustion-3;Exhaustion-4;Exhaustion-5;Exhaustion-6;Fire;Flying;Frightened;Flying;Gaseous;Haste;Heroism;Hexed;Immobilized;Incapacitated;Inspired;Invisible;Madness;Mage-Armor;Marked;Mirror-Image;Paralyzed;Poisoned;Prone;Rage;Restrained;Shielded;Silenced;Size-Increase;Size-Reduce;Slowed;Stealth;strong;Stunned;Surprised;Unconscious;Wet

  --~|array;define;aryConditions;Blinded;Charmed;Deafened;Exhaustion-1;Exhaustion-2;Exhaustion-3;Exhaustion-4;Exhaustion-5;Exhaustion-6;Frightened;Grappled;Incapacitated;Invisible;Paralyzed;Petrified;Poisoned;Prone;Restrained;Stunned;Unconscious

  --/|Run through the array of SMs and break into Condition or Status 

  --~SM_Name|array;getfirst;arySM
  --/|+Debug|SM_Name: [&SM_Name]
  
  --:LOOPSTART|
    --?[&SM_Name] -eq ArrayError|LOOPEND

        --/| Test to see if this is a condition or status
        --~COND_INDEX|array;indexof;aryConditions;[&SM_Name]
        --/|+Debug|Cond_Index: [&COND_INDEX]

        --&tbl|[td [&tdStyle1]][rbutton][sm width=16px][&SM_Name][/sm]&nbsp;[&SM_Name]::SET_STATUSMARK;[&TokenId]\[&SM_Name][/rbutton][/td]

        --?[&COND_INDEX] -eq ArrayError|IS_STATUS|IS_COND
        --:IS_COND|
          --/| Are we on column 1 or 2?
          --?[$Cond_Col.Total] -eq 1|COND_1|COND_2
            --:COND_1|
              --&tbl|[tr [&trStyle1]] [&tbl]
              --=Cond_Col|2
              --&tblCond|+ [&tbl]
              --^CONTINUE|

            --:COND_2|
              --&tbl|[&tbl] [/tr]
              --=Cond_Col|1
              --&tblCond|+ [&tbl]            
              --^CONTINUE|

        --:IS_STATUS|
          --?[$Stat_Col.Total] -eq 1|STAT_1|STAT_2
            --:STAT_1|
              --&tbl|[tr [&trStyle1]] [&tbl]
              --=Stat_Col|2
              --&tblStat|+ [&tbl]
              --^CONTINUE|

            --:STAT_2|
              --&tbl|[&tbl] [/tr]
              --=Stat_Col|1
              --&tblStat|+ [&tbl]            
              --^CONTINUE|

    --:CONTINUE|
    --~SM_Name|array;getnext;arySM
    --/+Debug|SM_Name: [&SM_Name]
    --^LOOPSTART|
  --:LOOPEND|

  --/| Complete the tables based on where we ended up?
  --?[$Cond_Col] -eq 1|COND_NO_BLANK_CELLS
  --&tblCond|+ [td][/td] 
  --:COND_NO_BLANK_CELLS|

  --?[$Stat_Col] -eq 1|STAT_NO_BLANK_CELLS
  --&tblStat|+ [td][/td] 
  --:STAT_NO_BLANK_CELLS|

  --/~LenS|string;length;[&tblStat]
  --/+Debug2| Length Cond: [&LenC] Stat: [&LenS] 
  
  --&tblCond|[t [&tStyle]] [&tblCond] [/tr] [/t]
  --&tblStat|[t [&tStyle]] [&tblStat] [/tr] [/t]

  --/~LenS|string;length;[&tblStat]
  --/+Debug3| Length Cond: [&LenC] Stat: [&LenS] 
  
  -->SECTION_HEADER|Mark Conditions
  --+|[&tblCond]

  -->SECTION_HEADER|Mark Status
  --+|[&tblStat]

--X|

--:SET_STATUSMARK|
  --~Arg|string;split;\;[&reentryval]
  --/+Debug|[&reentryval]
  --/+Debug|[&Arg1] [&Arg2]
  --&TokenId|[&Arg1]
  --&SM|[&Arg2]
  --#titleCardBackground|#932729 
  --#oddRowBackground|#CEC7B6 
  --#evenRowBackground|#B6AB91
  --#Title|[&SM]
  --#leftsub|[*[&TokenId]:t-name]
  --#rightsub|

  --#hidecard|0

  --&SM_Token|[sm width=50px][&SM][/sm]

  --C[&SM]|Aid:Aid|Asleep:Sleeping|Bane:Bane|Beast-Shape:Beast|Blessed:Blessed|Blinded:Blinded|Charmed:Charmed|Check:Check|Cold:Cold|Concentrating:Concentrating|Confused:Confused|Cross:Cross|Cursed:Cursed|dead:dead|Deafened:Deafened|Disarmed:Disarmed|Diseased:Diseased|Drunk:Drunk|Exhaustion-1:Exh1|Exhaustion-2:Exh2|Exhaustion-3:Exh3|Exhaustion-4:Exh4|Exhaustion-5:Exh5|Exhaustion-6:Exh6|Fire:Fire|Flying:Flying|Frightened:Frightened|Gaseous:Gaseous|Grappled:Grappled|Haste:Haste|Heroism:Heroism|Hexed:Hexed|Immobilized:Immobilized|Incapacitated:Incapacitated|Inspired:Inspired|Invisible:Invisible|Madness:Madness|Mage-Armor:Mage_Armor|Marked:Marked|Mirror-Image:Mirror_Image|Paralyzed:Paralyzed|Petrified:Petrified|Poisoned:Poisoned|Prone:Prone|Rage:Rage|Restrained:Restrained|Shielded:Shielded|Silenced:Silenced|Size-Reduce:Size_Reduce|Size-Increase:Size_Increase|Slowed:Slowed|Stealth:Stealth|Stunned:Stunned|Surprised:Surprised|Unconscious:Unconscious|Shrunk:Shrunk|Sleeping:Sleeping|strong:strong|Wet:Wet

  --+ERROR|[b][c]Status not found![/c][/b]

  --:Aid| 
      --+|[&SM_Token] [b][i]Here, let me help you. [/i][/b]
      --+|**Your spell bolsters your allies with toughness and resolve.** Choose up to **three** creatures within range. Each target’s hit point maximum and current hit points increase by 5 for the duration.
      --+|**At Higher Levels.** When you cast this spell using a spell slot of 3rd level or higher, a target’s hit points increase by an additional 5 for each slot level above 2nd.
  --^MARK|

  --:Asleep|
  --:Sleeping|
      --+|[&SM_Token] [b][i]Sweet dreams[/i][/b]
      --+|This spell sends creatures into a magical slumber. Roll 5d8; the total is how many hit points of creatures this spell can affect. Creatures within 20 feet of a point you choose within range are affected in ascending order of their current hit points (ignoring unconscious creatures).
      --+|Starting with the creature that has the lowest current hit points, each creature affected by this spell falls unconscious until the spell ends, the sleeper takes damage, or someone uses an action to shake or slap the sleeper awake. Subtract each creature’s hit points from the total before moving on to the creature with the next lowest hit points. A creature’s hit points must be equal to or less than the remaining total for that creature to be affected.
      --+|Undead and creatures immune to being charmed aren’t affected by this spell.
      --+|**At Higher Levels.** When you cast this spell using a spell slot of 2nd level or higher, roll an additional 2d8 for each slot level above 1st.
  --^MARK|

  --:Bane|
      --+|[&SM_Token] [b][i]You are the bane of my existance![/i][/b]
      --+|Up to three creatures of your choice that you can see within range must make **Charisma** saving throws. Whenever a target that fails this saving throw makes an attack roll or a saving throw before the spell ends, the target must roll a **d4** and [b][i]subtract[/b][/i] the number rolled from the attack roll or saving throw.
      --+|**At Higher Levels.** When you cast this spell using a spell slot of 2nd level or higher, you can target one additional creature for each slot level above 1st.
    --^MARK|

  --:Beast|
      --+|[&SM_Token] [b][i]Grrrr!!![/i][/b]
      --+|You can stay in a beast shape for a number of hours equal to half your druid level (rounded down). You then revert to your normal form unless you expend another use of this feature. You can revert to your normal form earlier by using a bonus action on your turn. You automatically revert if you fall unconscious, drop to 0 hit points, or die.  While you are transformed, the following rules apply:
      --+|  Your game statistics are replaced by the statistics of the beast, but you retain your alignment, personality, and Intelligence, Wisdom, and Charisma scores. You also retain all of your skill and saving throw proficiencies, in addition to gaining those of the creature. If the creature has the same proficiency as you and the bonus in its stat block is higher than yours, use the creature’s bonus instead of yours. If the creature has any legendary or lair actions, you can’t use them.
      --+|  When you transform, you assume the beast’s hit points and Hit Dice. When you revert to your normal form, you return to the number of hit points you had before you transformed. However, if you revert as a result of dropping to 0 hit points, any excess damage carries over to your normal form. For example, if you take 10 damage in animal form and have only 1 hit point left, you revert and take 9 damage. As long as the excess damage doesn’t reduce your normal form to 0 hit points, you aren’t knocked unconscious.
      --+|  You can’t cast spells, and your ability to speak or take any action that requires hands is limited to the capabilities of your beast form. Transforming doesn’t break your concentration on a spell you’ve already cast, however, or prevent you from taking actions that are part of a spell, such as call lightning, that you’ve already cast.
      --+|  You retain the benefit of any features from your class, race, or other source and can use them if the new form is physically capable of doing so. However, you can’t use any of your special senses, such as darkvision, unless your new form also has that sense.
      --+|  You choose whether your equipment falls to the ground in your space, merges into your new form, or is worn by it. Worn equipment functions as normal, but the DM decides whether it is practical for the new form to wear a piece of equipment, based on the creature’s shape and size. Your equipment doesn’t change size or shape to match the new form, and any equipment that the new form can’t wear must either fall to the ground or merge with it. Equipment that merges with the form has no effect until you leave the form.
    --^MARK|

  --:Blessed|
      --+|[&SM_Token] [b][i]I couldn't be more blessed.[/i][/b]
      --+|You bless up to three creatures of your choice within range. Whenever a target makes an attack roll or a saving throw before the spell ends, the target can roll a **d4** and add the number rolled to the attack roll or saving throw.
      --+|**At Higher Levels.** When you cast this spell using a spell slot of 2nd level or higher, you can target one additional creature for each slot level above 1st.
      --^MARK|

  --:Blinded|
    --+|[&SM_Token] [b][i]Mama alwasy told me not to look into the eyes of the sun...[/i][/b]
    --+|A [b][i]Blinded[/b][/i] creature can’t see and automatically fails any ability check that requires sight.
    --+|Attack rolls against the creature have **advantage**, and the creature’s attack rolls have **disadvantage**.
    --^MARK|

  --:Charmed|
    --+|[&SM_Token][b][i]I put a spell on you![/i][/b]
    --+| A [b][i]charmed[/b][/i] creature can’t attack the charmer or target the charmer with harmful abilities or magical effects.
    --+|The charmer has **advantage** on any ability check to interact socially with the creature.
  --^MARK|
  
  --:Charmed|
  --:Cross|
    --+|[&SM_Token] Utility Markers.  Could be used to indicate Advantage/Disadvantage
  --^MARK|

  --:Cold|
    --+|[&SM_Token] [b][i]Brrrrr!!!!![/i][/b]
    --+|The infernal chill radiating from an ice devil's spear and the frigid blast of a young white dragon's breath deal cold damage.  Often a side affect of cold-damage is reduction in speed.
    --^MARK|

  --:Concentrating|
      --+|[&SM_Token][b][i]Leave me alone![/i][/b]
      --+| Normal activity, such as moving and attacking, doesn’t interfere with concentration. The following factors can break concentration:
      --+|  **Casting another spell that requires concentration.** You lose concentration on a spell if you cast another spell that requires concentration. You can’t concentrate on two spells at once.
      --+|  **Taking damage.** Whenever you take damage while you are concentrating on a spell, you must make a **Constitution** saving throw to maintain your concentration. The [b][i]DC equals 10 or half the damage you take, whichever number is higher.[/b][/i] If you take damage from multiple sources, such as an arrow and a dragon’s breath, you make a separate saving throw for each source of damage.
      --+|  **Being [b][i]incapacitated[/b][/i] or [b][i]killed[/b][/i].** You lose concentration on a spell if you are incapacitated or if you die.
    --^MARK|

  --:Confused|
      --+|[&SM_Token] [b][i]Which way did he go, which way did he go?[/i][/b]
      --+| This spell assaults and twists creatures' minds, spawning delusions and provoking uncontrolled action. Each creature in a 10-foot-radius sphere centered on a point you choose within range must succeed on a Wisdom saving throw when you cast this spell or be affected by it.
      --+|An affected target can't take reactions and must roll a d10 at the start of each of its turns to determine its behavior for that turn.
      
        --&tStyle|style = "width:100%;border-collapse:collapse;border:1px dashed black;"
        --&trStyle1|style="border-top:1px dashed black;"
        --&tdStyle1|style="width:25%;text-align:center;background-color:#AFFFFF;font-size:80%"
        --&tdStyle2|style="width:75%;text-align:center;background-color:#BFFFFF;font-size:80%"
      
        --+|[t [&tStyle]]
            [tr [&trStyle1]]
                [td [&tdStyle1]] 1 [/td] [td [&tdStyle2]] The creature uses all its movement to move in a random direction. To determine the direction, roll a d8 and assign a direction to each die face. The creature doesn't take an action this turn.[/td]                
              [/tr] [tr [trStyle2]]
                [td [&tdStyle1]] 2-6 [/td] [td [&tdStyle2]] The creature doesn't move or take actions this turn.[/td]
              [/tr] [tr [trStyle2]]
                [td [&tdStyle1]] 3-7 [/td] [td [&tdStyle2]] The creature uses its action to make a melee attack against a randomly determined creature within its reach. If there is no creature within its reach, the creature does nothing this turn. [/td]
              [/tr] [tr [trStyle2]]
                [td [&tdStyle1]] 9-10 [/td] [td [&tdStyle2]] The creature can act and move normally. [/td]
              [/tr]
            [/t]
      
      --+|At the end of each of its turns, an affected target can make a Wisdom saving throw. If it succeeds, this effect ends for that target.
      --+|[b]At Higher Levels.[/b] When you cast this spell using a spell slot of 5th level or higher, the radius of the sphere increases by 5 feet for each slot level above 4th.
      --^MARK|

  --:Cursed|[b][i] [/i][/b]
      --+|[&SM_Token] [b][i]I place a curse on you![/i][/b]
      --+|[u]From the [b]Curse[/b] Spell:[/u]
      --+|You touch a creature, and that creature must succeed on a Wisdom saving throw or become cursed for the duration of the spell. When you cast this spell, choose the nature of the curse from the following options:
      --+|* Choose one ability score. While cursed, the target has disadvantage on ability checks and saving throws made with that ability score.
      --+|* While cursed, the target has disadvantage on attack rolls against you.
      --+|* While cursed, the target must make a Wisdom saving throw at the start of each of its turns. If it fails, it wastes its action that turn doing nothing.
      --+|* While the target is cursed, your attacks and spells deal an extra 1d8 necrotic damage to the target.
      --+|A remove curse spell ends this effect. At the DM's option, you may choose an alternative curse effect, but it should be no more powerful than those described above. The DM has final say on such a curse's effect.
      --+At Higher Levels.|If you cast this spell using a spell slot of 4th level or higher, the duration is concentration, up to 10 minutes. If you use a spell slot of 5th level or higher, the duration is 8 hours. If you use a spell slot of 7th level or higher, the duration is 24 hours. If you use a 9th level spell slot, the spell lasts until it is dispelled. Using a spell slot of 5th level or higher grants a duration that doesn't require concentration.
    --^MARK|

  --:dead|
      --+|[&SM_Token] [b][i]Dam, this sucks![/i][/b]
      --+|When you drop to 0 hit points, you either die outright or fall unconscious, see [button]Dropping to 0 Hitpoints::https://www.dndbeyond.com/sources/basic-rules/combat#Droppingto0HitPoints[/button] for more info.
    --^MARK|

  --:Deafened|
    --+|[&SM_Token] [b][i]Did you say something?[/i][/b]
    --+|A [b][i]deafened[/b][/i] creature can’t hear and automatically fails any ability check that requires hearing.
    --^MARK|

  --:Disarmed|
      --+|[&SM_Token] [b][i]Does the 5 second rule apply here?[/i][/b]
      --+|Whatever you were holding is now on the ground.  It will take an action to retrieve the object.  
    --^MARK|
  
  --:Diseased|
      --+|[&SM_Token] [b][i]You look like shit![/i][/b]
      --+| You have been diseased.  Hopefully there is a cure, and you can afford it.
    --^MARK|

  --:Drunk|
      --+|[&SM_Token] [b][i]I'll get you a bucket.[/i][/b]
      --+|You have become drunk (poisoned) for 1 hour, and must then repeat the a saving throw.
      --+|On a successful save after 1 hour and you have stopped drinking your are no longer drunk. 
      --+|On a failed save after 1 hour, the character continues to drink until falling unconscious for 1d20 - [Concentration Bonus] hours, then awakens and remains drunk for 1 hour thereafter.
      --+|Once a character’s drunken state ends, whether after 1 hour or several hours, the drinker suffers one level of exhaustion.
    --^MARK|

  --:Exh1| Future version may test for current level and auto update.  Or maybe it breaks the request up into Exhaustion followed by a Level dialog 
      --+|[&SM_Token] [b][i]One second, I need a break.[/i][/b]
      --+| **Disadvantage** on ability checks
      --+|Next Level: Speed havled
      --+|An effect that removes exhaustion reduces its level as specified in the effect’s description, with all exhaustion effects ending if a creature’s exhaustion level is reduced below 1.
      --+|Finishing a long rest reduces a creature’s exhaustion level by 1, provided that the creature has also ingested some food and drink. Also, being raised from the dead reduces a creature’s exhaustion level by 1
    --^MARK|

  --:Exh2|
      --+|[&SM_Token][b][i]Go on whithout me, I'll catch up.[/i][/b]
      --+|Speed halved
      --+|Next Level: **Disadvantage** on attack rolls and saving throws
      --+|An effect that removes exhaustion reduces its level as specified in the effect’s description, with all exhaustion effects ending if a creature’s exhaustion level is reduced below 1.
      --+|Finishing a long rest reduces a creature’s exhaustion level by 1, provided that the creature has also ingested some food and drink. Also, being raised from the dead reduces a creature’s exhaustion level by 1
    --^MARK|

  --:Exh3|
      --+|[&SM_Token] [b][i]I think I'm going to throw up.[/i][/b]
      --+|**Disadvantage** on attack rolls and saving throws
      --+|Next Level: Hitpoint maximum halved
      --+|An effect that removes exhaustion reduces its level as specified in the effect’s description, with all exhaustion effects ending if a creature’s exhaustion level is reduced below 1.
      --+|Finishing a long rest reduces a creature’s exhaustion level by 1, provided that the creature has also ingested some food and drink. Also, being raised from the dead reduces a creature’s exhaustion level by 1
    --^MARK|

  --:Exh4|
      --+|[&SM_Token][b][i]I just need a 4 hour nap, a coffee and 3 donuts'[/i][/b]
      --+|Hitpoint maximum halved
      --+|Next Level: Speed reduced to zero (0)
      --+|An effect that removes exhaustion reduces its level as specified in the effect’s description, with all exhaustion effects ending if a creature’s exhaustion level is reduced below 1.
      --+|Finishing a long rest reduces a creature’s exhaustion level by 1, provided that the creature has also ingested some food and drink. Also, being raised from the dead reduces a creature’s exhaustion level by 1
    --^MARK|

  --:Exh5|
      --+|[&SM_Token] [b][i]Hibernation mode activated![/i][/b]
      --+|Speed reduced to zero (0)
      --+|Next Level: [b][i]**Death**[/b][/i]
      --+|An effect that removes exhaustion reduces its level as specified in the effect’s description, with all exhaustion effects ending if a creature’s exhaustion level is reduced below 1.
      --+|Finishing a long rest reduces a creature’s exhaustion level by 1, provided that the creature has also ingested some food and drink. Also, being raised from the dead reduces a creature’s exhaustion level by 1
    --^MARK|

  --:Exh6|
      --+|[&SM_Token] [b][i]It's not that I'm afraid to die,I just don't want to be there when it happens.[/i][/b]
      --+| **Death**    
    --^MARK|

  --:Fire|
    --+|[&SM_Token][b][i]FIRE!!!!!![/i][/b]
    --+|In addition to Fire damage, fire can often ignite flammable ojbects in the area that aren't being worn or carried.
  --^MARK|

  --:Flying|
    --+|[&SM_Token][b][i]Look at me, I can fly![/i][/b]
    --+|Flying creatures enjoy many benefits of mobility, but they must also deal with the danger of falling. If a flying creature is knocked prone, has its speed reduced to 0, or is otherwise deprived of the ability to move, the creature falls, unless it has the ability to hover or it is being held aloft by magic, such as by the fly spell.    
  --^MARK|

  --:Frightened|
      --+|[&SM_Token] [b][i]It's OK to be scared. It just means you're about to do something really, really brave.[/i][/b]
      --+| A [b][i]frightened[/b][/i] creature has **disadvantage** on ability checks and attack rolls while the source of its fear is within line of sight.
      --+|The creature can’t willingly move closer to the source of its fear.
  --^MARK|

  --:Gaseous|
      --+|[&SM_Token] [b][i] What's that smell?[/i][/b]
      --+| [b][i]From the Gaseous Form spell.[/b][/i]  You transform a willing creature you touch, along with everything it's wearing and carrying, into a misty cloud for the duration. The spell ends if the creature drops to 0 hit points. An incorporeal creature isn't affected.
      --+|While in this form, the target's only method of movement is a flying speed of 10 feet. The target can enter and occupy the space of another creature. The target has resistance to nonmagical damage, and it has advantage on Strength, Dexterity, and Constitution saving throws. The target can pass through small holes, narrow openings, and even mere cracks, though it treats liquids as though they were solid surfaces. The target can't fall and remains hovering in the air even when stunned or otherwise incapacitated.
      --+|While in the form of a misty cloud, the target can't talk or manipulate objects, and any objects it was carrying or holding can't be dropped, used, or otherwise interacted with. The target can't attack or cast spells.
    --^MARK|

  --:Grappled|
  --:Immobilized|  
      --+|[&SM_Token] [b][i]Ok, you got me, now let me go![/i][/b]
      --+|A [b][i]grappled[/b][/i] creature’s speed becomes 0, and it can’t benefit from any bonus to its speed.
      --+|The condition ends if the grappler is [b][i]incapacitated[/b][/i] (see the condition).
      --+|The condition also ends if an effect removes the grappled creature from the reach of the grappler or grappling effect, such as when a creature is hurled away by the [b][i]thunderwave[/b][/i] spell.
  --^MARK|
 
  --:Haste|
      --+|[&SM_Token] [b][i]Arriba arriba!!![/i][/b]
      --+|Your speed is doubled, it gains a +2 bonus to AC, you have advantage on Dexterity saving throws, and you gain an additional action on each of its turns. That action can be used only to take the Attack (one weapon attack only), Dash, Disengage, Hide, or Use an Object action.
      --+|When the effect ends, you can’t move or take actions until after its next turn, as a wave of lethargy sweeps over you.
     --^MARK|
  
  --:Heroism|
      --+|[&SM_Token][b][i]Here I am to save the day![/b][/i]
      --+|A willing creature you touch is imbued with bravery. Until the spell ends, the creature is immune to being **frightened** and gains temporary hit points equal to your spellcasting ability modifier at the start of each of its turns.
      --+|When the spell ends, the target loses any remaining temporary hit points from this spell.
      --+|**At Higher Levels.** When you cast this spell using a spell slot of 2nd level or higher, you can target one additional creature for each slot level above 1st.
   
    --^MARK|
  --:Hexed|
      --+|[&SM_Token][b][i]I don't like to call it 'Revenge', 'Returning the favor' sounds much better![/b][/i]
      --+|You place a curse on a creature that you can see within range. Until the spell ends, you deal an extra **1d6** [b][i]necrotic[/b][/i] damage to the target whenever you hit it with an attack. Also, choose one ability when you cast the spell. The target has **Disadvantage** on ability checks made with the chosen ability.
      --+|If the target drops to 0 hit points before this spell ends, you can use a bonus action on a subsequent turn of yours to curse a new creature.
      --+|A remove curse cast on the target ends this spell early.
      --+|**At Higher Levels.** When you cast this spell using a spell slot of 3rd or 4th level, you can maintain your concentration on the spell for up to 8 hours. When you use a spell slot of 5th level or higher, you can maintain your concentration on the spell for up to 24 hours.
    --^MARK|

  --:Incapacitated|
      --+|[&SM_Token][b][i]Turn out the lights, the party's over.[/b][/i]
      --+|An [b][i]incapacitated[/b][/i] creature can’t take **actions** or **reactions**.    
  --^MARK|

  --:Inspired|
      --+|[&SM_Token][b][i]Anything you can do, I can do better, I can do anything better than you.[/i][/b]
      --+|You can inspire others through stirring words or music. To do so, you use a bonus action on your turn to choose one creature other than yourself within 60 feet of you who can hear you. That creature gains one [b][i]Bardic Inspiration die[/b][/i], a **d6**. 
      --+|Once within the next 10 minutes, the creature can roll the die and add the number rolled to one ability check, attack roll, or saving throw it makes. The creature can wait until after it rolls the **d20** before deciding to use the Bardic Inspiration die, but must decide before the DM says whether the roll succeeds or fails. Once the [b][i]Bardic Inspiration die[/b][/i] is rolled, it is lost. A creature can have only one [b][i]Bardic Inspiration die[/b][/i] at a time.
      --+|You can use this feature a number of times equal to your Charisma modifier (a minimum of once). You regain any expended uses when you finish a long rest.
      --+|**At Higher Levels.** Your Bardic Inspiration die changes when you reach certain levels in this class. The die becomes a d8 at 5th level, a d10 at 10th level, and a d12 at 15th level.
    --^MARK|

  --:Invisible|
      --+|[&SM_Token] [b][i]You can't see me![/i][/b]
      --+|An [b][i]invisible[/b][/i] creature is impossible to see without the aid of magic or a special sense. For the purpose of hiding, the creature is heavily obscured. The creature’s location can be detected by any noise it makes or any tracks it leaves.
      --+|Attack rolls agaisnt the creature have **Disadvantage**, and the creature’s attack rolls have **Advantage**.
    --^MARK|


  --:Madness|
      --/+| !!! Future Update:  Tie in to the Madness Scripts once I've consolidated them
      --+|[&SM_Token][b][i] Lets go Crazy![/i][/b]
      --+| What form of Madness have you been inflicted with? [button]Madness Macro::~Mule|Madness[/button]
  --^MARK|

  --:Mage_Armor|
    --+|[&SM_Token][b][i]Remember, you can't beam through a force field. So, don't try it.[/i][/b]
    --+|You touch a willing creature who isn't wearing armor, and a protective magical force surrounds it until the spell ends. The target's base AC becomes 13 + its Dexterity modifier. The spell ends if the target dons armor or if you dismiss the spell as an action.
  --^MARK|

  --:Marked|
  --:HuntersMark|
      --+|[&SM_Token][b][i] Tag! You're IT![/i][/b]
      --+| You choose a creature you can see within range and mystically mark it as your quarry. Until the spell ends, you deal an extra 1d6 damage to the target whenever you hit it with a weapon attack.
      --+|You have advantage on any Wisdom (Perception) or Wisdom (Survival) check you make to find it.
      --+|If the target drops to 0 hit points before this spell ends, you can use a bonus action on a subsequent turn of yours to mark a new creature.
      --+|**At Higher Levels.** When you cast this spell using a spell slot of 3rd or 4th level, you can maintain your concentration on the spell for up to 8 hours. When you use a spell slot of 5th level or higher, you can maintain your concentration on the spell for up to 24 hours.
    --^MARK|

  --:Mirror_Image|
      --+|[&SM_Token][b][i]Out of the box came Thing 2 and Thing 1![/i][/b]
      --+|Three illusory duplicates of yourself appear in your space. Until the spell ends, the duplicates move with you and mimic your actions, shifting position so it’s impossible to track which image is real. You can use your action to dismiss the illusory duplicates.
      --+|Each time a creature targets you with an attack during the spell’s duration, roll a **d20** to determine whether the attack instead targets one of your duplicates.
      --+|If you have three duplicates, you must roll a 6 or higher to change the attack’s target to a duplicate. With two duplicates, you must roll an 8 or higher. With one duplicate, you must roll an 11 or higher.
      --+|A duplicate’s AC equals 10 + your Dexterity modifier. If an attack hits a duplicate, the duplicate is destroyed. A duplicate can be destroyed only by an attack that hits it. It ignores all other damage and effects. The spell ends when all three duplicates are destroyed.
      --+|A creature is unaffected by this spell if it can’t see, if it relies on senses other than sight, such as blindsight, or if it can perceive illusions as false, as with truesight.
    --^MARK|

  --:Paralyzed|
      --+|[&SM_Token] [b][i]I think my legs are falling asleep.[/i][/b]
      --+| A [b][i]paralyzed[/b][/i] creature is [b][i]incapacitated[/b][/i] (see the condition) and can’t move or speak.
      --+|The creature automatically fails **Strength** and **Dexterity** saving throws.
      --+|Attack rolls against the creature have **Advantage**.
      --+|Any attack that hits the creature is a **critical hit** if the attacker is within 5 feet of the creature.    
    --^MARK|

  --:Petrified|
      --+|[&SM_Token] [b][i] First I was afraid, I was petrified [/i][/b]
      --+|A [b][i]petrified[/b][/i] creature is transformed, along with any nonmagical object it is wearing or carrying, into a solid inanimate substance (usually stone). Its weight increases by a factor of ten, and it ceases aging.
      --+|The creature is [b][i]incapacitated[/b][/i] (see the condition), can’t move or speak, and is unaware of its surroundings.
      --+|Attack rolls against the creature have **Advantage**.
      --+|The creature automatically fails **Strength** and **Dexterity** saving throws.
      --+|The creature has **Resistance** to all damage.
      --+|The creature is **immune** to poison and disease, although a poison or disease laready in its system is suspended, not neutralized.
    --^MARK|

  --:Poisoned|
      --+|[&SM_Token] [b][i] Never go against a Sicilian when death is on the line! [/i][/b]
      --+| A [b][i]poisoned[/b][/i] creature has **Disadvantage** on attack rolls and ability checks.

    --^MARK|
  --:Prone|
      --+|[&SM_Token] [b][i]I've fallen and can't get up.[/i][/b]
      --+|A [b][i]prone[/b][/i] creature’s only movement option is to crawl, unless it stands up and thereby ends the condition.
      --+|The creature has **Disadvantage** on attack rolls.
      --+|An attack roll against the creature has **Advantage** if the attacker is within 5 feet of the creature. Otherwise, the attack roll has **Disadvantage**.
    --^MARK|

  --:Rage|
      --+|[&SM_Token] [b][i]I'm this close to losing my shit![/i][/b]
      --+|In battle, you fight with primal ferocity. On your turn, you can enter a rage as a bonus action.  While raging, you gain the following benefits if you aren’t wearing heavy armor:
      --+|  - You have advantage on Strength checks and Strength saving throws.
      --+|  - When you make a melee weapon attack using Strength, you gain a bonus to the damage roll that increases as you gain levels as a barbarian, as shown in the Rage Damage column of the Barbarian table.
      --+|  - You have resistance to bludgeoning, piercing, and slashing damage.
      --+|If you are able to cast spells, you can’t cast them or concentrate on them while raging.
      --+|Your rage lasts for 1 minute. It ends early if you are knocked unconscious or if your turn ends and you haven’t attacked a hostile creature since your last turn or taken damage since then. You can also end your rage on your turn as a bonus action.
      --+|Once you have raged the number of times shown for your barbarian level in the Rages column of the Barbarian table, you must finish a long rest before you can rage again.
    --^MARK|

  --:Restrained|
      --+|[&SM_Token][b][i]Turn me loose, turn me loose, turn me loose, I've gotta do it my way[/i][/b]
      --+| A [b][i]restrained[/b][/i] creature’s speed becomes 0, and it can’t benefit from any bonus to its speed.
      --+|Attack rolls against the creature have **Advantage**, and the creature’s attack rolls have **Disadvantage**.
      --+|The creature has **Disadvantage** on **Dexterity** saving throws.
    --^MARK|

  --:Shield|
  --:Shielded|
      --+|[&SM_Token][b][i]You've been blocked![/i][/b]
      --+|An invisible barrier of magical force appears and protects you. Until the start of your next turn, you have a +5 bonus to AC, including against the triggering attack, and you take no damage from magic missile.    
    --^MARK|

  --:Silenced|
    --+|[&SM_Token] [b][i]Can you hear me now?[/i][/b]
    --+|For the duration, no sound can be created within or pass through a 20-foot-radius sphere centered on a point you choose within range. Any creature or object entirely inside the sphere is immune to thunder damage, and creatures are deafened while entirely inside it. Casting a spell that includes a verbal component is impossible there.
  --^MARK|

  --:Size_Increase|
  --:Enlarged|
      --+|[&SM_Token] [b][i]Eat Me. I knew who I was this morning, but I've changed a few times since then.[/i][/b]
      --+|If the target is a creature, everything it is wearing and carrying changes size with it. Any item dropped by an affected creature returns to normal size at once.
      --+|**Enlarge.** The target’s size doubles in all dimensions, and its weight is multiplied by eight. This growth increases its size by one category, from Medium to Large, for example. If there isn’t enough room for the target to double its size, the creature or object attains the maximum possible size in the space available. Until the spell ends, the target also has advantage on Strength checks and Strength saving throws. The target’s weapons also grow to match its new size. While these weapons are enlarged, the target’s attacks with them deal 1d4 extra damage.
  --^MARK|

  --:Size_Reduce|  
  --:Shrunk|
      --+|[&SM_Token] [b][i]Drink Me. Very few things are really impossible.[/i][/b]
      --+|If the target is a creature, everything it is wearing and carrying changes size with it. Any item dropped by an affected creature returns to normal size at once.
      --+|**Reduce.** The target’s size is halved in all dimensions, and its weight is reduced to one-eighth of normal. This reduction decreases its size by one category--from Medium to Small, for example. Until the spell ends, the target also has disadvantage on Strength checks and Strength saving throws. The target’s weapons also shrink to match its new size. While these weapons are reduced, the target’s attacks with them deal 1d4 less damage (this can’t reduce the damage below 1).
    --^MARK|

  --:Slowed|
      --+|[&SM_Token] You alter time around up to six creatures of your choice in a 40-foot cube within range. Each target must succeed on a Wisdom saving throw or be affected by this spell for the duration.
      --+|An affected target’s speed is halved, it takes a −2 penalty to AC and Dexterity saving throws, and it can’t use reactions. On its turn, it can use either an action or a bonus action, not both. Regardless of the creature’s abilities or magic items, it can’t make more than one melee or ranged attack during its turn.
      --+|If the creature attempts to cast a spell with a casting time of 1 action, roll a d20. On an 11 or higher, the spell doesn’t take effect until the creature’s next turn, and the creature must use its action on that turn to complete the spell. If it can’t, the spell is wasted.
      --+|A creature affected by this spell makes another Wisdom saving throw at the end of each of its turns. On a successful save, the effect ends for it.
    --^MARK|

  --:Stealth|
  --:Hidden|
      --+|[&SM_Token] [i][b]Sneaky, Sneaky!!![/b][/i]
      --+|**Unseen Attackers and Targets:** When you attack a target that you can’t see, you have disadvantage on the attack roll. This is true whether you’re guessing the target’s location or you’re targeting a creature you can hear but not see. If the target isn’t in the location you targeted, you automatically miss, but the DM typically just says that the attack missed, not whether you guessed the target’s location correctly.
      --+|When a creature can’t see you, you have advantage on attack rolls against it. If you are hidden, [i]both unseen and unheard[/i], when you make an attack, you give away your location when the attack hits or misses.
    --^MARK|

  --:strong|
        --+|[&SM_Token] The target has advantage on Strength checks, and his or her carrying capacity doubles.    
    --^MARK|

  --:Stunned|
      --+|[&SM_Token] A [b][i]stunned[/b][/i] creature is [b][i]incapacitated[/b][/i] (see the condition), can’t move, and can speak only falteringly.
      --+|The creature automatically fails **Strength** and **Dexterity** saving throws.
      --+|Attack rolls against the creature have **Advantage**
    --^MARK|

  --:Surprised|
    --+|[&SM_Token] [b][i]SURPRISE!!![/i][/b]
    --+|If you're surprised, you can't move or take an action on your first turn of the combat, and you can't take a reaction until that turn ends. A member of a group can be surprised even if the other members aren't.
  --^MARK|

  --:Unconscious|
      --+|[&SM_Token][b][i]Turn out the lights, the party's over...[/b][/i]
      --+|An [b][i]unconscious[/b][/i] creature is [b][i]incapacitated[/b][/i], can’t move or speak, and is unaware of its surroundings.
      --+|The creature drops whatever it’s holding and falls [b][i]prone[/b][/i].
      --+|The creature automatically fails **Strength** and **Dexterity** saving throws.
      --+|Attack rolls against the creature have **Advantage**
      --+|Any attack that hits the creature is a **critical hit** if the attacker is within 5 feet of the creature
    --^MARK|

  --:Wet|
      --+|[&SM_Token] You are drenched.  You have disadvantage on saving throws to prevent ill effects of cold weather.  You have advantage on saving throws associated effects or attacks dealing fire damage. 
      --+| It will take you one hour to dry, assuming you are in dry conditions.  
    --^MARK|

  --:MARK|
    --@token-mod|_set statusmarkers|[&SM] _ids [&TokenId] _ignore-selected
  --X|
--<|

--:SECTION_HEADER|Title
  --&hdrstyle_T|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:1px solid black;"
  --&hdrstyle_TR|style="border:0px solid black;"
  --&hdrstyle_TD|style="width:100%;background-color:#edf7f0;font-size:110%;font-weight:bold;text-align:center"
  --+|[t [&hdrstyle_T]][tr [&hdrstyle_TR]][td [&hdrstyle_TD]][c][%1%][/c][/td][/tr][/t]
--<|

}}
