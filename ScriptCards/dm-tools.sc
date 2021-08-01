!scriptcard {{ 

  --/|Script Name : DMTools
  --/|Version     : 4.1
  --/|Requires SC : 1.4.0+, TokenMod, NoteLog, Chatsetattr, ping-token
  --/|Author      : Will M.

  --/|Description : A collection of utility functions against party characters

  --/|Updates     : 4.1 - Added pagetokens prefilter (pc) to speed up code; 
  --/|                    Cleaned up formatting
  --/|                    Changed TurnorderMacros name to CombatMacros 

  --:TOP|
  --#title|DM Tools
  --#titleCardBackground|#03038a
  --#titlefontface|Patrick Hand
  --/#oddRowBackground|#d8d8e6
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|#0905f2
  --#buttonbordercolor|#FFFFFF
  --#bodyFontSize|11px
  --#sourceToken|@{selected|token_id}
  --#hidecard|0    
  --#timezone|America/Chicago
  --#bodyFontSize|11px
  --#whisper|gm
  --#debug|0
  --Ssettings|DMTools
  --:SETTINGS_BOTTOM|
  --#reentrant|DMTools

  --/|>SECTION_HEADER|[&SendingPlayerID]
  
  --/| This is my collection of utility macros grouped together by functionality
  --/|  Comment out this section if you don't plan to utilze, or replace it with 

  --/|  your own macro utilities
  -->SECTION_HEADER|Macros
  --+|[c][button]&#x1F4CD;::~Mule|Page-Assets[/button]
         [button]&#x1F4CB;::~Mule|Menu-Char-Token-Info[/button]
         [button]&#x1F527;::~Mule|Menu-Utility-Macros[/button]
         [button]&#x2694;::~Mule|Menu-Combat-Macros[/button]
         [button]&#x1F3AD;::~Mule|Player-Macros[/button]
         [rbutton]&#x2699;::GLOBAL_NPC_SETTINGS[/rbutton]         
         [button]&#x1F310;::~Mule|DM-Links[/button]
         [button]&#x1F39A;::~Mule|NPC-Layers[/button]
         [button]&#x1F9DB;::~Mule|NPC-Tools[/button]
     [/c]

  --/| This is a collection of reports that aggregate party information
  -->SECTION_HEADER|Party Reports
  --+|[c][rbutton]&#x1F497;::PARTY_HEALTH_REPORT[/rbutton]
        [rbutton]&#x1F520;::PARTY_RESOURCES_REPORT[/rbutton]
        [rbutton]&#x1F4AC;::PARTY_LANG_REPORT[/rbutton]
        [rbutton]&#x1F4B0;::PARTY_FUNDS_REPORT[/rbutton]
        [rbutton]&#x1F453;::PARTY_UDL_REPORT[/rbutton]
        [/c]

  -->SECTION_HEADER|Character Reports

  --~tokencnt|array;pagetokens;alltokens;@{selected|token_id};pc
  --/|Loop through all of the tokens in "alltokens" 
  --~TokenId|array;getfirst;alltokens
  --&CharId|[*[&TokenId]:t-represents]
  --?[&TokenId] -eq ArrayError|ENDLOOP

  --&TblStyle1|"width:100%;text-align:left;padding:1px;border-spacing:1px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border: 0px dashed black;"
  --&tbl|[t style=[&TblStyle1]]

  --:LOOPCHECK|
    --&CharId|[*[&TokenId]:t-represents]
    --/+Debug|[*[&TokenId]:t-name]

    --/|Skip tokens that are not on the token layer - this is a good indicator that the token is not associated with the party
    --?[*[&TokenId]:t-layer] -ne objects|CONTINUE

    --/|Filter out MapNotes
    --?"[*[&TokenId]:t-bar1_value]" -eq "MapNote" |CONTINUE

   --:START|
    --&CharId|[*[&TokenId]:t-represents]
    -->SHORT_NAME|[*[&CharId]:character_name]

    --/| Create a row for each identified character.  Remove the [rbutton] syntax if you don't plan to use the PingToken API functionality
    --&tbl|+ 
        [tr][td style="width:30%;text-align:left;background-color:#FFFFFF"][b][img width=30 height=30][*[&TokenId]:t-imgsrc][/img][rbutton][&gSN]::FIND_TOKEN;[&TokenId][/rbutton][/b][/td]
          [td style="width:70%;text-align:right;background-color:#FFFFFF"]
            [rbutton]&#x1F4C3;::CHAR_DETAILS_REPORT;[&CharId]\[&TokenId]\[/rbutton]
            [rbutton]&#x1F3F9;::ATTACKS_REPORT;[&CharId]\[&TokenId]\[/rbutton]
            [rbutton]&#x1F393;::FEATURES_REPORT;[&CharId]\[&TokenId]\[/rbutton]
            [rbutton]&#x1F4AB;::SPELLBOOK_REPORT;[&CharId]\[&TokenId]\[/rbutton]
            [rbutton]&#x1F392;::INV_REPORT;[&CharId]\[&TokenId]\[/rbutton]
            [rbutton]&#x1F520;::RESOURCES_REPORT;[&CharId]\[&TokenId]\[/rbutton]
        [/td][/tr]
        [tr][td colspan=2 style="border-bottom: 1px solid black;width:100%;text-align:left;background-color:#FFFFFF"][rbutton][+]::ADD_QUICK_NOTE;[&CharId][/rbutton][i][*[&CharId]:QuickNote][/i][/td]

    --:CONTINUE|
    --~TokenId|array;getnext;alltokens
    --?[&TokenId] -ne ArrayError|LOOPCHECK
  --:ENDLOOP|

  --&tbl|+ [/t] 
  --+|[&tbl]

  --~cstDate|system;date;getdatetime
  --+Date/Time:|[&cstDate] [b]Settings:[/b][rbutton]Load::LOAD_SAVED_SETTINGS[/rbutton] [rbutton]List::SHOW_SAVED_SETTINGS[/rbutton]

--X|

--/|==================  Party Lighting Report =======================
--:PARTY_UDL_REPORT| (UDL_)
  --#title|UDL Lighting Report
  --#leftsub|
  --#rightsub|
  --#oddRowBackground|#ffffff
  --#buttonbackground|#ffffff
  --#evenRowBackground|#d8d8e6
  --#sourcetoken|@{selected|token_id}
  --#activepage|[*S:t-pageid]
  --#hidecard|0

  --&UDLEnabled|&#x274C;
  --&DLModeEnabled|&#x274C;
  --&ExpModeEnabled|&#x274C;
  --&LUonDrop|&#x274C;
  --&ForceLOS|&#x274C;
  --&RestrictMvmt|&#x274C;
  --&GlobalLight|&#x274C;
  --&ForceLightRefresh|&#x274C;
  --&LLFoW|&#x274C;
  --&LLLigting|&#x274C;

  --?"[*P:dynamic_lighting_enabled]" -ne "true"|UDL1_END --&UDLEnabled|&#x1F4A1; --:UDL1_END|
  --?"[*P:daylight_mode_enabled]" -ne "true"|UDL2_END --&DLModeEnabled|&#x1F4A1; --:UDL2_END|
  --?"[*P:explorer_mode]" -ne "basic"|UDL3_END --&ExpModeEnabled|&#x1F4A1; --:UDL3_END|
  --?"[*P:lightupdatedrop]" -ne "true"|UDL4_END --&LUonDrop|&#x1F4A1; --:UDL4_END|
  --?"[*P:lightenforcelos]" -ne "true"|UDL5_END --&ForceLOS|&#x1F4A1; --:UDL5_END|
  --?"[*P:lightrestrictmove]" -ne "true"|UDL6_END --&RestrictMvmt|&#x1F4A1; --:UDL6_END|
  --?"[*P:lightglobalillum]" -ne "true"|UDL7_END --&GlobalLight|&#x1F4A1; --:UDL7_END|
  --?"[*P:force_lighting_refresh]" -ne "true"|UDL8_END --&ForceLightRefresh|&#x1F4A1; --:UDL8_END|
  --?"[*P:showdarkness]" -ne "true"|UDL9_END --&LLFoW|&#x1F4A1; --:UDL9_END|
  --?"[*P:showlighting]" -ne "true"|UDLA_END --&LLLigting|&#x1F4A1; --:UDLA_END|

  --&Tbl|[t style="width:100%;"]
  --&Tbl|+ [tr][td style="text-align:center"][b][*P:name][/b][/td][/tr]
  --&Tbl|+ [tr][td style="text-align:center"][i](W:[*P:width] x H:[*P:height] Scale Units:[*P:scale_units] Scale Nbr:[*P:scale_number])[/i][/td][/tr][/t]
  --+|[&Tbl]

  --&Tbl|[t style="width:100%"]
  --&Tbl|+ [tr][td]UDL[/td][td][&UDLEnabled][/td][td]Daylight Mode[/td][td][&DLModeEnabled][/td][/tr]
  --&Tbl|+ [tr][td]Explore[/td][td][&ExpModeEnabled][/td][td]Drop Mode[/td][td][&LUonDrop][/td][/tr]
  --&Tbl|+ [tr][td]Restrict Mvmt[/td][td][&RestrictMvmt][/td][td]GM Opacity[/td][td][*P:fog_opacity][/td][/tr]
  --&Tbl|+ [tr][td]Force LOS[/td][td][&ForceLOS][/td][td]Force Refresh[/td][td][&ForceLightRefresh][/td][/tr]
  --&Tbl|+ [tr][td]Global Illum[/td][td][&GlobalLight][/td][td][/td][td][/td][/tr]
  --&Tbl|+ [tr][td]FoW (LL)[/td][td][&LLFoW][/td][td]Legacy Lighting[/td][td][&LLLigting][/td][/tr]
  --&Tbl|+ [/t]
  --+|[&Tbl]

  --/+/UDL Enabled?| [*P:dynamic_lighting_enabled]
  --/+/Daylight Mode?| [*P:daylight_mode_enabled]
  --/+/Explorer Mode?| [*P:explorer_mode]
  --/+/Update on Drop?|[*P:lightupdatedrop]
  --/+/Force LOS?|[*P:lightenforcelos]
  --/+/Restrict Movement?|[*P:lightrestrictmove]
  --/+/Fog(LL)/GM Darkness(UDL) Opacity|[*P:fog_opacity] 
  --/+/Global Light?|[*P:lightglobalillum]
  --/+/Force Light Refresh?|[*P:force_lighting_refresh]
  
  --/|Loop through all of the tokens in "alltokens" 
  --~tokencnt|array;pagetokens;alltokens;@{selected|token_id}
  --~TokenId|array;getfirst;alltokens

  --?[&TokenId] -eq ArrayError|UDL_ENDLOOP

  --:UDL_LOOPCHECK|

  --&CharId|[*[&TokenId]:t-represents]

  --/|I added a flag in my player character sheets called PlayerCharacter and set it to 1 for all player controlled objects (themselves and their npc companions)
  --?"[*[&TokenId]:playercharacter]" -eq 1|UDL_PC

  --/|Otherwise - Skip targets that are not on the token layer or that don't represent characters
  --?[*[&TokenId]:t-layer] -ne objects|UDL_CONTINUE
  --?"[*[&TokenId]:t-represents]" -ninc "-"|UDL_CONTINUE
  --?"[*[&TokenId]:npc]" -eq 1|UDL_CONTINUE

  --:UDL_PC|

  --&CharId|[*[&TokenId]:t-represents]

  --&NVE|None --&NVESymbol|&#x274C;

  --/|null, nocturnal or Dimming 
  --?[*[&TokenId]:t-night_vision_effect] -inc Dimming|UDL_DIMMING_EFFECT
    --?[*[&TokenId]:t-night_vision_effect] -ne null|UDL_SETUP_DEF
      --/|Nocturnal (1F316)
      --&NVESymbol|&#x1F316;
      --^UDL_SETUP_DEF|
  --:UDL_DIMMING_EFFECT|1F505

    --/| Dimming Effect format (Dimming_0.5)  where 0.5 diming start / represents vision dist
    --~DStartPct|string;after;Dimming_;[*[&TokenId]:t-night_vision_effect]
    --=DStart|[&DStartPct] * [*[&TokenId]:t-night_vision_distance]
    --&NVE|Dim ([$DStart.Total])
    --*UDL_DIM|Start:[$DStart] Pct:[&DStartPct] Dist:[*[&TokenId]:t-night_vision_distance]
    --&NVESymbol|&#x1F505;
    --^UDL_SETUP_DEF|

  --:UDL_SETUP_DEF|
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

  --+|[c][b][rbutton][*[&CharId]:character_name]::SET_UDL;[&CharId]\[&TokenId][/rbutton][/b][/c]

  --+|[t style="width:100%"]
      [tr][td style="width:50%;text-align:left"]Vision[/td]
        [td style="width:30%;text-align:center"][&Vis][/td]
        [td style="width:20%;text-align:center"][/td][/tr]
      [tr][td style="width:50%;text-align:left"]Night Vision[/td]
        [td style="width:30%;text-align:center"][&NightVis][/td]
        [td style="width:20%;text-align:right"][*[&TokenId]:t-night_vision_distance] ft.[/td][/tr]
      [tr][td style="width:50%;text-align:left"]Night Vision Effect[/td]
        [td style="width:30%;text-align:center"][&NVESymbol][/td]
        [td style="width:20%;text-align:right"][&NVE][/td][/tr]
      [tr][td style="width:50%"][l]Emits Light[/l] [r][i]Bright[/i][/r][/td]
        [td style="width:30%;text-align:center"][&BrightLight][/td]
        [td style="width:20%;text-align:right"][*[&TokenId]:t-bright_light_distance] ft.[/td][/tr]  
      [tr][td style="width:50%;text-align:right"][i]Low[/i][/td]
        [td style="width:30%;text-align:center"][&LowLight][/td]
        [td style="width:20%;text-align:right"][*[&TokenId]:t-low_light_distance] ft.[/td][/tr][/t]


  --/|Can't get to the Player object yet - Controlled by returns PlayerIds, not Names
  --/&CB|[*[&CharId]:controlledby]
  --/~CBAry|array;split;,;&CB
  --/+|[i]Controlled By:[&CBAry1] [*[&CBAry1]:name] / [*[&CBAry1]:displayname] / [*[&CBAry1]:_displayname][/i]

  --:UDL_CONTINUE|
  --~TokenId|array;getnext;alltokens
  --?[&TokenId] -ne ArrayError|UDL_LOOPCHECK

--:UDL_ENDLOOP|
-->FOOTER_BUTTONS_PARTY|
--X|

--:UDL_PAGE_SETTINGS| Future addition
--<|

--:UDL_PLAYER_CONTROLLEDBY_NAMES| Future addition

--<|

--/|================== SET_UDL function =======================
--:SET_UDL|CharId; TokenID; 
  --#hidecard|0
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]
  --#sourceToken|[&TokenId]
  --#title|Set Updated Dynamic Lighting
  --#leftsub|[*[&TokenId]:t-name]

  --#oddRowBackground|#ffffff
  --#evenRowBackground|#ffffff


  --&tStyle|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;"
  --&trStyle1|style="border:0px dashed black;"
  --&tdStyle1|style="width:30%;text-align:left;background-color:#FFFFFF;font-size:80%"
  --&tdStyle2|style="width:70%;text-align:left;background-color:#FFFFFF;font-size:80%"

  --+|[t [&tStyle]]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Reset::SET_TOKEN_UDL;[&TokenId]\1[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Normal::SET_TOKEN_UDL;[&TokenId]\2[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Dark Vision (60')::SET_TOKEN_UDL;[&TokenId]\3[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Dark Vision (90')::SET_TOKEN_UDL;[&TokenId]\4[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Dark Vision (120')::SET_TOKEN_UDL;[&TokenId]\5[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Candle (Carried)::SET_TOKEN_UDL;[&TokenId]\6[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Candle (Stationary)::SET_TOKEN_UDL;[&TokenId]\7[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Torch/Light (Carried)::SET_TOKEN_UDL;[&TokenId]\8[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Torch/Light (Stationary)::SET_TOKEN_UDL;[&TokenId]\9[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Hooded Lantern (Carried)::SET_TOKEN_UDL;[&TokenId]\10[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Hooded Lantern (Covered)::SET_TOKEN_UDL;[&TokenId]\11[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Hooded Lantern (Stationary)::SET_TOKEN_UDL;[&TokenId]\12[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Bullseye Lantern::SET_TOKEN_UDL;[&TokenId]\13[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Flashlight::SET_TOKEN_UDL;[&TokenId]\14[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Camp Fire::SET_TOKEN_UDL;[&TokenId]\15[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Bon Fire::SET_TOKEN_UDL;[&TokenId]\16[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Fez (Soft Light)::SET_TOKEN_UDL;[&TokenId]\17[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Street Light::SET_TOKEN_UDL;[&TokenId]\18[/rbutton][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]][rbutton]Spot Light::SET_TOKEN_UDL;[&TokenId]\19[/rbutton][/td][/tr]
      [/t]
-->FOOTER_BUTTONS_PARTY|      
--X|
--<|
--:SET_TOKEN_UDL|TokenId; LightingID
  --#hidecard|1
  --~Arg|string;split;\;[&reentryval]
  --&TokenId|[&Arg1]
  --&LId|[&Arg2]
  --#title|Set Updated Dynamic Lighting
  --#leftsub|[*[&TokenId]:t-name]

  --+UDL Debug Args:|[*[&TokenId]:t-name]([&TokenId]) - [&LId] 

  --C[&LId]|1:STU_1|2:STU_2|3:STU_3|4:STU_4|5:STU_5|6:STU_6|7:STU_7|8:STU_8|9:STU_9|10:STU_10|11:STU_11|12:STU_12|13:STU_13|14:STU_14|15:STU_15|16:STU_16|17:STU_17|18:STU_18|19:STU_19
    --+Err:|Ivalid Lighting Entry - ([&LId])
    --+UDL Debug Err| [&LId]
    --X|

    --:STU_1|Reset 
      --+UDL Debug Case| [&LId] - STU_1
      --&Vision|_off has_bright_light_visionhas_night_vision emits_bright_light emits_low_light has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _set night_vision_distance#0 bright_light_distance#0 low_light_distance#0 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0 night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_2|Normal 
      --+UDL Debug Case| [&LId] - STU_2
      --&Vision|_off has_night_vision emits_bright_light emits_low_light has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on has_bright_light_vision  _set night_vision_distance#0 bright_light_distance#0 low_light_distance#0 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0 night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_3|Dark Vision (60') 
      --+UDL Debug Case| [&LId] - STU_3
      --&Vision|_off emits_bright_light emits_low_light has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on has_bright_light_vision has_night_vision  _set night_vision_distance#60 bright_light_distance#0 low_light_distance#0 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0 night_vision_effect#Nocturnal light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_4|Dark Vision (90')
      --+UDL Debug Case| [&LId] - STU_4
      --&Vision|_off emits_bright_light emits_low_light has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on has_bright_light_vision has_night_vision  _set night_vision_distance#90 bright_light_distance#0 low_light_distance#0 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0 night_vision_effect#Nocturnal light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_5|Dark Vision (120')
      --+UDL Debug Case| [&LId] - STU_5
      --&Vision|_off emits_bright_light emits_low_light has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on has_bright_light_vision has_night_vision  _set night_vision_distance#120 bright_light_distance#0 low_light_distance#0 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0 night_vision_effect#Nocturnal light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_6|Candle (Carried)
      --+UDL Debug Case| [&LId] - STU_6
      --&Vision|_off has_night_vision has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on has_bright_light_vision emits_bright_light emits_low_light  _set night_vision_distance#0 bright_light_distance#5 low_light_distance#10 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0 night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|      
   
    --:STU_7|Candle (Stationary)
      --+UDL Debug Case| [&LId] - STU_7
      --&Vision|_off has_bright_light_vision has_night_vision has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on emits_bright_light emits_low_light  _set night_vision_distance#0 bright_light_distance#5 low_light_distance#10 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0 night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_8|Torch/Light (Carried)
      --+UDL Debug Case| [&LId] - STU_8
      --&Vision|_off has_night_vision has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on has_bright_light_vision emits_bright_light emits_low_light  _set night_vision_distance#0 bright_light_distance#20 low_light_distance#20 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0 night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_9|Torch/Light (Stationary)
      --+UDL Debug Case| [&LId] - STU_9
      --&Vision|_off has_bright_light_vision has_night_vision has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on emits_bright_light emits_low_light  _set night_vision_distance#0 bright_light_distance#20 low_light_distance#20 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0 night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|  

    --:STU_10|Hooded Lantern (Carried)
      --+UDL Debug Case| [&LId] - STU_10
      --&Vision|_off has_night_vision has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on has_bright_light_vision emits_bright_light emits_low_light  _set night_vision_distance#0 bright_light_distance#30 low_light_distance#30 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0 night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_11|Hooded Lantern (Covered) 
      --+UDL Debug Case| [&LId] - STU_11
      --&Vision|_off has_bright_light_vision has_night_vision has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on emits_bright_light emits_low_light  _set night_vision_distance#0 bright_light_distance#0 low_light_distance#5 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0 night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_12|Hooded Lantern (Stationary) 
      --+UDL Debug Case| [&LId] - STU_12
      --&Vision|_off has_bright_light_vision has_night_vision has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on emits_bright_light emits_low_light  _set night_vision_distance#0 bright_light_distance#30 low_light_distance#30 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0 night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_13|Bullseye Lantern
      --+UDL Debug Case| [&LId] - STU_13
      --&Vision|_off has_night_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on has_bright_light_vision emits_bright_light emits_low_light has_limit_field_of_vision  _set night_vision_distance#0 bright_light_distance#60 low_light_distance#60 limit_field_of_vision_center#60 limit_field_of_vision_total#60 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0  night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_14|Flashlight
      --+UDL Debug Case| [&LId] - STU_14
      --&Vision|_off has_night_vision has_limit_field_of_night_vision has_directional_dim_light  _on has_bright_light_vision emits_bright_light emits_low_light has_limit_field_of_vision has_directional_bright_light  _set night_vision_distance#0 bright_light_distance#100 low_light_distance#100 limit_field_of_vision_center#60 limit_field_of_vision_total#20 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0  night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_15|Camp Fire
      --+UDL Debug Case| [&LId] - STU_15
      --&Vision|_off has_bright_light_vision has_night_vision has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on emits_bright_light emits_low_light  _set night_vision_distance#0 bright_light_distance#70 low_light_distance#70 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0  night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_16|Bon Fire
      --+UDL Debug Case| [&LId] - STU_16
      --&Vision|_off has_bright_light_vision has_night_vision has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on emits_bright_light emits_low_light  _set night_vision_distance#0 bright_light_distance#100 low_light_distance#100 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0  night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_17|Fez (Soft Light)
      --+UDL Debug Case| [&LId] - STU_17
      --&Vision|_off has_bright_light_vision has_night_vision emits_bright_light has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on emits_low_light  _set night_vision_distance#0 bright_light_distance#0 low_light_distance#20 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0  night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_18|Street Light
      --+UDL Debug Case| [&LId] - STU_18
      --&Vision|_off has_bright_light_vision has_limit_field_of_vision has_limit_field_of_night_vision has_directional_bright_light has_directional_dim_light  _on emits_bright_light emits_low_light  _set night_vision_distance#0 bright_light_distance#50 low_light_distance#25 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#0 directional_bright_light_total#0 directional_dim_light_center#0 directional_dim_light_total#0  night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

    --:STU_19|Spot Light
      --+UDL Debug Case| [&LId] - STU_19
      --&Vision|_off has_night_vision has_limit_field_of_vision has_limit_field_of_night_vision  _on has_bright_light_vision emits_bright_light emits_low_light has_directional_bright_light has_directional_dim_light  _set night_vision_distance#0 bright_light_distance#30 low_light_distance#30 limit_field_of_vision_center#0 limit_field_of_vision_total#0 limit_field_of_night_vision_center#0 limit_field_of_night_vision_total#0 directional_bright_light_center#90 directional_bright_light_total#20 directional_dim_light_center#90 directional_dim_light_total#30  night_vision_effect#None light_sensitivity_multiplier#100
      --^APPLY_VISION|

  --:APPLY_VISION|

  --+UDL ApplyVision| [&Vision]
  --@token-mod|[&Vision] _ids [&TokenId] _ignore-selected

-->FOOTER_BUTTONS_PARTY|
--X|
--<|


--/|==================  Party Health Report =======================
--:PARTY_HEALTH_REPORT| (PH_)
  --#hidecard|0
    
  --#title|Party Health
  --#leftsub|
  --#rightsub|
  --#oddRowBackground|#d8d8e6
  --#evenRowBackground|#ffffff

  --~tokencnt|array;pagetokens;alltokens;@{selected|token_id}

  --/|Loop through all of the tokens in "alltokens" 
  --~TokenId|array;getfirst;alltokens

  --&CID|[*[&TokenId]:t-represents]
  --?[&TokenId] -eq ArrayError|PH_ENDLOOP

  --&tbl|[t cellpadding="15" border="1" style="width:100%"][tr][td style="width:20%;text-align:left"][b]Char[/b][/td][td style="width:15%;text-align:center"][b]AC[/b][/td][td style="width:15%;text-align:center"][b]PP[/b][/td][td style="width:30%;text-align:center"][b]HP/Max[/b][/td][td style="width:20%;text-align:center"][b]Mad Lvl[/b][/td] [/tr]

  --:PH_LOOPCHECK|
    --&CID|[*[&TokenId]:t-represents]

    --/|Skip targets that are not on the token layer or that don't represent creatures
    --?[*[&TokenId]:t-layer] -ne objects|PH_CONTINUE
    --?"[*[&TokenId]:t-represents]" -ninc "-"|PH_CONTINUE
    --?"[*[&TokenId]:npc]" -eq 1|PH_CONTINUE

    --&CID|[*[&TokenId]:t-represents]
    -->SHORT_NAME|[*[&CID]:character_name]

    --&tbl|+ [tr][td style="width:20%;text-align:left"][b][&gSN][/b][/td][td style="width:15%;text-align:center"][*[&CID]:ac][/td][td style="width:15%;text-align:center"][*[&CID]:passive_wisdom][/td][td style="width:30%;text-align:center"][*[&CID]:hp] / [*[&CID]:hp^][/td][td style="width:20%;text-align:center"][*[&CID]:madness_level][/td] [/tr]

    --:PH_CONTINUE|
    --~TokenId|array;getnext;alltokens
    --?[&TokenId] -ne ArrayError|PH_LOOPCHECK
  --:PH_ENDLOOP|

  --&tbl|[&tbl] [/t]
  --+|[&tbl]

-->FOOTER_BUTTONS_PARTY|
--X|
--<|

--/|==================  Party Languages Report =======================
--:PARTY_LANG_REPORT| (PL_)
  --#hidecard|0
    
  --#title|Party Languages
  --#leftsub|
  --#rightsub|
  --#oddRowBackground|#d8d8e6
  --#evenRowBackground|#ffffff

  --&tbl|
  --~tokencnt|array;pagetokens;alltokens;@{selected|token_id}

  --/|Loop through all of the tokens in "alltokens" 
  --~TokenId|array;getfirst;alltokens

  --&CID|[*[&TokenId]:t-represents]
  --?[&TokenId] -eq ArrayError|PL_ENDLOOP

  --:PL_LOOPCHECK|
    --&CID|[*[&TokenId]:t-represents]

    --/|Skip targets that are not on the token layer or that don't represent creatures
    --?[*[&TokenId]:t-layer] -ne objects|PL_CONTINUE
    --?"[*[&TokenId]:t-represents]" -ninc "-"|PL_CONTINUE
    --?"[*[&TokenId]:npc]" -eq 1|PL_CONTINUE

    --&CID|[*[&TokenId]:t-represents]
    -->SHORT_NAME|[*[&CID]:character_name]

    --Rfirst|[&CID];repeating_proficiencies
    --:PL_LANG_LOOP|
      --?"[*R:name]" -eq "NoRepeatingAttributeLoaded"|PL_DONE_LANG
        --/|*Language|[*R:name]
        --?"[*R:prof_type]" -ne "LANGUAGE"|PL_LANG_NEXT
          --&tbl|+ [tr][td style="width:40%;text-align:left"][b][&gSN][/b][/td]
                       [td style="width:60%;text-align:center"][*R:name][/td] [/tr]
          --&gSN|
        --:PL_LANG_NEXT|
        --Rnext|
    --^PL_LANG_LOOP|
    --:PL_DONE_LANG|

    --:PL_CONTINUE|
    --~TokenId|array;getnext;alltokens
    --?[&TokenId] -ne ArrayError|PL_LOOPCHECK
  --:PL_ENDLOOP|

  --&tbl|[t cellpadding="15" border="1" style="width:100%"][&tbl] [/t]
  --+|[&tbl]

-->FOOTER_BUTTONS_PARTY|
--X|
--<|

--/|==================  Party Funds Report =======================
--:PARTY_FUNDS_REPORT| (PF_)
  --#hidecard|0
  --#title|Party Funds
  --#leftsub|
  --#rightsub|
  --#oddRowBackground|#d8d8e6
  --#evenRowBackground|#ffffff

  --~tokencnt|array;pagetokens;alltokens;@{selected|token_id}

  --/|Loop through all of the tokens in "alltokens" 
  --~TokenId|array;getfirst;alltokens

  --&CharId|[*[&TokenId]:t-represents]

  --=GTtlGP|0 --=TtlCP|0 --=TtlSP|0 --=TtlEP|0 --=TtlGP|0 --=TtlPP|0 

  --?[&TokenId] -eq ArrayError|PF_ENDLOOP

  --&tbl|[t cellpadding="15" border="1" style="width:100%"][tr][td style="width:12%;text-align:left"][b]Name[/b][/td][td style="width:14%;text-align:right"][b]cp[/b][/td][td style="width:14%";text-align:right"][r][b]sp[/b][/r][/td][td style="width:14%;text-align:right"][b]ep[/b][/td][td style="width:14%;text-align:right"][b]gp[/b][/td][td style="width:14%;text-align:right"][b]pp[/b][/td][td style="width:18%;text-align:right"][b]Ttl gp[/b][/td][/tr]

  --:PF_LOOPCHECK|
    --&CharId|[*[&TokenId]:t-represents]

    --/|Skip targets that are not on the token layer or that don't represent creatures
    --?[*[&TokenId]:t-layer] -ne objects|PF_CONTINUE
    --?"[*[&TokenId]:t-represents]" -ninc "-"|PF_CONTINUE
    --?"[*[&TokenId]:npc]" -eq 1|PF_CONTINUE

    --&CharId|[*[&TokenId]:t-represents]
    -->SHORT_NAME|[*[&CharId]:character_name]

  --=STtlGP|[*[&CharId]:cp] / 100 
  --=STtlGP|[*[&CharId]:sp] / 10 + [$STtlGP] 
  --=STtlGP|[*[&CharId]:ep] / 2 + [$STtlGP]
  --=STtlGP|[*[&CharId]:gp] + [$STtlGP]
  --=STtlGP|[*[&CharId]:pp]*10 + [$STtlGP]
  --~STtlGP|math;round;[$STtlGP]
  --=GTtlGP|[$GTtlGP] + [$STtlGP]

  --=TtlCP|[*[&CharId]:cp] + [$TtlCP]
  --=TtlSP|[*[&CharId]:sp] + [$TtlSP]
  --=TtlEP|[*[&CharId]:ep] + [$TtlEP]
  --=TtlGP|[*[&CharId]:gp] + [$TtlGP]
  --=TtlPP|[*[&CharId]:pp] + [$TtlPP]

  --&tbl|+ [tr][td style="width:12%;text-align:left"][&gSN][/td][td style="width:14%;text-align:right"][*[&CharId]:cp][/td][td style="width:14%;text-align:right"][*[&CharId]:sp][/td][td style="width:14%;text-align:right"][*[&CharId]:ep][/td][td style="width:14%;text-align:right"][*[&CharId]:gp][/td][td style="width:14%;text-align:right"][*[&CharId]:pp][/td][td style="width:18%;text-align:right"][b][$STtlGP.Total][/b][/td][/tr]

    --:PF_CONTINUE|
    --~TokenId|array;getnext;alltokens
    --?[&TokenId] -ne ArrayError|PF_LOOPCHECK
  --:PF_ENDLOOP|

  --&tbl|+ [tr][td style="width:12%;text-align:right"][b]Total:[/b][/td][td style="width:14%;text-align:right"][b][$TtlCP.Total][/b][/td][td style="width:14%;text-align:right"][b][$TtlSP.Total][/b][/td][td style="width:14%;text-align:right"][b][$TtlEP.Total][/b][/td][td style="width:14%;text-align:right"][b][$TtlGP.Total][/b][/td][td style="width:14%;text-align:right"][b][$TtlPP.Total][/b][/td][td style="width:18%;text-align:right"][b][$GTtlGP.Total][/b][/td][/tr]

  --&tbl|[&tbl] [/t]
  --+|[&tbl]

-->FOOTER_BUTTONS_PARTY| Add Footer Buttons
  --X|
--<|

--/|==================  Party Resources Report =======================
--:PARTY_RESOURCES_REPORT| (PR_)
  --#hidecard|0
  --#title|Party Resources
  --#leftsub|
  --#rightsub|
  --#oddRowBackground|#d8d8e6
  --#evenRowBackground|#ffffff

  --~tokencnt|array;pagetokens;alltokens;@{selected|token_id}

  --/|Loop through all of the tokens in "alltokens" 
  --~TokenId|array;getfirst;alltokens
  --&CharId|[*[&TokenId]:t-represents]

  --?[&TokenId] -eq ArrayError|PR_ENDLOOP

  --&tbl|[t cellpadding="15" border="1" style="width:100%"][tr][td style="width:15%;text-align:left"][b]Char[/b][/td][td style="width:45%;text-align:left"][b]Resource[/b][/td][td style="width:20%;text-align:center"][b]Avail[/b][/td][td style="width:20%;text-align:center"][b]Total[/b][/td][/tr]

  --:PR_LOOPCHECK|
    --&CharId|[*[&TokenId]:t-represents]

    --/|Skip targets that are not on the token layer or that don't represent creatures
    --?[*[&TokenId]:t-layer] -ne objects|PR_CONTINUE
    --?"[*[&TokenId]:t-represents]" -ninc "-"|PR_CONTINUE
    --?"[*[&TokenId]:npc]" -eq 1|PR_CONTINUE

    --&CharId|[*[&TokenId]:t-represents]
    -->SHORT_NAME|[*[&CharId]:character_name]

    --&tbl|+ [tr][td style="width:15%;text-align:left"][&gSN][/td][td style="width:45%;text-align:left"][*[&CharId]:class_resource_name][/td][td style="width:20%;text-align:center"][*[&CharId]:class_resource][/td][td style="width:20%;text-align:center"][*[&CharId]:class_resource^][/td][/tr]

    --&tbl|+ [tr][td style="width:15%;text-align:left"][/td][td style="width:45%;text-align:left"][*[&CharId]:other_resource_name][/td][td style="width:20%;text-align:center"][*[&CharId]:other_resource][/td][td style="width:20%;text-align:center"][*[&CharId]:other_resource^][/td][/tr]
    
    --:PR_CONTINUE|
    --~TokenId|array;getnext;alltokens
    --?[&TokenId] -ne ArrayError|PR_LOOPCHECK
  --:PR_ENDLOOP|

  --&tbl|[&tbl] [/t]
  --+|[&tbl]

-->FOOTER_BUTTONS_PARTY| Add Footer Buttons
  --X|

--<|

--/|==================  Character Details Report =======================
--:CHAR_DETAILS_REPORT| CDR_
  
  --#hidecard|0
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]
  --#sourceToken|[&TokenId]
  --#title|[*[&TokenId]:t-name]
  --#rightsub|[*[&CharId]:race]

  --#oddRowBackground|#ffffff
  --#evenRowBackground|#ffffff


  --&t|[*[&CharId]:class]-[*[&CharId]:base_level]
  --?"[*[&CharId]:multiclass1_flag]" -eq "0"|CDR_DONE1 --&t|+ /[*[&CharId]:multiclass1] - [*[&CharId]:multiclass1_lvl]
  --?"[*[&CharId]:multiclass2_flag]" -eq "0"|CDR_DONE1 --&t|+ /[*[&CharId]:multiclass2] - [*[&CharId]:multiclass2_lvl] 
  --?"[*[&CharId]:multiclass3_flag]" -eq "0"|CDR_DONE1 --&t|+ /[*[&CharId]:multiclass3] - [*[&CharId]:multiclass3_lvl]
  --:CDR_DONE1| 
  --#leftsub|[&t]

  -->ROLL20_CHARACTERSHEET_LINK|[&CharId];Roll20
  --+CharId:|[&CharId] [r][&ROLL20_CHARACTERSHEET_BTN]&nbsp;&nbsp;[/r]
  --+TokenId:|[&TokenId]

  --?[*[&CharId]:strength_save_prof] -ne 0|&StrTitle;Str+|&StrTitle;Str
  --?[*[&CharId]:dexterity_save_prof] -ne 0|&DexTitle;Dex+|&DexTitle;Dex
  --?[*[&CharId]:constitution_save_prof] -ne 0|&ConTitle;Con+|&ConTitle;Con
  --?[*[&CharId]:intelligence_save_prof] -ne 0|&IntTitle;Int+|&IntTitle;Int
  --?[*[&CharId]:wisdom_save_prof] -ne 0|&WisTitle;Wis+|&WisTitle;Wis
  --?[*[&CharId]:charisma_save_prof] -ne 0|&ChaTitle;Ch2+|&ChaTitle;Cha

  --+|[t style="border:2px solid black;width:100%"][tr style="background-color:#d8d8e6"][td style="width:15%;text-align:center"][b][&StrTitle][/b][/td] [td style="width:15%;text-align:center"][b][&DexTitle][/b][/td] 
        [td style="width:15%;text-align:center"][b][&ConTitle][/b][/td][td style="width:15%;text-align:center"][b][&IntTitle][/b][/td] [td style="width:15%;text-align:center"][b][&WisTitle][/b][/td][td style="width:15%;text-align:center"][b][&ChaTitle][/b][/td][/tr]
      [tr style="border:1px solid black"]
        [td style="width:15%;text-align:center"][*[&CharId]:strength] ([*[&CharId]:strength_mod])[/td] [td style="width:15%;text-align:center"][*[&CharId]:dexterity] ([*[&CharId]:dexterity_mod])[/td] 
        [td style="width:15%;text-align:center"][*[&CharId]:constitution] ([*[&CharId]:constitution_mod])[/td][td style="width:15%;text-align:center"][*[&CharId]:intelligence] ([*[&CharId]:intelligence_mod])[/td]
        [td style="width:15%;text-align:center"][*[&CharId]:wisdom] ([*[&CharId]:wisdom_mod])[/td][td style="width:15%;text-align:center"][*[&CharId]:charisma] ([*[&CharId]:charisma_mod])[/td][/tr]
      [tr style="border:1px solid black"]
        [td style="width:15%;text-align:center"][*[&CharId]:strength_save_bonus][/td] [td style="width:15%;text-align:center"][*[&CharId]:dexterity_save_bonus][/td] 
        [td style="width:15%;text-align:center"][*[&CharId]:constitution_save_bonus][/td][td style="width:15%;text-align:center"][*[&CharId]:intelligence_save_bonus][/td]
        [td style="width:15%;text-align:center"][*[&CharId]:wisdom_save_bonus][/td][td style="width:15%;text-align:center"][*[&CharId]:charisma_save_bonus][/td][/tr][/t]
      
  --+|[t style="border:0px solid black;width:96%"]
        [tr][td style="width:25%;text-align:right"][b]AC: [/b] [/td][td style="width:20%;text-align:center"][*[&CharId]:ac][/td]
            [td style="width:25%;text-align:right"][b]HP: [/b] [/td][td style="width:20%;text-align:center"][*[&CharId]:hp]/[*[&CharId]:hp^][/td][/tr]
        [tr][td style="width:25%;text-align:right"][b]Speed: [/b] [/td][td style="width:20%;text-align:center"][*[&CharId]:speed][/td]
            [td style="width:25%;text-align:right"][b]PP: [/b] [/td][td style="width:20%;text-align:center"][*[&CharId]:passive_wisdom][/td][/tr]
        [tr][td style="width:25%;text-align:right"][b]Init B.: [/b] [/td][td style="width:20%;text-align:center"][*[&CharId]:initiative_bonus][/td]
            [td style="width:25%;text-align:right"][b]Prof B.: [/b] [/td][td style="width:20%;text-align:center"][*[&CharId]:pb][/td][/tr][/t]


  --&tStyle|style="border:1px solid black;width:100%"
  --&trStyle1|style="background-color:#BADFBB"
  --&trStyle2|style="background-color:#BADFBB"
  --&tdStyle1|style="width:35%;text-align:left;font-weight:bold"
  --&tdStyle2|style="width:15%;text-align:center;font-weight:bold"
  --&tdStyle3|colspan=4 style="width:100%;text-align:center"

  --+|[t [&tStyle]]
        [tr][td [&tdStyle1]]Acrobatics: [/td][td [&tdStyle2]][*[&CharId]:acrobatics_bonus][/td][td [&tdStyle1]]Animal Hand: [/td][td [&tdStyle2]][*[&CharId]:animal_handling_bonus][/td][/tr]
        [tr][td [&tdStyle1]]Arcana: [/td][td [&tdStyle2]][*[&CharId]:arcana_bonus][/td][td [&tdStyle1]]Athletics: [/td][td [&tdStyle2]][*[&CharId]:athletics_bonus][/td][/tr]
        [tr][td [&tdStyle1]]Deciption: [/td][td [&tdStyle2]][*[&CharId]:deception_bonus][/td][td [&tdStyle1]]History: [/td][td [&tdStyle2]][*[&CharId]:history_bonus][/td][/tr]
        [tr][td [&tdStyle1]]Insight: [/td][td [&tdStyle2]][*[&CharId]:insight_bonus][/td][td [&tdStyle1]]Intimidation: [/td][td [&tdStyle2]][*[&CharId]:intimidation_bonus][/td][/tr]
        [tr][td [&tdStyle1]]Investigation: [/td][td [&tdStyle2]][*[&CharId]:investigation_bonus][/td][td [&tdStyle1]]Medicine: [/td][td [&tdStyle2]][*[&CharId]:medicine_bonus][/td][/tr]
        [tr][td [&tdStyle1]]Nature: [/td][td [&tdStyle2]][*[&CharId]:nature_bonus][/td][td [&tdStyle1]]Perception: [/td][td [&tdStyle2]][*[&CharId]:perception_bonus][/td][/tr]
        [tr][td [&tdStyle1]]Performance: [/td][td [&tdStyle2]][*[&CharId]:performance_bonus][/td][td [&tdStyle1]]Persuasion: [/td][td [&tdStyle2]][*[&CharId]:persuasion_bonus][/td][/tr]
        [tr][td [&tdStyle1]]Religion: [/td][td [&tdStyle2]][*[&CharId]:religion_bonus][/td][td [&tdStyle1]]SoH: [/td][td [&tdStyle2]][*[&CharId]:sleight_of_hand_bonus][/td][/tr]
        [tr][td [&tdStyle1]]Stealth: [/td][td [&tdStyle2]][*[&CharId]:stealth_bonus][/td][td [&tdStyle1]]Survival: [/td][td [&tdStyle2]][*[&CharId]:survival_bonus][/td][/tr][/t]

   -->Lib5E_PC_Attack_List|[&CharId]  

  --/| ===== Global Attack Modifiers
  --&tbl|[t style="width:100%;text-align:center"][tr style="background-color:#d8d8e6"][td colspan=2][b][c] Global Modifiers [/c][/b][/td][/tr]
  --Rfirst|[&CharId];repeating_tohitmod
  --:CDR_LOOP1|
    --?"[*R:global_attack_active_flag]" -eq "1"|>CDR_ADDGM;[*R:global_attack_name](Atk);[*R:global_attack_roll];
      --Rnext| --?"[*R:global_attack_name]" -ne "NoRepeatingAttributeLoaded"|CDR_LOOP1

  --/| ===== Global Save Modifiers
  --Rfirst|[&CharId];repeating_savemod
  --:CDR_LOOP2|
    --?"[*R:global_save_active_flag]" -eq "1"|>CDR_ADDGM;[*R:global_save_name](Sav);[*R:global_save_roll]
      --Rnext| --?"[*R:global_save_name]" -ne "NoRepeatingAttributeLoaded"|CDR_LOOP2

  --/| ===== Global Skill Modifiers
  --Rfirst|[&CharId];repeating_skillmod
  --:CDR_LOOP3|
    --?"[*R:global_skill_active_flag]" -eq "1"|>CDR_ADDGM;[*R:global_skill_name](Skl);[*R:global_skill_roll]
      --Rnext| --?"[*R:global_skill_name]" -ne "NoRepeatingAttributeLoaded"|CDR_LOOP3

  --/| ===== Global AC Modifiers
  --Rfirst|[&CharId];repeating_acmod
  --:CDR_LOOP4|
    --?"[*R:global_ac_active_flag]" -eq "1"|>CDR_ADDGM;[*R:global_ac_name](AC);[*R:global_ac_val]
      --Rnext| --?"[*R:global_ac_name]" -ne "NoRepeatingAttributeLoaded"|CDR_LOOP4

  --/| ===== Global Damage Modifiers
  --Rfirst|[&CharId];repeating_damagemod
  --:CDR_LOOP5|
    --?"[*R:global_damage_active_flag]" -eq "1"|>CDR_ADDGM;[*R:global_damage_name](Dmg);[*R:global_damage_damage]
      --Rnext| --?"[*R:global_damage_name]" -ne "NoRepeatingAttributeLoaded"|CDR_LOOP5

  --+|[&tbl] [/t]


  --+|[t style="border:1px solid black;width:100%"]
        [tr style="background-color:#d8d8e6"][td style="width:100%;text-align:center"][b]Quick Note[/b][/td][/tr]
        [tr][td style="width:100%;text-align:left"][*[&CharId]:QuickNote][/td][/tr]
        [tr][td style="width:100%;text-align:left"][rbutton][+]::ADD_QUICK_NOTE;[&CharId][/rbutton][rbutton][Log]::QUICK_NOTE_LOG;[&CharId][/rbutton][/td][/tr]
        [/t]


  -->FOOTER_BUTTONS| Add Footer Buttons

  --X|

--:CDR_ADDGM|
  --&tbl|+ [tr][td style="text-align:left"][%1%][/td] [td style="text-align:right"]([%2%])[/td][/tr]
--<|

--:ADD_QUICK_NOTE|CharId
  --#hidecard|1
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --I;Add a quick note for [*[&CharId]:character_name]|q;QN;Add a QuickNote?

  --~cstDate|system;date;getdate
  --@setattr|_charid [&CharId]  _QuickNoteLog|[&cstDate]: [&QN][hr]%QuickNoteLog% _silent
  --@setattr|_charid [&CharId]  _QuickNote|[&QN] _silent

--X|

--:QUICK_NOTE_LOG|CharId
  --#hidecard|0
  --#oddRowBackground|#D2C09A
  --#evenRowBackground|#A1A9D2

  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --&QNLog|[*[&CharId]:QuickNoteLog]
  --~|array;fromstring;aryLog;[hr];[&QNLog]
  --~LogEntry|array;getfirst;aryLog
  --:QNL_START|
  --?[&LogEntry] -eq ArrayError|QNL_DONE
    --+|[&LogEntry]
    --~LogEntry|array;getnext;aryLog
    --^QNL_START|
  --:QNL_DONE|
  -->FOOTER_BUTTONS| Add Footer Buttons
--X|

--:ATTACKS_REPORT| (ATK_)
  --#hidecard|0
  --~Arg|string;split;\;[&reentryval]

  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]

  --#sourceToken|[&TokenId]
  --#title|Attacks
  --#leftsub|[*[&TokenId]:t-name]
  --#rightsub|
  --#oddRowBackground|#ffffff
  --#evenRowBackground|#ffffff

  --Rfirst|[&CharId];repeating_attack
  --/rDump|

  --&tbl|

  --:ATK_LOOP_TOP|
    --?"[*R:name]" -eq NoRepeatingAttributeLoaded|ATK_LOOP_END

      --&tbl|+ [tr][td style="width:60%;text-align:left"][rbutton][*R:atkname]::ATTACK_DETAILS;[&CharId]\[&TokenId]\[*R:atkname]\1[/rbutton][/td]
                   [td style="width:10%;text-align:center"][*R:atkbonus][/td]
                   [td style="width:30%;text-align:center"][*R:atkdmgtype][/td][/tr]
    --Rnext|
  --^ATK_LOOP_TOP|Back to top of loop
  --:ATK_LOOP_END|
  
  --&tbl|[t border="1" style="width:100%"][tr]
            [td style="width:60%;text-align:center;background-color:#d8d8e6"][b]Attack[/b][/td]
            [td style="width:10%;text-align:center;background-color:#d8d8e6"][b]Bonus[/b][/td]
            [td style="width:30%;text-align:center;background-color:#d8d8e6;"][b]Damage[/b][/td][/tr][&tbl][/t]
  --+|[&tbl]

-->FOOTER_BUTTONS| Add Footer Buttons
  --X|
--<|

--/|==================  Attack Details =======================
--:ATTACK_DETAILS| (ATKD_)
  --#hidecard|0
  --~Arg|string;split;\;[&reentryval]
  --/+Debug Args| [$ArgCount] [&Arg1] [&Arg2] [&Arg3] [&Arg4]

  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]
  --&AtkName|[&Arg3]
  
  --#sourceToken|[&TokenId]
  --#title|Attack Details
  --#leftsub|[*[&TokenId]:t-name]
  --#rightsub|[&AtkName]
  --#oddRowBackground|#ffffff
  --#evenRowBackground|#ffffff

  --Rfind|[&CharId];[&AtkName];repeating_attack;atkname
  --rDump|
  --?"[*R:atkname]" -eq NoRepeatingAttributeLoaded|ATKD_DONE

  --+Bonus:| [*R:atkbonus] to Hit
  --+Damage:| [*R:atkdmgtype]
  --+Critical:| [b]On:[/b][*R:atkcritrange] [b]Dmg:[/b][*R:dmgcustcrit]
  --+Type:| [*R:dmgtype]
  --+Range:| [*R:atkrange]
  --+|[br]
  --/+rollbase:| [*R:rollbase]
  --+Desc:| [*R:atk_desc] 

  --:ATKD_DONE|

-->FOOTER_BUTTONS| Add Footer Buttons
  --X| Exit
--<|

--/|==================  Spellbook Report =======================
--:SPELLBOOK_REPORT| (SB_)
  --#hidecard|0
  --/+Debug|ReentryVal: [&reentryval]
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]

  --#sourceToken|[&TokenId]
  --#title|Spell Book
  --#leftsub|[*[&TokenId]:t-name] [*[&CharId]:caster_level] 
  --#rightsub|

  --&SCA|[*[&CharId]:spellcasting_ability]
  --~SCA|string;substring;3;3;[&SCA]
  --~SCA|string;totitlecase
  -->ADD_POS_SIGN|[*[&CharId]:spell_attack_bonus] --&SAB|[&zRET]
  --+|[l]&nbsp;[b]Spell Attack Bonus: [&SAB][/l][/b] [r][b]Save DC:[*[&CharId]:spell_save_dc]([&SCA])&nbsp;&nbsp;[/b][/r]
  
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

-->FOOTER_BUTTONS| Add Footer Buttons
  --X| DONE

--:REPORTSPELLS|Parameter: Level
  --#hidecard|0
  --&zLvl|[%1%]

  --Rfirst|[*[&CharId]:character_id];repeating_spell-[&zLvl]

  --?"[*R:spellname]" -eq NoRepeatingAttributeLoaded|SB_DONE
  
  --&LvlDesc|Cantrip
  --&zLvlSlots|

  --?[&zLvl] -eq cantrip|SB_SKIPCANTRIP
    --&LvlDesc|Level [&zLvl]
    --=SlotsTotal|[*[&CharId]:lvl[&zLvl]_slots_total]
    --=SlotsExpended|[*[&CharId]:lvl[&zLvl]_slots_expended]
    --&zLvlSlots|Slots [b][$SlotsExpended.Total][/b] of [b][$SlotsTotal.Total][/b] remaining
  --:SB_SKIPCANTRIP|

  --&HdrRow|[&LvlDesc] ([&zLvlSlots])
  -->SECTION_HEADER|[&HdrRow]

  --:SB_DISPLAYLOOP|
    --?"[*R:spellname]" -eq NoRepeatingAttributeLoaded|SB_DONE

      --&zRIT|[r]
      --?"[*R:spellritual]" -inc "Yes"|SB_ENDRIT
        --&zRIT|
      --:SB_ENDRIT|

      --&zCON|[c]
      --?"[*R:spellconcentration]" -inc "concentration=1"|SB_ENDCON
        --&zCON|
      --:SB_ENDCON| 

      --?[&zLvl] -eq cantrip -or [*R:spellprepared] -eq 1|SB_PREPARED
        --+|[rbutton]&#x2B1C;::TOGGLE_PREPARED_SPELL;[&CharId]\[*R>spellprepared]\1[/rbutton] [rbutton][*R:spellname]::SPELL_DETAILS;[*R:spellname]\repeating_spell-[&zLvl]\spellname[/rbutton] [i][*R:innate] [&zCON][&zRIT][/i] 
        --/+SpellName Ref|[*R>spellname]
        --/+Prepared Ref|[*R>spellprepared]        
        --/+Prepared test|[*[&CharId]:[*R>spellprepared]]
      --^SB_ENDIF-1|

      --:SB_PREPARED|
        --+|[rbutton]&#x2705;::TOGGLE_PREPARED_SPELL;[&CharId]\[*R>spellprepared]\0[/rbutton] [rbutton][*R:spellname]::SPELL_DETAILS;[*R:spellname]\repeating_spell-[&zLvl]\spellname[/rbutton] [i][*R:innate] [&zCON][&zRIT][/i]
        --/+SpellName Ref|[*R>spellname]
        --/+Prepared Ref|[*R>spellprepared]
        --/+Prepared test|[*[&CharId]:[*R>spellprepared]]        
      --:SB_ENDIF-1|
    --Rnext|

  --^SB_DISPLAYLOOP|  
  --:SB_DONE|
--<|Return

--/|==================  Toggle Spell Prepared =======================
--:TOGGLE_PREPARED_SPELL|CharId; SpellPrepared_field
  --#hidecard|1
  --+ReEntryVal|[&reentryval]
  --~rArgs|string;split;\;[&reentryval]
  --+Split|[&rArgs]: [&rArgs1], [&rArgs2], [&rArgs3]

  --@setattr|_charid [&rArgs1] _evaluate _silent _[&rArgs2]|[&rArgs3]
  --+Prepared Toggled| @setattr|_charid [&Args1] _evaluate _[&rArgs2]|[&rArgs3]

  --X|
--<|

--/|==================  Spell Details =======================
--:SPELL_DETAILS| (SBD_)

  --#hidecard|0
  --/+ReEntryVal|[&reentryval]
  --~rArgs|string;split;\;[&reentryval]
  --/+Split|[&rArgs]: [&rArgs1], [&rArgs2], [&rArgs3]

  --Rfind|[*[&CharId]:character_id];[&rArgs1];[&rArgs2];[&rArgs3]
  --/Rdump|

  --#title|[*R:spellname]  [*R:innate]
  --#leftsub|[*[&CharId]:character_name] ([*[&CharId]:caster_level])
  --#rightsub|[*R:spellschool] [*R:spelllevel]

  --+Casting Time:|[*R:spellcastingtime]
  --+Range:|[*R:spellrange]
  --+Target:|[*R:spelltarget]

  --&zSCV|V
  --?"[*R:spellcomp_v]" -inc "v=1"|SBD_ENDSCV
    --&zSCV|
  --:SBD_ENDSCV|

  --&zSCS|S
  --?"[*R:spellcomp_s]" -inc "s=1"|SBD_ENDSCS
    --&zSCS|
  --:SBD_ENDSCS|

  --&zSCM|M
  --?"[*R:spellcomp_m]" -inc "m=1"|SBD_ENDSCM
    --&zSCM|
  --:SBD_ENDSCM|

  --+Components:|[&zSCV][&zSCS][&zSCM] [i]([*R:spellcomp_materials])[/i]
  --+Duration:|[*R:spellduration]
  --+|[*R:spelldescription]
  
  --~Len|string;length;[*R:spellathigherlevels]
  --?[$Len] -le 5|SBD_END_HV_CHECK
    --+|[b][i]At Higher Levels.[/i][/b][*R:spellathigherlevels]
  --:SBD_END_HV_CHECK|
  
  --+|[c][sheetbutton]&#x1F4AB; Cast Spell &#x1F9D9;::[*[&CharId]:character_name]::[&rArgs2]_[*R:xxxActionIDxxxx]_spell[/sheetbutton][/c]

  -->DNDBEYOND_SPELL_LINK|[*R:spellname]
  
  --&SCA|[*[&CharId]:spellcasting_ability]
  --~SCA|string;substring;3;3;[&SCA]
  --~SCA|string;totitlecase
  -->ADD_POS_SIGN|[*[&CharId]:spell_attack_bonus] --&SAB|[&zRET]
  --+Spell Atk Bonus: |[b][&SAB] &nbsp;&nbsp; Save DC:[*[&CharId]:spell_save_dc]&nbsp;([&SCA])[/b] [r][button]D&DBeyond::[&zDnDBeyondSpellLink][/button][/r]


-->FOOTER_BUTTONS| Add Footer Buttons
  
  --X|
--<|

--/|==================  Charcter Features Report =======================
--:FEATURES_REPORT| (FTR_)
  --#hidecard|0

  --/+Debug|ReentryVal: [&reentryval]
  --~Arg|string;split;\;[&reentryval]
  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]

  --#sourceToken|[&TokenId]
  --#title|Features and Traits
  --#leftsub|[*[&TokenId]:t-name]
  --#rightsub|
  --#hidecard|0
  --/#oddRowBackground|#d8d8e6
  --/#evenRowBackground|#d8d8e6


  --/|======= Features and Traits =============
  --Rfirst|[&CharId];repeating_traits
  --/rDump|

  --&C| --&R| --&F| --&BG| --&O|
  --:FTR_LOOP_TOP|
    --?"[*R:name]" -eq NoRepeatingAttributeLoaded|FTR_LOOP_END

      --/+OUTPUT|[*R:output]
      --&FName|[*R:name]
      --~FName|string;replace;:;+;[&FName]
      --/+Source|[*R:source] CLASS; RACIAL; FEAT; BACKGROUND
      --/+Source_Type|[*R:source_type]
      --/+Description|[*R:description]

      --C[*R:source]|CLASS:FTR_CLASS|RACIAL:FTR_RACIAL|FEAT:FTR_FEAT|BACKGROUND:FTR_BG|
      --&O|+ [tr][td][rbutton][*R:name]::FEATURE_DETAILS;[&CharId]\[&TokenId]\[*R:name][/rbutton]([*R:source])[/td][td][r][i][*R:source_type][/i][/r][/td][/tr] --^FTR_CONTINUE|
      --:FTR_CLASS|  --&C|+ [tr][td][rbutton][*R:name]::FEATURE_DETAILS;[&CharId]\[&TokenId]\[&FName][/rbutton][/td][td][r][i][*R:source_type][/i][/r][/td][/tr] --^FTR_CONTINUE| 
      --:FTR_RACIAL| --&R|+ [tr][td][rbutton][*R:name]::FEATURE_DETAILS;[&CharId]\[&TokenId]\[*R:name][/rbutton][/td][td][r][i][*R:source_type][/i][/r][/td][/tr] --^FTR_CONTINUE| 
      --:FTR_FEAT|  --&F|+ [tr][td][rbutton][*R:name]::FEATURE_DETAILS;[&CharId]\[&TokenId]\[*R:name][/rbutton][/td][td][r][i][*R:source_type][/i][/r][/td][/tr] --^FTR_CONTINUE| 
      --:FTR_BG|  --&BG|+ [tr][td][rbutton][*R:name]::FEATURE_DETAILS;[&CharId]\[&TokenId]\[*R:name][/rbutton][/td][td][r][i][*R:source_type][/i][/r][/td][/tr] --^FTR_CONTINUE| 

      --:FTR_CONTINUE|
    --Rnext|

  --^FTR_LOOP_TOP|Back to top of loop
  --:FTR_LOOP_END|

  -->SECTION_HEADER|Class Abilities
  --+|[t style="width:100%"][&C][/t]

  -->SECTION_HEADER|Racial Abilities
  --+|[t style="width:100%"][&R][/t]

  -->SECTION_HEADER|Feats
  --+|[t style="width:100%"][&F][/t]

  -->SECTION_HEADER|Background Features
  --+|[t style="width:100%"][&BG][/t]

  -->SECTION_HEADER|Other Features
  --+|[t style="width:100%"][&O][/t]

  --/|======= Proficiencies =============
  -->SECTION_HEADER|Proficiencies

  --Rfirst|[&CharId];repeating_proficiencies
  --/rDump|
  --&L| --&W| --&A| --&O|

  --:FTR_PRF_LOOP_TOP|
    --?"[*R:name]" -eq NoRepeatingAttributeLoaded|FTR_PRF_LOOP_END

      --C[*R:prof_type]|LANGUAGE:PRF_LANG|WEAPON:PRF_WEAPON|ARMOR:PRF_ARMOR|

      --&O|+ [*R:name]([*R:prof_type]), --^FTR_PRF_CONTINUE|
      --:PRF_LANG|  --&L|+ [*R:name], --^FTR_PRF_CONTINUE| 
      --:PRF_WEAPON| --&W|+ [*R:name], --^FTR_PRF_CONTINUE| 
      --:PRF_ARMOR|  --&A|+ [*R:name], --^FTR_PRF_CONTINUE| 
      --:FTR_PRF_CONTINUE|
    --Rnext|
  --^FTR_PRF_LOOP_TOP|Back to top of loop
  --:FTR_PRF_LOOP_END|

  --/| Take out the final comma
  --~Len|string;length;[&A] --=Len|[$Len.Total] - 1 --~Len|math;max;0,[$Len.Total] --~A|string;Left;[$Len.Total];[&A]
  --~Len|string;length;[&W] --=Len|[$Len.Total] - 1 --~Len|math;max;0,[$Len.Total] --~W|string;Left;[$Len.Total];[&W]
  --~Len|string;length;[&L] --=Len|[$Len.Total] - 1 --~Len|math;max;0,[$Len.Total] --~L|string;Left;[$Len.Total];[&L]
  --~Len|string;length;[&O] --=Len|[$Len.Total] - 1 --~Len|math;max;0,[$Len.Total] --~O|string;Left;[$Len.Total];[&O]    

  --&tStyle|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;"
  --&trStyle1|style="border:0px dashed black;"
  --&tdStyle1|style="width:30%;text-align:left;background-color:#FFFFFF;font-size:80%"
  --&tdStyle2|style="width:70%;text-align:left;background-color:#FFFFFF;font-size:80%"

  --+|[t [&tStyle]]
          [tr [&trStyle1]][td [&tdStyle1]]&nbsp;&nbsp;Armor[/td][td [&tdStyle2]][&A][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]]&nbsp;&nbsp;Weapon[/td][td [&tdStyle2]][&W][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]]&nbsp;&nbsp;Language[/td][td [&tdStyle2]][&L][/td][/tr]
          [tr [&trStyle1]][td [&tdStyle1]]&nbsp;&nbsp;Other[/td][td [&tdStyle2]][&O][/td][/tr]
      [/t]

  --/|======= Tools / Custom =============
  -->SECTION_HEADER|Tools & Custom Skills
  --Rfirst|[&CharId];repeating_tool
  --/rDump|

  --&tStyle|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;"
  --&trStyle1|style="border:0px dashed black;"
  --&tdStyle1|style="width:40%;text-align:left;background-color:#FFFFFF;font-size:80%"
  --&tdStyle2|style="width:10%;text-align:left;background-color:#FFFFFF;font-size:80%"

  --&Tbl|[t [&tStyle]]
  --:FTR_TOOL_LOOP_TOP|
    --?"[*R:name]" -eq NoRepeatingAttributeLoaded|FTR_TOOL_LOOP_END
      --&Tbl|+ [tr [&trStyle1]][td [&tdStyle1]]&nbsp;&nbsp;[*R:toolname][/td][td [&tdStyle2]] [*R:toolbonus_base] [/td]
      
      --Rnext|

      --&ToolName| --&ToolBonus|
      --?"[*R:name]" -eq NoRepeatingAttributeLoaded|FTR_TOOL_LOOP_CONTINUE
      --&ToolName|[*R:toolname] --&ToolBonus|[*R:toolbonus_base]
      --RNext

  --:FTR_TOOL_LOOP_CONTINUE|
  --&Tbl|+ [td [&tdStyle1]]&nbsp;&nbsp;[&ToolName][/td][td [&tdStyle2]] [&ToolBonus] [/td][/tr]
     
  --^FTR_TOOL_LOOP_TOP|Back to top of loop
  --:FTR_TOOL_LOOP_END|
  --&Tbl|+ [/t]
  --+|[&Tbl]
  
-->FOOTER_BUTTONS| Add Footer Buttons
 --X|
--<|

--/|==================  Charcter Feature Details =======================
--:FEATURE_DETAILS| (FTRD_)
  
  --#hidecard|0
  --~Arg|string;split;\;[&reentryval]
  --/+Debug Args| [$ArgCount] [&Arg1] [&Arg2] [&Arg3]

  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]
  --&FtrName|[&Arg3]
  --#sourceToken|[&TokenId]

  --~FtrName|string;replace;+;:;[&FtrName]
  
  --#title|Feature Details
  --#leftsub|[*[&TokenId]:t-name]
  --#rightsub|[&FtrName]
  --#oddRowBackground|#ffffff
  --#evenRowBackground|#ffffff

  --Rfind|[&CharId];[&FtrName];repeating_traits;name
  --rDump|
  --?"[*R:name]" -eq NoRepeatingAttributeLoaded|FTRD_DONE

  --+Source:| [*R:source]
  --+Source type:| [*R:source_type]
  --+Desc:| [*R:description]
  --+Options:| [*R:options_flag] 
  --+Display:| [*R:display_flag]
  
  --:FTRD_DONE|

-->FOOTER_BUTTONS| Add Footer Buttons  
--X| Exit
--<|

--/|==================  Charcter Inventory Report =======================
--:INV_REPORT| (INV_)
  --#hidecard|0
  --~Arg|string;split;\;[&reentryval]

  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]
  --#sourceToken|[&TokenId]

  --#title|Inventory
  --#leftsub|[*[&TokenId]:t-name]
  --#rightsub|
  --#oddRowBackground|#d8d8e6
  --#evenRowBackground|#ffffff

  --Rfirst|[&CharId];repeating_inventory
  --/rDump|


  -->SECTION_HEADER|Money
  --=TtlGP|[*[&CharId]:cp] / 100 
  --=TtlGP|[*[&CharId]:sp] / 10 + [$TtlGP] 
  --=TtlGP|[*[&CharId]:ep] / 2 + [$TtlGP]
  --=TtlGP|[*[&CharId]:gp] + [$TtlGP]
  --=TtlGP|[*[&CharId]:pp]*10 + [$TtlGP]
  --=TtlGP|[$TtlGP]*100
  --~TtlGP|math;round;[$TtlGP]
  --=TtlGP|[$TtlGP]/100

  --&tbl|[t style="width:90%;text-align:right"][tr][td style="width:15%;text-align:right"]cp[/td][td style="width:15%";text-align:right]sp[/td][td style="width:15%;text-align:right"]ep[/td][td style="width:15%;text-align:right"]gp[/td][td style="width:15%;text-align:right"]pp[/td][td style="width:25%;text-align:right"]Ttl(gp)[/td][/tr]

  --&tbl|+  [tr][td style="text-align:right"][*[&CharId]:cp][/td][td style="text-align:right"][*[&CharId]:sp][/td][td style="text-align:right"][*[&CharId]:ep][/td][td style="text-align:right"][*[&CharId]:gp][/td][td style="text-align:right"][*[&CharId]:pp][/td][td style="text-align:right"][$TtlGP.Total][/td][/tr][/t]
  --+|[&tbl]

  --&tbl|
  -->SECTION_HEADER|Items
  --=ItemWt|0

  --:INV_LOOP_TOP|
    --?"[*R:name]" -eq NoRepeatingAttributeLoaded|INV_LOOP_END

      --&E|&#x1F392; --?[*R:equipped] -ne 1|INV_NOTEQUIPPED --&E|&#x270B; --:INV_NOTEQUIPPED|
      --&tbl|+ [tr][td style="width:60%"][rbutton][*R:itemname]::INV_DETAILS;[&CharId]\[&TokenId]\[*R:itemname][/rbutton][/td][td style="width:13%;text-align:center"][*R:itemcount][/td][td style="width:13%;text-align:center"][&E][/td][td style="text-align:right;width:14%"][*R:itemweight][/td][/tr]

    --Rnext|
  --^INV_LOOP_TOP|Back to top of loop
  --:INV_LOOP_END|

  --=CC|15 * [*[&TokenId]:strength]
  --&CC|[$CC.Total]
  --&tbl|+ [tr][td style="text-align:left;width:60%"][b]Carrying: [$CC.Total]lbs[/b][/td][td][/td][td style="text-align:right;width:13%"][b]Total:[/b][/td][td style="text-align:right;width:14%"][r][b][*[&CharId]:weighttotal] lbs[/b][/r][/td][/tr]  

  --&tbl|[t style="width:90%"][tr][td style="width:60%"][b]Item Name[/b][/td][td style="width:13%;text-align:center"][b]#[/b][/td][td style="width:13%;text-align:center"][b]Equip[/b][/td][td style="text-align:center;width:14%"][r][b]Lbs[/b][/r][/td][/tr][&tbl][/t]

  --+|[&tbl]

  -->FOOTER_BUTTONS| Add Footer Buttons
  --X|
--<|

--/|==================  Inv Details Report =======================
--:INV_DETAILS| (INVD_)
  --#hidecard|0
  --~Arg|string;split;\;[&reentryval]
  --/+Debug Args| [$ArgCount] [&Arg1] [&Arg2] [&Arg3]

  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]
  --&ItemName|[&Arg3]
  
  --#sourceToken|[&TokenId]
  --#title|Inventory Item Info
  --#leftsub|[*[&TokenId]:t-name]
  --#rightsub|[&ItemName]
  --#oddRowBackground|#ffffff
  --#evenRowBackground|#ffffff

  --Rfind|[&CharId];[&ItemName];repeating_inventory;name
  --/rDump|
  --?"[*R:name]" -eq NoRepeatingAttributeLoaded|INVD_DONE

  --+Count:| [*R:count]
  --+Weight:| [*R:weight]
  --+Properties:| [*R:properties]
  --+Modifiers:| [*R:modifiers]
  --+Content:| [*R:content]
  --:INVD_DONE|

-->FOOTER_BUTTONS| Add Footer Buttons
  --X| Exit

--<|

--/|==================  Resource Summary Report =======================
--:RESOURCES_REPORT| RES_
  --#hidecard|0
  --~Arg|string;split;\;[&reentryval]

  --&CharId|[&Arg1]
  --&TokenId|[&Arg2]

  --#sourceToken|[&TokenId]
  --#title|Resources
  --#leftsub|[*[&TokenId]:t-name]
  --#rightsub|
  --#oddRowBackground|#ffffff
  --#evenRowBackground|#ffffff

  --/| First get the Class and Other Resources
  --&tbl|[t style="width:100%"][tr style="background-color:#d8d8e6"][td style="width:55%;text-align:left"][b]Resource[/b][/td][td style="width:15%";text-align:center][b]Total[/b][/td][td style="width:15%;text-align:center"][b]Exp[/b][/td][td style="width:15%;text-align:center"][b]Rem[/b][/td][/tr]

  --=ResExp|[*[&CharId]:class_resource]
  --=ResTotal|[*[&CharId]:class_resource^]
  --=ResRem|[$ResTotal]-[$ResExp]
  --&tbl|+ [tr style="background-color:#FFFFFF"][td style="width:55%;text-align:left"]&nbsp;&nbsp;[*[&CharId]:class_resource_name][/td][td style="width:15%;text-align:center"][$ResTotal.Total][/td][td style="width:15%;text-align:center"][$ResExp.Total][/td][td style="width:15%;text-align:center"][$ResRem.Total][/td][/tr]

  --=ResExp|[*[&CharId]:other_resource]
  --=ResTotal|[*[&CharId]:other_resource^]
  --=ResRem|[$ResTotal]-[$ResExp]
  --&tbl|+ [tr style="background-color:#FFFFFF"][td style="width:55%;text-align:left"]&nbsp;&nbsp;[*[&CharId]:other_resource_name][/td][td style="width:15%;text-align:center"][$ResTotal.Total][/td][td style="width:15%;text-align:center"][$ResExp.Total][/td][td style="width:15%;text-align:center"][$ResRem.Total][/td][/tr]

  --/| Then get the repeating resources

  --Rfirst|[&CharId];repeating_resource
  --/rDump|

  --:RES_LOOP_TOP|
    --?"[*R:resource_left_name]" -eq NoRepeatingAttributeLoaded|RES_LOOP_END

      --=ResExp|[*R:resource_left]
      --=ResTotal|[*[&CharId]:resource_left^]
      --=ResRem|[$ResTotal]-[$ResExp]
      --&tbl|+ [tr style="background-color:#FFFFFF"][td style="width:55%;text-align:left"]&nbsp;&nbsp;[*R:resource_left_name][/td][td style="width:15%;text-align:center"][$ResTotal.Total][/td][td style="width:15%;text-align:center"][$ResExp.Total][/td][td style="width:15%;text-align:center"][$ResRem.Total][/td][/tr]

      --=ResExp|[*R:resource_right]
      --=ResTotal|[*[&CharId]:resource_right^]
      --=ResRem|[$ResTotal]-[$ResExp]
      --&tbl|+ [tr style="background-color:#FFFFFF"][td style="width:55%;text-align:left"]&nbsp;&nbsp;[*R:resource_right_name][/td][td style="width:15%;text-align:center"][$ResTotal.Total][/td][td style="width:15%;text-align:center"][$ResExp.Total][/td][td style="width:15%;text-align:center"][$ResRem.Total][/td][/tr]

    --Rnext|
  --^RES_LOOP_TOP|Back to top of loop
  --:RES_LOOP_END|

  --+|[C][&tbl][/C]

-->FOOTER_BUTTONS| Add Footer Buttons
  --X|
--<|



--/|==================  Utility Functions =======================
--:SHORT_NAME|Shortens player name for reporting purposes (6 characters); Parameter:Character_Name
  --~gSN|string;left;8;[%1%]
  --~gSN|string;before; ;[&gSN]
--<| Return

--:FIND_TOKEN|TokenId
  --#hidecard|1  
  --@ping-token|_[&reentryval]
  --X|
--<|

--:SECTION_HEADER|Title
  --&hdrstyle_T|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 1px black;border: 0px solid black;"
  --&hdrstyle_TR|style="border:0px solid black;"
  --&hdrstyle_TD|style="width:100%;background-color:#d8d8e6;font-size:110%;font-weight:bold;text-align:center"
  --+|[t [&hdrstyle_T]][tr [&hdrstyle_TR]][td [&hdrstyle_TD]][c][%1%][/c][/td][/tr][/t]
--<|

--:ADD_POS_SIGN|Parameter1: Number / Can be a roll or string variable, but should be a number. 
 --/| Populates zRET(String) and rRET(Roll Variable) (Global vars effectively)
  --&zRET|[%1%]
  --=rRET|[%1%]
  --?[%1%] -le 0|_APS_SKIP  --=rRET|&#43;[%1%] --&zRET|[$rRET.Text] --:_APS_SKIP|
--<|

--:FOOTER_BUTTONS|

  --&tStyle|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border: 0px solid black;"
  --&trStyle1|style="border:1px solid black;"
  --&tdStyle1|style="width:100%;background-color:#FFFFFF;font-size:70%"
  --+|[t [&tStyle]][tr [&trStyle1]][td [&tdStyle1]]
      [l][rbutton]&#x1F4C3;::CHAR_DETAILS_REPORT;[&CharId]\[&TokenId]\[/rbutton]
          [rbutton]&#x1F3F9;::ATTACKS_REPORT;[&CharId]\[&TokenId]\[/rbutton]
          [rbutton]&#x1F393;::FEATURES_REPORT;[&CharId]\[&TokenId]\[/rbutton]
          [rbutton]&#x1F4AB;::SPELLBOOK_REPORT;[&CharId]\[&TokenId]\[/rbutton]
          [rbutton]&#x1F392;::INV_REPORT;[&CharId]\[&TokenId]\[/rbutton]
          [rbutton]&#x1F520;::RESOURCES_REPORT;[&CharId]\[&TokenId]\[/rbutton]
          [rbutton]&#x1F3AF;::FIND_TOKEN;[&TokenId][/rbutton]
      [/l]
        
          [r][button]MapNotes::~Mule|MapNote-Tools[/button][button]NPC::~Mule|NPC-Tools[/button][button]DM::~Mule|DM-Tools[/button][/r]
      [/td][/tr][/t]
--<|

--:FOOTER_BUTTONS_PARTY|

  --&tStyle|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border: 0px solid black;"
  --&trStyle1|style="border:1px solid black;"
  --&tdStyle1|style="width:100%;background-color:#FFFFFF;font-size:70%"

    --+|[t [&tStyle]][tr [&trStyle1]][td [&tdStyle1]]
        [l][rbutton]&#x1F497;::PARTY_HEALTH_REPORT[/rbutton]
          [rbutton]&#x1F520;::PARTY_RESOURCES_REPORT[/rbutton]
          [rbutton]&#x1F4AC;::PARTY_LANG_REPORT[/rbutton]   
          [rbutton]&#x1F4B0;::PARTY_FUNDS_REPORT[/rbutton]
          [rbutton]&#x1F453;::PARTY_UDL_REPORT[/rbutton]
        [/l]
          [r][button]MapNotes::~Mule|MapNote-Tools[/button][button]NPC::~Mule|NPC-Tools[/button][button]DM::~Mule|DM-Tools[/button][/r]
        [/td][/tr][/t]
--<|

--:ROLL20_CHARACTERSHEET_LINK|CharId, button caption
  --&zROLL20_CS|https://journal.roll20.net/character/[%1%]
  --&ROLL20_CHARACTERSHEET_BTN|[button][%2%]::[&zROLL20_CS][/button]
--<|

--/|I use D&D Beyond for my rules refrences.  These functions create hyperlinks based on context of card
--:DNDBEYOND_MONSTER_LINK|MonsterName 
  --/|Format is https://www.dndbeyond.com/monsters/vampire-spellcaster
  --~MN|string;replace; ;-;[%1%]
  --&zDnDBeyondMonsterLink|https://www.dndbeyond.com/monsters/[&MN]
--<|

--:DNDBEYOND_SPELL_LINK|SpellName 
  --/|Format is https://www.dndbeyond.com/spells/dominate-person
  --~SN|string;replace; ;-;[%1%]
  --&zDnDBeyondSpellLink|https://www.dndbeyond.com/spells/[&SN]
--<|

--:Lib5E_PC_Class_Summary|character_id
--&_Lib5E_Temp|[t][tr][td][*[%1%]:class] - [*[%1%]:base_level][/td][/tr] --?"[*[%1%]:multiclass1_flag]" -eq "0"|_Lib5E_PC_Class_Summary_Done --&_Lib5E_Temp|+[tr][td][*[%1%]:multiclass1] - [*[%1%]:multiclass1_lvl]
--?"[*[%1%]:multiclass2_flag]" -eq "0"|_Lib5E_PC_Class_Summary_Done --&_Lib5E_Temp|+[tr][td][*[%1%]:multiclass2] - [*[%1%]:multiclass2_lvl] --?"[*[%1%]:multiclass3_flag]" -eq "0"|_Lib5E_PC_Class_Summary_Done --&_Lib5E_Temp|+[tr][td][*[%1%]:multiclass3] - [*[%1%]:multiclass3_lvl]
--:_Lib5E_PC_Class_Summary_Done| --&_Lib5E_Temp|+[/t] --+|[&_Lib5E_Temp] --<| 

--:Lib5E_PC_Attack_List|character_id 
  --&_Lib5E_Temp| --Rfirst|[%1%];repeating_attack --:_Lib5E_PCAL_Loop| --?"[*R:atkname]" -eq "NoRepeatingAttributeLoaded"|_Lib5E_PCAL_Done 
  --&_Lib5E_NameTemp|[*R:atkname] --~_Lib5E_NameTemp|string;replace;(; ;[&_Lib5E_NameTemp] --~_Lib5E_NameTemp|string;replace;); ;[&_Lib5E_NameTemp] 
  --&_Lib5E_Temp|+[button][&_Lib5E_NameTemp]::~[%1%]|[*R>attack][/button]   --Rnext| --^_Lib5E_PCAL_Loop| --:_Lib5E_PCAL_Done| --+Available Attacks|[br][&_Lib5E_Temp] 
--<|

--:SHOW_SAVED_SETTINGS|
  --@sc-liststoredsettings|
  --X|

--:LOAD_SAVED_SETTINGS|
  --I;Load Saved Settings|q;SName;Enter saved settings name:
  --?X[&SName] -eq X|LSS_INVALID_NAME
    --Lsettings|[&SName]
    --^SETTINGS_BOTTOM|
  --:LSS_INVALID_NAME|
    --+ERROR|Invalid Setting Name
    --+|[HR]
    --^SETTINGS_BOTTOM|
  --x|

--/|==================  Global NPC Settings =======================
--:GLOBAL_NPC_SETTINGS|
  --#hidecard|0

  -->SECTION_HEADER|Whisper Rolls
  --+|[c][rbutton]Never::APPLY_NPC_GLOBAL_SETTING;WR_Never[/rbutton]
         [rbutton]Toggle::APPLY_NPC_GLOBAL_SETTING;WR_Toggle[/rbutton]
         [rbutton]Query::APPLY_NPC_GLOBAL_SETTING;WR_Query[/rbutton]
         [rbutton]Always::APPLY_NPC_GLOBAL_SETTING;WR_Always[/rbutton]
     [/c]

  -->SECTION_HEADER|Roll Queries (Advantage)
  --+|[c][rbutton]Never::APPLY_NPC_GLOBAL_SETTING;RA_Never[/rbutton]
         [rbutton]Toggle::APPLY_NPC_GLOBAL_SETTING;RA_Toggle[/rbutton]
         [rbutton]Query::APPLY_NPC_GLOBAL_SETTING;RA_Query[/rbutton]
         [rbutton]Always::APPLY_NPC_GLOBAL_SETTING;RA_Always[/rbutton]
     [/c]

  -->SECTION_HEADER|Automatic Damage Rolls
  --+|[c][rbutton]On::APPLY_NPC_GLOBAL_SETTING;DT_On[/rbutton]
         [rbutton]Off::APPLY_NPC_GLOBAL_SETTING;DT_Off[/rbutton]
     [/c]

  -->SECTION_HEADER|NPC Name in Rolls
  --+|[c][rbutton]Show::APPLY_NPC_GLOBAL_SETTING;NPCName_Show[/rbutton]
         [rbutton]Hide::APPLY_NPC_GLOBAL_SETTING;NPCName_Hide[/rbutton]
         
     [/c]

  -->SECTION_HEADER|Dex Tiebreaker
  --+|[c][rbutton]On::APPLY_NPC_GLOBAL_SETTING;DTB_On[/rbutton]
         [rbutton]Off::APPLY_NPC_GLOBAL_SETTING;DTB_Off[/rbutton]
     [/c]

  --/>SECTION_HEADER|Configure ChatSetAttr API Script
  --/+|[c][rbutton]On::APPLY_NPC_GLOBAL_SETTING;Config[/rbutton]

  -->FOOTER_BUTTONS_MAIN|
  --X|

--<|

--:APPLY_NPC_GLOBAL_SETTING|MODE
  --&Mode|[&reentryval]
  --#Title|Apply Global Setting
  --+|[c][b]Hang on, this may take a minute...[/b][/c]

  --C[&Mode]|WR_Never:WR_NEVER|WR_Always:WR_ALWAYS|WR_Toggle:WR_TOGGLE|WR_Query:WR_QUERY|RA_Never:RA_NEVER|RA_Toggle:RA_TOGGLE|RA_Query:RA_QUERY|RA_Always:RA_ALWAYS|NPCName_Show:NPCNAME_SHOW|NPCName_Hide:NPCNAME_HIDE|DTB_On:DTB_ON|DTB_Off:DTB_OFF|DT_On:DT_ON|DT_Off:DT_OFF|Config:SETATTR_CONFIG

    --/| *|Invalid Option ([&Mode]) in APPLY_NPC_GLOBAL_SETTING
    --X|

  --:WR_NEVER|
    --#leftsub|Never Whisper Rolls
    --@setattr|_allgm _replace _wtype|
    --X|
  --:WR_ALWAYS|
    --#leftsub|Always Whisper Rolls
    --@setattr|_allgm _replace _wtype|'/w gm '
    --X|
  --:WR_TOGGLE|
    --#leftsub|Toggle Whisper Rolls  
    --@setattr|_allgm _replace _wtype|\at{whispertoggle}
    --X|
  --:WR_QUERY|
    --#leftsub|Query Whisper Rolls  
    --@setattr|_allgm _replace _wtype|\ques{Whisper\ques\|Public Roll,\|Whisper Roll,/w gm }
    --X|

  --:RA_NEVER|
    --#leftsub|Never Roll Advantage
    --&P1|{{normal=1}
    --&P1|+} {{r2=[[0d20
    --@setattr|_allgm _replace _rtype|[&P1]
    --X|

  --:RA_TOGGLE|
    --#leftsub|Toggle Roll Advantage  
    --@setattr|_allgm _replace _rtype|\at{advantagetoggle}
    --X|

  --:RA_QUERY|
    --#leftsub|Query Roll Advantage  
    --@setattr|_allgm _replace _rtype|\at{queryadvantage}
    --X|

  --:RA_ALWAYS|
    --#leftsub|Always Roll Advantage
    --&P1|{{always=1}
    --&P1|+} {{r2=[[\at{d20}
    --@setattr|_allgm _replace _rtype|[&P1]

    --X|
  --:NPCNAME_SHOW|
    --#leftsub|Show NPC Name on Roll
    --&P1|{{charname=\at{npc_name}
    --&P1|+} --&P1|+}

    --&P2|{{name=\at{npc_name}
    --&P2|+} --&P2|+}

    --@setattr|_allgm _replace _charname_output|[&P1] _npc_name_flag|[&P2]

    --X|
  --:NPCNAME_HIDE|
    --#leftsub|Hide NPC Name on Roll
    --@setattr|_allgm _replace _npc_name_flag|0 _charname_output|0
    --X|
  --:DTB_ON|
    --#leftsub|Dex Tiebreaker On
    --@setattr|_allgm _replace _init_tiebreaker|\at{dexterity}/100
    --X|
  --:DTB_OFF|
    --#leftsub|Dex Tiebreaker Off
    --@setattr|_allgm _replace _init_tiebreaker|0
    --X|

  --:DT_ON|
    --#leftsub|Roll Damage Automaticaly
    --@setattr|_allgm _replace _dtype|full
    --X|  

  --:DT_OFF|
    --#leftsub|Do Not Roll Damage Automaticaly
    --@setattr|_allgm _replace _dtype|pick
    --X|  

  --:SETATTR_CONFIG|
    --#leftsub|Configure ChatSetAttr
    --@setattr-config|
  --X|
--<|  
}}