!scriptcard {{ 
 --/|Script Name : Spellbook
  --/|Version     : 2.0
  --/|Requires SC : 1.3.7+, 
  --/|Author      : Will M.

  --/|Description : Spellbook utility, allows user to see prepared spells, remaining slots, 
  --/|              spell details, and even cast some spells

  --#reentrant|Spellbook
  --#titlefontsize|1.2em
  --#subtitlefontsize|1.0em
  --#titlecardbackground|#800080
  --#titlefontface|Tahoma
  --#bodyfontface|Tahoma
  --#tableborderradius|3px
  --#buttonfontsize|small
  --#buttontextcolor|#800080
  --#buttonbordercolor|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#whisper|self
  --#sourceToken|@{selected|token_id}

  --#title|Spell Book
  --#leftsub|@{selected|character_name} (@{selected|caster_level})
  --#debug|0

  --+Spell Attack Bonus: |[b][*S:spell_attack_bonus][/b]
  --+Spell Save DC: |[b][*S:spell_save_dc][/b]

  --&SCA|[*S:spellcasting_ability]
  --~SCA|string;substring;3;3;[&SCA]
  --+Spell Casting Ability: |[b][&SCA][/b]

  -->REPORTSPELLS|cantrip
  -->REPORTSPELLS|1
  -->REPORTSPELLS|2
  -->REPORTSPELLS|3
  -->REPORTSPELLS|4
  -->REPORTSPELLS|5
  -->REPORTSPELLS|6
  -->REPORTSPELLS|7
  -->REPORTSPELLS|8
  -->REPORTSPELLS|9


  --X| DONE

--:REPORTSPELLS|Parameter: Level
  --&zLvl|[%1%]

  --Rfirst|@{selected|character_id};repeating_spell-[&zLvl]

  --?"[*R:spellname]" -eq NoRepeatingAttributeLoaded|Done
  
  --&STblStyle1|"width:100%;text-align:center;padding:5px;border-spacing:0px;border-collapse:collapse;text-shadow: 1px 1px 3px purple;border: 1px dashed purple;"
  
  --&LvlDesc|Cantrip
  --&zLvlSlots|

  --?[&zLvl] -eq cantrip|SKIPCANTRIP
    --&LvlDesc|Level [&zLvl]
    --=SlotsTotal|[*S:lvl[&zLvl]_slots_total]
    --=SlotsExpended|[*S:lvl[&zLvl]_slots_expended]
    --&zLvlSlots|Slots [b][$SlotsExpended.Total][/b] of [b][$SlotsTotal.Total][/b] remaining
  --:SKIPCANTRIP|

  --&STbl|[t style=[&STblStyle1]][tr][td][&LvlDesc][/td][td][&zLvlSlots][/td][/tr][/t]
  --+|[&STbl]

  --:DisplayLoop|
    --?"[*R:spellname]" -eq NoRepeatingAttributeLoaded|Done

      --&zRIT|[r]
      --?"[*R:spellritual]" -inc "Yes"|ENDRIT
        --&zRIT|
      --:ENDRIT|

      --&zCON|[c]
      --?"[*R:spellconcentration]" -inc "concentration=1"|ENDCON
        --&zCON|
      --:ENDCON| 

      --?[&zLvl] -eq cantrip -or [*R:spellprepared] -eq 1|PREPARED

        --+|⬜️[rbutton]🔎[*R:spellname]::SPELLDETAILS;[*R:spellname]\repeating_spell-[&zLvl]\spellname[/rbutton] [i][*R:innate] [&zCON][&zRIT][/i]

      --^ENDIF-1|

      --:PREPARED|
        
        --+|✅[rbutton]🔎[*R:spellname]::SPELLDETAILS;[*R:spellname]\repeating_spell-[&zLvl]\spellname[/rbutton] [i][*R:innate] [&zCON][&zRIT][/i]

      --:ENDIF-1|

    --Rnext|

  -->DisplayLoop|  

  --:Done|
--<|

--:SPELLDETAILS|

  --/+ReEntryVal|[&reentryval]
  --~rArgs|string;split;\;[&reentryval]
  --/+Split|[&rArgs]: [&rArgs1], [&rArgs2], [&rArgs3]

  --Rfind|@{selected|character_id};[&rArgs1];[&rArgs2];[&rArgs3]
  --Rdump|

  --#title|[*R:spellname]  [*R:innate]
  --#leftsub|@{selected|character_name} (@{selected|caster_level})
  --#rightsub|[*R:spellschool] [*R:spelllevel]
  --#oddRowBackground|#eeeeee
  --#evenRowBackground|#ffffff

  --+Casting Time:|[*R:spellcastingtime]
  --+Range:|[*R:spellrange]
  --+Target:|[*R:spelltarget]

  --&zSCV|V
  --?"[*R:spellcomp_v]" -inc "v=1"|ENDSCV
    --&zSCV|
  --:ENDSCV|

  --&zSCS|S
  --?"[*R:spellcomp_s]" -inc "s=1"|ENDSCS
    --&zSCS|
  --:ENDSCS|

  --&zSCM|M
  --?"[*R:spellcomp_m]" -inc "m=1"|ENDSCM
    --&zSCM|
  --:ENDSCM|

  --+Components:|[&zSCV][&zSCS][&zSCM] [i]([*R:spellcomp_materials])[/i]
  
  --+Duration:|[*R:spellduration]
  
  --+|[*R:spelldescription]
  
  --~Len|string;length;[*R:spellathigherlevels]
  --?[$Len] -le 5|END_HV_CHECK
    --+|[b][i]At Higher Levels.[/i][/b][*R:spellathigherlevels]
  --:END_HV_CHECK|
  
  --+|[c][sheetbutton]💫 Cast Spell 🧙::@{selected|character_name}::[&rArgs2]_[*R:xxxActionIDxxxx]_spell[/sheetbutton][/c]
  
  --+|[c][b]Spell Save DC[/b] @{selected|spell_save_dc}[/c]

  --X|
--<|

--:PREPARESPELL|
  --#title|Prepare Spell Toggle
  --+NOT YET SUPPORTED|
  --X|
--<|

}}
