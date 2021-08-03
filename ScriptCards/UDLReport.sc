!scriptcards {{

  --/|Script Name : UDL Report
  --/|Version     : 4.0
  --/|Requires SC : 1.3.7+, 
  --/|Author      : Will M.

  --/|Description : Quick report showing Lighting Config for page and characters.

  --/|Dev Note    : Need to add new parameters recently added

  --#title|UDL Lighting Report
  --#leftsub|
  --#rightsub|
  --#oddRowBackground|#eeeeee
  --#evenRowBackground|#ffffff
  --#whisper|self
  
  --/|Loop through all of the tokens in "alltokens" 
  --~tokencnt|array;pagetokens;alltokens;@{selected|token_id}
  --~TokenId|array;getfirst;alltokens

  --?[&TokenId] -eq ArrayError|UDL_ENDLOOP

  --:UDL_LOOPCHECK|

    --&CharId|[*[&TokenId]:t-represents]

    --/I added a flag in my player character sheets called PlayerCharacter and set it to 1 for all player controlled objects (themselves and their npc companions)
    --?"[*[&TokenId]:playercharacter]" -eq 1|UDL_PC

    --/|Otherwise - Skip targets that are not on the token layer or that don't represent characters
    --?[*[&TokenId]:t-layer] -ne objects|UDL_CONTINUE
    --?"[*[&TokenId]:t-represents]" -ninc "-"|UDL_CONTINUE
    --?"[*[&TokenId]:npc]" -eq 1|UDL_CONTINUE

    --:UDL_PC|

    --&CharId|[*[&TokenId]:t-represents]

    --/|Default icons to red X
    --&Vis|&#x274C;
    --&NightVis|&#x274C;
    --&BrightLight|&#x274C;
    --&LowLight|&#x274C;

    --/|Update Emoji icons based on UDL settings
    --?"[*[&TokenId]:t-has_bright_light_vision]" -ne "true"|NO_BLV --&Vis|&#x1F453; --:NO_BLV|
    --?"[*[&TokenId]:t-has_night_vision]" -ne "true"|NO_NV --&NightVis|&#x1F319; --:NO_NV|
    --?"[*[&TokenId]:t-emits_bright_light]" -ne "true"|NO_BL --&BrightLight|&#x1F4A1; --:NO_BL|
    --?"[*[&TokenId]:t-emits_low_light]" -ne "true"|NO_LL --&LowLight|&#x1F56F; --:NO_LL|

    --+|[c][b] [*[&CharId]:character_name][/b] [/c]

  --+|[t style="width:100%"][tr][td style="width:50%;text-align:left"]Vision[/td]
        [td style="width:30%;text-align:center"][&Vis][/td][td style="width:10%;text-align:center"][/td][/tr]
      [tr][td style="width:50%;text-align:left"]Night Vision[/td]
        [td style="width:30%;text-align:center"][&NightVis][/td]
        [td style="width:20%;text-align:right"][*[&TokenId]:t-night_vision_distance] ft.[/td][/tr]
      [tr][td style="width:50%"][l]Emits Light[/l] [r][i]Bright[/i][/r][/td]
        [td style="width:30%;text-align:center"][&BrightLight][/td]
        [td style="width:20%;text-align:right"][*[&TokenId]:t-bright_light_distance] ft.[/td][/tr]  
      [tr][td style="width:50%;text-align:right"][i]Low[/i][/td]
        [td style="width:30%;text-align:center"][&LowLight][/td]
        [td style="width:20%;text-align:right"][*[&TokenId]:t-low_light_distance] ft.[/td][/tr][/t]

    --:UDL_CONTINUE|
    --~TokenId|array;getnext;alltokens
    --?[&TokenId] -ne ArrayError|UDL_LOOPCHECK
  --:UDL_ENDLOOP|
  --X|
}}